package mutex.editor.control;

import java.util.List;
import java.util.LinkedList;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mutex.display.ElementEvent;
import mutex.display.ElementListener;
import mutex.editor.control.inputs.*;
import mutex.editor.model.Editable;
import mutex.editor.model.Field;

public class PropertyEditor extends JPanel implements ElementListener, ChangeListener
{
	private static final int SPACING = 20;
	private Editable myEditable;
	//the message to display if there is no editable
	private String noEditableMessage;
	private List<LabeledField> myInputs;
	//keeping a list of listeners here makes it easier to control
	private List<ChangeListener> listeners;
	//used to ignore events if the property editor is currently self-modifying
	private boolean isModifying;
	private String mostRecentField;
	
	public PropertyEditor(String message)
	{
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setAlignmentY(TOP_ALIGNMENT);
		
		noEditableMessage = message;
		
		myInputs = new LinkedList<>();
		listeners = new LinkedList<>();
		isModifying = false;
		
		setEditable(null);

//		this.setBorder(BorderFactory.createCompoundBorder(
//				BorderFactory.createLineBorder(Color.red),this.getBorder()));
	}
	
	public String getMostRecentField(){
		return mostRecentField;
	}
	
	public void setEditable(Editable e)
	{
		myEditable = e;
		LabeledField labeledInput;
		this.removeAll();
		myInputs.clear();
		if(e != null)
		{
			List<Field> fields = e.getEditableFields();
			if(fields == null)
				setBlank();
			else
				for(Field field: e.getEditableFields())
				{
					//System.out.println(field.getName());
					if(field.getType().isEnum())
					{
						labeledInput = new LabeledObjectField(
											field,
											field.getType().getEnumConstants());
					}
					else if(field.getType()==Integer.class || field.getType()==int.class)
					{
						labeledInput = new LabeledIntegerField(field);
					}
					else if(field.getType()==Double.class	|| field.getType()==double.class ||
							field.getType()==Float.class	|| field.getType()==float.class)
					{
						labeledInput = new LabeledNumberField(field);
					}
					else if(field.getType()==Boolean.class || field.getType()==boolean.class)
					{
						labeledInput = new LabeledBooleanField(field);
					}
					else if(Editable.class.isAssignableFrom(field.getType())) {
						labeledInput = new LabeledEditableField(field);
					}
					else
					{
						//not sure how to handle things that aren't ints, bools, or strings
						labeledInput = new LabeledTextField(field);
					}
					
					labeledInput.addChangeListener(this);
					this.add(Box.createVerticalStrut(SPACING));
					this.add(labeledInput);
					
					myInputs.add(labeledInput);
				}
		}
		else
		{
			setBlank();
		}

		validate();
		repaint();
	}

	public Editable getEditable() {
		return myEditable;
	}
	
	private void setBlank()
	{
		JLabel label = new JLabel(noEditableMessage);
		label.setAlignmentX(CENTER_ALIGNMENT);
		
		this.add(Box.createGlue());
		this.add(label);
		this.add(Box.createGlue());
	}
	
	private void modifyFields(LabeledField source)
	{
		//to avoid loops, block change events during modification
		isModifying = true;
		for(Field field: myEditable.getEditableFields())
		{
			//skip over the value that was just changed
			if(source.getField().getName().equals(field.getName()))
				continue;
			//should we assume that the fields come in the same order every time?
			//probably safer not to
			for(LabeledField input: myInputs)
			{
				//if the input corresponds to the field...
				if(input.getField().getName().equals(field.getName()))
				{
					//update its value
					input.setValue(field.getValue());
					input.setInputValid(true);
					break;
				}
			}
		}
		isModifying = false;
	}

	@Override
	public void elementSelected(ElementEvent e)
	{
		setEditable(e.getElement());
	}
	
	public void addChangeListener(ChangeListener l)
	{
		//I need a way to notify containers when one of their children has been modified
		//so that they can repaint, or whatever else they need
		listeners.add(l);
	}
	
	private void updateChangeListeners()
	{
		ChangeEvent event = new ChangeEvent(this);
		for(ChangeListener l: listeners)
			l.stateChanged(event);
	}
	

	@Override
	public void stateChanged(ChangeEvent e)
	{
		//ignore the change event if it was caused by the current self-modification
		if(isModifying)
			return;

		LabeledField source = (LabeledField) e.getSource();
		boolean success = myEditable.setField(source.getField());
		mostRecentField = source.getLabel();
		
		//if the field change failed, show that it is invalid
		source.setInputValid(success);
		
		//if the field change succeeded, it's possible that other fields changed too
		if(success)
			modifyFields(source);
		
		updateChangeListeners();
	}
}