package mutex.simulator.control;

import java.awt.Dimension;
import javax.swing.*;
import javax.swing.event.*;

/**
 * A slider that acts as an input.
 * 
 * @author Ryley
 */
public class DelaySlider extends JPanel implements ChangeListener
{
	//the default text
	public static final String LABEL = "Delay(ms): ";
	//the default minimum value
	public static final int MIN = 2;
	//the default maximum value
	public static final int MAX = 200;
	//the default initial value
	public static final int INIT = 100;
	
    private JLabel label;
    private JSlider slider;
    
    public DelaySlider()
    {
        setLayout(new BoxLayout(this,BoxLayout.LINE_AXIS));
        setBorder(BorderFactory.createEmptyBorder(0,10,0,10));
        label = new JLabel(LABEL+INIT);
        slider = new JSlider(MIN,MAX,INIT);
        
        //slider.setMajorTickSpacing((max-min)/5);
        //slider.setMinorTickSpacing((max-min)/25);
        //slider.setPaintTicks(true);
        slider.addChangeListener(this);
        
        add(slider);
        add(Box.createHorizontalStrut(10));
        add(label);
        
        Dimension labelSize = new Dimension(110,30);
        label.setMinimumSize(labelSize);
        label.setPreferredSize(labelSize);
        label.setMaximumSize(labelSize);
        
        //this.setBorder(BorderFactory.createCompoundBorder(
        //		BorderFactory.createLineBorder(Color.red),this.getBorder()));
    }
    
    public void addChangeListener(ChangeListener listener)
    {
    	slider.addChangeListener(listener);
    }
    
    /**
     * changes the displayed value for the slider.
     * 
     * @param e the change in slider position being listened to
     */
    public void stateChanged(ChangeEvent e) {
        int value = ((JSlider)e.getSource()).getValue();
        label.setText(LABEL+value);
    }
}
