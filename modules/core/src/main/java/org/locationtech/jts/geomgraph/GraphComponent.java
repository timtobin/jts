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
package org.locationtech.jts.geomgraph;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.IntersectionMatrix;
import org.locationtech.jts.util.Assert;

/**
 * A GraphComponent is the parent class for the objects' that form a graph. Each
 * GraphComponent can carry a Label.
 *
 * @version 1.7
 */
public abstract class GraphComponent {

	private boolean isCovered = false;

	private boolean isCoveredSet = false;

	/**
	 * isInResult indicates if this component has already been included in the
	 * result
	 */
	private boolean isInResult = false;

	private boolean isVisited = false;
	protected Label label;

	public GraphComponent() {
	}

	public GraphComponent(Label label) {
		this.label = label;
	}

	/**
	 * Compute the contribution to an IM for this component.
	 *
	 * @param im
	 *            Intersection matrix
	 */
	protected abstract void computeIM(IntersectionMatrix im);

	/**
	 * @return a coordinate in this component (or null, if there are none)
	 */
	public abstract Coordinate getCoordinate();

	public Label getLabel() {
		return label;
	}

	public boolean isCovered() {
		return isCovered;
	}

	public boolean isCoveredSet() {
		return isCoveredSet;
	}

	public boolean isInResult() {
		return isInResult;
	}

	/**
	 * An isolated component is one that does not intersect or touch any other
	 * component. This is the case if the label has valid locations for only a
	 * single Geometry.
	 *
	 * @return true if this component is isolated
	 */
	public abstract boolean isIsolated();

	public boolean isVisited() {
		return isVisited;
	}

	public void setCovered(boolean isCovered) {
		this.isCovered = isCovered;
		this.isCoveredSet = true;
	}

	public void setInResult(boolean isInResult) {
		this.isInResult = isInResult;
	}

	public void setLabel(Label label) {
		this.label = label;
	}

	public void setVisited(boolean isVisited) {
		this.isVisited = isVisited;
	}

	/**
	 * Update the IM with the contribution for this component. A component only
	 * contributes if it has a labelling for both parent geometries
	 *
	 * @param im
	 *            Intersection matrix
	 */
	public void updateIM(IntersectionMatrix im) {
		Assert.isTrue(label.getGeometryCount() >= 2, "found partial label");
		computeIM(im);
	}
}
