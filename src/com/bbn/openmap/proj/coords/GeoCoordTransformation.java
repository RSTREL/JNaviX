//**********************************************************************
//
//<copyright>
//
//BBN Technologies
//10 Moulton Street
//Cambridge, MA 02138
//(617) 873-8000
//
//Copyright (C) BBNT Solutions LLC. All rights reserved.
//
//</copyright>
//**********************************************************************
//
//$Source:
///cvs/darwars/ambush/aar/src/com/bbn/ambush/mission/MissionHandler.java,v
//$
//$RCSfile: GeoCoordTransformation.java,v $
//$Revision: 1.1.2.3 $
//$Date: 2008/01/25 17:44:27 $
//$Author: dietrick $
//
//**********************************************************************

package com.bbn.openmap.proj.coords;

import java.awt.geom.Point2D;

import com.bbn.openmap.LatLonPoint;

/**
 * A DecimalDegreeTranslator is an object that knows how to translate a set of
 * coordinates from one coordinate system definition to/from a set of decimal
 * degree coordinates.
 * 
 * @author dietrick
 */
public interface GeoCoordTransformation {
    Point2D forward(double lat, double lon);

    Point2D forward(double lat, double lon, Point2D ret);

    LatLonPoint inverse(double x, double y);

    LatLonPoint inverse(double x, double y, LatLonPoint ret);
}
