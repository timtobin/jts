package org.locationtech.jts.geom.util;

import static org.junit.jupiter.api.Assertions.assertEquals;


import java.util.List;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

public class GeometryExtracterTest {

	static WKTReader reader = new WKTReader();

  @Test
  public void testExtract() throws ParseException {
		Geometry gc = reader.read("GEOMETRYCOLLECTION ( POINT (1 1), LINESTRING (0 0, 10 10), LINESTRING (10 10, 20 20), LINEARRING (10 10, 20 20, 15 15, 10 10), POLYGON ((0 0, 100 0, 100 100, 0 100, 0 0)), GEOMETRYCOLLECTION ( POINT (1 1) ) )");
		
		// verify that LinearRings are included when extracting LineStrings
		List lineStringsAndLinearRings = GeometryExtracter.extract(gc, Geometry.TYPENAME_LINESTRING);
		assertEquals(3, lineStringsAndLinearRings.size());
		
		// verify that only LinearRings are extracted
		List linearRings = GeometryExtracter.extract(gc, Geometry.TYPENAME_LINEARRING);
		assertEquals(1, linearRings.size());
		
		// verify that nested geometries are extracted
		List points = GeometryExtracter.extract(gc, Geometry.TYPENAME_POINT);
		assertEquals(2, points.size());
	}

}
