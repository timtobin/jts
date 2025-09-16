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

package test.jts;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.impl.CoordinateArraySequenceFactory;
import org.locationtech.jts.geom.impl.PackedCoordinateSequenceFactory;
import org.locationtech.jts.io.Ordinate;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;

/**
 * A base class for Geometry tests which provides various utility methods.
 *
 * @author mbdavis
 */
public abstract class GeometryTestCase {

	private static final String CHECK_EQUAL_FAIL = "FAIL - Expected = %s -- Actual = %s\n";
	private static final String CHECK_EQUAL_FAIL_MSG = "FAIL - %s: Expected = %s -- Actual = %s\n";
	private static final String CHECK_NO_AlIAS_FAIL = "FAIL - geometries have aliased coordinates\n";

	/**
	 * Gets a {@link CoordinateSequenceFactory} that can create sequences for
	 * ordinates defined in the provided bit-pattern.
	 *
	 * @param ordinateFlags
	 *            a bit-pattern of ordinates
	 * @return a {@code CoordinateSequenceFactory}
	 */
	public static CoordinateSequenceFactory getCSFactory(EnumSet<Ordinate> ordinateFlags) {
		if (ordinateFlags.contains(Ordinate.M))
			return PackedCoordinateSequenceFactory.DOUBLE_FACTORY;

		return CoordinateArraySequenceFactory.instance();
	}

	/**
	 * Gets a {@link WKTReader} to read geometries from WKT with expected ordinates.
	 *
	 * @param ordinateFlags
	 *            a set of expected ordinates
	 * @return a {@code WKTReader}
	 */
	public static WKTReader getWKTReader(EnumSet<Ordinate> ordinateFlags) {
		return getWKTReader(ordinateFlags, new PrecisionModel());
	}

	/**
	 * Gets a {@link WKTReader} to read geometries from WKT with expected ordinates.
	 *
	 * @param ordinateFlags
	 *            a set of expected ordinates
	 * @param precisionModel
	 *            a precision model
	 * @return a {@code WKTReader}
	 */
	public static WKTReader getWKTReader(EnumSet<Ordinate> ordinateFlags, PrecisionModel precisionModel) {

		WKTReader result;

		if (!ordinateFlags.contains(Ordinate.X))
			ordinateFlags.add(Ordinate.X);
		if (!ordinateFlags.contains(Ordinate.Y))
			ordinateFlags.add(Ordinate.Y);

		if (ordinateFlags.size() == 2) {
			result = new WKTReader(new GeometryFactory(precisionModel, 0, CoordinateArraySequenceFactory.instance()));
			result.setIsOldJtsCoordinateSyntaxAllowed(false);
		} else if (ordinateFlags.contains(Ordinate.Z))
			result = new WKTReader(new GeometryFactory(precisionModel, 0, CoordinateArraySequenceFactory.instance()));
		else if (ordinateFlags.contains(Ordinate.M)) {
			result = new WKTReader(
					new GeometryFactory(precisionModel, 0, PackedCoordinateSequenceFactory.DOUBLE_FACTORY));
			result.setIsOldJtsCoordinateSyntaxAllowed(false);
		} else
			result = new WKTReader(
					new GeometryFactory(precisionModel, 0, PackedCoordinateSequenceFactory.DOUBLE_FACTORY));

		return result;
	}

	/**
	 * Gets a {@link WKTReader} to read geometries from WKT with expected ordinates.
	 *
	 * @param ordinateFlags
	 *            a set of expected ordinates
	 * @param scale
	 *            a scale value to create a {@link PrecisionModel}
	 * @return a {@code WKTReader}
	 */
	public static WKTReader getWKTReader(EnumSet<Ordinate> ordinateFlags, double scale) {
		return getWKTReader(ordinateFlags, new PrecisionModel(scale));
	}

