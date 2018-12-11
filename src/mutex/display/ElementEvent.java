package mutex.display;

import mutex.display.elements.ViewElement;

public class ElementEvent
{
	private Object mySource;
	private ViewElement myElement;
	
	public ElementEvent(Object source, ViewElement element)
	{
		mySource = source;
		myElement = element;
	}
	
	public Object getSource()
	{
		return mySource;
	}
	
	public ViewElement getElement()
	{
		return myElement;
	}
}
