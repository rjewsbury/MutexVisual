package mutex.editor.model;

import java.util.LinkedList;
import java.util.List;

import mutex.display.ElementController;
import mutex.display.elements.ElementContainer;
import mutex.display.elements.ThreadBall;
import mutex.display.elements.ViewElement;
import mutex.simulator.model.AlgorithmThread;
import mutex.simulator.model.AlgorithmThreadGroup;

/**
 * A partially finished element controller compatible with the drag and drop window.
 */
@Deprecated
public class EditableElementController extends ElementController
{
	private ThreadBall[] myThreads;
	private int myThreadSize;
	private LinkedList<ViewElement> myElements;
	
	public EditableElementController()
	{
		myElements = new LinkedList<>();
		myThreadSize = ThreadBall.RADIUS;
	}
	
	public void addElement(ViewElement e){
		//adds the element to the front
		myElements.addFirst(e);
	}
	public void removeElement(ViewElement e){
		myElements.remove(e);
	}
	
	public ViewElement getElement(int x, int y)
	{
		for(ViewElement e: myElements)
			if(e.contains(x, y))
				return e;
		return null;
	}

	@Override
	public void setNumThreads(int n)
	{
		clearThreads();
		for(ViewElement element: myElements)
		{
			if(element instanceof ElementContainer)
				;//somehow update sizes only for containers that care about n?
		}
	}

	@Override
	public void addThreads(AlgorithmThreadGroup threads)
	{
		setNumThreads(threads.size());
		
		myThreads = new ThreadBall[threads.size()];
		
		for(int i=0; i<threads.size(); i++)
		{
			myThreads[i] = new ThreadBall(
					threads.getThread(i).getID(),
					threads.getThread(i).getPreferredColor());
		}
		
		for(int i=0; i<threads.size(); i++)
			update(threads.getThread(i));
	}

	@Override
	public void clearThreads()
	{
		if(myThreads == null)
			return;
		
		for(ThreadBall ball: myThreads)
		{
			ball.leaveContainer();
		}
		myThreads = null;
	}

	@Override
	public List<ViewElement> getElements()
	{
		return myElements;
	}

	@Override
	public void update(AlgorithmThread updated)
	{
		//figure out how to actually control elements?
	}

	@Override
	public List<Field> getEditableFields()
	{
		return null;
	}

	@Override
	public boolean setField(Field fields)
	{
		return false;
	}

}
