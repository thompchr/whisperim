package org.whisperim.client;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;

import org.dyno.visual.swing.layouts.Bilateral;
import org.dyno.visual.swing.layouts.Constraints;
import org.dyno.visual.swing.layouts.GroupLayout;
import org.dyno.visual.swing.layouts.Leading;
import org.dyno.visual.swing.layouts.Trailing;

public class WhisperIMNew extends JFrame {

	private static final long serialVersionUID = 1L;
	private JEditorPane jEditorPane0;
	private JScrollPane jScrollPane0;
	private JToggleButton encryptionToggle;
	private JEditorPane jEditorPane1;
	private JScrollPane jScrollPane1;
	private JButton jButton1;
	private JLabel foreignHandleLbl;
	private static final String PREFERRED_LOOK_AND_FEEL = "javax.swing.plaf.metal.MetalLookAndFeel";
	public WhisperIMNew() {
		initComponent();
	}

	private void initComponent() {
		setLayout(new GroupLayout());
		add(getEncryptionToggle(), new Constraints(new Trailing(12, 12, 12), new Leading(12, 12, 12)));
		add(getJButton1(), new Constraints(new Leading(398, 12, 12), new Leading(311, 50, 12, 12)));
		add(getJScrollPane0(), new Constraints(new Bilateral(13, 12, 22), new Leading(62, 231, 10, 10)));
		add(getJScrollPane1(), new Constraints(new Leading(12, 368, 12, 12), new Leading(312, 49, 10, 10)));
		add(getForeignHandleLbl(), new Constraints(new Leading(46, 10, 10), new Leading(22, 12, 12)));
		setSize(488, 377);
	}

	private JLabel getForeignHandleLbl() {
		if (foreignHandleLbl == null) {
			foreignHandleLbl = new JLabel();
			foreignHandleLbl.setText("jLabel6");
		}
		return foreignHandleLbl;
	}

	private JButton getJButton1() {
		if (jButton1 == null) {
			jButton1 = new JButton();
			jButton1.setText("jButton1");
		}
		return jButton1;
	}

	private JScrollPane getJScrollPane1() {
		if (jScrollPane1 == null) {
			jScrollPane1 = new JScrollPane();
			jScrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			jScrollPane1.add(getJEditorPane1());
			jScrollPane1.setViewportView(getJEditorPane1());
		}
		return jScrollPane1;
	}

	private JEditorPane getJEditorPane1() {
		if (jEditorPane1 == null) {
			jEditorPane1 = new JEditorPane();
			jEditorPane1.setText("jEditorPane1");
		}
		return jEditorPane1;
	}

	private JToggleButton getEncryptionToggle() {
		if (encryptionToggle == null) {
			encryptionToggle = new JToggleButton();
			encryptionToggle.setText("jToggleButton0");
		}
		return encryptionToggle;
	}

	private JScrollPane getJScrollPane0() {
		if (jScrollPane0 == null) {
			jScrollPane0 = new JScrollPane();
			jScrollPane0.add(getJEditorPane0());
			jScrollPane0.setViewportView(getJEditorPane0());
		}
		return jScrollPane0;
	}

	private JEditorPane getJEditorPane0() {
		if (jEditorPane0 == null) {
			jEditorPane0 = new JEditorPane();
			jEditorPane0.setEditable(false);
			jEditorPane0.setText("jEditorPane0");
		}
		return jEditorPane0;
	}
	
	private void receiveMessage(){
		
	}

}
