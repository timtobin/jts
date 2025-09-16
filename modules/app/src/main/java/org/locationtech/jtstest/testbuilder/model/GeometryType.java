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

public interface GeometryType {
	public static final int GEOMETRYCOLLECTION = 1;

	public static final int LINESTRING = 6;
	public static final int MULTILINESTRING = 3;
	public static final int MULTIPOINT = 4;
	public static final int MULTIPOLYGON = 2;
	public static final int POINT = 7;
	public static final int POLYGON = 5;
	public static final int WELLKNOWNTEXT = 1;
}
