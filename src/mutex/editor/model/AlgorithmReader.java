package mutex.editor.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AlgorithmReader
{
	//matches a class / method signature
	public static final String CLASS_REGEX = "((public|final)\\s+)*class\\s+(\\S+?)(\\s+|\\{)";
	public static final String SUPER_REGEX = "extends\\s+(\\S+?)(\\s+|\\{)";
	public static final String IMPLEMENT_REGEX = "implements\\s+(\\S+\\s*,\\s*)*\\S+\\s*(?=\\{)";
	public static final String INIT_REGEX = "void\\s+initialize\\s*\\(\\s*\\)\\s*(?=\\{)";
	public static final String INIT_SHARED_REGEX = "void\\s+initializeShared\\s*\\(\\s*\\)\\s*(?=\\{)";
	public static final String ALGORITHM_REGEX = "void\\s+algorithm\\s*\\(\\s*\\)\\s*(?=\\{)";

	//matches " /*__*/ " across multiple lines
	private static final String MULTI_COMMENT_REGEX = "/\\*.*?\\*/";
	//matches " //___(newline)"
	private static final String COMMENT_REGEX = "//.*?(\\R)";

	//matches java keywords that can come before a method
	public static final String KEYWORD = "((public|private|protected|final|static|synchronized|transient|volatile|native|strictfp)\\s+)";

	public static final String TAB_REPLACEMENT = "    ";
	
	public static String replaceTabs(String source)
	{
		//this method does not properly account for
		//unaligned tabs, that do not use a full 4 spaces
		return source.replaceAll("\\t", TAB_REPLACEMENT);
	}
	
	public static String removeBlankLines(String source)
	{
		return source.replaceAll("(\\R)\\s*\\R", "$1");
	}
	
//-----------------------------
	
	//entire code
	private String myCode;
	
	//sections
	private String myImports;
	private String myClassName;
	private String mySuperclassName;
	private String[] myImplements;
	private String myInitialize;
	private String myInitializeShared;
	private String myAlgorithm;
	private String myOtherContent;

	//variables
	private String[] mySharedVars;
	private String[] myMemberVars;
	
	public AlgorithmReader(String code)
	{
		if(code == null)
			myCode = "";
		else
			myCode = replaceTabs(code);
	}

	public String getCode() {
		return myCode;
	}

	public String getImports()
	{
		if(myImports == null){
			//the first line up to the class declaration should contain imports
			//KNOWN BEHAVIOUR: ABSTRACT CLASSES ARE NOT ALLOWED
			Pattern className = Pattern.compile(CLASS_REGEX);
			Matcher match = className.matcher(myCode);
			if(!match.find())
				throw new RuntimeException("Illegal Syntax: missing class name.");

			myImports = myCode.substring(0, match.start());
		}
		return removeBlankLines(myImports);
	}
	
	public String getClassName()
	{
		if(myClassName == null){
			Pattern className = Pattern.compile(CLASS_REGEX);
			Matcher match = className.matcher(myCode);
			if(!match.find())
				throw new RuntimeException("Illegal Syntax: missing class name.");

			//the class name is the word that follows "class"
			myClassName = match.group().split("class\\s+",2)[1].split("(\\s+|\\{)",2)[0];
		}
		return myClassName;
	}

	public String getFileName(){
		return getClassName()+".java";
	}

	public String getSuperclassName()
	{
		if(mySuperclassName == null){
			Pattern superclass = Pattern.compile(getClassName()+"\\s+"+SUPER_REGEX);
			Matcher match = superclass.matcher(myCode);
			if(!match.find())
				mySuperclassName = "";
			else {
				mySuperclassName = match.group().split("extends\\s+",2)[1].split("(\\s+|\\{)",2)[0];
			}
		}
		return mySuperclassName;
	}

	public String[] getImplements()
	{
		if(myImplements == null){
			Pattern implementNames = Pattern.compile(getClassName()+"\\s+(extends\\s+\\S+\\s+)?"+IMPLEMENT_REGEX);
			Matcher match = implementNames.matcher(myCode);
			if(!match.find())
				myImplements = new String[0];
			else{
				myImplements = match.group().split("implements\\s+",2)[1].trim().split("\\s*,\\s*");
			}
		}
		return myImplements;
	}

	private String getPotentialVariables()
	{
		String content = getOtherContent();
		//remove comments
		content = content.replaceAll(COMMENT_REGEX,"").replaceAll(MULTI_COMMENT_REGEX, "");

		//remove method bodies
		int start, end;
		start = content.indexOf('{');
		while(start != -1)
		{
			end = indexOfMatchingBracket(content, start);
			if(end == -1)
				content = content.substring(0, start);
			else
				//leaves the closing brackets so they can be used in the next step
				content = content.substring(0, start+1)+content.substring(end);
			start = content.indexOf('{',start+1);
		}

		return content;
	}

	/**
	 * Shared variables are marked with "shared"
	 * Returns an array of "Type varName" strings
	 *
	 * TODO: Allow comma separated declarations, e.g., int x=5,y=3;
	 */
	public String[] getSharedVariables()
	{
		if(mySharedVars == null)
		{
			ArrayList<String> sharedVars = new ArrayList<>();
			String content = getPotentialVariables();

			//matches a variable type and name, preceded by keywords including shared, not followed by a (
			Pattern sharedPattern = Pattern.compile(
					"(?<=^|}|;)\\s*"+KEYWORD+"*shared\\s+"+KEYWORD+"*\\S+?[\\[\\]\\s]+[^=\\(;\\s]+[\\[\\]\\s]*(?=;|=)");
			Matcher sharedMatcher = sharedPattern.matcher(content);

			while(sharedMatcher.find()){
				sharedVars.add(sharedMatcher.group().replaceFirst("\\s*"+KEYWORD+"*shared\\s+"+KEYWORD+"*", ""));
			}

			mySharedVars = new String[sharedVars.size()];
			sharedVars.toArray(mySharedVars);
		}

		//deep copy so they can't be messed with
		return Arrays.copyOf(mySharedVars,mySharedVars.length);
	}

	/**
	 * Member variables are non-static
	 * Returns an array of "Type varName" strings
	 *
	 * TODO: Allow comma separated declarations, e.g., int x=5,y=3;
	 */
	public String[] getMemberVariables()
	{
		if(myMemberVars == null)
		{
			ArrayList<String> memberVars = new ArrayList<>();
			String content = getPotentialVariables();

			//matches a variable type and name, preceded by keywords, not followed by a (
			Pattern memberPattern = Pattern.compile(
					"(?<=^|}|;)\\s*"+KEYWORD+"*\\S+?[\\[\\]\\s]+[^=\\(;\\s]+[\\[\\]\\s]*(?=;|=)");
			Matcher memberMatcher = memberPattern.matcher(content);


			while(memberMatcher.find()){
				memberVars.add(memberMatcher.group().replaceFirst("\\s*"+KEYWORD+"*", ""));
			}

			myMemberVars = new String[memberVars.size()];
			memberVars.toArray(myMemberVars);
		}

		//deep copy so they can't be messed with
		return Arrays.copyOf(myMemberVars, myMemberVars.length);
	}

	/**
	 * Gets the contents of the method named initializeShared()
	 */
	public String getInitializeShared()
	{
		if(myInitializeShared == null){
			Pattern initializeShared = Pattern.compile(INIT_SHARED_REGEX);
			Matcher match = initializeShared.matcher(myCode);
			if(!match.find()){
				myInitializeShared = "";
				return myInitializeShared;
			}
			int endIndex = indexOfMatchingBracket(myCode, match.end());

			if(endIndex == -1){
				myInitializeShared = "";
				return myInitializeShared;
			}

			myInitializeShared = myCode.substring(match.end()+1, endIndex);
			//to differentiate between an empty method and no method,
			//empty will be replaced with whitespace
			if(myInitializeShared.equals(""))
				myInitializeShared = " ";
		}
		return myInitializeShared;
	}

	/**
	 * gets the contents of the method named initialize()
	 */
	public String getInitialize()
	{
		if(myInitialize == null){
			Pattern initialize = Pattern.compile(INIT_REGEX);
			Matcher match = initialize.matcher(myCode);
			if(!match.find()){
				myInitialize = "";
				return myInitialize;
			}
			int endIndex = indexOfMatchingBracket(myCode, match.end());

			if(endIndex == -1){
				myInitialize = "";
				return myInitialize;
			}

			myInitialize = myCode.substring(match.end()+1, endIndex);
			//to differentiate between an empty method and no method,
			//empty will be replaced with whitespace
			if(myInitialize.equals(""))
				myInitialize = " ";
		}
		return myInitialize;
	}

	/**
	 * gets the contents of the method named algorithm()
	 */
	public String getAlgorithm()
	{
		if(myAlgorithm == null){
			Pattern algorithm = Pattern.compile(ALGORITHM_REGEX);
			Matcher match = algorithm.matcher(myCode);
			if(!match.find())
				return "";
			int endIndex = indexOfMatchingBracket(myCode, match.end());

			if(endIndex == -1)
				return "";

			myAlgorithm = myCode.substring(match.end()+1, endIndex);

			//remove whitespace after newlines, but keep indentation
			//TODO: FIX THIS SO IT WORKS FOR ARBITRARY WHITESPACE
			myAlgorithm = myAlgorithm.replaceAll("(\\R)( {8}|\t\t)", "$1");
		}
		return myAlgorithm;
	}

	/**
	 * Takes a regex of a method declaration, and removes that method, along
	 * with its body, from the given source String.
	 */
	private String removeMethod(String methodRegex, String source){
		int endIndex;

		//ensures the regex ends in a curly brace
		Pattern pattern = Pattern.compile(KEYWORD+"*"+methodRegex+"\\s*?(?=\\{)");
		Matcher match = pattern.matcher(source);
		if(match.find()){
			endIndex = indexOfMatchingBracket(source, match.end());
			if(endIndex != -1)
				source = source.substring(0,match.start())
						+source.substring(endIndex+1);
		}

		return source;
	}

	public String getOtherContent()
	{
		if(myOtherContent == null){
			int endIndex = indexOfMatchingBracket(myCode, myCode.indexOf('{'));

			if(endIndex == -1)
				return "";

			myOtherContent = myCode.substring(myCode.indexOf('{')+1, endIndex);

			//REMOVE algorithm, initialize, and initializeShared
			myOtherContent = removeMethod(ALGORITHM_REGEX, myOtherContent);
			myOtherContent = removeMethod(INIT_REGEX, myOtherContent);
			myOtherContent = removeMethod(INIT_SHARED_REGEX, myOtherContent);
		}

		//everything else is "other" content
		return removeBlankLines(myOtherContent);
	}

	/**
	 * given a string and the index of an opening bracket,
	 * finds the index of a matching closing bracket
	 */
	private int indexOfMatchingBracket(String text, int indexOfOpeningBracket)
	{
		//TODO: KNOWN BUG: THIS BREAKS IF THERE ARE BRACKETS IN COMMENTS. FOR EXAMPLE {}{{{}}}{}{}{{}{}
		
		int closingIndex = 0;
		int i = indexOfOpeningBracket;
		int nextOpen;
		int nextClose;
		int depth = 0;
		while(closingIndex == 0)
		{
			nextOpen = text.indexOf('{',i);
			nextClose = text.indexOf('}', i);
			
			if(nextClose == -1)
				closingIndex = -1;
			else if(nextOpen == -1 || nextClose < nextOpen)
			{
				depth--;
				i = nextClose+1;
				if(depth < 1)
					closingIndex = nextClose;
			}
			else //nextOpen <= nextClose
			{
				depth++;
				i = nextOpen+1;
			}
		}
		return closingIndex;
	}

	public static void main(String[] args)
	{
		AlgorithmReader r = new AlgorithmReader(
				"import stuff;"
				+ "public class Foo{//hello!\n"
				+ "public int[] thing[]= {{1,2}, {3,2}};\n"
				+ "shared private boolean foobar;public void otherStuff2(){int thing; boolean stuff;}\n"
				+ "public void algorithm(){//this is some stuff\n"
				+ "doStuff();}private shared static int thing [][];public void otherStuff(){//method comment\nchar hi;}}");
		System.out.println(r.getPotentialVariables());
		System.out.println("-----");
		for(String shared: r.getSharedVariables())
			System.out.println('>'+shared);
		System.out.println("-----");
		for(String member: r.getMemberVariables())
			System.out.println('>'+member);
	}
}
