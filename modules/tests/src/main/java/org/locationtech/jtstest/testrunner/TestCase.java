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
package org.locationtech.jtstest.testrunner;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTWriter;
import org.locationtech.jtstest.util.StringUtil;

/**
 * A set of tests for two Geometry's.
 *
 * @author jaquino
 * @created June 22, 2001
 * @version 1.7
 */
public class TestCase implements Runnable {
	private Geometry a;
	private File aWktFile;
	private Geometry b;
	private File bWktFile;
	private final int caseIndex;
	private String description;
	private boolean isRun = false;
	private final int lineNumber;
	private final TestRun testRun;
	private final List<Test> tests = new ArrayList<>();

	/**
	 * Creates a TestCase with the given description. The tests will be applied to a
	 * and b.
	 */
	public TestCase(String description, Geometry a, Geometry b, File aWktFile, File bWktFile, TestRun testRun,
			int caseIndex, int lineNumber) {
		this.description = description;
		this.a = a;
		this.b = b;
		this.aWktFile = aWktFile;
		this.bWktFile = bWktFile;
		this.testRun = testRun;
		this.caseIndex = caseIndex;
		this.lineNumber = lineNumber;
	}

	/** Adds a Test to the TestCase. */
	public void add(Test test) {
		tests.add(test);
	}

	public int getCaseIndex() {
		return caseIndex;
	}

	public String getDescription() {
		return description;
	}

	public Geometry getGeometryA() {
		return a;
	}

	public Geometry getGeometryB() {
		return b;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	/**
	 * Returns the number of tests.
	 *
	 * @return The testCount value
	 */
	public int getTestCount() {
		return tests.size();
	}

	public TestRun getTestRun() {
		return testRun;
	}

	public List<Test> getTests() {
		return Collections.unmodifiableList(tests);
	}

	public boolean isRun() {
		return isRun;
	}

	public void remove(Test test) {
		tests.remove(test);
	}

	public void run() {
		isRun = true;
		for (Test test : tests) {
			test.run();
		}
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setGeometryA(Geometry a) {
		aWktFile = null;
		this.a = a;
	}

	public void setGeometryB(Geometry b) {
		bWktFile = null;
		this.b = b;
	}

	public String toXml() {
		WKTWriter writer = new WKTWriter();
		String xml = "";
		xml += "<case>" + StringUtil.newLine;
		if (description != null && description.length() > 0) {
			xml += "  <desc>" + StringUtil.escapeHTML(description) + "</desc>" + StringUtil.newLine;
		}
		xml += xml("a", a, aWktFile, writer) + StringUtil.newLine;
		xml += xml("b", b, bWktFile, writer);
		for (Test test : tests) {
			xml += test.toXml();
		}
		xml += "</case>" + StringUtil.newLine;
		return xml;
	}

	private String xml(String id, Geometry g, File wktFile, WKTWriter writer) {
		if (g == null) {
			return "";
		}
		if (wktFile != null) {
			return "  <" + id + " file=\"" + wktFile + "\"/>";
		}
		String xml = "";
		xml += "  <" + id + ">" + StringUtil.newLine;
		xml += StringUtil.indent(writer.writeFormatted(g), 4) + StringUtil.newLine;
		xml += "  </" + id + ">" + StringUtil.newLine;
		return xml;
	}
}
