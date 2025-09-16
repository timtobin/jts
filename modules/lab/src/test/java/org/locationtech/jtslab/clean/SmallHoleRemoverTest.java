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
package org.locationtech.jtslab.clean;

import static org.junit.jupiter.api.Assertions.assertTrue;


import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

public class SmallHoleRemoverTest {

  private WKTReader reader = new WKTReader();

  @Test
  public void testNoHole() {
    checkHolesRemoved("POLYGON ((1 9, 9 9, 9 1, 1 1, 1 9))", 
        "POLYGON ((1 9, 9 9, 9 1, 1 1, 1 9))");
  }

  @Test
  public void testOneLarge() {
    checkHolesRemoved("POLYGON ((100 200, 200 200, 200 100, 100 100, 100 200), (130 180, 175 180, 175 136, 130 136, 130 180))", 
        "POLYGON ((100 200, 200 200, 200 100, 100 100, 100 200), (130 180, 175 180, 175 136, 130 136, 130 180))");
  }

  @Test
  public void testOneSmall() {
    checkHolesRemoved("POLYGON ((100 200, 200 200, 200 100, 100 100, 100 200), (130 160, 140 150, 130 150, 130 160))", 
        "POLYGON ((100 200, 200 200, 200 100, 100 100, 100 200))");
  }

  @Test
  public void testOneLargeOneSmall() {
    checkHolesRemoved("POLYGON ((100 200, 200 200, 200 100, 100 100, 100 200), (130 160, 140 150, 130 150, 130 160), (150 190, 190 190, 190 150, 150 150, 150 190))", 
        "POLYGON ((100 200, 200 200, 200 100, 100 100, 100 200), (150 190, 190 190, 190 150, 150 150, 150 190))");
  }


  @Test
  public void testOneSmallMP() {
    checkHolesRemoved("MULTIPOLYGON (((1 9, 9 9, 9 1, 1 1, 1 9), (2 5, 2 2, 12 2, 2 5)), ((21 9, 25 9, 25 5, 21 5, 21 9)))", 
        "MULTIPOLYGON (((1 9, 9 9, 9 1, 1 1, 1 9)), ((21 9, 25 9, 25 5, 21 5, 21 9)))");
  }

  @Test
  public void testOneSmallGC() {
    checkHolesRemoved("GEOMETRYCOLLECTION (POLYGON ((1 9, 9 9, 9 1, 1 1, 1 9), (2 5, 2 2, 12 2, 2 5)), LINESTRING (15 9, 19 5))", 
        "GEOMETRYCOLLECTION (POLYGON ((1 9, 9 9, 9 1, 1 1, 1 9)), LINESTRING (15 9, 19 5))");
  }

  private void checkHolesRemoved(String inputWKT, String expectedWKT) {
    Geometry input = read(inputWKT);
    Geometry expected = read(expectedWKT);
    
    Geometry actual = SmallHoleRemover.clean(input, 100);
    checkEqual(expected, actual);
  }

  private void checkEqual(Geometry expected, Geometry actual) {
    Geometry actualNorm = actual.norm();
    boolean equal = actualNorm.equalsExact(expected.norm());
    if (! equal) {
      System.out.println("FAIL - Expected = " + expected
          + " actual = " + actual.norm());
    }
    assertTrue(equal);
  }

  private Geometry read(String wkt) {
    try {
       return reader.read(wkt);
    } catch (ParseException e) {
      throw new RuntimeException(e.getMessage());
    }
    
  }

}
