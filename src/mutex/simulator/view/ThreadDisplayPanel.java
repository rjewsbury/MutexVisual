package mutex.simulator.view;

import mutex.display.elements.ThreadArrow;
import mutex.simulator.model.*;

import javax.swing.*;
import java.awt.*;

public class ThreadDisplayPanel extends JPanel implements ThreadUpdateListener {

    private static final int MAX_ARROW_WIDTH = 40;
    private static final int MIN_ARROW_WIDTH = 10;

    private ThreadArrow[] myThreads;
    private CodeConstants myCode;
    private int myOffset;

    public ThreadDisplayPanel(AlgorithmThreadGroup threads, CodeConstants code)
    {
        this.setBackground(Color.WHITE);

        myOffset = 0;

        setCodeConstants(code);
        setThreadGroup(threads);
    }

    public void setCodeConstants(CodeConstants code) {
        myCode = code;
    }

    public void setThreadGroup(AlgorithmThreadGroup threads) {
        myThreads = new ThreadArrow[threads.size()];
        for(int i=0; i<myThreads.length; i++)
        {
            myThreads[i] = new ThreadArrow(
                    threads.getThread(i).getID(),
                    threads.getThread(i).getPreferredColor());
            myThreads[i].setHeight(CodeDisplayPanel.FONT.getSize());
        }

        for (int i = 0; i < threads.size(); i++) {
            update(threads.getThread(i));
        }
    }

    public void setOffset(int offset) {
        int diff = offset - myOffset;
        myOffset = offset;

        for(ThreadArrow arrow : myThreads) {
            arrow.setY(arrow.getY()-diff);
        }

        repaint();
    }

    private void moveMarker(int ID, int stepNum)
    {
        Step step = myCode.getStep(stepNum);
        //adds an extra half-size to account for characters below the baseline
        myThreads[ID].setY(CodeDisplayPanel.BUFFER
                + CodeDisplayPanel.lineDistance(step.getLine())
                - myOffset);
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
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.BLACK);
        for (int i = 0; i < myThreads.length; i++) {
            int x = (i+1)*getWidth()/(myThreads.length+1);
            int width = getWidth()/(myThreads.length+1);

            g.drawLine(x, 0, x, getHeight());

            if(width < MIN_ARROW_WIDTH)
                myThreads[i].setWidth(MIN_ARROW_WIDTH);
            else if (width < MAX_ARROW_WIDTH)
                myThreads[i].setWidth(width);
            else
                myThreads[i].setWidth(MAX_ARROW_WIDTH);

            myThreads[i].setX(x-myThreads[i].getWidth()/2);
        }

        for (ThreadArrow arrow : myThreads) {
            arrow.draw(g);
        }
    }
}
