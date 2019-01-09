package mutex.editor.model;

import java.util.ArrayList;
import java.util.Arrays;

import mutex.simulator.model.CodeConstants;
import mutex.simulator.model.Step;

public class AlgorithmWriter
{
	public static void main(String[] args)
	{
		AlgorithmReader r = new AlgorithmReader(ExampleCode.KNUTH_EXAMPLE);
		AlgorithmWriter w = new AlgorithmWriter(r);

		System.out.println(w.buildAlgorithmThread());
	}
	
	public static final String MODEL_PACKAGE = "mutex.simulator.model";
	public static final String CODE_CONSTANT_BASE =
			"import "+MODEL_PACKAGE+".CodeConstants;%n%n"+
			"public class %sConstants extends CodeConstants{%n"+
			"    public static final String[] CODE =%n"+
			"        %s;%n"+
			"    public static final int[] STEP_X = %s;%n"+
			"    public static final int[] STEP_LINE = %s;%n"+
			"    public static final int[] STEP_WIDTH = %s;%n%n"+
			"    public %1$sConstants(){%n"+
			"        super(CODE, STEP_X, STEP_LINE, STEP_WIDTH);%n"+
			"    }%n"+
			"}%n";
	public static final String ALGORITHM_THREAD_BASE =
			"//imports%n"+
			"import "+MODEL_PACKAGE+".AlgorithmParams;%n"+
			"import "+MODEL_PACKAGE+".AlgorithmThread;%n"+
			"import "+MODEL_PACKAGE+".Value;%n"+
			"import "+MODEL_PACKAGE+".Wrapper;%n"+
			"%s%n"+
			"public class %s {%n"+
			"    public %s(AlgorithmParams params){%n"+
			"        super(params);%n"+
			"        initializeShared();%n"+
			"        initialize();%n"+
			"    }%n%n"+
			"    %s%n%n"+
			"    public void initializeShared(){%n"+
			"        %s%n"+
			"    }%n%n"+
			"    public void initialize(){%n"+
			"        %s%n"+
			"    }%n%n"+
			"    public void algorithm() throws InterruptedException{%n"+
			"        %s%n"+
			"    }%n"+
			"}%n";
	//matches either a trailing close bracket or a semicolon
	private static final String END_REGEX = "\\s*(\\)\\s*\\{|;)";
	//this is probably missing a few separators
	private static final String SEPARATOR_REGEX = "(==|<|>|!|&|\\||\\+|-|\\*|/|%|\\?|:)";
	//matches what comes after an un-processed variable
	private static final String READ_REGEX = "(?!Var)((\\s*\\[.+?])*)";
	//matches whatever comes after a variable name that describes a write
	private static final String WRITE_REGEX = READ_REGEX+"\\s*=\\s*([^=]+?)"+END_REGEX;
	//matches " /*__*/ " across multiple lines
	private static final String MULTI_COMMENT_REGEX = "/\\*.*?\\*/";
	//matches " //___(newline)"
	private static final String COMMENT_REGEX = "//.*?(\\R)";
	private static final String VAR_SYMBOL_REGEX = "(?<![a-zA-Z0-9_$])";
	private static final String VAR_SYMBOLS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_$";
	private static final String MIXED_OP_REGEX = "(\\+\\+|--|\\+=|-=|\\*=|/=|%=|<<=|>>=|&=|^=|\\|=)";
	private static final String PRIMITIVE_REGEX = "char|boolean|byte|short|int|long|float|double";
	
	private AlgorithmReader myReader;
	private String[] myPauseVariables;
	private String[] myNonPauseVariables;

	//used to cast boxed types back to primitives
	//avoids issues with comparing primitives using "=="
	private String[] myPauseVarConversion;
	private String[] myNonPauseVarConversion;

	public AlgorithmWriter(AlgorithmReader reader)
	{
		myReader = reader;
		setPauseVars(reader.getSharedVariables());
	}
	
