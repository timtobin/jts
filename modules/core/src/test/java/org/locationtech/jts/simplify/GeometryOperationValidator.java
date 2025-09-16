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

package org.locationtech.jts.simplify;

import org.junit.jupiter.api.Assertions;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTReader;

/** Runs various validation tests on a the results of a geometry operation */
public class GeometryOperationValidator {
	private static final WKTReader rdr = new WKTReader();
	private boolean expectedSameStructure = false;
	private final Geometry[] ioGeometry;
	private String wktExpected = null;

	public GeometryOperationValidator(Geometry[] ioGeometry) {
		this.ioGeometry = ioGeometry;
	}

	public boolean isAllTestsPassed() {
		try {
			test();
		} catch (Throwable e) {
			return false;
		}
		return true;
	}

	public GeometryOperationValidator setExpectedResult(String wktExpected) {
		this.wktExpected = wktExpected;
		return this;
	}

	public GeometryOperationValidator setExpectedSameStructure() {
		this.expectedSameStructure = true;
		return this;
	}

	/**
	 * Tests if the result is valid. Throws an exception if result is not valid.
	 * This allows chaining multiple tests together.
	 *
	 * @throws Exception
	 *             if the result is not valid.
	 */
	public void test() throws Exception {
		testSameStructure();
		testValid();
		testExpectedResult();
	}

	public GeometryOperationValidator testEmpty(boolean isEmpty) throws Exception {
		String failureCondition = isEmpty ? "not empty" : "empty";
		Assertions.assertEquals(ioGeometry[1].isEmpty(), isEmpty, "simplified geometry is " + failureCondition);
		return this;
	}

	private void testExpectedResult() throws Exception {
		if (wktExpected == null)
			return;
		Geometry expectedGeom = rdr.read(wktExpected);
		boolean isEqual = expectedGeom.equalsExact(ioGeometry[1]);
		if (!isEqual) {
			System.out.println("Result not expected: " + ioGeometry[1]);
		}
		Assertions.assertTrue(isEqual, "Expected result not found");
	}

	public GeometryOperationValidator testSameStructure() throws Exception {
		if (!expectedSameStructure)
			return this;
		Assertions.assertTrue(SameStructureTester.isSameStructure(ioGeometry[0], ioGeometry[1]),
				"simplified geometry has different structure than input");
		return this;
	}

	public GeometryOperationValidator testValid() throws Exception {
		Assertions.assertTrue(ioGeometry[1].isValid(), "simplified geometry is not valid");
		return this;
	}
}
