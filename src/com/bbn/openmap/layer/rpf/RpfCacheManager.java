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
// $Source: /cvs/distapps/openmap/src/openmap/com/bbn/openmap/layer/rpf/RpfCacheManager.java,v $
// $RCSfile: RpfCacheManager.java,v $
// $Revision: 1.3.2.1 $
// $Date: 2004/10/14 18:27:14 $
// $Author: dietrick $
// 
// **********************************************************************

package com.bbn.openmap.layer.rpf;

import java.io.Serializable;

import com.bbn.openmap.LatLonPoint;
import com.bbn.openmap.omGraphics.*;
import com.bbn.openmap.proj.CADRG;
import com.bbn.openmap.proj.Projection;
import com.bbn.openmap.util.Debug;

/**
 * The RpfCacheManager is the object you need if you want to retrieve
 * RPF files. You set it up with a RpfFrameProvider, and it gives that
 * provider to the RpfCacheHandler it creates. The RpfCacheManager
 * then handles working with the RpfCacheHandlers to get subframes to
 * display.
 * <P>
 * 
 * RPF data comes with a Table of Contents, which sits at the root of
 * the RPF file system structure and contains information about the
 * frame files. When the RpfFrameProvider gets created, it creates an
 * array of RpfTocHandlers. These Table of Contents readers know how
 * to take a geographic area and figure out which frames and subframes
 * are needed to put on the screen. An array of RpfTocHandlers are
 * needed in case there are many places where there is RPF data.
 * <P>
 * 
 * The RpfCacheManager also manages objects called RpfCacheHandlers.
 * Cache handlers take the information from a frame provider, and
 * create a subframe cache for that zone and map type. The situation
 * gets pretty tricky when the screen has the equator and/or the
 * dateline on it, and a different cache handler is needed for each
 * quadrant of the earth. This situation is relatively rare, though,
 * and the RpfCacheManager automatically checks for these situations
 * and creates the cache handlers needed.
 * <P>
 * There are two calls to the Cache that you need to use. The
 * constructor sets up the cache with the location of the data. The
 * getRectangle() call returns an OMGraphicList of objects to draw,
 * that cover the area asked for.
 */
public class RpfCacheManager implements Serializable {

    /**
     * The size of the smaller caches, when more cachehandlers are
     * needed to cover the equator and the dateline. Lowered from 20
     * to try to conserve memory.
     */
    public final static int SMALL_CACHE_SIZE = 10;
    /** A box is a earth quadrant. */
    public final static int MAX_NUM_BOXES = 4;
    /**
     * The cache handlers needed to cover the screen. Need one for
     * each earth quadrant, and for each RPF directory, in case
     * coverage is spread out over different sources.
     */
    protected RpfCacheHandler[] caches = new RpfCacheHandler[MAX_NUM_BOXES];
    /** The place to look for for image data. */
    protected RpfFrameProvider frameProvider;
    /**
     * Contains information about displaying the RPF data. Also passed
     * to the RpfTocHandlers to determine chart selection.
     * 
     * @see RpfViewAttributes
     */
    protected RpfViewAttributes viewAttributes;
    /**
     * A specialized OMGraphicList to handle the maps and info, and
     * switching either/or on/off.
     */
    protected RpfMaps graphics;
    /**
     * The size of the aux caches, which are used when the map crosses
     * the equator or dateline.
     */
    protected int auxCacheSize;

    public RpfCacheManager() {}

    /**
     * Constructor that lets you set the RPF frame provider
     * 
     * @param fp the object supplying the data.
     */
    public RpfCacheManager(RpfFrameProvider fp) {
        this(fp, new RpfViewAttributes());
    }

    /**
     * Constructor that lets you set the RPF frame provider, the view
     * attributes and the subframe cache size.
     * 
     * @param rfp the object supplying the data.
     * @param rva the view attributes for the images.
     */
    public RpfCacheManager(RpfFrameProvider rfp, RpfViewAttributes rva) {
        this(rfp, rva, RpfCacheHandler.SUBFRAME_CACHE_SIZE, SMALL_CACHE_SIZE);
    }

    /**
     * Constructor that lets you set the RPF frame provider, the view
     * attributes and the subframe cache sizes.
     * 
     * @param rfp the object supplying the data.
     * @param rva the view attributes for the images.
     * @param mainCacheSize the number of subframes held in the large
     *        main cache.
     * @param auxSubframeCacheSize the number of subframes held in the
     *        aux caches.
     */
    public RpfCacheManager(RpfFrameProvider rfp, RpfViewAttributes rva,
            int mainCacheSize, int auxSubframeCacheSize) {
        frameProvider = rfp;
        viewAttributes = rva;
        caches[0] = new RpfCacheHandler(rfp, rva, mainCacheSize);
        graphics = new RpfMaps(rva);
        auxCacheSize = auxSubframeCacheSize;
    }

    //      public void finalize() {
    //      Debug.message("gc", "RpfCacheManager: getting GC'd");
    //      }

    /**
     * Reset the caches in the RpfCacheHandlers.
     */
    public void clearCaches() {
        for (int i = 0; i < caches.length; i++) {
            if (caches[i] != null) {
                caches[i].clearCache();
            }
        }
    }

