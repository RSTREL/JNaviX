// **********************************************************************
//
// <copyright>
//
//  BBN Technologies
//  10 Moulton Street
//  Cambridge, MA 02138
//  (617) 873-8000
//
//  Copyright (C) BBNT Solutions LLC. All rights reserved.
//
// </copyright>
// **********************************************************************
//
// $Source: /cvs/distapps/openmap/src/openmap/com/bbn/openmap/omGraphics/OMPoly.java,v $
// $RCSfile: OMPoly.java,v $
// $Revision: 1.10.2.7 $
// $Date: 2005/08/11 21:03:22 $
// $Author: dietrick $
//
// **********************************************************************

package com.bbn.openmap.omGraphics;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.io.Serializable;
import java.util.ArrayList;

import com.bbn.openmap.proj.DrawUtil;
import com.bbn.openmap.proj.ProjMath;
import com.bbn.openmap.proj.Projection;
import com.bbn.openmap.util.Debug;

/**
 * Graphic object that represents a polygon or polyline
 * (multi-line-segment-object).
 * <p>
 * 
 * The differentiator between polygons and polylines is the fill
 * color. If the fillPaint is equal to OMColor.clear, then the poly
 * will be considered a polyline. There are methods to override this
 * in the OMPoly class, but they do play around with the fillPaint,
 * depending on the order in which the methods are called. If you know
 * it's a polyline, call setIsPolygon(false) if you think that the
 * fillPaint could be set to anything other than the default,
 * OMColor.clear.
 * <P>
 * 
 * All of the OMGraphics are moving to having their internal
 * representation as java.awt.Shape objects. Unfortunately, this has
 * the side effect of slowing OMPolys down, because the way that the
 * projection classes handle transformations cause more objects to be
 * allocated and more loops to be run through. So, by default, the
 * OMPoly does NOT use Shape objects internally, to keep layers that
 * throw down many, many polys running quickly. If you want to do some
 * spatial analysis on an OMPoly, call setDoShapes(true) on it, then
 * generate(Projection), and then call getShapes() to get the
 * java.awt.Shape objects for the poly. You can then run the different
 * Shape spatial analysis methods on the Shape objects.
 * 
 * <h3>NOTES:</h3>
 * <ul>
 * <li>See the <a
 * href="../../../../com.bbn.openmap.proj.Projection.html#poly_restrictions">
 * RESTRICTIONS </a> on Lat/Lon polygons/polylines. Not following the
 * guidelines listed may result in ambiguous/undefined shapes! Similar
 * assumptions apply to the other vector graphics that we define:
 * circles, ellipses, rects, lines.
 * <li>LatLon OMPolys store latlon coordinates internally in radian
 * format for efficiency in projecting. Subclasses should follow this
 * model.
 * <li>Holes in the poly are not supported.
 * <p>
 * </ul>
 * <h3>TODO:</h3>
 * <ul>
 * <li>Polar filled-polygon correction for Cylindrical projections
 * (like OMCircle).
 * </ul>
 * 
 * @see OMCircle
 * @see OMRect
 * @see OMLine
 */
public class OMPoly extends OMAbstractLine implements Serializable {

    /**
     * Translation offsets. For RENDERTYPE_OFFSET, the xy points are
     * relative to the position of fixed latlon point.
     */
    public final static int COORDMODE_ORIGIN = 0;

    /**
     * Delta offsets. For RENDERTYPE_OFFSET, each xy point in the
     * array is relative to the previous point, and the first point is
     * relative to the fixed latlon point.
     */
    public final static int COORDMODE_PREVIOUS = 1;

    /**
     * Radians or decimal degrees. After construction and conversion,
     * this should always be radians.
     */
    protected int units = -1;// this should be set correctly at
    // construction

    /**
     * For RENDERTYPE_OFFSET, the latitude of the starting point of
     * the poly. Stored as radians!
     */
    protected float lat = 0.0f;

    /**
     * For RENDERTYPE_OFFSET, the longitude of the starting point of
     * the poly. Stored as radians!
     */
    protected float lon = 0.0f;

    /**
     * For RENDERTYPE_OFFSET, type of offset.
     * 
     * @see #COORDMODE_ORIGIN
     * @see #COORDMODE_PREVIOUS
     */
    protected int coordMode = COORDMODE_ORIGIN;

