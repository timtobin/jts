/*
 * Copyright (c) 2020 Martin Davis.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v20.html
 * and the Eclipse Distribution License is available at
 *
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.locationtech.jtstest.testbuilder.ui.render;

import java.awt.Color;

import org.locationtech.jtstest.testbuilder.AppColors;

/**
 * Settings to configure the appearance of the Geometry View.
 *
 * @author Martin Davis
 */
public class ViewStyle {

	// the default values here are the ones shown in UI on app startup

	private Color borderClr = Color.GRAY;

	private Color clrBackground = AppColors.GEOM_VIEW_BACKGROUND;
	private boolean isBorderEnabled;
	private boolean isGridEnabled = true;
	private boolean isLegendBorderEnabled = true;
	private boolean isLegendEnabled = false;

	private boolean isLegendMetricsEnabled;
	private boolean isLegendStatsEnabled;
	private boolean isTitleBorderEnabled = true;
	private boolean isTitleEnabled = false;

	private Color legendFillClr = Color.WHITE;

	private String title = "";
	private Color titleFillClr = Color.WHITE;

	public ViewStyle() {
	}

	public Color getBackground() {
		return clrBackground;
	}

	public Color getBorderColor() {
		return borderClr;
	}

	public Color getLegendFill() {
		return legendFillClr;
	}

	public String getTitle() {
		return title;
	}

	public Color getTitleFill() {
		return titleFillClr;
	}

	public boolean isBorderEnabled() {
		return isBorderEnabled;
	}

	public boolean isGridEnabled() {
		return isGridEnabled;
	}

	public boolean isLegendBorderEnabled() {
		return isLegendBorderEnabled;
	}

	public boolean isLegendEnabled() {
		return isLegendEnabled;
	}

	public boolean isLegendMetricsEnabled() {
		return isLegendMetricsEnabled;
	}

	public boolean isLegendStatsEnabled() {
		return isLegendStatsEnabled;
	}

	public boolean isTitleBorderEnabled() {
		return isTitleBorderEnabled;
	}

	public boolean isTitleEnabled() {
		return isTitleEnabled;
	}

	public void setBackground(Color clrBackground) {
		this.clrBackground = clrBackground;
	}

	public void setBorderColor(Color clr) {
		borderClr = clr;
	}

	public void setBorderEnabled(boolean isEnabled) {
		isBorderEnabled = isEnabled;
	}

	public void setGridEnabled(boolean isEnabled) {
		this.isGridEnabled = isEnabled;
	}

	public void setLegendBorderEnabled(boolean isEnabled) {
		isLegendBorderEnabled = isEnabled;
	}

	public void setLegendEnabled(boolean isEnabled) {
		this.isLegendEnabled = isEnabled;
	}

	public void setLegendFill(Color fillClr) {
		legendFillClr = fillClr;
	}

	public void setLegendMetricsEnabled(boolean isEabled) {
		this.isLegendMetricsEnabled = isEabled;
	}

	public void setLegendStatsEnabled(boolean isEabled) {
		this.isLegendStatsEnabled = isEabled;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setTitleBorderEnabled(boolean isEnabled) {
		isTitleBorderEnabled = isEnabled;
	}

	public void setTitleEnabled(boolean isEnabled) {
		this.isTitleEnabled = isEnabled;
	}

	public void setTitleFill(Color fillClr) {
		this.titleFillClr = fillClr;
	}
}
