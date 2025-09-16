package org.locationtech.jts.triangulate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;

import test.jts.GeometryTestCase;

public class VoronoiDiagramBuilderTest extends GeometryTestCase {
	private static final double TRIANGULATION_TOLERANCE = 0.0;

	public static Geometry voronoiDiagram(Geometry sitesGeom, Geometry clipGeom) {
		VoronoiDiagramBuilder builder = new VoronoiDiagramBuilder();
		builder.setSites(sitesGeom);
		if (clipGeom != null)
			builder.setClipEnvelope(clipGeom.getEnvelopeInternal());
		builder.setTolerance(TRIANGULATION_TOLERANCE);
		return builder.getDiagram(sitesGeom.getFactory());
	}

	@Test
	public void testClipEnvelope() {
		Geometry sites = read("MULTIPOINT ((50 100), (50 50), (100 50), (100 100))");
		Geometry clip = read("POLYGON ((0 0, 0 200, 200 200, 200 0, 0 0))");
		Geometry voronoi = voronoiDiagram(sites, clip);
		assertEquals(voronoi.getEnvelopeInternal(), clip.getEnvelopeInternal());
	}

	@Test
	public void testClipEnvelopeBig() {
		Geometry sites = read("MULTIPOINT ((50 100), (50 50), (100 50), (100 100))");
		Geometry clip = read("POLYGON ((-1000 1000, 1000 1000, 1000 -1000, -1000 -1000, -1000 1000))");
		Geometry voronoi = voronoiDiagram(sites, clip);
		assertEquals(voronoi.getEnvelopeInternal(), clip.getEnvelopeInternal());
	}
}
