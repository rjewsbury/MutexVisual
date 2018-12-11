package mutex.simulator.model;
public class DefaultThread extends AlgorithmThread
{
	public DefaultThread(AlgorithmParams params){
		super(params);
		initializeShared();
		initialize();
	}

	boolean foo = true;
	Value<Boolean> fooVar;
	
	int bar;
	Value<Integer> barVar;
	
	protected void initializeShared()
	{
		fooVar = new Value<>(foo);
		fooVar = (Value<Boolean>) addSharedVariable("foo",new Wrapper(fooVar)).getVariable();
	}

	protected void initialize()
	{
		barVar = new Value<>(bar);
		addVariable("bar", new Wrapper(barVar));
	}

	protected void algorithm() throws InterruptedException
	{
		if(pauseRead(0, fooVar) == (ID%2 == 0))
			pauseWrite(1,barVar, pauseRead(2,barVar) + 1);
		pauseWrite(3,fooVar, (ID%2 == 0));
	}
}
