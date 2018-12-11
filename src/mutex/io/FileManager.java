package mutex.io;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

/**
 * Deals with all file I/O
 * 
 * @author Ryley
 * @version 2.0
 */
public class FileManager
{
	public static class SourceFileFilter extends FileFilter{
		@Override
		public boolean accept(File f){
			boolean accepts = false;
			String[] splitName;
			if (f.isDirectory())
				accepts = true;
			else{
				splitName = f.getName().split("\\.");
				if (splitName.length > 1)
					accepts = splitName[splitName.length - 1].equals("mutex");
			}
			return accepts;
		}

		@Override
		public String getDescription(){
			return "Mutual Exclusion Algorithm (.mutex)";
		}
	}

	public static class CompiledFileFilter extends FileFilter{
		@Override
		public boolean accept(File f){
			boolean accepts = false;
			String[] splitName;
			if (f.isDirectory())
				accepts = true;
			else{
				splitName = f.getName().split("\\.");
				if (splitName.length > 1)
					accepts = splitName[splitName.length - 1].equals("mutexc");
			}
			return accepts;
		}

		@Override
		public String getDescription(){
			return "Compiled Mutex Algorithm (.mutexc)";
		}
	}
	
	//remembers the last location for saving, loading, and importing
	private JFileChooser saveChooser, loadChooser;
	private FileFilter myFilter;
	//the most recent save location
	private File saveLocation;

	public FileManager() {
		this(null);
	}

	/**
	 * Creates a new default file manager
	 */
	public FileManager(FileFilter filter)
	{
		if(filter == null)
			myFilter = new SourceFileFilter();
		else
			myFilter = filter;

		saveChooser = new JFileChooser();
		saveChooser.addChoosableFileFilter(myFilter);
		//sets the default
		saveChooser.setFileFilter(myFilter);
		saveChooser.setCurrentDirectory(new File("."));

		loadChooser = new JFileChooser();
		loadChooser.addChoosableFileFilter(myFilter);
		//prevents non source files from being used
		loadChooser.setAcceptAllFileFilterUsed(false);
		loadChooser.setCurrentDirectory(new File("."));
		
		//by default, we don't know where to save to
		saveLocation = null;
	}
	
	/**
	 * When a new file is created, we don't want save to automatically return the last location
	 */
	public void clearSaveLocation()
	{
		saveLocation = null;
	}
	
	public File getSaveFile(boolean changeLocation)
	{
		File tempLocation;
		
		//change location
		if(saveLocation == null || changeLocation)
		{
			//if they want to save
			if(saveChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
			{
				tempLocation = saveChooser.getSelectedFile();
				
				//add the extension
				if(!myFilter.accept(tempLocation))
				{
					//add an extension based on the filter
					if(myFilter instanceof  SourceFileFilter)
						tempLocation = new File(tempLocation.getAbsolutePath()+".mutex");
					else if (myFilter instanceof  CompiledFileFilter)
						tempLocation = new File(tempLocation.getAbsolutePath()+".mutexc");
				}
				
				//if the file can be found, ask if it's okay to overwrite
				if(tempLocation.exists())
				{
					// if they still want to save
					int option = JOptionPane.showConfirmDialog(null,
							"A file with that name already exists!\nAre you sure you want to overwrite it?",
							"Overwriting a file", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
					
					if (option == JOptionPane.YES_OPTION)
					{
						//now we can save
						saveLocation = tempLocation;
						return saveLocation;
					}
				}
				else
				{
					//create a new file
					saveLocation = tempLocation;
					try{
						saveLocation.createNewFile();
						return saveLocation;
					}catch (IOException e){
						e.printStackTrace();
					} 
				}
			}
		}
		else
		{
			//just use the default location
			return saveLocation;
		}
		//failed to save
		return null;
	}
	
	/**
	 * gets the contents of a file
	 */
	public File getLoadFile(boolean showWarning)
	{
		//warn them
		int option = 0;

		if(showWarning)
			option = JOptionPane.showConfirmDialog(null,
				"Loading a new configuration will lose any unsaved work.\nwould you still like to continue?",
				"Loading a configuration", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		
		//continue
		if (!showWarning || option == JOptionPane.YES_OPTION)
		{
			int loadReturn = loadChooser.showOpenDialog(null);
			if (loadReturn == JFileChooser.APPROVE_OPTION &&
					loadChooser.getSelectedFile().exists() &&
					myFilter.accept(loadChooser.getSelectedFile()))
			{
				saveLocation = loadChooser.getSelectedFile();
				//return a loadable file
				return saveLocation;
			}
		}
		return null;
	}
}
