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
// $Source: /cvs/distapps/openmap/src/openmap/com/bbn/openmap/event/LayerListener.java,v $
// $RCSfile: LayerListener.java,v $
// $Revision: 1.1.1.1.2.1 $
// $Date: 2004/10/14 18:26:45 $
// $Author: dietrick $
// 
// **********************************************************************

package com.bbn.openmap.event;

/**
 * Listens for requests to add, remove, or shuffle layers of the map.
 */
public interface LayerListener extends java.util.EventListener {
    public void setLayers(LayerEvent evt);
}

