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
//$RCSfile: WorldFileImageReaderLoader.java,v $
//$Revision: 1.1.2.2 $
//$Date: 2007/01/22 15:52:59 $
//$Author: dietrick $
//
//**********************************************************************

package com.bbn.openmap.dataAccess.image;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * An ImageReaderLoader that answers queries on behalf of a
 * WorldFileImageReader. Will answer yes for image files with .tif, .png, .jpg,
 * .jpeg and .gif extensions.
 * 
 * @author dietrick
 */
public class WorldFileImageReaderLoader implements ImageReaderLoader {
    public WorldFileImageReaderLoader() {}

    public ImageReader getImageReader(URL fileURL) {
        WorldFileImageReader wfid = null;
        try {
            wfid = new WorldFileImageReader(fileURL);
        } catch (MalformedURLException murle) {

        } catch (IOException ioe) {

        }
        return wfid;
    }

    public boolean isLoadable(String fileName) {
        if (fileName != null) {
            fileName = fileName.toLowerCase();
            return (fileName.endsWith(".tif") || fileName.endsWith(".png")
                    || fileName.endsWith(".jpeg") || fileName.endsWith(".jpg") || fileName.endsWith(".gif"));
        }

        return false;
    }

    public boolean isLoadable(URL fileURL) {
        return isLoadable(fileURL.getPath());
    }
    
}
