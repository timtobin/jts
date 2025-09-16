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
package org.locationtech.jtstest.testbuilder;

import javax.swing.ImageIcon;

public class AppIcons {
  public static final ImageIcon APP = load("app-icon.gif");

  public static final ImageIcon ADD = load("Plus.png");
  public static final ImageIcon ADD_SMALL = load("Plus_small.png");
  public static final ImageIcon DELETE = load("Delete.png");
  public static final ImageIcon DELETE_SMALL = load("Delete_small.png");

  public static final ImageIcon EXECUTE = load("Execute.png");
  public static final ImageIcon SAVE_IMAGE = load("SaveImage.png");
  public static final ImageIcon UNDO = load("Undo.png");
  public static final ImageIcon CLEAR = load("Delete_small.png");
  public static final ImageIcon GEOM_INSPECT = load("InspectGeometry.png");
  public static final ImageIcon GEOM_EXCHANGE = load("ExchangeGeoms.png");

  public static final ImageIcon GEOFUNC_BINARY = load("BinaryGeomFunction.png");
  public static final ImageIcon EDIT_GRID = load("DrawingGrid.png");

  public static final ImageIcon DOWN = load("Down.png");
  public static final ImageIcon UP = load("Up.png");
  public static final ImageIcon LEFT = load("Left.png");
  public static final ImageIcon RIGHT = load("Right.png");

  public static final ImageIcon ZOOM = load("Magnify.png");
  public static final ImageIcon COPY_TO_TEST = load("CopyToTest.png");
  public static final ImageIcon COPY = load("Copy.png");
  public static final ImageIcon PASTE = load("Paste.png");
  public static final ImageIcon CUT = load("Delete_small.png");
  public static final ImageIcon GEOM_LOAD = load("LoadWKTToTest.png");
  public static final ImageIcon MOVE = load("Move.png");

  public static final ImageIcon ICON_COLLECTION = load("Icon_GeomCollection.png");
  public static final ImageIcon ICON_COLLECTION_B = load("Icon_GeomCollection_B.png");
  public static final ImageIcon ICON_LINEARRING = load("Icon_LinearRing.png");
  public static final ImageIcon ICON_LINEARRING_B = load("Icon_LinearRing_B.png");
  public static final ImageIcon ICON_LINESTRING = load("Icon_LineString.png");
  public static final ImageIcon ICON_LINESTRING_B = load("Icon_LineString_B.png");
  public static final ImageIcon ICON_POINT = load("Icon_Point.png");
  public static final ImageIcon ICON_POINT_B = load("Icon_Point_B.png");
  public static final ImageIcon ICON_POLYGON = load("Icon_Polygon.png");
  public static final ImageIcon ICON_POLYGON_B = load("Icon_Polygon_B.png");

  public static ImageIcon load(String filename) {
    return new ImageIcon(AppIcons.class.getResource(filename));
  }
}