	/**
	 * Tests two {@link CoordinateSequence}s for equality. The following items are
	 * checked:
	 *
	 * <ul>
	 * <li>size
	 * <li>dimension
	 * <li>ordinate values
	 * </ul>
	 *
	 * @param seq1
	 *            a sequence
	 * @param seq2
	 *            another sequence
	 * @return {@code true} if both sequences are equal
	 */
	public static boolean isEqual(CoordinateSequence seq1, CoordinateSequence seq2) {
		return isEqualTol(seq1, seq2, 0d);
	}

	/**
	 * Tests two {@link CoordinateSequence}s for equality. The following items are
	 * checked:
	 *
	 * <ul>
	 * <li>size
	 * <li>dimension up to {@code dimension}
	 * <li>ordinate values with {@code tolerance}
	 * </ul>
	 *
	 * @param seq1
	 *            a sequence
	 * @param seq2
	 *            another sequence
	 * @return {@code true} if both sequences are equal
	 */
	public static boolean isEqual(CoordinateSequence seq1, CoordinateSequence seq2, int dimension, double tolerance) {
		if (seq1 != null && seq2 == null)
			return false;
		if (seq1 == null && seq2 != null)
			return false;

		if (seq1.size() != seq2.size())
			return false;

		if (seq1.getDimension() < dimension)
			throw new IllegalArgumentException("dimension too high for seq1");
		if (seq2.getDimension() < dimension)
			throw new IllegalArgumentException("dimension too high for seq2");

		for (int i = 0; i < seq1.size(); i++) {
			for (int j = 0; j < dimension; j++) {
				double val1 = seq1.getOrdinate(i, j);
				double val2 = seq2.getOrdinate(i, j);
				if (Double.isNaN(val1) || Double.isNaN(val2)) {
					return Double.isNaN(val1) && Double.isNaN(val2);
				} else if (Math.abs(val1 - val2) > tolerance)
					return false;
			}
		}

		return true;
	}

	/**
	 * Tests two {@link CoordinateSequence}s for equality. The following items are
	 * checked:
	 *
	 * <ul>
	 * <li>size
	 * <li>dimension up to {@code dimension}
	 * <li>ordinate values
	 * </ul>
	 *
	 * @param seq1
	 *            a sequence
	 * @param seq2
	 *            another sequence
	 * @return {@code true} if both sequences are equal
	 */
	public static boolean isEqualDim(CoordinateSequence seq1, CoordinateSequence seq2, int dimension) {
		return isEqual(seq1, seq2, dimension, 0d);
	}

	/**
	 * Tests two {@link CoordinateSequence}s for equality. The following items are
	 * checked:
	 *
	 * <ul>
	 * <li>size
	 * <li>dimension
	 * <li>ordinate values with {@code tolerance}
	 * </ul>
	 *
	 * @param seq1
	 *            a sequence
	 * @param seq2
	 *            another sequence
	 * @return {@code true} if both sequences are equal
	 */
	public static boolean isEqualTol(CoordinateSequence seq1, CoordinateSequence seq2, double tolerance) {
		if (seq1.getDimension() != seq2.getDimension())
			return false;
		return isEqual(seq1, seq2, seq1.getDimension(), tolerance);
	}