    /**
     * The x array of ints, representing pixels, used for x/y or
     * offset polys.
     */
    protected int[] xs = null;

    /**
     * The y array of ints, representing pixels, used for x/y or
     * offset polys.
     */
    protected int[] ys = null;

    /**
     * Poly is a polygon or a polyline. This is true if the fillColor
     * is not clear, false if it is.
     */
    protected boolean isPolygon = false;

    /** raw float lats and lons stored internally in radians. */
    protected float[] rawllpts = null;

    /**
     * Flag for telling the OMPoly to use the Shape objects to
     * represent itself internally. See intro for more info.
     */
    protected boolean doShapes = false;

    /**
     * Construct a default OMPoly.
     */
    public OMPoly() {
        super(RENDERTYPE_UNKNOWN, LINETYPE_UNKNOWN, DECLUTTERTYPE_NONE);
    }

    /**
     * Create an OMPoly from a list of float lat/lon pairs.
     * <p>
     * NOTES:
     * <ul>
     * <li>llPoints array is converted into radians IN PLACE for more
     * efficient handling internally if it's not already in radians!
     * For even better performance, you should send us an array
     * already in radians format!
     * <li>If you want the poly to be connected (as a polygon), you
     * need to ensure that the first and last coordinate pairs are the
     * same.
     * </ul>
     * 
     * @param llPoints array of lat/lon points, arranged lat, lon,
     *        lat, lon, etc.
     * @param units radians or decimal degrees. Use OMGraphic.RADIANS
     *        or OMGraphic.DECIMAL_DEGREES
     * @param lType line type, from a list defined in OMGraphic.
     */
    public OMPoly(float[] llPoints, int units, int lType) {
        this(llPoints, units, lType, -1);
    }

    /**
     * Create an OMPoly from a list of float lat/lon pairs.
     * <p>
     * NOTES:
     * <ul>
     * <li>llPoints array is converted into radians IN PLACE for more
     * efficient handling internally if it's not already in radians!
     * For even better performance, you should send us an array
     * already in radians format!
     * <li>If you want the poly to be connected (as a polygon), you
     * need to ensure that the first and last coordinate pairs are the
     * same.
     * </ul>
     * 
     * @param llPoints array of lat/lon points, arranged lat, lon,
     *        lat, lon, etc.
     * @param units radians or decimal degrees. Use OMGraphic.RADIANS
     *        or OMGraphic.DECIMAL_DEGREES
     * @param lType line type, from a list defined in OMGraphic.
     * @param nsegs number of segment points (only for
     *        LINETYPE_GREATCIRCLE or LINETYPE_RHUMB line types, and
     *        if &lt; 1, this value is generated internally)
     */
    public OMPoly(float[] llPoints, int units, int lType, int nsegs) {
        super(RENDERTYPE_LATLON, lType, DECLUTTERTYPE_NONE);
        setLocation(llPoints, units);
        this.nsegs = nsegs;
    }

    /**
     * Create an OMPoly from a list of xy pairs. If you want the poly
     * to be connected, you need to ensure that the first and last
     * coordinate pairs are the same.
     * 
     * @param xypoints array of x/y points, arranged x, y, x, y, etc.
     */
    public OMPoly(int[] xypoints) {
        super(RENDERTYPE_XY, LINETYPE_UNKNOWN, DECLUTTERTYPE_NONE);
        setLocation(xypoints);
    }

    /**
     * Create an x/y OMPoly. If you want the poly to be connected, you
     * need to ensure that the first and last coordinate pairs are the
     * same.
     * 
     * @param xPoints int[] of x coordinates
     * @param yPoints int[] of y coordinates
     */
    public OMPoly(int[] xPoints, int[] yPoints) {
        super(RENDERTYPE_XY, LINETYPE_UNKNOWN, DECLUTTERTYPE_NONE);

        setLocation(xPoints, yPoints);
    }

    /**
     * Create an x/y OMPoly at an offset from lat/lon. If you want the
     * poly to be connected, you need to ensure that the first and
     * last coordinate pairs are the same.
     * 
     * @param latPoint latitude in decimal degrees
     * @param lonPoint longitude in decimal degrees
     * @param xypoints int[] of x,y pairs
     * @param cMode offset coordinate mode
     */
    public OMPoly(float latPoint, float lonPoint, int[] xypoints, int cMode) {
        super(RENDERTYPE_OFFSET, LINETYPE_UNKNOWN, DECLUTTERTYPE_NONE);

        setLocation(latPoint, lonPoint, OMGraphic.DECIMAL_DEGREES, xypoints);
        coordMode = cMode;
    }

