package mutex.editor.model;

public class ExampleCode {
    public static final String EXAMPLE_1 =
            "class Example1"								+System.lineSeparator()
                    + "{"												+System.lineSeparator()
                    + "	// Shared variables are marked by the \"shared\" keyword."+System.lineSeparator()
                    + "	// They can be read or written atomically by any thread"+System.lineSeparator()
                    + "	shared int index = 0;"							+System.lineSeparator()
                    + "	shared int count = 0;"							+System.lineSeparator()
                    + "	"												+System.lineSeparator()
                    + "	// If an array is shared, it is treated as if each"+System.lineSeparator()
                    + "	// element in the array is an atomic variable."+System.lineSeparator()
                    + "	shared int[] array = new int[N];"				+System.lineSeparator()
                    + "	"												+System.lineSeparator()
                    + "	public void algorithm()"						+System.lineSeparator()
                    + "	{"												+System.lineSeparator()
                    + "		// \"N\" is a special variable that stores" +System.lineSeparator()
                    + "		// The current number of threads in the simulation"+System.lineSeparator()
                    + "		// N can be changed under the \"Settings\" tab"+System.lineSeparator()
                    + "		if( index == N )" 							+System.lineSeparator()
                    + "		{" 											+System.lineSeparator()
                    + "			// \"ID\" is a special variable that stores"+System.lineSeparator()
                    + "			// The current thread's ID. Thread IDs are"+System.lineSeparator()
                    + "			// numbers from 0 to N-1 inclusive"+System.lineSeparator()
                    + "			index = ID;" 							+System.lineSeparator()
                    + "		}" 											+System.lineSeparator()
                    + "		" 											+System.lineSeparator()
                    + "		// Shared variables also change the flow of execution"+System.lineSeparator()
                    + "		// Whenever a thread is about to read or write "+System.lineSeparator()
                    + "		// a shared variable, it will pause, to more"+System.lineSeparator()
                    + "		// clearly demonstrate the race conditions"+System.lineSeparator()
                    + "		" 											+System.lineSeparator()
                    + "		// For example," 							+System.lineSeparator()
                    + "		// The read and write are separate operations,"+System.lineSeparator()
                    + "		// so threads will run into race conditions"+System.lineSeparator()
                    + "		count = count + 1;"							+System.lineSeparator()
                    + "		"											+System.lineSeparator()
                    + "		// First, the array indices are read"	+System.lineSeparator()
                    + "		// The left side count is read and the index calculated"+System.lineSeparator()
                    + "		// The count is read again for the right side"+System.lineSeparator()
                    + "		// Then the right side array value is read"	+System.lineSeparator()
                    + "		// Then the array value is written"			+System.lineSeparator()
                    + "		array[count % N] = array[count % N] + 1;"	+System.lineSeparator()
                    + "		" 											+System.lineSeparator()
                    + "		if( index == ID )" 							+System.lineSeparator()
                    + "		{"											+System.lineSeparator()
                    + "			array[ID] = array[ID] + 1;" 			+System.lineSeparator()
                    + "			" 										+System.lineSeparator()
                    + "			// Due to race conditions, this algorithm"+System.lineSeparator()
                    + "			// does not provide mutual exclusion" 	+System.lineSeparator()
                    + "			CriticalSection();" 					+System.lineSeparator()
                    + "			" 										+System.lineSeparator()
                    + "			index = N;" 							+System.lineSeparator()
                    + "		}" 											+System.lineSeparator()
                    + "	}"												+System.lineSeparator()
                    + "}";

    public static final String EXAMPLE_2 =
            "class Example2"									+System.lineSeparator()
                    + "{"												+System.lineSeparator()
                    + "	// Variables can be linked to a display"		+System.lineSeparator()
                    + "	// and influence how the display acts."			+System.lineSeparator()
                    + "	"												+System.lineSeparator()
                    + "	// Different displays require different variables."	+System.lineSeparator()
                    + "	// The display type can be changed under the \"Settings\" tab."	+System.lineSeparator()
                    + "	// The currently linked variables can be seen"	+System.lineSeparator()
                    + "	// under the \"Display\" tab"					+System.lineSeparator()
                    + "	"												+System.lineSeparator()
                    + "	// These are the default linked variables for"	+System.lineSeparator()
                    + "	// the \"Turn Wheel\" display type"				+System.lineSeparator()
                    + "	shared int turn = N;"							+System.lineSeparator()
                    + "	shared boolean[] intent = new boolean[N];"		+System.lineSeparator()
                    + "	shared boolean[] passed = new boolean[N];"		+System.lineSeparator()
                    + " "
                    + "	"												+System.lineSeparator()
                    + "	public void algorithm()"						+System.lineSeparator()
                    + "	{"												+System.lineSeparator()
                    + "		intent[ID] = true;"							+System.lineSeparator()
                    + "		turn = ID;"									+System.lineSeparator()
                    + "		passed[ID] = true;"							+System.lineSeparator()
                    + "		"											+System.lineSeparator()
                    + "		CriticalSection();"							+System.lineSeparator()
                    + "		"											+System.lineSeparator()
                    + "		passed[ID] = false;"						+System.lineSeparator()
                    + "		turn = N;"									+System.lineSeparator()
                    + "		intent[ID] = false;"						+System.lineSeparator()
                    + "	}"												+System.lineSeparator()
                    + "}";

