package mutex.simulator.model;

import java.lang.reflect.Array;

/**
 * A wrapper object for values and arrays of values
 * so that they can all be stored together and displayed
 * in the variable menu
 *
 * @author Ryley Jewsbury
 */
public class Wrapper
{
	Object myVariable;
	int arrayDimension;
	
	//cannot accept primitive array types due to cast?
	public Wrapper(Object variable)
	{
		myVariable = variable;
		
		int i=0;
		for(; variable.getClass().isArray(); i++){
			//assumes that the array contains at least one element
			variable = Array.get(variable, 0);
		}
		
		arrayDimension = i;
	}
	
	public Object getVariable()
	{
		return myVariable;
	}
	
	public int getDimension()
	{
		return arrayDimension;
	}
}
