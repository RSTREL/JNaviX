package com.bbn.openmap.proj;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import com.bbn.openmap.LatLonPoint;
import com.bbn.openmap.proj.coords.CoordinateReferenceSystem;
import com.bbn.openmap.proj.coords.DatumShiftGCT;

/**
 * This projection wraps an other projection and adds datum handling. The
 * motivation for this projection is to be able to serve wms clients requesting
 * maps in a datum different from wgs84.
 * <p>
 * The underlying data is supposed to be wgs4. The given {@link DatumShiftGCT}
 * convert this to another datum before using the wrapped projection to
 * calculate the screen coordinates.
 * <p>
 * {@link LatLonPoint} as input or output of any of the methods in this class is
 * in wgs84. Internally, each method will convert datums as needed.
 * <p>
 * A user (like wms) of this projection will use a
 * {@link CoordinateReferenceSystem} to convert coordinates to wgs84 before
 * using this method.
 */
public class DatumShiftProjection extends Proj {

	private Proj wrappedProjection;

	private DatumShiftGCT datum;

	public DatumShiftProjection(Proj proj, DatumShiftGCT datum) {
		super(proj.getCenter(), proj.getScale(), proj.getWidth(), proj
				.getHeight(), 299);
		this.datum = datum;
		this.wrappedProjection = proj;

		setCenter(proj.getCenter());
	}

	public void setCenter(float lat, float lon) {
		super.setCenter(lat, lon);

		Point2D centerInDifferentDatum = datum.forward(lat, lon,
				new Point2D.Double());
		wrappedProjection.setCenter((float) centerInDifferentDatum.getY(),
				(float) centerInDifferentDatum.getX());
	}

	protected ArrayList _forwardPoly(float[] rawllpts, int ltype, int nsegs,
			boolean isFilled) {
		
		float[] rawllptsInDifferentDatum = new float[rawllpts.length];

		Point2D tmpll = new Point2D.Double();

		for (int i = 0; i < rawllpts.length; i += 2) {
			tmpll = datum.forward(Math.toDegrees(rawllpts[i]), Math.toDegrees(rawllpts[i + 1]), tmpll);
			rawllptsInDifferentDatum[i] = (float) Math.toRadians(tmpll.getY());
			rawllptsInDifferentDatum[i + 1] = (float) Math.toRadians(tmpll.getX());
		}

		return wrappedProjection._forwardPoly(rawllptsInDifferentDatum, ltype,
				nsegs, isFilled);
	}

	protected void computeParameters() {
		if (wrappedProjection != null) {

			wrappedProjection.width = width;
			wrappedProjection.height = height;
			wrappedProjection.scale = scale;
			// wrappedProjection.ctrLat = ctrLat;
			// wrappedProjection.ctrLon = ctrLon;

			wrappedProjection.computeParameters();
		}
	}

	public void drawBackground(Graphics2D g, Paint p) {
		wrappedProjection.drawBackground(g, p);
	}

	public void drawBackground(Graphics g) {
		wrappedProjection.drawBackground(g);
	}

	public float normalize_latitude(float lat) {
		if (lat > NORTH_POLE) {
			return NORTH_POLE;
		} else if (lat < SOUTH_POLE) {
			return SOUTH_POLE;
		}
		return lat;
	}

	public Point forward(LatLonPoint llp, Point pt) {
		return forward(llp.getLatitude(), llp.getLongitude(), pt);
	}

	public Point forward(float lat, float lon, Point pt) {
		return forward(lat, lon, pt, false);
	}

	public Point forward(float lat, float lon, Point pt, boolean isRadian) {
		Point2D t;
		if (isRadian) {
			t = datum.forward(Math.toDegrees(lat), Math.toDegrees(lon));
		} else {
			t = datum.forward(lat, lon);
		}
		return wrappedProjection
				.forward((float) t.getY(), (float) t.getX(), pt);
	}

	public boolean forwardRaw(float[] rawllpts, int rawoff, int[] xcoords,
			int[] ycoords, boolean[] visible, int copyoff, int copylen) {

		float[] rawllptsInDifferentDatum = new float[rawllpts.length];

		Point2D tmpll = new Point2D.Double();

		int end = copylen + copyoff;
		for (int i = copyoff, j = rawoff; i < end; i++, j += 2) {
			tmpll = datum.forward(Math.toDegrees(rawllpts[j]), Math.toDegrees(rawllpts[j + 1]), tmpll);
			rawllptsInDifferentDatum[j] = (float) Math.toRadians(tmpll.getY());
			rawllptsInDifferentDatum[j + 1] = (float) Math.toRadians(tmpll.getX());
		}

		return wrappedProjection.forwardRaw(rawllptsInDifferentDatum, rawoff,
				xcoords, ycoords, visible, copyoff, copylen);
	}

	public LatLonPoint getLowerRight() {
		LatLonPoint llp = wrappedProjection.getLowerRight();
		return datum.inverse(llp.getLongitude(), llp.getLatitude());
	}

	public LatLonPoint getUpperLeft() {
		LatLonPoint llp = wrappedProjection.getUpperLeft();
		return datum.inverse(llp.getLongitude(), llp.getLatitude());
	}

	public LatLonPoint inverse(Point point, LatLonPoint llpt) {
		return inverse(point.x, point.y, llpt);
	}

	public LatLonPoint inverse(int x, int y, LatLonPoint llpt) {
		llpt = wrappedProjection.inverse(x, y, llpt);
		return datum.inverse(llpt.getLongitude(), llpt.getLatitude(), llpt);
	}

	public boolean isPlotable(float lat, float lon) {
		Point2D t = datum.forward(lat, lon);
		return wrappedProjection.isPlotable((float) t.getY(), (float) t.getX());
	}

	public float getScale(LatLonPoint ll1, LatLonPoint ll2, Point point1,
			Point point2) {
		return wrappedProjection.getScale(ll1, ll2, point1, point2);
	}

}
