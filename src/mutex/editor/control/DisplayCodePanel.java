package mutex.editor.control;

import java.awt.event.ActionEvent;

public class DisplayCodePanel extends CodePanel {

    public static final String NEW_TEMPLATE =
            "import mutex.display.elements.*;\n" +
            "import mutex.simulator.model.AlgorithmThread;\n" +
            "import java.util.Arrays;\n" +
            "import java.util.List;\n" +
            "\n" +
            "public class DisplayName extends CustomDisplay\n" +
            "{\n" +
            "	private ThreadBall[] myThreads;\n" +
            "	private NonCriticalSection ncs;\n" +
            "	private CriticalSection cs;\n" +
            "\n" +
            "	public void initializeDisplay(int numThreads)\n" +
            "	{\n" +
            "		//set initial conditions for view elements\n" +
            "		cs = new CriticalSection(80);\n" +
            "		ncs = new NonCriticalSection(numThreads, 80, 450);\n" +
            "\n" +
            "		cs.setX(50);\n" +
            "		cs.setY(50);\n" +
            "\n" +
            "		ncs.setX(150);\n" +
            "		ncs.setY(50);\n" +
            "	}\n" +
            "\n" +
            "	public void setNumThreads(int n)\n" +
            "	{\n" +
            "		//resize or add/remove view components to handle n threads\n" +
            "		clearThreads();\n" +
            "\n" +
            "		ncs.setSize(n);\n" +
            "	}\n" +
            "\n" +
            "	public void addThreads(AlgorithmThread[] threads)\n" +
            "	{\n" +
            "		// add the representations for the threads into the display\n" +
            "		setNumThreads(threads.length);\n" +
            "\n" +
            "		myThreads = new ThreadBall[threads.length];\n" +
            "\n" +
            "		for(int i=0; i<threads.length; i++)\n" +
            "			myThreads[i] = new ThreadBall(\n" +
            "					threads[i].getID(),\n" +
            "					threads[i].getPreferredColor());\n" +
            "\n" +
            "		for(int i=0; i<threads.length; i++)\n" +
            "			update(threads[i]);\n" +
            "	}\n" +
            "	\n" +
            "	public void clearThreads()\n" +
            "	{\n" +
            "		// remove the current thread representations from the display\n" +
            "		if(myThreads == null)\n" +
            "			return;\n" +
            "\n" +
            "		for(ThreadBall ball: myThreads)\n" +
            "		{\n" +
            "			ball.leaveContainer();\n" +
            "		}\n" +
            "		myThreads = null;\n" +
            "	}\n" +
            "	\n" +
            "	public List<ViewElement> getElements()\n" +
            "	{\n" +
            "		// creates a list of the elements to be drawn on screen\n" +
            "		// myThreads are not included so that threads are only\n" +
            "		// displayed when they are in a container\n" +
            "		return Arrays.asList(ncs, cs);\n" +
            "	}\n" +
            "	\n" +
            "	public void update(AlgorithmThread updated)\n" +
            "	{\n"+
            "		// casting allows the thread to access the auto-generated getters.\n"+
            "		// e.g. \"shared int var\" generates \"thread.getVar()\"\n"+
            "		AlgorithmName thread = (AlgorithmName) updated;\n"+
            "		int ID = thread.getID();\n"+
            "		boolean inCritical = thread.isInCritical();\n"+
            "\n" +
            "		if(!inCritical)\n"+
            "		{"+
            "			//move the thread to the front of the NCS list\n" +
            "			myThreads[ID].setContainer(ncs);\n" +
            "			ncs.addFirst(myThreads[ID]);\n" +
            "		}\n" +
            "		else if(!myThreads[ID].inContainer(cs))\n" +
            "		{\n" +
            "			//move the thread to the front of the CS list\n" +
            "			myThreads[ID].setContainer(cs);\n" +
            "			cs.addFirst(myThreads[ID]);\n" +
            "		}\n" +
            "	}\n" +
            "}";

    DisplayCodePanel() {
        setText(NEW_TEMPLATE);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        super.actionPerformed(actionEvent);
        //currently no display actions
    }
}
