package mutex.editor.control;

import mutex.editor.model.ExampleCode;

import java.awt.event.ActionEvent;

public class AlgorithmCodePanel extends CodePanel {

    public static final String NEW_TEMPLATE =
            "public class AlgorithmName extends AlgorithmThread"+System.lineSeparator()
                    + "{"												+System.lineSeparator()
                    + "	// shared variables can be accessed by all threads"+System.lineSeparator()
                    + "	shared int foo = N;"							+System.lineSeparator()
                    + "	"												+System.lineSeparator()
                    + "	// non-shared variables belong to a single thread"+System.lineSeparator()
                    + "	int bar = ID;"									+System.lineSeparator()
                    + "	"												+System.lineSeparator()
                    + "	// threads will loop through this method"		+System.lineSeparator()
                    + "	public void algorithm()"						+System.lineSeparator()
                    + "	{"												+System.lineSeparator()
                    + "		CriticalSection();"							+System.lineSeparator()
                    + "	}"												+System.lineSeparator()
                    + "}";

    AlgorithmCodePanel() {
        setText(NEW_TEMPLATE);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        super.actionPerformed(actionEvent);

        String command = actionEvent.getActionCommand();
        if(EXAMPLE1_BUTTON.equals(command)) {
            confirmAndSet(ExampleCode.EXAMPLE_1);
        }
        else if(EXAMPLE2_BUTTON.equals(command)) {
            confirmAndSet(ExampleCode.EXAMPLE_2);
        }
        else if(EXAMPLE3_BUTTON.equals(command)) {
            confirmAndSet(ExampleCode.EXAMPLE_3);
        }
    }
}
