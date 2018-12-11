package mutex.display.elements;

import java.awt.Color;
import java.awt.Graphics2D;

public class ThreadFrame extends ViewElement
{
	public static final int TAG_WIDTH = 20;
	public static final int TAG_HEIGHT = 10;
	
	private int myID;
	private Color myColor;
	private int myWidth, myHeight;
	private int myTagOffset;
	
	public ThreadFrame(int ID, Color color)
	{
		myID = ID;
		myColor = color;
		myWidth = 0;
		myHeight = 15;
		myTagOffset = 0;
	}
	
	public void setWidth(int width)
	{
		myWidth = width;
	}
	
	public void setHeight(int height)
	{
		myHeight = height;
	}
	
	public int getWidth()
	{
		return myWidth;
	}
	
	public int getHeight()
	{
		return myHeight;
	}
	
	public void setTagOffset(int offset)
	{
		myTagOffset = offset;
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		int tagX = getX()+myTagOffset;
		
		g.setColor(myColor);
		g.drawRect(getX(), getY(), myWidth, myHeight);
		g.fillRect(tagX, getY()-TAG_HEIGHT, TAG_WIDTH, TAG_HEIGHT);
		
		g.setColor(Color.BLACK);
		g.setFont(g.getFont().deriveFont((float)TAG_HEIGHT));
		g.drawString(""+myID, tagX, getY());
	}

	@Override
	public ThreadFrame clone()
	{
		return (ThreadFrame) super.clone();
	}
}
