package mutex.display.elements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.List;

import mutex.editor.model.Field;

public class ElementArray extends ElementContainer
{
	private ViewElement[] myElements;
	private int myCellWidth;
	private Direction myDirection;
	
	private String myName;
	private Color myNameColor;
	private float myFontSize;
	
	public ElementArray(int length, int boxWidth, Direction direction)
	{
		myElements = new ViewElement[length];
		
		myCellWidth = boxWidth;
		setDirection(direction);
		
		setBorder(true);
		
		setName("",Color.BLACK,12f);
	}
	
	public ElementArray(int numElements, int size)
	{
		this(numElements, size, Direction.RIGHT);
	}
	
	public ElementArray(int numElements)
	{
		this(numElements, 50, Direction.RIGHT);
	}
	
	@Override
	public List<Field> getEditableFields()
	{
		List<Field> fields = super.getEditableFields();
		
		fields.add(new Field("Direction", getDirection(), Direction.class));
		fields.add(new Field("Label", myName, String.class));
		//getting a color string isnt very easy
		String color = String.format("%02x%02x%02x",
				myNameColor.getRed(),
				myNameColor.getGreen(),
				myNameColor.getBlue());
		fields.add(new Field("Color", color, String.class));
		fields.add(new Field("Label Size", myFontSize, float.class));
		
		return fields;
	}
	@Override
	public boolean setField(Field field)
	{
		boolean valid = false;
		switch(field.getName())
		{
			case "Direction":
				setDirection((Direction)field.getValue());
				valid = true;
				break;
			case "Label":
				myName = field.getValue().toString();
				valid = true;
				break;
			case "Color":
				try{
					myNameColor = Color.decode("0x"+field.getValue().toString());
					valid = true;
				}catch(NumberFormatException e){}
				break;
			case "Label Size":
				myFontSize = (float)field.getValue();
				valid = true;
				break;
		}
		if(!valid)
			valid = super.setField(field);
		
		return valid;
	}
	
	private void positionElement(int pos)
	{
		if(myElements[pos] == null)
			return;
		
		int x = getX()+myCellWidth/2;
		int y = getY()+myCellWidth/2;
		
		switch(myDirection)
		{
			case RIGHT:
				x += pos*myCellWidth;
				break;
			case DOWN:
				y += pos*myCellWidth;
				break;
			case LEFT:
				x += (myElements.length-1-pos)*myCellWidth;
				break;
			case UP:
				y += (myElements.length-1-pos)*myCellWidth;
				break;
		}
		myElements[pos].setCenterX(x);
		myElements[pos].setCenterY(y);
	}
	
	@Override
	public void setX(int x)
	{
		super.setX(x);
		for(int i = 0; i < myElements.length; i++)
			positionElement(i);
	}
	
	@Override
	public void setY(int y)
	{
		super.setY(y);
		for(int i = 0; i < myElements.length; i++)
			positionElement(i);
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
	
	/**
	 * Scales the boxes, keeping the top left corner fixed
	 */
	public void setCellWidth(int size)
	{
		myCellWidth = size;
		for(int i = 0; i < myElements.length; i++)
			positionElement(i);
	}
	public int getCellWidth()
	{
		return myCellWidth;
	}
	
	@Override
	public void setWidth(int width)
	{
		switch(myDirection)
		{
			case UP:
			case DOWN:
				setCellWidth(width);
				break;
			case LEFT:
			case RIGHT:
				setCellWidth(width / myElements.length);
				break;
			default:
				setCellWidth(width);
				break;
		}
	}
	public int getWidth()
	{
		switch(myDirection)
		{
			case UP:
			case DOWN:
				return myCellWidth;
			case LEFT:
			case RIGHT:
				return myCellWidth * myElements.length;
			default:
				return 0;
		}
	}
	@Override
	public void setHeight(int height)
	{
		switch(myDirection)
		{
			case UP:
			case DOWN:
				setCellWidth(height / myElements.length);
				break;
			case LEFT:
			case RIGHT:
				setCellWidth(height);
				break;
			default:
				setCellWidth(height);
				break;
		}
	}
	public int getHeight()
	{
		switch(myDirection)
		{
			case UP:
			case DOWN:
				return myCellWidth * myElements.length;
			case LEFT:
			case RIGHT:
				return myCellWidth;
			default:
				return 0;
		}
	}
	
	public void setDirection(Direction direction)
	{
		myDirection = direction;
	}
	public Direction getDirection()
	{
		return myDirection;
	}
	
	public void setName(String name, Color color, float fontSize)
	{
		if(name == null || color == null)
			throw new NullPointerException("QueueBox names/colors cannot be set to null");
		if(fontSize < 1)
			throw new IllegalArgumentException("Illegal font size");
		
		myName = name;
		myNameColor = color;
		myFontSize = fontSize;
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		int dx = 0;
		int dy = 0;
		switch(myDirection)
		{
			case UP:
			case DOWN:
				dy = myCellWidth;
				break;
			case LEFT:
			case RIGHT:
				dx = myCellWidth;
		}
		
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(1));
		if(hasBorder())
			for(int i=0; i<myElements.length; i++)
				g.drawRect(getX()+dx*i, getY()+dy*i, myCellWidth, myCellWidth);
		
		g.setColor(myNameColor);
		
		Font oldFont = g.getFont();
		g.setFont(g.getFont().deriveFont(myFontSize));
		//Currently draws the name ABOVE the box.
		//use g.drawString(myName, getX()+1, getY()+myFontSize);
		//to draw inside, but this is covered by the elements
		g.drawString(myName, getX(), getY());
		g.setFont(oldFont);
		
		for(ViewElement e: myElements)
			if(e != null)
				e.draw(g);
	}

	@Override
	public ElementArray clone()
	{
		ElementArray copy = (ElementArray)super.clone();
		for(int i=0; i<myElements.length; i++)
			if(myElements[i] != null)
				copy.myElements[i] = myElements[i].clone();
		return copy;
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
}
