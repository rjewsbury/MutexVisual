package mutex.simulator.control;

import mutex.io.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimulatorMenuBar extends JMenuBar implements ActionListener
{

	private List<CompiledFileListener> myCompiledFileListeners;
	private List<ActionListener> myActionListeners;
	private FileManager myFileManager;
	private CompiledFile myPrevFile;

	public SimulatorMenuBar()
	{
		myCompiledFileListeners = new ArrayList<>();
		myActionListeners = new ArrayList<>();
		myFileManager = new FileManager(new FileManager.CompiledFileFilter());
		
		add(createFileMenu());
		add(createControlMenu());
		add(createViewMenu());
		add(createHelpMenu());
		
		add(Box.createHorizontalStrut(20));
		
		//if I ever feel like adding more buttons, I can put them here
		List<JMenuItem> myOtherItems = Arrays.asList(
//				new JMenuItem()
		);

		for(JMenuItem item: myOtherItems){
			item.setOpaque(false);
			item.setMaximumSize(new Dimension(item.getPreferredSize().width, Integer.MAX_VALUE));
			add(item);
			item.addActionListener(this);
		}
	}

	//rebroadcasts actions to the list of listeners
	@Override
	public void actionPerformed(ActionEvent actionEvent) {
//		System.out.println(actionEvent.getActionCommand());
//		System.out.println(actionEvent.getModifiers());
		for(ActionListener al: myActionListeners) {
			al.actionPerformed(actionEvent);
		}
	}
	
	public void addActionListener(ActionListener listener){
		myActionListeners.add(listener);
	}

	public void addCompiledFileListener(CompiledFileListener listener){
		myCompiledFileListeners.add(listener);
	}

	private void saveCompiledFileListeners(){
		for(CompiledFileListener listener: myCompiledFileListeners) {
			//System.out.println(listener.getClass().getName());
			listener.saveFile(myPrevFile);
		}
	}

	private void loadCompiledFileListeners(){
		for(CompiledFileListener listener: myCompiledFileListeners)
			listener.loadFile(myPrevFile);
	}

	private JMenu createFileMenu()
	{
		JMenu fileMenu = new JMenu("File");
		JMenuItem tempItem;

		tempItem = new JMenuItem("Open File...");
		tempItem.addActionListener(e -> {
			File file = myFileManager.getLoadFile(false);
			if(file == null)
				return;

			try{
				myPrevFile = new CompiledFile(file);
				loadCompiledFileListeners();
			}catch(IOException _e){
				//if the file fails to load, do not replace the previous file
				_e.printStackTrace();
			}
		});
		fileMenu.add(tempItem);

		fileMenu.addSeparator();

		tempItem = new JMenuItem("Save");
		tempItem.addActionListener(e -> {
			File file = myFileManager.getSaveFile(false);
			if(file == null)
				return;

			if(myPrevFile == null)
				myPrevFile = new CompiledFile();
			saveCompiledFileListeners();
			try{
				myPrevFile.writeFile(file);
			}catch (IOException e1){
				e1.printStackTrace();
				PopupManager.warningMessage(
						e1.getClass().getName()
								+System.lineSeparator()
								+e1.getMessage()
				);
			}
		});
		fileMenu.add(tempItem);

		tempItem = new JMenuItem("Save as...");
		tempItem.addActionListener(e -> {
			File file = myFileManager.getSaveFile(true);
			if(file == null)
				return;

			if(myPrevFile == null)
				myPrevFile = new CompiledFile();
			saveCompiledFileListeners();
			try{
				myPrevFile.writeFile(file);
			}catch (IOException e1){
				e1.printStackTrace();
				PopupManager.warningMessage(
						e1.getClass().getName()
								+System.lineSeparator()
								+e1.getMessage()
				);
			}
		});
		fileMenu.add(tempItem);

		return fileMenu;
	}

	private JMenu createControlMenu()
	{
		JMenu viewMenu = new JMenu("Control");
		JMenuItem tempItem;

		tempItem = new JCheckBoxMenuItem(SimulatorFrame.PAUSE_CRITICAL);
		tempItem.addActionListener(this);
		((JCheckBoxMenuItem) tempItem).setState(true);
		viewMenu.add(tempItem);

		return viewMenu;
	}

	private JMenu createViewMenu()
	{
		JMenu viewMenu = new JMenu("View");
		JMenuItem tempItem;

		tempItem = new JCheckBoxMenuItem(SimulatorFrame.SHOW_THREADS);
		tempItem.addActionListener(this);
		((JCheckBoxMenuItem) tempItem).setState(true);
		viewMenu.add(tempItem);

		tempItem = new JCheckBoxMenuItem(SimulatorFrame.SHOW_VARIABLES);
		tempItem.addActionListener(this);
		((JCheckBoxMenuItem) tempItem).setState(true);
		viewMenu.add(tempItem);

		return viewMenu;
	}

	private JMenu createHelpMenu() {
		JMenu helpMenu = new JMenu("Help");
		JMenuItem tempItem;

		tempItem = new JMenuItem("Show User Guide");
		tempItem.addActionListener(actionEvent -> new HelpFrame());
		helpMenu.add(tempItem);

		return helpMenu;
	}
}
