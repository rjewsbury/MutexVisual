package mutex.editor.control.inputs;

import mutex.editor.model.Field;

public class LabeledIntegerField extends LabeledTextField
{
	private int myNumber;
	public LabeledIntegerField(Field field)
	{
		super(field);
		
		if(field.getType() != Integer.class && field.getType() != int.class)
			throw new IllegalArgumentException("the given field is not an integer");
		
		myNumber = (int)field.getValue();
	}
	
	public void setInteger(int num)
	{
		myNumber = num;
		setText(""+num);
	}
	
	public int getInteger()
	{
		//returns the last valid number, even if the field is currently invalid
		return myNumber;
	}
	
	public void setValue(Object val)
	{
		setInteger((int)val);
	}
	public Object getValue()
	{
		return getInteger();
	}
	
	@Override
	protected void stateChanged()
	{
		//if text changed to something invalid, dont bother sending out an event?
		try{
			myNumber = Integer.parseInt(getText());
			setInputValid(true);
			super.stateChanged();
		}catch(NumberFormatException ex){
			setInputValid(false);
		}
	}
}
