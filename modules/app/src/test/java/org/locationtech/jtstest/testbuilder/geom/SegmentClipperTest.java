package org.locationtech.jtstest.testbuilder.geom;


import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;

public class SegmentClipperTest {

  @Test
  public void testSimple() {
    checkClip(new Coordinate(0, 10), new Coordinate(20, 30), 
        new Envelope(10, 100, 10, 100), 
        new Coordinate(10, 20), new Coordinate(20, 30) );
  }
  public void checkClip(Coordinate p0, Coordinate p1, Envelope env, Coordinate expected0, Coordinate expected1) {

    SegmentClipper.clip(p0, p1, env);
    boolean isOK = expected0.equals2D(p0) && expected1.equals2D(p1);
    if (!isOK) {
      System.out.println("FAIL: " 
          + "Actual = " + p0 + " - " + p1 
          + " , Expected = " + expected0 + " - " + expected1);
    }
  }

  
}
