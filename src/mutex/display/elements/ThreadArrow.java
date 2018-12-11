package mutex.display.elements;

import java.awt.*;

public class ThreadArrow extends ViewElement{

    private int myID;
    private Color myColor;
    private int myWidth, myHeight;

    public ThreadArrow(int ID, Color color)
    {
        myID = ID;
        myColor = color;
        myWidth = 30;
        myHeight = 15;
    }

    public void setWidth(int width)
    {
        myWidth = width;
    }

    public void setHeight(int height)
    {
        myHeight = height;
    }

    public int getWidth()
    {
        return myWidth;
    }

    public int getHeight()
    {
        return myHeight;
    }

    @Override
    public void draw(Graphics2D g)
    {
        g.setColor(myColor);
        g.fillPolygon(
                new int[]{getX(),getX()+myWidth,getX()+myWidth/2},
                new int[]{getY(),getY(),getY()+myHeight},
                3);

//        g.setColor(Color.BLACK);
//        g.setFont(g.getFont().deriveFont((float)myHeight));
//        g.drawString(""+myID, getX(), getY());
    }

    @Override
    public ThreadFrame clone()
    {
        return (ThreadFrame) super.clone();
    }
}
