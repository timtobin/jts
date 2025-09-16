/*
 * Copyright (c) 2019 Martin Davis.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v20.html
 * and the Eclipse Distribution License is available at
 *
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.locationtech.jtstest.cmd;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.index.SpatialIndex;
import org.locationtech.jts.index.strtree.STRtree;
import org.locationtech.jts.util.Stopwatch;
import org.locationtech.jtstest.geomfunction.FilterGeometryFunction;
import org.locationtech.jtstest.geomfunction.GeometryFunction;
import org.locationtech.jtstest.geomfunction.GeometryFunctionRegistry;
import org.locationtech.jtstest.testbuilder.ui.SwingUtil;
import org.locationtech.jtstest.util.io.MultiFormatBufferedReader;
import org.locationtech.jtstest.util.io.MultiFormatFileReader;
import org.locationtech.jtstest.util.io.MultiFormatReader;

class FunctionInvoker {
	private final String[] args;
	private Geometry b;
	private final GeometryFunction func;

	public FunctionInvoker(GeometryFunction fun, String[] args) {
		this.func = fun;
		this.args = args;
	}

	private Object[] createFunctionArgs(GeometryFunction func, Geometry geomB, String arg1) {
		Class<?>[] paramTypes = func.getParameterTypes();
		Object[] paramVal = new Object[paramTypes.length];

		int iparam = 0;
		if (func.isBinary()) {
			paramVal[0] = geomB;
			iparam++;
		}
		// just handling one scalar arg for now
		if (iparam < paramVal.length) {
			paramVal[iparam] = SwingUtil.coerce(arg1, paramTypes[iparam]);
		}
		return paramVal;
	}

	public Object[] getArgs(int i) {
		String arg = args == null ? null : args[i];
		return createFunctionArgs(func, b, arg);
	}

	public GeometryFunction getFunction() {
		return func;
	}

	public int getNumInvocations() {
		if (args == null)
			return 1;
		return args.length;
	}

	public String getValue(int i) {
		if (args == null) {
			return null;
		}
		return args[i];
	}

	public boolean isBinaryGeom() {
		return func.isBinary();
	}

	public void setB(Geometry geom) {
		this.b = geom;
	}
}

class IndexedGeometry {
	private List<Integer> allIndexes = null;
	private SpatialIndex index = null;

	public IndexedGeometry(List<Geometry> geoms, boolean isIndexed) {
		if (isIndexed) {
			initIndex(geoms);
		} else {
			initList(geoms);
		}
	}

	private void initIndex(List<Geometry> geoms) {
		index = new STRtree();
		for (int i = 0; i < geoms.size(); i++) {
			Geometry comp = geoms.get(i);
			index.insert(comp.getEnvelopeInternal(), i);
		}
	}

	private void initList(List<Geometry> geoms) {
		allIndexes = new ArrayList<Integer>();
		for (int i = 0; i < geoms.size(); i++) {
			allIndexes.add(i);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Integer> query(Geometry geom) {
		if (index != null) {
			List<Integer> vals = index.query(geom.getEnvelopeInternal());
			// sort indices in ascending order for readability
			Collections.sort(vals);
			return vals;
		}
		return allIndexes;
	}
}

/**
 * Runs an operation according to supplied parameters.
 *
 * @author Martin Davis
 */
public class JTSOpRunner {

	public static final String ERR_FILE_NOT_FOUND = "File not found";
	public static final String ERR_FUNCTION_ERR = "Error executing function";
	public static final String ERR_FUNCTION_NOT_FOUND = "Function not found";
	public static final String ERR_INPUT = "Unable to read input";
	public static final String ERR_INVALID_RESULT = "Result is invalid";
	public static final String ERR_PARSE_GEOM = "Unable to parse geometry";
	public static final String ERR_REQUIRED_A = "Geometry A may be required";
	public static final String ERR_REQUIRED_B = "Geometry B is required";
	public static final String ERR_WRONG_ARG_COUNT = "Function arguments and parameters do not match";

	private static final String SYM_A = "A";
	private static final String SYM_B = "B";

