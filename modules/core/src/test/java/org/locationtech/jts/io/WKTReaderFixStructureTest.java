package org.locationtech.jts.io;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import test.jts.GeometryTestCase;

public class WKTReaderFixStructureTest extends GeometryTestCase {
  private final WKTReader readerFix;
  private final WKTReader reader;

  public WKTReaderFixStructureTest() {
    reader = new WKTReader();
    readerFix = new WKTReader();
    readerFix.setFixStructure(true);
  }

  @Test
  public void testLineaStringShort() throws ParseException {
    checkFixStructure("LINESTRING (0 0)");
  }

  @Test
  public void testLinearRingUnclosed() throws ParseException {
    checkFixStructure("LINEARRING (0 0, 0 1, 1 0)");
  }

  @Test
  public void testLinearRingShort() throws ParseException {
    checkFixStructure("LINEARRING (0 0, 0 1)");
  }

  @Test
  public void testPolygonShort() throws ParseException {
    checkFixStructure("POLYGON ((0 0))");
  }

  @Test
  public void testPolygonUnclosed() throws ParseException {
    checkFixStructure("POLYGON ((0 0, 0 1, 1 0))");
  }

  @Test
  public void testPolygonUnclosedHole() throws ParseException {
    checkFixStructure("POLYGON ((0 0, 0 10, 10 0, 0 0), (0 0, 1 0, 0 1))");
  }

  @Test
  public void testCollection() throws ParseException {
    checkFixStructure(
        "GEOMETRYCOLLECTION (LINESTRING (0 0), LINEARRING (0 0, 0 1), POLYGON ((0 0, 0 10, 10 0, 0 0), (0 0, 1 0, 0 1)) )");
  }

  private void checkFixStructure(String wkt) throws ParseException {
    checkHasBadStructure(wkt);
    checkFixed(wkt);
  }

  private void checkFixed(String wkt) throws ParseException {
    // if not fixed will fail with IllegalArgumentException
    readerFix.read(wkt);
  }

  private void checkHasBadStructure(String wkt) throws ParseException {
    try {
      reader.read(wkt);
      fail("Input does not have non-closed rings");
    } catch (IllegalArgumentException e) {
      // ok, do nothing
    }
  }
}
