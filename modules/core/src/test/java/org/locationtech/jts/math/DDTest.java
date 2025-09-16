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

package org.locationtech.jts.math;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests basic accessor and mutator operations for {@link DD}s.
 *
 * @author Martin Davis
 */
public class DDTest {
  private static final double VALUE_DBL = 2.2;

  @Test
  public void testSetValueDouble() {
    assertTrue(VALUE_DBL == (new DD(1)).setValue(VALUE_DBL).doubleValue());
  }

  @Test
  public void testSetValueDD() {
    assertTrue((new DD(VALUE_DBL)).equals((new DD(1)).setValue(new DD(2.2))));
    assertTrue(DD.PI.equals((new DD(1)).setValue(DD.PI)));
  }

  @Test
  public void testCopy() {
    assertTrue((new DD(VALUE_DBL)).equals(DD.copy(new DD(VALUE_DBL))));
    assertTrue(DD.PI.equals(DD.copy(DD.PI)));
  }
}
