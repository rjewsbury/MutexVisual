package mutex.simulator.model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;

import mutex.io.PopupManager;
import mutex.simulator.control.ControlListener;
import mutex.simulator.control.DelaySlider;
import mutex.simulator.control.ResetButton;
import mutex.simulator.control.RunMenu;

/**
 * Controls the movement of an array of algorithm threads
 *
 * @author Ryley Jewsbury
 */
public class ThreadController implements Runnable, ControlListener
{
	private Random myRNG;
	private AlgorithmThreadGroup myThreads;
	
	private boolean modelRunning;
	private int myDelay;
	//if true, prevents the simulation from running when mutual exclusion is broken
	private boolean pauseCritical;
	
	private boolean[] threadRunning;
	private int runningCount;
	private Thread modelThread;
	
	//uses an observer pattern to notify views of model updates
	private ArrayList<ThreadUpdateListener> myUpdateListeners;
	//uses action events to notify control if threads are running
	private ArrayList<ActionListener> myActionListeners;
	
	public ThreadController(AlgorithmThreadGroup threads)
	{
		setThreadGroup(threads);

		modelRunning = false;
		pauseCritical = false;
		myDelay = DelaySlider.INIT;
		myRNG = new Random();
		myUpdateListeners = new ArrayList<>();
		myActionListeners = new ArrayList<>();
	}

	public void setThreadGroup(AlgorithmThreadGroup threads) {
		if(myThreads != null) {
			for(int i = 0; i < myThreads.size(); i++)
			{
				myThreads.getThread(i).stop();
			}
		}

		myThreads = threads;
		for(int i = 0; i < myThreads.size(); i++)
		{
			myThreads.getThread(i).start();
		}

		runningCount = myThreads.size();
		threadRunning = new boolean[runningCount];
		Arrays.fill(threadRunning, true);
	}

	public void pauseCriticalSection(boolean pause) {
		pauseCritical = pause;
	}
	
	public void addUpdateListener(ThreadUpdateListener l)
	{
		myUpdateListeners.add(l);
		
		//make the state consistent with the threads
		for(int i=0; i<myThreads.size(); i++)
			l.update(myThreads.getThread(i));
	}
	
	public void updateListeners(AlgorithmThread t)
	{
		for(ThreadUpdateListener l: myUpdateListeners)
			l.update(t);
	}

	public void addActionListener(ActionListener l)
	{
		myActionListeners.add(l);
	}

	public void sendAction(String actionCommand) {
		ActionEvent event = new ActionEvent(this, myThreads.size(), actionCommand);

		for (ActionListener l : myActionListeners) {
			l.actionPerformed(event);
		}
	}
	
	/**
	 * needs to be synchronized so that pressing step while the simulation is running
	 * still ensures that only one thread is ever executing.
	 * 
	 * might not be necessary. maybe prevents weird cache-storing bugs?
	 *
	 * if threadID is out of bounds, chooses a random thread
	 */
	public synchronized void step(int threadID)
	{
		if(runningCount == 0)
			return;

		int ID;
		if(threadID < 0 || threadID >= myThreads.size()) {
			int turn = myRNG.nextInt(runningCount);

			/*int turn = (int)Math.round(myRNG.nextGaussian()*runningCount/5.0+(runningCount-1)/2.0);
			if(turn < 0)
				turn = 0;
			if(turn >= runningCount)
				turn = runningCount-1;*/

			for(ID = 0; !threadRunning[ID] || turn > 0; ID++)
			{
				if(threadRunning[ID])
					turn--;
			}
		}
		else
			ID = threadID;


		//waits for the thread to reach the next pause
		if(myThreads.getThread(ID).getException() == null) {
			//System.out.println("Stepping! "+ID);
			myThreads.getThread(ID).allowStep();
		}

		//if an exception was thrown,
		if(myThreads.getThread(ID).getException() != null) {
			System.out.println("Error! Thread "+ID);

			modelRunning = false;

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(os);
			myThreads.getThread(ID).getException().printStackTrace(ps);
			PopupManager.errorMessage(os.toString());
		}
		
		updateListeners(myThreads.getThread(ID));
	}

	public boolean mutualExclusionBroken() {
		boolean inCritical = false;
		for(int i = 0; i < myThreads.size(); i++) {
			if(myThreads.getThread(i).isInCritical()) {
				if (inCritical) {
					//there's already another thread in the critical section
					return true;
				}
				else
					inCritical = true;
			}
		}
		return false;
	}
	
	public void run()
	{
		modelRunning = true;

		//check if mutual exclusion is broken
		while(modelRunning && (!pauseCritical || !mutualExclusionBroken()))
		{
			step(-1);
			
			try{
				Thread.sleep(myDelay);
			}catch (InterruptedException e){
				e.printStackTrace();
			}
		}

		modelRunning = false;
		sendAction(RunMenu.STOP_MESSAGE);
	}
	
	public void start()
	{
		if(!modelRunning) {
			modelThread = new Thread(this);
			modelThread.start();
		}
	}
	
	public void stop()
	{
		if(modelThread != null && modelRunning)
		{
			modelRunning = false;
			try{
				modelThread.join();
				modelThread = null;
			}catch (InterruptedException e){
				e.printStackTrace();
			}
		}
	}

	//maybe use the Simulator frame to rebuild everything instead of rebuilding them seperately?
	public void reset()
	{
		stop();
		myThreads.initializeShared();
		for(int i=0; i<myThreads.size(); i++)
		{
			AlgorithmThread t = myThreads.getThread(i);
			t.resetAlgorithm();
			this.updateListeners(t);
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		switch(e.getActionCommand())
		{
			case RunMenu.RUN_MESSAGE:
				start();
				break;
			case RunMenu.STOP_MESSAGE:
				stop();
				break;
			case RunMenu.STEP_MESSAGE:
				step(e.getID());
				break;
			case ResetButton.RESET_MESSAGE:
				reset();
				break;
			default:
				System.out.println("I don't recognize \""+e.getActionCommand()+"\"");
		}
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		if(e.getSource() instanceof JSlider)
			myDelay = ((JSlider)e.getSource()).getValue();
	}

	@Override
	public void itemStateChanged(ItemEvent e)
	{
		int ID = e.getID();
		if(e.getStateChange() == ItemEvent.SELECTED)
		{
			runningCount++;
			threadRunning[ID] = true;
		}
		else
		{
			runningCount--;
			threadRunning[ID] = false;
		}
	}
}
