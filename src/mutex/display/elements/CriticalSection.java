package mutex.display.elements;

import java.awt.Color;

public class CriticalSection extends ElementList
{
	public CriticalSection(int width)
	{
		//adds 10% so it's possible to see if multiple threads get in
		this(width, (int)(width*1.1));
	}
	
	public CriticalSection(int width, int length)
	{
		super(1, width, length, Direction.RIGHT);
		this.setName("CS", Color.RED, width/4f);
	}
	
	@Override
	public CriticalSection clone()
	{
		return (CriticalSection)super.clone();
	}
}
