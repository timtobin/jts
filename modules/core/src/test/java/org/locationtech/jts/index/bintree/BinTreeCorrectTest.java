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
package org.locationtech.jts.index.bintree;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.util.Stopwatch;

/**
 * @version 1.7
 */
public class BinTreeCorrectTest {
	static final double MAX_EXTENT = 1000.0;
	static final double MIN_EXTENT = -1000.0;
	static final int NUM_ITEMS = 20000;

	final Bintree btree = new Bintree();
	final IntervalList intervalList = new IntervalList();

	public BinTreeCorrectTest() {
	}

	void createGrid(int nGridCells) {
		int gridSize = (int) Math.sqrt(nGridCells);
		gridSize += 1;
		double extent = MAX_EXTENT - MIN_EXTENT;
		double gridInc = extent / gridSize;
		double cellSize = 2 * gridInc;

		for (int i = 0; i < gridSize; i++) {
			double x = MIN_EXTENT + gridInc * i;
			Interval interval = new Interval(x, x + cellSize);
			btree.insert(interval, interval);
			intervalList.add(interval);
		}
	}

	void fill() {
		createGrid(NUM_ITEMS);
	}

	private List getOverlapping(List items, Interval searchInterval) {
		List result = new ArrayList();
		for (Object item : items) {
			Interval interval = (Interval) item;
			if (interval.overlaps(searchInterval))
				result.add(interval);
		}
		return result;
	}

	void queryGrid(int nGridCells, double cellSize) {
		Stopwatch sw = new Stopwatch();
		sw.start();

		int gridSize = (int) Math.sqrt(nGridCells);
		gridSize += 1;
		double extent = MAX_EXTENT - MIN_EXTENT;
		double gridInc = extent / gridSize;

		for (int i = 0; i < gridSize; i++) {
			double x = MIN_EXTENT + gridInc * i;
			Interval interval = new Interval(x, x + cellSize);
			queryTest(interval);
			// queryTime(env);
		}
		System.out.println("Time = " + sw.getTimeString());
	}

	void queryTest(Interval interval) {
		List candidateList = btree.query(interval);
		List finalList = getOverlapping(candidateList, interval);

		List eList = intervalList.query(interval);
		System.out.println(finalList.size());

		if (finalList.size() != eList.size())
			throw new RuntimeException("queries do not match");
	}

	void queryTime(Interval interval) {
		// List finalList = getOverlapping(q.query(env), env);

		List eList = intervalList.query(interval);
	}

	public void run() {
		fill();
		System.out.println("depth = " + btree.depth() + "  size = " + btree.size());
		runQueries();
	}

	void runQueries() {
		int nGridCells = 100;
		int cellSize = (int) Math.sqrt(NUM_ITEMS);
		double extent = MAX_EXTENT - MIN_EXTENT;
		double queryCellSize = 2.0 * extent / cellSize;

		queryGrid(nGridCells, queryCellSize);

		// queryGrid(200);
	}
}
