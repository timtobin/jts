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

package org.locationtech.jts.io.kml;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.ParseException;

public class KMLReaderTest {
	private final KMLReader kmlReader = new KMLReader(Arrays.asList("altitudeMode", "tesselate", "extrude"));

	private void checkExceptionThrown(String kmlString, String expectedError) {
		try {
			kmlReader.read(kmlString);
			Assertions.fail("Exception must be thrown");
		} catch (ParseException e) {
			assertEquals(expectedError, e.getMessage(), "Exception text differs");
		}
	}

	private void checkParsingResult(String kmlString, String expectedWKT, Map<String, String>[] expectedAttributes) {
		try {
			Geometry parsedGeometry = kmlReader.read(kmlString);
			String wkt = parsedGeometry.toText();

			assertEquals(expectedWKT, wkt, "WKTs are not equal");

			for (int i = 0; i < parsedGeometry.getNumGeometries(); i++) {
				Geometry geometryN = parsedGeometry.getGeometryN(i);
				assertTrue(geometryN.getUserData() != null || expectedAttributes[i] == null, "User data is not filled");

				if (geometryN.getUserData() != null) {
					Map<String, String> actualUserData = (Map<String, String>) geometryN.getUserData();
					assertEquals(expectedAttributes[i].size(), actualUserData.size(),
							"Number of attributes differs in user data");

					for (Map.Entry<String, String> entry : expectedAttributes[i].entrySet()) {
						assertTrue(actualUserData.containsKey(entry.getKey()),
								"User data has not attribute " + entry.getKey());
						assertEquals(entry.getValue(), actualUserData.get(entry.getKey()), "Attribute value differs");
					}
				}
			}
		} catch (ParseException e) {
			throw new RuntimeException("ParseException: " + e.getMessage());
		}
	}

	@Test
	public void testCoordinatesErrors() {
		checkExceptionThrown("<Point></Point>", "No element coordinates found in Point");
		checkExceptionThrown("<Point><coordinates></coordinates></Point>", "Empty coordinates");
		checkExceptionThrown("<Point><coordinates>1.0</coordinates></Point>", "Invalid coordinate format");
		checkExceptionThrown("<Point><coordinates>,1.0</coordinates></Point>", "Invalid coordinate format");
		checkExceptionThrown("<Point><coordinates>1.0,</coordinates></Point>", "Invalid coordinate format");

		checkExceptionThrown("<Polygon></Polygon>", "No outer boundary for Polygon");
		checkExceptionThrown("<Polygon><outerBoundaryIs><LinearRing></LinearRing></outerBoundaryIs></Polygon>",
				"No element coordinates found in outerBoundaryIs");
		checkExceptionThrown("<Polygon><innerBoundaryIs><LinearRing></LinearRing></innerBoundaryIs></Polygon>",
				"No element coordinates found in innerBoundaryIs");
	}

	@Test
	public void testCoordinatesWithWhitespace() {
		checkParsingResult(
				"<LineString>" + "   <coordinates> 1.0,2.0" + "   -1.0,-2.0 </coordinates>" + "</LineString>",
				"LINESTRING (1 2, -1 -2)", new Map[]{null, null});
	}

	@Test
	public void testLineString() {
		checkParsingResult(
				"<LineString><tesselate>1</tesselate><coordinates>1.0,1.0 2.0,2.0</coordinates></LineString>",
				"LINESTRING (1 1, 2 2)", new Map[]{Collections.singletonMap("tesselate", "1")});
	}

	@Test
	public void testMultiGeometry() {
		checkParsingResult("<MultiGeometry>" + "   <Point>" + "       <altitudeMode>absolute</altitudeMode>"
				+ "       <coordinates>1.0,1.0</coordinates>" + "   </Point>" + "   <LineString>"
				+ "       <tesselate>1</tesselate>" + "       <coordinates>1.0,1.0 2.0,2.0</coordinates>"
				+ "   </LineString>" + "   <Polygon>" + "       <altitudeMode>relativeToGround</altitudeMode>"
				+ "       <outerBoundaryIs>" + "           <LinearRing>"
				+ "               <coordinates>1.0,1.0 1.0,10.0 10.0,10.0 10.0,1.0 1.0,1.0</coordinates>"
				+ "           </LinearRing>" + "       </outerBoundaryIs>" + "   </Polygon>" + "</MultiGeometry>",
				"GEOMETRYCOLLECTION (POINT (1 1), LINESTRING (1 1, 2 2), POLYGON ((1 1, 1 10, 10 10, 10 1, 1 1)))",
				new Map[]{Collections.singletonMap("altitudeMode", "absolute"),
						Collections.singletonMap("tesselate", "1"),
						Collections.singletonMap("altitudeMode", "relativeToGround")});
	}