	private static List<Geometry> collect(List<Geometry> geoms, GeometryFactory factory) {
		GeometryCollection geomColl = factory.createGeometryCollection(GeometryFactory.toGeometryArray(geoms));
		return toList(geomColl);
	}

	private static void explode(Geometry geom, List<Geometry> geomsEx) {
		for (int i = 0; i < geom.getNumGeometries(); i++) {
			geomsEx.add(geom.getGeometryN(i));
		}
	}

	private static List<Geometry> explode(List<Geometry> geoms) {
		if (geoms == null)
			return null;
		List<Geometry> geomsEx = new ArrayList<Geometry>();
		for (Geometry geom : geoms) {
			explode(geom, geomsEx);
		}
		return geomsEx;
	}

	private static String fileInfo(String filename, int limit, int offset) {
		if (filename == null)
			return null;
		String info = filename;
		if (limit > OpParams.LIMIT_DEFAULT)
			info += " LIMIT " + limit;
		if (offset > OpParams.OFFSET_DEFAULT)
			info += " OFFSET " + offset;
		return info;
	}

	public static boolean isCustomSRID(int srid) {
		return srid > 0;
	}

	private static String limitLength(String s, int n) {
		if (s.length() <= n)
			return s;
		return s.substring(0, n) + "...";
	}

	private static String opSummary(GeometryFunction func, String arg) {
		StringBuilder sb = new StringBuilder();
		sb.append("Op: " + func.getCategory() + "." + func.getName());
		if (arg != null) {
			sb.append(" " + arg);
		}
		return sb.toString();
	}

	private static List<Geometry> toList(Geometry geometry) {
		List<Geometry> geoms = new ArrayList<Geometry>();
		geoms.add(geometry);
		return geoms;
	}

	private boolean captureGeometry = false;

	private GeometryFunctionRegistry funcRegistry;
	private List<Geometry> geomA;
	private List<Geometry> geomB;
	private GeometryFactory geomFactory = new GeometryFactory();
	private IndexedGeometry geomIndexB;
	private GeometryOutput geomOut;
	private String hdrSave;
	private boolean isTime;

	private boolean isVerbose = false;

	private int opCount = 0;

	private CommandOutput out;

	private OpParams param;

	private final List<Geometry> resultGeoms = new ArrayList<Geometry>();

	private InputStream stdIn = System.in;

	private String symGeom2 = SYM_B;

	private long totalTime;

	public JTSOpRunner() {
	}

	public void captureOutput() {
		out = new CommandOutput(true);
		geomOut = new GeometryOutput(out);
	}

	public void captureResult() {
		captureGeometry = true;
	}

	private void checkFunctionArgs(GeometryFunction func, List<Geometry> geomB, String[] argList) {
		Class<?>[] paramTypes = func.getParameterTypes();
		int nParam = paramTypes.length;

		/*
		 * // disable this check for now, since it does not handle functions where B is
		 * optional if (func.isBinary() && geomB == null) throw new
		 * CommandError(ERR_REQUIRED_B);
		 */

		/*
		 * check count of supplied args. Assumes B has been checked.
		 */
		int argCount = 0;
		if (func.isBinary()
		// disable B check for now
		// && geomB != null
		) {
			argCount++;
		}
		if (argList != null)
			argCount++;
		if (nParam != argCount) {
			throw new CommandError(ERR_WRONG_ARG_COUNT, func.getName());
		}
	}

	private GeometryFactory createGeometryFactory(int srid) {
		if (srid > 0) {
			return new GeometryFactory(new PrecisionModel(), srid);
		}
		return new GeometryFactory();
	}

	private String errorMsg(Throwable ex) {
		String msg = "ERROR excuting function: " + ex.getMessage() + "\n";
		msg += toStackString(ex);
		if (ex.getCause() != null) {
			msg += "Caused by:\n";
			msg += toStackString(ex.getCause());
		}
		return msg;
	}

