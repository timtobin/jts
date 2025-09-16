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

import static org.junit.jupiter.api.Assertions.*;


/**
 * @version 1.7
 */
public class IntersectionMatrixTest {

  private static final int A = Dimension.A;
  private static final int L = Dimension.L;
  private static final int P = Dimension.P;

  @org.junit.jupiter.api.Test
  public void testToString() throws Exception {
    IntersectionMatrix i = new IntersectionMatrix();
    i.set("012*TF012");
    assertEquals("012*TF012", i.toString());

    IntersectionMatrix c = new IntersectionMatrix(i);
    assertEquals("012*TF012", c.toString());
  }

  @org.junit.jupiter.api.Test
  public void testTranspose() {
    IntersectionMatrix x = new IntersectionMatrix("012*TF012");

    IntersectionMatrix i = new IntersectionMatrix(x);
    IntersectionMatrix j = i.transpose();
    assertSame(i, j);

    assertEquals("0*01T12F2", i.toString());

    assertEquals("012*TF012", x.toString());
  }

  @org.junit.jupiter.api.Test
  public void testTransposeString() {
    checkTranspose("T*****FF*", "T*F**F***");
    checkTranspose("012*TF012", "0*01T12F2");
  }

  private void checkTranspose(String im, String imTrans) {
    assertEquals(IntersectionMatrix.transpose(im), imTrans);
    assertEquals(IntersectionMatrix.transpose(imTrans), im);
  }

  @org.junit.jupiter.api.Test
  public void testIsDisjoint() {
    assertTrue((new IntersectionMatrix("FF*FF****")).isDisjoint());
    assertTrue((new IntersectionMatrix("FF1FF2T*0")).isDisjoint());
    assertTrue(!(new IntersectionMatrix("*F*FF****")).isDisjoint());
  }

  @org.junit.jupiter.api.Test
  public void testIsTouches() {
    assertTrue((new IntersectionMatrix("FT*******")).isTouches(P, A));
    assertTrue((new IntersectionMatrix("FT*******")).isTouches(A, P));
    assertTrue(!(new IntersectionMatrix("FT*******")).isTouches(P, P));
  }

  @org.junit.jupiter.api.Test
  public void testIsIntersects() {
    assertTrue(!(new IntersectionMatrix("FF*FF****")).isIntersects());
    assertTrue(!(new IntersectionMatrix("FF1FF2T*0")).isIntersects());
    assertTrue((new IntersectionMatrix("*F*FF****")).isIntersects());
  }

  @org.junit.jupiter.api.Test
  public void testIsCrosses() {
    assertTrue((new IntersectionMatrix("TFTFFFFFF")).isCrosses(P, L));
    assertTrue(!(new IntersectionMatrix("TFTFFFFFF")).isCrosses(L, P));
    assertTrue(!(new IntersectionMatrix("TFFFFFTFF")).isCrosses(P, L));
    assertTrue((new IntersectionMatrix("TFFFFFTFF")).isCrosses(L, P));
    assertTrue((new IntersectionMatrix("0FFFFFFFF")).isCrosses(L, L));
    assertTrue(!(new IntersectionMatrix("1FFFFFFFF")).isCrosses(L, L));
  }

  @org.junit.jupiter.api.Test
  public void testIsWithin() {
    assertTrue((new IntersectionMatrix("T0F00F000")).isWithin());
    assertTrue(!(new IntersectionMatrix("T00000FF0")).isWithin());
  }

  @org.junit.jupiter.api.Test
  public void testIsContains() {
    assertTrue(!(new IntersectionMatrix("T0F00F000")).isContains());
    assertTrue((new IntersectionMatrix("T00000FF0")).isContains());
  }

  @org.junit.jupiter.api.Test
  public void testIsOverlaps() {
    assertTrue((new IntersectionMatrix("2*2***2**")).isOverlaps(P, P));
    assertTrue((new IntersectionMatrix("2*2***2**")).isOverlaps(A, A));
    assertTrue(!(new IntersectionMatrix("2*2***2**")).isOverlaps(P, A));
    assertTrue(!(new IntersectionMatrix("2*2***2**")).isOverlaps(L, L));
    assertTrue((new IntersectionMatrix("1*2***2**")).isOverlaps(L, L));

    assertTrue(!(new IntersectionMatrix("0FFFFFFF2")).isOverlaps(P, P));
    assertTrue(!(new IntersectionMatrix("1FFF0FFF2")).isOverlaps(L, L));
    assertTrue(!(new IntersectionMatrix("2FFF1FFF2")).isOverlaps(A, A));
  }

  @org.junit.jupiter.api.Test
  public void testIsEquals() {
    assertTrue((new IntersectionMatrix("0FFFFFFF2")).isEquals(P, P));
    assertTrue((new IntersectionMatrix("1FFF0FFF2")).isEquals(L, L));
    assertTrue((new IntersectionMatrix("2FFF1FFF2")).isEquals(A, A));

    assertTrue(!(new IntersectionMatrix("0F0FFFFF2")).isEquals(P, P));
    assertTrue((new IntersectionMatrix("1FFF1FFF2")).isEquals(L, L));
    assertTrue(!(new IntersectionMatrix("2FFF1*FF2")).isEquals(A, A));

    assertTrue(!(new IntersectionMatrix("0FFFFFFF2")).isEquals(P, L));
    assertTrue(!(new IntersectionMatrix("1FFF0FFF2")).isEquals(L, A));
    assertTrue(!(new IntersectionMatrix("2FFF1FFF2")).isEquals(A, P));
  }

}