	/**
	 * At the time of writing this, I have no idea how i'm going to get
	 * the user to define which variables will pause, so this method
	 * takes as much possible precaution and only allows existing vars
	 * to be marked as pause or non pause vars
	 */
	public void setPauseVars(String[] vars)
	{
		ArrayList<String> pauseVars = new ArrayList<>();
		ArrayList<String> pauseVarConversion = new ArrayList<>();
		ArrayList<String> nonPauseVars = new ArrayList<>();
		ArrayList<String> nonPauseVarConversion = new ArrayList<>();
		
		String[] names = new String[vars.length];
		String[] sharedVars = myReader.getSharedVariables();
		String[] memberVars = myReader.getMemberVariables();
		
		String var;
		String conversion;
		String[] tokens;
		for(int i=0; i<names.length; i++){
			//removes all type information from the given name
			tokens = vars[i].replaceAll("\\[|]", "").split("\\s+");
			names[i] = tokens[tokens.length-1];
		}

		// search through vars for matches and populate the lists
		searchVars(names,sharedVars,pauseVars,pauseVarConversion,nonPauseVars,nonPauseVarConversion);
		searchVars(names,memberVars,pauseVars,pauseVarConversion,nonPauseVars,nonPauseVarConversion);

		myPauseVariables = new String[pauseVars.size()];
		myNonPauseVariables = new String[nonPauseVars.size()];
		pauseVars.toArray(myPauseVariables);
		nonPauseVars.toArray(myNonPauseVariables);

		myPauseVarConversion = new String[pauseVarConversion.size()];
		myNonPauseVarConversion = new String[nonPauseVarConversion.size()];
		pauseVarConversion.toArray(myPauseVarConversion);
		nonPauseVarConversion.toArray(myNonPauseVarConversion);
	}

	private void searchVars(String[] names, String[] vars,
							ArrayList<String> found, ArrayList<String> foundConversion,
							ArrayList<String> notFound, ArrayList<String> notFoundConversion) {
		String[] tokens;
		String searchVar;
		String conversion;
		boolean hasFound;
		for (String sharedVar : vars) {
			tokens = sharedVar.replaceAll("\\[|]", "").split("\\s+");
			conversion = tokens[0].matches(PRIMITIVE_REGEX)?
					String.format(".%sValue()",tokens[0]):
					""; // object types dont need a conversion
			searchVar = tokens[tokens.length - 1];
			//search for the variable name
			hasFound = false;
			for (String name : names) {
				if (searchVar.equals(name)) {
					found.add(searchVar);
					foundConversion.add(conversion);
					//found it, so move on
					hasFound = true;
					break;
				}
			}
			//didn't find it
			if(!hasFound) {
				notFound.add(searchVar);
				notFoundConversion.add(conversion);
			}
		}
	}

	//creates code that *should* compile if things were written correctly
	public String getCompilableSource()
	{
		if(!myReader.getSuperclassName().equals("AlgorithmThread")) {
			throw new RuntimeException("Illegal Syntax: the algorithm must extend AlgorithmThread");
		}

		String code = myReader.getCode().replaceAll("shared", "static");
		code = "import mutex.simulator.model.*;\n" + code;
		String insert =
				//dummy constructor
				"\n"+myReader.getClassName()+"(){\n\tsuper(new AlgorithmParams());\n}"+
						"static int N = 0; int ID = 0;\n"+
						"public void CriticalSection(){}\n"+
						"public void CriticalSection(int time){}\n";

		//build dummy getters
		String[] sharedVars = myReader.getSharedVariables();
		String[] memberVars = myReader.getMemberVariables();
		ArrayList<String> vars = new ArrayList<>();
		vars.addAll(Arrays.asList(sharedVars));
		vars.addAll(Arrays.asList(memberVars));

		for(String dummyVar : vars)
		{
			String var = dummyVar;
			int dimension;
			String[] tokens;

			//counts the number of opening brackets
			dimension = var.length()-var.replace("[", "").length();
			tokens = var.split("[\\[\\]\\s]+");

			var = "public "+tokens[0]+getArrayBrackets(dimension)+
					" get"+tokens[1].substring(0,1).toUpperCase()+tokens[1].substring(1)+"() {\n" +
					"return "+tokens[1]+";\n}\n";
			insert += var;
		}

		if(myReader.getInitialize().equals(""))
			insert += "public void initialize(){}";
		if(myReader.getInitializeShared().equals(""))
			insert += "public void initializeShared(){}";

		code = code.substring(0, code.indexOf('{')+1)+
				insert +
				code.substring(code.indexOf('{')+1);
		return code;
	}
	
