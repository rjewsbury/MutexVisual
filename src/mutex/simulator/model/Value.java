package mutex.simulator.model;

import java.lang.reflect.Array;

/**
 * A generic variable. this seems really strange,
 * but it was the only way I could come up with to
 * have AlgorithmThread methods that work regardless
 * of what the algorithm is, or what shared memory exists.
 * 
 * I guess I'm trying to make Java dynamically typed?
 * not really sure why I'm using Java then.
 *
 * @author Ryley Jewsbury
 */
public class Value<E>
{
	/**
	 * Currently not in use. creates variable arrays as nested 1D arrays of variables,
	 * opposes the current design of having wrapped multi-dimensional arrays of values
	 */
	private static Value buildVariable(Object var)
	{
		if(var.getClass().isArray())
		{
			int length = Array.getLength(var);
			Value<?>[] result = new Value[length];
			for(int i=0; i < length; i++)
				result[i] = buildVariable(Array.get(var, i));
			
			return new Value<>(result);
		}
		else
		{
			return new Value<>(var);
		}
	}
	
	private <T> T getVariable(int index)
	{
		T[] value = (T[]) myValue;
		return value[index];
	}
//------------------------
	
	private E myValue;
	
	public Value(E initial)
	{
		//does not work for array types
		myValue = initial;
	}
	
	public void set(E val)
	{
		myValue = val;
	}
	
	public E get()
	{
		return myValue;
	}
	
	public String toString()
	{
		if(myValue == null)
			return "null";
		return myValue.toString();
	}
}
