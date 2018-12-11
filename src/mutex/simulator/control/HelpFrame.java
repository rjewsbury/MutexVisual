package mutex.simulator.control;

import javax.swing.*;
import java.awt.*;

public class HelpFrame extends JFrame{

    private static final String HELP_TEXT =
            "The Simulator\n" +
                    "    a. Displays\n" +
                    "        The Simulator has four main display windows...\n" +
                    "        i. The Code Display:\n" +
                    "            This display shows the algorithm code as written in the editor.\n" +
                    "            As threads execute the code, A box labelled with their ID\n" +
                    "            will move through the code, pausing whenever a shared variable\n" +
                    "            is read or written to show the next operation.\n" +
                    "            \n" +
                    "        ii. The Thread Display:\n" +
                    "            This display shows the progress of each thread as it moves through\n" +
                    "            the algorithm. It is a more abstract representation of progress,\n" +
                    "            which makes it easier to tell at a glance where each thread is.\n" +
                    "            This display can be disabled in the view menu.\n" +
                    "            \n" +
                    "        iii. The Variable Display:\n" +
                    "            This display shows the contents of memory used by the algorithm.\n" +
                    "            Whenever a thread accesses or manipulates a variable, a box\n" +
                    "            labelled with their ID will appear over that variable.\n" +
                    "            Shared variables are shown by name, while non-shared variables\n" +
                    "            are shown with the prefix \"Thd#:\" to display which thread they\n" +
                    "            belong to. This display can be disabled in the view menu\n" +
                    "            \n" +
                    "        iv. The Algorithm display:\n" +
                    "            This display shows an abstract representation of how the algorithm\n" +
                    "            is behaving. As the linked variables change, the display updates\n" +
                    "            to represent the current state\n" +
                    "    b. Controls\n" +
                    "        i. Active Threads:\n" +
                    "            Each thread can be activated or deactivated separately, in order\n" +
                    "            to simulate high contention or low contention situations.\n" +
                    "            At the top of the control panel, there is a row of check boxes.\n" +
                    "            When a box labelled with an ID is checked, the thread with that\n" +
                    "            ID is eligible to continue execution.\n" +
                    "            When the box is unchecked, that thread will be paused in its current\n" +
                    "            state. This can be used to either have threads not participate,\n" +
                    "            or to cause threads to stay at certain key checkpoints in the\n" +
                    "            algorithm for an indefinite period.\n" +
                    "            \n" +
                    "        ii. Speed Control:\n" +
                    "            The algorithm is run by choosing a thread from the list of active\n" +
                    "            threads, and having it execute a piece of code up to the next\n" +
                    "            shared memory access. This allows the algorithm to be run at\n" +
                    "            a reasonable pace to visualize, while not sacrificing any of the\n" +
                    "            possible behaviours that may occur when the code is run at full\n" +
                    "            speed.\n" +
                    "            The slider in the control menu allows the user to change the\n" +
                    "            speed of the algorithm by changing the waiting period between\n" +
                    "            steps of execution.\n" +
                    "            \n" +
                    "        iii. Execution Control:\n" +
                    "            Run / Stop:\n" +
                    "                Run causes the algorithm to begin execution at the speed\n" +
                    "                given by the Speed Controls. All active threads will begin\n" +
                    "                moving through the algorithm. Stop causes execution to freeze\n" +
                    "                in whatever state it currently has.\n" +
                    "            Step:\n" +
                    "                This causes a random active thread to move one step to the next\n" +
                    "                shared memory access. By carefully manipulating active threads\n" +
                    "                and using the step function, Edge cases can be created and tested\n" +
                    "            Reset:\n" +
                    "                This causes all threads to return to their initial state at\n" +
                    "                the beginning of the algorithm, resetting both their positions\n" +
                    "                and the state of all variables.\n" +
                    "                The field next to the reset button determines the number of\n" +
                    "                threads to be used by the algorithm upon reset. Any integer\n" +
                    "                greater than 1 may be used, however larger numbers will suffer\n" +
                    "                on performance." +
                    "                \n" +
                    "            When the simulation is done being used, or the user wishes to\n" +
                    "            change the number of threads, the simulation window can simply\n" +
                    "            be closed, the change can be made in the editor, and the algorithm\n" +
                    "            can be re-compiled.";

    public HelpFrame() {
        JPanel helpPanel = new JPanel();
        JTextArea helpTextArea = new JTextArea();
        helpTextArea.setEditable(false);
        helpTextArea.setText(HELP_TEXT);
        JScrollPane helpScroll = new JScrollPane(helpTextArea);
        helpPanel.setLayout(new GridLayout(0,1));
        helpPanel.add(helpScroll);

        //setting up the frame
        setTitle("User Guide");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(new Dimension(300, 400));

        add(helpPanel);
        setVisible(true);
    }
}
