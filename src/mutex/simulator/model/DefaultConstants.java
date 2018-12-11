package mutex.simulator.model;
public class DefaultConstants extends CodeConstants
{
	public static final String[] CODE = {
			"/* This is a default algorithm!",
			" * To load a compiled algorithm file,",
			" * Use the file menu in the top left!",
			" * To create or modify algorithms,",
			" * Load the editor jar file.",
			" */",
			"if(foo == (ID%2 == 0)){",
			"	bar = bar + 1;",
			"}",
			"foo = (ID%2 == 0);"
	};
	
	public static final Step[] STEPS =
		{	new Step(6, 3, 3),
			new Step(7, 4, 13),
			new Step(7, 10, 3),
			new Step(9, 0, 17)};
	
	public DefaultConstants(){
		super(CODE, STEPS);
	}
}
