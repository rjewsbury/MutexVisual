package mutex.editor.model;

import java.io.*;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.tools.*;
import javax.tools.JavaFileObject.Kind;

/**
 * Based on code written by SoulMachine
 * http://www.soulmachine.me/blog/2015/07/22/compile-and-run-java-source-code-in-memory/
 */
public class MemoryFileManager extends ForwardingJavaFileManager<JavaFileManager>
{
	private static final String SOURCE_EXT = ".java";

	private Map<String, JavaFileObject> fileObjects;
	private Map<String, byte[]> myClassBytes;
	
	protected MemoryFileManager(JavaFileManager fileManager)
	{
		super(fileManager);
		myClassBytes = new HashMap<>();
		fileObjects = new HashMap<>();
	}
	
	public Map<String, byte[]> getClassBytes()
	{
		return myClassBytes;
	}
	
	@Override
	public void close() throws IOException
	{
		super.close();
		//the only resource opened by the classloader is the byte[] map
		myClassBytes = null;
	}
	
	//dummy overriding method to prevent resources from being flushed???
	//@Override
	//public void flush(){}
	
	@Override
	public JavaFileObject getJavaFileForOutput(
			JavaFileManager.Location location,
			String className,
			Kind kind,
			FileObject sibling) throws IOException
	{
		//redirects all class output files to the byte[] map
		if(kind == Kind.CLASS)
			return new JavaClassString(className);
		else
			return super.getJavaFileForOutput(location, className, kind, sibling);
	}

//	//based on https://www.ibm.com/developerworks/java/library/j-jcomp/index.html
//	@Override
//	public FileObject getFileForInput(Location location, String packageName, String relativeName) throws IOException {
//		FileObject obj = fileObjects.get(relativeName);
//		System.out.println("----------------"+relativeName);
//		if (obj != null)
//			return obj;
//
//		return super.getFileForInput(location, packageName, relativeName);
//	}
//
//	public void putFileForInput(String relativeName, JavaFileObject file) {
//		fileObjects.put(relativeName, file);
//	}

//	@Override
//	public String inferBinaryName(Location location, JavaFileObject javaFileObject)
//	{
//		if(javaFileObject instanceof JavaClassString || javaFileObject instanceof JavaSourceString) {
//			//strip the .java
//			String name = javaFileObject.getName();
//			if(name.endsWith(".java"))
//				name.substring(0, name.length()-5);
//			return name;
//		}
//
//		return super.inferBinaryName(location, javaFileObject);
//	}
	
	private static URI toURI(String name)
	{
		//if a file with that name already exists (somewhere????), use the existing path
		File file = new File(name);
		if(file.exists())
			return file.toURI();
		else
		{
			//changes a package to a path
			String newUri = "string:///"+name.replace('.', '/');
			//if the name was a source file, we accidentally replaced the last . with a /
			//we only need to care about source extensions, because other types are not given extensions
			if(name.endsWith(SOURCE_EXT))
				newUri = newUri.substring(0,newUri.length()-SOURCE_EXT.length())+SOURCE_EXT;
			try{
				return URI.create(newUri);
			}catch(IllegalArgumentException e){
				//create a default URI
				System.err.println("Illegal URI: "+newUri);
				return URI.create("string:///com/sun/script/java/java_source");
			}
		}
	}
	
	public static JavaFileObject makeStringSource(String fileName, String code)
	{
		return new JavaSourceString(fileName, code);
	}

	//represents a source code file in memory
	private static class JavaSourceString extends SimpleJavaFileObject
	{
		private final String myCode;
		public JavaSourceString(String fileName, String code)
		{
			super(toURI(fileName), Kind.SOURCE);
			myCode = code;
		}
		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors)
		{
			return myCode;
		}
	}
	
	private class JavaClassString extends SimpleJavaFileObject
	{
		private String myName;
		public JavaClassString(String name)
		{
			super(toURI(name), Kind.CLASS);
			myName = name;
		}
		//redirects the output to the byte[] map on close
		@Override
		public OutputStream openOutputStream()
		{
			return new FilterOutputStream(new ByteArrayOutputStream()){
				public void close() throws IOException
				{
					out.close();
					ByteArrayOutputStream bytes = (ByteArrayOutputStream) out;
					myClassBytes.put(myName, bytes.toByteArray());
				}
			};
		}
	}
}
