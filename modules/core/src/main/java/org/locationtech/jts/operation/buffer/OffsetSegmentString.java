/*
 * Copyright (c) 2016 Martin Davis.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v20.html
 * and the Eclipse Distribution License is available at
 *
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.locationtech.jts.operation.buffer;

import java.util.ArrayList;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.PrecisionModel;

/**
 * A dynamic list of the vertices in a constructed offset curve. Automatically
 * removes adjacent vertices which are closer than a given tolerance.
 *
 * @author Martin Davis
 */
class OffsetSegmentString {
	private static final Coordinate[] COORDINATE_ARRAY_TYPE = new Coordinate[0];

	/**
	 * The distance below which two adjacent points on the curve are considered to
	 * be coincident. This is chosen to be a small fraction of the offset distance.
	 */
	private double minimimVertexDistance = 0.0;

	private PrecisionModel precisionModel = null;

	private final ArrayList ptList;

	public OffsetSegmentString() {
		ptList = new ArrayList();
	}

	public void addPt(Coordinate pt) {
		Coordinate bufPt = new Coordinate(pt);
		precisionModel.makePrecise(bufPt);
		// don't add duplicate (or near-duplicate) points
		if (isRedundant(bufPt))
			return;
		ptList.add(bufPt);
		// System.out.println(bufPt);
	}

	public void addPts(Coordinate[] pt, boolean isForward) {
		if (isForward) {
			for (Coordinate coordinate : pt) {
				addPt(coordinate);
			}
		} else {
			for (int i = pt.length - 1; i >= 0; i--) {
				addPt(pt[i]);
			}
		}
	}

	public void closeRing() {
		if (ptList.isEmpty())
			return;
		Coordinate startPt = new Coordinate((Coordinate) ptList.getFirst());
		Coordinate lastPt = (Coordinate) ptList.getLast();
		if (startPt.equals(lastPt))
			return;
		ptList.add(startPt);
	}

	public Coordinate[] getCoordinates() {
		/*
		 * // check that points are a ring - add the startpoint again if they are not if
		 * (ptList.size() > 1) { Coordinate start = (Coordinate) ptList.get(0);
		 * Coordinate end = (Coordinate) ptList.get(ptList.size() - 1); if (!
		 * start.equals(end) ) addPt(start); }
		 */
		return (Coordinate[]) ptList.toArray(COORDINATE_ARRAY_TYPE);
	}

	/**
	 * Tests whether the given point is redundant relative to the previous point in
	 * the list (up to tolerance).
	 *
	 * @param pt
	 * @return true if the point is redundant
	 */
	private boolean isRedundant(Coordinate pt) {
		if (ptList.isEmpty())
			return false;
		Coordinate lastPt = (Coordinate) ptList.getLast();
		double ptDist = pt.distance(lastPt);
		return ptDist < minimimVertexDistance;
	}

	public void reverse() {
	}

	public void setMinimumVertexDistance(double minimimVertexDistance) {
		this.minimimVertexDistance = minimimVertexDistance;
	}

	public void setPrecisionModel(PrecisionModel precisionModel) {
		this.precisionModel = precisionModel;
	}

	public String toString() {
		GeometryFactory fact = new GeometryFactory();
		LineString line = fact.createLineString(getCoordinates());
		return line.toString();
	}
}
