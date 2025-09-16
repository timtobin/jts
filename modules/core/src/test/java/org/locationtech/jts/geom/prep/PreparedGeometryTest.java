package org.locationtech.jts.geom.prep;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;

import test.jts.GeometryTestCase;

public class PreparedGeometryTest extends GeometryTestCase {
	@Test
	public void testEmptyElement() {
		Geometry geomA = read("MULTIPOLYGON (((9 9, 9 1, 1 1, 2 4, 7 7, 9 9)), EMPTY)");
		Geometry geomB = read("MULTIPOLYGON (((7 6, 7 3, 4 3, 7 6)), EMPTY)");
		PreparedGeometry prepA = PreparedGeometryFactory.prepare(geomA);
		assertTrue(prepA.covers(geomB));
		assertTrue(prepA.contains(geomB));
		assertTrue(prepA.intersects(geomB));
	}
}
