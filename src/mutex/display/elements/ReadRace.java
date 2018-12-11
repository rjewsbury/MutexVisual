package mutex.display.elements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class ReadRace extends ElementContainer
{
	private ViewElement myLeftElement, myRightElement;
	private boolean isLeftUp, isRightUp;
	private int mySize;
	
	public ReadRace(int size)
	{
		this(size, false, false);
	}
	
	public ReadRace(int size, boolean left, boolean right)
	{
		myLeftElement = myRightElement = null;
		isLeftUp = left;
		isRightUp = right;
		mySize = size;
	}
	
	public void addElement(ViewElement e){
		if(myLeftElement == null)
			addLeftElement(e);
		else
			addRightElement(e);
		//throws an error if both sides are full?
	}
	public void addElement(ViewElement e, int index){
		if(index % 2 == 1)
			addLeftElement(e);
		else
			addRightElement(e);
	}
	public void addLeftElement(ViewElement e)
	{
		if(myLeftElement != null)
			throw new IllegalArgumentException("A left element already exists");
		
		myLeftElement = e;
		
		e.setCenterX(getX()+mySize/2);
		e.setCenterY(getY()+mySize/2);
	}
	
	public void addRightElement(ViewElement e)
	{
		if(myRightElement != null)
			throw new IllegalArgumentException("A right element already exists");
		
		myRightElement = e;
		
		e.setCenterX(getX()+mySize*5/2);
		e.setCenterY(getY()+mySize/2);
	}
	
	public ViewElement getElement(int index){
		if(index == 0)
			return myLeftElement;
		else if(index == 1)
			return myRightElement;
		else throw new IndexOutOfBoundsException("Read races only have 2 positions");
	}
	
	public ViewElement removeElement(ViewElement e)
	{
		if(myLeftElement == e)
			myLeftElement = null;
		else if(myRightElement == e)
			myRightElement = null;
		else
			return null;
		
		return e;
	}
	
	@Override
	public ViewElement removeElement(int index)
	{
		ViewElement e = null;
		if(index == 0)
		{
			e = myLeftElement;
			myLeftElement = null;
		}
		else if(index == 1)
		{
			e = myRightElement;
			myRightElement = null;
		}
		else throw new IndexOutOfBoundsException();
			
		return e;
	}
	
	public void setLeftState(boolean left)
	{
		isLeftUp = left;
	}
	
	public void setRightState(boolean right)
	{
		isRightUp = right;
	}
	
	public int getWidth()
	{
		return 3*mySize;
	}
	public void setWidth(int width)
	{
		mySize = width/3;
	}
	public int getHeight()
	{
		return mySize;
	}
	public void setHeight(int height)
	{
		mySize = height;
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(2));
		if(hasBorder())
		{
			g.drawRect(getX(), getY(), getWidth(), getHeight());
			g.drawRect(getX()+mySize, getY(), mySize, mySize);
			g.drawLine(getX()+mySize, getY()+mySize/2, getX()+2*mySize, getY()+mySize/2);
			g.drawLine(getX()+mySize*3/2, getY(), getX()+mySize*3/2, getY()+mySize);
		}
		
		if(isLeftUp)
			g.fillOval(getX()+mySize, getY(), mySize/2, mySize/2);
		else
			g.fillOval(getX()+mySize, getY()+mySize/2, mySize/2, mySize/2);
		if(isRightUp)
			g.fillOval(getX()+mySize*3/2, getY(), mySize/2, mySize/2);
		else
			g.fillOval(getX()+mySize*3/2, getY()+mySize/2, mySize/2, mySize/2);
		
		if(myLeftElement != null)
			myLeftElement.draw(g);
		if(myRightElement != null)
			myRightElement.draw(g);
	}

	@Override
	public void setSize(int size){
		//dummy method. read races are always fixed size
	}

	@Override
	public int size(){
		return 2;
	}

	@Override
	public ReadRace clone()
	{
		ReadRace copy = (ReadRace) super.clone();
		if(myLeftElement != null)
			copy.addLeftElement(myLeftElement.clone());
		if(myRightElement != null)
			copy.addRightElement(myRightElement.clone());
		return copy;
	}
}
