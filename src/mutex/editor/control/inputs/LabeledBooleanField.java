package mutex.editor.control.inputs;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JCheckBox;

import mutex.editor.model.Field;

/**
 * This is pretty much just a jcheckbox, but it mirrors the
 * implementation of the other labeled inputs
 */
public class LabeledBooleanField extends LabeledField implements ItemListener
{
	private JCheckBox myCheckBox;
	
	public LabeledBooleanField(Field field)
	{
		super(field);
		
		if(field.getType() != Boolean.class && field.getType() != boolean.class)
			throw new IllegalArgumentException("the given field is not a boolean");
		
		//create components
		myCheckBox = new JCheckBox();
		myCheckBox.addItemListener(this);
		myCheckBox.setSelected((boolean)field.getValue());
		add(myCheckBox);
		
		//forces the panel to stay the "packed" size
		setMinimumSize(getPreferredSize());
		setMaximumSize(new Dimension(Integer.MAX_VALUE,getPreferredSize().height));
		
//		this.setBorder(BorderFactory.createCompoundBorder(
//		BorderFactory.createLineBorder(Color.red),this.getBorder()));
	}
	
	public void setValue(Object val){
		//if the value isn't a boolean, should it do nothing, or throw an exception?
		setBoolean((boolean) val);
	}
	public Object getValue(){
		return getBoolean();
	}
	
	public void setBoolean(boolean val)
	{
		myCheckBox.setSelected(val);
	}
	
	public boolean getBoolean()
	{
		return myCheckBox.isSelected();
	}

	@Override
	public void itemStateChanged(ItemEvent e)
	{
		stateChanged();
	}
}
