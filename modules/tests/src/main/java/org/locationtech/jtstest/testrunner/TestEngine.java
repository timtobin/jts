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
import java.util.Date;
import java.util.List;

/**
 * Converts test File's to TestCase's and runs them.
 *
 * @version 1.7
 */
public class TestEngine implements Runnable {
	private Date end = null;
	private boolean running = false;
	private Date start = null;
	// default is to run all tests
	private int testCaseIndexToRun = -1;
	private List<File> testFiles;

	private TestReader testReader = new TestReader();
	private List<TestRun> testRuns = new ArrayList<TestRun>();

	/** Creates a TestEngine. */
	public TestEngine() {
	}

	public void clearParsingProblems() {
		testReader.clearParsingProblems();
	}

	/** Creates TestRun's, one for each test File. */
	private List<TestRun> createTestRunsFromFiles() {
		List<TestRun> testRuns = new ArrayList<TestRun>();
		int runIndex = 0;
		for (File testFile : testFiles) {
			runIndex++;
			System.out.println("Reading test file " + testFile.getAbsolutePath());
			TestRun testRun = testReader.createTestRun(testFile, runIndex);
			if (testRun != null) {
				testRuns.add(testRun);
			}
		}
		return testRuns;
	}

	public Date getEnd() {
		return end;
	}

	public int getExceptionCount() {
		int exceptionCount = 0;
		for (Test test : getTests()) {
			if (test.getException() != null) {
				exceptionCount++;
			}
		}
		return exceptionCount;
	}

	public int getFailedCount() {
		int failedCount = 0;
		for (Test test : getTests()) {
			if ((test.getException() == null) && (!test.isPassed())) {
				failedCount++;
			}
		}
		return failedCount;
	}

	public int getParseExceptionCount() {
		return testReader.getParsingProblems().size();
	}

	public List getParsingProblems() {
		return Collections.unmodifiableList(testReader.getParsingProblems());
	}

	public int getPassedCount() {
		int passedCount = 0;
		for (Test test : getTests()) {
			if (test.isPassed()) {
				passedCount++;
			}
		}
		return passedCount;
	}

	public Date getStart() {
		return start;
	}

	public int getTestCaseCount() {
		int count = 0;
		for (TestRun testRun : testRuns) {
			count += testRun.getTestCases().size();
		}
		return count;
	}

	/** Returns the total number of tests. */
	public int getTestCount() {
		int count = 0;
		for (TestRun testRun : testRuns) {
			count += testRun.getTestCount();
		}
		return count;
	}

	public List<TestRun> getTestRuns() {
		return testRuns;
	}

	private List<Test> getTests() {
		List<Test> tests = new ArrayList<Test>();
		for (TestRun testRun : testRuns) {
			tests.addAll(getTests(testRun));
		}
		return tests;
	}

	private List<Test> getTests(TestRun testRun) {
		List<Test> tests = new ArrayList<Test>();
		for (TestCase testCase : testRun.getTestCases()) {
			tests.addAll(testCase.getTests());
		}
		return tests;
	}

	/** Returns whether the TestEngine is running any TestCase's. */
	public boolean isRunning() {
		return running;
	}

	public void run() {
		running = true;
		start = new Date();
		clearParsingProblems();
		testRuns = createTestRunsFromFiles();
		System.out.println("Running tests...");
		for (TestRun testRun : testRuns) {
			if (testCaseIndexToRun >= 0) {
				testRun.setTestCaseIndexToRun(testCaseIndexToRun);
			}
			testRun.run();
		}
		end = new Date();
		running = false;
	}

	public void setTestCaseIndexToRun(int testCaseIndexToRun) {
		this.testCaseIndexToRun = testCaseIndexToRun;
	}

	/** Sets the File's that contain the tests. */
	public void setTestFiles(List<File> testFiles) {
		this.testFiles = testFiles;
	}
}
