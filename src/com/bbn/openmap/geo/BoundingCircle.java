// **********************************************************************
//
//<copyright>
//
//BBN Technologies, a Verizon Company
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
//$RCSfile: BoundingCircle.java,v $
//$Revision: 1.2.2.8 $
//$Date: 2007/02/13 20:00:54 $
//$Author: dietrick $
//
//**********************************************************************

package com.bbn.openmap.geo;


/**
 * Interface that describes a bounding circle around a given point on
 * a sphere.
 */
public interface BoundingCircle {

    /**
     * Returns a Geo representing the center of the circle.
     * 
     * @return Geo
     */
    public Geo getCenter();

    /**
     * @return the radius of the bounding circle in radians.
     */
    public double getRadius();
    
    /**
     * @return whether this bounding circle intersects with another.
     */
    public boolean intersects(BoundingCircle bc);
    
    /**
     * @return whether this bounding circle intersects with a Geo with a radius (radians) around it.
     */
    public boolean intersects(Geo g, double radius);

    /**
     * A simple implementation of BoundingCircle, storing the center
     * and radius as data members.
     */
    public static class Impl implements BoundingCircle {
        private Geo center; // center of circle
        private double radius; // angle (in radians) to edge

        public Impl(Geo center, double radius) {
            init(center, radius);
        }

        public Impl(GeoArray arrayPoints) {
            init(arrayPoints);
        }

        public Impl(GeoPath path) {
            init(path.getPoints());
        }
        
        public Impl(Geo[] gs) {
            init(gs);
        }
        
        /**
         * Works by computing the centroid, then finding the
         * largest radius. This will not, in general, produce the
         * minimal bounding circle.
         */
        protected void init(Geo[] region) {
            Geo c = Intersection.center(region); // centroid

            double r = 0.0;
            int length = region.length;
            for (int i = 0; i < length; i++) {
                double pr = c.distance(region[i]);
                if (pr > r) {
                    r = pr;
                }
            }

            init(c, r);
        }
        
        /**
         * Works by computing the centroid, then finding the
         * largest radius. This will not, in general, produce the
         * minimal bounding circle.
         */
        protected void init(GeoArray region) {
            Geo c = Intersection.center(region, new Geo()); // centroid
            Geo storage = new Geo();
            double r = 0.0;
            int length = region.getSize();
            for (int i = 0; i < length; i++) {
                double pr = c.distance(region.get(i, storage));
                if (pr > r) {
                    r = pr;
                }
            }

            init(c, r);
        }

        protected void init(Geo center, double radius) {
            this.center = center;
            this.radius = radius;
        }

        public final Geo getCenter() {
            return center;
        }

        public final double getRadius() {
            return radius;
        }
        
        public boolean intersects(BoundingCircle bc) {
            return intersects(bc.getCenter(), bc.getRadius());
        }
        
        public boolean intersects(Geo g, double r) {
            return this.center.distance(g) <= (getRadius() + r);
        }
        
        public String toString() {
            return "BoundingCircle: center(" + center + ") with radius (" + radius + ")";
        }
    }
}