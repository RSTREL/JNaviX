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
// $Source: /cvs/distapps/openmap/src/openmap/com/bbn/openmap/gui/time/RealTimeHandler.java,v $
// $RCSfile: RealTimeHandler.java,v $
// $Revision: 1.2.2.1 $
// $Date: 2004/10/14 18:26:56 $
// $Author: dietrick $
// 
// **********************************************************************

package com.bbn.openmap.gui.time;

/**
 * The RealTimeHandler interface describes an object that has to deal
 * with a timer that controls time on a different scale. For instance,
 * the timer may be controlling objects on a map that move over days,
 * where days in the scenario time are represented by several seconds
 * on the timer.
 */
public interface RealTimeHandler {

    /**
     * Set the timer interval, or how often the timer updates.
     * 
     * @param interval number of milliseconds between updates.
     */
    public void setUpdateInterval(int interval);

    /**
     * Get the timer interval, or how often the timer updates.
     * 
     * @return interval number of milliseconds between updates.
     */
    public int getUpdateInterval();

    /**
     * Set the number of scenario units that pass when the timer
     * updates within its interval.
     * 
     * @param pace a number that means something to the
     *        RealTimeHandler.
     */
    public void setPace(int pace);

    /**
     * Get the number of scenario units that pass when the timer
     * updates within its interval.
     * 
     * @return a number that means something to the RealTimeHandler.
     */
    public int getPace();

    /**
     * Set the current time value for the timer. The meaning of the
     * time value depends on the RealTimeHandler.
     * 
     * @param time
     */
    public void setTime(long time);

    /**
     * Get the current time value for the timer. The meaning of the
     * time value depends on the RealTimeHandler.
     * 
     * @return time
     */
    public long getTime();

    /**
     * Start the timer.
     */
    public void startClock();

    /**
     * Stop the timer.
     */
    public void stopClock();

    /**
     * Set whether time increases or decreases when the clock is run.
     * If direction is zero or greater, clock runs forward. If
     * direction is negative, clock runs backward.
     */
    public void setClockDirection(int direction);

    /**
     * Get whether time increases or decreases when the clock is run.
     * If direction is zero or greater, clock runs forward. If
     * direction is negative, clock runs backward.
     */
    public int getClockDirection();

    /**
     * Move the clock forward one clock interval.
     */
    public void stepForward();

    /**
     * Move the clock back one clock interval.
     */
    public void stepBackward();
}