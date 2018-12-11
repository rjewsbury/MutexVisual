package mutex.editor.model;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;

import javax.tools.*;

import mutex.io.PopupManager;

/**
 * Based on code written by SoulMachine
 * http://www.soulmachine.me/blog/2015/07/22/compile-and-run-java-source-code-in-memory/
 */
public class AlgorithmCompiler implements Closeable
{
	private JavaCompiler myCompiler;
	private MemoryFileManager myFileManager;
	
	public AlgorithmCompiler() throws Exception
	{
		myCompiler = ToolProvider.getSystemJavaCompiler();
		if(myCompiler == null) {
			System.out.println("System Compiler not found.");
			String version = System.getProperty("java.version");
			//in an attempt to allow the editor to be run by JREs as well as JDKs,
			//I tried including tools.jar from a JDK as a direct dependency
			//however, in Java 1.9 they switched to using implementation-specific libs
			//I'm leaving this here for now, in case a better solution is found
			if(version.startsWith("1.8")) {
				System.out.println("Using 1.8 tools.");
				// this line gets flagged by my IDE, but it compiles fine,
				// because tools.jar is included directly as a dependency
				myCompiler = com.sun.tools.javac.api.JavacTool.create();
			}
			else {
				throw new Exception("Could not get the Java Compiler. Must be running JDK.");
			}
        }
		else {
            System.out.println("Got System Compiler");
        }
		StandardJavaFileManager manager = myCompiler.getStandardFileManager(null, null, null);
		myFileManager = new MemoryFileManager(manager);
	}
	
	public boolean compile(String fileName, String source){
		HashMap<String, String> sources = new HashMap<>();
		sources.put(fileName, source);
		return compile(sources);
	}
	
	public boolean compile(Map<String, String> sources)
	{
		OutputStream stream = new ByteArrayOutputStream();
		Writer err = new PrintWriter(stream);
		
		String classPath = getClassPath();

		String OpSystem = System.getProperty("os.name");
		//Java 10 on windows cannot look inside the jar for the classpath,
		//So I need to include a bin with the necessary class files
		if(OpSystem.startsWith("Windows")) {
			classPath += ";./bin/";
		}

		//System.out.println(classPath);
		
		List<JavaFileObject> units = new ArrayList<>(sources.size());
		for(String fileName : sources.keySet()) {
			JavaFileObject source = MemoryFileManager.makeStringSource(fileName, sources.get(fileName));
			units.add(source);
			//myFileManager.putFileForInput(fileName, source);
		}
		
		boolean success = compile(units, err, classPath);
		
		if(!stream.toString().equals("")) {
			PopupManager.errorMessage(stream.toString());
			System.out.println(stream.toString());
		}
		return success;
	}
	
	public String getClassPath()
	{
		try{
			String path = AlgorithmCompiler.class.getProtectionDomain()
							.getCodeSource().getLocation().toURI().getPath();
			//System.out.println(path);
			return path;
		}catch (URISyntaxException e){
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean compile(List<JavaFileObject> units, Writer err, String classPath)
	{
		//collects errors
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
		//javac options
		List<String> options = new ArrayList<>();
		//shows a description of every use of deprecated stuff
		//options.add("-deprecation");
		if(classPath != null) {
			options.add("-classpath");
			options.add(classPath);
		}
		
		JavaCompiler.CompilationTask task =
				myCompiler.getTask(err, myFileManager, diagnostics, options, null, units);

		if (!task.call()) {
			PrintWriter writer = new PrintWriter(err);
			for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics())
				writer.println(diagnostic);
			writer.flush();
			return false;
		}
		return true;
	}
	
	public Map<String, byte[]> getCompiledBytes()
	{
		return myFileManager.getClassBytes();
	}
	
	public void close()
	{
		if(myFileManager != null) {
			try {
				myFileManager.close();
			} catch (IOException e) {
				e.printStackTrace();
			}    //if we can't close it, give up?
		}
	}
}