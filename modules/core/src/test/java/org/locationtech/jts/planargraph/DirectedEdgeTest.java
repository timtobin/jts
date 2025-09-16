package org.locationtech.jts.planargraph;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;

public class DirectedEdgeTest {
  @Test
  public void testDirectedEdgeComparator() {
    DirectedEdge d1 =
        new DirectedEdge(
            new Node(new Coordinate(0, 0)),
            new Node(new Coordinate(10, 10)),
            new Coordinate(10, 10),
            true);
    DirectedEdge d2 =
        new DirectedEdge(
            new Node(new Coordinate(0, 0)),
            new Node(new Coordinate(20, 20)),
            new Coordinate(20, 20),
            false);
    assertEquals(0, d2.compareTo(d1));
  }

  @Test
  public void testDirectedEdgeToEdges() {
    DirectedEdge d1 =
        new DirectedEdge(
            new Node(new Coordinate(0, 0)),
            new Node(new Coordinate(10, 10)),
            new Coordinate(10, 10),
            true);
    DirectedEdge d2 =
        new DirectedEdge(
            new Node(new Coordinate(20, 0)),
            new Node(new Coordinate(20, 10)),
            new Coordinate(20, 10),
            false);
    List edges = DirectedEdge.toEdges(Arrays.asList(d1, d2));
    assertEquals(2, edges.size());
    assertNull(edges.getFirst());
    assertNull(edges.get(1));
  }
}
