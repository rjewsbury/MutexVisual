package mutex.editor.control.inputs;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import mutex.editor.model.Field;

public class LabeledObjectField extends LabeledField implements ActionListener
{
	private static final int MAX_HEIGHT = 20;
	private JComboBox<?> myComboBox;
	
	public LabeledObjectField(Field field, Object[] options)
	{
		super(field);
		
		myComboBox = new JComboBox<>(options);
		myComboBox.addActionListener(this);
		myComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, MAX_HEIGHT));
		add(myComboBox);
		
		//should I do some kind of check to make sure that all objects in the list are
		//legal field values? or that the current field value is in the list?
		this.setValue(field.getValue());
		
		this.setMaximumSize(myComboBox.getMaximumSize());
	}

	@Override
	public void setValue(Object value)
	{
		for(int i=0; i<myComboBox.getItemCount(); i++)
		{
			if(myComboBox.getItemAt(i) == value)
			{
				myComboBox.setSelectedIndex(i);
				break;
			}
		}
	}

	@Override
	public Object getValue()
	{
		return myComboBox.getSelectedItem();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		stateChanged();
	}
}
