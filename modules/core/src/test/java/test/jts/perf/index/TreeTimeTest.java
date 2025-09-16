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
package test.jts.perf.index;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.index.hprtree.HPRtree;
import org.locationtech.jts.index.quadtree.Quadtree;
import org.locationtech.jts.index.strtree.STRtree;

/**
 * @version 1.7
 */
public class TreeTimeTest {
	public static final int NUM_ITEMS = 100000;

	public static void main(String[] args) throws Exception {
		int n = NUM_ITEMS;
		TreeTimeTest test = new TreeTimeTest();
		// List items = IndexTester.createGridItems(n);
		List items = IndexTester.createRandomBoxes(n);
		List queries = IndexTester.createRandomBoxes(n);

		System.out.println("----------------------------------------------");
		System.out.println("Dummy run to ensure classes are loaded before real run");
		System.out.println("----------------------------------------------");
		test.run(items, queries);
		System.out.println("----------------------------------------------");
		System.out.println("Real run");
		System.out.println("----------------------------------------------");
		test.run(items, queries);
	}

	public TreeTimeTest() {
	}

	public IndexTester.IndexResult run(Index index, List items, List queries) {
		return new IndexTester(index).testAll(items, queries);
	}

	public List run(List items, List queries) throws Exception {
		ArrayList indexResults = new ArrayList();
		System.out.println("# items = " + items.size());
		indexResults.add(run(new HPRtreeIndex(16), items, queries));
		indexResults.add(run(new STRtreeIndex(4), items, queries));
		// indexResults.add(run(new QuadtreeIndex(), items));
		// indexResults.add(run(new QXtreeIndex(), n));
		// indexResults.add(run(new EnvelopeListIndex(), n));
		return indexResults;
	}

	class EnvelopeListIndex implements Index {
		final EnvelopeList index = new EnvelopeList();

		public void finishInserting() {
		}

		public void insert(Envelope itemEnv, Object item) {
			index.add(itemEnv);
		}

		public List query(Envelope searchEnv) {
			return index.query(searchEnv);
		}

		public String toString() {
			return "Env";
		}
	}

	class HPRtreeIndex implements Index {
		private final int nodeCapacity;

		final HPRtree index;

		public HPRtreeIndex(int nodeCapacity) {
			this.nodeCapacity = nodeCapacity;
			index = new HPRtree(nodeCapacity);
		}

		public void finishInserting() {
			index.build();
		}

		public void insert(Envelope itemEnv, Object item) {
			index.insert(itemEnv, item);
		}

		public List query(Envelope searchEnv) {
			return index.query(searchEnv);
		}

		public String toString() {
			return "HPR[M=" + nodeCapacity + "]";
		}
	}

	class QuadtreeIndex implements Index {
		final Quadtree index = new Quadtree();

		public void finishInserting() {
		}

		public void insert(Envelope itemEnv, Object item) {
			index.insert(itemEnv, item);
		}

		public List query(Envelope searchEnv) {
			return index.query(searchEnv);
		}

		public String toString() {
			return "Quad";
		}
	}

	class STRtreeIndex implements Index {
		final STRtree index;

		// public String toString() { return "" + index.getNodeCapacity() + ""; }
		public STRtreeIndex(int nodeCapacity) {
			index = new STRtree(nodeCapacity);
		}

		public void finishInserting() {
			index.build();
		}

		public void insert(Envelope itemEnv, Object item) {
			index.insert(itemEnv, item);
		}

		public List query(Envelope searchEnv) {
			return index.query(searchEnv);
		}

		public String toString() {
			return "STR[M=" + index.getNodeCapacity() + "]";
		}
	}
}
