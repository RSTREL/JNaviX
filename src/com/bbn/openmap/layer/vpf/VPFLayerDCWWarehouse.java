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
// $Source: /cvs/distapps/openmap/src/openmap/com/bbn/openmap/layer/vpf/VPFLayerDCWWarehouse.java,v $
// $Revision: 1.3.2.2 $ $Date: 2005/08/09 21:17:53 $ $Author: dietrick $
// **********************************************************************

package com.bbn.openmap.layer.vpf;

import java.util.*;

import com.bbn.openmap.LatLonPoint;
import com.bbn.openmap.omGraphics.*;
import com.bbn.openmap.util.Debug;
import com.bbn.openmap.util.PropUtils;
import com.bbn.openmap.io.FormatException;

/**
 * Implement a graphic factory that builds OMGraphics. This one
 * handles DCW dtabases. It has some properties that can be added to
 * the VPFLayer property list that further control which types of
 * features will not be displayed for a coverage type.
 * <P>
 * 
 * If you use the com.bbn.openmap.layer.vpf.Server class, and run it
 * on a (coverage type)/int.vdt file:
 * 
 * <pre>
 * 
 * 
 *  java com.bbn.openmap.layer.vpf.Server /dcw/noamer/po/int.vdt
 * 
 *  
 * </pre>
 * 
 * You can use the values in column 3 to add to a list to have that
 * attribute type *NOT* show up.
 * 
 * <pre>
 * 
 * 
 *  vpflayer.areaTypeExclude=
 *  vpflayer.lineTypeExclude=9 (for po coverages, gets rid of tile boundaries)
 *  vpflayer.textTypeExclude=
 *  
 * </pre>
 * 
 * These are space-separated lists.
 * 
 * @see com.bbn.openmap.omGraphics.OMGraphic
 */
public class VPFLayerDCWWarehouse extends LayerGraphicWarehouseSupport {

    public final static String AreaTypeExcludeProperty = "areaTypeExclude";
    public final static String LineTypeExcludeProperty = "lineTypeExclude";
    public final static String TextTypeExcludeProperty = "textTypeExclude";

    protected int[] areaTypeExcludes = null;
    protected int[] lineTypeExcludes = null;
    protected int[] textTypeExcludes = null;

    protected boolean DEBUG = false;

    /**
     *  
     */
    public VPFLayerDCWWarehouse() {
        super();
        DEBUG = Debug.debugging("DCW");
    }

    public void setProperties(String prefix, Properties props) {
        super.setProperties(prefix, props);

        String realPrefix = PropUtils.getScopedPropertyPrefix(prefix);

        String list = props.getProperty(realPrefix + AreaTypeExcludeProperty);
        if (list != null) {
            areaTypeExcludes = getNumbersFromPropertyList(list);
        } else {
            areaTypeExcludes = getNumbersFromPropertyList("0 2");
            // topology artifacts and oceans, buy default
        }

        if (DEBUG)
            Debug.output("Excluding area types: " + areaTypeExcludes);

        list = props.getProperty(realPrefix + LineTypeExcludeProperty);
        if (list != null) {
            lineTypeExcludes = getNumbersFromPropertyList(list);
        }

        if (DEBUG)
            Debug.output("Excluding area types: " + lineTypeExcludes);

        list = props.getProperty(realPrefix + TextTypeExcludeProperty);
        if (list != null) {
            textTypeExcludes = getNumbersFromPropertyList(list);
        }

        if (DEBUG)
            Debug.output("Excluding area types: " + textTypeExcludes);

    }

    /**
     * From a string of space separated numbers, creates an int[].
     */
    protected int[] getNumbersFromPropertyList(String list) {
        List realList = new ArrayList();

        List excludes = PropUtils.parseSpacedMarkers(list);

        for (Iterator it = excludes.iterator(); it.hasNext();) {
            String number = (String) it.next();
            try {
                realList.add(Integer.valueOf(number));
            } catch (NumberFormatException nfe) {
            }
        }

        int[] numbers = null;

        int size = realList.size();

        if (size > 0) {
            numbers = new int[size];
            int count = 0;
            for (Iterator it = realList.iterator(); it.hasNext();) {
                numbers[count++] = ((Integer) it.next()).intValue();
            }
        }
        return numbers;
    }

