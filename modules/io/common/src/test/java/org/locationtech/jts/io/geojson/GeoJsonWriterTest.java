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

package org.locationtech.jts.io.geojson;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;

import test.jts.GeometryTestCase;

public class GeoJsonWriterTest extends GeometryTestCase {

  public GeoJsonWriter geoJsonWriter;

  @BeforeEach
  public void setUp() throws Exception {
    this.geoJsonWriter = new GeoJsonWriter();
  }

  @Test
  public void testCRS() throws ParseException {
    runTest(
        "POINT (1 2)",
        1234,
        "{'type':'Point','coordinates':[1,2],'crs':{'type':'name','properties':{'name':'EPSG:1234'}}}");
  }

  @Test
  public void testPoint() throws ParseException {
    runTest("POINT (1 2)", "{'type':'Point','coordinates':[1,2]}");
  }

  @Test
  public void testPointEmpty() throws ParseException {
    runTest("POINT EMPTY", "{'type':'Point','coordinates':[]}");
  }

  @Test
  public void testLineString() throws ParseException {
    runTest(
        "LINESTRING (1 2, 10 20, 100 200)",
        "{'type':'LineString','coordinates':[[1,2],[10,20],[100,200]]}");
  }

  @Test
  public void testLineStringEmpty() throws ParseException {
    runTest("LINESTRING EMPTY", "{'type':'LineString','coordinates':[]}");
  }

  @Test
  public void testPolygon() throws ParseException {
    runTest(
        "POLYGON ((0 0, 100 0, 100 100, 0 100, 0 0))",
        "{'type':'Polygon','coordinates':[[[0.0,0.0],[100,0.0],[100,100],[0.0,100],[0.0,0.0]]]}");
  }

  @Test
  public void testPolygonEmpty() throws ParseException {
    runTest("POLYGON EMPTY", "{'type':'Polygon','coordinates':[]}");
  }

  @Test
  public void testPolygonWithHole() throws ParseException {
    runTest(
        "POLYGON ((0 0, 100 0, 100 100, 0 100, 0 0), (1 1, 1 10, 10 10, 10 1, 1 1) )",
        "{'type':'Polygon','coordinates':[[[0.0,0.0],[100,0.0],[100,100],[0.0,100],[0.0,0.0]],[[1,1],[1,10],[10,10],[10,1],[1,1]]]}");
  }

  @Test
  public void testPolygonRightHandRule() throws ParseException {
    runTest(
        "POLYGON ((0 0, 0 100, 100 100, 100 0, 0 0))",
        true,
        "{'type':'Polygon','coordinates':[[[0.0,0.0],[100,0.0],[100,100],[0.0,100],[0.0,0.0]]]}");
  }

  @Test
  public void testPolygonWithHoleRightHandRule() throws ParseException {
    runTest(
        "POLYGON ((0 0, 0 100, 100 100, 100 0, 0 0), (1 1, 10 1, 10 10, 1 10, 1 1) )",
        true,
        "{'type':'Polygon','coordinates':[[[0.0,0.0],[100,0.0],[100,100],[0.0,100],[0.0,0.0]],[[1,1],[1,10],[10,10],[10,1],[1,1]]]}");
  }

  @Test
  public void testMultiPoint() throws ParseException {
    runTest(
        "MULTIPOINT ((0 0), (1 4), (100 200))",
        "{'type':'MultiPoint','coordinates':[[0.0,0.0],[1,4],[100,200]]}");
  }

  @Test
  public void testMultiLineString() throws ParseException {
    runTest(
        "MULTILINESTRING ((0 0, 1 10), (10 10, 20 30), (123 123, 456 789))",
        "{'type':'MultiLineString','coordinates':[[[0.0,0.0],[1,10]],[[10,10],[20,30]],[[123,123],[456,789]]]}");
  }

  @Test
  public void testMultiPolygon() throws ParseException {
    runTest(
        "MULTIPOLYGON ( ((0 0, 100 0, 100 100, 0 100, 0 0), (1 1, 1 10, 10 10, 10 1, 1 1) ), ((200 200, 200 250, 250 250, 250 200, 200 200)) )",
        "{'type':'MultiPolygon','coordinates':[[[[0.0,0.0],[100,0.0],[100,100],[0.0,100],[0.0,0.0]],[[1,1],[1,10],[10,10],[10,1],[1,1]]],[[[200,200],[200,250],[250,250],[250,200],[200,200]]]]}");
  }

  @Test
  public void testGeometryCollection() throws ParseException {
    runTest(
        "GEOMETRYCOLLECTION ( POINT ( 1 1), LINESTRING (0 0, 10 10), POLYGON ((0 0, 100 0, 100 100, 0 100, 0 0)) )",
        "{'type':'GeometryCollection','geometries':[{'type':'Point','coordinates':[1,1]},{'type':'LineString','coordinates':[[0.0,0.0],[10,10]]},{'type':'Polygon','coordinates':[[[0.0,0.0],[100,0.0],[100,100],[0.0,100],[0.0,0.0]]]}]}");
  }

  // empty atomic geometries are not supported in GeoJSON

  @Test
  public void testMultiPointEmpty() throws ParseException {
    runTest("MULTIPOINT EMPTY", "{'type':'MultiPoint','coordinates':[]}");
  }

  @Test
  public void testMultiLineStringEmpty() throws ParseException {
    runTest("MULTILINESTRING EMPTY", "{'type':'MultiLineString','coordinates':[]}");
  }

  @Test
  public void testMultiPolygonEmpty() throws ParseException {
    runTest("MULTIPOLYGON EMPTY", "{'type':'MultiPolygon','coordinates':[]}");
  }

  @Test
  public void testGeometryCollectionEmpty() throws ParseException {
    runTest("GEOMETRYCOLLECTION EMPTY", "{'type':'GeometryCollection','geometries':[]}");
  }

  private void runTest(String wkt) throws ParseException {
    Geometry expected = read(wkt);
    geoJsonWriter.setEncodeCRS(false);
    String json = this.geoJsonWriter.write(expected);
    System.out.println('"' + json.replace('"', '\'') + '"');
    // checkEqual(result, expected);
  }

  private void runTest(String wkt, String expectedGeojson) throws ParseException {
    runTest(wkt, 0, false, false, expectedGeojson);
  }

  private void runTest(String wkt, int srid, String expectedGeojson) throws ParseException {
    runTest(wkt, srid, true, false, expectedGeojson);
  }

  private void runTest(String wkt, boolean enforceRHR, String expectedGeojson)
      throws ParseException {
    runTest(wkt, 0, false, enforceRHR, expectedGeojson);
  }

  private void runTest(
      String wkt, int srid, boolean encodeCRS, boolean enforceRHR, String expectedGeojson)
      throws ParseException {
    Geometry geom = read(wkt);
    geom.setSRID(srid);
    geoJsonWriter.setEncodeCRS(encodeCRS);
    geoJsonWriter.setForceCCW(enforceRHR);
    String json = this.geoJsonWriter.write(geom);
    json = json.replace('"', '\'');
    assertEquals(expectedGeojson, json);
  }
}
