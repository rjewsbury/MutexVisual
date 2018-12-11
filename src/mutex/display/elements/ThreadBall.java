package mutex.display.elements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class ThreadBall extends ViewElement
{
	//default radius
	public static final int RADIUS = 20;
	
	private int myRadius;
	private Color myColor;
	private int myID;
	private ElementContainer myContainer;
	private boolean myLight;
	
	public ThreadBall(int ID, Color color)
	{
		this(ID, color, RADIUS);
	}
	
	public ThreadBall(int ID, Color color, int radius)
	{
		myRadius = radius;
		myID = ID;
		myColor = color;
		myLight = false;
		myContainer = null;
	}
	
	public void setCenterX(int x){
		setX(x-myRadius);
	}
	public void setCenterY(int y){
		setY(y-myRadius);
	}
	public int getCenterX(){
		return getX()+myRadius;
	}
	public int getCenterY(){
		return getY()+myRadius;
	}
	
	/**
	 * The container controls the threadball's position, so having
	 * multiple controllers can cause problems
	 */
	public void setContainer(ElementContainer container, int index)
	{
		setContainer(container);
		container.addElement(this, index);
	}
	
	public void setContainer(ElementContainer container)
	{
		if(myContainer != container)
			leaveContainer();
		myContainer = container;
	}
	
	public boolean inContainer(ElementContainer container)
	{
		return myContainer == container;
	}
	
	public void leaveContainer()
	{
		if(myContainer != null)
			myContainer.removeElement(this);
		myContainer = null;
	}
	
	public void setRadius(int radius)
	{
		int centerX = getCenterX();
		int centerY = getCenterY();
		
		myRadius = radius;
		
		//by default this would cause the circle to expand from the top left corner,
		//so we reposition it to stay centered
		setCenterX(centerX);
		setCenterY(centerY);
	}
	
	public void setLight(boolean light)
	{
		if(myLight != light)
		{
			if(myLight)
				myColor = myColor.darker();
			else
				myColor = myColor.brighter();
		}
		myLight = light;
	}
	
	public int getID()
	{
		return myID;
	}
	
	public int getWidth()
	{
		return myRadius*2;
	}
	public void setWidth(int width)
	{
		myRadius = width/2;
	}
	public int getHeight()
	{
		return myRadius*2;
	}
	public void setHeight(int height)
	{
		myRadius = height/2;
	}
	
	public void draw(Graphics2D g)
	{
		g.setColor(myColor);
		g.setStroke(new BasicStroke(myRadius/10f));
		
		g.fillOval(getX(), getY(), getWidth(), getHeight());
		g.setColor(Color.BLACK);
		g.drawOval(getX(), getY(), getWidth(), getHeight());
		
		g.setFont(g.getFont().deriveFont((float)myRadius));
		
		//center the ID
		String idString = myID+"";
		int xOffset = (int)(myRadius*(1-idString.length()*0.3));
		int yOffset = (int)(myRadius*1.4);
		g.drawString(idString, getX()+xOffset, getY()+yOffset);
	}

	@Override
	public ThreadBall clone()
	{
		ThreadBall copy = (ThreadBall) super.clone();
		//if the threadball is being copied, wont it have a different container??
		copy.myContainer = null;
		return copy;
	}
}
