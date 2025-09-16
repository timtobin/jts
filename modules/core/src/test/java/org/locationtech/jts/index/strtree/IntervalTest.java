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
package org.locationtech.jts.index.strtree;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * @version 1.7
 */
public class IntervalTest {
	@Test
	public void testCopyConstructor() {
		assertEquals(new Interval(3, 4), new Interval(3, 4));
		assertEquals(new Interval(3, 4), new Interval(new Interval(3, 4)));
	}

	@Test
	public void testExpandToInclude() {
		assertEquals(new Interval(3, 8), new Interval(3, 4).expandToInclude(new Interval(7, 8)));
		assertEquals(new Interval(3, 7), new Interval(3, 7).expandToInclude(new Interval(4, 5)));
		assertEquals(new Interval(3, 8), new Interval(3, 7).expandToInclude(new Interval(4, 8)));
	}

	@Test
	public void testGetCentre() {
		assertEquals(6.5, new Interval(4, 9).getCentre(), 1E-10);
	}

	@Test
	public void testIntersectsBasic() {
		assertTrue(new Interval(5, 10).intersects(new Interval(7, 12)));
		assertTrue(new Interval(7, 12).intersects(new Interval(5, 10)));
		assertTrue(!new Interval(5, 10).intersects(new Interval(11, 12)));
		assertTrue(!new Interval(11, 12).intersects(new Interval(5, 10)));
		assertTrue(new Interval(5, 10).intersects(new Interval(10, 12)));
		assertTrue(new Interval(10, 12).intersects(new Interval(5, 10)));
	}

	@Test
	public void testIntersectsZeroWidthInterval() {
		assertTrue(new Interval(10, 10).intersects(new Interval(7, 12)));
		assertTrue(new Interval(7, 12).intersects(new Interval(10, 10)));
		assertTrue(!new Interval(10, 10).intersects(new Interval(11, 12)));
		assertTrue(!new Interval(11, 12).intersects(new Interval(10, 10)));
		assertTrue(new Interval(10, 10).intersects(new Interval(10, 12)));
		assertTrue(new Interval(10, 12).intersects(new Interval(10, 10)));
	}
}
