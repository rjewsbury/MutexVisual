package mutex.display;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import mutex.display.controllers.DefaultDisplay;
import mutex.display.controllers.StairDisplay;
import mutex.display.controllers.TreeDisplay;
import mutex.display.controllers.WheelDisplay;
import mutex.display.elements.ViewElement;
import mutex.editor.model.Editable;
import mutex.editor.model.Field;
import mutex.simulator.model.AlgorithmThread;
import mutex.simulator.model.AlgorithmThreadGroup;

public abstract class ElementController implements Editable
{
	public static ElementController getController(DisplayControllerParameters params) {
		ElementController controller = null;

		switch (params.getDisplayType()) {
			case BLANK:
				controller = new DefaultDisplay(2);
				break;
			case TURN_WHEEL:
				controller = new WheelDisplay(2);
				break;
			case STAIRCASE:
				controller = new StairDisplay(2);
				break;
			case TREE:
				controller = new TreeDisplay(2);
				break;
			case CUSTOM:
				controller = new DefaultDisplay(2);
				break;
		}
//		System.out.println(params.getDisplayType());
//		for(Field f: params.getEditableFields())
//			System.out.println(f.getName()+" "+f.getValue());

		if(controller != null)
			for(Field field: params.getEditableFields())
				controller.setField(field);

		return controller;
	}

	public DisplayControllerParameters getParameters() {
		DisplayControllerParameters params = new DisplayControllerParameters();
		params.setEditableFields(new ArrayList<Field>(getEditableFields()));

		//if I ever add more preset display types, I will need to update this
		if(this instanceof DefaultDisplay)
			params.setDisplayType(DisplayType.BLANK);
		else if(this instanceof WheelDisplay)
			params.setDisplayType(DisplayType.TURN_WHEEL);
		else if(this instanceof StairDisplay)
			params.setDisplayType(DisplayType.STAIRCASE);
		else if(this instanceof TreeDisplay)
			params.setDisplayType(DisplayType.TREE);
		else
			params.setDisplayType(DisplayType.CUSTOM);

//		System.out.println(params.getDisplayType());
//		for(Field f: params.getEditableFields())
//			System.out.println(f.getName()+" "+f.getValue());

		return params;
	}

	/**
	 * updates the sizes of elements that are dependent on the number of threads
	 */
	public abstract void setNumThreads(int n);
	
	/*
	 * updates the size of thread ball elements that will be used,
	 * as well as any dependent elements
	 *
	 * implementing resizing has proven to be difficult.
	 */
	//public void setThreadSize(int size);
	
	/**
	 * This method gets called at the beginning of the simulation,
	 * and adds thread elements to the display
	 * 
	 * if the group is a different size than the current expected size,
	 * the current size should be changed to match the group
	 */
	public abstract void addThreads(AlgorithmThreadGroup threads);
	
	/**
	 * This removes all threads from the display
	 */
	public abstract void clearThreads();
	
	/**
	 * This returns the list of elements being controlled
	 */
	public abstract List<ViewElement> getElements();
	
	/**
	 * When a thread changes state, this method tells each element how to update
	 */
	public abstract void update(AlgorithmThread updated);
}