	public String buildAlgorithmThread()
	{
		//build Var objects
		String declaredVars = generateVars();
		
		//modify initializes to create Vars and add them to the Map
		String initializeShared = modifyInitializeShared();
		String initialize = modifyInitialize();
		
		//modify the algorithm to include pauses
		String algorithm = modifyAlgorithm(
				myPauseVariables, myPauseVarConversion,
				myNonPauseVariables, myNonPauseVarConversion);
		String classStatement = myReader.getClassName();

		//currently, the algorithm cannot have a user defined superclass
		//must be allowed to extend AlgorithmThread
		if(!myReader.getSuperclassName().equals("AlgorithmThread"))
			throw new RuntimeException("Illegal Syntax: the algorithm must extend AlgorithmThread");
		classStatement += " extends AlgorithmThread";

		if(myReader.getImplements().length > 0) {
			String[] implementNames = myReader.getImplements();
			classStatement += " implements "+implementNames[0];
			for (int i = 1; i < implementNames.length; i++) {
				classStatement += " , "+implementNames[i];
			}
		}

		return String.format(
				ALGORITHM_THREAD_BASE,
				myReader.getImports(),
				classStatement,
				myReader.getClassName(),
				declaredVars,
				initializeShared,
				initialize,
				algorithm);
	}
	
	/**
	 * Mirrors the functionality of the modifyAlgorithm method
	 * so that step numbers and pause steps line up
	 */
	public CodeConstants buildConstants()
	{
		ArrayList<Integer> stepLine = new ArrayList<>();
		ArrayList<Integer> stepX = new ArrayList<>();
		ArrayList<Integer> stepWidth = new ArrayList<>();
		
		String code = myReader.getAlgorithm();
		//the display does not work very well on multi-line steps,
		//so restrict it to single lines
		String[] lines = code.trim().split("\\R");
		String line;
		
		boolean commentMode = false;
		
		lineReader:
		for(int lineNum=0; lineNum < lines.length; lineNum++){
			line = lines[lineNum];
			for(int x = 0; x < line.length(); x++){
				//if we're in a comment block, try to get out
				if(commentMode){
					int end = line.indexOf("*/", x);
					if(end != -1){
						continue lineReader;
					}else{
						x = end;
						commentMode = false;
					}
				}
				//if we're not in a comment block...
				//check for comments
				else if(line.startsWith("//", x))
					continue lineReader;
				else if(line.startsWith("/*", x))
					commentMode = true;
				//check for prebuilt functions. currently only CriticalSection
				else if(line.startsWith("CriticalSection",x)){
					stepLine.add(lineNum);
					stepX.add(x);
					stepWidth.add(getStepString(line, x, "CriticalSection").length());
				}
				//check for variables
				else{
					for(String var: myPauseVariables){
						if(line.startsWith(var,x)) {
							char prev;
							if(x == 0)
								prev = ' ';
							else
								prev = line.charAt(x-1);

							// check that the previous character isnt part of a variable name
							// poor man's regex lookbehind
							if (VAR_SYMBOLS.indexOf(prev) < 0) {
								stepLine.add(lineNum);
								stepX.add(x);
								stepWidth.add(getStepString(line, x, var).length());
							}
						}
					}//thats a lot of closing brackets
				}//maybe I should refactor this
			}//watch out for that last step
		}
		
		Step[] steps = new Step[stepLine.size()];
		for(int i=0; i<steps.length; i++)
			steps[i] = new Step(
					stepLine.get(i),
					stepX.get(i),
					stepWidth.get(i));
		
		return new CodeConstants(lines, steps);
	}
	
