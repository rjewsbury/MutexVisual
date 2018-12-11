package mutex.display.elements;

import java.util.List;

import mutex.editor.model.Field;

public abstract class ElementContainer extends ViewElement
{
	private boolean showBorder;
	
	public ElementContainer()
	{
		showBorder = true;
	}
	
	@Override
	public List<Field> getEditableFields()
	{
		List<Field> fields = super.getEditableFields();
		
		fields.add(new Field("Border",showBorder,Boolean.class));
		fields.add(new Field("Elements", size(), int.class));

		return fields;
	}
	@Override
	public boolean setField(Field field)
	{
		boolean valid = false;
		switch(field.getName())
		{
			case "Border":
				setBorder((boolean)field.getValue());
				valid = true;
				break;
			case "Elements":
				setSize((int)field.getValue());
				valid = (size() == (int)field.getValue());
				break;
		}
		if(valid)
			return true;
		else
			return super.setField(field);
	}
	
	//concrete implementations are responsible for storing elements
	//in whatever datatype they need
	public abstract void addElement(ViewElement e);
	public abstract void addElement(ViewElement e, int index);
	public abstract ViewElement getElement(int index);
	public abstract ViewElement removeElement(ViewElement e);
	public abstract ViewElement removeElement(int index);
	
	
	public void setBorder(boolean show){
		showBorder = show;
	}
	public boolean hasBorder(){
		return showBorder;
	}
	
	//because each implementation has their own storage,
	//we dont know how to change its size in the abstract sense
	public abstract void setSize(int size);
	public abstract int size();
}