    /**
     * Create an x/y OMPoly at an offset from lat/lon. If you want the
     * poly to be connected, you need to ensure that the first and
     * last coordinate pairs are the same.
     * 
     * @param latPoint latitude in decimal degrees
     * @param lonPoint longitude in decimal degrees
     * @param xPoints int[] of x coordinates
     * @param yPoints int[] of y coordinates
     * @param cMode offset coordinate mode
     */
    public OMPoly(float latPoint, float lonPoint, int[] xPoints, int[] yPoints,
            int cMode) {
        super(RENDERTYPE_OFFSET, LINETYPE_UNKNOWN, DECLUTTERTYPE_NONE);

        setLocation(latPoint,
                lonPoint,
                OMGraphic.DECIMAL_DEGREES,
                xPoints,
                yPoints);
        coordMode = cMode;
    }

    /**
     * Set an OMPoly from a list of float lat/lon pairs.
     * <p>
     * NOTES:
     * <ul>
     * <li>llPoints array is converted into radians IN PLACE for more
     * efficient handling internally if it's not already in radians!
     * If you don't want the array to be changed, send in a copy.
     * <li>If you want the poly to be connected (as a polygon), you
     * need to ensure that the first and last coordinate pairs are the
     * same.
     * </ul>
     * This is for RENDERTYPE_LATLON polys.
     * 
     * @param llPoints array of lat/lon points, arranged lat, lon,
     *        lat, lon, etc.
     * @param units radians or decimal degrees. Use OMGraphic.RADIANS
     *        or OMGraphic.DECIMAL_DEGREES
     */
    public void setLocation(float[] llPoints, int units) {
        this.units = OMGraphic.RADIANS;
        if (units == OMGraphic.DECIMAL_DEGREES) {
            ProjMath.arrayDegToRad(llPoints);
        }
        rawllpts = llPoints;
        setNeedToRegenerate(true);
        setRenderType(RENDERTYPE_LATLON);
    }

    /**
     * Set an OMPoly from a list of xy pixel pairs. If you want the
     * poly to be connected, you need to ensure that the first and
     * last coordinate pairs are the same. This is for RENDERTYPE_XY
     * polys.
     * 
     * @param xypoints array of x/y points, arranged x, y, x, y, etc.
     */
    public void setLocation(int[] xypoints) {
        int end = xypoints.length >> 1;
        xs = new int[end];
        ys = new int[end];
        for (int i = 0, j = 0; i < end; i++, j += 2) {
            xs[i] = xypoints[j];
            ys[i] = xypoints[j + 1];
        }
        setNeedToRegenerate(true);
        setRenderType(RENDERTYPE_XY);
    }

    /**
     * Set an OMPoly from a x/y coordinates. If you want the poly to
     * be connected, you need to ensure that the first and last
     * coordinate pairs are the same. This is for RENDERTYPE_XY polys.
     * 
     * @param xPoints int[] of x coordinates
     * @param yPoints int[] of y coordinates
     */
    public void setLocation(int[] xPoints, int[] yPoints) {
        xs = xPoints;
        ys = yPoints;
        setNeedToRegenerate(true);
        setRenderType(RENDERTYPE_XY);
    }

    /**
     * Set the location based on a latitude, longitude, and some xy
     * points. The coordinate mode and the polygon setting are the
     * same as in the constructor used. This is for RENDERTYPE_OFFSET
     * polys.
     * 
     * @param latPoint latitude in decimal degrees
     * @param lonPoint longitude in decimal degrees
     * @param units radians or decimal degrees. Use OMGraphic.RADIANS
     *        or OMGraphic.DECIMAL_DEGREES
     * @param xypoints array of x/y points, arranged x, y, x, y, etc.
     */
    public void setLocation(float latPoint, float lonPoint, int units,
                            int[] xypoints) {
        this.units = OMGraphic.RADIANS;
        if (units == OMGraphic.DECIMAL_DEGREES) {
            lat = ProjMath.degToRad(latPoint);
            lon = ProjMath.degToRad(lonPoint);
        } else {
            lat = latPoint;
            lon = lonPoint;
        }
        int end = xypoints.length >> 1;
        xs = new int[end];
        ys = new int[end];
        for (int i = 0, j = 0; i < end; i++, j += 2) {
            xs[i] = xypoints[j];
            ys[i] = xypoints[j + 1];
        }
        setNeedToRegenerate(true);
        setRenderType(RENDERTYPE_OFFSET);
    }

