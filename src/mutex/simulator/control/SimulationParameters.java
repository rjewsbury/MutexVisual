package mutex.simulator.control;

import mutex.display.DisplayControllerParameters;
import mutex.display.ElementController;
import mutex.display.controllers.DefaultDisplay;
import mutex.editor.model.AlgorithmClassLoader;
import mutex.simulator.model.CodeConstants;
import mutex.simulator.model.DefaultConstants;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.HashMap;

public class SimulationParameters implements Serializable
{
	// to avoid InvalidClassExceptions, while allowing me to add new parameters later,
	// the version ID stays constant
	//(note to self: fields can be added/removed, but types can't be changed)
	public static final long serialVersionUID = 1L;

	private String myAlgorithmName;
	private AlgorithmClassLoader myClassLoader;
	private CodeConstants myCode;

	//one of these two will always be null.
	//the simulation takes either a custom display and needs a name
	//or a pre-built display with variable names
	private String myDisplayName;
	private ElementController myElementController;

	private int myNumThreads;
	
	public SimulationParameters()
	{
		setSafeDefaults();
	}
	
	private void setSafeDefaults()
	{
		myClassLoader = new AlgorithmClassLoader(new HashMap<>());
		myAlgorithmName = "Default";

		myCode = new DefaultConstants();

		myDisplayName = null;
		myElementController = new DefaultDisplay(myNumThreads);

		myNumThreads = 2;
	}
	
	public void setClassLoader(AlgorithmClassLoader loader){
		if(loader != null) myClassLoader = loader;
	}
	public ClassLoader getClassLoader(){
		return myClassLoader;
	}
	
	public void setAlgorithmName(String name){
		if(name != null) myAlgorithmName = name;
	}
	public String getAlgorithmName(){
		return myAlgorithmName;
	}
	
	public void setCode(CodeConstants code){
		if(code != null) myCode = code;
	}
	public CodeConstants getCode(){
		return myCode;
	}

	public String getDisplayName() {
		return myDisplayName;
	}
	public void setDisplayName(String myDisplayName) {
		this.myDisplayName = myDisplayName;
	}

	public void setDisplay(ElementController display){
		myElementController = display;
	}
	public ElementController getDisplay(){
		return myElementController;
	}
	
	public void setNumThreads(int threads){
		if(threads > 0) myNumThreads = threads;
	}
	public int getNumThreads(){
		return myNumThreads;
	}

	//serializable methods
	private void writeObject(java.io.ObjectOutputStream out)
			throws IOException {
		out.writeObject(myAlgorithmName);
		out.writeObject(myClassLoader);
		out.writeObject(myCode);
		out.writeObject(myDisplayName);
		if(myDisplayName == null) {
			//for consistency, if there isnt a custom name, there has to be a controller
			if(myElementController == null)
				myElementController = new DefaultDisplay(2);
			out.writeObject(myElementController.getParameters());
		}
		out.writeInt(myNumThreads);
	}
	private void readObject(java.io.ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		myAlgorithmName = (String)in.readObject();
		myClassLoader = (AlgorithmClassLoader)in.readObject();
		myCode = (CodeConstants) in.readObject();
		myDisplayName = (String) in.readObject();
		if(myDisplayName == null) {
			myElementController = ElementController.getController((DisplayControllerParameters)in.readObject());
		}
		myNumThreads = in.readInt();
	}
	private void readObjectNoData()
			throws ObjectStreamException {
		setSafeDefaults();
	}
}
