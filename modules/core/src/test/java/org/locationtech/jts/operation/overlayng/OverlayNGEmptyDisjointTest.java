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
package org.locationtech.jts.operation.overlayng;

import static org.locationtech.jts.operation.overlayng.OverlayNG.INTERSECTION;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.PrecisionModel;

public class OverlayNGEmptyDisjointTest extends OverlayNGTestCase {
	public static Geometry intersectionNoOpt(Geometry a, Geometry b, double scaleFactor) {
		PrecisionModel pm = new PrecisionModel(scaleFactor);
		OverlayNG ov = new OverlayNG(a, b, pm, INTERSECTION);
		ov.setOptimized(false);
		return ov.getResult();
	}

	@Test
	public void testDisjointIntersection() {
		Geometry a = read("POLYGON ((60 90, 90 90, 90 60, 60 60, 60 90))");
		Geometry b = read("POLYGON ((200 300, 300 300, 300 200, 200 200, 200 300))");
		Geometry expected = read("POLYGON EMPTY");
		Geometry actual = intersection(a, b, 1);
		checkEqual(expected, actual);
	}

	@Test
	public void testDisjointIntersectionNoOpt() {
		Geometry a = read("POLYGON ((60 90, 90 90, 90 60, 60 60, 60 90))");
		Geometry b = read("POLYGON ((200 300, 300 300, 300 200, 200 200, 200 300))");
		Geometry expected = read("POLYGON EMPTY");
		Geometry actual = intersectionNoOpt(a, b, 1);
		checkEqual(expected, actual);
	}

	@Test
	public void testEmptyABIntersection() {
		Geometry a = read("POLYGON EMPTY");
		Geometry b = read("POLYGON EMPTY");
		Geometry expected = read("POLYGON EMPTY");
		Geometry actual = intersection(a, b, 1);
		checkEqual(expected, actual);
	}

	@Test
	public void testEmptyADifference() {
		Geometry a = read("POLYGON EMPTY");
		Geometry b = read("POLYGON ((1 0, 2 5, 3 0, 1 0))");
		Geometry expected = read("POLYGON EMPTY");
		Geometry actual = difference(a, b, 1);
		checkEqual(expected, actual);
	}

	@Test
	public void testEmptyAPolygonIntersection() {
		Geometry a = read("POLYGON EMPTY");
		Geometry b = read("POLYGON ((1 0, 2 5, 3 0, 1 0))");
		Geometry expected = read("POLYGON EMPTY");
		Geometry actual = intersection(a, b, 1);
		checkEqual(expected, actual);
	}

	@Test
	public void testEmptyASymDifference() {
		Geometry a = read("POLYGON EMPTY");
		Geometry b = read("POLYGON ((1 0, 2 5, 3 0, 1 0))");
		Geometry expected = read("POLYGON ((1 0, 2 5, 3 0, 1 0))");
		Geometry actual = symDifference(a, b, 1);
		checkEqual(expected, actual);
	}

	@Test
	public void testEmptyAUnion() {
		Geometry a = read("POLYGON EMPTY");
		Geometry b = read("POLYGON ((1 0, 2 5, 3 0, 1 0))");
		Geometry expected = read("POLYGON ((1 0, 2 5, 3 0, 1 0))");
		Geometry actual = union(a, b, 1);
		checkEqual(expected, actual);
	}

	@Test
	public void testEmptyBIntersection() {
		Geometry a = read("POLYGON ((1 0, 2 5, 3 0, 1 0))");
		Geometry b = read("POLYGON EMPTY");
		Geometry expected = read("POLYGON EMPTY");
		Geometry actual = intersection(a, b, 1);
		checkEqual(expected, actual);
	}

	@Test
	public void testEmptyGCBothIntersection() {
		Geometry a = read("GEOMETRYCOLLECTION EMPTY");
		Geometry b = read("GEOMETRYCOLLECTION EMPTY");
		Geometry expected = read("GEOMETRYCOLLECTION EMPTY");
		Geometry actual = intersection(a, b, 1);
		checkEqual(expected, actual);
	}

	@Test
	public void testEmptyLinePolygonDifference() {
		Geometry a = read("LINESTRING EMPTY");
		Geometry b = read("POLYGON ((1 0, 2 5, 3 0, 1 0))");
		Geometry expected = read("LINESTRING EMPTY");
		Geometry actual = difference(a, b, 1);
		checkEqual(expected, actual);
	}

	@Test
	public void testEmptyLinePolygonIntersection() {
		Geometry a = read("LINESTRING EMPTY");
		Geometry b = read("POLYGON ((1 0, 2 5, 3 0, 1 0))");
		Geometry expected = read("LINESTRING EMPTY");
		Geometry actual = intersection(a, b, 1);
		checkEqual(expected, actual);
	}

	@Test
	public void testEmptyPointPolygonIntersection() {
		Geometry a = read("POINT EMPTY");
		Geometry b = read("POLYGON ((1 0, 2 5, 3 0, 1 0))");
		Geometry expected = read("POINT EMPTY");
		Geometry actual = intersection(a, b, 1);
		checkEqual(expected, actual);
	}
}
