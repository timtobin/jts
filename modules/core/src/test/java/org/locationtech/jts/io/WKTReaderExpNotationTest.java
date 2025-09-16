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
package org.locationtech.jts.io;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

/** Tests the {@link WKTReader} with exponential notation. */
public class WKTReaderExpNotationTest {
  private final GeometryFactory fact = new GeometryFactory();
  private final WKTReader rdr = new WKTReader(fact);

  @Test
  public void testGoodBasicExp() throws IOException, ParseException {
    readGoodCheckCoordinate("POINT ( 1e01 -1E02)", 1E01, -1E02);
  }

  @Test
  public void testGoodWithExpSign() throws IOException, ParseException {
    readGoodCheckCoordinate("POINT ( 1e-04 1E-05)", 1e-04, 1e-05);
  }

  @Test
  public void testBadExpFormat() throws IOException, ParseException {
    readBad("POINT (1e0a1 1X02)");
  }

  @Test
  public void testBadExpPlusSign() throws IOException, ParseException {
    readBad("POINT (1e+01 1X02)");
  }

  @Test
  public void testBadPlusSign() throws IOException, ParseException {
    readBad("POINT ( +1e+01 1X02)");
  }

  private void readGoodCheckCoordinate(String wkt, double x, double y)
      throws IOException, ParseException {
    Geometry g = rdr.read(wkt);
    Coordinate pt = g.getCoordinate();
    assertEquals(pt.x, x, 0.0001);
    assertEquals(pt.y, y, 0.0001);
  }

  private void readBad(String wkt) throws IOException {
    boolean threwParseEx = false;
    try {
      Geometry g = rdr.read(wkt);
    } catch (ParseException ex) {
      // System.out.println(ex.getMessage());
      threwParseEx = true;
    }
    assertTrue(threwParseEx);
  }
}
