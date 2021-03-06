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
// $Source:
// /cvs/distapps/openmap/src/openmap/com/bbn/openmap/omGraphics/editable/RectUndefinedState.java,v
// $
// $RCSfile: RectUndefinedState.java,v $
// $Revision: 1.2.2.1 $
// $Date: 2004/10/14 18:27:30 $
// $Author: dietrick $
// 
// **********************************************************************

package com.bbn.openmap.omGraphics.editable;

import java.awt.event.*;

import com.bbn.openmap.omGraphics.*;
import com.bbn.openmap.util.Debug;

public class RectUndefinedState extends GraphicUndefinedState {

    public RectUndefinedState(EditableOMRect eomr) {
        super(eomr);
    }

    /**
     * In this state, we need to draw a rect from scratch. So, we
     * listen for a mouse down, and set both points there, and then
     * set the mode to rect edit.
     */
    public boolean mousePressed(MouseEvent e) {
        Debug.message("eomg",
                "RectStateMachine|undefined state|mousePressed = "
                        + graphic.getGraphic().getRenderType());

        graphic.getGrabPoint(EditableOMRect.NW_POINT_INDEX).set(e.getX(),
                e.getY());
        GrabPoint gb;
        gb = graphic.getGrabPoint(EditableOMRect.SE_POINT_INDEX);
        gb.set(e.getX(), e.getY());
        graphic.setMovingPoint(gb);

        if (graphic.getGraphic().getRenderType() == OMGraphic.RENDERTYPE_OFFSET) {
            //          graphic.getGrabPoint(EditableOMRect.OFFSET_POINT_INDEX).set(e.getX(),
            // e.getY());
            graphic.getStateMachine().setOffsetNeeded(true);
            Debug.message("eomg",
                    "RectStateMachine|undefined state| *offset needed*");
        }
        graphic.getStateMachine().setEdit();
        return getMapMouseListenerResponse();
    }

}