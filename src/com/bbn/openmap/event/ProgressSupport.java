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
// $Source: /cvs/distapps/openmap/src/openmap/com/bbn/openmap/event/ProgressSupport.java,v $
// $RCSfile: ProgressSupport.java,v $
// $Revision: 1.3.2.1 $
// $Date: 2004/10/14 18:26:46 $
// $Author: dietrick $
// 
// **********************************************************************

package com.bbn.openmap.event;

import com.bbn.openmap.util.Debug;
import java.util.Iterator;

/**
 * This is a utility class that can be used by beans that need support
 * for handling ProgressListeners and firing ProgressEvents. You can
 * use an instance of this class as a member field of your bean and
 * delegate work to it.
 */
public class ProgressSupport extends ListenerSupport {

    /**
     * Construct a ProgressSupport.
     * 
     * @param sourceBean The bean to be given as the source for any
     *        events.
     */
    public ProgressSupport(Object sourceBean) {
        super(sourceBean);
        Debug.message("progresssupport", "ProgressSupport | ProgressSupport");
    }

    /**
     * Add a ProgressListener to the listener list.
     * 
     * @param listener The ProgressListener to be added
     */
    public void addProgressListener(ProgressListener listener) {
        addListener(listener);
    }

    /**
     * Remove a ProgressListener from the listener list.
     * 
     * @param listener The ProgressListener to be removed
     */
    public void removeProgressListener(ProgressListener listener) {
        removeListener(listener);
    }

    /**
     * Send a layer event to all registered listeners.
     * 
     * @param taskname the description of the task
     * @param finishedValue the completed value
     * @param currentValue the currentValue
     */
    public void fireUpdate(int type, String taskname, float finishedValue,
                           float currentValue) {
        Debug.message("progresssupport", "ProgressSupport | fireUpdate");

        boolean DEBUG = Debug.debugging("progresssupport");

        if (DEBUG) {
            Debug.output("ProgressSupport | fireUpdate has " + size()
                    + " listeners");
        }

        if (size() == 0)
            return;

        ProgressEvent evt = new ProgressEvent(source, type, taskname, finishedValue, currentValue);

        Iterator it = iterator();
        while (it.hasNext()) {
            ((ProgressListener) it.next()).updateProgress(evt);
        }
    }
}