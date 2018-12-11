package mutex.editor.control.inputs;

import mutex.editor.control.PropertyEditor;
import mutex.editor.model.Editable;
import mutex.editor.model.Field;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class LabeledEditableField extends LabeledField implements ChangeListener
{
    private PropertyEditor myProperties;

    public LabeledEditableField(Field field)
    {
        super(field);

        if(!Editable.class.isAssignableFrom(field.getType()))
            throw new IllegalArgumentException("the given field is not an editable");

        myProperties = new PropertyEditor("No Properties");
        myProperties.setEditable((Editable)field.getValue());
        myProperties.addChangeListener(this);
        myProperties.setBorder(BorderFactory.createCompoundBorder(
        		BorderFactory.createLineBorder(Color.BLACK),myProperties.getBorder()));
        add(myProperties);

        this.setValue(field.getValue());

//        this.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(Color.red),this.getBorder()));
    }

    public void setEditable(Editable editable) {
        myProperties.setEditable(editable);
    }

    public Editable getEditable() {
        return myProperties.getEditable();
    }

    @Override
    public void setValue(Object value)
    {
        setEditable((Editable) value);
    }

    @Override
    public Object getValue()
    {
        return getEditable();
    }

    @Override
    public void stateChanged(ChangeEvent changeEvent) {
        stateChanged();
    }
}