	private String getObjectTypeString(String type)
	{
		switch(type){
			case "int":
				type = "Integer";
				break;
			case "double":
				type = "Double";
				break;
			case "long":
				type = "Long";
				break;
			case "float":
				type = "Float";
				break;
			case "char":
				type = "Character";
				break;
			case "short":
				type = "Short";
				break;
			case "byte":
				type = "Byte";
				break;
			case "boolean":
				type = "Boolean";
				break;
		}
		
		return type;
	}
	
	private String getArrayBrackets(int dimension)
	{
		String brackets = "";
		
		for(int i=0; i<dimension; i++)
			brackets += "[]";
		
		return brackets;
	}
	
	/**
	 * var is a "type" "name" pair, with brackets pretty much anywhere
	 * e.g. int[] foo[ ] []
	 */
	private String buildVariable(String var)
	{
		int dimension;
		String[] tokens;
		
		//counts the number of opening brackets
		dimension = var.length()-var.replace("[", "").length();
		tokens = var.split("[\\[\\]\\s]+");

		//before replacing the primitive type, create a getter
		//this is used for custom displays
		var = "public "+tokens[0]+getArrayBrackets(dimension)+
				" get"+tokens[1].substring(0,1).toUpperCase()+tokens[1].substring(1)+"() {\n";

		if(dimension > 0) {
			var += tokens[0]+getArrayBrackets(dimension)+" "+tokens[1]+
					" = new "+tokens[0]+"["+tokens[1]+"Var.length]"+getArrayBrackets(dimension-1)+";\n";
			String indexBrackets = "";
			String closingBrackets = "";
			for(int i = 0; i < dimension; i++) {
				String iter = "i"+i;
				var += "for(int "+iter+" = 0; "+iter+" < "+tokens[1]+"Var"+indexBrackets+".length; "+iter+"++) {\n";
				closingBrackets += "}";
				indexBrackets += "["+iter+"]";

				if(i+1 == dimension) {
					var += tokens[1]+indexBrackets+" = "+tokens[1]+"Var"+indexBrackets+".get();\n" +
							closingBrackets+"\n";
				} else {
					var += tokens[1]+indexBrackets
							+" = new "+tokens[0]+"["+tokens[1]+"Var"+indexBrackets+".length]"
							+getArrayBrackets(dimension-2-i)+";\n";
				}
			}
			var += "return "+tokens[1]+";\n}\n";
		}
		else {
			var += "return "+tokens[1]+"Var.get();\n}\n";
		}

		//get the object type for primitives
		tokens[0] = getObjectTypeString(tokens[0]);
		
		var += "Value<"+tokens[0]+"> "+tokens[1]
				+"Var"+getArrayBrackets(dimension)+";\n";
		
		return var;
	}
	
	private String generateVars()
	{
		String[] sharedVars = myReader.getSharedVariables();
		String[] memberVars = myReader.getMemberVariables();
		String result;
		
		//gets the other contents, removing the shared keyword
		result = myReader.getOtherContent().replaceAll("(?<=^|}|;|\\s)(\\s*)shared ", "$1 ");
		
		for(String sharedVar : sharedVars)
		{
			result += System.lineSeparator()
					+ buildVariable(sharedVar);
		}
		
		for(String memberVar : memberVars)
		{
			result += System.lineSeparator()
					+ buildVariable(memberVar);
		}
		
		return result;
	}
	
