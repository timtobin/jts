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
package org.locationtech.jts.precision;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.WKTReader;

/**
 * @version 1.7
 */
public class SimpleGeometryPrecisionReducerTest {
  private final PrecisionModel pmFloat = new PrecisionModel();
  private final PrecisionModel pmFixed1 = new PrecisionModel(1);
  private final SimpleGeometryPrecisionReducer reducer =
      new SimpleGeometryPrecisionReducer(pmFixed1);
  private final SimpleGeometryPrecisionReducer reducerKeepCollapse =
      new SimpleGeometryPrecisionReducer(pmFixed1);

  private final GeometryFactory gfFloat = new GeometryFactory(pmFloat, 0);
  WKTReader reader = new WKTReader(gfFloat);

  public SimpleGeometryPrecisionReducerTest() {
    reducerKeepCollapse.setRemoveCollapsedComponents(false);
  }

  @Test
  public void testSquare() throws Exception {
    Geometry g = reader.read("POLYGON (( 0 0, 0 1.4, 1.4 1.4, 1.4 0, 0 0 ))");
    Geometry g2 = reader.read("POLYGON (( 0 0, 0 1, 1 1, 1 0, 0 0 ))");
    Geometry gReduce = reducer.reduce(g);
    assertTrue(gReduce.equalsExact(g2));
  }

  @Test
  public void testTinySquareCollapse() throws Exception {
    Geometry g = reader.read("POLYGON (( 0 0, 0 .4, .4 .4, .4 0, 0 0 ))");
    Geometry g2 = reader.read("POLYGON EMPTY");
    Geometry gReduce = reducer.reduce(g);
    assertTrue(gReduce.equalsExact(g2));
  }

  @Test
  public void testSquareCollapse() throws Exception {
    Geometry g = reader.read("POLYGON (( 0 0, 0 1.4, .4 .4, .4 0, 0 0 ))");
    Geometry g2 = reader.read("POLYGON EMPTY");
    Geometry gReduce = reducer.reduce(g);
    assertTrue(gReduce.equalsExact(g2));
  }

  @Test
  public void testSquareKeepCollapse() throws Exception {
    Geometry g = reader.read("POLYGON (( 0 0, 0 1.4, .4 .4, .4 0, 0 0 ))");
    Geometry g2 = reader.read("POLYGON (( 0 0, 0 1, 0 0, 0 0, 0 0 ))");
    Geometry gReduce = reducerKeepCollapse.reduce(g);
    assertTrue(gReduce.equalsExact(g2));
  }

  @Test
  public void testLine() throws Exception {
    Geometry g = reader.read("LINESTRING ( 0 0, 0 1.4 )");
    Geometry g2 = reader.read("LINESTRING (0 0, 0 1)");
    Geometry gReduce = reducer.reduce(g);
    assertTrue(gReduce.equalsExact(g2));
  }

  @Test
  public void testLineRemoveCollapse() throws Exception {
    Geometry g = reader.read("LINESTRING ( 0 0, 0 .4 )");
    Geometry g2 = reader.read("LINESTRING EMPTY");
    Geometry gReduce = reducer.reduce(g);
    assertTrue(gReduce.equalsExact(g2));
  }

  @Test
  public void testLineKeepCollapse() throws Exception {
    Geometry g = reader.read("LINESTRING ( 0 0, 0 .4 )");
    Geometry g2 = reader.read("LINESTRING ( 0 0, 0 0 )");
    Geometry gReduce = reducerKeepCollapse.reduce(g);
    assertTrue(gReduce.equalsExact(g2));
  }
}
