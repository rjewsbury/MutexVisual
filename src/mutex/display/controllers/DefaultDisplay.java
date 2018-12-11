package mutex.display.controllers;

import java.util.Arrays;
import java.util.List;

import mutex.display.ElementController;
import mutex.display.elements.CriticalSection;
import mutex.display.elements.NonCriticalSection;
import mutex.display.elements.ThreadBall;
import mutex.display.elements.ViewElement;
import mutex.editor.model.Field;
import mutex.simulator.model.AlgorithmThread;
import mutex.simulator.model.AlgorithmThreadGroup;

public class DefaultDisplay extends ElementController
{
	private ThreadBall[] myThreads;
	private NonCriticalSection ncs;
	private CriticalSection cs;
	
	public DefaultDisplay(int numThreads)
	{
		cs = new CriticalSection(80);
		ncs = new NonCriticalSection(numThreads, 80, 450);
		
		cs.setX(50);
		cs.setY(50);
		
		ncs.setX(150);
		ncs.setY(50);
	}
	
	@Override
	public void setNumThreads(int n)
	{
		//just in case, make sure there are no threads before changing size
		clearThreads();
		
		ncs.setSize(n);
	}

	@Override
	public void addThreads(AlgorithmThreadGroup threads)
	{
		setNumThreads(threads.size());
		
		myThreads = new ThreadBall[threads.size()];
		
		for(int i=0; i<threads.size(); i++)
			myThreads[i] = new ThreadBall(
					threads.getThread(i).getID(),
					threads.getThread(i).getPreferredColor());
		
		for(int i=0; i<threads.size(); i++)
			update(threads.getThread(i));
	}

	@Override
	public void clearThreads()
	{
		if(myThreads == null)
			return;
		
		for(ThreadBall ball: myThreads)
		{
			ball.leaveContainer();
		}
		myThreads = null;
	}

	@Override
	public List<ViewElement> getElements()
	{
		return Arrays.asList(ncs, cs);
	}

	@Override
	public void update(AlgorithmThread updated)
	{
		int ID = updated.getID();
		if(!updated.isInCritical()){
			myThreads[ID].setContainer(ncs);
			ncs.addFirst(myThreads[updated.getID()]);
		}
		else if(!myThreads[ID].inContainer(cs))
		{
			myThreads[ID].setContainer(cs);
			cs.addFirst(myThreads[ID]);
		}
	}
	
	@Override
	public List<Field> getEditableFields()
	{
		return null;
	}

	@Override
	public boolean setField(Field field)
	{
		return false;
	}
}
