package mutex.editor.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import mutex.display.ElementEvent;
import mutex.display.ElementListener;
import mutex.display.elements.*;

/**
 * A window for selecting, dragging, and dropping visual components.
 * Replaced by the visual code window as it is more flexible.
 */
@Deprecated
public class ElementSelector extends JPanel implements ActionListener
{
	//we need some number of threads for variable length containers to use
	private static final int NUM_THREADS = 3;
	private static final int DEFAULT_SIZE = 50;
	//as far as I can tell, the selection of elements
	//has to be hardcoded
	private List<ViewElement> selection;
	private List<ElementListener> listeners;
	private List<JButton> buttons;
	
	public ElementSelector()
	{
		selection = Arrays.asList(
				new CriticalSection(DEFAULT_SIZE),
				new NonCriticalSection(NUM_THREADS, DEFAULT_SIZE),
				new ElementList(NUM_THREADS, DEFAULT_SIZE),
				new RaceQueue(NUM_THREADS, DEFAULT_SIZE),
				new ElementArray(NUM_THREADS, DEFAULT_SIZE),
				new ReadRace(DEFAULT_SIZE),
				new TurnWheel(NUM_THREADS, DEFAULT_SIZE),
				new Arrow(DEFAULT_SIZE));
		
		listeners = new ArrayList<>();
		buttons = new ArrayList<>();
		
		this.addComponentListener(new ResizeListener());
		this.setLayout(new GridLayout(0,2));
		addButtons();
	}
	
	private void addButtons()
	{
		JButton temp;
		int i=0;
		for(ViewElement e: selection)
		{
			temp = new JButton(e.getClass().getSimpleName(),new ElementIcon(e));
			temp.setVerticalTextPosition(SwingConstants.BOTTOM);
			temp.setHorizontalTextPosition(SwingConstants.CENTER);
			//temp.setActionCommand(e.getClass().getSimpleName());
			temp.setActionCommand(""+i);
			temp.addActionListener(this);
			temp.setPreferredSize(new Dimension(100,100));
			temp.setMaximumSize(temp.getPreferredSize());
			
			buttons.add(temp);
			this.add(temp);
			
			i++;
		}
	}
	
	public void addElementListener(ElementListener l)
	{
		listeners.add(l);
	}
	
	private void selectElement(ViewElement element)
	{
		for(ElementListener listener: listeners)
			listener.elementSelected(new ElementEvent(this,element.clone()));
	}
	
	@Override
	public void actionPerformed(ActionEvent ae)
	{
		try{
			int index = Integer.decode(ae.getActionCommand());
			selectElement(selection.get(index));
		}catch(NumberFormatException e){
			throw new AssertionError();
		}
		
//		String name = ae.getActionCommand();
//		ViewElement element = null;
//		for(ViewElement e: selection)
//			if(e.getClass().getSimpleName().equals(name))
//			{
//				element = e;
//				break;
//			}
//		if(element != null)
//			selectElement(element);
	}
	
//------------------------------------------
	private class ResizeListener extends ComponentAdapter
	{
		@Override
		public void componentResized(ComponentEvent e)
		{
			//System.out.println("resize "+e.paramString()+e.getComponent().getName());
			//on resize, change the number of columns and validate?
		}
	}
	
	private class ElementIcon implements Icon
	{
		private static final int SIZE = 60;
		private static final int BUFFER = 5;
		
		private ViewElement myElement;
		
		public ElementIcon(ViewElement e)
		{
			myElement = e.clone();
			myElement.scale(SIZE);
		}
		
		@Override
		public int getIconHeight()
		{
			return SIZE+2*BUFFER;
		}

		@Override
		public int getIconWidth()
		{
			return SIZE+2*BUFFER;
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y)
		{
			myElement.setCenterX(x+BUFFER+SIZE/2);
			myElement.setCenterY(y+BUFFER+SIZE/2);
			myElement.draw(g);
		}
	}
}
