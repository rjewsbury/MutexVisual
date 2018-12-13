package mutex.simulator.control;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;

import javax.swing.*;
import javax.swing.event.ChangeEvent;

import mutex.display.ElementController;
import mutex.display.controllers.CustomDisplay;
import mutex.io.CompiledFile;
import mutex.io.CompiledFileListener;
import mutex.io.PopupManager;
import mutex.simulator.model.*;
import mutex.simulator.view.*;

public class SimulatorFrame extends JFrame implements ActionListener, ControlListener, CompiledFileListener {
	public static final Dimension PREFERRED_SIZE = new Dimension(1200, 700);
	public static final Dimension MIN_SIZE = new Dimension(400,400);

	public static final String SHOW_THREADS = "Show Thread Display";
	public static final String SHOW_VARIABLES = "Show Variable Display";
	public static final String PAUSE_CRITICAL = "Stop if Mutual Exclusion is Violated";

	//COMPONENTS
	private ThreadController myThreadController;
	private DisplayPanel myDisplay;
	private CodeDisplayPanel myCodeDisplay;
	private ThreadDisplayPanel myThreadDisplay;
	private VariableDisplayPanel myVarDisplay;
	private ControlPanel myControl;

	private JScrollPane myCodeScroll;
	private JSplitPane myProgressSplit;
	private JPanel myProgressDisplay;

	private JScrollPane myDisplayScroll;
	private JSplitPane myRightSplit;
	private JPanel myRightPanel;

