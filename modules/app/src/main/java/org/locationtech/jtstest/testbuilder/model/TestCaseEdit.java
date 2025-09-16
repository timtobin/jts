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
package org.locationtech.jtstest.testbuilder.model;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.IntersectionMatrix;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jtstest.test.TestCase;
import org.locationtech.jtstest.test.Testable;

/**
 * @version 1.7
 */
public class TestCaseEdit implements Testable {
	private static Geometry cloneGeometry(Geometry geom) {
		if (geom == null)
			return null;
		return (Geometry) geom.clone();
	}

	private Geometry[] geom = new Geometry[2];

	private String opName = "";
	private Geometry resultGeom = null;

	private Testable testable;

	public TestCaseEdit(Geometry[] geom) {
		this.testable = new TestCase();
		setGeometry(0, geom[0]);
		setGeometry(1, geom[1]);
	}

	public TestCaseEdit(Geometry[] geom, String name) {
		this.testable = new TestCase();
		setGeometry(0, geom[0]);
		setGeometry(1, geom[1]);
		testable.setName(name);
	}

	public TestCaseEdit(PrecisionModel pm) {
		TestCase testCase = new TestCase();
		testCase.setPrecisionModel(pm);
		testable = testCase;
	}

	public TestCaseEdit(TestCaseEdit tce) {
		this.testable = new TestCase();
		setGeometry(0, tce.getGeometry(0));
		setGeometry(1, tce.getGeometry(1));
	}

	public TestCaseEdit(Testable tc) throws ParseException {
		this.testable = tc;
		testable.initGeometry();
		setGeometry(0, testable.getGeometry(0));
		setGeometry(1, testable.getGeometry(1));
	}

	public String getDescription() {
		return testable.getDescription();
	}

	public Geometry[] getGeometries() {
		return new Geometry[]{testable.getGeometry(0), testable.getGeometry(1)};
	}

	public Geometry getGeometry(int i) {
		// return geom[i];
		return testable.getGeometry(i);
	}

	public IntersectionMatrix getIM() {
		runRelate();
		return testable.getIntersectionMatrix();
	}

	public IntersectionMatrix getIntersectionMatrix() {
		return testable.getIntersectionMatrix();
	}

	public String getName() {
		return testable.getName();
	}

	public String getOpName() {
		return opName;
	}

	public Geometry getResult() {
		return resultGeom;
	}

	public Testable getTestable() {
		return testable;
	}

	public String getWellKnownText(int i) {
		return testable.getWellKnownText(i);
	}

	public void initGeometry() throws ParseException {
		testable.initGeometry();
	}

	void runRelate() {
		Geometry[] geom = getGeometries();
		if (geom[0] == null || geom[1] == null) {
			return;
		}
		testable.setIntersectionMatrix(geom[0].relate(geom[1]));
	}

	public void setGeometry(int i, Geometry geom) {
		testable.setGeometry(i, geom);
	}

	public void setIntersectionMatrix(IntersectionMatrix im) {
		testable.setIntersectionMatrix(im);
	}

	public void setName(String name) {
		testable.setName(name);
	}

	public void setOpName(String name) {
		opName = name;
	}

	public void setResult(Geometry geom) {
		resultGeom = geom;
	}
}