    /**
     * Set the location based on a latitude, longitude, and some xy
     * points. The coordinate mode and the polygon setting are the
     * same as in the constructor used. This is for RENDERTYPE_OFFSET
     * polys.
     * 
     * @param latPoint latitude in decimal degrees
     * @param lonPoint longitude in decimal degrees
     * @param units radians or decimal degrees. Use OMGraphic.RADIANS
     *        or OMGraphic.DECIMAL_DEGREES
     * @param xPoints int[] of x coordinates
     * @param yPoints int[] of y coordinates
     */
    public void setLocation(float latPoint, float lonPoint, int units,
                            int[] xPoints, int[] yPoints) {
        this.units = OMGraphic.RADIANS;
        if (units == OMGraphic.DECIMAL_DEGREES) {
            lat = ProjMath.degToRad(latPoint);
            lon = ProjMath.degToRad(lonPoint);
        } else {
            lat = latPoint;
            lon = lonPoint;
        }
        xs = xPoints;
        ys = yPoints;
        setNeedToRegenerate(true);
        setRenderType(RENDERTYPE_OFFSET);
    }

    /**
     * Return the rawllpts array. NOTE: this is an unsafe method to
     * access the rawllpts array. Use with caution. These are RADIANS!
     * 
     * @return float[] rawllpts of lat, lon, lat, lon
     */
    public float[] getLatLonArray() {
        return rawllpts;
    }

    /**
     * Set the latitude of the offset point, in decimal degrees. For
     * RENDERTYPE_OFFSET Polygons.
     */
    public void setLat(float lat) {
        this.lat = ProjMath.degToRad(lat);
        setNeedToRegenerate(true);
    }

    /**
     * Get the latitude of the offset point, in decimal degrees. For
     * RENDERTYPE_OFFSET Polygons.
     */
    public float getLat() {
        return ProjMath.radToDeg(lat);
    }

    /**
     * Set the longitude of the offset point, in decimal degrees. For
     * RENDERTYPE_OFFSET Polygons.
     */
    public void setLon(float lon) {
        this.lon = ProjMath.degToRad(lon);
        setNeedToRegenerate(true);
    }

    /**
     * Get the longitude of the offset point, in decimal degrees. For
     * RENDERTYPE_OFFSET Polygons.
     */
    public float getLon() {
        return ProjMath.radToDeg(lon);
    }

    /**
     * Set the array of x points. For RENDERTYPE_OFFSET, RENDERTYPE_XY
     * polys.
     */
    public void setXs(int[] x) {
        xs = x;
        setNeedToRegenerate(true);
    }

    /**
     * Get the array of x points. For RENDERTYPE_OFFSET, RENDERTYPE_XY
     * polys.
     */
    public int[] getXs() {
        return xs;
    }

    /**
     * Set the array of y points. For RENDERTYPE_OFFSET, RENDERTYPE_XY
     * polys.
     */
    public void setYs(int[] y) {
        ys = y;
        setNeedToRegenerate(true);
    }

    /**
     * Get the array of y points. For RENDERTYPE_OFFSET, RENDERTYPE_XY
     * polys.
     */
    public int[] getYs() {
        return ys;
    }

    /**
     * Set the fill Paint of the poly. If the color value is
     * non-clear, then the poly is a polygon (connected and filled),
     * otherwise it's a polyline (non-filled).
     * 
     * @param paint value Color
     */
    public void setFillPaint(Paint paint) {
        super.setFillPaint(paint);
        isPolygon = !isClear(paint);
    }

    /**
     * Check if this is a polygon or a polyline. A polygon is a
     * multi-segment line that has a non-clear fill color. A polyline
     * is a multi-segment line that has no fill color.
     * 
     * @return true if polygon false if polyline
     */
    public boolean isPolygon() {
        return isPolygon;
    }

