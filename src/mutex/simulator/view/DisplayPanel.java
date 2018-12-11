package mutex.simulator.view;

import java.awt.*;

import javax.swing.JPanel;

import mutex.display.ElementController;
import mutex.display.controllers.DefaultDisplay;
import mutex.display.elements.ViewElement;
import mutex.simulator.model.*;

/**
 * Handles display for an ElementController
 *
 * @author Ryley Jewsbury
 */
public class DisplayPanel extends JPanel implements ThreadUpdateListener
{
	public static final int BUFFER = 50;
	
	private ElementController myController;
	
	public DisplayPanel(ElementController display)
	{
		setBackground(Color.WHITE);
		setController(display);
	}
	
	public void setController(ElementController controller)
	{
		if(controller == null)
			controller = new DefaultDisplay(2);

		myController = controller;
		setMinimumSize(calculateDimension());
		setPreferredSize(getMinimumSize());
		repaint();
	}
	
	public ElementController getController()
	{
		return myController;
	}
	
	private Dimension calculateDimension()
	{
		int maxX = 0;
		int maxY = 0;
		int newX = 0;
		int newY = 0;
		for(ViewElement e: myController.getElements())
		{
			newX = e.getX()+e.getWidth()+BUFFER;
			newY = e.getY()+e.getHeight()+BUFFER;
			if(newX > maxX)
				maxX = newX;
			if(newY > maxY)
				maxY = newY;
		}
		
		return new Dimension(maxX, maxY);
	}
	
	public synchronized void update(AlgorithmThread updated)
	{
		myController.update(updated);
		//draws the updated state
		repaint();
	}
	
	@Override
	public synchronized void paintComponent(Graphics g)
	{
		g.setColor(getBackground());
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		for(ViewElement e: myController.getElements()) {
			e.draw(g);
		}
	}
}
