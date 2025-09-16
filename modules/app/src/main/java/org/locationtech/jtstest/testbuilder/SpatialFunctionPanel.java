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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.locationtech.jts.operation.buffer.BufferParameters;
import org.locationtech.jts.util.Stopwatch;
import org.locationtech.jtstest.geomfunction.BaseGeometryFunction;
import org.locationtech.jtstest.geomfunction.GeometryFunction;
import org.locationtech.jtstest.geomfunction.GeometryFunctionRegistry;
import org.locationtech.jtstest.geomfunction.RepeaterGeometryFunction;
import org.locationtech.jtstest.geomfunction.SpreaderGeometryFunction;
import org.locationtech.jtstest.testbuilder.controller.JTSTestBuilderController;
import org.locationtech.jtstest.testbuilder.event.GeometryFunctionEvent;
import org.locationtech.jtstest.testbuilder.event.GeometryFunctionListener;
import org.locationtech.jtstest.testbuilder.event.SpatialFunctionPanelEvent;
import org.locationtech.jtstest.testbuilder.event.SpatialFunctionPanelListener;
import org.locationtech.jtstest.testbuilder.ui.SwingUtil;
import org.locationtech.jtstest.util.ClassUtil;

/**
 * @version 1.7
 */
public class SpatialFunctionPanel extends JPanel implements FunctionPanel {
	private static final EmptyBorder LABEL_BORDER = new EmptyBorder(3, 5, 3, 5);

	private static final String[] PARAM_DEFAULT = {"10", "0", "0", "0", "0"};

	private static final String[] capStyleItems = new String[]{"Round", "Flat", "Square"};
	private static final Object[] capStyleValues = new Object[]{BufferParameters.CAP_ROUND, BufferParameters.CAP_FLAT,
			BufferParameters.CAP_SQUARE};
	private static final String[] joinStyleItems = new String[]{"Round", "Mitre", "Bevel"};
	private static final Object[] joinStyleValues = new Object[]{BufferParameters.JOIN_ROUND,
			BufferParameters.JOIN_MITRE, BufferParameters.JOIN_BEVEL};

	public static int attributeParamOffset(GeometryFunction func) {
		return func.isBinary() ? 1 : 0;
	}

	static void hideAllParams(JComponent[] paramComp, JLabel[] paramLabel) {
		for (int i = 0; i < paramComp.length; i++) {
			paramComp[i].setVisible(false);
			paramLabel[i].setVisible(false);
		}
	}

	static void initLabels(JLabel[] paramLabel) {
		for (JLabel lbl : paramLabel) {
			lbl.setHorizontalAlignment(SwingConstants.RIGHT);
			lbl.setBorder(LABEL_BORDER);
		}
	}

	private static int numNonGeomParams(GeometryFunction func) {
		int count = 0;
		Class[] paramTypes = func.getParameterTypes();
		for (Class paramType : paramTypes) {
			if (!ClassUtil.isGeometry(paramType))
				count++;
		}
		return count;
	}

	private static void setToolTipText(JComponent control, GeometryFunction func, int i) {
		String txt = null;
		if (func.getParameterTypes().length > i) {
			txt = "Enter a " + func.getParameterTypes()[i].getSimpleName();
		}
		control.setToolTipText(txt);
	}

	static void updateParameters(GeometryFunction func, JComponent[] paramComp, JLabel[] paramLabel) {
		int numNonGeomParams = numNonGeomParams(func);
		int indexOffset = BaseGeometryFunction.firstScalarParamIndex(func);
		for (int i = 0; i < paramComp.length; i++) {
			boolean isUsed = numNonGeomParams > i;
			if (isUsed) {
				paramLabel[i].setText(func.getParameterNames()[i + indexOffset]);
			}
			paramComp[i].setVisible(isUsed);
			paramLabel[i].setVisible(isUsed);
			SpatialFunctionPanel.setToolTipText(paramComp[i], func, i);
		}
	}

	public static String valOrDefault(String s, String defaultVal) {
		if (s.length() > 0)
			return s;
		return defaultVal;
	}

	private final JButton btnClearResult = new JButton();
	private JButton btnExecEach;
	private final JComboBox cbCapStyle = new JComboBox();

	private final JCheckBox cbExecAuto = new JCheckBox();
	private final JCheckBox cbExecEachA = new JCheckBox();

	private final JCheckBox cbExecEachB = new JCheckBox();
	private final JCheckBox cbExecRepeat = new JCheckBox();

