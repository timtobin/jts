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
package org.locationtech.jts.operation.linemerge;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.util.Assert;


/**
 * @version 1.7
 */
public class LineMergerTest {
  private static final WKTReader reader = new WKTReader();

  @Test
  public void test1() {
    doTest(new String[]{
        "LINESTRING (120 120, 180 140)", "LINESTRING (200 180, 180 140)",
        "LINESTRING (200 180, 240 180)"
    }, new String[]{"LINESTRING (120 120, 180 140, 200 180, 240 180)"});
  }

  @Test
  public void test2() {
    doTest(new String[]{"LINESTRING (120 300, 80 340)",
            "LINESTRING (120 300, 140 320, 160 320)",
            "LINESTRING (40 320, 20 340, 0 320)",
            "LINESTRING (0 320, 20 300, 40 320)",
            "LINESTRING (40 320, 60 320, 80 340)",
            "LINESTRING (160 320, 180 340, 200 320)",
            "LINESTRING (200 320, 180 300, 160 320)"},
        new String[]{
            "LINESTRING (160 320, 180 340, 200 320, 180 300, 160 320)",
            "LINESTRING (40 320, 20 340, 0 320, 20 300, 40 320)",
            "LINESTRING (40 320, 60 320, 80 340, 120 300, 140 320, 160 320)"});
  }

  @Test
  public void test3() {
    doTest(new String[]{"LINESTRING (0 0, 100 100)", "LINESTRING (0 100, 100 0)"},
        new String[]{"LINESTRING (0 0, 100 100)", "LINESTRING (0 100, 100 0)"});
  }

  @Test
  public void test4() {
    doTest(new String[]{"LINESTRING EMPTY", "LINESTRING EMPTY"},
        new String[]{});
  }

  @Test
  public void test5() {
    doTest(new String[]{},
        new String[]{});
  }

  @Test
  public void testSingleUniquePoint() {
    doTest(new String[]{"LINESTRING (10642 31441, 10642 31441)", "LINESTRING EMPTY"},
        new String[]{});
  }


  private void doTest(String[] inputWKT, String[] expectedOutputWKT) {
    doTest(inputWKT, expectedOutputWKT, true);
  }

  public static void doTest(String[] inputWKT, String[] expectedOutputWKT, boolean compareDirections) {
    LineMerger lineMerger = new LineMerger();
    lineMerger.add(toGeometries(inputWKT));
    compare(toGeometries(expectedOutputWKT), lineMerger.getMergedLineStrings(), compareDirections);
  }

  public static void compare(Collection expectedGeometries,
      Collection actualGeometries, boolean compareDirections) {
    assertEquals(expectedGeometries.size(), actualGeometries.size(), "Geometry count, " + actualGeometries);
    for (Object geometry : expectedGeometries) {
      Geometry expectedGeometry = (Geometry) geometry;
      assertTrue(contains(actualGeometries, expectedGeometry, compareDirections),
          "Not found: " + expectedGeometry + ", " + actualGeometries);
    }
  }

  private static boolean contains(Collection geometries, Geometry g, boolean exact) {
    for (Object geometry : geometries) {
      Geometry element = (Geometry) geometry;
      if (exact && element.equalsExact(g)) {
        return true;
      }
      if (!exact && element.equalsTopo(g)) {
        return true;
      }
    }

    return false;
  }

  public static Collection toGeometries(String[] inputWKT) {
    ArrayList geometries = new ArrayList();
    for (String s : inputWKT) {
      try {
        geometries.add(reader.read(s));
      } catch (ParseException e) {
        Assert.shouldNeverReachHere();
      }
    }

    return geometries;
  }
}
