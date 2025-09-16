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

import org.junit.jupiter.api.Test;

import test.jts.GeometryTestCase;

/**
 * Test for {@link GeometryCollectionIterator}.
 *
 * @version 1.7
 */
public class GeometryCollectionIteratorTest extends GeometryTestCase {
	@Test
	public void testAtomic() throws Exception {
		Polygon g = (Polygon) read("POLYGON ((1 9, 9 9, 9 1, 1 1, 1 9))");
		GeometryCollectionIterator i = new GeometryCollectionIterator(g);
		assertTrue(i.hasNext());
		assertInstanceOf(Polygon.class, i.next());
		assertFalse(i.hasNext());
	}

	@Test
	public void testGeometryCollection() throws Exception {
		GeometryCollection g = (GeometryCollection) read("GEOMETRYCOLLECTION (GEOMETRYCOLLECTION (POINT (10 10)))");
		GeometryCollectionIterator i = new GeometryCollectionIterator(g);
		assertTrue(i.hasNext());
		assertInstanceOf(GeometryCollection.class, i.next());
		assertTrue(i.hasNext());
		assertInstanceOf(GeometryCollection.class, i.next());
		assertTrue(i.hasNext());
		assertInstanceOf(Point.class, i.next());
		assertFalse(i.hasNext());
	}
}
