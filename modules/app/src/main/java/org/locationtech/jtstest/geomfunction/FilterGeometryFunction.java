/*
 * Copyright (c) 2021 Martin Davis
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v20.html
 * and the Eclipse Distribution License is available at
 *
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */
package org.locationtech.jtstest.geomfunction;

import org.locationtech.jts.geom.Geometry;

public class FilterGeometryFunction implements GeometryFunction {

	public static int OP_EQ = 1;
	public static int OP_GE = 3;
	public static int OP_GT = 4;
	public static int OP_LE = 5;
	public static int OP_LT = 6;
	public static int OP_NE = 2;

	private final int filterOp;
	private final double filterVal;
	private final GeometryFunction fun;

	public FilterGeometryFunction(GeometryFunction fun, double filterVal) {
		this.fun = fun;
		this.filterVal = filterVal;
		filterOp = OP_EQ;
	}

	public FilterGeometryFunction(GeometryFunction fun, int op, double val) {
		this.fun = fun;
		this.filterOp = op;
		this.filterVal = val;
	}

	public String getCategory() {
		return fun.getCategory();
	}

	public String getDescription() {
		return fun.getDescription();
	}

	public String getName() {
		return fun.getName() + "?";
	}

	public String[] getParameterNames() {
		return fun.getParameterNames();
	}

	public Class[] getParameterTypes() {
		return fun.getParameterTypes();
	}

	public Class getReturnType() {
		return Geometry.class;
	}

	public String getSignature() {
		return fun.getSignature();
	}

	@Override
	public Object invoke(Geometry geom, Object[] args) {
		Object result = fun.invoke(geom, args);
		double val = toDouble(result);
		if (isMatch(val))
			return geom;
		return null;
	}

	public boolean isBinary() {
		return fun.isBinary();
	}

	private boolean isMatch(double val) {
		if (OP_EQ == filterOp)
			return val == filterVal;
		if (OP_NE == filterOp)
			return val != filterVal;
		if (OP_GE == filterOp)
			return val >= filterVal;
		if (OP_GT == filterOp)
			return val > filterVal;
		if (OP_LE == filterOp)
			return val <= filterVal;
		if (OP_LT == filterOp)
			return val < filterVal;
		return false;
	}

	public boolean isRequiredB() {
		return fun.isRequiredB();
	}

	private double toDouble(Object result) {
		if (result instanceof Boolean boolean1)
			return boolean1 ? 1 : 0;
		if (result instanceof Number number)
			return number.doubleValue();
		return 0;
	}
}