	/**
	 * Given a name that ends with "Var", and an array dimension,
	 * copies the values stored in the variable with the same name without "Var"
	 */
	private String initializeValue(String name, int dimension)
	{
		String result = System.lineSeparator();
		String baseName = name.replace("Var", "");
		
		result += name+" = new Value";
		if(dimension < 1)
			result += "("+baseName+");";
		else
		{
			String index = "i"+dimension;
			
			result += "["+baseName+".length]"+getArrayBrackets(dimension-1)+";";
			result += System.lineSeparator()
					+ "for(int "+index+" = 0; "+index+" < "+name+".length; "+index+"++){";
			
			result += initializeValue(name+"["+index+"]", dimension-1)+"}";
		}
		
		return result;
	}
	
	private String modifyInitializeShared()
	{
		String[] sharedVars = myReader.getSharedVariables();
		String result = myReader.getInitializeShared();
		int dimension;
		
		for(String var: sharedVars)
		{
			dimension = var.length()-var.replace("[", "").length();
			var = var.split("[\\[\\]\\s]+")[1];
			var += "Var";
			
			result += initializeValue(var, dimension);
			result += System.lineSeparator()
					+ var+" = (Value"+getArrayBrackets(dimension)+")"
					+ "addSharedVariable(\""+var.replace("Var", "")+"\","
					+ "new Wrapper("+var+")).getVariable();";
		}
		
		return result;
	}
	
	private String modifyInitialize()
	{
		String[] memberVars = myReader.getMemberVariables();
		String result = myReader.getInitialize();
		int dimension;
		
		for(String var: memberVars)
		{
			dimension = var.length()-var.replace("[", "").length();
			var = var.split("[\\[\\]\\s]+")[1];
			var += "Var";
			
			result += initializeValue(var, dimension) + System.lineSeparator();
			result += "this.addVariable(\""+var.replace("Var", "")+"\", new Wrapper("+var+"));";
		}
		
		return result;
	}

	private boolean hasMixedOp(String code, String var) {
		//++ and += include both a read and a write, and also the order matters??
		//assignment operators have a fixed order, but I'd still rather deal with them later
		//there's probably some way to parse them, but I would rather not deal with it
		if(code.replaceAll(var+READ_REGEX+"\\s*"+MIXED_OP_REGEX,"")
				.length() < code.length())
			return true;
		return false;
	}
	
