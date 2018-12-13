package mutex.editor.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DisplayReader {
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

    public DisplayReader(String code)
    {
        if(code == null)
            myCode = "";
        else
            myCode = replaceTabs(code);
    }

    public String getCode() {
        return myCode;
    }

    public String getClassName()
    {
        if(myClassName == null){
            Pattern className = Pattern.compile(CLASS_REGEX);
            Matcher match = className.matcher(myCode);
            if(!match.find())
                throw new RuntimeException("Illegal Syntax: missing a display class name.");

            //the class name is the word that follows "class"
            myClassName = match.group().split("class\\s+",2)[1].split("(\\s+|\\{)",2)[0];
        }
        return myClassName;
    }

    public String getFileName()
    {
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

    //creates code that *should* compile if things were written correctly
    public String getCompilableSource()
    {
        if(!getSuperclassName().equals("CustomDisplay"))
            throw new RuntimeException("Illegal Syntax: the display must extend CustomDisplay");

        String code = myCode;

        //insert the required import for CustomDisplay
        code = "import mutex.display.controllers.CustomDisplay;\n"+code;

        //insert the constructor
        String insert =
                "public "+getClassName()+"(int threads){super(threads);}";

        code = code.substring(0, code.indexOf('{')+1)+
                insert +
                code.substring(code.indexOf('{')+1);

        return code;
    }
}
