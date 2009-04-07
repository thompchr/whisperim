 /**************************************************************************
 * Copyright 2009                                                          *
 * Kirk Banks   				                                           *
 *                                                                         *
 * Licensed under the Apache License, Version 2.0 (the "License");         *
 * you may not use this file except in compliance with the License.        *
 * You may obtain a copy of the License at                                 *
 *                                                                         *
 * http://www.apache.org/licenses/LICENSE-2.0                              *
 *                                                                         *
 * Unless required by applicable law or agreed to in writing, software     *
 * distributed under the License is distributed on an "AS IS" BASIS,       *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.*
 * See the License for the specific language governing permissions and     *
 * limitations under the License.                                          *
 **************************************************************************/

package org.whisperim.client;

import com.thoughtworks.xstream.XStream;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import javax.swing.undo.*;

public class ProfileEditor extends JFrame implements ActionListener{
	
	private HTMLDocument document;
	private JTextPane textPane = new JTextPane();
	private File currentFile = new File(SAVEPATH_);
	private WhisperClient client_;
	private XStream xstream_;
		
	protected UndoableEditListener undoHandler = new UndoHandler();
	protected UndoManager undo = new UndoManager();
		
	private UndoAction undoAction = new UndoAction();
	private RedoAction redoAction = new RedoAction();
		
	private Action cutAction = new DefaultEditorKit.CutAction();
	private Action copyAction = new DefaultEditorKit.CopyAction();
	private Action pasteAction = new DefaultEditorKit.PasteAction();

	private Action boldAction = new StyledEditorKit.BoldAction();
	private Action underlineAction = new StyledEditorKit.UnderlineAction();
	private Action italicAction = new StyledEditorKit.ItalicAction();
	
	private static final String SAVEPATH_ = "Profile.html";
	
	private static final String WHISPER_ = "Whisper Profile";	
	private static final String FILE_ = "File";
	private static final String EDIT_ = "Edit";
	private static final String COLOR_ = "Color";
	private static final String FONT_ = "Font";
	private static final String STYLE_ = "Style";
	private static final String ALIGN_ = "Align";
	private static final String NEW_ = "New";
	private static final String OPEN_ = "Open";
	private static final String SAVE_ = "Save";
	private static final String SAVEAS_ = "Save As";
	private static final String DELETE_ = "Delete";
	private static final String EXIT_ = "Exit";
	private static final String UNDO_ = "Undo";
	private static final String REDO_ = "Redo";
	private static final String CUT_ = "Cut";
	private static final String COPY_ = "Copy";
	private static final String PASTE_ = "Paste";
	private static final String CLEAR_ = "Clear";
	private static final String SELECTALL_ = "Select All";
	private static final String RED_ = "Red";
	private static final String ORANGE_ = "Orange";
	private static final String YELLOW_ = "Yellow";
	private static final String GREEN_ = "Green";
	private static final String BLUE_ = "Blue";
	private static final String CYAN_ = "Cyan";
	private static final String MAGENTA_ = "Magenta";
	private static final String BLACK_ = "Black";
	private static final String FONTTYPE_ = "Font Type";
	private static final String FONTSIZE_ = "Font Size";
	private static final String BOLD_ = "Bold";
	private static final String ITALIC_ = "Italics";
	private static final String UNDERLINE_ = "Underline";
	private static final String SUBSCRIPT_ = "Subscript";
	private static final String SUPERSCRIPT_ = "Superscript";
	private static final String STRIKETHROUGH_ = "StrikeThrough";
	private static final String LEFT_ = "Left";
	private static final String CENTER_ = "Center";
	private static final String RIGHT_ = "Right";

