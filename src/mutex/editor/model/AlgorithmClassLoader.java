package mutex.editor.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class AlgorithmClassLoader extends ClassLoader implements Serializable
{
	// to avoid InvalidClassExceptions, while allowing me to add new parameters later,
	// the version ID stays constant
	//(note to self: fields can be added/removed, but types can't be changed)
	public static final long serialVersionUID = 1L;

	private Map<String, byte[]> myClassBytes;

	public AlgorithmClassLoader()
	{
		super(AlgorithmClassLoader.class.getClassLoader());
		myClassBytes = new HashMap<>();
	}

	public AlgorithmClassLoader(Map<String, byte[]> classBytes)
	{
		super(AlgorithmClassLoader.class.getClassLoader());
		if(classBytes == null)
			myClassBytes = new HashMap<>();
		else
			myClassBytes = classBytes;
	}
	
	@Override
	public Class<?> findClass(String name) throws ClassNotFoundException
	{
		//System.out.println(name + myClassBytes);
		byte[] bytes = myClassBytes.get(name);
		if(bytes != null)
		{
			return defineClass(name, bytes, 0, bytes.length);
		}
		else
			return super.findClass(name);
	}
}
