package org.locationtech.jts.linearref;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Lineal;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.Point;
import test.jts.GeometryTestCase;

public class LengthLocationMapTest extends GeometryTestCase {

  @Test
  public void testLengthAtPosition30()
  {
    checkLlm("LINESTRING (0 0, 0 100)", "POINT (0 30)", 30);
  }

  @Test
  public void testLengthAtPosition50()
  {
    checkLlm("LINESTRING (0 0, 0 100)", "POINT (0 50)", 50);
  }

  @Test
  public void testLengthAtPosition60()
  {
    checkLlm("LINESTRING (0 0, 0 100)", "POINT (0 60)", 60);
  }

  @Test
  public void testLengthAtPosition100()
  {
    checkLlm("LINESTRING (0 0, 0 100)", "POINT (0 100)", 100);
  }

  @Test
  public void testLengthAtPosition101()
  {
    checkLlm("LINESTRING (0 0, 0 100)", "POINT (0 101)", 100);
  }

  @Test
  public void testLengthAtPosition0()
  {
    checkLlm("LINESTRING (0 0, 0 100)", "POINT (0 0)", 0);
  }

  @Test
  public void testLengthAtPositionMinus1()
  {
    checkLlm("LINESTRING (0 0, 0 100)", "POINT (0 -1)", 0);
  }

  @Test
  public void testMultiLineLengthPosition30()
  {
    checkLlm("MULTILINESTRING((0 0, 0 50), (0 50, 0 100))", "POINT (0 30)", 30);
  }

  @Test
  public void testMultiLineLengthPosition50()
  {
    checkLlm("MULTILINESTRING((0 0, 0 50), (0 50, 0 100))", "POINT (0 50)", 50);
  }

  @Test
  public void testMultiLineLengthPosition60()
  {
    checkLlm("MULTILINESTRING((0 0, 0 50), (0 50, 0 100))", "POINT (0 60)", 60);
  }

  @Test
  public void testMultiLineLengthAtPosition100()
  {
    checkLlm("MULTILINESTRING((0 0, 0 50), (0 50, 0 100))", "POINT (0 100)", 100);
  }

  @Test
  public void testMultiLineLengthAtPosition101()
  {
    checkLlm("MULTILINESTRING((0 0, 0 50), (0 50, 0 100))", "POINT (0 101)", 100);
  }

  @Test
  public void testMultiLineLengthAtPosition0()
  {
    checkLlm("MULTILINESTRING((0 0, 0 50), (0 50, 0 100))", "POINT (0 0)", 0);
  }

  @Test
  public void testMultiLineLengthAtPositionMinus1()
  {
    checkLlm("MULTILINESTRING((0 0, 0 50), (0 50, 0 100))", "POINT (0 -1)", 0);
  }

  @Test
  public void testMultiLineHoleLengthPosition30()
  {
    checkLlm("MULTILINESTRING((0 0, 0 50), (0 51, 0 100))", "POINT (0 30)", 30);
  }

  @Test
  public void testMultiLineHoleLengthPosition50()
  {
    checkLlm("MULTILINESTRING((0 0, 0 50), (0 51, 0 100))", "POINT (0 50)", 50);
  }

  @Test
  public void testMultiLineHoleLengthPosition60()
  {
    checkLlm("MULTILINESTRING((0 0, 0 50), (0 51, 0 100))", "POINT (0 60)", 59);
  }

  @Test
  public void testMultiLineHoleLengthAtPosition100()
  {
    checkLlm("MULTILINESTRING((0 0, 0 50), (0 51, 0 100))", "POINT (0 100)", 99);
  }

  @Test
  public void testMultiLineHoleLengthAtPosition101()
  {
    checkLlm("MULTILINESTRING((0 0, 0 50), (0 51, 0 100))", "POINT (0 101)", 99);
  }

  @Test
  public void testMultiLineHoleLengthAtPosition0()
  {
    checkLlm("MULTILINESTRING((0 0, 0 50), (0 51, 0 100))", "POINT (0 0)", 0);
  }

  @Test
  public void testMultiLineHoleLengthAtPosition60()
  {
    checkLlm("MULTILINESTRING((0 0, 0 30), (0 31, 0 60), (0 61, 0 100))", "POINT (0 60)", 59);
  }

  @Test
  public void testMultiLineHoleLengthAtPositionMinus1()
  {
    checkLlm("MULTILINESTRING((0 0, 0 50), (0 51, 0 100))", "POINT (0 -1)", 0);
  }

  private void checkLlm(String wkt0, String wkt1, double expectedDistance) {
    Lineal line = (Lineal) read(wkt0);
    Point point = (Point) read(wkt1);
    if (line instanceof LineString string)
      checkLlm(string, point, expectedDistance);
    else
      checkLlm((MultiLineString) line, point, expectedDistance);
  }

  private void checkLlm(LineString geom0, Point geom1, double expectedDistance) {
    LinearLocation loc = LocationIndexOfPoint.indexOf(geom0, geom1.getCoordinate());
    assertEquals(expectedDistance, LengthLocationMap.getLength(geom0, loc));
  }

  private void checkLlm(MultiLineString geom0, Point geom1, double expectedDistance) {
    LinearLocation loc = LocationIndexOfPoint.indexOf(geom0, geom1.getCoordinate());
    assertEquals(expectedDistance, LengthLocationMap.getLength(geom0, loc));
  }
}
