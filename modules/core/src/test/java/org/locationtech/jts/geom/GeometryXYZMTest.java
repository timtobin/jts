package org.locationtech.jts.geom;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.impl.PackedCoordinateSequenceFactory;
import org.locationtech.jts.io.ParseException;


import test.jts.GeometryTestCase;

/**
 * Tests to confirm that operations call {@link CoordinateSequence#createCoordinate()}
 * to ensure they work correctly with coordinates of any dimension
 * (in particular XYZM coordinates, which do not fit in the default {@link Coordinate}).
 * 
 * @author Martin Davis
 *
 */
public class GeometryXYZMTest extends GeometryTestCase {
  static GeometryFactory geomFact = new GeometryFactory(PackedCoordinateSequenceFactory.DOUBLE_FACTORY);

  @Test
  public void testArea() {
    Polygon geom = (Polygon) read(geomFact, "POLYGON ZM ((1 9 2 3, 9 9 2 3, 9 1 2 3, 1 1 2 3, 1 9 2 3))");
    double area = geom.getArea();
    assertEquals(64.0, area);
  }

  @Test
  public void testLength() {
    Polygon geom = (Polygon) read(geomFact, "POLYGON ZM ((1 9 2 3, 9 9 2 3, 9 1 2 3, 1 1 2 3, 1 9 2 3))");
    double len = geom.getLength();
    assertEquals(32.0, len);
  }
}
