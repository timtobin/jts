package org.locationtech.jts.index.quadtree;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Envelope;

public class IsEmptyTest {
  @Test
  public void testSpatialIndex() throws Exception {
    Quadtree index = new Quadtree();
    assertTrue(index.isEmpty());
    assertTrue(index.isEmpty());

    index.insert(new Envelope(0, 0, 1, 1), "test");
    assertTrue(index.size() == 1);
    assertTrue(!index.isEmpty());

    index.remove(new Envelope(0, 0, 1, 1), "test");
    assertTrue(index.isEmpty());
    assertTrue(index.isEmpty());
  }
}
