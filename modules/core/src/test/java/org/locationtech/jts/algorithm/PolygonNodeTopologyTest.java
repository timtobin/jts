package org.locationtech.jts.algorithm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;

import test.jts.GeometryTestCase;

public class PolygonNodeTopologyTest extends GeometryTestCase {
	private void checkCrossing(String wktA, String wktB) {
		checkCrossing(wktA, wktB, true);
	}

	private void checkCrossing(String wktA, String wktB, boolean isExpected) {
		Coordinate[] a = readPts(wktA);
		Coordinate[] b = readPts(wktB);
		// assert: a[1] = b[1]
		boolean isCrossing = PolygonNodeTopology.isCrossing(a[1], a[0], a[2], b[0], b[2]);
		assertEquals(isCrossing, isExpected);
	}

	private void checkExterior(String wktA, String wktB) {
		checkInteriorSegment(wktA, wktB, false);
	}

	private void checkInterior(String wktA, String wktB) {
		checkInteriorSegment(wktA, wktB, true);
	}

	private void checkInteriorSegment(String wktA, String wktB, boolean isExpected) {
		Coordinate[] a = readPts(wktA);
		Coordinate[] b = readPts(wktB);
		// assert: a[1] = b[1]
		boolean isInterior = PolygonNodeTopology.isInteriorSegment(a[1], a[0], a[2], b[1]);
		assertEquals(isInterior, isExpected);
	}

	private void checkNonCrossing(String wktA, String wktB) {
		checkCrossing(wktA, wktB, false);
	}

	private Coordinate[] readPts(String wkt) {
		LineString line = (LineString) read(wkt);
		return line.getCoordinates();
	}

	// -----------------------------------------------

	@Test
	public void testExteriorSegment() {
		checkExterior("LINESTRING (5 9, 5 5, 9 5)", "LINESTRING (5 5, 9 9)");
	}

	@Test
	public void testInteriorSegment() {
		checkInterior("LINESTRING (5 9, 5 5, 9 5)", "LINESTRING (5 5, 0 0)");
	}

	@Test
	public void testNonCrossing() {
		checkCrossing("LINESTRING (500 1000, 1000 1000, 1000 1500)", "LINESTRING (1000 500, 1000 1000, 500 1500)");
	}

	@Test
	public void testNonCrossingBothCollinear() {
		checkNonCrossing("LINESTRING (3 1, 5 5, 9 9)", "LINESTRING (3 1, 5 5, 9 9)");
	}

	@Test
	public void testNonCrossingCollinear() {
		checkNonCrossing("LINESTRING (3 1, 5 5, 9 9)", "LINESTRING (2 1, 5 5, 9 9)");
	}

	@Test
	public void testNonCrossingQuadrant2() {
		checkNonCrossing("LINESTRING (500 1000, 1000 1000, 1000 1500)", "LINESTRING (300 1200, 1000 1000, 500 1500)");
	}

	@Test
	public void testNonCrossingQuadrant4() {
		checkNonCrossing("LINESTRING (500 1000, 1000 1000, 1000 1500)", "LINESTRING (1000 500, 1000 1000, 1500 1000)");
	}
}
