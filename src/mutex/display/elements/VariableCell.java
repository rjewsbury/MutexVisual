package mutex.display.elements;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import mutex.EditorMain;
import mutex.simulator.model.Value;

/**
 * Displays a single variable. allows threads to show which variable is being read.
 * Uses a generic variable, which is probably bad.
 *
 * @author Ryley Jewsbury
 */
public class VariableCell extends ViewElement
{
	public static final Font FONT = EditorMain.GLOBAL_FONT;
	
	private Value<?> myValue;
	private String myName;
	private int myWidth;
	private int myHeight;
	
	public VariableCell(String name, Value<?> v)
	{
		myName = name;
		myValue = v;
		setWidth(10);
		setHeight(10);
	}
	
	public Value<?> getValue()
	{
		return myValue;
	}
	
	public void setWidth(int width)
	{
		myWidth = width;
	}
	public int getWidth()
	{
		return myWidth;
	}
	public void setHeight(int height)
	{
		myHeight = height;
	}
	public int getHeight()
	{
		return myHeight;
	}

	@Override
	public void draw(Graphics2D g)
	{
		g.setColor(Color.WHITE);
		g.fillRect(getX(), getY(), myWidth, myHeight);
		
		g.setColor(Color.BLACK);
		g.setFont(FONT);
		g.drawRect(getX(), getY(), myWidth, myHeight);
		
		String varString = myValue.toString();
		FontMetrics metrics = g.getFontMetrics();
		
		int x = getX() + (myWidth - metrics.stringWidth(varString))/2;
		int y = getY() + (myHeight - metrics.getHeight())/2 + metrics.getAscent();
		
		g.drawString(varString, x, y);
		if(myName != null)
			g.drawString(myName, getX(), getY());
	}

	@Override
	public VariableCell clone()
	{
		//follows the same variable object
		return (VariableCell) super.clone();
	}
}
