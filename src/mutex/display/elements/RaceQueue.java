package mutex.display.elements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class RaceQueue extends ElementList
{
	private int raceID;
	
	public RaceQueue(int numElements, int width)
	{
		super(numElements, width);
		raceID = -1;
	}
	
	public RaceQueue(int numElements, int width, Direction direction)
	{
		super(numElements, width, direction);
		raceID = -1;
	}

	public void setRaceID(int id)
	{
		raceID = id;
		repositionElements();
	}
	
	@Override
	public void repositionElements()
	{
		//KNOWN BUG:
		//if the queue is full, but the raceID is none of the contained elements,
		//adding null to the queue overflows the list. possibly just place a
		//element in the race position if it's full? or just ignore it.
		
		//find the race winner in the queue
		ViewElement raceElement = null;
		
		if(raceID != -1)
			for(int i=0;i<size();i++)
				if(getElement(i) != null && ((ThreadBall)getElement(i)).getID() == raceID)
				{
					raceElement = getElement(i);
					break;
				}
			
		//place the winner in the special first position
		addFirst(raceElement);
		//position the elements
		super.repositionElements();
		//if the race winner wasn't actually in the queue, remove the blank space
		if(raceElement == null)
			removeElement(raceElement);
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		super.draw(g);
		//add a bold box around the race winner position
		int x = getX();
		int y = getY();
		
		if(getDirection() == Direction.LEFT)
			x += getWidth() - getContainerWidth();
		else if(getDirection() == Direction.UP)
			y += getHeight() - getContainerWidth();
		
		g.setStroke(new BasicStroke(3));
		g.setColor(Color.BLACK);
		g.drawRect(x, y, getContainerWidth(), getContainerWidth());
	}
	
	@Override
	public RaceQueue clone()
	{
		return (RaceQueue) super.clone();
	}
}
