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
package org.locationtech.jtstest.test;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.IntersectionMatrix;
import org.locationtech.jts.io.ParseException;

/**
 * @version 1.7
 */
public interface Testable {

	String getDescription();

	Geometry getGeometry(int index);

	IntersectionMatrix getIntersectionMatrix();

	String getName();

	String getWellKnownText(int i);

	void initGeometry() throws ParseException;

	void setGeometry(int index, Geometry g);

	void setIntersectionMatrix(IntersectionMatrix im);

	void setName(String name);
}
