package mutex.io;

import javax.swing.JOptionPane;

/**
 * Displays information about what the program is doing, particularly errors.
 * 
 * Might be a better design to have an "output panel" where the program can log things
 *
 * @author Ryley Jewsbury
 */
public class PopupManager
{
	public static void warningMessage(String message)
	{
		JOptionPane.showMessageDialog(null, message, "Warning", JOptionPane.WARNING_MESSAGE);
	}
	
	public static void errorMessage(String message)
	{
		JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	public static boolean YesNoMessage(String message)
	{
		return JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, message, "Confirm", JOptionPane.YES_NO_OPTION);
	}
}
