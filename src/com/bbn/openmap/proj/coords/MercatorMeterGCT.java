package com.bbn.openmap.proj.coords;

import java.awt.geom.Point2D;

import com.bbn.openmap.LatLonPoint;
import com.bbn.openmap.MoreMath;
import com.bbn.openmap.proj.Planet;

/**
 * Convert between mercator meters and lat/lon degrees.
 * 
 * http://johndeck.blogspot.com/2005_09_01_johndeck_archive.html
 * http://search.cpan.org/src/RRWO/GPS-Lowrance-0.31/lib/Geo/Coordinates/MercatorMeters.pm
 */
public class MercatorMeterGCT extends AbstractGCT implements
        GeoCoordTransformation {
    
    public final static MercatorMeterGCT INSTANCE = new MercatorMeterGCT();
    
    // TODO: better names?
    private double latfac;
    private double lonfac;
    
    public MercatorMeterGCT() {
        latfac = Planet.wgs84_earthPolarRadiusMeters_D;
        lonfac = Planet.wgs84_earthPolarRadiusMeters_D;
    }

    public MercatorMeterGCT(double latfac, double lonfac) {
        this.latfac = latfac;
        this.lonfac = lonfac;
    }

    public Point2D forward(double lat, double lon, Point2D ret) {

        if (!((lat >= -90d) && (lat <= 90d))) {
            lat = LatLonPoint.normalize_latitude((float) lat);
        }
        if (!((lon >= -180d) && (lon <= 180d))) {
            lon = LatLonPoint.wrap_longitude((float) lon);
        }

        double latrad = Math.toRadians(lat);
        double lonrad = Math.toRadians(lon);

        double lat_m = latfac
                * Math.log(Math.tan(((latrad + MoreMath.HALF_PI_D) / 2)));
        double lon_m = lonfac * lonrad;

        ret.setLocation(lon_m, lat_m);

        return ret;
    }

    public LatLonPoint inverse(double lon_m, double lat_m, LatLonPoint ret) {
        double latrad = (2 * Math.atan(Math.exp(lat_m / latfac)))
                - MoreMath.HALF_PI_D;
        double lonrad = lon_m / lonfac;

        float lat = (float) Math.toDegrees(latrad);
        float lon = (float) Math.toDegrees(lonrad);

        if (!((lat >= -90d) && (lat <= 90d))) {
            lat = LatLonPoint.normalize_latitude(lat);
        }
        if (!((lon >= -180d) && (lon <= 180d))) {
            lon = LatLonPoint.wrap_longitude(lon);
        }

        ret.setLatLon(lat, lon);

        return ret;
    }

}