    public static final String EXAMPLE_3 =
            "class Example3"								+System.lineSeparator()
                    + "{"												+System.lineSeparator()
                    + "	shared int[][] bracket;"						+System.lineSeparator()
                    + "	"												+System.lineSeparator()
                    + "	boolean even;"									+System.lineSeparator()
                    + "	"												+System.lineSeparator()
                    + "	// this method is run once before execution begins"	+System.lineSeparator()
                    + "	// it is meant for initializations that take"	+System.lineSeparator()
                    + "	// more than one line"							+System.lineSeparator()
                    + "	public void initializeShared()"					+System.lineSeparator()
                    + "	{"												+System.lineSeparator()
                    + "		bracket = new int[N][];"					+System.lineSeparator()
                    + "		for(int i = 0; i < N; i++) {"				+System.lineSeparator()
                    + "			bracket[i] = new int[N-i];"				+System.lineSeparator()
                    + "		}"											+System.lineSeparator()
                    + "	}"												+System.lineSeparator()
                    + "	"												+System.lineSeparator()
                    + "	// this method is run once by each thread"		+System.lineSeparator()
                    + "	// before execution begins"						+System.lineSeparator()
                    + "	public void initialize()"						+System.lineSeparator()
                    + "	{"												+System.lineSeparator()
                    + "		even = (ID % 2 == 0);"						+System.lineSeparator()
                    + "	}"												+System.lineSeparator()
                    + "	"												+System.lineSeparator()
                    + "	//The algorithm method is run in a loop by all threads"+System.lineSeparator()
                    + "	public void algorithm()"						+System.lineSeparator()
                    + "	{"												+System.lineSeparator()
                    + "		bracket[ID][N - ID - 1] = ID;"				+System.lineSeparator()
                    + "		"											+System.lineSeparator()
                    + "		// The CriticalSection method moves the thread"+System.lineSeparator()
                    + "		// to the CS area of the display"			+System.lineSeparator()
                    + "		CriticalSection();"							+System.lineSeparator()
                    + "	}"												+System.lineSeparator()
                    + "}";

    public static final String KNUTH_EXAMPLE = "/**"+System.lineSeparator()+
            " * All the information required for the current algorithm."+System.lineSeparator()+
            " * Everything here is specific to the algorithm being run."+System.lineSeparator()+
            " *"+System.lineSeparator()+
            " * @author Ryley Jewsbury"+System.lineSeparator()+
            " */"+System.lineSeparator()+
            "public class Knuth"+System.lineSeparator()+
            "{"+System.lineSeparator()+
            "	public shared boolean[] intent = new boolean[N];"+System.lineSeparator()+
            "	shared boolean[] passed;"+System.lineSeparator()+
            "	shared int turn;"+System.lineSeparator()+
            "	int j;"+System.lineSeparator()+
            ""+System.lineSeparator()+
            "	void initializeShared()"+System.lineSeparator()+
            "	{"+System.lineSeparator()+
            "		passed = new boolean[N];"+System.lineSeparator()+
            "	}"+System.lineSeparator()+
            "	/*testing other content*/"+
            "	void algorithm()"+System.lineSeparator()+
            "	{"+System.lineSeparator()+
            "		intent[ID] = true;"+System.lineSeparator()+
            "        entry:"+System.lineSeparator()+
            "        while(true)"+System.lineSeparator()+
            "        {"+System.lineSeparator()+
            "            waitTurn:"+System.lineSeparator()+
            "            while(true)"+System.lineSeparator()+
            "            {"+System.lineSeparator()+
            "                for(j = turn; j!=ID; j = (j+1)%N)"+System.lineSeparator()+
            "                {"+System.lineSeparator()+
            "                    if(intent[j])"+System.lineSeparator()+
            "                    {"+System.lineSeparator()+
            "                        //goto waitTurn"+System.lineSeparator()+
            "                        continue waitTurn;"+System.lineSeparator()+
            "                    }"+System.lineSeparator()+
            "                }"+System.lineSeparator()+
            "                break;"+System.lineSeparator()+
            "            }"+System.lineSeparator()+
            "            passed[ID] = true;"+System.lineSeparator()+
            "            for(j=0; j<N; j = j + 1)"+System.lineSeparator()+
            "            {"+System.lineSeparator()+
            "                if(passed[j] && j != ID)"+System.lineSeparator()+
            "                {"+System.lineSeparator()+
            "                    passed[ID] = false;"+System.lineSeparator()+
            "                    continue entry;"+System.lineSeparator()+
            "                }"+System.lineSeparator()+
            "            }"+System.lineSeparator()+
            "            break;"+System.lineSeparator()+
            "        }"+System.lineSeparator()+
            "        CriticalSection();"+System.lineSeparator()+
            "        turn = (ID+1)%N;"+System.lineSeparator()+
            "        passed[ID] = false;"+System.lineSeparator()+
            "        intent[ID] = false;"+System.lineSeparator()+
            "	}"+System.lineSeparator()+
            "}";
}
