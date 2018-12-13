package mutex.io;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import mutex.display.ElementController;
import mutex.display.controllers.DefaultDisplay;
import mutex.editor.control.ControlParameters;

public class SourceFile implements Serializable
{
	// to avoid InvalidClassExceptions, while allowing me to add new parameters later,
	// the version ID stays constant
	//(note to self: fields can be added/removed, but types can't be changed)
	public static final long serialVersionUID = 1L;

	private String myCode;
	private String myDisplayCode;
	private ControlParameters myControls;
	
	public SourceFile(File file) throws IOException
	{
		setSafeDefaults();
		readFile(file);
	}
	
	public SourceFile()
	{
		setSafeDefaults();
	}
	
	public void setSafeDefaults()
	{
		myCode = "";
		myDisplayCode = "";
		myControls = new ControlParameters();
	}
	
	public void setCode(String code)
	{
		if(code != null) myCode = code;
	}
	public String getCode()
	{
		return myCode;
	}
	
	public void setControls(ControlParameters controls)
	{
		if(controls != null) myControls = controls;
	}
	public ControlParameters getControls()
	{
		return myControls;
	}

	public void setDisplayCode(String code)
	{
		if(code != null) myDisplayCode = code;
	}
	public String getDisplayCode()
	{
		return myDisplayCode;
	}
	
	private void copySource(SourceFile file)
	{
		myCode = file.getCode();
		myDisplayCode = file.getDisplayCode();
		myControls = file.getControls();
	}
	
	/**
	 * Code for reading property files borrowed from
	 * http://www.mkyong.com/java/java-properties-file-examples/
	 */
	private void readFile(File source) throws IOException
	{
		try(FileInputStream fileIn = new FileInputStream(source);
				GZIPInputStream zipIn = new GZIPInputStream(fileIn);
				ObjectInputStream in = new ObjectInputStream(zipIn))
		{
			SourceFile file = (SourceFile)in.readObject();
			copySource(file);
		}
		catch (ClassNotFoundException e){
			e.printStackTrace();
		}
		catch (IOException e) {
			PopupManager.errorMessage("Error reading file:\n"
					+ e.getClass().getName()+"\n"
					+ e.getMessage());
		}
	}
	
	/**
	 * code for writing property files borrowed from
	 * http://www.mkyong.com/java/java-properties-file-examples/
	 */
	public void writeFile(File dest) throws IOException
	{
		try(FileOutputStream fileOut = new FileOutputStream(dest);
				GZIPOutputStream zipOut = new GZIPOutputStream(fileOut);
				ObjectOutputStream objOut = new ObjectOutputStream(zipOut))
		{
			objOut.writeObject(this);
		}
	}
}
