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
//$RCSfile: UTMGCT.java,v $
//$Revision: 1.1.2.5 $
//$Date: 2008/09/18 23:23:42 $
//$Author: dietrick $
//
//**********************************************************************

package com.bbn.openmap.proj.coords;

import java.awt.geom.Point2D;
import java.util.Properties;

import com.bbn.openmap.LatLonPoint;
import com.bbn.openmap.proj.Ellipsoid;
import com.bbn.openmap.util.PropUtils;

public class UTMGCT extends AbstractGCT implements GeoCoordTransformation {

	public final static String ZoneProperty = "zone";
	public final static String HemiProperty = "hemi";
	public final static String ElliposoidProperty = "ellipsoid";

	protected Ellipsoid ellipsoid = Ellipsoid.WGS_84;

	protected LatLonPoint tmpLL = new LatLonPoint();
	protected UTMPoint tmpUTM = new UTMPoint();

	protected int zone_number;
	protected char zone_letter;

	/**
	 * Make sure you call setProperties() or set the UTM before trying to use
	 * this object.
	 * 
	 */
	public UTMGCT() {
	}

	public UTMGCT(int zone_number, char zone_letter) {
		this.zone_number = zone_number;
		this.zone_letter = zone_letter;
	}

	public synchronized Point2D forward(double lat, double lon, Point2D ret) {
		tmpLL.setLatLon((float) lat, (float) lon);
		UTMPoint.LLtoUTM(tmpLL, ellipsoid, tmpUTM, zone_number,
				zone_letter == 'N');
		ret.setLocation(tmpUTM.easting, tmpUTM.northing);
		return ret;
	}

	public LatLonPoint inverse(double x, double y, LatLonPoint ret) {
		UTMPoint.UTMtoLL(ellipsoid, (float) y, (float) x, zone_number,
				zone_letter, ret);
		return ret;
	}

	public void setProperties(String propertyPrefix, Properties props) {
		super.setProperties(propertyPrefix, props);

		propertyPrefix = PropUtils.getScopedPropertyPrefix(propertyPrefix);
		zone_number = PropUtils.intFromProperties(props, propertyPrefix
				+ ZoneProperty, 0);
		String hString = props.getProperty(propertyPrefix + HemiProperty, "N");
		zone_letter = hString.charAt(0);

		String eName = props.getProperty(propertyPrefix + ElliposoidProperty,
				"WGS_84");
		ellipsoid = Ellipsoid.getByName(eName);
	}

	public Properties getProperties(Properties props) {
		props = super.getProperties(props);
		String prefix = PropUtils.getScopedPropertyPrefix(this);
		props.put(prefix + ZoneProperty, Integer.toString(zone_number));
		props.put(prefix + HemiProperty, Character.toString(zone_letter));

		props.put(prefix + ElliposoidProperty, ellipsoid.name.toUpperCase()
				.replace(' ', '_'));
		return props;
	}

	public Properties getPropertyInfo(Properties props) {
		props = super.getPropertyInfo(props);
		return props;
	}

	public Ellipsoid getEllipsoid() {
		return ellipsoid;
	}

	public void setEllipsoid(Ellipsoid ellipsoid) {
		this.ellipsoid = ellipsoid;
	}
}
