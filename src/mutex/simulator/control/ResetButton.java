package mutex.simulator.control;

import mutex.EditorMain;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ResetButton extends JPanel implements ActionListener, DocumentListener {

    public static final String RESET_MESSAGE = "Reset";
    private static final Color VALID_COLOR = Color.WHITE;
    private static final Color INVALID_COLOR = Color.getHSBColor(0, 0.2f, 1);

    private ArrayList<ActionListener> myListeners;
    private JButton myResetButton;
    private JTextField myResetNumber;

    public ResetButton(int initialNumber) {
        myListeners = new ArrayList<>();

        myResetNumber = new JTextField();
        myResetNumber.setFont(EditorMain.GLOBAL_FONT);
        myResetNumber.setText(""+initialNumber);
        myResetButton = new JButton(RESET_MESSAGE);
        myResetButton.setFont(EditorMain.GLOBAL_FONT);

        myResetNumber.getDocument().addDocumentListener(this);
        myResetButton.addActionListener(this);

        int buttonHeight = myResetButton.getPreferredSize().height;
        Dimension textDimension = new Dimension(buttonHeight, buttonHeight);
        //System.out.println(buttonHeight);
        myResetNumber.setMinimumSize(textDimension);
        myResetNumber.setPreferredSize(textDimension);
        myResetNumber.setMaximumSize(textDimension);
        myResetNumber.setSize(textDimension);


        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(myResetNumber);
        add(myResetButton);

        setMaximumSize(getPreferredSize());
    }

    public void addActionListener(ActionListener listener) {
        myListeners.add(listener);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if(actionEvent.getSource() == myResetButton)
            reset();
    }

    public void reset() {
        int number = 0;
        try {
            number = Integer.parseInt(myResetNumber.getText());
        } catch (NumberFormatException e) {
            //leave the number as 0
        }

        //can only support 2 or more threads
        if(number <= 1) {
            number = 0;
        }

        //modify the event to include the number info
        ActionEvent event = new ActionEvent(this, number, RESET_MESSAGE);

        //broadcast the event
        for(ActionListener al : myListeners) {
            al.actionPerformed(event);
        }
    }

    @Override
    public void insertUpdate(DocumentEvent documentEvent) {
        updateTextField();
    }

    @Override
    public void removeUpdate(DocumentEvent documentEvent) {
        updateTextField();
    }

    @Override
    public void changedUpdate(DocumentEvent documentEvent) {
        updateTextField();
    }

    private void updateTextField() {
        try {
            //test that it's a valid number
            int number = Integer.parseInt(myResetNumber.getText());
            if(number > 1) {
                myResetNumber.setBackground(VALID_COLOR);
                return;
            }
        } catch (NumberFormatException e) {}

        //if any test fails,
        myResetNumber.setBackground(INVALID_COLOR);
    }
}
