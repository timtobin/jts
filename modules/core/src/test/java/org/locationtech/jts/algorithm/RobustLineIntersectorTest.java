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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;


/**
 * Basic functionality tests for RobustLineIntersector.
 * 
 * @version 1.7
 */
public class RobustLineIntersectorTest {

  RobustLineIntersector i = new RobustLineIntersector();


  @Test
  public void test2Lines() {
    RobustLineIntersector i = new RobustLineIntersector();
    Coordinate p1 = new Coordinate(10, 10);
    Coordinate p2 = new Coordinate(20, 20);
    Coordinate q1 = new Coordinate(20, 10);
    Coordinate q2 = new Coordinate(10, 20);
    Coordinate x = new Coordinate(15, 15);
    i.computeIntersection(p1, p2, q1, q2);
    assertEquals(RobustLineIntersector.POINT_INTERSECTION, i.getIntersectionNum());
    assertEquals(1, i.getIntersectionNum());
    assertEquals(x, i.getIntersection(0));
    assertTrue(i.isProper());
    assertTrue(i.hasIntersection());
  }

  @Test
  public void testCollinear1() {
    RobustLineIntersector i = new RobustLineIntersector();
    Coordinate p1 = new Coordinate(10, 10);
    Coordinate p2 = new Coordinate(20, 10);
    Coordinate q1 = new Coordinate(22, 10);
    Coordinate q2 = new Coordinate(30, 10);
    i.computeIntersection(p1, p2, q1, q2);
    assertEquals(RobustLineIntersector.NO_INTERSECTION, i.getIntersectionNum());
    assertTrue(!i.isProper());
    assertTrue(!i.hasIntersection());
  }

  @Test
  public void testCollinear2() {
    RobustLineIntersector i = new RobustLineIntersector();
    Coordinate p1 = new Coordinate(10, 10);
    Coordinate p2 = new Coordinate(20, 10);
    Coordinate q1 = new Coordinate(20, 10);
    Coordinate q2 = new Coordinate(30, 10);
    i.computeIntersection(p1, p2, q1, q2);
    assertEquals(RobustLineIntersector.POINT_INTERSECTION, i.getIntersectionNum());
    assertTrue(!i.isProper());
    assertTrue(i.hasIntersection());
  }

  @Test
  public void testCollinear3() {
    RobustLineIntersector i = new RobustLineIntersector();
    Coordinate p1 = new Coordinate(10, 10);
    Coordinate p2 = new Coordinate(20, 10);
    Coordinate q1 = new Coordinate(15, 10);
    Coordinate q2 = new Coordinate(30, 10);
    i.computeIntersection(p1, p2, q1, q2);
    assertEquals(RobustLineIntersector.COLLINEAR_INTERSECTION, i.getIntersectionNum());
    assertTrue(!i.isProper());
    assertTrue(i.hasIntersection());
  }

  @Test
  public void testCollinear4() {
    RobustLineIntersector i = new RobustLineIntersector();
    Coordinate p1 = new Coordinate(30, 10);
    Coordinate p2 = new Coordinate(20, 10);
    Coordinate q1 = new Coordinate(10, 10);
    Coordinate q2 = new Coordinate(30, 10);
    i.computeIntersection(p1, p2, q1, q2);
    assertEquals(RobustLineIntersector.COLLINEAR_INTERSECTION, i.getIntersectionNum());
    assertTrue(i.hasIntersection());
  }

  @Test
  public void testEndpointIntersection() {
    i.computeIntersection(new Coordinate(100, 100), new Coordinate(10, 100),
        new Coordinate(100, 10), new Coordinate(100, 100));
    assertTrue(i.hasIntersection());
    assertEquals(1, i.getIntersectionNum());
  }

  @Test
  public void testEndpointIntersection2() {
    i.computeIntersection(new Coordinate(190, 50), new Coordinate(120, 100),
        new Coordinate(120, 100), new Coordinate(50, 150));
    assertTrue(i.hasIntersection());
    assertEquals(1, i.getIntersectionNum());
    assertEquals(new Coordinate(120, 100), i.getIntersection(1));
  }

  @Test
  public void testOverlap() {
    i.computeIntersection(new Coordinate(180, 200), new Coordinate(160, 180),
        new Coordinate(220, 240), new Coordinate(140, 160));
    assertTrue(i.hasIntersection());
    assertEquals(2, i.getIntersectionNum());
  }

  @Test
  public void testIsProper1() {
    i.computeIntersection(new Coordinate(30, 10), new Coordinate(30, 30),
        new Coordinate(10, 10), new Coordinate(90, 11));
    assertTrue(i.hasIntersection());
    assertEquals(1, i.getIntersectionNum());
    assertTrue(i.isProper());
  }

  @Test
  public void testIsProper2() {
    i.computeIntersection(new Coordinate(10, 30), new Coordinate(10, 0),
        new Coordinate(11, 90), new Coordinate(10, 10));
    assertTrue(i.hasIntersection());
    assertEquals(1, i.getIntersectionNum());
    assertTrue(!i.isProper());
  }

  @Test
  public void testIsCCW() {
    assertEquals(1, Orientation.index(new Coordinate(-123456789, -40), new Coordinate(0, 0), new Coordinate(381039468754763d, 123456789)));
  }

  @Test
  public void testIsCCW2() {
    assertEquals(0, Orientation.index(new Coordinate(10, 10), new Coordinate(20, 20), new Coordinate(0, 0)));
  }

  @Test
  public void testA() {
    Coordinate p1 = new Coordinate(-123456789, -40);
    Coordinate p2 = new Coordinate(381039468754763d, 123456789);
    Coordinate q = new Coordinate(0, 0);
    LineString l = new GeometryFactory().createLineString(new Coordinate[]{p1, p2});
    Point p = new GeometryFactory().createPoint(q);
    assertEquals(false, l.intersects(p));
    assertEquals(false, PointLocation.isOnLine(q, new Coordinate[]{p1, p2}));
    assertEquals(-1, Orientation.index(p1, p2, q));
  }

}