	@Test
	public void testMultiGeometryWithAllLines() {
		checkParsingResult(
				"<MultiGeometry>" + "   <LineString><coordinates>1.0,1.0 2.0,2.0</coordinates></LineString>"
						+ "   <LineString><coordinates>5.0,5.0 6.0,6.0</coordinates></LineString>" + "</MultiGeometry>",
				"MULTILINESTRING ((1 1, 2 2), (5 5, 6 6))", new Map[]{null, null});
	}

	@Test
	public void testMultiGeometryWithAllPoints() {
		checkParsingResult(
				"<MultiGeometry>" + "   <Point><coordinates>1.0,1.0</coordinates></Point>"
						+ "   <Point><coordinates>2.0,2.0</coordinates></Point>" + "</MultiGeometry>",
				"MULTIPOINT ((1 1), (2 2))", new Map[]{null, null});
	}

	@Test
	public void testMultiGeometryWithAllPolygons() {
		checkParsingResult("<MultiGeometry>"
				+ "   <Polygon><outerBoundaryIs><LinearRing><coordinates>2.0,2.0 2.0,3.0 3.0,3.0 3.0,2.0 2.0,2.0</coordinates></LinearRing></outerBoundaryIs></Polygon>"
				+ "   <Polygon><outerBoundaryIs><LinearRing><coordinates>6.0,6.0 6.0,7.0 7.0,7.0 7.0,6.0 6.0,6.0</coordinates></LinearRing></outerBoundaryIs></Polygon>"
				+ "</MultiGeometry>", "MULTIPOLYGON (((2 2, 2 3, 3 3, 3 2, 2 2)), ((6 6, 6 7, 7 7, 7 6, 6 6)))",
				new Map[]{null, null});
	}

	@Test
	public void testPoint() {
		checkParsingResult("<Point><altitudeMode>absolute</altitudeMode><coordinates>1.0,1.0</coordinates></Point>",
				"POINT (1 1)", new Map[]{Collections.singletonMap("altitudeMode", "absolute")});
	}

	@Test
	public void testPolygon() {
		checkParsingResult("<Polygon>" + "   <altitudeMode>relativeToGround</altitudeMode>" + "   <outerBoundaryIs>"
				+ "       <LinearRing>"
				+ "           <coordinates>1.0,1.0 1.0,10.0 10.0,10.0 10.0,1.0 1.0,1.0</coordinates>"
				+ "       </LinearRing>" + "   </outerBoundaryIs>" + "   <innerBoundaryIs>" + "       <LinearRing>"
				+ "           <coordinates>2.0,2.0 2.0,3.0 3.0,3.0 3.0,2.0 2.0,2.0</coordinates>"
				+ "       </LinearRing>" + "   </innerBoundaryIs>" + "   <innerBoundaryIs>" + "       <LinearRing>"
				+ "           <coordinates>6.0,6.0 6.0,7.0 7.0,7.0 7.0,6.0 6.0,6.0</coordinates>"
				+ "       </LinearRing>" + "   </innerBoundaryIs>" + "</Polygon>",
				"POLYGON ((1 1, 1 10, 10 10, 10 1, 1 1), (2 2, 2 3, 3 3, 3 2, 2 2), (6 6, 6 7, 7 7, 7 6, 6 6))",
				new Map[]{Collections.singletonMap("altitudeMode", "relativeToGround")});
	}

	@Test
	public void testPrecisionAndSRID() {
		String kml = "<Point><altitudeMode>absolute</altitudeMode><coordinates>1.385093,1.436456</coordinates></Point>";
		GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(1000.0), 4326);
		KMLReader kmlReader = new KMLReader(geometryFactory);
		try {
			Geometry parsedGeometry = kmlReader.read(kml);
			assertEquals(geometryFactory.getSRID(), parsedGeometry.getSRID(), "Wrong SRID");
			assertEquals("POINT (1.385 1.436)", parsedGeometry.toText(), "Wrong precision");
		} catch (ParseException e) {
			throw new RuntimeException("ParseException: " + e.getMessage());
		}
	}

	@Test
	public void testUnknownGeometryType() {
		checkExceptionThrown("<StrangePoint></StrangePoint>", "Unknown KML geometry type StrangePoint");
	}

	@Test
	public void testZ() {
		String kml = "<Point><coordinates>1.0,1.0,50.0</coordinates></Point>";
		KMLReader kmlReader = new KMLReader();
		try {
			Geometry parsedGeometry = kmlReader.read(kml);
			assertEquals(50.0, parsedGeometry.getCoordinate().z, "Wrong Z");
		} catch (ParseException e) {
			throw new RuntimeException("ParseException: " + e.getMessage());
		}
	}
}
