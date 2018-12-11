package mutex.simulator.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;

import mutex.EditorMain;

public class RunMenu extends JPanel implements ActionListener
{
	public static final String RUN_MESSAGE = " Run ";
	public static final String STOP_MESSAGE = "Stop ";
	public static final String STEP_MESSAGE = "Step ";

	public static final int SPACING = 20;

	private ArrayList<ActionListener> myActionListeners;
	private JButton myRunButton;
	private JButton myStepButton;
	private ResetButton myResetButton;
	
	public RunMenu(int initialNumber)
	{
		myActionListeners = new ArrayList<>();

		//create components
		myRunButton = new JButton(RUN_MESSAGE);
		myRunButton.setFont(EditorMain.GLOBAL_FONT);
		myStepButton = new JButton(STEP_MESSAGE);
		myStepButton.setFont(EditorMain.GLOBAL_FONT);
		myResetButton = new ResetButton(initialNumber);

		//add listeners
		myRunButton.addActionListener(this);
		myStepButton.addActionListener(this);
		myResetButton.addActionListener(this);
		
		//put the components together
		setLayout(new FlowLayout());
		add(myRunButton);
		add(Box.createHorizontalStrut(SPACING));
		add(myStepButton);
		add(Box.createHorizontalStrut(SPACING));
		add(myResetButton);

		setMinimumSize(getPreferredSize());
		setMaximumSize(getPreferredSize());

//		myResetButton.setBorder(BorderFactory.createCompoundBorder(
//        		BorderFactory.createLineBorder(Color.red),myResetButton.getBorder()));
	}

	public void reset() {
		myResetButton.reset();
	}
	
	public void addActionListener(ActionListener listener)
	{
		myActionListeners.add(listener);
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		switch(e.getActionCommand())
		{
			case RUN_MESSAGE:
				myRunButton.setText(STOP_MESSAGE);
				break;
			case STEP_MESSAGE:
				//the ID -1 means that no specific thread should be stepped
				e = new ActionEvent(e.getSource(), -1, e.getActionCommand(), e.getWhen(), e.getModifiers());
				break;
			case STOP_MESSAGE:
			case ResetButton.RESET_MESSAGE:
				myRunButton.setText(RUN_MESSAGE);
				break;
		}

		for (ActionListener al : myActionListeners) {
			al.actionPerformed(e);
		}
	}
}