    /**
     * Set the view attributes for the layer. The frame provider view
     * attributes are updated, and the cache is cleared.
     * 
     * @param rva the RpfViewAttributes used for the layer.
     */
    public void setViewAttributes(RpfViewAttributes rva) {
        viewAttributes = rva;
        for (int i = 0; i < caches.length; i++) {
            if (caches[i] != null) {
                caches[i].setViewAttributes(viewAttributes);
            }
        }
    }

    /**
     * Get the view attributes or the layer.
     * 
     * @return RpfViewAttributes.
     */
    public RpfViewAttributes getViewAttributes() {
        return viewAttributes;
    }

    /**
     * Set the RpfFrameProvider for the layer. Clears out the cache,
     * and the frame provider gets the RpfViewAttributes held by the
     * layer.
     * 
     * @param fp the frame provider.
     */
    public void setFrameProvider(RpfFrameProvider fp) {
        frameProvider = fp;

        for (int i = 0; i < caches.length; i++) {
            if (caches[i] != null) {
                caches[i].setFrameProvider(fp);
            }
        }
    }

    /**
     * Return RpfFrameProvider used by the layer.
     */
    public RpfFrameProvider getFrameProvider() {
        return frameProvider;
    }

    /**
     * Returns the Vector containing RpfCoverageBoxes from the primary
     * RpfCacheHandler. The Vector is the same that was returned to
     * the cache handler from the RpfFrameProvider as a result of the
     * last setCache call. These provide rudimentary knowledge about
     * what is being displayed.
     * 
     * @return Vector of RpfCoverageBoxes.
     */
    public java.util.Vector getCoverageBoxes() {
        return caches[0].getCoverageBoxes();
    }

    /**
     * The call to the cache that lets you choose what kind of
     * information is returned. This function also figures out what
     * part of the earth is covered on the screen, and creates
     * auxillary cache handlers as needed. The CADRG projection held
     * inside the view attributes, used by the RpfTocHandlers, is set
     * here. If the projection passed in is not CADRG, and the caller
     * doesn't care, then a new CADRG projection is created.
     * 
     * @param proj the projection of the screen.
     */
    public OMGraphicList getRectangle(Projection proj) {

        float[] lat = new float[3];
        float[] lon = new float[3];

        // This should be checked by the caller.
        if (!(proj instanceof CADRG)) {
            if (viewAttributes.requireProjection) {
                return new OMGraphicList();
            } else {
                viewAttributes.proj = new CADRG(proj.getCenter(), proj.getScale(), proj.getWidth(), proj.getHeight());
            }
        } else {
            viewAttributes.proj = (CADRG) proj;
        }

        // Need to update the view attributes of the frame provider if
        // it is remote.
        if (frameProvider != null && frameProvider.needViewAttributeUpdates()) {
            frameProvider.setViewAttributes(viewAttributes);
        }

        // Hand off coordinates and scale to RpfCacheHandler.
        // Then the RpfCacheHandler will figure out the frames to load
        // and add to the display list.

        // This next bit of mumbo jumbo is to handle the equator and
        // dateline: Worst case, crossing both, treat each area
        // separately, so it is the same as handling four requests for
        // data - above and below the equator, and left and right of
        // the dateline. Normal case, there is only one box. Two
        // boxes if crossing only one of the boundaries.

        int xa = 2;
        int ya = 2;
        int lat_minus = 2;
        int lon_minus = 2;
        // Set up checks for equator and dateline
        LatLonPoint ll1 = proj.getUpperLeft();
        LatLonPoint ll2 = proj.getLowerRight();

        lat[0] = ll1.getLatitude();
        lon[0] = ll1.getLongitude();
        lat[1] = ll2.getLatitude();
        lon[1] = ll2.getLongitude();
        lat[2] = ll2.getLatitude();
        lon[2] = ll2.getLongitude();

        if (lon[0] > 0 && lon[2] < 0) {
            lon[1] = -179.999f; // put a little breather on the
                                // dateline
            lon_minus = 1;
        }
        if (lat[0] > 0 && lat[2] < 0) {
            lat[1] = -0.0001f; // put a little breather on the equator
            lat_minus = 1;
        }

        if (Debug.debugging("rpf")) {
            Debug.output("RpfCacheManager - for:");
            Debug.output("\tlat[0] " + lat[0]);
            Debug.output("\tlon[0] " + lon[0]);
            Debug.output("\tlat[1] " + lat[1]);
            Debug.output("\tlon[1] " + lon[1]);
            Debug.output("\tlat[2] " + lat[2]);
            Debug.output("\tlon[2] " + lon[2]);
            Debug.output("\tlat_minus = " + lat_minus);
            Debug.output("\tlon_minus = " + lon_minus);
        }

        /*
         * Worst case, there are four boxes on the screen. Best case,
         * there is one. The things that create boxes and dictates how
         * large they are are the equator and the dateline. When the
         * screen straddles one or both of these lat/lon lines,
         * lon_minus and lat_minus get adjusted, causing two or four
         * different calls to the tochandler to get the data
         * above/below the equator, and left/right of the dateline.
         * Plus, each path gets checked until the required boxes are
         * filled.
         */

        // Normal (maybe) box[0] gets filled every time - bottom right
        // box.
        caches[0].setCache(lat[ya - lat_minus],
                lon[xa - lon_minus],
                lat[ya],
                lon[xa],
                viewAttributes.proj);

        if (Debug.debugging("rpf"))
            Debug.output("RpfCacheManager: main (1) cache used.");

        // Dateline split
        if (lon_minus == 1) {
            if (caches[1] == null) {
                caches[1] = new RpfCacheHandler(frameProvider, viewAttributes, auxCacheSize);
            }
            caches[1].setCache(lat[ya - lat_minus], lon[0], lat[ya], -1f
                    * lon[1], viewAttributes.proj); // -1 to make it
                                                    // 180

            if (Debug.debugging("rpf"))
                Debug.output("-- second cache used");
        } else {
            caches[1] = null;
        }

        // Equator Split
        if (lat_minus == 1) {
            if (caches[2] == null) {
                caches[2] = new RpfCacheHandler(frameProvider, viewAttributes, auxCacheSize);
            }
            caches[2].setCache(lat[0], lon[xa - lon_minus], -1f * lat[1], // flip
                                                                          // breather
                    lon[xa], viewAttributes.proj);

            if (Debug.debugging("rpf"))
                Debug.output("-- third cache used");
        } else {
            caches[2] = null;
        }

        // Both!!
        if (lon_minus == 1 && lat_minus == 1) {
            if (caches[3] == null) {
                caches[3] = new RpfCacheHandler(frameProvider, viewAttributes, auxCacheSize);
            }
            caches[3].setCache(lat[0], lon[0], -1f * lat[1],//  flip
                                                            // breather
                    -1f * lon[1], viewAttributes.proj);// -1 to make
                                                       // it 180, not
                                                       // -180

            if (Debug.debugging("rpf"))
                Debug.output("-- fourth cache used");
        } else {
            caches[3] = null;
        }

        OMGraphicList gl = new OMGraphicList();
        gl.add(getSubframes());
        return gl;
    }

