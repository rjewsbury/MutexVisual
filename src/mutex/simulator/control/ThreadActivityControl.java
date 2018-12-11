package mutex.simulator.control;

import mutex.EditorMain;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class ThreadActivityControl extends JPanel implements ItemListener, ActionListener {

    private ItemListener myItemListener;
    private ActionListener myActionListener;
    private JCheckBox myCheckBox;
    private JButton myButton;
    private int myID;

    public ThreadActivityControl(int id) {
        myID = id;

        setLayout(new GridLayout(2,1));

        myCheckBox = new JCheckBox();
        myCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
        myCheckBox.addItemListener(this);
        add(myCheckBox);

        myButton = new JButton(""+id);
        myButton.setFont(EditorMain.GLOBAL_FONT.deriveFont(10f));
        myButton.setMargin(new Insets(0,0,0,0));
        myButton.addActionListener(this);
//        myButton.setPreferredSize(new Dimension(0,0));
        add(myButton);

//        this.setBorder(BorderFactory.createCompoundBorder(
//        		BorderFactory.createLineBorder(Color.BLACK),this.getBorder()));
    }

    public void setSelected(boolean b) {
        myCheckBox.setSelected(b);
    }

    public void addItemListener(ItemListener listener) {
        myItemListener = listener;
    }

    public void addActionListener(ActionListener listener) {
        myActionListener = listener;
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        if(myItemListener != null)
            myItemListener.itemStateChanged(
                    new ItemEvent(
                            myCheckBox,
                            myID,
                            itemEvent.getItem(),
                            itemEvent.getStateChange()));
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if(myActionListener != null)
            myActionListener.actionPerformed(new ActionEvent(myButton, myID, RunMenu.STEP_MESSAGE));
    }
}
