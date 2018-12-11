package mutex.editor.control;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Box;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import mutex.io.*;

public class EditorMenuBar extends JMenuBar implements ActionListener
{
	private List<SourceFileListener> mySourceFileListeners;
	private List<ActionListener> myActionListeners;
	private FileManager myFileManager;
	private SourceFile myPrevFile;

	public EditorMenuBar()
	{
		mySourceFileListeners = new ArrayList<>();
		myActionListeners = new ArrayList<>();
		myFileManager = new FileManager();
		
		add(createFileMenu());
		add(createEditMenu());
		//nothing to view
		//add(createViewMenu());
		add(createHelpMenu());
		
		add(Box.createHorizontalStrut(20));
		
		//if I ever feel like adding more buttons, I can put them here
		List<JMenuItem> myOtherItems = Arrays.asList(
				new JMenuItem(EditorFrame.COMPILE_BUTTON));
		
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
		for(ActionListener al: myActionListeners) {
			al.actionPerformed(actionEvent);
		}
	}
	
	public void addActionListener(ActionListener listener){
		myActionListeners.add(listener);
	}
	
	public void addSourceFileListener(SourceFileListener listener){
		mySourceFileListeners.add(listener);
	}
	
	private void saveSourceFileListeners(){
		for(SourceFileListener listener: mySourceFileListeners)
			listener.saveFile(myPrevFile);
	}
	
	private void loadSourceFileListeners(){
		for(SourceFileListener listener: mySourceFileListeners)
			listener.loadFile(myPrevFile);
	}
	
	private JMenu createFileMenu()
	{
		JMenu fileMenu = new JMenu("File");
		JMenuItem tempItem;
		
		tempItem = new JMenuItem("New");
		tempItem.addActionListener(e -> {
			myFileManager.clearSaveLocation();
			myPrevFile = new SourceFile();

			loadSourceFileListeners();
		});
		fileMenu.add(tempItem);
		
		fileMenu.addSeparator();
		
		tempItem = new JMenuItem("Open File...");
		tempItem.addActionListener(e -> {
			File file = myFileManager.getLoadFile(true);
			if(file == null)
				return;

			try{
				myPrevFile = new SourceFile(file);
				loadSourceFileListeners();
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
				myPrevFile = new SourceFile();
			saveSourceFileListeners();
			try{
				myPrevFile.writeFile(file);
			}catch (IOException e1){
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
				myPrevFile = new SourceFile();
			saveSourceFileListeners();
			try{
				myPrevFile.writeFile(file);
			}catch (IOException e1){
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
	
	private JMenu createEditMenu()
	{
		JMenu editMenu = new JMenu("Edit");
		JMenuItem tempItem;
		
		tempItem = new JMenuItem(EditorFrame.UNDO_BUTTON);
		tempItem.addActionListener(this);
		editMenu.add(tempItem);

		tempItem = new JMenuItem(EditorFrame.REDO_BUTTON);
		tempItem.addActionListener(this);
		editMenu.add(tempItem);
		
		return editMenu;
	}
	
	private JMenu createViewMenu()
	{
		JMenu viewMenu = new JMenu("View");
		JMenuItem tempItem;
		
		tempItem = new JMenuItem("(Not yet implemented)");
		viewMenu.add(tempItem);
		
		return viewMenu;
	}
	
	private JMenu createHelpMenu()
	{
		JMenu helpMenu = new JMenu("Help");
		JMenuItem tempItem;

		tempItem = new JMenuItem(CodePanel.EXAMPLE1_BUTTON);
		tempItem.addActionListener(this);
		helpMenu.add(tempItem);

		tempItem = new JMenuItem(CodePanel.EXAMPLE2_BUTTON);
		tempItem.addActionListener(this);
		helpMenu.add(tempItem);

		tempItem = new JMenuItem(CodePanel.EXAMPLE3_BUTTON);
		tempItem.addActionListener(this);
		helpMenu.add(tempItem);
		
		return helpMenu;
	}
}
