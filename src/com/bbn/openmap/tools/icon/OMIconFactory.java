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
// $Source: /cvs/distapps/openmap/src/openmap/com/bbn/openmap/tools/icon/OMIconFactory.java,v $
// $RCSfile: OMIconFactory.java,v $
// $Revision: 1.2.2.1 $
// $Date: 2004/10/14 18:27:43 $
// $Author: dietrick $
// 
// **********************************************************************

package com.bbn.openmap.tools.icon;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

import com.bbn.openmap.omGraphics.DrawingAttributes;

/**
 * The OMIconFactory is a factory to build ImageIcons, with content
 * described by IconParts for a certain size.
 */
public class OMIconFactory {

    /**
     * Create an ImageIcon that is a certain pixel height and width.
     * This will return an empty ImageIcon, and you'd have to do the
     * rendering into its image.
     */
    public static ImageIcon createImageIcon(int width, int height) {
        return createImageIcon(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * Create an ImageIcon that is a certain pixel height and width,
     * with a specified image type (ARGB, RGB, etc). This will return
     * an empty ImageIcon, and you'd have to do the rendering into its
     * image.
     */
    public static ImageIcon createImageIcon(int width, int height, int imageType) {
        return new ImageIcon(new BufferedImage(width, height, imageType));
    }

    /**
     * Create an ImageIcon that is a certain pixel height and width
     * created with a certain IconPart geometry. The geometry will be
     * drawing with default DrawingAttributes.
     */
    public static ImageIcon getIcon(int width, int height, IconPart geometry) {
        return getIcon(width, height, geometry, null);
    }

    /**
     * Create an ImageIcon that is a certain pixel height and width
     * created with a certain IconPart geometry. Also, the
     * DrawingAttributes can be used to add color/texture to the
     * IconPart geometries.
     */
    public static ImageIcon getIcon(int width, int height, IconPart geometry,
                                    DrawingAttributes appDA) {
        ImageIcon icon = createImageIcon(width, height);
        Graphics2D g = (Graphics2D) icon.getImage().getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        geometry.render(g, width, height, appDA);
        return icon;
    }
}