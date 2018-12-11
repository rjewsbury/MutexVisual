package mutex.display.elements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

import mutex.editor.model.Field;

public class TurnWheel extends ElementContainer
{
	//the ratio of arrow size to wheel size
	public static final double ARROW_RATIO = 1/3.0;
	
	//an array of size N that tracks which threads are in the wheel
	private ViewElement[] myElements;
	private Arrow myArrow;
	private int myRadius;
	private int myThickness;
	private boolean showArrow;
	
	public TurnWheel(int numThreads, int radius)
	{
		myElements = new ThreadBall[numThreads];
		myRadius = radius;
		myThickness = 2;
		showArrow = true;
		
		myArrow = new Arrow();
		myArrow.setCenterX(myRadius);
		myArrow.setCenterY(myRadius);
		myArrow.setRadius((int)(myRadius*ARROW_RATIO));
		myArrow.setThickness(2);
	}
	
	@Override
	public List<Field> getEditableFields()
	{
		List<Field> fields = super.getEditableFields();
		
		fields.add(new Field("Thickness", myThickness, int.class));
		fields.add(new Field("Arrow Size", myArrow.getWidth(), int.class));
		
		return fields;
	}
	@Override
	public boolean setField(Field field)
	{
		boolean valid = false;
		switch(field.getName())
		{
			case "Thickness":
				setThickness((int)field.getValue());
				valid = true;
				break;
			case "Arrow Size":
				myArrow.setWidth((int)field.getValue());
				myArrow.setCenterX(getCenterX());
				myArrow.setCenterY(getCenterY());
				valid = true;
				break;
		}
		if(!valid)
			valid = super.setField(field);
		
		return valid;
	}
	
	public void setWidth(int width)
	{
		myRadius = width/2;
		myArrow.setCenterX(getCenterX());
		myArrow.setCenterY(getCenterY());
	}
	public int getWidth()
	{
		return myRadius*2;
	}
	public void setHeight(int height)
	{
		myRadius = height/2;
		myArrow.setCenterX(getCenterX());
		myArrow.setCenterY(getCenterY());
	}
	public int getHeight()
	{
		return myRadius*2;
	}
	
	public void setTurn(int turn)
	{
		if(0 <= turn && turn < myElements.length)
		{
			myArrow.setRotation(Math.toRadians(360*turn/((float)myElements.length)));
			showArrow = true;
		}
		else
			showArrow = false;
	}
	
	public void setThickness(int thickness)
	{
		myThickness = thickness;
	}
	
	private void positionElement(int index)
	{
		if(myElements[index] == null)
			return;
		
		//calculate and move the thread to a position on the wheel
		double angle = Math.toRadians(360*index/((float)myElements.length));
		myElements[index].setCenterX(getCenterX() + (int)(Math.cos(angle)*myRadius));
		myElements[index].setCenterY(getCenterY() + (int)(Math.sin(angle)*myRadius));
	}
	
	@Override
	public void setX(int x)
	{
		super.setX(x);
		for(int i = 0; i < myElements.length; i++)
			positionElement(i);
		myArrow.setCenterX(getCenterX());
	}
	
	@Override
	public void setY(int y)
	{
		super.setY(y);
		for(int i = 0; i < myElements.length; i++)
			positionElement(i);
		myArrow.setCenterY(getCenterY());
	}
	
	public void scale(int size)
	{
		super.scale(size);
		myArrow.scale((int)(size*ARROW_RATIO));
	}
	
	public void addElement(ViewElement e){
		for(int i=0; i< myElements.length; i++)
			if(myElements[i] == null)
			{
				addElement(e,i);
				return;
			}
		//should an exception be thrown if there's no open space?
	}
	public void addElement(ViewElement e, int pos)
	{
		myElements[pos] = e;
		positionElement(pos);
	}
	
	public ViewElement getElement(int pos)
	{
		return myElements[pos];
	}
	@Override
	public ViewElement removeElement(int index)
	{
		ViewElement e = myElements[index];
		myElements[index] = null;
		return e;
	}
	/**
	 * Stops drawing the element as part of the array
	 * returns the element, or null if it's not found
	 */
	public ViewElement removeElement(ViewElement e)
	{
		for(int i=0; i<myElements.length; i++)
		{
			if(e.equals(myElements[i]))
			{
				e = myElements[i];
				myElements[i] = null;
				return e;
			}
		}
		return null;
	}
	
	public void draw(Graphics2D g)
	{
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(myThickness));
		
		if(hasBorder())
			g.drawOval(getX(), getY(), 2*myRadius, 2*myRadius);
		if(showArrow)
			myArrow.draw(g);
		
		//ensures that threads are drawn on top
		for(ViewElement e: myElements)
			if(e != null)
				e.draw(g);
	}
	@Override
	public int size()
	{
		return myElements.length;
	}

	@Override  
	public void setSize(int size)
	{
		ViewElement[] oldElement = myElements;
		myElements = new ViewElement[size];
		
		for(int i=0; i<oldElement.length && i<myElements.length; i++)
			myElements[i] = oldElement[i];
	}
	
	@Override
	public TurnWheel clone()
	{
		TurnWheel copy = (TurnWheel) super.clone();
		for(int i=0; i<myElements.length; i++)
			if(myElements[i] != null)
				copy.myElements[i] = myElements[i].clone();
		copy.myArrow = myArrow.clone();
		return copy;
	}
}
