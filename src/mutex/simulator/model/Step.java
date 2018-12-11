package mutex.simulator.model;

import java.io.Serializable;

public class Step implements Serializable
{
	private int myLineNum;
	private int myPosition;
	private int myWidth;
	
	public Step(int lineNum, int pos, int width)
	{
		myLineNum = lineNum;
		myPosition = pos;
		myWidth = width;
	}
	
	public int getLine(){
		return myLineNum;
	}
	public int getPos(){
		return myPosition;
	}
	public int getWidth(){
		return myWidth;
	}
}
