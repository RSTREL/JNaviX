// **********************************************************************
//
// <copyright>
//
// BBN Technologies
// 10 Moulton Street
// Cambridge, MA 02138
// (617) 873-8000
//
// Copyright (C) BBNT Solutions LLC. All rights reserved.
//
// </copyright>
// **********************************************************************
//
// $Source:
// /cvs/distapps/openmap/src/openmap/com/bbn/openmap/omGraphics/editable/TextUndefinedState.java,v
// $
// $RCSfile: TextUndefinedState.java,v $
// $Revision: 1.3.2.3 $
// $Date: 2005/08/11 21:03:32 $
// $Author: dietrick $
//
// **********************************************************************

package com.bbn.openmap.omGraphics.editable;

import java.awt.event.MouseEvent;

import com.bbn.openmap.omGraphics.EditableOMText;
import com.bbn.openmap.omGraphics.GrabPoint;
import com.bbn.openmap.omGraphics.OMGraphic;

public class TextUndefinedState extends GraphicUndefinedState {

    // ~ Constructors
    // ---------------------------------------------------------------------------------------------------

    /**
     * Creates a new TextUndefinedState object.
     * 
     * @param eomc
     */
    public TextUndefinedState(EditableOMText eomc) {
        super(eomc);
    }

    // ~ Methods
    // --------------------------------------------------------------------------------------------------------

    /**
     */
    public boolean mousePressed(MouseEvent e) {
        GrabPoint gb = graphic.getGrabPoint(EditableOMText.CENTER_POINT_INDEX);

        gb.set(e.getX(), e.getY());

        graphic.setMovingPoint(gb);

        if (graphic.getGraphic().getRenderType() == OMGraphic.RENDERTYPE_OFFSET) {
            graphic.getGrabPoint(EditableOMText.OFFSET_POINT_INDEX)
                    .set(e.getX(), e.getY());

            graphic.getStateMachine().setOffsetNeeded(true);
        }

        graphic.getStateMachine().setEdit();

        return getMapMouseListenerResponse();
    }

    /**
     */
    public boolean mouseMoved(MouseEvent e) {
        graphic.fireEvent(EOMGCursors.EDIT, i18n.get(TextUndefinedState.class,
                "Click_to_define_the_text_location.",
                "Click to define the text location."));
        return false;
    }
} // end class TextUndefinedState
