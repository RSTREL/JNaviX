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
// /cvs/distapps/openmap/src/openmap/com/bbn/openmap/omGraphics/editable/RectSelectedState.java,v
// $
// $RCSfile: RectSelectedState.java,v $
// $Revision: 1.2.2.2 $
// $Date: 2005/08/10 22:45:11 $
// $Author: dietrick $
// 
// **********************************************************************

package com.bbn.openmap.omGraphics.editable;

import java.awt.event.MouseEvent;

import com.bbn.openmap.omGraphics.EditableOMRect;
import com.bbn.openmap.omGraphics.GrabPoint;
import com.bbn.openmap.util.Debug;

public class RectSelectedState extends GraphicSelectedState {

    public RectSelectedState(EditableOMRect eomr) {
        super(eomr);
    }

    /**
     * In this state, we need to change states only if the graphic, or
     * anyplace off the graphic is pressed down on. If the end points
     * are clicked on, then we do nothing except set the moving point
     * and go to edit mode.
     */
    public boolean mousePressed(MouseEvent e) {
        Debug.message("eomg", "RectStateMachine|selected state|mousePressed");
        // This is added for Rectangles:
        ((EditableOMRect) graphic).initRectSize();
        return super.mousePressed(e);
    }

    public boolean mouseMoved(MouseEvent e) {
        Debug.message("eomgdetail",
                "RectStateMachine|selected state|mouseMoved");

        GrabPoint mp = graphic.getMovingPoint(e);
        if (mp == null) {
            graphic.fireEvent(EOMGCursors.DEFAULT, "");
        } else {
            graphic.fireEvent(EOMGCursors.EDIT,
                    i18n.get(RectSelectedState.class,
                            "Click_and_Drag_to_change_the_graphic.",
                            "Click and Drag to change the graphic."));
        }
        return false;
    }
}