	void execute(OpParams param) {
		this.param = param;

		// -- init output to file or console
		if (out == null) {
			if (param.outputFile != null) {
				out = new CommandOutput(param.outputFile);
			} else {
				out = new CommandOutput();
			}
			geomOut = new GeometryOutput(out);
		}

		geomFactory = createGeometryFactory(param.srid);
		geomA = null;
		geomB = null;

		loadGeometry();
		if (geomA != null) {
			printGeometrySummary("A", geomA, fileInfo(param.fileA, param.limitA, param.offsetA));
		}
		if (geomB != null) {
			printGeometrySummary("B", geomB, fileInfo(param.fileB, param.limitB, param.offsetB));
		}

		// --- If -ab aa specified, use A for B
		if (param.isGeomAB) {
			geomB = geomA;
			symGeom2 = SYM_A;
		}

		// index B if present and requested
		if (geomB != null) {
			geomIndexB = new IndexedGeometry(geomB, param.isIndexed);
		}

		if (param.operation != null) {
			executeFunction();
		} else {
			// no op specified, so just output A (allows format conversion)
			outputList(geomA, param.format);
		}
	}

	private void executeFunction() {
		GeometryFunction baseFun = getFunction(param.operation);
		GeometryFunction func = baseFun;
		if (param.isFilter) {
			func = new FilterGeometryFunction(func, param.filterOp, param.filterVal);
		}

		if (func == null) {
			throw new CommandError(ERR_FUNCTION_NOT_FOUND, param.operation);
		}
		String[] argList = param.argList;
		checkFunctionArgs(func, geomB, argList);

		FunctionInvoker fun = new FunctionInvoker(func, argList);
		executeFunctionOverA(fun);

		if (isVerbose || isTime) {
			out.logln("\nOperation " + func.getCategory() + "." + func.getName() + ": " + opCount
					+ " invocations - Total Time: " + Stopwatch.getTimeString(totalTime));
		}
	}

	private void executeFunction(Geometry geomA, FunctionInvoker fun, String hdr) {
		// Set saved hdr to blank in case verbose is on
		hdrSave = "";
		// printlnInfo(hdr);
		for (int i = 0; i < fun.getNumInvocations(); i++) {
			Object[] funArgs = fun.getArgs(i);
			GeometryFunction func = fun.getFunction();
			String arg = fun.getValue(i);

			String opDesc = "[" + (opCount + 1) + "] -- " + opSummary(func, arg) + " : ";
			if (isVerbose) {
				out.logln(opDesc + hdr);
			} else {
				hdrSave = hdr + "\n" + opDesc;
			}
			executeFunctionRepeat(geomA, func, funArgs);
		}
	}

	private Object executeFunctionOnce(Geometry geomA, GeometryFunction func, Object[] funArgs) {
		Stopwatch timer = new Stopwatch();
		Object result = null;
		try {
			result = func.invoke(geomA, funArgs);
		} catch (NullPointerException ex) {
			if (geomA == null)
				throw new CommandError(ERR_REQUIRED_A, param.operation);
			// if A is present then must be something else
			logError(errorMsg(ex));
		} catch (Exception ex) {
			logError(errorMsg(ex));
		} finally {
			timer.stop();
		}
		totalTime += timer.getTime();
		printlnInfo("Time: " + timer.getTimeString());
		opCount++;

		if (result instanceof Geometry geometry) {
			printGeometrySummary("Result", geometry);
		}
		if (param.validate) {
			validate(result);
		}
		if (!param.isQuiet) {
			outputResult(result, param.isExplode, param.format);
		}
		return result;
	}

	private void executeFunctionOverA(FunctionInvoker fun) {
		int numGeom = 1;
		if (geomA != null) {
			numGeom = geomA.size();
		}
		String header = "";
		for (int i = 0; i < numGeom; i++) {
			Geometry comp = geomA == null ? null : geomA.get(i);
			String hdr = GeometryOutput.writeGeometrySummary(SYM_A + "[" + i + "]", comp);
			if (geomB == null) {
				executeFunction(comp, fun, hdr);
			} else {
				executeFunctionOverB(comp, fun, hdr);
			}
		}
	}

