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
// $Source: /cvs/distapps/openmap/src/openmap/com/bbn/openmap/util/wanderer/Purge.java,v $
// $RCSfile: Purge.java,v $
// $Revision: 1.2.2.3 $
// $Date: 2005/08/09 18:42:05 $
// $Author: dietrick $
// 
// **********************************************************************

package com.bbn.openmap.util.wanderer;

import java.io.File;

import com.bbn.openmap.util.ArgParser;
import com.bbn.openmap.util.Debug;

/**
 * PURGE deletes files that start with, or end with, certain strings.
 * Good for cleaning up backup leftover from various editors.
 * 
 * <pre>
 * 
 *  Usage: java com.bbn.openmap.util.wanderer.Purge (dir path) ...
 *  
 * </pre>
 */
public class Purge extends Wanderer implements WandererCallback {

    boolean DETAIL = false;
    String[][] purgeables = new String[2][];

    public Purge(String[] startsWith, String[] endsWith) {
        super();
        purgeables[0] = startsWith;
        purgeables[1] = endsWith;

        if (purgeables[0] == null) {
            purgeables[0] = new String[0];
        }

        if (purgeables[1] == null) {
            purgeables[1] = new String[0];
        }

        DETAIL = Debug.debugging("purge");
        setCallback(this);

        if (DETAIL) {
            StringBuffer sb = new StringBuffer("Deleting files that ");
            boolean sw = false;
            if (startsWith != null && startsWith.length > 0) {
                sb.append("start with ");
                for (int i = 0; i < startsWith.length; i++) {
                    sb.append(startsWith[i] + " ");
                }
                sw = true;
            }

            if (endsWith != null && endsWith.length > 0) {
                if (sw)
                    sb.append("and ");
                sb.append("end with ");
                for (int i = 0; i < endsWith.length; i++) {
                    sb.append(endsWith[i] + " ");
                }
            }
            Debug.output(sb.toString());
        }

    }

    public void handleDirectory(File directory) {
    // Do nothing to directories
    }

    public void handleFile(File file) {
        String fileName = file.getName();
        int i;

        for (i = 0; i < purgeables[0].length; i++) {
            if (fileName.startsWith(purgeables[0][i])) {
                if (DETAIL)
                    Debug.output("Deleting " + fileName);
                file.delete();
                return;
            }
        }

        for (i = 0; i < purgeables[1].length; i++) {
            if (fileName.endsWith(purgeables[1][i])) {
                if (DETAIL)
                    Debug.output("Deleting " + fileName);
                file.delete();
                return;
            }
        }
    }

    /**
     * Given a set of files or directories, parade through them to
     * find files that end with '`', or files that start with '.#',
     * and delete them.
     * 
     * @param argv paths to files or directories, use -h to get a
     *        usage statement.
     */
    public static void main(String[] argv) {
        Debug.init();

        ArgParser ap = new ArgParser("Purge");

        if (argv.length == 0) {
            ap.bail("Wanders through directory tree pruning '~' files.\nUsage: java com.bbn.openmap.util.wanderer.Purge <dir>",
                    false);
        }

        Purge purge = new Purge(new String[] { ".#" }, new String[] { "~" });

        // Assume that the arguments are paths to directories or
        // files.
        for (int i = 0; i < argv.length; i++) {
            purge.handleEntry(new File(argv[i]));
        }
    }
}