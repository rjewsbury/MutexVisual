package mutex.editor.control;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import mutex.EditorMain;

import java.awt.event.*;

public class CodePanel extends JTextArea implements ActionListener
{
	public static final String EXAMPLE1_BUTTON = "Example (Keywords)";
	public static final String EXAMPLE2_BUTTON = "Example (Linked Variables)";
	public static final String EXAMPLE3_BUTTON = "Example (Key Methods)";

	private UndoManager undo;
	
	public CodePanel()
	{
		this.setFont(EditorMain.GLOBAL_FONT);
		this.setTabSize(4);

		initializeUndoManager();

		((AbstractDocument)this.getDocument()).setDocumentFilter(new DocumentFilter(){
			@Override
			public void insertString(FilterBypass fb, int offs, String str, AttributeSet a)
					throws BadLocationException{

				if("\n".equals(str)) {
					str = addWhitespace(fb.getDocument(), offs);
				}
				super.insertString(fb, offs, str, a);
			}
			@Override
			public void replace(FilterBypass fb, int offs, int length, String str, AttributeSet a)
					throws BadLocationException{

				if("\n".equals(str)) {
					str = addWhitespace(fb.getDocument(), offs);
				}
				super.replace(fb, offs, length, str, a);
			}

			private String addWhitespace(Document doc, int offs)
					throws BadLocationException{
				Element root = doc.getDefaultRootElement();
				int line = root.getElementIndex(offs);
				int start = root.getElement(line).getStartOffset();

				String space = doc.getText(start, offs-start).replaceAll("^(\\s*).*$","$1");

				return System.lineSeparator()+space;
			}
		});
	}

	private void initializeUndoManager() {
		undo = new UndoManager();
		// Listen for undo and redo events
		getDocument().addUndoableEditListener(evt -> undo.addEdit(evt.getEdit()));

		// Create an undo action and add it to the text component
		getActionMap().put("Undo",
				new AbstractAction("Undo") {
					public void actionPerformed(ActionEvent evt) {
						try {
							if (undo.canUndo()) {
								undo.undo();
							}
						} catch (CannotUndoException e) {
						}
					}
				});

		// Bind the undo action to ctl-Z
		getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");

		// Create a redo action and add it to the text component
		getActionMap().put("Redo",
				new AbstractAction("Redo") {
					public void actionPerformed(ActionEvent evt) {
						try {
							if (undo.canRedo()) {
								undo.redo();
							}
						} catch (CannotRedoException e) {
						}
					}
				});

		// Bind the redo action to ctl-Y
		getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");
	}

	public void undo() {
		if (undo.canUndo()) {
			undo.undo();
		}
	}

	public void redo() {
		if (undo.canRedo()) {
			undo.redo();
		}
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		String command = actionEvent.getActionCommand();
		//currently no general code actions
	}

	public void confirmAndSet(String text){
		int option;
		if (undo.canUndo())
			option = JOptionPane.showConfirmDialog(null,
					"Loading a new configuration will lose any unsaved work.\nwould you still like to continue?",
					"Loading a configuration", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		else
			option = JOptionPane.YES_OPTION;

		if(option == JOptionPane.YES_OPTION) {
			setText(text);
		}
	}
}
