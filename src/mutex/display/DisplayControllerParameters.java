package mutex.display;

import mutex.editor.model.Field;

import java.io.Serializable;
import java.util.ArrayList;

public class DisplayControllerParameters implements Serializable {

    // to avoid InvalidClassExceptions, while allowing me to add new parameters later,
    // the version ID stays constant
    //(note to self: fields can be added/removed, but types can't be changed)
    public static final long serialVersionUID = 1L;

    private DisplayType myDisplayType;
    private ArrayList<Field> myEditableFields;

    public DisplayControllerParameters() {
        setSafeDefaults();
    }

    public void setSafeDefaults() {
        myDisplayType = DisplayType.BLANK;
        myEditableFields = new ArrayList<>(0);
    }

    public DisplayType getDisplayType() {
        return myDisplayType;
    }

    public void setDisplayType(DisplayType displayType) {
        this.myDisplayType = displayType;
    }

    public ArrayList<Field> getEditableFields() {
        return myEditableFields;
    }

    public void setEditableFields(ArrayList<Field> editableFields) {
        this.myEditableFields = editableFields;
    }
}