	JMenu fileMenu;	
	JMenu editMenu;
	JMenu colorMenu;	
	JMenu fontMenu;
	JMenu styleMenu;
	JMenu alignMenu;
	JMenuItem newItem;
	JMenuItem openItem;
	JMenuItem saveItem;
	JMenuItem saveAsItem;
	JMenuItem deleteItem;
	JMenuItem exitItem;
	JMenuItem undoItem;
	JMenuItem redoItem;
	JMenuItem cutItem;
	JMenuItem copyItem;
	JMenuItem pasteItem;
	JMenuItem clearItem;
	JMenuItem selectAllItem;
	JMenuItem redTextItem;
	JMenuItem orangeTextItem;
	JMenuItem yellowTextItem;
	JMenuItem greenTextItem;
	JMenuItem blueTextItem;
	JMenuItem cyanTextItem;
	JMenuItem magentaTextItem;
	JMenuItem blackTextItem;
	JMenu fontTypeMenu;
	JMenu fontSizeMenu;
	JMenuItem boldMenuItem;
	JMenuItem underlineMenuItem;
	JMenuItem italicMenuItem;
	JMenuItem subscriptMenuItem;
	JMenuItem superscriptMenuItem;
	JMenuItem strikeThroughMenuItem;
	JMenuItem leftAlignMenuItem;
	JMenuItem centerMenuItem;
	JMenuItem rightAlignMenuItem;
	
