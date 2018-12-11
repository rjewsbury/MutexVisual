package mutex.simulator.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

import mutex.EditorMain;
import mutex.display.elements.ThreadFrame;
import mutex.simulator.model.AlgorithmThread;
import mutex.simulator.model.AlgorithmThreadGroup;
import mutex.simulator.model.CodeConstants;
import mutex.simulator.model.Step;
import mutex.simulator.model.ThreadUpdateListener;

public class CodeDisplayPanel extends JPanel implements ThreadUpdateListener
{
	public static final int SPACING = 6;
	public static final int BUFFER = 10;
	public static final Font FONT = EditorMain.GLOBAL_FONT;
	public static final int TEXT_WIDTH = (int)(FONT.getSize()*0.6);

	public static int charDistance(int chars)
	{
		return chars*TEXT_WIDTH;
	}

	public static int lineDistance(int lines)
	{
		return lines*(SPACING+FONT.getSize());
	}

	private ThreadFrame[] myThreads;
	private CodeConstants myCode;
	
	public CodeDisplayPanel(AlgorithmThreadGroup threads, CodeConstants code)
	{
		this.setBackground(Color.WHITE);
		
		setCodeConstants(code);

		setThreadGroup(threads);
	}

	public void setCodeConstants(CodeConstants code) {
		myCode = code;

		Dimension size = calculateSize();

		setMinimumSize(size);
		setPreferredSize(size);
	}

	public void setThreadGroup(AlgorithmThreadGroup threads) {
		myThreads = new ThreadFrame[threads.size()];
		for(int i=0; i<myThreads.length; i++)
		{
			myThreads[i] = new ThreadFrame(
					threads.getThread(i).getID(),
					threads.getThread(i).getPreferredColor());
			myThreads[i].setHeight(FONT.getSize());
		}

		for (int i = 0; i < threads.size(); i++) {
			update(threads.getThread(i));
		}
	}
	
	private Dimension calculateSize()
	{
		int height = 2*BUFFER + (FONT.getSize()+SPACING)*myCode.getCodeLength();
		int width = 0;
		for(int i=0; i<myCode.getCodeLength(); i++)
		{
			String line = myCode.getLine(i);
			if(line.length() > width)
				width = line.length();
		}
		
		width = 2*BUFFER + width*TEXT_WIDTH;
		
		return new Dimension(width, height);
	}
	
	private void moveMarker(int ID, int stepNum)
	{
		Step step = myCode.getStep(stepNum);
		myThreads[ID].setX(BUFFER+charDistance(step.getPos()));
		//adds an extra half-size to account for characters below the baseline
		myThreads[ID].setY(BUFFER+lineDistance(step.getLine())+FONT.getSize()/5);
		
		int width = charDistance(step.getWidth());
		myThreads[ID].setWidth(width);
		myThreads[ID].setTagOffset((width - ThreadFrame.TAG_WIDTH)*ID/myThreads.length);
	}
	
	@Override
	public void update(AlgorithmThread thread)
	{
		int ID = thread.getID();
		int step = thread.getStepNumber();
		
		if(myCode.getMaxSteps() > 0)
			moveMarker(ID, step);
		
		repaint();
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		
		g.setColor(Color.BLACK);
		g.setFont(FONT);
		for(int i=0; i<myCode.getCodeLength();i++)
		{
			String line = myCode.getLine(i);
			//the coordinate is at the baseline, so add the font size an extra time
			g.drawString(line, BUFFER, BUFFER+lineDistance(i)+FONT.getSize());
		}
		
		for(ThreadFrame m: myThreads)
			m.draw(g);
	}
}
