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
// /cvs/distapps/openmap/src/openmap/com/bbn/openmap/omGraphics/editable/RectSetOffsetState.java,v
// $
// $RCSfile: RectSetOffsetState.java,v $
// $Revision: 1.2.2.2 $
// $Date: 2005/08/10 22:45:10 $
// $Author: dietrick $
// 
// **********************************************************************

package com.bbn.openmap.omGraphics.editable;

import java.awt.event.MouseEvent;

import com.bbn.openmap.omGraphics.EditableOMRect;
import com.bbn.openmap.omGraphics.OffsetGrabPoint;

public class RectSetOffsetState extends GraphicSetOffsetState {

    public RectSetOffsetState(EditableOMRect eomc) {
        super(eomc);
    }

    protected void setGrabPoint(MouseEvent e) {
        OffsetGrabPoint ogb = (OffsetGrabPoint) graphic.getGrabPoint(EditableOMRect.OFFSET_POINT_INDEX);
        ogb.set(e.getX(), e.getY());
        ogb.updateOffsets();

        graphic.setMovingPoint(graphic.getGrabPoint(EditableOMRect.OFFSET_POINT_INDEX));
        graphic.redraw(e);
        graphic.fireEvent(EOMGCursors.PUTNODE,
                i18n.get(RectSetOffsetState.class,
                        "Click_to_place_offset_point.",
                        "Click to place offset point."));
    }
}
