//**********************************************************************
//
//<copyright>
//
//BBN Technologies
//10 Moulton Street
//Cambridge, MA 02138
//(617) 873-8000
//
//Copyright (C) BBNT Solutions LLC. All rights reserved.
//
//</copyright>
//**********************************************************************
//
//$Source:
///cvs/darwars/ambush/aar/src/com/bbn/ambush/mission/MissionHandler.java,v
//$
//$RCSfile: WorldFile.java,v $
//$Revision: 1.1.2.4 $
//$Date: 2007/01/22 16:39:40 $
//$Author: dietrick $
//
//**********************************************************************

package com.bbn.openmap.dataAccess.image;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A world file is a small text file that describes the geographic location of
 * an image on a map. It looks like this:
 * 
 * <pre>
 *            20.154 &lt;the dimension of a pixel in map units in the x direction&gt;
 *            0.000 &lt;rotation term for row&gt;
 *            0.000 &lt;rotation term for column&gt;
 *            -20.154 &lt;the dimension of a pixel in map units in the y direction&gt;
 *            424178 &lt;the x coordinate of the center of pixel 1,1 (upper-left pixel)&gt;
 *            4313415 &lt;the y coordinate of the center of pixel 1,1 (upper-left pixel)&gt;
 * </pre>
 * 
 * The naming convention of the world file is that it should have the same name
 * as the file it represents, with a modified extension. The extension should be
 * the extension of the image file with a w attached to it, or the first and
 * third letters of the image file extension with a w appended to it. It can
 * also have a .wld extension.
 * 
 * @author dietrick
 */
public class WorldFile {

    public static Logger logger = Logger.getLogger("com.bbn.openmap.dataAccess.image.WorldFile");

    /**
     * The dimension of a pixel in map units in the x direction.
     */
    protected double xDim;
    /**
     * The dimension of a pixel in map units in the y direction.
     */
    protected double yDim;
    /**
     * The rotation term for row.
     */
    protected double rowRot;
    /**
     * The rotation term for column.
     */
    protected double colRot;
    /**
     * The x coordinate of the center of pixel 1,1 (upper-left pixel).
     */
    protected double x;
    /**
     * The y coordinate of the center of pixel 1,1 (upper-left pixel).
     */
    protected double y;

    protected WorldFile() {
    // For subclasses.
    }

    public WorldFile(URL fileURL) throws MalformedURLException, IOException {
        read(fileURL.openStream());
    }

    public WorldFile(InputStream is) throws MalformedURLException, IOException {
        read(is);
    }

    public void read(InputStream is) throws IOException {
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        xDim = Double.parseDouble(br.readLine());
        rowRot = Double.parseDouble(br.readLine());
        colRot = Double.parseDouble(br.readLine());
        yDim = Double.parseDouble(br.readLine());
        x = Double.parseDouble(br.readLine());
        y = Double.parseDouble(br.readLine());

        logger.info(this.toString());
    }

    public String toString() {
        return "WorldFile[x(" + x + "), y(" + y + "), xDim(" + xDim
                + "), yDim(" + yDim + "), colRot(" + colRot + "), rowRot("
                + rowRot + ")]";
    }

    public static WorldFile get(URL imageFileURL) {
        WorldFile wf = null;
        try {
            String startingString = imageFileURL.toString();

            int extensionIndex = startingString.lastIndexOf('.');
            String worldFileNameBase = startingString;
            String extension = null;
            InputStream is = null;

            if (extensionIndex != -1) {

                extension = startingString.substring(extensionIndex);
                worldFileNameBase = startingString.substring(0, extensionIndex);

                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("base name for image: " + worldFileNameBase);
                    logger.fine("image extension: " + extension);
                }

                // Try adding w to extension and seeing if that file is there.
                is = checkValidityAndGetStream(worldFileNameBase + extension
                        + "w");

                if (is == null && extension.length() >= 4) {
                    is = checkValidityAndGetStream(worldFileNameBase + "."
                            + extension.charAt(1) + extension.charAt(3) + "w");
                }

            }

            if (is == null) {
                // Try world file extension if nothing else has worked, or if
                // their isn't an extension on the image file.
                extension = ".wld";
                is = checkValidityAndGetStream(worldFileNameBase + extension);
            }

            if (is != null) {
                wf = new WorldFile(is);

                // Check for coordinates of pixel to see if they make sense. If
                // they are greater than 180, then they are probably meters, and
                // we can't handle them right now.

                double x = wf.getX();
                double y = wf.getY();

                if (x < -180 || x > 180 || y > 90 || y < -90) {
                    logger.warning("Looks like an unsupported projection: "
                            + wf.toString());
                    wf = new ErrWorldFile("World File (" + worldFileNameBase
                            + extension
                            + ") doesn't contain decimal degree coordinates");
                }
            }

        } catch (MalformedURLException murle) {

        } catch (IOException ioe) {

        }

        return wf;
    }

    protected static InputStream checkValidityAndGetStream(String wfURLString) {
        try {
            logger.fine("checking for world file: " + wfURLString);
            URL wfURL = new URL(wfURLString);
            return wfURL.openStream();
        } catch (MalformedURLException murle) {
            logger.warning("MalformedURLException for " + wfURLString);
        } catch (IOException ioe) {
            logger.warning("IOException for " + wfURLString);
        }
        return null;

    }

    public double getColRot() {
        return colRot;
    }

    public void setColRot(double colRot) {
        this.colRot = colRot;
    }

    public double getRowRot() {
        return rowRot;
    }

    public void setRowRot(double rowRot) {
        this.rowRot = rowRot;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getXDim() {
        return xDim;
    }

    public void setXDim(double dim) {
        xDim = dim;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getYDim() {
        return yDim;
    }

    public void setYDim(double dim) {
        yDim = dim;
    }
}
