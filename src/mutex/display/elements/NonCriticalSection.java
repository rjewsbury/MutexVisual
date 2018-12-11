package mutex.display.elements;

import java.awt.Color;

public class NonCriticalSection extends ElementList
{
	public NonCriticalSection(int numThreads, int width)
	{
		super(numThreads, width);
		setName("NCS", Color.blue,width/4f);
	}
	
	public NonCriticalSection(int numThreads, int width, int length)
	{
		super(numThreads, width, length);
		setName("NCS", Color.blue,width/4f);
	}
	
	public NonCriticalSection(int numThreads, int width, Direction direction)
	{
		super(numThreads, width, direction);
		setName("NCS", Color.blue,width/4f);
	}
	
	@Override
	public NonCriticalSection clone()
	{
		return (NonCriticalSection) super.clone();
	}
}