	private void executeFunctionOverB(Geometry geomA, FunctionInvoker fun, String header) {
		// spread over B
		List<Integer> targetB = geomIndexB.query(geomA);
		for (int index : targetB) {
			Geometry gb = geomB.get(index);
			String hdr = header + ", " + GeometryOutput.writeGeometrySummary(symGeom2 + "[" + index + "]", gb);
			fun.setB(gb);
			executeFunction(geomA, fun, hdr);
		}
	}

	private Object executeFunctionRepeat(Geometry geomA, GeometryFunction func, Object[] funArgs) {
		Object result = null;
		for (int i = 0; i < param.repeat; i++) {
			if (param.repeat > 1) {
				printlnInfo("Run: " + (i + 1) + " of " + param.repeat + "   ");
			}
			result = executeFunctionOnce(geomA, func, funArgs);
		}
		return result;
	}

	private GeometryFunction getFunction(String operation) {
		// default category is Geometry
		String category = "Geometry";
		String name = operation;
		String[] opCatName = operation.split("\\.");
		if (opCatName.length == 2) {
			category = opCatName[0];
			name = opCatName[1];
		}
		return funcRegistry.find(category, name);
	}

	public String getOutput() {
		return out.getOutput();
	}

	public List<Geometry> getResultGeometry() {
		return resultGeoms;
	}

	private void loadGeometry() {
		geomA = readGeometry("A", param.fileA, param.geomA, param.limitA, param.offsetA);
		geomB = readGeometry("B", param.fileB, param.geomB, param.limitB, param.offsetB);

		if (param.eachA) {
			geomA = explode(geomA);
		}
		if (param.eachB) {
			geomB = explode(geomB);
		}
		if (param.isCollect) {
			geomA = collect(geomA, geomFactory);
		}
	}

	private void loadGeometryAB() {
		List<Geometry> geomAB = readGeometry("AB", param.fileA, param.geomA, OpParams.LIMIT_DEFAULT,
				OpParams.OFFSET_DEFAULT);
		if (geomAB.size() < 2) {
			throw new CommandError(ERR_REQUIRED_B);
		}
		geomA = toList(geomAB.getFirst());
		geomB = toList(geomAB.get(1));
	}

	private void logError(String msg) {
		// this will be blank if already printed in verbose mode
		out.logln(hdrSave);
		out.logln(msg);
	}

	private void outputList(List<Geometry> geoms, String outputFormat) {
		if (geoms == null)
			return;
		if (outputFormat == null)
			return;

		for (Geometry geom : geoms) {
			outputResult(geom, param.isExplode, outputFormat);
		}
	}

	private void outputResult(Object result, boolean isExplode, String outputFormat) {
		if (result == null)
			return;
		if (outputFormat == null)
			return;

		if (!(result instanceof Geometry geom)) {
			out.println(result);
			return;
		}
		if (isExplode && geom instanceof GeometryCollection) {
			for (int i = 0; i < geom.getNumGeometries(); i++) {
				printGeometry(geom.getGeometryN(i), param.srid, outputFormat);
			}
		} else {
			printGeometry(geom, param.srid, outputFormat);
		}
	}

	private void printGeometry(Geometry geom, int srid, String outputFormat) {
		if (geom == null)
			return;
		if (outputFormat == null)
			return;

		if (captureGeometry) {
			resultGeoms.add(geom);
		}
		geomOut.printGeometry(geom, srid, outputFormat);
	}

	private void printGeometrySummary(String label, Geometry geom) {
		// short-circuit to avoid cost
		if (!isVerbose)
			return;
		printlnInfo(GeometryOutput.writeGeometrySummary(label, geom));
	}

	private void printGeometrySummary(String label, List<Geometry> geom, String source) {
		// short-circuit to avoid cost
		if (!isVerbose)
			return;

		String srcname = "";
		if (source != null)
			srcname = " -- " + source;
		printlnInfo(GeometryOutput.writeGeometrySummary(label, geom) + srcname);
	}

	private void printlnInfo(String s) {
		if (!isVerbose)
			return;
		out.logln(s);
	}

