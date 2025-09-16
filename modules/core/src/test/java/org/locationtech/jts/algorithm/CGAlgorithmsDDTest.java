package org.locationtech.jts.algorithm;

import static org.junit.jupiter.api.Assertions.assertEquals;


import org.junit.jupiter.api.Test;


public class CGAlgorithmsDDTest {
  @Test
  public void testSignOfDet2x2() {
    checkSignOfDet2x2(1, 1, 2, 2, 0);
    checkSignOfDet2x2(1, 1, 2, 3, 1);
    checkSignOfDet2x2(1, 1, 3, 2, -1);
  }

  private void checkSignOfDet2x2(double x1, double y1, double x2, double y2, int sign) {
    assertEquals(sign, CGAlgorithmsDD.signOfDet2x2(x1, y1, x2, y2));
  }
}
