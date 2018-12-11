package mutex.editor.control;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import mutex.display.DisplayControllerParameters;
import mutex.display.DisplayType;
import mutex.display.ElementController;
import mutex.display.controllers.DefaultDisplay;
import mutex.editor.model.Editable;
import mutex.editor.model.Field;

public class ControlParameters implements Serializable, Editable
{
	// to avoid InvalidClassExceptions, while allowing me to add new parameters later,
	// the version ID stays constant
	//(note to self: fields can be added/removed, but types can't be changed)
	public static final long serialVersionUID = 1L;
	
	private int myNumThreads;
	private DisplayType myDisplayType;
	private ElementController myController;
	
	public ControlParameters()
	{
		setSafeDefaults();
	}
	
	private void setSafeDefaults()
	{
		setNumThreads(2);
		setDisplayType(DisplayType.BLANK);
		setController(new DefaultDisplay(2));
	}
	
	public void setNumThreads(int threads)
	{
		if(threads > 1)
			myNumThreads = threads;
	}
	
	public int getNumThreads()
	{
		return myNumThreads;
	}
	
	public void setDisplayType(DisplayType type)
	{
		if(type != null)
			myDisplayType = type;
	}
	
	public DisplayType getDisplayType()
	{
		return myDisplayType;
	}

	public void setController(ElementController controller) {
		myController = controller;
	}

	public ElementController getController() {
		return myController;
	}

	@Override
	public List<Field> getEditableFields()
	{
		List<Field> fields = new ArrayList<>();
		fields.add(new Field("Threads", getNumThreads(), int.class));
		fields.add(new Field("Display Type", getDisplayType(), DisplayType.class));
		fields.add(new Field("Variables", getController(), ElementController.class));
		return fields;
	}

	@Override
	public boolean setField(Field field)
	{
		boolean success = false;
		switch(field.getName())
		{
			case "Threads":
				setNumThreads((int)field.getValue());
				success = (getNumThreads() == (int)field.getValue());
				break;
			case "Display Type":
				setDisplayType((DisplayType)field.getValue());
				success = (getDisplayType() == field.getValue());
				break;
			case "Variables":
				setController((ElementController)field.getValue());
				success = (getController() == field.getValue());
		}
		return success;
	}

	//serializable methods
	//instead of saving the element controller, saves just its editable fields
	private void writeObject(java.io.ObjectOutputStream out)
			throws IOException {
		out.writeInt(myNumThreads);
		out.writeObject(myDisplayType);
		if(myDisplayType != DisplayType.CUSTOM)
			out.writeObject(myController.getParameters());
	}
	private void readObject(java.io.ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		myNumThreads = in.readInt();
		myDisplayType = (DisplayType)in.readObject();
		if(myDisplayType != DisplayType.CUSTOM)
			myController = ElementController.getController((DisplayControllerParameters)in.readObject());
	}
	private void readObjectNoData()
			throws ObjectStreamException {
		setSafeDefaults();
	}
}