    /**
     * Creates the OMGraphicList by cycling through the caches,
     * getting the images and attribute infomation.
     */
    protected OMGraphic getSubframes() {

        //      graphics = new RpfMaps(viewAttributes);
        graphics.clear();

        if (Debug.debugging("rpf")) {
            Debug.output("RpfCacheManager: show maps: "
                    + viewAttributes.showMaps + ", showInfo: "
                    + viewAttributes.showInfo);
        }

        if (!(viewAttributes.showMaps || viewAttributes.showInfo)) {
            return graphics;
        }

        for (int nbox = 0; nbox < MAX_NUM_BOXES; nbox++) {
            if (caches[nbox] != null && caches[nbox].getGoodData()) {

                int subframeRunningCount = 0;

                for (int subx = caches[nbox].start.x; subx <= caches[nbox].end.x; subx++) {
                    for (int suby = caches[nbox].start.y; suby <= caches[nbox].end.y; suby++) {

                        ///////
                        RpfSubframe subframe = caches[nbox].getCached(subx,
                                suby,
                                subframeRunningCount);
                        ///////

                        if (subframe == null) {
                            if (Debug.debugging("rpf")) {
                                Debug.output("RpfCacheManager: checking other TOCs for subframe.");
                            }
                            subframe = caches[nbox].getSubframeFromOtherTOC(subx,
                                    suby,
                                    subframeRunningCount);
                        }

                        if (subframe != null) {
                            graphics.addInfo(subframe.information);
                            graphics.addInfo(subframe.rectangle);
                            graphics.addMap(subframe.image);

                            if (Debug.debugging("rpf"))
                                Debug.output("RpfCacheManager: Adding subframe "
                                        + subx + ", " + suby);
                        } else {
                            if (Debug.debugging("rpf"))
                                Debug.output("RpfCacheManager: subframe "
                                        + subx + ", " + suby + " empty");
                        }
                        subframeRunningCount++;
                    }
                }
            }
        }

        if (Debug.debugging("rpf")) {
            Debug.output("RpfCacheManager: done.");
        }

        return graphics;
    }

    public class RpfMaps extends OMGraphic {
        public RpfViewAttributes atts;
        public OMGraphicList maps = new OMGraphicList();
        public OMGraphicList infos = new OMGraphicList();

        public RpfMaps(RpfViewAttributes rva) {
            atts = rva;
        }

        public void clear() {
            maps.clear();
            infos.clear();
        }

        public void addMap(OMGraphic graphic) {
            maps.add(graphic);
        }

        public void addInfo(OMGraphic graphic) {
            infos.add(graphic);
        }

        public boolean generate(Projection proj) {
            maps.generate(proj);
            infos.generate(proj);
            return true;
        }

        public void render(java.awt.Graphics g) {
            if (atts.showMaps) {
                maps.render(g);
            }
            if (atts.showInfo) {
                infos.render(g);
            }
        }

        public float distance(int x, int y) {
            return Float.MAX_VALUE;
        }
    }

}

