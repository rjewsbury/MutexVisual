package mutex.display.elements;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import mutex.editor.model.Field;
import mutex.editor.model.Editable;

public abstract class ViewElement implements Cloneable, Editable
{
	private int myX, myY;
	
	public ViewElement()
	{
		myX = 0;
		myY = 0;
	}
	
	public ViewElement(int x, int y)
	{
		myX = x;
		myY = y;
	}
	@Override
	public List<Field> getEditableFields()
	{
		List<Field> fields = new ArrayList<>();
		fields.add(new Field("X", myX, int.class));
		fields.add(new Field("Y", myY, int.class));
		fields.add(new Field("Width", getWidth(), int.class));
		fields.add(new Field("Height", getHeight(), int.class));
		
		return fields;
	}
	@Override
	public boolean setField(Field field)
	{
		boolean valid = false;
		switch(field.getName())
		{
			case "X":
				setX((int)field.getValue());
				valid = true;
				break;
			case "Y":
				setY((int)field.getValue());
				valid = true;
				break;
			case "Width":
				setWidth((int)field.getValue());
				valid = true;
				break;
			case "Height":
				setHeight((int)field.getValue());
				valid = true;
				break;
		}
		return valid;
	}
	
	public void setX(int x){
		myX = x;
	}
	public void setY(int y){
		myY = y;
	}
	public void setCenterX(int x){
		setX(x-getWidth()/2);
	}
	public void setCenterY(int y){
		setY(y-getHeight()/2);
	}
	public void setRightX(int x){
		setX(x-getWidth());
	}
	public void setBottomY(int y){
		setY(y-getHeight());
	}
	
	public int getX(){
		return myX;
	}
	public int getY(){
		return myY;
	}
	public int getCenterX(){
		return myX+getWidth()/2;
	}
	public int getCenterY(){
		return myY+getHeight()/2;
	}
	public int getRightX(){
		return myX+getWidth();
	}
	public int getBottomY(){
		return myY+getHeight();
	}
	
	public boolean contains(int x, int y)
	{
		return getX() < x
			&& x < getX()+getWidth()
			&& getY() < y
			&& y < getY()+getHeight();
	}
	
	public abstract void setWidth(int width);
	public abstract int getWidth();
	public abstract void setHeight(int height);
	public abstract int getHeight();
	public void scale(int size)
	{
		double ratio = (double)getWidth()/getHeight();
		if(ratio > 1)
		{
			setWidth(size);
			setHeight((int)(size/ratio));
		}
		else
		{
			setWidth((int)(size/ratio));
			setHeight(size);
		}
	}
	
	public void draw(Graphics g){
		draw((Graphics2D)g);
	}
	public abstract void draw(Graphics2D g2);
	
	public ViewElement clone()
	{
		try
		{
			return (ViewElement)super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			//*should* be impossible to get here
			throw new AssertionError();
		}
	}
}