	//parameters
	private SimulationParameters myParameters;
	private Class<? extends AlgorithmThread> myAlgorithmClass;
	private AlgorithmThreadGroup myThreadGroup;
	private ElementController myDisplayController;

	
	public SimulatorFrame(SimulationParameters params) throws InvocationTargetException {
		//in the case of no parameters, use defaults
		if(params == null) {
			params = new SimulationParameters();
		}

		//setting up the frame
		setTitle("Concurrency Visual Tool");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(PREFERRED_SIZE);
		setMinimumSize(MIN_SIZE);
		//resizing things is terrible to deal with anyway
		//setResizable(false);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				myThreadController.stop();
				myThreadGroup.stopThreads();
			}
		});

		try {
			setParameters(params);
		} catch (InvocationTargetException e) {
			//this may be caused by the user's code creating runtime exceptions
			throw e;
		} catch (NoSuchMethodException | InstantiationException | IllegalAccessException e) {
			//I'm not sure how these could be caused
			e.printStackTrace();
			return;
		}
		//---------------------------------------
		//creating model
		myThreadController = new ThreadController(myThreadGroup);
		
		//creating controls
		myControl = new ControlPanel(myParameters.getNumThreads());
		
		//linking the thread controller to the controls
		myControl.addControlListener(myThreadController);
		myControl.addControlListener(this);
		
		//creating displays
		myDisplay = new DisplayPanel(myDisplayController);
		myVarDisplay = new VariableDisplayPanel(myThreadGroup);
		myCodeDisplay = new CodeDisplayPanel(myThreadGroup, myParameters.getCode());
		myThreadDisplay = new ThreadDisplayPanel(myThreadGroup, myParameters.getCode());
		
		//make the displays listen for changes to the state
		myThreadController.addUpdateListener(myDisplay);
		myThreadController.addUpdateListener(myCodeDisplay);
		myThreadController.addUpdateListener(myThreadDisplay);
		myThreadController.addUpdateListener(myVarDisplay);

		myThreadController.addActionListener(myControl.getRunMenu());

		//putting the components together
		myDisplayScroll = new JScrollPane(myDisplay);
		JScrollPane varScroll = new JScrollPane(myVarDisplay);
		myCodeScroll = new JScrollPane(myCodeDisplay);
		myCodeScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		myCodeScroll.getVerticalScrollBar().addAdjustmentListener(adjustmentEvent -> {
			//System.out.println(adjustmentEvent.getValue());
			myThreadDisplay.setOffset(adjustmentEvent.getValue());
		});

		//split the displays
		myProgressSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, myThreadDisplay, myCodeScroll);
		myProgressSplit.setDividerLocation(60);

		myProgressDisplay = new JPanel();
		myProgressDisplay.setLayout(new GridLayout(1,0));
		myProgressDisplay.add(myProgressSplit);

		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BorderLayout());
		leftPanel.add(myProgressDisplay, BorderLayout.CENTER);
		leftPanel.add(myControl, BorderLayout.NORTH);

		myRightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,myDisplayScroll, varScroll);

		myRightPanel = new JPanel();
		myRightPanel.setLayout(new GridLayout(0,1));
		myRightPanel.add(myRightSplit);

		JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,leftPanel,myRightPanel);
		
		add(mainSplit);

		SimulatorMenuBar menuBar = new SimulatorMenuBar();
		menuBar.addActionListener(this);
		menuBar.addCompiledFileListener(this);
		setJMenuBar(menuBar);

		setVisible(true);
		
		//maybe don't include this?
		pack();
	}

	private void setParameters(SimulationParameters params) throws
			InvocationTargetException, NoSuchMethodException,
			InstantiationException, IllegalAccessException {
		//load the algorithm specific data
		ClassLoader loader = params.getClassLoader();

		try{
			myAlgorithmClass = (Class<? extends AlgorithmThread>) loader.loadClass(params.getAlgorithmName());
		}catch (ClassNotFoundException e){
			myAlgorithmClass = DefaultThread.class;
		}

		if(myThreadGroup != null)
			myThreadGroup.stopThreads();
		myThreadGroup = new AlgorithmThreadGroup(myAlgorithmClass,params.getNumThreads());

		if(params.getDisplay() != null) {
			myDisplayController = params.getDisplay();
		} else {
			if(params.getDisplayName() != null) {
				Class<? extends CustomDisplay> displayClass;
				try {
					displayClass = (Class<? extends CustomDisplay>) loader.loadClass(params.getDisplayName());
					myDisplayController = displayClass.getConstructor(int.class).newInstance(params.getNumThreads());
				} catch (ClassNotFoundException e) {
					throw new NullPointerException("The simulation parameters did not contain any display controller");
				}
			} else {
				throw new NullPointerException("The simulation parameters did not contain any display controller");
			}
		}

		myDisplayController.clearThreads();
		myDisplayController.setNumThreads(params.getNumThreads());
		myDisplayController.addThreads(myThreadGroup);

		//if this is the first time parameters are being set, the constructor will handle initialization
		if(myParameters == null) {
			myParameters = params;
		}
		//otherwise, displays must be updated.
		else {
			myParameters = params;

			myControl.reset();
			myCodeDisplay.setCodeConstants(myParameters.getCode());
			myThreadDisplay.setCodeConstants(myParameters.getCode());

			resizeThreadGroup(params.getNumThreads());
		}

		revalidate();
		repaint();
	}

	//updates all displays for a new thread group
	private void resizeThreadGroup(int size) throws
			InvocationTargetException, NoSuchMethodException,
			InstantiationException, IllegalAccessException {
		myParameters.setNumThreads(size);

		if(myThreadGroup != null)
			myThreadGroup.stopThreads();
		myThreadGroup = new AlgorithmThreadGroup(myAlgorithmClass, size);

		myThreadController.setThreadGroup(myThreadGroup);
		myThreadDisplay.setThreadGroup(myThreadGroup);
		myCodeDisplay.setThreadGroup(myThreadGroup);
		myVarDisplay.setThreadGroup(myThreadGroup);

		myDisplayController.setNumThreads(size);
		myDisplayController.addThreads(myThreadGroup);
		myDisplay.setController(myDisplayController);
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		if(SHOW_VARIABLES.equals(actionEvent.getActionCommand())) {
			setVariableDisplayVisible(((JCheckBoxMenuItem)actionEvent.getSource()).getState());
		}
		else if(SHOW_THREADS.equals(actionEvent.getActionCommand())) {
			setThreadDisplayVisible(((JCheckBoxMenuItem)actionEvent.getSource()).getState());
		}
		else if(ResetButton.RESET_MESSAGE.equals(actionEvent.getActionCommand())) {

			//if the group size changed, a new group needs to be created
			if( actionEvent.getID() != myThreadGroup.size()) {
				try {
					resizeThreadGroup(actionEvent.getID());
				} catch (InvocationTargetException | NoSuchMethodException
						| InstantiationException | IllegalAccessException e) {
					//this should never happen!
					//if it does, it wasn't able to construct new instances of the thread class
					e.printStackTrace();
				}
			}
		}
		else if(PAUSE_CRITICAL.equals(actionEvent.getActionCommand())) {
			myThreadController.pauseCriticalSection(((JCheckBoxMenuItem)actionEvent.getSource()).getState());
		}
	}

	private void setThreadDisplayVisible(boolean visible) {
		myProgressDisplay.remove(0);
		if(visible) {
			myProgressSplit.add(myCodeScroll);
			myProgressDisplay.add(myProgressSplit);

			myProgressSplit.setDividerLocation(60);
		}
		else {
			myProgressSplit.remove(myCodeScroll);
			myProgressDisplay.add(myCodeScroll);
		}
		myProgressDisplay.revalidate();
		myProgressDisplay.repaint();
	}

	private void setVariableDisplayVisible(boolean visible) {
		myRightPanel.remove(0);
		if(visible) {
			myRightSplit.add(myDisplayScroll);
			myRightPanel.add(myRightSplit);
		}
		else {
			myRightSplit.remove(myDisplayScroll);
			myRightPanel.add(myDisplayScroll);
		}
		myRightPanel.revalidate();
		myRightPanel.repaint();
	}

	@Override
	public void itemStateChanged(ItemEvent itemEvent) {
		//the frame doesnt need to know which threads are active
	}

	@Override
	public void stateChanged(ChangeEvent changeEvent) {
		//the frame doesnt need to know the current delay
	}

	@Override
	public void loadFile(CompiledFile file) {
		try {
			setParameters(file.getParameters());
		} catch (InvocationTargetException | NoSuchMethodException
				| InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedClassVersionError e) {
			PopupManager.errorMessage("This file was compiled with a newer version of java:\n"
					+ e.getClass().getName()+"\n"
					+ e.getMessage());
		}
	}

	@Override
	public void saveFile(CompiledFile file) {
		file.setParameters(myParameters);
	}
}