	private final JComboBox cbJoinStyle = new JComboBox();
	private final ImageIcon clearIcon = new ImageIcon(this.getClass().getResource("clear.gif"));
	private GeometryFunction currentFunc = null;
	private JButton execButton = new JButton();
	private JButton execToNewButton = new JButton();
	private final ImageIcon expandDownIcon = new ImageIcon(this.getClass().getResource("Expand-Down.png"));
	private final Map<GeometryFunction, String> funcParamMap = new HashMap<GeometryFunction, String>();
	private final JLabel lblCapStyle = new JLabel();

	private final JLabel lblDistance = new JLabel();
	private final JLabel lblFunction = new JLabel();
	private final JLabel lblFunctionName = new JLabel();
	private final JLabel lblJoinStyle = new JLabel();
	private final JLabel lblMitreLimit = new JLabel();
	private final JLabel lblQuadSegs = new JLabel();
	private final JLabel[] paramLabel = {lblDistance, lblQuadSegs, lblCapStyle, lblJoinStyle, lblMitreLimit};
	private transient Vector spatialFunctionPanelListeners;
	private Stopwatch timer;
	private final JTextField txtDistance = new JTextField();
	private final JTextField txtMitreLimit = new JTextField();

	private final JTextField txtQuadrantSegs = new JTextField();
	private final JTextField txtRepeatCount = new JTextField();
	private final JComponent[] paramComp = {txtDistance, txtQuadrantSegs, cbCapStyle, cbJoinStyle, txtMitreLimit};

	final BorderLayout borderLayout1 = new BorderLayout();
	final BorderLayout borderLayout2 = new BorderLayout();
	// GeometryFunctionListPanel geomFuncPanel = new GeometryFunctionListPanel();
	final GeometryFunctionTreePanel geomFuncPanel = new GeometryFunctionTreePanel();

	GridLayout gridLayout1 = new GridLayout();

	final GridLayout gridLayout2 = new GridLayout();

	final JPanel panelExec = new JPanel();

	final JPanel panelExecMeta = new JPanel();

	final JPanel panelExecParam = new JPanel();

	final JPanel panelFunction = new JPanel();

	final JPanel panelParam = new JPanel();