    /**
     * Set the Polyline/Polygon setting, if you know better. If the
     * fillPaint is set after this method is called, then the
     * fillPaint isPolygon rules apply. If the fillPaint is opaque,
     * then it is assumed to be a Polygon and isPolygon will be set to
     * true. If this is set to be false, the fillPaint will be set to
     * clear.
     */
    public void setIsPolygon(boolean set) {
        if (!set) {
            // This is important for the rendering, especially if the
            // shapes are being created and OMGraphic.render() will be
            // used. The fillPaint being == OMColor.clear will
            // prevent the filled area from being drawn.
            fillPaint = OMColor.clear;
        }
        isPolygon = set;
    }

    /**
     * Set the number of subsegments for each segment in the poly.
     * (This is only for LINETYPE_GREATCIRCLE or LINETYPE_RHUMB line
     * types, and if &lt; 1, this value is generated internally).
     * 
     * @param nsegs number of segment points
     */
    public void setNumSegs(int nsegs) {
        this.nsegs = nsegs;
    }

    /**
     * Get the number of subsegments for each segment in the poly.
     * (This is only for LINETYPE_GREATCIRCLE or LINETYPE_RHUMB line
     * types).
     * 
     * @return int number of segment points
     */
    public int getNumSegs() {
        return nsegs;
    }

    /**
     * For RENDERTYPE_OFFSET, type of offset.
     * 
     * @see #COORDMODE_ORIGIN
     * @see #COORDMODE_PREVIOUS
     */
    public void setCoordMode(int coordMode) {
        this.coordMode = coordMode;
    }

    /**
     * For RENDERTYPE_OFFSET, type of offset.
     * 
     * @see #COORDMODE_ORIGIN
     * @see #COORDMODE_PREVIOUS
     */
    public int getCoordMode() {
        return coordMode;
    }

    public void setDoShapes(boolean set) {
        doShapes = set;
    }

    public boolean getDoShapes() {
        return doShapes;
    }

    /**
     * Prepare the poly for rendering.
     * 
     * @param proj Projection
     * @return true if generate was successful
     */
    public boolean generate(Projection proj) {
        int i, j, npts;
        setShape(null);
        setNeedToRegenerate(true);

        if (proj == null) {
            Debug.message("omgraphic", "OMPoly: null projection in generate!");
            return false;
        }

        // answer the question now, saving calcuation for future
        // calculations. The set method forces the calculation for
        // the query.

        isGeometryClosed();

        switch (renderType) {

        case RENDERTYPE_XY:
            if (xs == null) {
                Debug.message("omgraphic",
                        "OMPoly x/y rendertype null coordinates");
                return false;
            }

            // Need to keep these around for the LabeledOMPoly
            xpoints = new int[1][0];
            xpoints[0] = xs;
            ypoints = new int[1][0];
            ypoints[0] = ys;

            break;

        case RENDERTYPE_OFFSET:
            if (xs == null) {
                Debug.message("omgraphic",
                        "OMPoly offset rendertype null coordinates");
                return false;
            }

            npts = xs.length;
            int[] _x = new int[npts];
            int[] _y = new int[npts];

            // forward project the radian point
            Point origin = proj.forward(lat, lon, new Point(0, 0), true);// radians

            if (coordMode == COORDMODE_ORIGIN) {
                for (i = 0; i < npts; i++) {
                    _x[i] = xs[i] + origin.x;
                    _y[i] = ys[i] + origin.y;
                }
            } else { // CModePrevious offset deltas
                _x[0] = xs[0] + origin.x;
                _y[0] = ys[0] + origin.y;

                for (i = 1; i < npts; i++) {
                    _x[i] = xs[i] + _x[i - 1];
                    _y[i] = ys[i] + _y[i - 1];
                }
            }
            // Need to keep these around for the LabeledOMPoly
            xpoints = new int[1][0];
            xpoints[0] = _x;
            ypoints = new int[1][0];
            ypoints[0] = _y;

            break;

        case RENDERTYPE_LATLON:
            // polygon/polyline project the polygon/polyline.
            // Vertices should already be in radians.
            ArrayList vector = proj.forwardPoly(rawllpts,
                    lineType,
                    nsegs,
                    isPolygon);
            int size = vector.size();

            xpoints = new int[(int) (size / 2)][0];
            ypoints = new int[xpoints.length][0];

            for (i = 0, j = 0; i < size; i += 2, j++) {
                xpoints[j] = (int[]) vector.get(i);
                ypoints[j] = (int[]) vector.get(i + 1);
            }

            if (!doShapes) {
                if (size > 1) {
                    if (arrowhead != null) {
                        arrowhead.generate(this);
                    }
                    setNeedToRegenerate(false);
                    initLabelingDuringGenerate();
                    if (checkPoints(xpoints, ypoints)) {
                        setLabelLocation(xpoints[0], ypoints[0]);
                    }
                    return true;
                } else {
                    return false;
                }
            }

            break;

        case RENDERTYPE_UNKNOWN:
            Debug.error("OMPoly.generate: invalid RenderType");
            return false;
        }

        if (arrowhead != null) {
            arrowhead.generate(this);
        }
        
        setNeedToRegenerate(false);
        createShape();
        return true;
    }

