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
// $Source: /cvs/distapps/openmap/src/openmap/com/bbn/openmap/graphicLoader/GLPoint.java,v $
// $RCSfile: GLPoint.java,v $
// $Revision: 1.2.2.1 $
// $Date: 2004/10/14 18:26:48 $
// $Author: dietrick $
// 
// **********************************************************************

package com.bbn.openmap.graphicLoader;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import com.bbn.openmap.Environment;
import com.bbn.openmap.omGraphics.OMPoint;
import com.bbn.openmap.LatLonPoint;
import com.bbn.openmap.proj.*;
import com.bbn.openmap.util.Debug;
import com.bbn.openmap.util.PaletteHelper;

/**
 * A GLPoint is a location on the map. It can move around randomly if
 * it's not set to be stationary, and will change color if it can see
 * any other point.
 */
public class GLPoint extends OMPoint implements ActionListener, FocusListener {

    public static Color CONNECTED_COLOR = Color.green;
    public static Color DISCONNECTED_COLOR = Color.red;
    protected boolean stationary = true;
    protected String name;
    protected int height = 0;// meters.

    public GLPoint(float lat, float lon, int radius, boolean isOval) {
        super(lat, lon, radius);
        setOval(isOval);
    }

    public void resetConnected() {
        setFillPaint(DISCONNECTED_COLOR);
    }

    public void connected(boolean connected) {
        if (connected) {
            setFillPaint(CONNECTED_COLOR);
        }
    }

    protected void moveRandomly(float factor) {
        double hor = Math.random() - .5;
        double vert = Math.random() - .5;

        setLat(getLat() + (float) vert * factor);
        setLon(getLon() + (float) hor * factor);
    }

    public void move(float factor) {
        if (!stationary) {
            moveRandomly(factor);
        }
    }

    public void move(int distance, Length units, float Az) {
        LatLonPoint newLocation = GreatCircle.spherical_between(ProjMath.degToRad(getLat()),
                ProjMath.degToRad(getLon()),
                units.toRadians(distance),
                Az);

        setLat(newLocation.getLatitude());
        setLon(newLocation.getLongitude());
    }

    public void setStationary(boolean set) {
        stationary = set;
        if (movementButton != null) {
            movementButton.setSelected(set);
        }
    }

    public boolean getStationary() {
        return stationary;
    }

    public void setName(String set) {
        name = set;
    }

    public String getName() {
        return name;
    }

    public void setHeight(int h) {
        height = h;
        if (heightField != null) {
            heightField.setText(Integer.toString(h));
        }
        if (Debug.debugging("graphicloader")) {
            Debug.output("GLPoint: " + getName() + " setting height to : " + h);
        }
    }

    public int getHeight() {
        return height;
    }

    protected transient java.awt.Container palette = null;

    /**
     * Make the palette visible. Will automatically determine if we're
     * running in an applet environment and will use a JInternalFrame
     * over a JFrame if necessary.
     */
    public void showPalette() {
        if (Environment.getBoolean(Environment.UseInternalFrames)) {

            final JLayeredPane desktop = Environment.getInternalFrameDesktop();

            // get the window
            palette = PaletteHelper.getPaletteInternalWindow(getGUI(),
                    getName(),
                    new InternalFrameAdapter() {
                        public void internalFrameClosed(InternalFrameEvent e) {
                            if (desktop != null) {
                                desktop.remove((JInternalFrame) palette);
                                desktop.repaint();
                            }
                            palette = null;
                        };
                    });
            // add the window to the desktop
            if (desktop != null) {
                desktop.add(palette);
                palette.setVisible(true);
            }
        } else {
            if (palette == null) {
                palette = PaletteHelper.getPaletteWindow(getGUI(),
                        getName(),
                        new ComponentAdapter() {
                            public void componentHidden(ComponentEvent e) {
                            //                              firePaletteEvent(false);
                            };
                        });
            }
            palette.setVisible(true);
            ((JFrame) palette).setState(java.awt.Frame.NORMAL);
        }
    }

    /**
     * Hide the GLPoint's palette.
     */
    public void hidePalette() {
        if (palette == null) {
            return;
        }

        if (Environment.getBoolean(Environment.UseInternalFrames)) {
            // close the palette
            try {
                ((JInternalFrame) palette).setClosed(true);
            } catch (java.beans.PropertyVetoException evt) {
                com.bbn.openmap.util.Assert.assertExp(false,
                        "GLPoint.hidePalette(): internal error!");
            }
        } else {
            palette.setVisible(false);
        }
    }

    JCheckBox movementButton = null;
    JTextField heightField = null;

    /**
     * Gets the gui controls associated with the GLPoint. This default
     * implementation returns null indicating that the GLPoint has no
     * gui controls.
     * 
     * @return java.awt.Component or null
     */
    public java.awt.Component getGUI() {
        JPanel panel = new JPanel(new GridLayout(0, 1));

        // Only want to do this once...
        if (movementButton == null) {
            movementButton = new JCheckBox("Stationary", getStationary());
            movementButton.addActionListener(this);
            movementButton.setActionCommand(MoveCmd);
        }

        panel.add(movementButton);

        JPanel heightPanel = new JPanel(new GridLayout(0, 3));

        heightPanel.add(new JLabel("Object height: "));
        if (heightField == null) {
            heightField = new JTextField(Integer.toString(height), 10);
            heightField.setHorizontalAlignment(JTextField.RIGHT);
            heightField.addActionListener(this);
            heightField.addFocusListener(this);
        }
        heightPanel.add(heightField);
        heightPanel.add(new JLabel(" meters"));

        panel.add(heightPanel);

        return panel;
    }

    public final static String MoveCmd = "MoveCommand";

    public void actionPerformed(java.awt.event.ActionEvent ae) {
        String cmd = ae.getActionCommand();
        if (cmd == MoveCmd) {
            JCheckBox check = (JCheckBox) ae.getSource();
            setStationary(check.isSelected());
        } else {
            try {
                setHeight(Integer.parseInt(cmd));
            } catch (NumberFormatException nfe) {
                setHeight(getHeight());
            }
        }
    }

    public void focusGained(FocusEvent e) {}

    public void focusLost(FocusEvent e) {
        try {
            setHeight(Integer.parseInt(((JTextField) (e.getSource())).getText()));
        } catch (NumberFormatException nfe) {
            setHeight(0);
        }
    }

}