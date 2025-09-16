package org.locationtech.jts.operation.distance;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;

public class IndexedFacetDistanceTest extends BaseDistanceTest {
	@Override
	protected double distance(Geometry g1, Geometry g2) {
		return IndexedFacetDistance.distance(g1, g2);
	}

	@Override
	protected boolean isWithinDistance(Geometry g1, Geometry g2, double distance) {
		return IndexedFacetDistance.isWithinDistance(g1, g2, distance);
	}

	protected Coordinate[] nearestPoints(Geometry g1, Geometry g2) {
		return IndexedFacetDistance.nearestPoints(g1, g2);
	}

	@Test
	public void testClosestPoints7() {
		// skip this test for now, since it relies on checking point-in-polygon
	}
}