	/**
	 * Takes a normal algorithm and modifies it to work within the simulator.
	 * 
	 * Pause vars are any variables that should cause execution to pause
	 * 
	 * non pause vars are any variables that should not cause a pause, but
	 * should still be stored in a variable for display purposes.
	 * 
	 * Currently, the vars have to be just the name, not including the type
	 */
	private String modifyAlgorithm(String[] pauseVars, String[] pauseConverions,
								   String[] nonPauseVars, String[] nonPauseConversions)
	{
		String modifiedCode = myReader.getAlgorithm();
		//remove all comments
		//KNOWN BUG: if someone uses " /* " or " // " in a string, everything breaks
		//removes " /*__*/ " across multiple lines
		modifiedCode = modifiedCode.replaceAll(MULTI_COMMENT_REGEX, "");
		//removes " //___(newline)" and leaves "(newline)"
		modifiedCode = modifiedCode.replaceAll(COMMENT_REGEX, "$1");
		
		//replace each step. marks step numbers temporarily with "%d"
		//modifiedCode[index] = line.replaceAll("\\[(.+?)\\]", ".getVariable($1)");
		for(int i = 0; i < pauseVars.length; i++)
		{
			//KNOWN BUG: because replace all does not find overlapping matches,
			//if there is a nested read of the same variable, this breaks.
			//eg. turn[turn[0]]
			//also, nested array lookups in general will break, because regex can't match brackets

			//check for mixed ops
			if(hasMixedOp(modifiedCode, pauseVars[i]))
				throw new IllegalArgumentException(
						"++, --, += and other special assignments on shared vars cannot be parsed,\n"+
								"because they implicitly involve both a read and a write.\n"+
								"Instead, please use separate operations. e.g.:\n"+
								"\tnumber = number + 1;");

			//replace all variable writes
			//matches: variable[optional index] =
			//				expression(does not contain equals) (semicolon or "){")
			modifiedCode = modifiedCode.replaceAll(
					VAR_SYMBOL_REGEX +pauseVars[i]+WRITE_REGEX,
					"pauseWrite\\(%d,"+pauseVars[i]+"Var$1,$3\\)$4");
			
			//replace all variable reads. makes sure to avoid writes already marked with Var
			//by using negative lookahead
			modifiedCode = modifiedCode.replaceAll(
					VAR_SYMBOL_REGEX +pauseVars[i]+READ_REGEX,
					"pauseRead\\(%d,"+pauseVars[i]+"Var$1\\)"+pauseConverions[i]);
		}
		for(int i = 0; i < nonPauseVars.length; i++)
		{
			//check for mixed ops
			if(hasMixedOp(modifiedCode, nonPauseVars[i]))
				throw new IllegalArgumentException(
						"++, --, += and other special assignments on member vars cannot be parsed,\n"+
								"because they implicitly involve both a read and a write.\n"+
								"Instead, please use separate operations. e.g.:\n"+
								"\tnumber = number + 1;");
			//replace all variable writes
			modifiedCode = modifiedCode.replaceAll(
					VAR_SYMBOL_REGEX +nonPauseVars[i]+WRITE_REGEX,
					nonPauseVars[i]+"Var$1\\.set\\($3\\)$4");
			
			//replace all variable reads.
			modifiedCode = modifiedCode.replaceAll(
					VAR_SYMBOL_REGEX +nonPauseVars[i]+READ_REGEX,
					nonPauseVars[i]+"Var$1\\.get\\(\\)"+nonPauseConversions[i]);
		}
		modifiedCode = modifiedCode.replaceAll(
				"CriticalSection\\s*\\(\\s*(\\d+)\\s*\\)",
				"CriticalSection(%d, $1)");
		modifiedCode = modifiedCode.replaceAll(
				"CriticalSection\\s*\\(\\s*\\)",
				"CriticalSection(%d)");
		
		String previousCode = "";
		
		for(int i = 0; !previousCode.equals(modifiedCode); i++)
		{
			previousCode = modifiedCode;
			modifiedCode = modifiedCode.replaceFirst("%d", ""+i);
		}
		
		return modifiedCode;
	}
	
	private static String getStepString(String line, int start, String var)
	{
		//strip comments
		line = line.split(COMMENT_REGEX)[0];
		//strips everything before the start point
		line = line.substring(start);
		//strip trailing close parenthesis, like in if/for statements
		//semicolons are guaranteed to end a statement
		line = line.split(END_REGEX)[0];
		//checks if the line starts with the variable, (possibly with an index)
		//followed by a single equals (no boolean operator). This separates
		//read operations from write operations
		if(line.matches("^"+var+"(\\[.+?])*\\s*=[^=].+"))
		{
			//needs no additional processing?
		}
		else
		{
			int openingBrackets = 0;
			for(int i = var.length(); i < line.length(); i++)
			{
				if(openingBrackets == 0 && line.substring(i).matches("^"+SEPARATOR_REGEX+".*"))
					//removes any remaining characters and ends the loop
					line = line.substring(0, i);
				else if(line.charAt(i) == '[')
					openingBrackets++;
				else if(line.charAt(i) == ']')
				{
					openingBrackets--;
					if(openingBrackets < 0)
						//removes any remaining characters and ends the loop
						line = line.substring(0, i);
				}
			}
			line = line.trim();
			
			//needs to be split on operators, because both operands are read separately
			//line = line.split(SEPARATOR_REGEX)[0];
			
			//add something to do with a bracket matching stack?
			//only end the token if the stack is currently empty and an end character is seen
			//or, if we reach the end of the line and there are still unclosed brackets
			//	as with multi-line arguments
			
			//if(line.indexOf('[') == -1)
			//	line = line.split("\\]")[0];
			//System.out.println("\t"+line);
		}
		return line;
	}
}
