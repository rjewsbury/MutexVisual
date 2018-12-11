package mutex.display.elements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.List;

import mutex.editor.model.Field;

public class ElementList extends ElementContainer
{
	private LinkedList<ViewElement> myElements;
	private int myContainerWidth;
	private int myContainerLength;
	private Direction myDirection;
	private int myMaxElements;
	
	private String myName;
	private Color myNameColor;
	private float myFontSize;
	
	public ElementList(int numElements, int width)
	{
		this(numElements, width, numElements*width, Direction.RIGHT);
	}
	
	public ElementList(int numElements, int width, int length)
	{
		this(numElements, width, length, Direction.RIGHT);
	}
	
	public ElementList(int numElements, int width, Direction dir)
	{
		this(numElements, width, numElements*width, dir);
	}
	
	public ElementList(int numElements, int width, int length, Direction dir)
	{
		myElements = new LinkedList<>();
		setBorder(true);
		setSize(numElements);
		setContainerWidth(width);
		setContainerLength(length);
		setDirection(dir);
		setName("",Color.BLACK,12f);
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
	
	@Override
	public void addElement(ViewElement e, int index)
	{
		//if the element is already in the queue, move it
		myElements.remove(e);
		
		if(myElements.size() >= myMaxElements)
		{
			//not sure how I want to handle this
			//System.err.println("Too many elements are in this display queue");
			//it's a list, so should I even prevent extra elements from entering?
			//return;
		}
		
		myElements.addLast(e);
	}
	public void addElement(ViewElement e){
		addLast(e);
	}
	/**
	 * Adds a given element to the queue
	 * @param e the element to be shown
	 */
	public void addLast(ViewElement e)
	{
		//if the element is already in the queue, move it to the end
		myElements.remove(e);
		
		if(myElements.size() >= myMaxElements)
		{
			//not sure how I want to handle this
			//System.err.println("Too many elements are in this display queue");
			//it's a list, so should I even prevent extra elements from entering?
			//return;
		}
		
		myElements.addLast(e);
	}
	
	/**
	 * Adds a given Element to the queue
	 * @param e the element to be shown
	 */
	public void addFirst(ViewElement e)
	{
		//if the element is already in the queue, move it to the front
		myElements.remove(e);
		
		if(myElements.size() == myMaxElements)
		{
			//not sure how I want to handle this
			//System.err.println("Too many elements are in this display list");
			//it's a list, so should I even prevent extra elements from entering?
			//return;
			//throw new IndexOutOfBoundsException("The queue is already full");
		}
		
		myElements.addFirst(e);
	}
	
	public ViewElement getElement(int index)
	{
		if(index < myElements.size())
			return myElements.get(index);
		else
			return null;
	}
	
	/**
	 * Stops drawing the element as part of the queue
	 */
	public ViewElement removeElement(ViewElement e)
	{
		if(myElements.remove(e))
			return e;
		else
			return null;
	}

	@Override
	public ViewElement removeElement(int index)
	{
		return null;
	}
	
	/**
	 * changes the list's max number of elements, keeping the top left corner fixed
	 */
	public void setSize(int size)
	{
		myMaxElements = size;
		while(myElements.size() > myMaxElements)
			myElements.removeLast();
		
		//change the visual size of the container?
		//have the option to choose between fixed size and variable size?
	}
	public int size()
	{
		return myMaxElements;
	}
	
	/**
	 * Scales the list length, keeping the top left corner fixed
	 */
	public void setContainerLength(int length)
	{
		myContainerLength = length;
	}
	public int getContainerLength()
	{
		return myContainerLength;
	}
	/**
	 * Scales the list width, perpendicular to the axis of the list
	 */
	public void setContainerWidth(int width)
	{
		myContainerWidth = width;
	}
	public int getContainerWidth()
	{
		return myContainerWidth;
	}
	public void setWidth(int width)
	{
		switch(myDirection)
		{
			case UP:
			case DOWN:
				setContainerWidth(width);
				break;
			case LEFT:
			case RIGHT:
				setContainerLength(width);
				break;
			default:
				setContainerWidth(width);
		}
	}
	public int getWidth()
	{
		switch(myDirection)
		{
			case UP:
			case DOWN:
				return myContainerWidth;
			case LEFT:
			case RIGHT:
				return myContainerLength;
			default:
				return 0;
		}
	}
	public void setHeight(int height)
	{
		switch(myDirection)
		{
			case UP:
			case DOWN:
				setContainerLength(height);
				break;
			case LEFT:
			case RIGHT:
				setContainerWidth(height);
				break;
			default:
				setContainerWidth(height);
		}
	}
	public int getHeight()
	{
		switch(myDirection)
		{
			case UP:
			case DOWN:
				return myContainerLength;
			case LEFT:
			case RIGHT:
				return myContainerWidth;
			default:
				return 0;
		}
	}
	
	public void setDirection(Direction direction)
	{
		if(direction != null)
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
	
	public void repositionElements()
	{
		int baseX = getX() + myContainerWidth/2;
		int baseY = getY() + myContainerWidth/2;
		int dx = 0;
		int dy = 0;
		
		switch(myDirection)
		{
			case UP:
				baseY += myContainerLength - myContainerWidth;
				dy = -myContainerWidth;
				break;
			case DOWN:
				dy = myContainerWidth;
				break;
			case LEFT:
				baseX += myContainerLength - myContainerWidth;
				dx = -myContainerWidth;
				break;
			case RIGHT:
				dx = myContainerWidth;
				break;
		}
		
		if(Math.abs(dx*(myElements.size()-1))> myContainerLength - myContainerWidth)
			dx = (int)(Math.signum(dx)*(myContainerLength-Math.abs(dx))/(myElements.size()-1));
		if(Math.abs(dy*(myElements.size()-1))> myContainerLength - myContainerWidth)
			dy = (int)(Math.signum(dy)*(myContainerLength-Math.abs(dy))/(myElements.size()-1));
		
		for(ViewElement e: myElements)
		{
			if(e != null)
			{
				e.setCenterX(baseX);
				e.setCenterY(baseY);
			}
			baseX += dx;
			baseY += dy;
		}
	}
	
	/**
	 * elements get repositioned every time they are drawn, because there's
	 * no way to know which position they are in the list beforehand,
	 * as other elements may have been added or removed
	 */
	@Override
	public void draw(Graphics2D g)
	{
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(1));
		if(hasBorder())
			g.drawRect(getX(), getY(), getWidth(), getHeight());
		
		g.setColor(myNameColor);
		Font oldFont = g.getFont();
		g.setFont(g.getFont().deriveFont(myFontSize));
		//Currently draws the name ABOVE the queue.
		//use g.drawString(myName, getX()+1, getY()+myFontSize);
		//to draw inside, but this is covered by the elements
		g.drawString(myName, getX(), getY());
		g.setFont(oldFont);
		
		repositionElements();
		
		for(ViewElement e: myElements)
			e.draw(g);
	}

	@Override
	public ElementList clone()
	{
		ElementList copy = (ElementList)super.clone();
		copy.myElements = new LinkedList<>();
		for(ViewElement e: myElements)
			if(e != null)
				copy.addLast(e.clone());
			else
				copy.addLast(null);
		return copy;
	}
}