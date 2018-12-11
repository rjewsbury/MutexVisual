package mutex.simulator.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import javax.swing.*;

public class ThreadSelector extends JPanel implements ItemListener, ActionListener {
	private ArrayList<ThreadActivityControl> myBoxes;
	private ArrayList<ItemListener> myItemListeners;
	private ArrayList<ActionListener> myActionListeners;
	
	public ThreadSelector(int numThreads)
	{
		myItemListeners = new ArrayList<>();
		myActionListeners = new ArrayList<>();

		setLayout(new GridLayout(1,0));
		myBoxes = new ArrayList<>(numThreads);
		
		for(int i=0; i<numThreads; i++)
		{
			myBoxes.add(new ThreadActivityControl(i));
			myBoxes.get(i).setSelected(true);
			myBoxes.get(i).addItemListener(this);
			myBoxes.get(i).addActionListener(this);
			add(myBoxes.get(i));
		}

//		this.setBorder(BorderFactory.createCompoundBorder(
//        		BorderFactory.createLineBorder(Color.BLACK),this.getBorder()));
	}

	//changes the number of thread check boxes
	public void updateCount(int count) {
		//before changing the count, reset all boxes to true
		for (ThreadActivityControl box : myBoxes) {
			box.setSelected(true);
		}

		if ( count < myBoxes.size()) {
			//remove boxes
			for(int i = myBoxes.size()-1; i >= count; i--) {
				ThreadActivityControl box = myBoxes.remove(i);
				//enable it, so that the state is consistent if it's re-added later
				box.setSelected(true);
				remove(box);
			}
		}
		else {
			for(int i = myBoxes.size(); i < count; i++) {
				myBoxes.add(new ThreadActivityControl(i));
				myBoxes.get(i).setSelected(true);
				myBoxes.get(i).addItemListener(this);
				myBoxes.get(i).addActionListener(this);
				add(myBoxes.get(i));
			}
		}
		revalidate();
		repaint();
	}

	public void addItemListener(ItemListener listener)
	{
		myItemListeners.add(listener);
	}

	public void addActionListener(ActionListener listener)
	{
		myActionListeners.add(listener);
	}

	@Override
	public void itemStateChanged(ItemEvent itemEvent) {
		for(ItemListener il: myItemListeners)
			il.itemStateChanged(itemEvent);
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		for (ActionListener al : myActionListeners) {
			al.actionPerformed(actionEvent);
		}
	}
}
