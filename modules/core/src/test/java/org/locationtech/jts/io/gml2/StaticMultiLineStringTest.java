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
package org.locationtech.jts.io.gml2;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.generator.LineStringGenerator;
import org.locationtech.jts.generator.MultiGenerator;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.MultiLineString;
import org.xml.sax.SAXException;


/**
 * Round trip testing for GML reading and writing. 
 *
 * @author David Zwiers, Vivid Solutions. 
 */
public class StaticMultiLineStringTest extends WritingTestCase {
  /**
   * Round Trip test for a single line string
   * @throws ParserConfigurationException 
   * @throws IOException 
   * @throws SAXException 
   */
  @Test
  public void testSingleMultiLineStringRoundTrip() throws SAXException, IOException, ParserConfigurationException {
    LineStringGenerator pgc = new LineStringGenerator();
    pgc.setGeometryFactory(geometryFactory);
    pgc.setNumberPoints(10);
    MultiGenerator pg = new MultiGenerator(pgc);
    pg.setBoundingBox(new Envelope(0, 10, 0, 10));
    pg.setNumberGeometries(3);
    pg.setGeometryFactory(geometryFactory);

    MultiLineString pt = (MultiLineString) pg.create();
    checkRoundTrip(pt);
  }

  /**
   * Round Trip test for a single line string with lots of points
   * @throws IOException 
   * @throws ParserConfigurationException 
   * @throws SAXException 
   */
  @Test
  public void testSingleMultiLineStringManyPointRoundTrip() throws IOException, SAXException, ParserConfigurationException {

    LineStringGenerator pgc = new LineStringGenerator();
    pgc.setGeometryFactory(geometryFactory);
    pgc.setNumberPoints(1000);
    pgc.setGenerationAlgorithm(LineStringGenerator.HORZ);
    MultiGenerator pg = new MultiGenerator(pgc);
    pg.setBoundingBox(new Envelope(0, 10, 0, 10));
    pg.setNumberGeometries(3);
    pg.setGeometryFactory(geometryFactory);

    MultiLineString pt = (MultiLineString) pg.create();
    checkRoundTrip(pt);
  }
}