	public SpatialFunctionPanel() {
		try {
			uiInit();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public synchronized void addSpatialFunctionPanelListener(SpatialFunctionPanelListener l) {
		Vector v = spatialFunctionPanelListeners == null
				? new Vector(2)
				: (Vector) spatialFunctionPanelListeners.clone();
		if (!v.contains(l)) {
			v.addElement(l);
			spatialFunctionPanelListeners = v;
		}
	}

	void clearExtended() {
		cbExecRepeat.setSelected(false);
		cbExecEachA.setSelected(false);
		cbExecEachB.setSelected(false);
	}

	public void clearFunction() {
		setCurrentFunction(null);
	}

	void clearResultButton_actionPerformed(ActionEvent e) {
		clearFunction();
	}

	public void enableExecuteControl(boolean isEnabled) {
		execButton.setEnabled(isEnabled);
		execToNewButton.setEnabled(isEnabled);
	}

	public void execFunction(GeometryFunction func, boolean createNew) {
		currentFunc = func;
		if (currentFunc == null)
			return;
		JTSTestBuilderController.resultController().execute(createNew);
	}

	public void execFunction(boolean createNew) {
		execFunction(getMetaFunction(), createNew);
	}

	protected void fireFunctionExecuted(SpatialFunctionPanelEvent e) {
		if (spatialFunctionPanelListeners != null) {
			Vector listeners = spatialFunctionPanelListeners;
			int count = listeners.size();
			for (int i = 0; i < count; i++) {
				((SpatialFunctionPanelListener) listeners.elementAt(i)).functionExecuted(e);
			}
		}
	}

	private void functionChanged(GeometryFunction func) {
		saveParameter(currentFunc);
		currentFunc = func;
		lblFunctionName.setText(func.getName());
		lblFunctionName.setToolTipText(GeometryFunctionRegistry.functionDescriptionHTML(func));

		updateParameters(func, paramComp, paramLabel);
		recallParameter(func);

		execButton.setEnabled(true);
		execToNewButton.setEnabled(true);
		cbExecAuto.setSelected(false);
	}

	public GeometryFunction getFunction() {
		return currentFunc;
	}

	public Object[] getFunctionParams() {
		if (currentFunc == null)
			return null;
		Class[] paramTypes = currentFunc.getParameterTypes();
		Object[] paramVal = new Object[paramTypes.length];

		for (int i = 0; i < paramVal.length; i++) {
			Object valRaw = getParamValue(i);
			paramVal[i] = SwingUtil.coerce(valRaw, paramTypes[i]);
		}
		return paramVal;
	}

	GeometryFunction getMetaFunction() {
		GeometryFunction funToRun = geomFuncPanel.getFunction();
		if (!isMetaFunctionEnabled())
			return funToRun;

		if (isFunctionRepeated()) {
			int count = SwingUtil.getInteger(txtRepeatCount, 10);
			funToRun = new RepeaterGeometryFunction(funToRun, count);
		}
		if (isFunctionEach()) {
			funToRun = new SpreaderGeometryFunction(funToRun, isEachA(), isEachB());
		}
		return funToRun;
	}

	private Object getParamValue(int index) {
		if (currentFunc.isBinary() && index == 0)
			return JTSTestBuilder.controller().getGeometryB();

		int attrIndex = index - attributeParamOffset(currentFunc);

		return switch (attrIndex) {
			case 0 -> valOrDefault(SwingUtil.value(txtDistance), PARAM_DEFAULT[0]);
			case 1 -> valOrDefault(SwingUtil.value(txtQuadrantSegs), PARAM_DEFAULT[1]);
			case 2 -> SwingUtil.value(cbCapStyle, capStyleValues);
			case 3 -> SwingUtil.value(cbJoinStyle, joinStyleValues);
			case 4 -> valOrDefault(SwingUtil.value(txtMitreLimit), PARAM_DEFAULT[4]);
			default -> null;
		};
	}

	public boolean isAutoExecute() {
		return cbExecAuto.isSelected();
	}

	private boolean isEachA() {
		return cbExecEachA.isSelected();
	}

	private boolean isEachB() {
		return cbExecEachB.isSelected();
	}

	private boolean isFunctionEach() {
		return cbExecEachA.isSelected() || cbExecEachB.isSelected();
	}

	private boolean isFunctionRepeated() {
		return cbExecRepeat.isSelected();
	}

	public boolean isFunctionSelected() {
		return currentFunc != null;
	}

	private boolean isMetaFunctionEnabled() {
		return panelExecMeta.isVisible();
	}

	private void recallParameter(GeometryFunction func) {
		if (!funcParamMap.containsKey(func))
			return;
		String val = funcParamMap.get(func);
		txtDistance.setText(val);
	}

	public synchronized void removeSpatialFunctionPanelListener(SpatialFunctionPanelListener l) {
		if (spatialFunctionPanelListeners != null && spatialFunctionPanelListeners.contains(l)) {
			Vector v = (Vector) spatialFunctionPanelListeners.clone();
			v.removeElement(l);
			spatialFunctionPanelListeners = v;
		}
	}

	// =================================================
	// Events
	// =================================================

	private void saveParameter(GeometryFunction func) {
		String val = SwingUtil.value(txtDistance);
		funcParamMap.put(func, val);
	}

	private void setCurrentFunction(GeometryFunction func) {
		currentFunc = func;
		// fire execution event even if null, to set UI appropriately
		fireFunctionExecuted(new SpatialFunctionPanelEvent(this));
	}

	void uiInit() throws Exception {
		this.setLayout(borderLayout1);

		// geomFuncPanel.populate(JTSTestBuilder.getFunctionRegistry().getGeometryFunctions());
		geomFuncPanel.populate(JTSTestBuilder.getFunctionRegistry().getCategorizedGeometryFunctions());

		panelParam.setLayout(gridLayout2);
		gridLayout2.setRows(5);
		gridLayout2.setColumns(2);
		panelExec.setLayout(new FlowLayout());
		panelExecParam.setLayout(borderLayout2);

		lblFunction.setText("Function");
		lblFunction.setHorizontalAlignment(SwingConstants.RIGHT);
		lblFunction.setBorder(LABEL_BORDER); // top,left,bottom,right

		lblFunctionName.setHorizontalAlignment(SwingConstants.LEFT);
		lblFunctionName.setFont(new java.awt.Font("Dialog", Font.PLAIN, 14));
		lblFunctionName.setForeground(Color.BLUE);
		lblFunctionName.setBorder(new EmptyBorder(0, 10, 2, 0));

		lblDistance.setText("Distance");

		txtDistance.setMaximumSize(new Dimension(25, 2147483647));
		txtDistance.setMinimumSize(new Dimension(25, 21));
		txtDistance.setPreferredSize(new Dimension(25, 17));
		txtDistance.setText(PARAM_DEFAULT[0]);
		txtDistance.setHorizontalAlignment(SwingConstants.RIGHT);

		lblQuadSegs.setText("Quadrant Segs");
		txtQuadrantSegs.setHorizontalAlignment(SwingConstants.RIGHT);

		lblCapStyle.setText("Cap Style");
		ComboBoxModel modelCapStyle = new DefaultComboBoxModel(capStyleItems);
		cbCapStyle.setModel(modelCapStyle);

		lblJoinStyle.setText("Join Style");
		ComboBoxModel modelJoinStyle = new DefaultComboBoxModel(joinStyleItems);
		cbJoinStyle.setModel(modelJoinStyle);

		lblMitreLimit.setText("Mitre Limit");
		txtMitreLimit.setHorizontalAlignment(SwingConstants.RIGHT);

		initLabels(paramLabel);

		// panelParam.add(lblFunction);
		// panelParam.add(lblFunctionName);
		panelParam.add(lblDistance);
		panelParam.add(txtDistance);
		panelParam.add(lblQuadSegs);
		panelParam.add(txtQuadrantSegs);
		panelParam.add(lblCapStyle);
		panelParam.add(cbCapStyle);
		panelParam.add(lblJoinStyle);
		panelParam.add(cbJoinStyle);
		panelParam.add(lblMitreLimit);
		panelParam.add(txtMitreLimit);

		panelFunction.setLayout(new BorderLayout());
		panelFunction.add(lblFunctionName, BorderLayout.NORTH);
		panelFunction.add(panelParam, BorderLayout.CENTER);

		cbExecEachA.setToolTipText("Compute for each A geometry element");
		cbExecEachA.setText("Each A");

		cbExecEachB.setToolTipText("Compute for each B geometry element");
		cbExecEachB.setText("Each B");

		cbExecRepeat.setToolTipText("Repeat function a number of times, incrementing the first parameter");
		cbExecRepeat.setText("Repeat");

		cbExecAuto.setToolTipText("Execute function when geometry changes");
		cbExecAuto.setText("Live Exec");

		txtRepeatCount.setMaximumSize(new Dimension(25, 2147483647));
		txtRepeatCount.setMinimumSize(new Dimension(30, 21));
		txtRepeatCount.setPreferredSize(new Dimension(30, 21));
		txtRepeatCount.setText("10");
		txtRepeatCount.setHorizontalAlignment(SwingConstants.RIGHT);

		execButton = SwingUtil.createButton(AppIcons.EXECUTE, AppStrings.TIP_EXECUTE, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				execFunction(false);
			}
		});
		execButton.setEnabled(false);

		execToNewButton = SwingUtil.createButton("New", AppIcons.EXECUTE, "Compute function result to a new case",
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						execFunction(true);
					}
				});
		execToNewButton.setEnabled(false);

		JButton btnShowExecExt = SwingUtil.createButton(expandDownIcon, "Show extended/meta Compute tools",
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						clearExtended();
						panelExecMeta.setVisible(!panelExecMeta.isVisible());
					}
				});
		btnShowExecExt.setPreferredSize(new Dimension(20, 20));
		btnShowExecExt.setBorder(BorderFactory.createEmptyBorder());
		btnShowExecExt.setContentAreaFilled(false);
		btnShowExecExt.setFocusable(false);

		panelExec.add(execButton);
		// disabled until behaviour is worked out
		panelExec.add(execToNewButton);
		// panelExec.add(btnShowExecExt);

		JPanel panelExecHolder = new JPanel();
		panelExecHolder.setLayout(new BorderLayout());
		panelExecHolder.add(panelExec, BorderLayout.CENTER);
		panelExecHolder.add(btnShowExecExt, BorderLayout.EAST);

		JPanel panelExecMeta1 = new JPanel();
		panelExecMeta1.setLayout(new FlowLayout());
		panelExecMeta1.add(cbExecEachA);
		panelExecMeta1.add(cbExecEachB);
		panelExecMeta1.add(cbExecRepeat);
		panelExecMeta1.add(txtRepeatCount);

		JPanel panelExecMeta2 = new JPanel();
		panelExecMeta2.setLayout(new FlowLayout());
		panelExecMeta2.add(cbExecAuto);

		panelExecMeta.setLayout(new BoxLayout(panelExecMeta, BoxLayout.Y_AXIS));
		panelExecMeta.add(panelExecMeta1);
		panelExecMeta.add(panelExecMeta2);
		panelExecMeta.setVisible(false);

		JPanel panelExecControl = new JPanel();
		panelExecControl.setLayout(new BoxLayout(panelExecControl, BoxLayout.Y_AXIS));
		panelExecControl.add(panelExecHolder);
		panelExecControl.add(panelExecMeta);

		panelExecParam.add(panelFunction, BorderLayout.CENTER);
		panelExecParam.add(panelExecControl, BorderLayout.SOUTH);

		this.add(geomFuncPanel, BorderLayout.CENTER);
		this.add(panelExecParam, BorderLayout.SOUTH);

		GeometryFunctionListener gfListener = new GeometryFunctionListener() {
			public void functionInvoked(GeometryFunctionEvent e) {
				execFunction(e.getFunction(), false);
			}

			public void functionSelected(GeometryFunctionEvent e) {
				functionChanged(e.getFunction());
			}
		};
		geomFuncPanel.addGeometryFunctionListener(gfListener);

		hideAllParams(paramComp, paramLabel);
	}
}
