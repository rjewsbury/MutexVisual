package mutex.simulator.model;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public abstract class AlgorithmThread implements Runnable
{
	//not private because they need to be referenced by the actual algorithm
	protected int N;
	protected int ID;
	
	private boolean isPaused;
	private boolean inCritical;
	private int myCriticalCount;
	private boolean running;
	private int myStepNumber;
	private Value<?> myNextVal;
	private Thread myThread;
	private Map<String, Wrapper> mySharedVariables;
	private Map<String, Wrapper> myVariables;

	private RuntimeException exception;
	
	//potentially a better way to choose color consistently across views?
	private Color myColor;
	
	/**
	 * Member variables that are initialized during declaration
	 * (public int x = 7;)
	 * do not get initialized until after the super constructor is called.
	 * because the initialization process for these classes needs those
	 * values to exist, initialize and initializeShared are called by the child
	 */
	public AlgorithmThread(AlgorithmParams params)
	{
		N = params.getN();
		ID = params.getID();
		mySharedVariables = params.getSharedVars();
		
		isPaused = false;
		inCritical = false;
		myCriticalCount = 0;
		myStepNumber = 0;
		myNextVal = null;
		myVariables = new HashMap<>();
		
		//allows colors to be consistent across displays
		//myColor = Color.getHSBColor((float)Math.random(), 0.9f, 0.8f);
		myColor = Color.getHSBColor((float)ID/N, 0.9f, 0.8f);
	}

	public RuntimeException getException() {
		return exception;
	}
	
	protected abstract void initializeShared();
	/**
	 * If a shared variable already exists, return the
	 * existing pointer instead of creating a new one
	 */
	protected Wrapper addSharedVariable(String name, Wrapper v){
		if(mySharedVariables.containsKey(name))
		{
			return mySharedVariables.get(name);
		}
		else
		{
			//System.out.println(name+" "+v);
			mySharedVariables.put(name, v);
			return v;
		}
	}
	public Map<String, Wrapper> getSharedVariables(){
		return mySharedVariables;
	}
	
	protected abstract void initialize();
	protected void addVariable(String name, Wrapper v){
		myVariables.put(name, v);
	}
	public Map<String, Wrapper> getVariables(){
		return myVariables;
	}
	
	public void setPreferredColor(Color color)
	{
		myColor = color;
	}
	public Color getPreferredColor()
	{
		return myColor;
	}

	protected synchronized void CriticalSection(int stepNum, int time) throws InterruptedException{
		myCriticalCount = time;

		//this is used to tell displays where the algorithm is
		inCritical = true;
		//this is used only for debugging
		//criticalID = ID;
		//System.out.println(ID+" in the critical section");


		try{
			while(myCriticalCount > 0) {
				pause(stepNum, null);
				myCriticalCount--;
			}
		}finally{
			//have to exit critical section even if interrupted
			inCritical = false;
		}
		//if(ID != criticalID)
		//	System.err.println("MUTUAL EXCLUSION BROKEN BY "+ID+" AND "+ criticalID);
		//System.out.println(ID+" leaving the critical section");
	}

	protected synchronized void CriticalSection(int stepNum) throws InterruptedException
	{
		CriticalSection(stepNum, 1);
	}
	
	public void start()
	{
		if(myThread == null || !running)
		{
			myThread = new Thread(this);
			
			//do I even need this?
			myThread.setDaemon(true);
			
			myThread.start();
		}
	}
	
	public synchronized void resetAlgorithm()
	{
		//tells the algorithm to start from the beginning
		initialize();

		if(running) {
			isPaused = false;
			myThread.interrupt();

			//wait for the algorithm to reach the first step again
			while(!isPaused)
			{
				try{
					this.wait();
				}catch (InterruptedException e){
					e.printStackTrace();
				}
			}
		}
		else {
			start();
		}
	}
	
	public void stop()
	{
		running = false;
		myThread.interrupt();
		myThread = null;
	}
	
	protected abstract void algorithm() throws InterruptedException;

	public void run()
	{
		running = true;
		while (running) {
			try {
				algorithm();
			} catch (InterruptedException e) {
				// if the thread was interrupted, simply restart the algorithm
			} catch (RuntimeException e) {
				// if there was any other runtime exception,
				// stop the simulation, and warn the controller
				exception = e;
				//allows the controller to exit the wait loop
				isPaused = true;
				running = false;

				synchronized (this) {
					this.notifyAll();
				}
			}
		}
	}
	
	public int getID()
	{
		return ID;
	}
	
	public int getStepNumber()
	{
		return myStepNumber;
	}
	
	public boolean isInCritical()
	{
		return inCritical;
	}
	
	public Value<?> getNextValue()
	{
		return myNextVal;
	}
	
	protected <E> void pauseWrite(int stepNum, Value<E> val, E newVal) throws InterruptedException
	{
		pause(stepNum, val);
		val.set(newVal);
	}
	protected <E> E pauseRead(int stepNum, Value<E> val) throws InterruptedException
	{
		pause(stepNum, val);
		return val.get();
	}
	
	//POSSIBLY USE UTIL.CONCURRENT OBJECTS INSTEAD OF WAIT/NOTIFY
	/**
	 * Causes the thread to wait until it is allowed to make another step
	 * @param stepNum - the current step the algorithm is trying to do.
	 * 					used so the displays can know where the thread is
	 * @throws InterruptedException thrown when
	 */
	protected synchronized void pause(int stepNum, Value<?> nextVal) throws InterruptedException
	{
		//tells the calling thread that it may continue
		this.notify();
		isPaused = true;
		myStepNumber = stepNum;
		myNextVal = nextVal;
		
		while(isPaused)
		{
			//may be interrupted for reset purposes
			this.wait();
		}
	}
	
	public synchronized void allowStep()
	{
		//tells the algorithm to do a single step
		this.notify();
		isPaused = false;
		
		while(!isPaused)
		{
			try{
				this.wait();
			}catch (InterruptedException e){
				e.printStackTrace();
			}
		}
	}
}