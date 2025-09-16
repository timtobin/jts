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
package org.locationtech.jts.io;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.GeometryFactory;

/** Tests the {@link WKTReader} with various syntax errors */
public class WKTReaderParseErrorTest {
  private final GeometryFactory fact = new GeometryFactory();
  private final WKTReader rdr = new WKTReader(fact);

  @Test
  public void testExtraLParen() throws IOException {
    readWithParseException("POINT (( 1e01 -1E02)");
  }

  @Test
  public void testMissingOrdinate() throws IOException {
    readWithParseException("POINT ( 1e01 )");
  }

  @Test
  public void testBadChar() throws IOException {
    readWithParseException("POINT ( # 1e-04 1E-05)");
  }

  @Test
  public void testBadExpFormat() throws IOException {
    readWithParseException("POINT (1e0a1 1X02)");
  }

  @Test
  public void testBadExpPlusSign() throws IOException {
    readWithParseException("POINT (1e+01 1X02)");
  }

  @Test
  public void testBadPlusSign() throws IOException {
    readWithParseException("POINT ( +1e+01 1X02)");
  }

  @Test
  public void testBadNumber() throws IOException {
    readWithParseException("POINT (0x 0)");
    readWithParseException("POINT (0e 0)");
    readWithParseException("POINT (0.. 0)");
  }

  @Test
  public void testBadCharsInType() throws IOException {
    readWithParseException("POINTABC ( 0 0 )");
    readWithParseException("LINESTRINGABC ( 0 0 )");
    readWithParseException("LINEARRINGABC ( 0 0, 0 0, 0 0 )");
    readWithParseException("POLYGONABC (( 0 0, 0 0, 0 0, 0 0 ))");
    readWithParseException("MULTIPOINTABC (( 0 0 ), ( 0 0 ))");
    readWithParseException("MULTILINESTRINGABC (( 0 0, 1 1 ), ( 0 0, 1 1 ))");
    readWithParseException("MULTIPOLYGONABC ((( 0 0, 1 1, 2 2, 0 0 )), (( 0 0, 1 1, 2 2, 0 0 )))");
    readWithParseException("GEOMETRYCOLLECTIONABC (POINT( 0 0 ), LINESTRING( 0 0, 1 1))");
  }

  @Test
  public void testBadCharsInTypeZ() throws IOException {
    readWithParseException("POINTABCZ ( 0 0 )");
    readWithParseException("LINESTRINGABCZ ( 0 0 )");
    readWithParseException("LINEARRINGABCZ ( 0 0, 0 0, 0 0 )");
    readWithParseException("POLYGONABCZ (( 0 0, 0 0, 0 0, 0 0 ))");
    readWithParseException("MULTIPOINTABCZ (( 0 0 ), ( 0 0 ))");
    readWithParseException("MULTILINESTRINGABCZ (( 0 0, 1 1 ), ( 0 0, 1 1 ))");
    readWithParseException("MULTIPOLYGONABCZ ((( 0 0, 1 1, 2 2, 0 0 )), (( 0 0, 1 1, 2 2, 0 0 )))");
    readWithParseException("GEOMETRYCOLLECTIONABCZ (POINT( 0 0 ), LINESTRING( 0 0, 1 1))");
  }

  @Test
  public void testBadCharsInTypeM() throws IOException {
    readWithParseException("LINESTRINGABCM ( 0 0 0, 1 1 1 )");
  }

  @Test
  public void testBadCharsInTypeZM() throws IOException {
    readWithParseException("LINESTRINGABCZM ( 0 0 0 0, 1 1 1 1 )");
  }

  @Test
  public void testBadType() throws IOException {
    readWithParseException("POIN (0 0)");
    readWithParseException("POIN T(0 0)");
    readWithParseException("P OINT (0 0)");
    readWithParseException("POINtt (0 0)");
    readWithParseException("POINTzz (0 0)");
    readWithParseException("POINTabc (0 0)");
    readWithParseException("POINTxy (0 0)");
    readWithParseException("POINT XY (0 0)");
    readWithParseException("POINT XY EMPT");
    readWithParseException("POINT XY EMPT Y");
    readWithParseException("POINT XY EMPTYY");

    // -- not an error, since parser stops after correct parse
    // checkParseError("POINT EMPTY Z");
  }

  @Test
  public void testBadDimension() throws IOException {
    readWithParseException("POINTZZ (0 0 0)");
    readWithParseException("POINT ZZ (0 0 0)");
    readWithParseException("POINT ZZM (0 0 0)");

    readWithParseException("POINT Z M (0 0 0 0)");
    readWithParseException("POINTZ M (0 0 0 0)");
    readWithParseException("POINT MZ (0 0 0 0)");
    readWithParseException("POINTMZ (0 0 0 0)");
    readWithParseException("POINTZ ZM (0 0 0 0)");
    readWithParseException("POINT ZMc (0 0 0 0)");

    // -- not errors; perhaps should be?
    // checkParseErrorZ("POINTZ Z (0 0 0)");
    // checkParseErrorZM("POINTZM Z (0 0 0 0)");
  }

  @Test
  public void testMissingOrdinates() throws IOException {
    readWithParseException("POINT (0)");
    readWithParseException("LINESTRING (0, 1 1)");
  }

  @Test
  public void testMissingComponents() throws IOException {
    readWithParseException("MULTILINESTRING (0 0)");
    readWithParseException("MULTILINESTRING ()");
    readWithParseException("GEOMETRYCOLLECTION ()");
    readWithParseException("GEOMETRYCOLLECTION");
  }

  @Test
  public void testEmptyComponents() throws ParseException, IOException {
    readWithInvalidException("POLYGON( EMPTY, (1 1,2 2,1 2,1 1))");

    // -- empty rings are valid
    // checkInvalidError("POLYGON( (1 1,2 2,1 2,1 1), EMPTY)");
  }

  private void readWithParseException(String wkt) throws IOException {
    boolean threwParseEx = false;
    try {
      rdr.read(wkt);
    } catch (ParseException ex) {
      // System.out.println(ex.getMessage());
      threwParseEx = true;
    }
    assertTrue(threwParseEx);
  }

  private void readWithInvalidException(String wkt) throws IOException, ParseException {
    try {
      rdr.read(wkt);
    } catch (IllegalArgumentException ex) {
      // System.out.println(ex.getMessage());
      return;
    }
    fail();
  }
}
