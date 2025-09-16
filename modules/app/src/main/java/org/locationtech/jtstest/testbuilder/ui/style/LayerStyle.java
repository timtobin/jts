/*
 * Copyright (c) 2019 Martin Davis.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v20.html
 * and the Eclipse Distribution License is available at
 *
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.locationtech.jtstest.testbuilder.ui.style;

import java.awt.Color;
import java.awt.Graphics2D;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.linearref.LengthIndexedLine;
import org.locationtech.jts.operation.buffer.BufferParameters;
import org.locationtech.jts.operation.buffer.OffsetCurveBuilder;
import org.locationtech.jtstest.testbuilder.ui.ColorUtil;
import org.locationtech.jtstest.testbuilder.ui.Viewport;

public class LayerStyle implements Style {

	public static final int INIT_OFFSET_SIZE = 5;

	static Geometry offsetLine(Geometry geom, double distance) {
		BufferParameters bufParams = new BufferParameters();
		OffsetCurveBuilder ocb = new OffsetCurveBuilder(geom.getFactory().getPrecisionModel(), bufParams);
		Coordinate[] pts = ocb.getOffsetCurve(geom.getCoordinates(), distance);
		Geometry offsetLine = geom.getFactory().createLineString(pts);
		Geometry trimLine = trimLine(offsetLine, Math.abs(distance * 1.5));
		return trimLine;
	}

	private static Geometry trimLine(Geometry line, double distance) {
		double len = line.getLength();
		if (len < 2 * distance)
			return line;
		LengthIndexedLine indLine = new LengthIndexedLine(line);
		return indLine.extractLine(distance, len - distance);
	}

	private StyleList decoratorStyle;
	private CircleLineEndStyle endPointStyle;
	private StyleGroup endPointsStyle;

	private int fillType = Palette.TYPE_BASIC;
	private final BasicStyle geomStyle;
	private boolean isOffsetLine;
	private boolean isShift;
	private DataLabelStyle labelStyle;
	private ArrowLineEndStyle lineArrowStyle;
	private CircleLineEndStyle lineCircleStyle;
	private int offsetSize = INIT_OFFSET_SIZE;

	private StyleGroup orientStyle;

	private ArrowSegmentStyle segArrowStyle;

	private SegmentIndexStyle segIndexStyle;

	private CircleLineEndStyle startPointStyle;

	private PolygonStructureStyle structureStyle;

	// private TintBandStyle tintBandStyle;

	private VertexLabelStyle vertexLabelStyle;

	private VertexStyle vertexStyle;

	public LayerStyle(BasicStyle geomStyle) {
		this.geomStyle = geomStyle;
		initDecorators(geomStyle);
	}

	public LayerStyle(BasicStyle geomStyle, StyleList decoratorStyle) {
		this.geomStyle = geomStyle;
		this.decoratorStyle = decoratorStyle;
	}

	public LayerStyle(LayerStyle layerStyle) {
		this.geomStyle = layerStyle.geomStyle.copy();
		initDecorators(geomStyle);
		update(layerStyle);
		isOffsetLine = layerStyle.isOffsetLine;
		offsetSize = layerStyle.offsetSize;
	}

	public LayerStyle copy() {
		return new LayerStyle(this);
	}

	public StyleList getDecoratorStyle() {
		return decoratorStyle;
	}

	public int getFillType() {
		return fillType;
	}

	public BasicStyle getGeomStyle() {
		return geomStyle;
	}

	public Color getLabelColor() {
		return labelStyle.getColor();
	}

	public int getLabelSize() {
		return labelStyle.getSize();
	}

	public int getOffsetSize() {
		return offsetSize;
	}

	public Color getVertexColor() {
		return vertexStyle.getColor();
	}

	public int getVertexSize() {
		return vertexStyle.getSize();
	}

	public int getVertexSymbol() {
		return vertexStyle.getSymbol();
	}

	private void initDecorators(BasicStyle style) {
		vertexStyle = new VertexStyle(style.getLineColor());
		vertexLabelStyle = new VertexLabelStyle(style.getLineColor());
		labelStyle = new DataLabelStyle(ColorUtil.opaque(style.getLineColor().darker()));

		segArrowStyle = new ArrowSegmentStyle(ColorUtil.lighter(style.getLineColor(), 0.8));
		lineArrowStyle = new ArrowLineEndStyle(ColorUtil.lighter(style.getLineColor(), 0.5), false, true);
		lineCircleStyle = new CircleLineEndStyle(ColorUtil.lighter(style.getLineColor(), 0.5), 6, 8, true, true);
		orientStyle = new StyleGroup(segArrowStyle, lineArrowStyle, lineCircleStyle);

		double endPtSize = 2 * vertexStyle.getSize();
		startPointStyle = new CircleLineEndStyle(style.getLineColor(), endPtSize, true, true);
		endPointStyle = new CircleLineEndStyle(style.getLineColor(), endPtSize, false, true);
		endPointsStyle = new StyleGroup(startPointStyle, endPointStyle);

		structureStyle = new PolygonStructureStyle(ColorUtil.opaque(style.getLineColor()));
		segIndexStyle = new SegmentIndexStyle(ColorUtil.opaque(style.getLineColor().darker()));

		// tintBandStyle = new TintBandStyle();

		// order is important here
		StyleList styleList = new StyleList();
		// styleList.add(tintBandStyle);
		styleList.add(vertexLabelStyle);
		styleList.add(vertexStyle);
		styleList.add(endPointsStyle);
		styleList.add(orientStyle);
		styleList.add(structureStyle);
		styleList.add(segIndexStyle);
		styleList.add(labelStyle);

		styleList.setEnabled(endPointsStyle, false);
		styleList.setEnabled(labelStyle, false);
		styleList.setEnabled(orientStyle, false);
		styleList.setEnabled(structureStyle, false);
		styleList.setEnabled(segIndexStyle, false);
		styleList.setEnabled(vertexLabelStyle, false);

		decoratorStyle = styleList;
	}

	public boolean isEndpoints() {
		return decoratorStyle.isEnabled(endPointsStyle);
	}

	public boolean isLabel() {
		return decoratorStyle.isEnabled(labelStyle);
	}

	public boolean isOffset() {
		return isOffsetLine;
	}

	public boolean isOrientations() {
		return decoratorStyle.isEnabled(orientStyle);
	}

	public boolean isSegIndex() {
		return decoratorStyle.isEnabled(segIndexStyle);
	}

	public boolean isShifted() {
		return isShift;
	}

	public boolean isStructure() {
		return decoratorStyle.isEnabled(structureStyle);
	}

	public boolean isVertexLabels() {
		return decoratorStyle.isEnabled(vertexLabelStyle);
	}

	public boolean isVertices() {
		return decoratorStyle.isEnabled(vertexStyle);
	}

	public void paint(Geometry geom, Viewport viewport, Graphics2D g) throws Exception {
		Geometry transformGeom = transform(geom, viewport);
		geomStyle.paint(transformGeom, viewport, g);
		decoratorStyle.paint(transformGeom, viewport, g);
	}

	public void setColor(Color color) {
		segArrowStyle.setColor(ColorUtil.lighter(color, 0.8));
		lineArrowStyle.setColor(ColorUtil.lighter(color, 0.5));
		lineCircleStyle.setColor(ColorUtil.lighter(color, 0.5));
	}

	public void setEndpoints(boolean show) {
		decoratorStyle.setEnabled(endPointsStyle, show);
	}

	public void setFillType(int fillType) {
		this.fillType = fillType;
	}

	public void setLabel(boolean show) {
		decoratorStyle.setEnabled(labelStyle, show);
	}

	public void setLabelColor(Color color) {
		labelStyle.setColor(color);
	}

	public void setLabelSize(int size) {
		labelStyle.setSize(size);
	}

	public void setOffset(boolean show) {
		isOffsetLine = show;
	}

	public void setOffsetSize(int offsetSize) {
		this.offsetSize = offsetSize;
	}

	public void setOrientations(boolean show) {
		decoratorStyle.setEnabled(orientStyle, show);
	}

	public void setSegIndex(boolean show) {
		decoratorStyle.setEnabled(segIndexStyle, show);
	}

	public void setShift(boolean isShift) {
		this.isShift = isShift;
	}

	public void setStructure(boolean show) {
		decoratorStyle.setEnabled(structureStyle, show);
	}

	public void setVertexColor(Color color) {
		vertexStyle.setColor(color);
		vertexLabelStyle.setColor(color);
		startPointStyle.setColor(color);
		endPointStyle.setColor(color);
	}

	public void setVertexLabels(boolean show) {
		decoratorStyle.setEnabled(vertexLabelStyle, show);
	}

	public void setVertexSize(int size) {
		vertexStyle.setSize(size);
		startPointStyle.setSize(2 * size);
		endPointStyle.setSize(2 * size);
	}

	public void setVertexSymbol(int sym) {
		vertexStyle.setSymbol(sym);
	}

	public void setVertices(boolean show) {
		decoratorStyle.setEnabled(vertexStyle, show);
	}

	private Geometry transform(Geometry geom, Viewport viewport) {
		Geometry transformGeom = geom;
		if (isOffsetLine && geom instanceof LineString) {
			double offsetDistance = viewport.toModel(offsetSize);
			transformGeom = offsetLine(geom, offsetDistance);
			if (transformGeom != null) {
				transformGeom.setUserData(geom.getUserData());
			}
		}
		return transformGeom;
	}

	private void update(LayerStyle layerStyle) {
		setStructure(layerStyle.isStructure());
		setSegIndex(layerStyle.isSegIndex());
		setOrientations(layerStyle.isOrientations());
		setLabel(layerStyle.isLabel());
		setLabelSize(layerStyle.getLabelSize());
		setVertices(layerStyle.isVertices());
		setVertexSize(layerStyle.getVertexSize());
		setVertexColor(layerStyle.getVertexColor());
		setVertexLabels(layerStyle.isVertexLabels());
		setVertexSymbol(layerStyle.getVertexSymbol());
		setEndpoints(layerStyle.isEndpoints());
	}
}
