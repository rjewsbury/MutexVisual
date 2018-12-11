package mutex.display.controllers;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mutex.display.ElementController;
import mutex.display.elements.*;
import mutex.editor.model.Field;
import mutex.simulator.model.AlgorithmThread;
import mutex.simulator.model.AlgorithmThreadGroup;
import mutex.simulator.model.Value;

public class StairDisplay extends ElementController
{
	private ThreadBall[] myThreads;
	private RaceQueue[] levels;
	private CriticalSection cs;
	private NonCriticalSection ncs;
	private String capturedVarName = "slowest";
	private String progressVarName = "level";
	
	public StairDisplay(int numThreads)
	{
		cs = new CriticalSection(80);
		ncs = new NonCriticalSection(numThreads, 80, 450);
		
		cs.setX(50+80*numThreads);
		cs.setY(200);
		
		ncs.setX(50);
		ncs.setY(50);
		
		setNumThreads(numThreads);
	}
	
	@Override
	public void setNumThreads(int n)
	{
		clearThreads();
		
		ncs.setSize(n);
		cs.setX(50+80*n);
		
		levels = new RaceQueue[n-1];
		for(int i = 0; i < levels.length; i++)
		{
			levels[i] = new RaceQueue(n-i, 80, Direction.DOWN);
			levels[i].setY(200);
			levels[i].setX(50+80*i);
		}
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
	public void addThreads(AlgorithmThreadGroup threads)
	{
		myThreads = new ThreadBall[threads.size()];
		
		for(int i=0; i<threads.size(); i++)
			myThreads[i] = new ThreadBall(
					threads.getThread(i).getID(),
					threads.getThread(i).getPreferredColor());

		for(int i=0; i<threads.size(); i++)
			update(threads.getThread(i));
	}
	
	@Override
	public List<ViewElement> getElements()
	{
		ArrayList<ViewElement> elements = new ArrayList<>();
		
		elements.add(cs);
		elements.add(ncs);
		elements.addAll(Arrays.asList(levels));
		
		return elements;
	}

	@Override
	public void update(AlgorithmThread updated)
	{
		int ID = updated.getID();

		Value<Integer>[] slowest;
		Value<Integer>[] level;

		try {
			slowest = (Value<Integer>[]) updated.getSharedVariables().get(capturedVarName).getVariable();
		} catch(NullPointerException e) {
			throw new MissingVariableException(capturedVarName);
		}
		try {
			level = (Value<Integer>[]) updated.getSharedVariables().get(progressVarName).getVariable();
		} catch (NullPointerException e) {
			throw new MissingVariableException(progressVarName);
		}
		
		for(int i=0; i<levels.length;i++)
			levels[i].setRaceID(slowest[i+1].get());
		
		if(level[ID].get() == 0)
		{
			if(!myThreads[ID].inContainer(ncs))
			{
				ncs.addLast(myThreads[ID]);
				myThreads[ID].setContainer(ncs);
			}
		}
		else if(updated.isInCritical())
		{
			if(!myThreads[ID].inContainer(cs))
			{
				cs.addFirst(myThreads[ID]);
				myThreads[ID].setContainer(cs);
			}
		}
		else
		{
			if(!myThreads[ID].inContainer(levels[level[ID].get()-1]))
			{
				levels[level[ID].get()-1].addLast(myThreads[ID]);
				myThreads[ID].setContainer(levels[level[ID].get()-1]);
			}
		}
	}

	@Override
	public List<Field> getEditableFields()
	{
		List<Field> fields = new ArrayList<>();
		
		fields.add(new Field("(int[]) Captured ID Var",capturedVarName,String.class));
		fields.add(new Field("(int[]) Progress Var",progressVarName,String.class));
		
		return fields;
	}

	@Override
	public boolean setField(Field field)
	{
		boolean valid = false;
		switch(field.getName())
		{
			case "(int[]) Captured ID Var":
				capturedVarName = (String)field.getValue();
				valid = true;
				break;
			case "(int) Progress Var":
				progressVarName = (String)field.getValue();
				valid = true;
				break;
		}
		
		return valid;
	}

}
