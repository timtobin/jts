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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * @version 1.7
 */
public class SIRtreeTest {
	@Test
	public void test() {
		TestTree t = new TestTree(2);
		t.insert(2, 6, "A");
		t.insert(2, 4, "B");
		t.insert(2, 3, "C");
		t.insert(2, 4, "D");
		t.insert(0, 1, "E");
		t.insert(2, 4, "F");
		t.insert(5, 6, "G");
		t.build();
		assertEquals(2, t.getRoot().getLevel());
		assertEquals(4, t.boundablesAtLevel(0).size());
		assertEquals(2, t.boundablesAtLevel(1).size());
		assertEquals(1, t.boundablesAtLevel(2).size());
		assertEquals(1, t.query(0.5, 0.5).size());
		assertEquals(0, t.query(1.5, 1.5).size());
		assertEquals(2, t.query(4.5, 5.5).size());
	}

	@Test
	public void testEmptyTree() {
		TestTree t = new TestTree(2);
		t.build();
		assertEquals(0, t.getRoot().getLevel());
		assertEquals(1, t.boundablesAtLevel(0).size());
		assertEquals(0, t.boundablesAtLevel(1).size());
		assertEquals(0, t.boundablesAtLevel(-1).size());
		assertEquals(0, t.query(0.5, 0.5).size());
	}

	private static class TestTree extends SIRtree {
		public TestTree(int nodeCapacity) {
			super(nodeCapacity);
		}

		protected List boundablesAtLevel(int level) {
			return super.boundablesAtLevel(level);
		}

		public AbstractNode getRoot() {
			return super.getRoot();
		}
	}
}
