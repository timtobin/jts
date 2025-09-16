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
package org.locationtech.jtstest.clean;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

/**
 * @version 1.7
 */
public class CleanDuplicatePoints {

	public static Coordinate[] removeDuplicatePoints(Coordinate[] coord) {
		List uniqueCoords = new ArrayList();
		Coordinate lastPt = null;
		for (Coordinate coordinate : coord) {
			if (lastPt == null || !lastPt.equals(coordinate)) {
				lastPt = coordinate;
				uniqueCoords.add(new Coordinate(lastPt));
			}
		}
		return (Coordinate[]) uniqueCoords.toArray(new Coordinate[0]);
	}

	private GeometryFactory fact;

	public CleanDuplicatePoints() {
	}

	public Geometry clean(Geometry g) {
		fact = g.getFactory();
		if (g.isEmpty())
			return g;
		return switch (g) {
			case Point point -> g;
			case MultiPoint multiPoint -> g;
			// LineString also handles LinearRings
			case LinearRing ring -> clean(ring);
			case LineString string1 -> clean(string1);
			case Polygon polygon1 -> clean(polygon1);
			case MultiLineString string -> clean(string);
			case MultiPolygon polygon -> clean(polygon);
			case GeometryCollection collection -> clean(collection);
			default -> throw new UnsupportedOperationException(g.getClass().getName());
		};
	}

	private GeometryCollection clean(GeometryCollection g) {
		List geoms = new ArrayList();
		for (int i = 0; i < g.getNumGeometries(); i++) {
			Geometry geom = g.getGeometryN(i);
			geoms.add(clean(geom));
		}
		return fact.createGeometryCollection(GeometryFactory.toGeometryArray(geoms));
	}

	private LineString clean(LineString g) {
		Coordinate[] coords = removeDuplicatePoints(g.getCoordinates());
		return fact.createLineString(coords);
	}

	private LinearRing clean(LinearRing g) {
		Coordinate[] coords = removeDuplicatePoints(g.getCoordinates());
		return fact.createLinearRing(coords);
	}

	private MultiLineString clean(MultiLineString g) {
		List lines = new ArrayList();
		for (int i = 0; i < g.getNumGeometries(); i++) {
			LineString line = (LineString) g.getGeometryN(i);
			lines.add(clean(line));
		}
		return fact.createMultiLineString(GeometryFactory.toLineStringArray(lines));
	}

	private MultiPolygon clean(MultiPolygon g) {
		List polys = new ArrayList();
		for (int i = 0; i < g.getNumGeometries(); i++) {
			Polygon poly = (Polygon) g.getGeometryN(i);
			polys.add(clean(poly));
		}
		return fact.createMultiPolygon(GeometryFactory.toPolygonArray(polys));
	}

	private Polygon clean(Polygon poly) {
		Coordinate[] shellCoords = removeDuplicatePoints(poly.getExteriorRing().getCoordinates());
		LinearRing shell = fact.createLinearRing(shellCoords);
		List holes = new ArrayList();
		for (int i = 0; i < poly.getNumInteriorRing(); i++) {
			Coordinate[] holeCoords = removeDuplicatePoints(poly.getInteriorRingN(i).getCoordinates());
			holes.add(fact.createLinearRing(holeCoords));
		}
		return fact.createPolygon(shell, GeometryFactory.toLinearRingArray(holes));
	}
}
