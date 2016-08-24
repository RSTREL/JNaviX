package com.bbn.openmap.util.coordFormatter;

/**
 * Copyright NAVICON A/S
 * com@navicon.dk
 *
 * Formats a string to represent DMS for lat/lon information.
 */
import java.text.NumberFormat;

import com.bbn.openmap.LatLonPoint;

public class DMSCoordInfoFormatter extends BasicCoordInfoFormatter {

    public DMSCoordInfoFormatter() {}

    public String createCoordinateInformationLine(int x, int y,
                                                  LatLonPoint llp, Object source) {
        if (llp != null) {
            return "Cursor Position (" + formatLatitude(llp.getLatitude())
                    + ", " + formatLongitude(llp.getLongitude()) + ")";
        } else {
            return "Lat, Lon (" + "?" + ", " + "?" + ")";
        }
    }

    public static String formatLatitude(float latitude) {
        return formatDegreesMinutes(latitude, 2, latitude < 0 ? "S" : "N");
    }

    public static String formatLongitude(float longitude) {
        return formatDegreesMinutes(longitude, 3, longitude < 0 ? "W" : "E");
    }

    public static String formatDegreesMinutes(double value, int integerDigits,
                                              String semisphere) {
        double valueAbs = Math.abs(value);
        int degrees = (int) valueAbs;
        double minutes = (valueAbs - degrees) * 60.0;

        NumberFormat nf = NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        nf.setMinimumIntegerDigits(integerDigits);
        nf.setMaximumIntegerDigits(integerDigits);
        String strDegrees = nf.format(degrees);
        nf.setMinimumIntegerDigits(2);
        nf.setMaximumIntegerDigits(2);
        nf.setMinimumFractionDigits(3);
        nf.setMaximumFractionDigits(3);
        String strMinutes = nf.format(minutes);
        return strDegrees + DEGREE_SIGN + strMinutes + "'" + semisphere;
    }
}
