package com.bbn.openmap.proj;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.util.ArrayList;

import com.bbn.openmap.LatLonPoint;

/**
 * A Projection that wraps another projection, but stretch the image to another
 * aspect ratio.
 * <p>
 * The motivation for this projection is to support the following clause in wms
 * 1.1.1.
 * <p>
 * OGC 01-068r3 (wms 1.1.1) 7.2.3.8. "In the case where the aspect ratio of the
 * BBOX and the ratio width/height are different, the WMS shall stretch the
 * returned map so that the resulting pixels could themselves be rendered in the
 * aspect ratio of the BBOX"
 * 
 * @author halset
 */
public class AspectRatioProjection extends Proj {

    private Proj wrappedProjection;

    private float xFactor;

    private float yFactor;

    private float halfWidth;

    private float halfWrappedWidth;

    private float halfHeight;

    private float halfWrappedHeight;

    private boolean xHasFactor;

    private boolean yHasFactor;

    /**
     * Constructor that takes a projection and the new width/height.
     * 
     * @param proj a projection to wrap
     * @param w a int with the new width
     * @param h a int with the new height
     */
    public AspectRatioProjection(Proj proj, int w, int h) {
        super(proj.getCenter(), proj.getScale(), w, h, 199);
        wrappedProjection = proj;
        xHasFactor = proj.getWidth() != w;
        yHasFactor = proj.getHeight() != h;
        computeParameters();
    }

    private Point fromWrapped(Point pt) {
        pt.x = xFromWrapped(pt.x);
        pt.y = yFromWrapped(pt.y);
        return pt;
    }

    private int xFromWrapped(int x) {
        if (!xHasFactor)
            return x;
        return (int) (((float) x - halfWrappedWidth) * xFactor + halfWidth);
    }

    private int yFromWrapped(int y) {
        if (!yHasFactor)
            return y;
        return (int) (((float) y - halfWrappedHeight) * yFactor + halfHeight);
    }

    private int xToWrapped(int x) {
        if (!xHasFactor)
            return x;
        return (int) (((float) x - halfWidth) / xFactor + halfWrappedWidth);
    }

    private int yToWrapped(int y) {
        if (!yHasFactor)
            return y;
        return (int) (((float) y - halfHeight) / yFactor + halfWrappedHeight);
    }

    protected ArrayList _forwardPoly(float[] rawllpts, int ltype, int nsegs, boolean isFilled) {
        ArrayList stuff = wrappedProjection._forwardPoly(rawllpts, ltype, nsegs, isFilled);
        int size = stuff.size();
        for (int i = 0; i < size; i += 2) {
            int[] xpts = (int[]) stuff.get(i);
            if (xHasFactor) {
                for (int j = 0; j < xpts.length; j++)
                    xpts[j] = xFromWrapped(xpts[j]);
            }
            if (yHasFactor) {
                int[] ypts = (int[]) stuff.get(i + 1);
                for (int j = 0; j < ypts.length; j++)
                    ypts[j] = yFromWrapped(ypts[j]);
            }
        }
        return stuff;
    }

    protected void computeParameters() {
        if (wrappedProjection != null) {
            wrappedProjection.computeParameters();
            xFactor = (float) ((double) getWidth() / (double) wrappedProjection.getWidth());
            yFactor = (float) ((double) getHeight() / (double) wrappedProjection.getHeight());
            halfWidth = (float) getWidth() / 2.0F;
            halfHeight = (float) getHeight() / 2.0F;
            halfWrappedWidth = (float) wrappedProjection.getWidth() / 2.0F;
            halfWrappedHeight = (float) wrappedProjection.getHeight() / 2.0F;
        }
    }

    public void drawBackground(Graphics2D g, Paint p) {
        wrappedProjection.drawBackground(g, p);
    }

    public void drawBackground(Graphics g) {
        wrappedProjection.drawBackground(g);
    }

    public float normalize_latitude(float lat) {
        if (wrappedProjection == null) {
            if (lat > NORTH_POLE) {
                return NORTH_POLE;
            } else if (lat < SOUTH_POLE) {
                return SOUTH_POLE;
            }
            return lat;
        }
        return wrappedProjection.normalize_latitude(lat);
    }

    public Point forward(LatLonPoint llp, Point pt) {
        return fromWrapped(wrappedProjection.forward(llp, pt));
    }

    public Point forward(float lat, float lon, Point pt) {
        return fromWrapped(wrappedProjection.forward(lat, lon, pt));
    }

    public Point forward(float lat, float lon, Point pt, boolean isRadian) {
        return fromWrapped(wrappedProjection.forward(lat, lon, pt, isRadian));
    }

    public boolean forwardRaw(float[] rawllpts, int rawoff, int[] xcoords, int[] ycoords,
            boolean[] visible, int copyoff, int copylen) {
        boolean r = wrappedProjection.forwardRaw(rawllpts, rawoff, xcoords, ycoords, visible,
                copyoff, copylen);
        int end = copylen + copyoff;
        for (int i = copyoff; i < end; i++) {
            if (xHasFactor)
                xcoords[i] = xFromWrapped(xcoords[i]);
            if (yHasFactor)
                ycoords[i] = yFromWrapped(ycoords[i]);
        }
        return r;
    }

    public LatLonPoint getLowerRight() {
        return wrappedProjection.getLowerRight();
    }

    public LatLonPoint getUpperLeft() {
        return wrappedProjection.getUpperLeft();
    }

    public LatLonPoint inverse(Point point, LatLonPoint llpt) {
        return inverse(point.x, point.y, llpt);
    }

    public LatLonPoint inverse(int x, int y, LatLonPoint llpt) {
        return wrappedProjection.inverse(xToWrapped(x), yToWrapped(y), llpt);
    }

    public boolean isPlotable(float lat, float lon) {
        return wrappedProjection.isPlotable(lat, lon);
    }
}
