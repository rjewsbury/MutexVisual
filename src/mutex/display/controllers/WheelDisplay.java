package mutex.display.controllers;

import java.util.ArrayList;
import java.util.List;

import mutex.display.ElementController;
import mutex.display.elements.*;
import mutex.simulator.model.AlgorithmThread;
import mutex.simulator.model.AlgorithmThreadGroup;
import mutex.simulator.model.Value;
import mutex.editor.model.Field;

public class WheelDisplay extends ElementController
{
	private ThreadBall[] myThreads;
	private TurnWheel turnWheel;
	private CriticalSection cs;
	private NonCriticalSection ncs;
	private String arrowVarName = "turn";
	private String glowVarName = "passed";
	private String wheelVarName = "intent";
	
	public WheelDisplay(int numThreads)
	{
		turnWheel = new TurnWheel(numThreads,150);
		cs = new CriticalSection(80);
		ncs = new NonCriticalSection(numThreads, 80, 450);
		
		turnWheel.setCenterX(200);
		turnWheel.setCenterY(350);
		
		cs.setX(400);
		cs.setY(200);
		
		ncs.setX(50);
		ncs.setY(50);
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
	public ArrayList<ViewElement> getElements()
	{
		ArrayList<ViewElement> elements = new ArrayList<>();
		
		elements.add(turnWheel);
		elements.add(cs);
		elements.add(ncs);
		
		return elements;
	}

	@Override
	public void update(AlgorithmThread updated)
	{
		int ID = updated.getID();
		try {
			turnWheel.setTurn(((Value<Integer>)updated.getSharedVariables().get(arrowVarName).getVariable()).get());
		} catch (NullPointerException e) {
			throw new MissingVariableException(arrowVarName);
		}

		Value<Boolean>[] passed;
		Value<Boolean>[] intent;
		try {
			passed = (Value<Boolean>[]) updated.getSharedVariables().get(glowVarName).getVariable();
		} catch (NullPointerException e) {
			throw new MissingVariableException(glowVarName);
		}
		try {
			intent = (Value<Boolean>[]) updated.getSharedVariables().get(wheelVarName).getVariable();
		} catch (NullPointerException e) {
			throw new MissingVariableException(wheelVarName);
		}
		
		if(!intent[ID].get())
		{
			if(ncs.getElement(ID) == null)
			{
				ncs.addLast(myThreads[ID]);
				myThreads[ID].setContainer(ncs);
			}
		}
		else if(!updated.isInCritical())
		{
			if(turnWheel.getElement(ID) == null)
			{
				turnWheel.addElement(myThreads[ID], ID);
				myThreads[ID].setContainer(turnWheel);
			}
		}
		else if(!myThreads[ID].inContainer(cs))
		{
			cs.addFirst(myThreads[ID]);
			myThreads[ID].setContainer(cs);
		}
		
		
		myThreads[ID].setLight(passed[ID].get());
		if(passed[ID].get())
			myThreads[ID].setRadius(25);
		else
			myThreads[ID].setRadius(20);
	}

	@Override
	public void setNumThreads(int n)
	{
		clearThreads();
		
		ncs.setSize(n);
		turnWheel.setSize(n);
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
	public List<Field> getEditableFields()
	{
		List<Field> fields = new ArrayList<>();
		
		fields.add(new Field("(int) Arrow Var",arrowVarName,String.class));
		fields.add(new Field("(bool[]) Wheel Var",wheelVarName,String.class));
		fields.add(new Field("(bool[]) Highlight Var",glowVarName,String.class));
		
		return fields;
	}

	@Override
	public boolean setField(Field field)
	{
		boolean valid = false;
		switch(field.getName())
		{
			case "(int) Arrow Var":
				arrowVarName = (String)field.getValue();
				valid = true;
				break;
			case "(bool[]) Wheel Var":
				wheelVarName = (String)field.getValue();
				valid = true;
				break;
			case "(bool[]) Highlight Var":
				glowVarName = (String)field.getValue();
				valid = true;
				break;
		}
		
		return valid;
	}
}
