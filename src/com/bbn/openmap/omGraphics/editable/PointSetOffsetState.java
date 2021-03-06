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
// /cvs/distapps/openmap/src/openmap/com/bbn/openmap/omGraphics/editable/PointSetOffsetState.java,v
// $
// $RCSfile: PointSetOffsetState.java,v $
// $Revision: 1.2.2.2 $
// $Date: 2005/08/10 22:45:11 $
// $Author: dietrick $
// 
// **********************************************************************

package com.bbn.openmap.omGraphics.editable;

import java.awt.event.MouseEvent;

import com.bbn.openmap.omGraphics.EditableOMPoint;
import com.bbn.openmap.omGraphics.OffsetGrabPoint;

public class PointSetOffsetState extends GraphicSetOffsetState {

    public PointSetOffsetState(EditableOMPoint eomc) {
        super(eomc);
    }

    protected void setGrabPoint(MouseEvent e) {
        OffsetGrabPoint ogb = (OffsetGrabPoint) graphic.getGrabPoint(EditableOMPoint.OFFSET_POINT_INDEX);
        ogb.set(e.getX(), e.getY());
        ogb.updateOffsets();

        graphic.setMovingPoint(graphic.getGrabPoint(EditableOMPoint.OFFSET_POINT_INDEX));
        graphic.redraw(e);
        graphic.fireEvent(EOMGCursors.PUTNODE,
                i18n.get(PointSetOffsetState.class,
                        "Click_to_place_offset_point.",
                        "Click to place offset point."));
    }
}