    /**
     * Return true if the xpoints and ypoints are not null and contain
     * coordinates.
     * 
     * @param xpoints2
     * @param ypoints2
     */
    protected boolean checkPoints(int[][] xpoints2, int[][] ypoints2) {
        if (xpoints == null || ypoints == null || xpoints.length == 0
                || ypoints.length == 0) {
            return false;
        }
        return true;
    }

    /**
     * Return true of the fill color/paint should be rendered (not
     * clear).
     */
    public boolean shouldRenderFill() {
        return !isClear(getFillPaint()) && isPolygon();
    }

    /**
     * Paint the poly. This works if generate() has been successful.
     * 
     * @param g java.awt.Graphics to paint the poly onto.
     */
    public void render(Graphics g) {
        if (shape != null) {
            super.render(g);

            if (arrowhead != null) {
                arrowhead.render(g);
            }

            return;
        }

        if (getNeedToRegenerate() || !isVisible())
            return;

        // safety: grab local reference of projected points
        int[][] xpts = xpoints;
        int[][] ypts = ypoints;

        if (xpts == null || ypts == null) {
            // Shouldn't get here, but crazy EditableOMPoly events
            // sometimes cause this to happen. Catch and wait to
            // paint later.
            setNeedToRegenerate(true);
            return;
        }

        int[] _x, _y;
        int i;
        int len = xpts.length;

        Paint displayPaint = getDisplayPaint();
        Paint fillPaint = getFillPaint();
        boolean isFillClear = isClear(fillPaint);
        boolean isLineClear = isClear(displayPaint);

        Paint tm = getTextureMask();

        // If shapes are null, then we have to do things the old way.
        try {
            for (i = 0; i < len; i++) {
                _x = xpts[i];
                _y = ypts[i];

                if (_x == null || _y == null) {
                    continue;
                }

                // render polygon
                if (isPolygon) {

                    // fill main polygon

                    if (!isFillClear) {
                        // set the interior coloring parameters
                        setGraphicsForFill(g);
                        g.fillPolygon(_x, _y, _x.length);

                        if (tm != null && tm != fillPaint) {
                            setGraphicsColor(g, tm);
                            g.fillPolygon(_x, _y, _x.length);
                        }
                    }

                    // only draw outline if different color or matted
                    if (matted || !isLineClear || !edgeMatchesFill) {

                        if (matted) {
                            if (g instanceof Graphics2D
                                    && stroke instanceof BasicStroke) {
                                ((Graphics2D) g).setStroke(new BasicStroke(((BasicStroke) stroke).getLineWidth() + 2f));
                                setGraphicsColor(g, mattingPaint);
                                g.drawPolyline(_x, _y, _x.length);
                            }
                        }

                        setGraphicsForEdge(g);
                        // for some reason, this used to be
                        // drawPolygon
                        g.drawPolygon(_x, _y, _x.length);
                    }
                }

                // render polyline
                else {

                    if (matted) {
                        if (g instanceof Graphics2D
                                && stroke instanceof BasicStroke) {
                            ((Graphics2D) g).setStroke(new BasicStroke(((BasicStroke) stroke).getLineWidth() + 2f));
                            // Just to draw the matting for the
                            // arrowhead.
                            if (arrowhead != null) {
                                setGraphicsColor(g, mattingPaint);
                                arrowhead.render(g);
                            }
                            setGraphicsColor(g, mattingPaint);
                            g.drawPolyline(_x, _y, _x.length);
                        }
                    }

                    // draw main outline
                    setGraphicsForEdge(g);
                    g.drawPolyline(_x, _y, _x.length);

                    if (arrowhead != null) {
                        arrowhead.render(g);
                    }
                }
            }

            renderLabel(g);

        } catch (Exception e) {
            // Trying to catch any clipping problems from within a JRE
            Debug.output("OMPoly: caught Java rendering exception\n"
                    + e.getMessage());
            if (Debug.debugging("ompoly")) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Return the shortest distance from the graphic to an XY-point.
     * This works if generate() has been successful.
     * 
     * @param x horizontal pixel location.
     * @param y vertical pixel location.
     * @return the distance of the object to the location given.
     */
    public float distance(int x, int y) {
        if (shape != null) {
            return super.distance(x, y);
        }

        // If shape is null, then we have to do things the old way.

        float temp, distance = Float.POSITIVE_INFINITY;

        if (getNeedToRegenerate()) {
            return distance;
        }

        // safety: grab local reference of projected points
        int[][] xpts = xpoints;
        int[][] ypts = ypoints;
        int[] _x, _y;
        int len = xpts.length;

        for (int i = 0; i < len; i++) {
            _x = xpts[i];
            _y = ypts[i];

            // check if point inside polygon
            if (isPolygon && DrawUtil.inside_polygon(_x, _y, x, y))
                return 0f; // close as can be

            // get the closest point
            temp = DrawUtil.closestPolyDistance(_x, _y, x, y, false);
            if (temp < distance)
                distance = temp;
        }

        return normalizeDistanceForLineWidth(distance);
    }

    /**
     * Get the array of java.awt.Shape objects that represent the
     * projected graphic. The array will contain more than one Shape
     * object of the object wraps around the earth and needs to show
     * up in more than one place on the map.
     * <p>
     * 
     * The java.awt.Shape object gives you the ability to do a little
     * spatial analysis on the graphics.
     * 
     * @return java.awt.geom.GeneralPath (Shape), or null if the
     *         graphic needs to be generated with the current map
     *         projection, or null if the OMGeometry hasn't been
     *         updated to use Shape objects for its internal
     *         representation.
     */
    public GeneralPath getShape() {
        if (shape == null) {
            // Since polygons have the option of not creating shape
            // objects, should create one if asked.
            createShape();
        }
        return shape;
    }

    /**
     * Since OMPoly has the option to not create a Shape, this method
     * is here to create it if it is asked for. The OMPoly needs to be
     * generated.
     */
    protected void createShape() {

        if (getNeedToRegenerate() || !checkPoints(xpoints, ypoints)) {
            return;
        }

        initLabelingDuringGenerate();

        switch (renderType) {

        case RENDERTYPE_XY:
        case RENDERTYPE_OFFSET:
            shape = createShape(xpoints[0], ypoints[0], isPolygon);
            break;
        case RENDERTYPE_LATLON:
            int size = xpoints.length;

            for (int i = 0; i < size; i++) {
                GeneralPath gp = createShape(xpoints[i], ypoints[i], isPolygon);

                if (shape == null) {
                    shape = gp;
                } else {
                    ((GeneralPath) shape).append(gp, false);
                }
            }

            break;

        default:
        }

        setLabelLocation(xpoints[0], ypoints[0]);
    }

    protected boolean geometryClosed = false;

    /**
     * Is the geometry closed ?
     * 
     * @return boolean
     */
    protected boolean isGeometryClosed() {
        geometryClosed = false;
        switch (renderType) {
        case RENDERTYPE_XY:
        case RENDERTYPE_OFFSET:
            if (xs != null && xs.length > 2) {
                geometryClosed = (xs[0] == xs[xs.length - 1] && ys[0] == ys[ys.length - 1]);
            }
            break;
        case RENDERTYPE_LATLON:
            if (rawllpts != null) {
                int l = rawllpts.length;
                if (l > 4) {
                    geometryClosed = (Math.abs(rawllpts[0] - rawllpts[l - 2]) < 1e-5 && Math.abs(rawllpts[1]
                            - rawllpts[l - 1]) < 1e-5);
                }
            }
            break;
        case RENDERTYPE_UNKNOWN:
            Debug.error("OMPoly.generate: invalid RenderType");
            break;
        }

        return geometryClosed;
    }

    /** For XMLEncoder */
    public float[] getRawllpts() {
        return this.rawllpts;
    }

    /** For XMLEncoder */
    public int getUnits() {
        return this.units;
    }

}