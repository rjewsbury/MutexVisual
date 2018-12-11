package mutex.editor.model;

import java.io.Serializable;
import java.util.Map;

/**
 * The other value solution worked for display and
 * delayed reads/writes, but this solution works
 * better for modifying objects at runtime because
 * type erasure makes it difficult to do anything
 * type related
 *
 * @author Ryley Jewsbury
 */
public class Field implements Serializable
{
	String myName;
	Class<?> myClass;
	Object myValue;
	
	//for a field with a fixed set of options
	Map<String, Object> myOptions;
	
	public Field(String name, Object value, Class<?> cls)
	{
		myName = name;
		myValue = value;
		myClass = cls;
		
		//primitives cause problems with their auto box/unboxing, so use their wrapper
		if(cls.isPrimitive())
		{
			if(cls == int.class)
				myClass = Integer.class;
			if(cls == long.class)
				myClass = Long.class;
			if(cls == float.class)
				myClass = Float.class;
			if(cls == double.class)
				myClass = Double.class;
			if(cls == short.class)
				myClass = Short.class;
			if(cls == byte.class)
				myClass = Byte.class;
			if(cls == boolean.class)
				myClass = Boolean.class;
			if(cls == char.class)
				myClass = Character.class;
		}
	}
	
	public String getName(){
		return myName;
	}
	public Class<?> getType(){
		return myClass;
	}
	public Object getValue(){
		return myValue;
	}
	public void setValue(Object value){
		//if the cast fails, its an invalid state
		//System.out.println(value);
		//this does not seem to like casting between primitives and their wrapper class
		if(myClass==double.class || myClass==Double.class)
			myValue = ((Number)value).doubleValue();
		else if(myClass==float.class || myClass==Float.class)
			myValue = ((Number)value).floatValue();
		else if(myClass==int.class || myClass==Integer.class)
			myValue = ((Number)value).intValue();
		else if(myClass==long.class || myClass==Long.class)
			myValue = ((Number)value).longValue();
		else if(myClass==short.class || myClass==Short.class)
			myValue = ((Number)value).shortValue();
		else if(myClass==byte.class || myClass==Byte.class)
			myValue = ((Number)value).byteValue();
		else
			myValue = myClass.cast(value);
	}
	public boolean hasOptions(){
		return myOptions != null;
	}
	public Map<String,Object> getOptions(){
		return myOptions;
	}
	public void setOptions(Map<String, Object> options){
		myOptions = options;
	}
	
//	public int getInt(){
//		return (int)myValue;
//	}
//	public long getLong(){
//		return (long)myValue;
//	}
//	public float getFloat(){
//		return (float)myValue;
//	}
//	public double getDouble(){
//		return (double)myValue;
//	}
//	public short getShort(){
//		return (short)myValue;
//	}
//	public byte getByte(){
//		return (byte)myValue;
//	}
//	public char getChar(){
//		return (char)myValue;
//	}
}
