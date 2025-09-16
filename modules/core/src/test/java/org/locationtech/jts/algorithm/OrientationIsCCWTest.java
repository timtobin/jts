/*
 * Copyright (c) 2016 Vivid Solutions.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v20.html
 * and the Eclipse Distribution License is available at
 *
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.locationtech.jts.algorithm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;

import test.jts.GeometryTestCase;

/**
 * Tests Orientation isCCW
 *
 * @version 1.7
 */
public class OrientationIsCCWTest extends GeometryTestCase {
  @Test
  public void testTooFewPoints() {
    Coordinate[] pts =
        new Coordinate[] {new Coordinate(0, 0), new Coordinate(1, 1), new Coordinate(2, 2)};
    boolean isCCW = Orientation.isCCW(pts);
    // actual value is undefined.  This just confirms no exception
    assertTrue(true);
  }

  @Test
  public void testCCW() {
    checkCCW(true, "POLYGON ((60 180, 140 120, 100 180, 140 240, 60 180))");
  }

  @Test
  public void testRingCW() {
    checkCCW(false, "POLYGON ((60 180, 140 240, 100 180, 140 120, 60 180))");
  }

  @Test
  public void testCCWSmall() {
    checkCCW(true, "POLYGON ((1 1, 9 1, 5 9, 1 1))");
  }

  @Test
  public void testDuplicateTopPoint() {
    checkCCW(true, "POLYGON ((60 180, 140 120, 100 180, 140 240, 140 240, 60 180))");
  }

  @Test
  public void testFlatTopSegment() {
    checkCCW(false, "POLYGON ((100 200, 200 200, 200 100, 100 100, 100 200))");
  }

  @Test
  public void testFlatMultipleTopSegment() {
    checkCCW(false, "POLYGON ((100 200, 127 200, 151 200, 173 200, 200 200, 100 100, 100 200))");
  }

  @Test
  public void testDegenerateRingHorizontal() {
    checkCCW(false, "POLYGON ((100 200, 100 200, 200 200, 100 200))");
  }

  @Test
  public void testDegenerateRingAngled() {
    checkCCW(false, "POLYGON ((100 100, 100 100, 200 200, 100 100))");
  }

  @Test
  public void testDegenerateRingVertical() {
    checkCCW(false, "POLYGON ((200 100, 200 100, 200 200, 200 100))");
  }

  /** This case is an invalid ring, so answer is a default value */
  @Test
  public void testTopAngledSegmentCollapse() {
    checkCCW(false, "POLYGON ((10 20, 61 20, 20 30, 50 60, 10 20))");
  }

  @Test
  public void testABATopFlatSegmentCollapse() {
    checkCCW(true, "POLYGON ((71 0, 40 40, 70 40, 40 40, 20 0, 71 0))");
  }

  @Test
  public void testABATopFlatSegmentCollapseMiddleStart() {
    checkCCW(true, "POLYGON ((90 90, 50 90, 10 10, 90 10, 50 90, 90 90))");
  }

  @Test
  public void testMultipleTopFlatSegmentCollapseSinglePoint() {
    checkCCW(
        true, "POLYGON ((100 100, 200 100, 150 200, 170 200, 200 200, 100 200, 150 200, 100 100))");
  }

  @Test
  public void testMultipleTopFlatSegmentCollapseFlatTop() {
    checkCCW(true, "POLYGON ((10 10, 90 10, 70 70, 90 70, 10 70, 30 70, 50 70, 10 10))");
  }

  /** Signed-area orientation returns orientation of largest enclosed area */
  @Test
  public void testBowTieByArea() {
    checkCCWArea(true, "POLYGON ((10 10, 50 10, 25 35, 35 35, 10 10))");
  }

  private void checkCCW(boolean expectedCCW, String wkt) {
    Coordinate[] pts2x = getCoordinates(wkt);
    assertEquals(expectedCCW, Orientation.isCCW(pts2x), "Coordinate array isCCW: ");
    CoordinateSequence seq2x = getCoordinateSequence(wkt);
    assertEquals(expectedCCW, Orientation.isCCW(seq2x), "CoordinateSequence isCCW: ");
  }

  private void checkCCWArea(boolean expectedCCW, String wkt) {
    Coordinate[] pts = getCoordinates(wkt);
    assertEquals(expectedCCW, Orientation.isCCWArea(pts), "Coordinate array isCCW: ");
  }

  private Coordinate[] getCoordinates(String wkt) {
    Geometry geom = read(wkt);
    return geom.getCoordinates();
  }

  private CoordinateSequence getCoordinateSequence(String wkt) {
    Geometry geom = read(wkt);
    if (!geom.getGeometryType().equals("Polygon")) throw new IllegalArgumentException("wkt");
    Polygon poly = (Polygon) geom;
    return ((Polygon) geom).getExteriorRing().getCoordinateSequence();
  }
}
