package mutex.editor.model;

import java.util.List;

public interface Editable
{
	/**
	 * gets a list of the names, values, and types of each
	 * of the editable fields
	 */
	List<Field> getEditableFields();
	
	/**
	 * Updates a field of the editable. If the update was valid,
	 * it may make valid changes to some of the other fields of
	 * the object, so anything using them must get editable fields again.
	 * 
	 * If the change is invalid, its guaranteed that there will be no changes
	 * 
	 * @return true if the field was valid, false if it was not
	 */
	boolean setField(Field fields);
}
