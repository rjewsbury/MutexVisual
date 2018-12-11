package mutex;

import java.awt.Font;
import javax.swing.*;

import mutex.editor.control.EditorFrame;

/**
 * TODO:
 * 	-Bugs
 * 		- 2017 - Occasional lag spikes.
 * 			Threads continue running when the simulation window closes?
 * 		- 2018 - Havnt seen this happen in a while. possibly fixed?
 * 	-Refactoring
 * 		-Restructure the display?
 * 		-OutputStream panel? instead of pop ups
 * 		-Redirect StdOut
 *
 * @author Ryley Jewsbury
 */
public class EditorMain implements Runnable
{
	public static final Font GLOBAL_FONT = new Font(Font.MONOSPACED,Font.PLAIN,12);
	
	public static void main(String[] args)
	{
		startSimulator();
	}
	
	public static void startSimulator()
	{
		//creates an EditorFrame on the GUI thread
		SwingUtilities.invokeLater(new EditorMain());
	}
	
	public void run()
	{
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (ClassNotFoundException | UnsupportedLookAndFeelException
				| IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
		}
		new EditorFrame();
	}
}
