package mutex.display.elements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

import mutex.editor.model.Field;

public class Arrow extends ViewElement
{
	//the ratio of head size to arrow length
	private static final double RATIO = 1;
	
	private int myRadius;
	private double myRotation;
	private int myThickness;
	
	public Arrow()
	{
		this(10,0,1);
	}
	public Arrow(int size)
	{
		this(size,0,1);
	}
	public Arrow(int size, double rotation, int thickness)
	{
		myRadius = size;
		myRotation = rotation;
		myThickness = thickness;
	}
	
	@Override
	public List<Field> getEditableFields()
	{
		List<Field> fields = super.getEditableFields();
		
		fields.add(new Field("Rotation", getRotation(), double.class));
		fields.add(new Field("Thickness", myThickness, int.class));
		
		return fields;
	}
	@Override
	public boolean setField(Field field)
	{
		boolean valid = false;
		switch(field.getName())
		{
			case "Rotation":
				setRotation((double)field.getValue());
				valid = true;
				break;
			case "Thickness":
				myThickness = (int)field.getValue();
				valid = true;
				break;
		}
		if(!valid)
			valid = super.setField(field);
		
		return valid;
	}
	
	public void setRadius(int radius)
	{
		int centerX = getCenterX();
		int centerY = getCenterY();
		
		myRadius = radius;
		
		//by default this would cause the arrow to expand from the top left corner,
		//so we reposition it to stay centered
		setCenterX(centerX);
		setCenterY(centerY);
	}
	
	public void setRotation(double angle)
	{
		myRotation = angle;
	}
	
	public double getRotation()
	{
		return myRotation;
	}
	
	public void setThickness(int thickness)
	{
		myThickness = thickness;
	}
	
	public void setCenterX(int x)
	{
		setX(x-myRadius);
	}
	
	public int getCenterX()
	{
		return getX()+myRadius;
	}
	
	public void setCenterY(int y)
	{
		setY(y-myRadius);
	}
	
	public int getCenterY()
	{
		return getY()+myRadius;
	}
	
	@Override
	public void setWidth(int width)
	{
		setRadius(width/2);
	}
	public int getWidth()
	{
		return myRadius*2;
	}
	@Override
	public void setHeight(int height)
	{
		setRadius(height/2);
	}
	public int getHeight()
	{
		return myRadius*2;
	}
	
	@Override
	public void draw(Graphics2D g)
	{
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(myThickness));
		
		int headX, headY, tailX, tailY, leftX, leftY, rightX, rightY;
		//main line
		headX = getCenterX() + (int)(Math.cos(myRotation)*myRadius);
		headY = getCenterY() + (int)(Math.sin(myRotation)*myRadius);
		tailX = getCenterX() - (int)(Math.cos(myRotation)*myRadius);
		tailY = getCenterY() - (int)(Math.sin(myRotation)*myRadius);
		
		//arrow head left
		leftX = headX - (int)(Math.cos(myRotation + Math.PI/6) * myRadius * RATIO);
		leftY = headY - (int)(Math.sin(myRotation + Math.PI/6) * myRadius * RATIO);
		
		//arrow head right
		rightX = headX - (int)(Math.cos(myRotation - Math.PI/6) * myRadius * RATIO);
		rightY = headY - (int)(Math.sin(myRotation - Math.PI/6) * myRadius * RATIO);
		
		// ----
		g.drawLine(headX, headY, tailX, tailY);
		g.fillPolygon(new int[]{headX, leftX, rightX}, new int[]{headY, leftY, rightY}, 3);
	}

	@Override
	public Arrow clone()
	{
		return (Arrow)super.clone();
	}
}
