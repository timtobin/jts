package org.locationtech.jts.util;

import static org.junit.jupiter.api.Assertions.assertEquals;


import org.junit.jupiter.api.Test;

public class IntArrayListTest {
  @Test
  public void testEmpty() {
    IntArrayList iar = new IntArrayList();
    assertEquals(0, iar.size());
  }

  @Test
  public void testAddFew() {
    IntArrayList iar = new IntArrayList();
    iar.add(1);
    iar.add(2);
    iar.add(3);
    assertEquals(3, iar.size());
    
    int[] data = iar.toArray();
    assertEquals(3, data.length);
    assertEquals(1, data[0]);
    assertEquals(2, data[1]);
    assertEquals(3, data[2]);
  }

  @Test
  public void testAddMany() {
    IntArrayList iar = new IntArrayList(20);
    
    int max = 100;
    for (int i = 0; i < max; i++) {
      iar.add(i);
    }

    assertEquals(max, iar.size());
    
    int[] data = iar.toArray();
    assertEquals(max, data.length);
    for (int j = 0; j < max; j++) {
      assertEquals(j, data[j]);
    }
  }

  @Test
  public void testAddAll() {
    IntArrayList iar = new IntArrayList();
    
    iar.addAll(null);
    iar.addAll(new int[0]);
    iar.addAll(new int[] { 1,2,3 });
    assertEquals(3, iar.size());
    
    int[] data = iar.toArray();
    assertEquals(3, data.length);
    assertEquals(1, data[0]);
    assertEquals(2, data[1]);
    assertEquals(3, data[2]);
  }
  

}
