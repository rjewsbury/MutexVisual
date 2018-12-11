package mutex.editor.control.inputs;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mutex.EditorMain;
import mutex.editor.model.Field;

/**
 * Not sure if this is a good solution for grouping inputs,
 * it was kind of an afterthought.
 */
public abstract class LabeledField extends JPanel
{
	public static final int SPACING = 20;
	
	private JLabel myLabel;
	private List<ChangeListener> myListeners;
	private boolean isValid;
	
	//needs to store a field to remember class type information
	private Field myField;
	
	public LabeledField(Field initial)
	{
		myListeners = new ArrayList<>();
		myField = initial;
		isValid = false;
		
		//create components
		myLabel = new JLabel(initial.getName());
		myLabel.setFont(EditorMain.GLOBAL_FONT);
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(myLabel);
		add(Box.createHorizontalStrut(SPACING));
	}
	
	public String getLabel()
	{
		return myLabel.getText();
	}
	public void setLabel(String label)
	{
		myLabel.setText(label);
	}
	
	public Field getField()
	{
		//if the field was in an invalid state, return it to a valid state
		myField.setValue(getValue());
		return myField;
	}
	
	public abstract void setValue(Object value);
	public abstract Object getValue();
	
	/**
	 * if it's possible for the user to enter illegal input with
	 * whichever concrete implementation is used, it should override
	 * this method to change the component in some way
	 */
	public void setInputValid(boolean valid)
	{
		isValid = valid;
	}
	public boolean isInputValid()
	{
		return isValid;
	}
	
	public void addChangeListener(ChangeListener listener)
	{
		myListeners.add(listener);
	}
	
	protected void stateChanged()
	{
		for(ChangeListener l: myListeners)
			l.stateChanged(new ChangeEvent(this));
	}
}
