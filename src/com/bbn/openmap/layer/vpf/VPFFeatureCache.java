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
// $Source: /cvs/distapps/openmap/src/openmap/com/bbn/openmap/layer/vpf/VPFFeatureCache.java,v $
// $RCSfile: VPFFeatureCache.java,v $
// $Revision: 1.2.2.2 $
// $Date: 2004/10/14 18:27:22 $
// $Author: dietrick $
// 
// **********************************************************************

package com.bbn.openmap.layer.vpf;

import com.bbn.openmap.layer.util.cacheHandler.CacheHandler;
import com.bbn.openmap.layer.util.cacheHandler.CacheObject;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.util.Debug;

/**
 * The VPFFeatureCache is an extended CacheHandler that caches
 * OMGraphicLists representing a specific feature contained in a
 * CoverageTile. It's used by the VPFCachedFeatureGraphicWarehouse,
 * which in turn is used by the LibraryBean as a central warehouse.
 */
public class VPFFeatureCache extends CacheHandler {

    /**
     * Create a VPFFeatureCache of the default maximum size.
     */
    public VPFFeatureCache() {
        super();
    }

    /**
     * Create a VPFFeatureCache of the specified maximum size.
     */
    public VPFFeatureCache(int maxSize) {
        super(maxSize);
    }

    /**
     * Adds an OMGraphic to a list, signified by the feature type and
     * the table. The PrimitiveTable provides an identifying tile
     * path.
     * 
     * @param omg OMGraphic to add
     * @param featureType the feature code of the OMGraphic
     * @param pt the PrimitiveTable containing the path to the
     *        CoverageTile.
     */
    protected synchronized void addToCachedList(OMGraphic omg,
                                                String featureType,
                                                PrimitiveTable pt, String type) {
        String key = createTableCacheKey(featureType, pt.getTileDirectory()
                .getPath());
        FeatureCacheGraphicList omgl = (FeatureCacheGraphicList) get(key);
        omgl.add(omg);
    }

    /**
     * Create an identifying key from the feature type and tile path.
     */
    public static String createTableCacheKey(String featureType, String tilePath) {
        return featureType + "-" + tilePath;
    }

    /**
     * Returns true if the features from a tile (as described by the
     * key) existed and was added to the warehouse graphics list.
     * Returns false if the list needs to be created and the contents
     * read in from data files. In both cases the OMGraphicList for
     * the tile/feature is loaded into the cache, the return value is
     * a signal to the caller that the list must be populated or not.
     * 
     * @param featureType the feature type code.
     * @param tilePath the relative path to the tile file.
     * @param requestor the OMGraphicList used to contain cached
     *        lists. The cached list will for the featureType/path
     *        code will be added to this list, regardless of whether
     *        it's been populated or not. The requestor list will be
     *        returned when the warehouse is asked for the graphics
     *        list.
     * @return true if the list has already been loaded and the caller
     *         doesn't need to read the data files to create list
     *         contents.
     */
    public synchronized boolean loadCachedGraphicList(String featureType,
                                                      String tilePath,
                                                      OMGraphicList requestor) {

        String key = createTableCacheKey(featureType, tilePath);
        boolean exists = (searchCache(key) != null);

        // Will retrieve the old list if it exists, create a new one
        // if it doesn't.

        FeatureCacheGraphicList fcgl = (FeatureCacheGraphicList) get(key,
                VPFUtil.getTypeForFeatureCode(featureType));
        if (fcgl.getFeatureName() == null) {
            fcgl.setFeatureName(featureType);
        }
        requestor.add(fcgl);

        // Might want to set the current attributes for the existing
        // contents of the list in case they were changed by the user.
        return exists;
    }

    /**
     * Query that the CoverageTable makes to decide whether to read
     * the file contents or to used the cached version.
     * 
     * @param currentFeature the feature type
     * @param currentTile the tile directory
     * @param requestor the OMGraphicList to add the cached list to.
     *        If the CoverageTable reads the data files, the
     *        OMGraphics created from the files will be added to the
     *        list added to the requestor.
     * @return true if the CoverageTable needs to read the data files.
     */
    public synchronized boolean needToFetchTileContents(
                                                        String currentFeature,
                                                        TileDirectory currentTile,
                                                        OMGraphicList requestor) {
        if (loadCachedGraphicList(currentFeature,
                currentTile.getPath(),
                requestor)) {
            if (Debug.debugging("vpf.cache")) {
                Debug.output("VPFFeatureCache: Loaded Cached List: "
                        + createTableCacheKey(currentFeature,
                                currentTile.getPath()));
            }
            return false;
        }
        return true;
    }

    /**
     * Additional get method that will call a load() method that takes
     * into account the featureType. The regular get() method will not
     * be used, unless something else calls it, which is not advised.
     * 
     * @param key the created key for cached list, see
     *        createTableCacheKey
     * @param featureType the kind of feature, VPFUtil.Area,
     *        VPFUtil.Edge, VPFUtil.Point or VPFUtil.Text.
     */
    public Object get(String key, String featureType) {
        CacheObject ret = searchCache(key);
        if (ret != null)
            return ret.obj;

        ret = load(key, featureType);
        if (ret == null)
            return null;

        replaceLeastUsed(ret);
        return ret.obj;
    }

    /**
     * CacheHandler method to load the new OMGraphicLists
     * (FeatureCacheGraphicLists). Shouldn't be used because the
     * FeatureCacheGraphicList type will be unknown. This method is
     * only defined to implement the CacheHandler abstract method.
     */
    public CacheObject load(String key) {
        return load(key, null);
    }

    /**
     * CacheHandler method to load the new OMGraphicLists
     * (FeatureCacheGraphicLists).
     */
    public CacheObject load(String key, String featureType) {
        if (key != null) {
            return new VPFListCacheObject(key, FeatureCacheGraphicList.createForType(featureType));
        }
        return null;
    }

    /**
     * CacheObject used by VPFFeatureCache.
     */
    public static class VPFListCacheObject extends CacheObject {
        /**
         * Construct a VPFListCacheObject, just calls superclass
         * constructor
         * 
         * @param id passed to superclass
         * @param obj passed to superclass
         */
        public VPFListCacheObject(String id, OMGraphicList obj) {
            super(id, obj);
        }

        /**
         */
        public void finalize() {
            ((OMGraphicList) obj).clear();
        }
    }

}