	/**
	 * Reads a {@link Geometry} from a WKT string using a custom
	 * {@link GeometryFactory}.
	 *
	 * @param geomFactory
	 *            the custom factory to use
	 * @param wkt
	 *            the WKT string
	 * @return the geometry read
	 */
	protected static Geometry read(GeometryFactory geomFactory, String wkt) {
		WKTReader reader = new WKTReader(geomFactory);
		try {
			return reader.read(wkt);
		} catch (ParseException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public static Geometry read(WKTReader reader, String wkt) {
		try {
			return reader.read(wkt);
		} catch (ParseException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public static List readList(WKTReader reader, String[] wkt) {
		ArrayList geometries = new ArrayList(wkt.length);
		for (String s : wkt) {
			geometries.add(read(reader, s));
		}
		return geometries;
	}

	final GeometryFactory geomFactory;

	final WKTReader readerWKT;

	final WKTWriter writerZ = new WKTWriter(3);

	protected GeometryTestCase() {
		this(CoordinateArraySequenceFactory.instance());
	}

	protected GeometryTestCase(CoordinateSequenceFactory coordinateSequenceFactory) {
		geomFactory = new GeometryFactory(coordinateSequenceFactory);
		readerWKT = new WKTReader(geomFactory);
	}

	protected void checkEqual(Collection expected, Collection actual) {
		checkEqual(toGeometryCollection(expected), toGeometryCollection(actual));
	}

	/**
	 * Checks that the normalized values of the expected and actual geometries are
	 * exactly equal.
	 *
	 * @param expected
	 *            the expected value
	 * @param actual
	 *            the actual value
	 */
	protected void checkEqual(Geometry expected, Geometry actual) {
		checkEqual("", expected, actual);
	}

	protected void checkEqual(Geometry expected, Geometry actual, double tolerance) {
		Geometry actualNorm = actual.norm();
		Geometry expectedNorm = expected.norm();
		boolean equal = actualNorm.equalsExact(expectedNorm, tolerance);
		if (!equal) {
			System.out.format(CHECK_EQUAL_FAIL, expectedNorm, actualNorm);
		}
		assertTrue(equal);
	}

	protected void checkEqual(Geometry[] expected, Geometry[] actual) {
		assertEquals(expected.length, actual.length, "Array length");
		for (int i = 0; i < expected.length; i++) {
			checkEqual("element " + i, expected[i], actual[i]);
		}
	}

	/**
	 * Checks that the normalized values of the expected and actual geometries are
	 * exactly equal.
	 *
	 * @param expected
	 *            the expected value
	 * @param actual
	 *            the actual value
	 */
	protected void checkEqual(String msg, Geometry expected, Geometry actual) {
		Geometry actualNorm = actual == null ? null : actual.norm();
		Geometry expectedNorm = expected == null ? null : expected.norm();
		boolean equal;
		if (actualNorm == null || expectedNorm == null) {
			equal = actualNorm == null && expectedNorm == null;
		} else {
			equal = actualNorm.equalsExact(expectedNorm);
		}
		if (!equal) {
			System.out.format(CHECK_EQUAL_FAIL_MSG, msg, expectedNorm, actualNorm);
		}
		assertTrue(equal);
	}

	/**
	 * Checks that the values of the expected and actual geometries are exactly
	 * equal.
	 *
	 * @param expected
	 *            the expected value
	 * @param actual
	 *            the actual value
	 */
	protected void checkEqualExact(Geometry expected, Geometry actual) {
		boolean equal = actual.equalsExact(expected);
		if (!equal) {
			System.out.format(CHECK_EQUAL_FAIL, expected, actual);
		}
		assertTrue(equal);
	}

	protected void checkEqualXY(Coordinate expected, Coordinate actual) {
		assertEquals(expected.getX(), actual.getX(), "Coordinate X");
		assertEquals(expected.getY(), actual.getY(), "Coordinate Y");
	}

	protected void checkEqualXY(Coordinate expected, Coordinate actual, double tolerance) {
		assertEquals(expected.getX(), actual.getX(), tolerance, "Coordinate X");
		assertEquals(expected.getY(), actual.getY(), tolerance, "Coordinate Y");
	}

	protected void checkEqualXY(String message, Coordinate expected, Coordinate actual) {
		assertEquals(expected.getX(), actual.getX(), message + " X");
		assertEquals(expected.getY(), actual.getY(), message + " Y");
	}

	protected void checkEqualXY(String message, Coordinate expected, Coordinate actual, double tolerance) {
		assertEquals(expected.getX(), actual.getX(), tolerance, message + " X");
		assertEquals(expected.getY(), actual.getY(), tolerance, message + " Y");
	}

	protected void checkEqualXYZ(Coordinate expected, Coordinate actual) {
		assertEquals(expected.getX(), actual.getX(), "Coordinate X");
		assertEquals(expected.getY(), actual.getY(), "Coordinate Y");
		assertEquals(expected.getZ(), actual.getZ(), "Coordinate Z");
	}

	protected void checkEqualXYZ(Geometry expected, Geometry actual) {
		Geometry actualNorm = actual.norm();
		Geometry expectedNorm = expected.norm();
		boolean equal = equalsExactMultipleDimension(actualNorm, expectedNorm, 3);
		if (!equal) {
			System.out.format(CHECK_EQUAL_FAIL, writerZ.write(expectedNorm), writerZ.write(actualNorm));
		}
		assertTrue(equal);
	}

	protected void checkEqualXYZM(Geometry expected, Geometry actual) {
		Geometry actualNorm = actual.norm();
		Geometry expectedNorm = expected.norm();
		boolean equal = equalsExactMultipleDimension(actualNorm, expectedNorm, 4);
		if (!equal) {
			System.out.format(CHECK_EQUAL_FAIL, writerZ.write(expectedNorm), writerZ.write(actualNorm));
		}
		assertTrue(equal);
	}

	protected void checkNoAlias(Geometry geom, Geometry geom2) {
		Geometry geom2Copy = geom2.copy();
		geom.apply((CoordinateFilter) coord -> coord.x = coord.x + 1);
		boolean equal = geom2.equalsExact(geom2Copy);
		if (!equal) {
			System.out.println(CHECK_NO_AlIAS_FAIL);
			fail();
		}
	}

	protected void checkValid(Geometry geom) {
		assertTrue(geom.isValid());
	}

	private boolean equalsExactMultipleDimension(Geometry a, Geometry b, int dimension) {
		if (a.getClass() != b.getClass())
			return false;
		if (a.getNumGeometries() != b.getNumGeometries())
			return false;
		switch (a) {
			case Point point -> {
				return isEqualDim(point.getCoordinateSequence(), ((Point) b).getCoordinateSequence(), dimension);
			}
			case LineString string -> {
				return isEqualDim(string.getCoordinateSequence(), ((LineString) b).getCoordinateSequence(), dimension);
			}
			case Polygon polygon -> {
				return equalsExactMultipleDimensionPolygon(polygon, (Polygon) b, dimension);
			}
			case GeometryCollection geometryCollection -> {
				for (int i = 0; i < a.getNumGeometries(); i++) {
					if (!equalsExactMultipleDimension(a.getGeometryN(i), b.getGeometryN(i), dimension))
						return false;
				}
				return true;
			}
			default -> {
			}
		}
		return false;
	}

	private boolean equalsExactMultipleDimensionPolygon(Polygon a, Polygon b, int dimension) {
		LinearRing aShell = a.getExteriorRing();
		LinearRing bShell = b.getExteriorRing();
		if (!isEqualDim(aShell.getCoordinateSequence(), bShell.getCoordinateSequence(), dimension))
			return false;
		if (a.getNumInteriorRing() != b.getNumInteriorRing())
			return false;
		for (int i = 0; i < a.getNumInteriorRing(); i++) {
			LinearRing aHole = a.getInteriorRingN(i);
			LinearRing bHole = b.getInteriorRingN(i);
			if (!isEqualDim(aHole.getCoordinateSequence(), bHole.getCoordinateSequence(), dimension))
				return false;
		}
		return true;
	}

	protected GeometryFactory getGeometryFactory() {
		return geomFactory;
	}

	protected Geometry read(String wkt) {
		// return read(readerWKT, wkt);
		return WKTorBReader.read(wkt, geomFactory);
	}

	protected Geometry[] readArray(String... wkt) {
		Geometry[] geometries = new Geometry[wkt.length];
		for (int i = 0; i < wkt.length; i++) {
			geometries[i] = (wkt[i] == null) ? null : read(wkt[i]);
		}
		return geometries;
	}

	protected List readList(String[] wkt) {
		ArrayList geometries = new ArrayList(wkt.length);
		for (String s : wkt) {
			geometries.add(read(s));
		}
		return geometries;
	}

	GeometryCollection toGeometryCollection(Collection geoms) {
		return geomFactory.createGeometryCollection(GeometryFactory.toGeometryArray(geoms));
	}
}
