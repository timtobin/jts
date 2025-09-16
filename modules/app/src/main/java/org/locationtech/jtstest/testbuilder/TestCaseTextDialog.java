/*
 * Copyright (c) 2016 Vivid Solutions.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v20.html
 * and the Eclipse Distribution License is available at
 *
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.locationtech.jtstest.testbuilder;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.locationtech.jtstest.testbuilder.io.IOUtil;
import org.locationtech.jtstest.testbuilder.io.JavaTestWriter;
import org.locationtech.jtstest.testbuilder.io.SVGTestWriter;
import org.locationtech.jtstest.testbuilder.io.XMLTestWriter;
import org.locationtech.jtstest.testbuilder.model.TestCaseEdit;

/**
 * @version 1.7
 */
public class TestCaseTextDialog extends JDialog {
	private TestCaseEdit test;
	final JPanel allOptionsPanel = new JPanel();
	final BorderLayout borderLayout1 = new BorderLayout();
	final BorderLayout borderLayout2 = new BorderLayout();
	final BorderLayout borderLayout3 = new BorderLayout();
	final JButton btnCopy = new JButton();
	final JButton btnOk = new JButton();
	final JPanel cmdButtonPanel = new JPanel();
	// ----------------------------------
	final JPanel dialogPanel = new JPanel();
	final JPanel functionsPanel = new JPanel();
	BoxLayout boxLayout1 = new BoxLayout(functionsPanel, BoxLayout.Y_AXIS);
	JCheckBox intersectsCB = new JCheckBox();
	final JPanel jPanel1 = new JPanel();
	final JScrollPane jScrollPane1 = new JScrollPane();
	final JRadioButton rbGML = new JRadioButton();
	final JRadioButton rbJTSJava = new JRadioButton();

	final JRadioButton rbSVG = new JRadioButton();
	final JRadioButton rbTestCaseJava = new JRadioButton();
	final JRadioButton rbWKB = new JRadioButton();
	final JRadioButton rbWKT = new JRadioButton();
	final JRadioButton rbWKTFormatted = new JRadioButton();
	final JRadioButton rbXML = new JRadioButton();
	final JRadioButton rbXMLWKB = new JRadioButton();
	final ButtonGroup textFormatGroup = new ButtonGroup();
	final JPanel textFormatPanel = new JPanel();

	final JTextArea txtGeomView = new JTextArea();

	public TestCaseTextDialog() {
		this(null, "", false);
	}

