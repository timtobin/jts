package org.locationtech.jts.awt;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Shape;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;

import test.jts.GeometryTestCase;

public class PolygonShapeTest extends GeometryTestCase {
	@Test
	public void testEmptyHole() {
		Geometry geom = read("POLYGON ((100 200, 200 200, 200 100, 100 100, 100 200), EMPTY)");
		ShapeWriter sw = new ShapeWriter();
		Shape shp = sw.toShape(geom);

		Geometry geom2 = ShapeReader.read(shp, 0.5, geom.getFactory());
		Geometry geomExpected = read("POLYGON ((100 -200, 200 -200, 200 -100, 100 -100, 100 -200))");
		assertTrue(geomExpected.equalsExact(geom2));
	}

	@Test
	public void testFlatness() {
		Geometry geom = read("POLYGON ((100 200, 200 200, 200 100, 100 100, 100 200))");
		ShapeWriter sw = new ShapeWriter();
		Shape shp = sw.toShape(geom);

		Geometry geom2 = ShapeReader.read(shp, 0.5, geom.getFactory());
		Geometry geomExpected = read("POLYGON ((100 -200, 200 -200, 200 -100, 100 -100, 100 -200))");
		assertTrue(geomExpected.equalsExact(geom2));
	}
}
