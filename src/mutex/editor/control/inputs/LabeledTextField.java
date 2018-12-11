package mutex.editor.control.inputs;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import mutex.editor.model.Field;

/**
 * keeps a JLabel and a JTextField positioned correctly
 * 
 * @author Ryley
 * @version 2.0
 */
public class LabeledTextField extends LabeledField implements DocumentListener
{
	public static final int FIELD_WIDTH = 70;
	public static final int FIELD_HEIGHT = 20;
	
	private static final Color VALID_COLOR = Color.WHITE;
	private static final Color INVALID_COLOR = Color.getHSBColor(0, 0.2f, 1);
	
	private JTextField myTextField;
	
	public LabeledTextField(Field field)
	{
		super(field);
		
		myTextField = new JTextField();
		
		myTextField.setPreferredSize(new Dimension(FIELD_WIDTH,FIELD_HEIGHT));
		myTextField.setMinimumSize(getPreferredSize());
		myTextField.setMaximumSize(new Dimension(Integer.MAX_VALUE,FIELD_HEIGHT));
		
		myTextField.setText(field.getValue().toString());
		myTextField.getDocument().addDocumentListener(this);
		add(myTextField);
		
		//forces the panel to stay the "packed" size
		setMinimumSize(getPreferredSize());
		setMaximumSize(new Dimension(Integer.MAX_VALUE,getPreferredSize().height));
		
//		this.setBorder(BorderFactory.createCompoundBorder(
//        		BorderFactory.createLineBorder(Color.red),this.getBorder()));
	}
	
	public void setValue(Object val)
	{
		setText(val.toString());
	}
	public Object getValue()
	{
		return getText();
	}
	
	public String getText()
	{
		return myTextField.getText();
	}
	
	public void setText(String text)
	{
		myTextField.setText(text);
	}
	
	public void setInputValid(boolean valid)
	{
		super.setInputValid(valid);
		if(isInputValid())
			myTextField.setBackground(VALID_COLOR);
		else
			myTextField.setBackground(INVALID_COLOR);
	}

	@Override
	public void changedUpdate(DocumentEvent e){
		stateChanged();
	}
	@Override
	public void insertUpdate(DocumentEvent e){
		stateChanged();
	}
	@Override
	public void removeUpdate(DocumentEvent e){
		stateChanged();
	}
}
