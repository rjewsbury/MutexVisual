package mutex.editor.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mutex.display.ElementEvent;
import mutex.display.ElementListener;
import mutex.display.elements.ViewElement;
import mutex.editor.control.PropertyEditor;
import mutex.editor.model.EditableElementController;

/**
 * Used to allow users to select and edit view elements.
 * Replaced by the visual code window as it is more flexible.
 */
@Deprecated
public class ClickableDisplay extends JPanel implements ElementListener, ChangeListener
{
	private static final Color SELECTED_COLOR = new Color(77,153,77);
	EditableElementController myController;
	ViewElement mySelected;
	List<ElementListener> listeners;
	
	public ClickableDisplay(){
		this(new EditableElementController());
	}
	public ClickableDisplay(EditableElementController controller)
	{
		this.setBackground(Color.WHITE);
		listeners = new ArrayList<>();
		
		myController = controller;
		
		MouseController mouse = new MouseController();
		this.addMouseListener(mouse);
		this.addMouseMotionListener(mouse);
	}
	
	public void setController(EditableElementController controller)
	{
		myController = controller;
		mySelected = null;
		updateElementListeners();
		repaint();
	}
	public EditableElementController getController()
	{
		return myController;
	}
	
	@Override
	public synchronized void paintComponent(Graphics g)
	{
		g.setColor(getBackground());
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		if(mySelected != null)
		{
			g.setColor(SELECTED_COLOR);
			g.fillRect(mySelected.getX(), mySelected.getY(),
					mySelected.getWidth(), mySelected.getHeight());
		}
		
		//elements at the beginning of the list are drawn on top
		List<ViewElement> list = myController.getElements();
		for(int i = list.size()-1; i>=0; i--)
			list.get(i).draw(g);
	}
	
	public void addElementListener(ElementListener listener)
	{
		listeners.add(listener);
	}
	public void updateElementListeners()
	{
		//events are immutable so we can use the same event for everyone
		ElementEvent event = new ElementEvent(this, mySelected);
		for(ElementListener l: listeners)
			l.elementSelected(event);
	}
	
	@Override
	public void elementSelected(ElementEvent e)
	{
		myController.addElement(e.getElement());
		repaint();
	}
	
	@Override
	public void stateChanged(ChangeEvent e)
	{
		if(e.getSource() instanceof PropertyEditor)
			repaint();
		else
			System.out.println("Unexpected change event from "+e.getSource().getClass().getName());
	}
	
	private class MouseController extends MouseAdapter
	{
		@Override
		public void mousePressed(MouseEvent e)
		{
			//looks for the frontmost element that contains the pressed position
			//selects that element
			mySelected = myController.getElement(e.getX(),e.getY());
			repaint();
		}
	
		@Override
		public void mouseReleased(MouseEvent e)
		{
			//if the mouse is released outside of the window, remove the element
			if(getMousePosition() == null)
			{
				myController.removeElement(mySelected);
				mySelected = null;
				repaint();
			}
			updateElementListeners();
		}
	
		@Override
		public void mouseDragged(MouseEvent e)
		{
			//moves the selected element
			if(mySelected != null)
			{
				mySelected.setCenterX(e.getX());
				mySelected.setCenterY(e.getY());
				repaint();
			}
		}
	}
}
