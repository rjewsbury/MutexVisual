package mutex.simulator.control;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class ControlPanel extends JPanel implements ActionListener {
	ThreadSelector mySelector;
	DelaySlider myDelaySlider;
	RunMenu myRunMenu;
	
	//potentially refactor so it takes any proper listeners, instead of a model?
	//would reduce coupling, and we only really care about the listener part anyway
	public ControlPanel(int numThreads)
	{
		//create components
		mySelector = new ThreadSelector(numThreads);
		JScrollPane scroll = new JScrollPane(mySelector);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		//forces there to always be room for a scroll bar
		scroll.setPreferredSize(new Dimension(0, 70));
		
		myDelaySlider = new DelaySlider();
		myRunMenu = new RunMenu(numThreads);
		myRunMenu.addActionListener(this);
		
		//place components
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(Box.createVerticalStrut(10));
		add(scroll);
		add(myDelaySlider);
		add(myRunMenu);
		add(Box.createVerticalStrut(10));
	}

	public RunMenu getRunMenu() {
		return myRunMenu;
	}

	public void reset() {
		myRunMenu.reset();
	}
	
	public void addControlListener(ControlListener listener)
	{
		mySelector.addItemListener(listener);
		mySelector.addActionListener(listener);
		myDelaySlider.addChangeListener(listener);
		myRunMenu.addActionListener(listener);
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		if(actionEvent.getActionCommand().equals(ResetButton.RESET_MESSAGE)) {
			mySelector.updateCount(actionEvent.getID());
		}
	}
}
