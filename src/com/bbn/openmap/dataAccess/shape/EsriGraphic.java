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
// $Source: /cvs/distapps/openmap/src/openmap/com/bbn/openmap/dataAccess/shape/EsriGraphic.java,v $
// $RCSfile: EsriGraphic.java,v $
// $Revision: 1.3.2.2 $
// $Date: 2006/08/24 20:56:33 $
// $Author: dietrick $
// 
// **********************************************************************

package com.bbn.openmap.dataAccess.shape;

/**
 * An interface to typecast OMGraphics.
 * 
 * @author Doug Van Auken
 */
public interface EsriGraphic extends ShapeConstants {

    /**
     * The lat/lon extent of the EsriGraphic, assumed to contain miny, minx,
     * maxy maxx in order of the array.
     */
    public void setExtents(float[] extents);

    /**
     * The lat/lon extent of the EsriGraphic, returned as miny, minx, maxy maxx
     * in order of the array.
     */
    public float[] getExtents();

    /**
     * Shallow copy this graphic. The contract is that the cloned object should
     * be capable of being placed on a different layer.
     */
    public EsriGraphic shallowCopy();

    /**
     * EsriGraphics can hold attributes.
     * 
     * @param key the key to use for an attribute
     * @param value the attribute value
     */
    public void putAttribute(Object key, Object value);

    /**
     * EsriGraphics can hold attributes.
     * 
     * @param key the key to use for an attribute return the attribute value,
     *        null if the attribute doesn't exist.
     */
    public Object getAttribute(Object key);

    /**
     * Set the type for the EsriGraphic.
     * @param type
     */
    public void setType(int type);
    
    /**
     * Get the graphic type in ESRI type number form
     */
    public int getType();
}