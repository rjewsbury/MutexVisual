package mutex.simulator.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JPanel;

import mutex.display.elements.ThreadFrame;
import mutex.display.elements.VariableCell;
import mutex.simulator.model.*;

public class VariableDisplayPanel extends JPanel implements ThreadUpdateListener
{
	public static final int BUFFER = 20;
	//the default width/height for cells
	public static final int WIDTH = 70;
	public static final int HEIGHT = 30;
	
	private ThreadFrame[] myFrames;
	//used to track the thread array for redrawing cells
	//see: public void update
	private AlgorithmThreadGroup myThreads;
	private ArrayList<VariableCell> myCells;
	private int maxWidth;
	private int maxCol;
	private int rowNum;
	private int colNum;
	
	public VariableDisplayPanel(AlgorithmThreadGroup threads)
	{
		this.setBackground(Color.WHITE);
		maxWidth = 900;//BUFFER*2 + model.getThreads().length * WIDTH;

		setThreadGroup(threads);
	}

	public void setThreadGroup(AlgorithmThreadGroup threads) {
		//is there a way to get around keeping this reference?
		myThreads = threads;

		buildCells(threads);

		myFrames = new ThreadFrame[threads.size()];
		for(int i=0; i< myFrames.length; i++)
		{
			myFrames[i] = new ThreadFrame(
					threads.getThread(i).getID(),
					threads.getThread(i).getPreferredColor());
		}

		//quick fix to avoid making space for an extra row if the row is empty
		if(colNum == 0)
			rowNum--;

		this.setMinimumSize(new Dimension(
				2*BUFFER+(maxCol+1)*WIDTH,
				BUFFER + (HEIGHT+BUFFER) * (rowNum+1)));
		this.setPreferredSize(getMinimumSize());
	}
	
	private void buildCells(AlgorithmThreadGroup threads)
	{
		rowNum = 0;
		colNum = 0;
		maxCol = 0;
		myCells = new ArrayList<>();
		addVariables(threads.getSharedVariables(), null);
		
		for(int i=0; i< myThreads.size(); i++)
		{
			addVariables(myThreads.getThread(i).getVariables(), "Thd"+i+":");
		}
	}
	
	private void newLine()
	{
		rowNum++;
		colNum = 0;
	}
	
	private void nextCell()
	{
		colNum++;
		if(getCellX()+WIDTH > maxWidth)
			newLine();
		
		if(colNum > maxCol)
			maxCol = colNum;
	}
	
	private int getCellX()
	{
		return BUFFER + (WIDTH)*colNum;
	}
	
	private int getCellY()
	{
		return BUFFER + (HEIGHT+BUFFER)*rowNum;
	}
	
	private void addVariables(Map<String, Wrapper> variables, String namePrefix)
	{
		Wrapper var;
		
		for(String name: variables.keySet())
		{
			var = variables.get(name);
			
			if(namePrefix != null)
				name = namePrefix + name;
			
			addCell(var.getVariable(), var.getDimension(), name, false);
			
			if(var.getDimension() > 0 && colNum > 0)
				nextCell();	//add a space so the next variable isn't touching
			
		}
	}
	
	private void addCell(Object variable, int dimension, String name, boolean connected)
	{
		if(dimension > 0)
		{
			if(colNum > 0)
				newLine();
			
			for(int i=0; i < Array.getLength(variable); i++)
				addCell(Array.get(variable, i), dimension-1, i==0?name:null, true);
		}
		else
		{
			VariableCell cell;
			
			cell = new VariableCell(name, (Value<?>)variable);
			cell.setWidth(WIDTH-(connected?0:BUFFER));
			cell.setHeight(HEIGHT);
			cell.setX(getCellX());
			cell.setY(getCellY());
			myCells.add(cell);
			
			//System.out.println("line:"+rowNum+"\tcol:"+colNum);
			
			nextCell();
		}
	}
	
	@Override
	public synchronized void update(AlgorithmThread t)
	{
		buildCells(myThreads);
		
		boolean found = false;
		for(VariableCell cell: myCells)
		{
			if(cell.getValue().equals(t.getNextValue()))
			{
				myFrames[t.getID()].setX(cell.getX());
				myFrames[t.getID()].setY(cell.getY());
				myFrames[t.getID()].setWidth(cell.getWidth());
				myFrames[t.getID()].setHeight(cell.getHeight());
				
				myFrames[t.getID()].setTagOffset(
						(cell.getWidth()-ThreadFrame.TAG_WIDTH)
						* t.getID()/myFrames.length);
				
				found = true;
				break;
			}
		}
		
		if(!found)
			myFrames[t.getID()].setX(-1);
		
		repaint();
	}
	
	@Override
	public synchronized void paintComponent(Graphics g)
	{
		g.setColor(getBackground());
		g.fill3DRect(0, 0, getWidth(), getHeight(), true);
		
		for(VariableCell cell: myCells)
		{
			cell.draw(g);
		}
		
		for(ThreadFrame frame: myFrames)
		{
			//uses negative positions to hide the frame
			if(frame.getX()>=0)
				frame.draw(g);
		}
	}
}
