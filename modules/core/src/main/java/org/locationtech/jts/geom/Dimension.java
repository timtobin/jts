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

/**
 * Provides constants representing the dimensions of a point, a curve and a surface. Also provides
 * constants representing the dimensions of the empty geometry and non-empty geometries, and the
 * wildcard constant {@link #DONTCARE} meaning "any dimension". These constants are used as the
 * entries in {@link IntersectionMatrix}s.
 *
 * @version 1.7
 */
public class Dimension {

  /** Dimension value of a point (0). */
  public static final int P = 0;

  /** Dimension value of a curve (1). */
  public static final int L = 1;

  /** Dimension value of a surface (2). */
  public static final int A = 2;

  /** Dimension value of the empty geometry (-1). */
  public static final int FALSE = -1;

  /** Dimension value of non-empty geometries (= {P, L, A}). */
  public static final int TRUE = -2;

  /** Dimension value for any dimension (= {FALSE, TRUE}). */
  public static final int DONTCARE = -3;

  /** Symbol for the FALSE pattern matrix entry */
  public static final char SYM_FALSE = 'F';

  /** Symbol for the TRUE pattern matrix entry */
  public static final char SYM_TRUE = 'T';

  /** Symbol for the DONTCARE pattern matrix entry */
  public static final char SYM_DONTCARE = '*';

  /** Symbol for the P (dimension 0) pattern matrix entry */
  public static final char SYM_P = '0';

  /** Symbol for the L (dimension 1) pattern matrix entry */
  public static final char SYM_L = '1';

  /** Symbol for the A (dimension 2) pattern matrix entry */
  public static final char SYM_A = '2';

  /**
   * Converts the dimension value to a dimension symbol, for example, <code>TRUE =&gt; 'T'</code> .
   *
   * @param dimensionValue a number that can be stored in the <code>IntersectionMatrix</code> .
   *     Possible values are <code>{TRUE, FALSE, DONTCARE, 0, 1, 2}</code>.
   * @return a character for use in the string representation of an <code>IntersectionMatrix</code>.
   *     Possible values are <code>{T, F, * , 0, 1, 2}</code> .
   */
  public static char toDimensionSymbol(int dimensionValue) {
    return switch (dimensionValue) {
      case FALSE -> SYM_FALSE;
      case TRUE -> SYM_TRUE;
      case DONTCARE -> SYM_DONTCARE;
      case P -> SYM_P;
      case L -> SYM_L;
      case A -> SYM_A;
      default -> throw new IllegalArgumentException("Unknown dimension value: " + dimensionValue);
    };
  }

  /**
   * Converts the dimension symbol to a dimension value, for example, <code>'*' =&gt; DONTCARE
   * </code> .
   *
   * @param dimensionSymbol a character for use in the string representation of an <code>
   *     IntersectionMatrix</code>. Possible values are <code>{T, F, * , 0, 1, 2}</code> .
   * @return a number that can be stored in the <code>IntersectionMatrix</code> . Possible values
   *     are <code>{TRUE, FALSE, DONTCARE, 0, 1, 2}</code>.
   */
  public static int toDimensionValue(char dimensionSymbol) {
    return switch (Character.toUpperCase(dimensionSymbol)) {
      case SYM_FALSE -> FALSE;
      case SYM_TRUE -> TRUE;
      case SYM_DONTCARE -> DONTCARE;
      case SYM_P -> P;
      case SYM_L -> L;
      case SYM_A -> A;
      default -> throw new IllegalArgumentException("Unknown dimension symbol: " + dimensionSymbol);
    };
  }
}