    /**
     * returns true if the num is a number somewhere on the list.
     */
    protected boolean onList(int[] list, int num) {
        if (list != null) {
            for (int i = 0; i < list.length; i++) {
                if (num == list[i]) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns the list of features to draw - DCW doesn't have
     * features, so returns an empty List
     */
    public List getFeatures() {
        return Collections.EMPTY_LIST;
    }

    /**
     *  
     */
    public void createArea(CoverageTable covtable, AreaTable areatable,
                           List facevec, LatLonPoint ll1, LatLonPoint ll2,
                           float dpplat, float dpplon) {
        List ipts = new ArrayList();

        if (areaTypeExcludes != null) {

            MutableInt areatype = new MutableInt(-1);
            // String descript = covtable.getAreaDescription(facevec, areatype);

            if (onList(areaTypeExcludes, areatype.value)) {
                return;
            }
        }

        //      if (areatype.value == 0) {//topology artifact
        //          return;
        //      }

        //      if (areatype.value == 2) {
        //          if (Debug.debugging("vpf")) {
        //              Debug.output("Skipping open ocean: " + descript);
        //          }
        //          return;
        //      }

        int totalSize = 0;
        try {
            totalSize = areatable.computeEdgePoints(facevec, ipts);
        } catch (FormatException f) {
            Debug.output("FormatException in computeEdgePoints: " + f);
            return;
        }
        if (totalSize == 0) {
            return;
        }

        OMPoly py = createAreaOMPoly(ipts,
                totalSize,
                ll1,
                ll2,
                dpplat,
                dpplon,
                covtable.doAntarcticaWorkaround);

        //      if (areatype.value == -1) {
        //          areatype.value = 0;
        //      }

        drawingAttributes.setTo(py);

        // HACK to get tile boundaries to not show up for areas.
        py.setLinePaint(py.getFillPaint());
        py.setSelectPaint(py.getFillPaint());

        graphics.add(py);
    }

    /**
     *  
     */
    public void createEdge(CoverageTable covtable, EdgeTable edgetable,
                           List edgevec, LatLonPoint ll1, LatLonPoint ll2,
                           float dpplat, float dpplon, CoordFloatString coords) {
        //  Kept these here to keep in mind that it may be possible to
        //  further figure out what exactly we have here.
        if (lineTypeExcludes != null) {

            MutableInt lineType = new MutableInt(-1);
            //String desc = covtable.getLineDescription(edgevec, lineType);

            if (onList(lineTypeExcludes, lineType.value)) {
                return;
            }
        }

        OMPoly py = createEdgeOMPoly(coords, ll1, ll2, dpplat, dpplon);
        drawingAttributes.setTo(py);
        py.setIsPolygon(false);
        graphics.add(py);
    }

    /**
     *  
     */
    public void createText(CoverageTable covtable, TextTable texttable,
                           List textvec, float latitude, float longitude,
                           String text) {
        //  Kept these here to keep in mind that it may be possible to
        //  further figure out what exactly we have here.
        if (textTypeExcludes != null) {

            MutableInt textType = new MutableInt(-1);
            //String desc = covtable.getTextDescription(textvec, textType);

            if (onList(textTypeExcludes, textType.value)) {
                return;
            }
        }

        OMText txt = createOMText(text, latitude, longitude);
        drawingAttributes.setTo(txt);
        graphics.add(txt);
    }

    /**
     * Method called by the VPF reader code to construct a node
     * feature.
     */
    public void createNode(CoverageTable c, NodeTable t, List nodeprim,
                           float latitude, float longitude, boolean isEntityNode) {
        OMPoint pt = createOMPoint(latitude, longitude);
        drawingAttributes.setTo(pt);
        graphics.add(pt);
    }
}