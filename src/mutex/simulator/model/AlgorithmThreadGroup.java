package mutex.simulator.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles shared memory between a group of threads
 *
 * @author Ryley Jewsbury
 */
public class AlgorithmThreadGroup
{
	private Class<? extends AlgorithmThread> myAlgorithmClass;
	private AlgorithmThread[] myThreads;
	private Map<String, Wrapper> mySharedVars;
	
	public AlgorithmThreadGroup(Class<? extends AlgorithmThread> algorithmClass, int size)
			throws InstantiationException, IllegalAccessException,
			NoSuchMethodException, SecurityException,
			IllegalArgumentException, InvocationTargetException
	{
		myAlgorithmClass = algorithmClass;
		resetGroup(size);
	}

	public void stopThreads() {
		if(myThreads != null) {
			for(AlgorithmThread thread : myThreads){
				thread.stop();
			}
		}
	}

	public void resetGroup(int size) throws
			IllegalAccessException, InvocationTargetException,
			InstantiationException, NoSuchMethodException {
		if(myThreads != null) {
			stopThreads();
		}
		else {
			myThreads = new AlgorithmThread[size];
		}

		if(mySharedVars != null) {
			mySharedVars.clear();
		}
		else {
			mySharedVars = new HashMap<>();
		}

		AlgorithmParams params;
		for(int i=0; i<myThreads.length; i++)
		{
			params = new AlgorithmParams(i, size, mySharedVars);
			Constructor<? extends AlgorithmThread> constructor = myAlgorithmClass.getConstructor(AlgorithmParams.class);
			myThreads[i] = constructor.newInstance(params);
		}
	}
	
	public void initializeShared()
	{
		/*
		 * With the current implementation, if there are any threads paused
		 * waiting to read a shared variable, they may return the wrong value
		 */
		mySharedVars.clear();
		for(AlgorithmThread thread: myThreads)
			thread.initializeShared();
	}
	public Map<String, Wrapper> getSharedVariables()
	{
		return mySharedVars;
	}
	
	public int size()
	{
		return myThreads.length;
	}
	
	/**
	 * Not sure I like giving complete access to each thread,
	 * or accessing them by index instead of ID
	 */
	public AlgorithmThread getThread(int index)
	{
		return myThreads[index];
	}
}
