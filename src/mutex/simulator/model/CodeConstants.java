package mutex.simulator.model;

import java.io.Serializable;

/**
 * This contains all of the algorithm specific code information.
 * would be generated along with CurrentAlgorithmThread's algorithm
 *
 * @author Ryley Jewsbury
 */
public class CodeConstants implements Serializable
{
	// to avoid InvalidClassExceptions, while allowing me to add new parameters later,
	// the version ID stays constant
	//(note to self: fields can be added/removed, but types can't be changed)
	public static final long serialVersionUID = 1L;

	final String TAB_REPLACEMENT = "    ";	//4 spaces
	private String[] myCode;
	private Step[] mySteps;
	
	public CodeConstants(String[] code, Step[] steps)
	{
		myCode = code;
		for(int i=0; i < myCode.length; i++)
			myCode[i] = myCode[i].replaceAll("\t", TAB_REPLACEMENT);
		mySteps = steps;
	}
	
	public int getCodeLength(){
		return myCode.length;
	}
	public String getLine(int n){
		return myCode[n];
	}
	
	public int getMaxSteps(){
		return mySteps.length;
	}
	public Step getStep(int n){
		return mySteps[n];
	}
}
