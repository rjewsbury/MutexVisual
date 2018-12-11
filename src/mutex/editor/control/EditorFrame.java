package mutex.editor.control;

import mutex.display.DisplayType;
import mutex.display.controllers.*;
import mutex.editor.model.*;
import mutex.io.PopupManager;
import mutex.io.SourceFile;
import mutex.io.SourceFileListener;
import mutex.simulator.control.SimulationParameters;
import mutex.simulator.control.SimulatorFrame;
import mutex.simulator.view.DisplayPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class EditorFrame extends JFrame implements ActionListener, SourceFileListener, ChangeListener
{
	public static final Dimension PREFERRED_SIZE = new Dimension(1100, 700);
	public static final Dimension MIN_SIZE = new Dimension(400,400);
	private static final int DEFAULT_THREAD_NUM = 4;
	public static final String COMPILE_BUTTON = "Compile & Run";
	public static final String COMPILE_DISPLAY_BUTTON = "Compile Display";
	public static final String UNDO_BUTTON = "Undo (Ctrl+Z)";
	public static final String REDO_BUTTON = "Redo (Ctrl+Y)";

	public static final String ALGORITHM_TAB = "Algorithm";
	public static final String SETTINGS_TAB = "Settings";
	public static final String DISPLAY_TAB = "Display";

	//COMPONENTS
	private CodePanel myCode;

	private PropertyEditor myControl;
	private JPanel myControlContainer;

	private CodePanel myDisplayCode;
	private JScrollPane myDisplayCodeScroll;
	private JButton myDisplayButton;

	private DisplayPanel myDisplay;
	private JTabbedPane myTabs;

	//model
	private ControlParameters myParameters;
	
	public EditorFrame()
	{
		//setting up the frame
		setTitle("Concurrency Visual Tool");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(PREFERRED_SIZE);
		setMinimumSize(MIN_SIZE);
		//resizing things is terrible to deal with anyway
		//setResizable(false);
		
		//creating model
		
		//creating controls
		myParameters = new ControlParameters();
		myParameters.setNumThreads(DEFAULT_THREAD_NUM);
		myParameters.setController(new DefaultDisplay(myParameters.getNumThreads()));
		
		myControl = new PropertyEditor("No Parameters");

		//creating display components
		//mySelector = new ElementSelector();
		myDisplay = new DisplayPanel(myParameters.getController());
		myCode = new AlgorithmCodePanel();
		myDisplayCode = new DisplayCodePanel();
		myDisplayButton = new JButton(COMPILE_DISPLAY_BUTTON);
		myDisplayButton.addActionListener(this);
		
		myControl.setEditable(myParameters);
		myControl.addChangeListener(this);

		myControlContainer = new JPanel();
		myControlContainer.setLayout(new BorderLayout());
		myControlContainer.add(myControl, BorderLayout.NORTH);

		myTabs = new JTabbedPane();
		myTabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		JScrollPane codeScroll = new JScrollPane(myCode);
		myDisplayCodeScroll = new JScrollPane(myDisplayCode);
		JScrollPane displayScroll = new JScrollPane(myDisplay);
		//JScrollPane selectorScroll = new JScrollPane(mySelector);
		myTabs.addTab(ALGORITHM_TAB,codeScroll);
		myTabs.addTab(SETTINGS_TAB, myControlContainer);
		//controlTabs.addTab("Elements",selectorScroll);
		JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,myTabs,displayScroll);

		//connect the menu bar
		EditorMenuBar menuBar = new EditorMenuBar();
		menuBar.addSourceFileListener(this);
		menuBar.addActionListener(this);
		menuBar.addActionListener(myCode);
		menuBar.addActionListener(myDisplayCode);

		//putting the components together
		add(mainSplit);
		setJMenuBar(menuBar);
		
		setVisible(true);
		
		//maybe don't include this?
		pack();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(COMPILE_BUTTON.equals(e.getActionCommand()))
			compileAndRun();
		else if(COMPILE_DISPLAY_BUTTON.equals(e.getActionCommand()))
			compileDisplay();
		else if(UNDO_BUTTON.equals(e.getActionCommand())) {
			// currently I dont have the code scroll as a member var,
			// but I know that the code tab will always be first
			if(myTabs.getSelectedIndex() == 0)
				myCode.undo();
			else if(myTabs.getSelectedComponent() == myDisplayCodeScroll)
				myDisplayCode.undo();
		}
		else if(REDO_BUTTON.equals(e.getActionCommand())) {
			if(myTabs.getSelectedIndex() == 0)
				myCode.redo();
			else if(myTabs.getSelectedComponent() == myDisplayCodeScroll)
				myDisplayCode.redo();
		}
	}

	private AlgorithmClassLoader getAlgorithmClassLoader() {
		//gather all the data required for compilation
		AlgorithmReader reader = new AlgorithmReader(myCode.getText());
		DisplayReader displayReader = new DisplayReader(myDisplayCode.getText());
		AlgorithmWriter writer = new AlgorithmWriter(reader);
		AlgorithmClassLoader loader = null;
		String source;
		String displaySource;

		try{
			//try with resource ensures the compiler is closed
			//not sure if I need nested trys to catch the exceptions properly
			try(AlgorithmCompiler compiler = new AlgorithmCompiler()) {
//				System.out.println(writer.buildAlgorithmThread());
//				System.out.println("-----------------------");
//				System.out.println(reader.getCompilableSource());
//				System.out.println(displayReader.getCompilableSource());

				//first, prove that their source compiles before modification
				HashMap<String, String> sources = new HashMap<>();
				try {
					sources.put(reader.getFileName(), writer.getCompilableSource());
					if(myParameters.getDisplayType() == DisplayType.CUSTOM) {
						sources.put(displayReader.getFileName(), displayReader.getCompilableSource());
					}
				} catch (RuntimeException _e) {
					_e.printStackTrace();
					PopupManager.errorMessage(_e.getMessage());
					return null;
				}

				if(!compiler.compile(sources))
				{
					return null;
				}

				//modify the data to create source files
				try{
					source = writer.buildAlgorithmThread();
				}catch(RuntimeException e1){
					PopupManager.errorMessage(
							"There was a problem modifying the source code:"
									+System.lineSeparator()+e1.getMessage());
					return null;
				}
				displaySource = displayReader.getCompilableSource();

//				System.out.println(source);
//				CodeConstants constants = writer.buildConstants();
//				for (int i = 0; i < constants.getMaxSteps(); i++) {
//					System.out.println(constants.getStep(i));
//				}

				sources = new HashMap<>();

				sources.put(reader.getFileName(),source);

				//if a custom display is being used, add the display to the compile list
				if(myParameters.getDisplayType() == DisplayType.CUSTOM) {

					sources.put(displayReader.getFileName(), displaySource);
				}

				//compile the source files into byte[]'s
				if(compiler.compile(sources))
				{
					//load the byte[]'s into class files
					loader = new AlgorithmClassLoader(compiler.getCompiledBytes());
				}
				return loader;
			}
		} catch (Exception _e) {
			//the program is not being run with the jdk
			//2017 - TODO: make it so the program can run without a jdk
			//2018 - TODO: come to terms with the fact that the program requires a jdk
			_e.printStackTrace();
			PopupManager.errorMessage("Error getting compiler:\n"
					+ _e.getClass().getName()+"\n"
					+ _e.getMessage());
			return null;
		}
	}

	private void compileDisplay() {
		// this function should only be possible to call
		// when the display type is custom
		if(myParameters.getDisplayType() == DisplayType.CUSTOM) {

			DisplayReader reader = new DisplayReader(myDisplayCode.getText());
			AlgorithmClassLoader loader = getAlgorithmClassLoader();

			if(loader != null) {

				Class<? extends CustomDisplay> displayClass;
				try {
					displayClass = (Class<? extends CustomDisplay>) loader.loadClass(reader.getClassName());
					CustomDisplay display = displayClass.getConstructor(int.class).newInstance(myParameters.getNumThreads());
					display.clearThreads();
					display.setNumThreads(myParameters.getNumThreads());
					myDisplay.setController(display);

				} catch (ClassNotFoundException | NoSuchMethodException
						| InstantiationException | IllegalAccessException
						| InvocationTargetException e) {
					e.printStackTrace();
					PopupManager.errorMessage("Unexpected Error:\n"
							+ e.getClass().getName()+"\n"
							+ e.getMessage());
				}
			}
		}
	}


	private void compileAndRun() {
		AlgorithmReader reader = new AlgorithmReader(myCode.getText());
		AlgorithmWriter writer = new AlgorithmWriter(reader);
		DisplayReader displayReader = new DisplayReader(myDisplayCode.getText());
		AlgorithmClassLoader loader = getAlgorithmClassLoader();

		if(loader != null)
		{
			//run the simulation
			SimulationParameters simParams = new SimulationParameters();
			simParams.setAlgorithmName(reader.getClassName());
			simParams.setNumThreads(myParameters.getNumThreads());
			simParams.setClassLoader(loader);

			//get the display controller, or the name of the custom display
			if(myParameters.getDisplayType() == DisplayType.CUSTOM) {
				simParams.setDisplayName(displayReader.getClassName());
				simParams.setDisplay(null);
			}
			else {
				simParams.setDisplay(myParameters.getController());
				simParams.setDisplayName(null);
			}

			//generate the step positions
			simParams.setCode(writer.buildConstants());
			if(simParams.getCode().getMaxSteps() < 1)
				PopupManager.errorMessage("There were no break points in the algorithm." +
						"\nPlease include a \"CriticalSection();\" call.");
			else try{
				new SimulatorFrame(simParams);

			} catch (MissingVariableException _e){
				_e.printStackTrace();
				PopupManager.errorMessage("One of the variable names in \"Settings\" was not found in the code:\n"
						+ "\""+_e.getVariableName()+"\"");
			} catch (InvocationTargetException _e) {
				_e.printStackTrace();
				if(_e.getCause() != null) {
					PopupManager.errorMessage("There was an error initializing the threads:\n"
							+ _e.getCause().getClass().getName()+"\n"
							+ _e.getCause().getStackTrace()[0].toString());
				}
			} catch (RuntimeException _e) {
				_e.printStackTrace();
				PopupManager.errorMessage("Unexpected Error:\n"
						+ _e.getClass().getName()+"\n"
						+ _e.getMessage());
			}
		}
	}

	@Override
	public void loadFile(SourceFile file)
	{
		myParameters = file.getControls();

		myCode.setText(file.getCode());
		myControl.setEditable(myParameters);

		if(file.getControls().getDisplayType() == DisplayType.CUSTOM) {

			myDisplayCode.setText(file.getDisplayCode());
			compileDisplay();

			if(myTabs.indexOfTab(DISPLAY_TAB) < 0) {
				myTabs.add(DISPLAY_TAB, myDisplayCodeScroll);
				myControlContainer.add(myDisplayButton, BorderLayout.SOUTH);
			}
		} else {

			myDisplay.setController(myParameters.getController());

			if(myTabs.indexOfTab(DISPLAY_TAB) >= 0) {
				myTabs.removeTabAt(myTabs.indexOfTab(DISPLAY_TAB));
				myControlContainer.remove(myDisplayButton);
			}
		}
	}

	@Override
	public void saveFile(SourceFile file)
	{
		file.setCode(myCode.getText());
		file.setControls(myParameters);

		if(myParameters.getDisplayType() == DisplayType.CUSTOM)
			file.setDisplayCode(myDisplayCode.getText());
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		if("Display Type".equals(myControl.getMostRecentField())){
			//control parameters have changed. update the display
			switch(myParameters.getDisplayType())
			{
				case BLANK:
					myParameters.setController(new DefaultDisplay(myParameters.getNumThreads()));
					break;
				case TURN_WHEEL:
					myParameters.setController(new WheelDisplay(myParameters.getNumThreads()));
					break;
				case TREE:
					myParameters.setController(new TreeDisplay(myParameters.getNumThreads()));
					break;
				case STAIRCASE:
					myParameters.setController(new StairDisplay(myParameters.getNumThreads()));
					break;
				case CUSTOM:
					myParameters.setController(null);
					break;
				default:
			}

			if(myParameters.getDisplayType().equals(DisplayType.CUSTOM)) {
				if(myTabs.indexOfTab(DISPLAY_TAB) < 0) {
					myTabs.add(DISPLAY_TAB, myDisplayCodeScroll);
					myControlContainer.add(myDisplayButton, BorderLayout.SOUTH);
				}
			} else {
				if(myTabs.indexOfTab(DISPLAY_TAB) >= 0) {
					myTabs.removeTabAt(myTabs.indexOfTab(DISPLAY_TAB));
					myControlContainer.remove(myDisplayButton);
				}
			}

			myControl.setEditable(myParameters);
			myDisplay.setController(myParameters.getController());
		}
		else
		{
			myDisplay.getController().setNumThreads(myParameters.getNumThreads());
			myDisplay.repaint();
		}
		
	}
}
