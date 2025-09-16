package org.locationtech.jts.geom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import test.jts.GeometryTestCase;
import test.jts.GeometryTestData;

public class GeometryReverseTest extends GeometryTestCase {
	private void checkReverse(final Geometry g) {
		int SRID = 123;
		g.setSRID(SRID);

		// User data left out for now
		// Object DATA = new Integer(999);
		// g.setUserData(DATA);

		Geometry reverse = g.reverse();

		assertEquals(g.getGeometryType(), reverse.getGeometryType(),
				g.getGeometryType() + ": Geometry types are not the same");
		assertEquals(g.getSRID(), reverse.getSRID(),
				g.getGeometryType() + ": Geometry.getSRID() values are not the same");

		assertTrue(checkSequences(g, reverse), g.getGeometryType() + ": Sequences are not opposite");
	}

	private boolean checkSequences(CoordinateSequence c1, CoordinateSequence c2) {

		if (c1.size() != c2.size())
			return false;
		if (c1.getDimension() != c2.getDimension())
			return false;
		if (c1.getMeasures() != c2.getMeasures())
			return false;

		for (int i = 0; i < c1.size(); i++) {
			int j = c1.size() - i - 1;
			for (int k = 0; k < c1.getDimension(); k++)
				if (c1.getOrdinate(i, k) != c2.getOrdinate(j, k))
					if (!(Double.isNaN(c1.getOrdinate(i, k)) && Double.isNaN(c2.getOrdinate(j, k))))
						return false;
		}
		return true;
	}

	private boolean checkSequences(Geometry g1, Geometry g2) {
		int numGeometries = g1.getNumGeometries();
		if (numGeometries != g2.getNumGeometries())
			return false;
		for (int i = 0; i < numGeometries; i++) {
			Geometry gt1 = g1.getGeometryN(i);
			// g1 instanceof MultiLineString ? numGeometries - i - 1 : i;
			Geometry gt2 = g2.getGeometryN(i);

			if (!gt1.getGeometryType().equals(gt2.getGeometryType()))
				return false;

			switch (gt1) {
				case Point point -> {
					if (!checkSequences(point.getCoordinateSequence(), ((Point) gt2).getCoordinateSequence()))
						return false;
				}
				case LineString string -> {
					if (!checkSequences(string.getCoordinateSequence(), ((LineString) gt2).getCoordinateSequence()))
						return false;
				}
				case Polygon pt1 -> {
					Polygon pt2 = (Polygon) gt2;
					if (!checkSequences(pt1.getExteriorRing().getCoordinateSequence(),
							pt2.getExteriorRing().getCoordinateSequence()))
						return false;
					for (int k = 0; k < pt1.getNumInteriorRing(); k++) {
						if (!checkSequences(pt1.getInteriorRingN(k).getCoordinateSequence(),
								pt2.getInteriorRingN(k).getCoordinateSequence()))
							return false;
					}
				}
				case GeometryCollection geometryCollection -> checkSequences(gt1, gt2);
				default -> {
					return false;
				}
			}
		}
		return true;
	}

	@Test
	public void testReverse() {
		for (String wkt : GeometryTestData.WKT_ALL) {
			checkReverse(read(wkt));
		}
	}
}
