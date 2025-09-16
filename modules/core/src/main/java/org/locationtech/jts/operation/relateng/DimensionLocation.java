/*
 * Copyright (c) 2023 Martin Davis.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v20.html
 * and the Eclipse Distribution License is available at
 *
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.locationtech.jts.operation.relateng;

import org.locationtech.jts.geom.Dimension;
import org.locationtech.jts.geom.Location;

/**
 * Codes which combine a geometry dimension and a location on the geometry.
 *
 * @author mdavis
 */
class DimensionLocation {

	public static final int EXTERIOR = Location.EXTERIOR;
	public static final int POINT_INTERIOR = 103;
	public static final int LINE_INTERIOR = 110;
	public static final int LINE_BOUNDARY = 111;
	public static final int AREA_INTERIOR = 120;
	public static final int AREA_BOUNDARY = 121;

	public static int locationArea(int loc) {
		return switch (loc) {
			case Location.INTERIOR -> AREA_INTERIOR;
			case Location.BOUNDARY -> AREA_BOUNDARY;
			default -> EXTERIOR;
		};
	}

	public static int locationLine(int loc) {
		return switch (loc) {
			case Location.INTERIOR -> LINE_INTERIOR;
			case Location.BOUNDARY -> LINE_BOUNDARY;
			default -> EXTERIOR;
		};
	}

	public static int locationPoint(int loc) {
		return switch (loc) {
			case Location.INTERIOR -> POINT_INTERIOR;
			default -> EXTERIOR;
		};
	}

	public static int location(int dimLoc) {
		return switch (dimLoc) {
			case POINT_INTERIOR, LINE_INTERIOR, AREA_INTERIOR -> Location.INTERIOR;
			case LINE_BOUNDARY, AREA_BOUNDARY -> Location.BOUNDARY;
			default -> Location.EXTERIOR;
		};
	}

	public static int dimension(int dimLoc) {
		return switch (dimLoc) {
			case POINT_INTERIOR -> Dimension.P;
			case LINE_INTERIOR, LINE_BOUNDARY -> Dimension.L;
			case AREA_INTERIOR, AREA_BOUNDARY -> Dimension.A;
			default -> Dimension.FALSE;
		};
	}

	public static int dimension(int dimLoc, int exteriorDim) {
		if (dimLoc == EXTERIOR)
			return exteriorDim;
		return dimension(dimLoc);
	}
}
