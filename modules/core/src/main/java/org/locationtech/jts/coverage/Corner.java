/*
 * Copyright (c) 2022 Martin Davis.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v20.html
 * and the Eclipse Distribution License is available at
 *
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.locationtech.jts.coverage;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Triangle;
import org.locationtech.jts.simplify.LinkedLine;

class Corner implements Comparable<Corner> {
	private static Coordinate safeCoord(Coordinate p) {
		if (p == null)
			return new Coordinate(Double.NaN, Double.NaN);
		return p;
	}

	private final double area;
	private final LinkedLine edge;
	private final int index;
	private final int next;

	private final int prev;

	public Corner(LinkedLine edge, int i, double area) {
		this.edge = edge;
		this.index = i;
		this.prev = edge.prev(i);
		this.next = edge.next(i);
		this.area = area;
	}

	/**
	 * Orders corners by increasing area. To ensure equal-area corners have a
	 * deterministic ordering, if area is equal then compares corner index.
	 */
	@Override
	public int compareTo(Corner o) {
		int comp = Double.compare(area, o.area);
		if (comp != 0)
			return comp;
		// -- ensure equal-area corners have a deterministic ordering
		return Integer.compare(index, o.index);
	}

	public Envelope envelope() {
		Coordinate pp = edge.getCoordinate(prev);
		Coordinate p = edge.getCoordinate(index);
		Coordinate pn = edge.getCoordinate(next);
		Envelope env = new Envelope(pp, pn);
		env.expandToInclude(p);
		return env;
	}

	public double getArea() {
		return area;
	}

	public Coordinate getCoordinate() {
		return edge.getCoordinate(index);
	}

	public int getIndex() {
		return index;
	}

	public boolean intersects(Coordinate v) {
		Coordinate pp = edge.getCoordinate(prev);
		Coordinate p = edge.getCoordinate(index);
		Coordinate pn = edge.getCoordinate(next);
		return Triangle.intersects(pp, p, pn, v);
	}

	public boolean isBaseline(Coordinate p0, Coordinate p1) {
		Coordinate prev = prev();
		Coordinate next = next();
		if (prev.equals2D(p0) && next.equals2D(p1))
			return true;
		if (prev.equals2D(p1) && next.equals2D(p0))
			return true;
		return false;
	}

	public boolean isRemoved() {
		return edge.prev(index) != prev || edge.next(index) != next;
	}

	public boolean isVertex(Coordinate v) {
		if (v.equals2D(edge.getCoordinate(prev)))
			return true;
		if (v.equals2D(edge.getCoordinate(index)))
			return true;
		if (v.equals2D(edge.getCoordinate(next)))
			return true;
		return false;
	}

	public boolean isVertex(int index) {
		return index == this.index || index == prev || index == next;
	}

	public Coordinate next() {
		return edge.getCoordinate(next);
	}

	public Coordinate prev() {
		return edge.getCoordinate(prev);
	}

	public LineString toLineString() {
		Coordinate pp = edge.getCoordinate(prev);
		Coordinate p = edge.getCoordinate(index);
		Coordinate pn = edge.getCoordinate(next);
		return (new GeometryFactory()).createLineString(new Coordinate[]{safeCoord(pp), safeCoord(p), safeCoord(pn)});
	}

	public String toString() {
		return toLineString().toString();
	}
}
