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

import org.junit.jupiter.api.Test;

/**
 * @version 1.7
 */
public class NonRobustLineIntersectorTest {

	private final NonRobustLineIntersector li = new NonRobustLineIntersector();

	@Test
	public void testGetIntersectionNum() {
		// MD: NonRobustLineIntersector may have different semantics for
		// getIntersectionNumber
		// li.computeIntersection(new Coordinate(220, 0), new Coordinate(110, 0),
		// new Coordinate(0, 0), new Coordinate(110, 0));
		// assertEquals(1, li.getIntersectionNum());
	}

	@Test
	public void testNegativeZero() {
		// MD suggests we ignore this issue for now.
		// li.computeIntersection(new Coordinate(220, 260), new Coordinate(220, 0),
		// new Coordinate(220, 0), new Coordinate(100, 0));
		// assertEquals((new Coordinate(220, 0)).toString(),
		// li.getIntersection(0).toString());
	}
} // public class NonRobustLineIntersectorTest extends TestCase
