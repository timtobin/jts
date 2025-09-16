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

package org.locationtech.jts.geom;

import org.locationtech.jts.io.WKTReader;



import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Test for com.vividsolutions.jts.geom.impl.PointImpl.
 *
 * @version 1.7
 */
public class PointImplTest {

  PrecisionModel precisionModel = new PrecisionModel(1000);
  GeometryFactory geometryFactory = new GeometryFactory(precisionModel, 0);
  WKTReader reader = new WKTReader(geometryFactory);

  @org.junit.jupiter.api.Test
  public void testEquals1() throws Exception {
    Point p1 = (Point) reader.read("POINT(1.234 5.678)");
    Point p2 = (Point) reader.read("POINT(1.234 5.678)");
    assertTrue(p1.equals(p2));
  }

  @org.junit.jupiter.api.Test
  public void testEquals2() throws Exception {
    Point p1 = (Point) reader.read("POINT(1.23 5.67)");
    Point p2 = (Point) reader.read("POINT(1.23 5.67)");
    assertTrue(p1.equals(p2));
  }

  @org.junit.jupiter.api.Test
  public void testEquals3() throws Exception {
    Point p1 = (Point) reader.read("POINT(1.235 5.678)");
    Point p2 = (Point) reader.read("POINT(1.234 5.678)");
    assertTrue(!p1.equals(p2));
  }

  @org.junit.jupiter.api.Test
  public void testEquals4() throws Exception {
    Point p1 = (Point) reader.read("POINT(1.2334 5.678)");
    Point p2 = (Point) reader.read("POINT(1.2333 5.678)");
    assertTrue(p1.equals(p2));
  }

  @org.junit.jupiter.api.Test
  public void testEquals5() throws Exception {
    Point p1 = (Point) reader.read("POINT(1.2334 5.678)");
    Point p2 = (Point) reader.read("POINT(1.2335 5.678)");
    assertTrue(!p1.equals(p2));
  }

  @org.junit.jupiter.api.Test
  public void testEquals6() throws Exception {
    Point p1 = (Point) reader.read("POINT(1.2324 5.678)");
    Point p2 = (Point) reader.read("POINT(1.2325 5.678)");
    assertTrue(!p1.equals(p2));
  }

  @org.junit.jupiter.api.Test
  public void testNegRounding1() throws Exception {
    Point pLo = (Point) reader.read("POINT(-1.233 5.678)");
    Point pHi = (Point) reader.read("POINT(-1.232 5.678)");

    Point p1 = (Point) reader.read("POINT(-1.2326 5.678)");
    Point p2 = (Point) reader.read("POINT(-1.2325 5.678)");
    Point p3 = (Point) reader.read("POINT(-1.2324 5.678)");

    assertTrue(!p1.equals(p2));
    assertTrue(p3.equals(p2));

    assertTrue(p1.equals(pLo));
    assertTrue(p2.equals(pHi));
    assertTrue(p3.equals(pHi));
  }

  @org.junit.jupiter.api.Test
  public void testIsSimple() throws Exception {
    Point p1 = (Point) reader.read("POINT(1.2324 5.678)");
    assertTrue(p1.isSimple());
    Point p2 = (Point) reader.read("POINT EMPTY");
    assertTrue(p2.isSimple());
  }


}