	/**
	 * Reads a geometry from a literal or a filename. If neither are provided this
	 * geometry is not present.
	 *
	 * @param geomLabel
	 *            label for geometry being read
	 * @param filename
	 *            the filename to read from, if present, or <code>null</code>
	 * @param geom
	 *            the geometry literal, if present, or <code>null</code>
	 * @param geomA2
	 * @return the geometry read, or null
	 * @throws Exception
	 */
	private List<Geometry> readGeometry(String geomLabel, String filename, String geomStr, int limit, int offset) {
		String geomDesc = " " + geomLabel + " ";
		if (geomStr != null) {
			// read a literal from the argument
			MultiFormatReader rdr = new MultiFormatReader(geomFactory);
			try {
				Geometry g = rdr.read(geomStr);
				return toList(g);
			} catch (org.locationtech.jts.io.ParseException ex) {
				throw new CommandError(ERR_PARSE_GEOM + geomDesc + " - " + ex.getMessage());
			} catch (Exception e) {
				throw new CommandError(ERR_PARSE_GEOM + geomDesc, limitLength(geomStr, 50));
			}
		}
		// no parameter supplied
		if (filename == null)
			return null;

		// must be a filename
		if (filename.equalsIgnoreCase(CommandOptions.SOURCE_STDIN)) {
			return readStdin(limit, offset);
		}

		try {
			return MultiFormatFileReader.read(filename, limit, offset, geomFactory);
		} catch (FileNotFoundException ex) {
			throw new CommandError(ERR_FILE_NOT_FOUND, filename);
		} catch (Exception e) {
			throw new CommandError(ERR_PARSE_GEOM + geomDesc, filename);
		}
	}

	private List<Geometry> readStdin(int limit, int offset) {
		try {
			return MultiFormatBufferedReader.read(new InputStreamReader(stdIn), limit, offset, geomFactory);
		} catch (org.locationtech.jts.io.ParseException ex) {
			throw new CommandError(ERR_PARSE_GEOM + " - " + ex.getMessage());
		} catch (Exception ex) {
			throw new CommandError(ERR_INPUT);
		}
	}

	public void replaceStdIn(InputStream inStream) {
		stdIn = inStream;
	}

	public void setRegistry(GeometryFunctionRegistry funcRegistry) {
		this.funcRegistry = funcRegistry;
	}

	public void setTime(boolean isTime) {
		this.isTime = isTime;
	}

	public void setVerbose(boolean isVerbose) {
		this.isVerbose = isVerbose;
	}

	private String toStackString(Throwable ex) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		String stack = sw.toString();
		return stack;
	}

	private void validate(Object result) {
		if (!(result instanceof Geometry resGeom))
			return;

		// TODO: print invalidity reason
		if (!resGeom.isValid()) {
			logError("Result is invalid");
		}
	}

	static class OpParams {
		static final int LIMIT_DEFAULT = -1;
		static final int OFFSET_DEFAULT = 0;

		/**
		 * Tests whether an input geometry has been supplied.
		 *
		 * @param file
		 * @param geom
		 * @return true if an input geometry is present
		 */
		static boolean isGeometryInput(String file, String geom) {
			return file != null || geom != null;
		}

		public String[] argList;
		public boolean eachA = false;
		public boolean eachAA = false;

		public boolean eachB = false;
		public String fileA;
		public String fileB;
		public int filterOp;

		public double filterVal = 0;
		public String format = null;
		public String geomB;
		public boolean isCollect = false;
		public boolean isExplode = false;
		public boolean isFilter = false;
		public boolean isGeomAB = false;
		public boolean isIndexed = false;
		public boolean isQuiet = false;
		public int limitA = LIMIT_DEFAULT;
		public int limitB = LIMIT_DEFAULT;
		public int offsetA = OFFSET_DEFAULT;

		public int offsetB = OFFSET_DEFAULT;
		public String outputFile;
		public Integer repeat;

		public int srid;

		public boolean validate = false;
		String geomA;

		String operation;
	}
}
