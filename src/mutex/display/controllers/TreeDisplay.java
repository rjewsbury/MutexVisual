package mutex.display.controllers;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mutex.display.ElementController;
import mutex.display.elements.*;
import mutex.editor.model.Field;
import mutex.simulator.model.AlgorithmThread;
import mutex.simulator.model.AlgorithmThreadGroup;
import mutex.simulator.model.Value;

public class TreeDisplay extends ElementController
{
	private static final int RADIUS = 40;
	private ThreadBall[] myThreads;
	
	private CriticalSection cs;
	private NonCriticalSection ncs;
	private TurnWheel[][] races;
	private String competitionVarName = "intents";
	private String arrowVarName = "turns";
	private String progressVarName = "level";
	
	public TreeDisplay(int numThreads)
	{
		cs = new CriticalSection(80);
		ncs = new NonCriticalSection(numThreads, 80, 450);
		
		cs.setX(50);
		cs.setY(50);
		
		ncs.setX(150);
		ncs.setY(50);
		
		setNumThreads(numThreads);
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
		
		for(TurnWheel[] row: races)
			Collections.addAll(elements, row);
		elements.add(cs);
		elements.add(ncs);
		
		return elements;
	}

	@Override
	public void update(AlgorithmThread updated)
	{
		int ID = updated.getID();

		Value<Boolean>[][] intents;
		Value<Integer>[][] turns;
		Value<Integer> level;

		try {
			intents = (Value<Boolean>[][]) updated.getSharedVariables().get(competitionVarName).getVariable();
		} catch (NullPointerException e) {
			throw new MissingVariableException(competitionVarName);
		}
		try {
			turns = (Value<Integer>[][]) updated.getSharedVariables().get(arrowVarName).getVariable();
		} catch (NullPointerException e) {
			throw new MissingVariableException(arrowVarName);
		}
		try {
			level = (Value<Integer>) updated.getVariables().get(progressVarName).getVariable();
		} catch (NullPointerException e) {
			throw new MissingVariableException(progressVarName);
		}
		
		if(level.get() < 1 && !myThreads[ID].inContainer(ncs)){
			ncs.addLast(myThreads[ID]);
			myThreads[ID].setContainer(ncs);
		}
		for(int i=level.get(); i < races.length && i >= 0; i--)
		{
			races[i][ID>>(i+1)].setTurn((turns[i][ID>>(i+1)].get()+1)%2);
			if(intents[i][ID>>i].get())
			{
				if(races[i][ID>>(i+1)].getElement(((ID>>i)+1)%2)==null)
				{
					races[i][ID>>(i+1)].addElement(myThreads[ID],((ID>>i)+1)%2);
					myThreads[ID].setContainer(races[i][ID>>(i+1)]);
				}
				break;
			}
		}
		if(updated.isInCritical()){
			cs.addFirst(myThreads[ID]);
			myThreads[ID].setContainer(cs);
		}
		
	}

	@Override
	public List<Field> getEditableFields()
	{
		List<Field> fields = new ArrayList<>();
		
		fields.add(new Field("(int[][]) Arrow Var",arrowVarName,String.class));
		fields.add(new Field("(bool[][]) Competition Var",competitionVarName,String.class));
		fields.add(new Field("(int) Progress Var",progressVarName,String.class));
		
		return fields;
	}

	@Override
	public boolean setField(Field field)
	{
		boolean valid = false;
		switch(field.getName())
		{
			case "(int[][]) Arrow Var":
				arrowVarName = (String)field.getValue();
				valid = true;
				break;
			case "(bool[][]) Competition Var":
				competitionVarName = (String)field.getValue();
				valid = true;
				break;
			case "(int) Progress Var":
				progressVarName = (String)field.getValue();
				valid = true;
				break;
		}
		
		return valid;
	}

	@Override
	public void setNumThreads(int n)
	{
		clearThreads();
		ncs.setSize(n);
		
		int width = 1;
		int height = 1;
		while(2*width < n){
			width *= 2;
			height += 1;
		}
		races = new TurnWheel[height][];
		for(int j=0; j<height; j++){
			races[j] = new TurnWheel[width];
			for(int k=0; k<width; k++){
				races[j][k] = new TurnWheel(2,RADIUS);
				races[j][k].setCenterX(2*RADIUS+(int)(1.5*RADIUS*((1<<j)-1))+3*RADIUS*k*(1<<j));
				races[j][k].setCenterY(200+3*RADIUS*j);
				races[j][k].setTurn(1);
				//races[j][k].setBorder(false);
			}
			width /= 2;
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
}
