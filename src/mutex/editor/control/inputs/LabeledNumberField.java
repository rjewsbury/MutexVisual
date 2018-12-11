package mutex.editor.control.inputs;

import mutex.editor.model.Field;

public class LabeledNumberField extends LabeledTextField
{
	private Number myNumber;
	public LabeledNumberField(Field field)
	{
		super(field);
		
		myNumber = (Number) field.getValue();
	}
	
	public void setNumber(Number num)
	{
		myNumber = num;
		setText(""+num);
	}
	public Number getNumber()
	{
		//returns the last valid number, even if the field is currently invalid
		return myNumber;
	}
	
	public void setValue(Object val)
	{
		setNumber((Number)val);
	}
	public Object getValue()
	{
		return getNumber();
	}
	
	@Override
	protected void stateChanged()
	{
		//if text changed to something invalid, dont bother sending out an event?
		try{
			myNumber = Double.parseDouble(getText());
			setInputValid(true);
			super.stateChanged();
		}catch(NumberFormatException ex){
			setInputValid(false);
		}
	}
}