	public ProfileEditor(WhisperClient client){
		super(WHISPER_);
		client_ = client;
		HTMLEditorKit editorKit = new HTMLEditorKit();
		document = (HTMLDocument)editorKit.createDefaultDocument();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		init();
		
		boolean text = currentFile.exists();
		if (!currentFile.exists()){
			String message = "You have no profile.  Would you like to create one?";
			int jp = JOptionPane.showConfirmDialog(null, message, "Profile", JOptionPane.YES_NO_OPTION);
			if (jp == JOptionPane.YES_OPTION){
				currentFile = new File(SAVEPATH_);
				saveDocumentAs();
			}
			else if(jp == JOptionPane.NO_OPTION){
				//Do nothing
			}
		}
		else
		{
			String message = "You have a profile.  Would you like to edit it?";
			int jp = JOptionPane.showConfirmDialog(null, message, "Profile", JOptionPane.YES_NO_OPTION);
			if (jp == JOptionPane.YES_OPTION){
				try {
					openDocument();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
			else if(jp == JOptionPane.NO_OPTION){
				//Do nothing
			}
		}
	}
	
	public void init(){
		addWindowListener(new FrameListener());
		
		//Menu Items
		JMenuBar menuBar = new JMenuBar();
		getContentPane().add(menuBar, BorderLayout.NORTH);
		fileMenu = new JMenu(FILE_);		
		editMenu = new JMenu(EDIT_);
		colorMenu = new JMenu(COLOR_);		
		fontMenu = new JMenu(FONT_);		
		styleMenu = new JMenu(STYLE_);	
		alignMenu = new JMenu(ALIGN_);
		
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(colorMenu);
		menuBar.add(fontMenu);
		menuBar.add(styleMenu);
		menuBar.add(alignMenu);
		
		newItem = new JMenuItem(NEW_);
		openItem = new JMenuItem(OPEN_);
		saveItem = new JMenuItem(SAVE_);
		//saveAsItem = new JMenuItem(SAVEAS_);
		deleteItem = new JMenuItem(DELETE_);
		exitItem = new JMenuItem(EXIT_);
		
		newItem.addActionListener(this);
		openItem.addActionListener(this);
		saveItem.addActionListener(this);
		deleteItem.addActionListener(this);
		//saveAsItem.addActionListener(this);
		exitItem.addActionListener(this);
		
		fileMenu.add(newItem);
		fileMenu.add(openItem);
		fileMenu.add(saveItem);
		fileMenu.add(deleteItem);
		//fileMenu.add(saveAsItem);
		fileMenu.add(exitItem);
		
		undoItem = new JMenuItem(undoAction);
		redoItem = new JMenuItem(redoAction);
		cutItem = new JMenuItem(cutAction);
		copyItem = new JMenuItem(copyAction);
		pasteItem = new JMenuItem(pasteAction);
		clearItem = new JMenuItem(CLEAR_);
		selectAllItem = new JMenuItem(SELECTALL_);

		cutItem.setText(CUT_);
		copyItem.setText(COPY_);
		pasteItem.setText(PASTE_);

		clearItem.addActionListener(this);
		selectAllItem.addActionListener(this);
		
		editMenu.add(undoItem);
		editMenu.add(redoItem);
		editMenu.add(cutItem);
		editMenu.add(copyItem);
		editMenu.add(pasteItem);
		editMenu.add(clearItem);
		editMenu.add(selectAllItem);
		
		redTextItem = new JMenuItem(new StyledEditorKit.ForegroundAction(RED_,Color.red));
		orangeTextItem = new JMenuItem(new StyledEditorKit.ForegroundAction(ORANGE_,Color.orange));
		yellowTextItem = new JMenuItem(new StyledEditorKit.ForegroundAction(YELLOW_,Color.yellow));
		greenTextItem = new JMenuItem(new StyledEditorKit.ForegroundAction(GREEN_,Color.green));
		blueTextItem = new JMenuItem(new StyledEditorKit.ForegroundAction(BLUE_,Color.blue));
		cyanTextItem = new JMenuItem(new StyledEditorKit.ForegroundAction(CYAN_,Color.cyan));
		magentaTextItem = new JMenuItem(new StyledEditorKit.ForegroundAction(MAGENTA_,Color.magenta));
		blackTextItem = new JMenuItem(new StyledEditorKit.ForegroundAction(BLACK_,Color.black));

		colorMenu.add(redTextItem);
		colorMenu.add(orangeTextItem);
		colorMenu.add(yellowTextItem);
		colorMenu.add(greenTextItem);
		colorMenu.add(blueTextItem);
		colorMenu.add(cyanTextItem);
		colorMenu.add(magentaTextItem);
		colorMenu.add(blackTextItem);

		fontTypeMenu = new JMenu(FONTTYPE_);
		fontMenu.add(fontTypeMenu);
		
		String[] fontTypes = {"SansSerif", "Serif", "Monospaced", "Dialog", "DialogInput"};
		for (int i = 0; i < fontTypes.length;i++){
			JMenuItem nextTypeItem = new JMenuItem(fontTypes[i]);
			nextTypeItem.setAction(new StyledEditorKit.FontFamilyAction(fontTypes[i], fontTypes[i]));
			fontTypeMenu.add(nextTypeItem);
		}

		fontSizeMenu = new JMenu(FONTSIZE_);
		fontMenu.add(fontSizeMenu);

		int[] fontSizes = {6,8,10,12,14,16,20,24,32,36,48,72};
		for (int i = 0; i < fontSizes.length;i++){
			JMenuItem nextSizeItem = new JMenuItem(String.valueOf(fontSizes[i]));
			nextSizeItem.setAction(new StyledEditorKit.FontSizeAction(String.valueOf(fontSizes[i]), fontSizes[i]));
			fontSizeMenu.add(nextSizeItem);
		}

		boldMenuItem = new JMenuItem(boldAction);
		underlineMenuItem = new JMenuItem(underlineAction);
		italicMenuItem = new JMenuItem(italicAction);
		
		boldMenuItem.setText(BOLD_);
		underlineMenuItem.setText(UNDERLINE_);
		italicMenuItem.setText(ITALIC_);	

		styleMenu.add(boldMenuItem);
		styleMenu.add(underlineMenuItem);
		styleMenu.add(italicMenuItem);
		
		subscriptMenuItem = new JMenuItem(new SubscriptAction());
		superscriptMenuItem = new JMenuItem(new SuperscriptAction());
		strikeThroughMenuItem = new JMenuItem(new StrikeThroughAction());

		subscriptMenuItem.setText(SUBSCRIPT_);
		superscriptMenuItem.setText(SUPERSCRIPT_);
		strikeThroughMenuItem.setText(STRIKETHROUGH_);

		styleMenu.add(subscriptMenuItem);
		styleMenu.add(superscriptMenuItem);
		styleMenu.add(strikeThroughMenuItem);
		
		leftAlignMenuItem = new JMenuItem(new StyledEditorKit.AlignmentAction(LEFT_,StyleConstants.ALIGN_LEFT));
		centerMenuItem = new JMenuItem(new StyledEditorKit.AlignmentAction(CENTER_,StyleConstants.ALIGN_CENTER));
		rightAlignMenuItem = new JMenuItem(new StyledEditorKit.AlignmentAction (RIGHT_,StyleConstants.ALIGN_RIGHT));

		leftAlignMenuItem.setText(LEFT_);
		centerMenuItem.setText(CENTER_);
		rightAlignMenuItem.setText(RIGHT_);

		alignMenu.add(leftAlignMenuItem);
		alignMenu.add(centerMenuItem);
		alignMenu.add(rightAlignMenuItem);

		//Buttons
		JPanel editorControlPanel = new JPanel();
		editorControlPanel.setLayout(new FlowLayout());

		JButton cutButton = new JButton(cutAction);
		JButton copyButton = new JButton(copyAction);
		JButton pasteButton = new JButton(pasteAction);

		JButton boldButton = new JButton(boldAction);
		JButton underlineButton = new JButton(underlineAction);
		JButton italicButton = new JButton(italicAction);
		
		cutButton.setText(CUT_);
		copyButton.setText(COPY_);
		pasteButton.setText(PASTE_);
		boldButton.setText(BOLD_);
		underlineButton.setText(UNDERLINE_);
		italicButton.setText(ITALIC_);
		
		editorControlPanel.add(cutButton);
		editorControlPanel.add(copyButton);
		editorControlPanel.add(pasteButton);	
		editorControlPanel.add(boldButton);
		editorControlPanel.add(underlineButton);
		editorControlPanel.add(italicButton);
			
		JButton subscriptButton = new JButton(new SubscriptAction());
		JButton superscriptButton = new JButton(new SuperscriptAction());
		JButton strikeThroughButton = new JButton(new StrikeThroughAction());

		JPanel specialPanel = new JPanel();
		specialPanel.setLayout(new FlowLayout());

		specialPanel.add(subscriptButton);
		specialPanel.add(superscriptButton);
		specialPanel.add(strikeThroughButton);
		
		JButton leftAlignButton = new JButton(new StyledEditorKit.AlignmentAction(LEFT_,StyleConstants.ALIGN_LEFT));
		JButton centerButton = new JButton(new StyledEditorKit.AlignmentAction(CENTER_,StyleConstants.ALIGN_CENTER));
		JButton rightAlignButton = new JButton(new StyledEditorKit.AlignmentAction (RIGHT_,StyleConstants.ALIGN_RIGHT));

		leftAlignButton.setText(LEFT_);
		centerButton.setText(CENTER_);
		rightAlignButton.setText(RIGHT_);
			
		JPanel alignPanel = new JPanel();
		alignPanel.setLayout(new FlowLayout());
		alignPanel.add(leftAlignButton);
		alignPanel.add(centerButton);
		alignPanel.add(rightAlignButton);

		document.addUndoableEditListener(undoHandler);
		resetUndoManager();

		textPane = new JTextPane(document);
		textPane.setContentType("text/html");
		JScrollPane scrollPane = new JScrollPane(textPane);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension scrollPaneSize = new Dimension(5*screenSize.width/15,5*screenSize.height/15);
		scrollPane.setPreferredSize(scrollPaneSize);
		
		JPanel toolPanel = new JPanel();
		toolPanel.setLayout(new BorderLayout());
		toolPanel.add(editorControlPanel, BorderLayout.NORTH);	
		toolPanel.add(specialPanel, BorderLayout.CENTER);	
		toolPanel.add(alignPanel, BorderLayout.SOUTH);	
		getContentPane().add(menuBar, BorderLayout.NORTH);	
		getContentPane().add(toolPanel, BorderLayout.CENTER);	
		getContentPane().add(scrollPane, BorderLayout.SOUTH);			
		pack();
		setLocationRelativeTo(null);
		startNewDocument();
		show();
	}
	
	public void actionPerformed(ActionEvent e){
		String actionCommand = e.getActionCommand();
		if (actionCommand.compareTo(NEW_) == 0){
			startNewDocument();
		} else if (actionCommand.compareTo(OPEN_) == 0){
			try {
				openDocument();
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
		} else if (actionCommand.compareTo(SAVE_) == 0){
			saveDocument();
		} else if (actionCommand.compareTo(SAVEAS_) == 0){
		    saveDocumentAs();
		} else if (actionCommand.compareTo(EXIT_) == 0){
			exit();
		} else if (actionCommand.compareTo(CLEAR_) == 0){
			clear();
		} else if (actionCommand.compareTo(SELECTALL_) == 0){
			selectAll();
		} else if (actionCommand.compareTo(DELETE_) == 0){
			deleteDocument();
		}
	}
	 
	protected void resetUndoManager() {
		undo.discardAllEdits();
		undoAction.update();
		redoAction.update();
	}
	
	public int handleNewDocument(int docLength){
		if(docLength > 0){
			String message = "Do you want to save?";
			int jp = JOptionPane.showConfirmDialog(this, message);
			if (jp == JOptionPane.YES_OPTION){
				return 2;
			}
			else if(jp == JOptionPane.NO_OPTION){
				return 1;
			}
			else if(jp == JOptionPane.CANCEL_OPTION){
				return 0;
			}
		}
		else{
			return 0;
		}
		return -1;
	}
	
	public void startNewDocument(){
		Document oldDoc = textPane.getDocument();
		int temp = handleNewDocument(oldDoc.getLength());
		if(temp > 0){
			if(temp == 2){
				saveDocument();
			}
			oldDoc.removeUndoableEditListener(undoHandler);
			HTMLEditorKit editorKit = new HTMLEditorKit();
			document = (HTMLDocument)editorKit.createDefaultDocument();
			textPane.setDocument(document);	
			currentFile = null;
			setTitle("ProfileEditor");	
			textPane.getDocument().addUndoableEditListener(undoHandler);
			resetUndoManager();
		}
		else
		{
			//Blank Profile. Do nothing and keep original blank document.
		}
	}

	public void openDocument() throws IOException, BadLocationException{
			File currentFile = new File("Profile.html");
			if (currentFile.exists()){
				setTitle(currentFile.getName());	
				FileReader fr = new FileReader(currentFile);
				Document oldDoc = textPane.getDocument();
				if(oldDoc != null)
					    oldDoc.removeUndoableEditListener(undoHandler);
				HTMLEditorKit editorKit = new HTMLEditorKit();
				document = (HTMLDocument)editorKit.createDefaultDocument();
				editorKit.read(fr,document,0);
				document.addUndoableEditListener(undoHandler);
				textPane.setDocument(document);
				resetUndoManager();
			}
			else {
				String message = "You have no profile.  Would you like to create one?";
				int jp = JOptionPane.showConfirmDialog(null, message, "Profile", JOptionPane.YES_NO_OPTION);
				if (jp == JOptionPane.YES_OPTION){
					currentFile = new File(SAVEPATH_);
					saveDocumentAs();
				}
				else if(jp == JOptionPane.NO_OPTION){
					//Do nothing
				}
			}
	}

	public void saveDocument(){
		setTitle(SAVEPATH_);
		if (currentFile != null){
			try{
				FileWriter fw = new FileWriter(currentFile);
				fw.write(textPane.getText());
				fw.close();
			}catch(FileNotFoundException e){
				System.err.println(e.getMessage());			
			}catch(IOException e){
				System.err.println(e.getMessage());
			}	
		}else{
			saveDocumentAs();
		}			
	}

	public void saveDocumentAs(){
		try{
			File currentFile = new File(SAVEPATH_);
			setTitle(SAVEPATH_);	
			FileWriter fw = new FileWriter(currentFile);
			fw.write(textPane.getText());
		}catch(FileNotFoundException e){
			System.err.println(e.getMessage());			
		}catch(IOException e){
			System.err.println(e.getMessage());
		}
	}
	
	public void deleteDocument(){
		setTitle(WHISPER_);
        if (currentFile.exists()){
        	currentFile.delete();
        }
	}

	public void exit(){
		this.dispose();
	}

	public void clear(){
		startNewDocument();
	}

	public void selectAll(){
		textPane.selectAll();
	}
	
	class FrameListener extends WindowAdapter{
		public void windowClosing(WindowEvent we){
			exit();
		}
	}
	
	class SubscriptAction extends StyledEditorKit.StyledTextAction{
		public SubscriptAction(){
			super(StyleConstants.Subscript.toString());
		}
		public void actionPerformed(ActionEvent e){
			JEditorPane editor = getEditor(e);
			if (editor != null) {
				StyledEditorKit kit = getStyledEditorKit(editor);
				MutableAttributeSet attr = kit.getInputAttributes();
				boolean subscript = (StyleConstants.isSubscript(attr)) ? false : true;
				SimpleAttributeSet sas = new SimpleAttributeSet();
				StyleConstants.setSubscript(sas, subscript);
				setCharacterAttributes(editor, sas, false);
			}
		}
	}
	
	class SuperscriptAction extends StyledEditorKit.StyledTextAction{
		public SuperscriptAction(){
			super(StyleConstants.Superscript.toString());
		}
		public void actionPerformed(ActionEvent e){
			JEditorPane editor = getEditor(e);
			if (editor != null) {
				StyledEditorKit kit = getStyledEditorKit(editor);
				MutableAttributeSet attr = kit.getInputAttributes();
				boolean superscript = (StyleConstants.isSuperscript(attr)) ? false : true;
				SimpleAttributeSet sas = new SimpleAttributeSet();
				StyleConstants.setSuperscript(sas, superscript);
				setCharacterAttributes(editor, sas, false);
			}			
		}
	}
	
	class StrikeThroughAction extends StyledEditorKit.StyledTextAction{
		public StrikeThroughAction(){
			super(StyleConstants.StrikeThrough.toString());
		}
		
		public void actionPerformed(ActionEvent e){
			JEditorPane editor = getEditor(e);
			if (editor != null) {
				StyledEditorKit kit = getStyledEditorKit(editor);
				MutableAttributeSet attr = kit.getInputAttributes();
				boolean strikeThrough = (StyleConstants.isStrikeThrough(attr)) ? false : true;
				SimpleAttributeSet sas = new SimpleAttributeSet();
				StyleConstants.setStrikeThrough(sas, strikeThrough);
				setCharacterAttributes(editor, sas, false);
			}			
		}
	}
	

	class HTMLFileFilter extends javax.swing.filechooser.FileFilter{
		public boolean accept(File f){
			return ((f.isDirectory()) ||(f.getName().toLowerCase().indexOf(".htm") > 0));
		}
		
		public String getDescription(){
			return "html";
		}
	}
	
	 class UndoHandler implements UndoableEditListener {
		public void undoableEditHappened(UndoableEditEvent e) {
			undo.addEdit(e.getEdit());
			undoAction.update();
			redoAction.update();
		}
	}
	
	class UndoAction extends AbstractAction {
		public UndoAction() {
			super(UNDO_);
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e) {
			try {
				undo.undo();
			} catch (CannotUndoException ex) {
				System.out.println(ex);
				ex.printStackTrace();
			}
			update();
			redoAction.update();
		}

		protected void update() {
			if(undo.canUndo()) {
				setEnabled(true);
				putValue(Action.NAME, undo.getUndoPresentationName());
			}else {
				setEnabled(false);
				putValue(Action.NAME, UNDO_);
			}
		}
	}

	class RedoAction extends AbstractAction {
		
		public RedoAction() {
			super(REDO_);
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e) {
			try {
				undo.redo();
			} catch (CannotRedoException ex) {
				System.err.println(ex);
				ex.printStackTrace();
			}
			update();
			undoAction.update();
		}
	
		protected void update() {
			if(undo.canRedo()) {
				setEnabled(true);
				putValue(Action.NAME, undo.getRedoPresentationName());
			}else {
				setEnabled(false);
				putValue(Action.NAME, REDO_);
			}
		}
	}
}