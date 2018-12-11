package mutex.simulator.model;

import java.util.HashMap;
import java.util.Map;

public class AlgorithmParams
{
	private int ID;
	private int N;
	private Map<String, Wrapper> sharedVars;
	
	public AlgorithmParams()
	{
		setSafeDefaults();
	}
	public AlgorithmParams(int id, int n, Map<String, Wrapper> sharedVars)
	{
		setSafeDefaults();
		setID(id);
		setN(n);
		setSharedVars(sharedVars);
	}
	
	private void setSafeDefaults()
	{
		setID(0);
		setN(1);
		setSharedVars(new HashMap<>());
	}
	
	public void setID(int id){
		if(id >= 0)
			ID = id;
	}
	public int getID(){
		return ID;
	}
	
	public void setN(int n){
		if(n > 0)
			N = n;
	}
	public int getN(){
		return N;
	}
	
	public void setSharedVars(Map<String, Wrapper> shared){
		if(shared != null)
			sharedVars = shared;
	}
	public Map<String,Wrapper> getSharedVars(){
		return sharedVars;
	}
}
