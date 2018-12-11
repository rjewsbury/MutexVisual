package mutex;

import mutex.simulator.control.SimulatorFrame;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

public class SimulatorMain implements Runnable
{
	public static void main(String[] args)
	{
		startSimulator();
	}
	
	public static void startSimulator()
	{
		//creates a SimulatorFrame on the GUI thread
		SwingUtilities.invokeLater(new SimulatorMain());
	}
	
	public void run()
	{
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (ClassNotFoundException | UnsupportedLookAndFeelException
				| IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
		}
		try {
			new SimulatorFrame(null);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