	public TestCaseTextDialog(Frame frame, String title, boolean modal) {
		super(frame, title, modal);
		try {
			jbInit();
			pack();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	void btnOk_actionPerformed(ActionEvent e) {
		setVisible(false);
	}

	void btnSelect_actionPerformed(ActionEvent e) {
		txtGeomView.selectAll();
		txtGeomView.copy();
	}

	void jbInit() throws Exception {
		dialogPanel.setLayout(borderLayout1);
		jScrollPane1.setPreferredSize(new Dimension(500, 300));
		txtGeomView.setLineWrap(true);
		jPanel1.setLayout(borderLayout2);
		btnCopy.setEnabled(true);
		btnCopy.setText("Copy");
		btnCopy.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				btnSelect_actionPerformed(e);
			}
		});
		btnOk.setToolTipText("");
		btnOk.setText("Close");
		btnOk.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				btnOk_actionPerformed(e);
			}
		});
		rbXML.setText("Test XML");
		rbXML.setToolTipText("");
		rbXML.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				rbXML_actionPerformed(e);
			}
		});
		rbXMLWKB.setText("Test XML - WKB");
		rbXMLWKB.setToolTipText("");
		rbXMLWKB.addActionListener(new java.awt.event.ActionListener() {

			public void actionPerformed(ActionEvent e) {
				rbXMLWKB_actionPerformed(e);
			}
		});
		rbSVG.setText("SVG");
		rbSVG.setToolTipText("");
		rbSVG.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rbSVG_actionPerformed(e);
			}
		});

		rbTestCaseJava.setText("TestCase Java");
		rbTestCaseJava.setToolTipText("");
		rbTestCaseJava.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rbTestCaseJava_actionPerformed(e);
			}
		});
		rbJTSJava.setEnabled(false);
		rbJTSJava.setText("JTS Java ");

		rbWKT.setText("WKT");
		rbWKT.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rbWKT_actionPerformed(e);
			}
		});

		rbWKTFormatted.setText("WKT-Formatted");
		rbWKTFormatted.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rbWKTFormatted_actionPerformed(e);
			}
		});

		rbWKB.setText("WKB");
		rbWKB.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rbWKB_actionPerformed(e);
			}
		});
		rbGML.setText("GML");
		rbGML.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rbGML_actionPerformed(e);
			}
		});
		getContentPane().add(dialogPanel);
		dialogPanel.add(jScrollPane1, BorderLayout.CENTER);
		dialogPanel.add(jPanel1, BorderLayout.SOUTH);
		jPanel1.add(cmdButtonPanel, BorderLayout.SOUTH);
		cmdButtonPanel.add(btnCopy, null);
		cmdButtonPanel.add(btnOk, null);
		textFormatPanel.add(rbWKT, null);
		textFormatPanel.add(rbWKTFormatted, null);
		textFormatPanel.add(rbWKB, null);
		textFormatPanel.add(rbGML, null);
		textFormatPanel.add(rbSVG, null);
		textFormatPanel.add(rbTestCaseJava, null);
		textFormatPanel.add(rbXML, null);
		textFormatPanel.add(rbXMLWKB, null);
		textFormatPanel.add(rbJTSJava, null);
		jScrollPane1.getViewport().add(txtGeomView, null);

		allOptionsPanel.setLayout(borderLayout3);
		allOptionsPanel.add(textFormatPanel, BorderLayout.NORTH);
		allOptionsPanel.add(functionsPanel, BorderLayout.CENTER);

		jPanel1.add(allOptionsPanel, BorderLayout.CENTER);

		textFormatGroup.add(rbJTSJava);
		textFormatGroup.add(rbTestCaseJava);
		textFormatGroup.add(rbXML);
		textFormatGroup.add(rbXMLWKB);
		textFormatGroup.add(rbWKT);
		textFormatGroup.add(rbWKTFormatted);
		textFormatGroup.add(rbWKB);
		textFormatGroup.add(rbGML);
		textFormatGroup.add(rbSVG);
	}

	void rbGML_actionPerformed(ActionEvent e) {
		writeView(IOUtil.toGML(test.getGeometry(0)), IOUtil.toGML(test.getGeometry(1)), IOUtil.toGML(test.getResult()));
	}

	void rbSVG_actionPerformed(ActionEvent e) {
		txtGeomView.setText(SVGTestWriter.writeTestSVG(test));
	}

	void rbTestCaseJava_actionPerformed(ActionEvent e) {
		txtGeomView.setText((new JavaTestWriter()).write(test));
	}

	void rbWKB_actionPerformed(ActionEvent e) {
		writeView(IOUtil.toWKBHex(test.getGeometry(0)), IOUtil.toWKBHex(test.getGeometry(1)),
				IOUtil.toWKBHex(test.getResult()));
	}

	void rbWKTFormatted_actionPerformed(ActionEvent e) {
		writeView(IOUtil.toWKT(test.getGeometry(0), true), IOUtil.toWKT(test.getGeometry(1), true),
				IOUtil.toWKT(test.getResult(), true));
	}

	void rbWKT_actionPerformed(ActionEvent e) {
		writeView(test.getGeometry(0) == null ? null : test.getGeometry(0).toString(),
				test.getGeometry(1) == null ? null : test.getGeometry(1).toString(),
				test.getResult() == null ? null : test.getResult().toString());
	}

	void rbXMLWKB_actionPerformed(ActionEvent e) {
		txtGeomView.setText((new XMLTestWriter()).getTestXML(test, false));
	}

	void rbXML_actionPerformed(ActionEvent e) {
		txtGeomView.setText((new XMLTestWriter()).getTestXML(test));
	}

	public void setTestCase(TestCaseEdit test) {
		this.test = test;
		// choose default format
		rbXML.setEnabled(true);
		rbXML.doClick();
	}

	private void writeView(String a, String b, String result) {
		txtGeomView.setText("");
		writeViewGeometry(AppStrings.GEOM_LABEL_A, a);
		writeViewGeometry(AppStrings.GEOM_LABEL_B, b);
		writeViewGeometry(AppStrings.GEOM_LABEL_RESULT, result);
	}

	private void writeViewGeometry(String tag, String str) {
		if (str == null || str.length() <= 0)
			return;
		txtGeomView.append(tag + ":\n\n");
		txtGeomView.append(str);
		txtGeomView.append("\n\n");
	}
}
