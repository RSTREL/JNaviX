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
// $Source: /cvs/distapps/openmap/src/openmap/com/bbn/openmap/layer/DrawingToolLayer.java,v $
// $RCSfile: DrawingToolLayer.java,v $
// $Revision: 1.24.2.12 $
// $Date: 2008/02/28 23:36:27 $
// $Author: dietrick $
// 
// **********************************************************************

package com.bbn.openmap.layer;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.URL;
import java.util.Properties;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.bbn.openmap.I18n;
import com.bbn.openmap.dataAccess.shape.DbfTableModel;
import com.bbn.openmap.dataAccess.shape.EsriGraphicList;
import com.bbn.openmap.dataAccess.shape.EsriShapeExport;
import com.bbn.openmap.event.MapMouseEvent;
import com.bbn.openmap.event.MapMouseMode;
import com.bbn.openmap.event.MapMouseSupport;
import com.bbn.openmap.event.SelectMouseMode;
import com.bbn.openmap.omGraphics.OMAction;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.event.MapMouseInterpreter;
import com.bbn.openmap.proj.Projection;
import com.bbn.openmap.tools.drawing.DrawingTool;
import com.bbn.openmap.tools.drawing.DrawingToolRequestor;
import com.bbn.openmap.tools.drawing.OMDrawingTool;
import com.bbn.openmap.util.Debug;
import com.bbn.openmap.util.FileUtils;
import com.bbn.openmap.util.PaletteHelper;
import com.bbn.openmap.util.PropUtils;
import com.bbn.openmap.util.propertyEditor.Inspector;

/**
 * This layer can receive graphics from the OMDrawingToolLauncher, and also sent
 * it's graphics to the OMDrawingTool for editing.
 * <P>
 * 
 * The projectionChanged() method is taken care of in the OMGraphicHandlerLayer
 * superclass.
 * <P>
 * 
 * This class responds to all the properties that the OMGraphicHandlerLayer
 * repsonds to, including the mouseModes property. If the mouseModes property
 * isn't set, the SelectMouseMode.modeID mode ID is set. When the
 * MapMouseInterpreter calls select(OMGraphic), the OMGraphic is passed to the
 * DrawingTool. This class also responds to the showHints property (true by
 * default), which dictates if tooltips and information delegator text is
 * displayed when the layer's contents are moused over.
 * 
 * <pre>
 *                                
 *                                # Properties for DrawingToolLayer:
 *                                drawingToolLayer.class=com.bbn.openmap.layer.DrawingToolLayer
 *                               
 *                                drawingToolLayer.prettyName=General Layer
 *                               
 *                                # optional flag to tell layer to display tooltip queues over it's OMGraphics
 *                                drawingToolLayer.showHints=true
 *                               
 *                                # optional flag to specify file to store and read OMGraphics.  A Save button 
 *                                # is available on the palette.  If it's not specified and the Save button is 
 *                                # chosen, the user will queried for this location.
 *                                drawingToolLayer.file=file to read OMGraphics from
 *   
 * </pre>
 */
public class DrawingToolLayer extends OMGraphicHandlerLayer implements
        DrawingToolRequestor {

    /** Get a handle on the DrawingTool. */
    protected OMDrawingTool drawingTool;

    /**
     * A flag to provide a tooltip over OMGraphics to click to edit.
     */
    protected boolean showHints = true;

    /**
     * A property that can control whether mouse events generate hints over map
     * objects (showHints).
     */
    public final static String ShowHintsProperty = "showHints";

    /**
     * A property to specify the file to use to read and save map objects in
     * (file).
     */
    public final static String SerializedURLNameProperty = "file";

    /**
     * The name of the file to read/save OMGraphics in.
     */
    protected String[] fileName;

    protected boolean DTL_DEBUG = false;

    public DrawingToolLayer() {
        // setList(new OMGraphicList());
        setAddToBeanContext(true);

        DTL_DEBUG = Debug.debugging("dtl");
    }

    public void setProperties(String prefix, Properties props) {
        super.setProperties(prefix, props);

        String realPrefix = PropUtils.getScopedPropertyPrefix(prefix);
        showHints = PropUtils.booleanFromProperties(props, realPrefix
                + ShowHintsProperty, showHints);

        if (getMouseModeIDsForEvents() == null) {
            setMouseModeIDsForEvents(new String[] { SelectMouseMode.modeID });
        }

        String fileList = props.getProperty(realPrefix
                + SerializedURLNameProperty);
        if (fileList != null && fileList.trim().length() > 0) {
            fileName = fileList.split(" ");
        }
    }

    public Properties getProperties(Properties props) {
        props = super.getProperties(props);
        String prefix = PropUtils.getScopedPropertyPrefix(this);

        props.put(prefix + ShowHintsProperty, new Boolean(showHints).toString());
        if (fileName != null && fileName.length > 0) {
            for (int i = 0; i < fileName.length; i++) {
                props.put(prefix + SerializedURLNameProperty,
                        PropUtils.unnull(fileName[i]));
            }
        }
        return props;
    }

    public Properties getPropertyInfo(Properties props) {
        props = super.getPropertyInfo(props);

        String interString;

        interString = i18n.get(DrawingToolLayer.class,
                ShowHintsProperty,
                I18n.TOOLTIP,
                "Display tooltips over layer's map objects.");
        props.put(ShowHintsProperty, interString);
        props.put(ShowHintsProperty + ScopedEditorProperty,
                "com.bbn.openmap.util.propertyEditor.YesNoPropertyEditor");
        interString = i18n.get(DrawingToolLayer.class,
                ShowHintsProperty,
                "Show Hints");
        props.put(ShowHintsProperty + LabelEditorProperty, interString);

        interString = i18n.get(DrawingToolLayer.class,
                SerializedURLNameProperty,
                I18n.TOOLTIP,
                "File to use for reading and saving map objects.");
        props.put(SerializedURLNameProperty, interString);
        props.put(SerializedURLNameProperty + ScopedEditorProperty,
                "com.bbn.openmap.util.propertyEditor.FUPropertyEditor");
        interString = i18n.get(DrawingToolLayer.class,
                ShowHintsProperty,
                "File Name for Saving");
        props.put(SerializedURLNameProperty + LabelEditorProperty, interString);

        props.put(initPropertiesProperty, SerializedURLNameProperty + " "
                + ShowHintsProperty);
        return props;
    }

    public OMGraphicList prepare() {
        OMGraphicList list = getList();
        Projection proj = getProjection();
        if (list == null) {
            list = load();
        }
        if (list != null && proj != null) {
            list.generate(proj);
        }

        return list;
    }

    public OMDrawingTool getDrawingTool() {
        return drawingTool;
    }

    public void setDrawingTool(OMDrawingTool dt) {
        drawingTool = dt;
    }

    /**
     * DrawingToolRequestor method.
     */
    public void drawingComplete(OMGraphic omg, OMAction action) {

        if (DTL_DEBUG) {
            String cname = omg.getClass().getName();
            int lastPeriod = cname.lastIndexOf('.');
            if (lastPeriod != -1) {
                cname = cname.substring(lastPeriod + 1);
            }
            Debug.output("DrawingToolLayer: DrawingTool complete for " + cname
                    + " > " + action);
        }
        // First thing, release the proxy MapMouseMode, if there is
        // one.
        releaseProxyMouseMode();

        // GRP, assuming that selection is off.
        OMGraphicList omgl = new OMGraphicList();
        omgl.add(omg);
        deselect(omgl);

        OMGraphicList list = getList();

        if (list == null) {
            list = load();
            setList(list);
        }

        if (list != null) {
            doAction(omg, action);
            repaint();
        } else {
            Debug.error("Layer " + getName() + " received " + omg + " and "
                    + action + " with no list ready");
        }
    }

    /**
     * If the DrawingToolLayer is using a hidden OMDrawingTool, release the
     * proxy lock on the active MapMouseMode.
     */
    public void releaseProxyMouseMode() {
        MapMouseMode pmmm = getProxyMouseMode();
        OMDrawingTool dt = getDrawingTool();
        if (pmmm != null && dt != null) {
            if (pmmm.isProxyFor(dt.getMouseMode())) {
                if (DTL_DEBUG) {
                    Debug.output("DTL: releasing proxy on " + pmmm.getID());
                }

                pmmm.releaseProxy();
                setProxyMouseMode(null);
                fireRequestInfoLine(""); // hidden drawing tool put up
                // coordinates, clean up.
            }

            if (dt.isActivated()) {
                dt.deactivate();
            }
        }
    }

    /**
     * Called by findAndInit(Iterator) so subclasses can find objects, too.
     */
    public void findAndInit(Object someObj) {
        if (someObj instanceof OMDrawingTool) {
            Debug.message("dtl", "DrawingToolLayer: found a drawing tool");
            setDrawingTool((OMDrawingTool) someObj);
        }
    }

    /**
     * BeanContextMembershipListener method. Called when a new object is removed
     * from the BeanContext of this object.
     */
    public void findAndUndo(Object someObj) {
        if (someObj instanceof DrawingTool && getDrawingTool() == someObj) {

            setDrawingTool(null);
        }
    }

    protected MapMouseMode proxyMMM = null;

    /**
     * Set the ProxyMouseMode for the internal drawing tool, if there is one.
     * Can be null. Used to reset the mouse mode when drawing's complete.
     */
    protected synchronized void setProxyMouseMode(MapMouseMode mmm) {
        proxyMMM = mmm;
    }

    /**
     * Get the ProxyMouseMode for the internal drawing tool, if there is one.
     * May be null. Used to reset the mouse mode when drawing's complete.
     */
    protected synchronized MapMouseMode getProxyMouseMode() {
        return proxyMMM;
    }

    /**
     * A method called from within different MapMouseListener methods to check
     * whether an OMGraphic *should* be edited if the OMDrawingTool is able to
     * edit it. Can be used by subclasses to delineate between OMGraphics that
     * are non-relocatable versus those that can be moved. This method should
     * work together with the getToolTipForOMGraphic() method so that OMGraphics
     * that shouldn't be edited don't provide tooltips that suggest that they
     * can be.
     * <P>
     * 
     * By default, this method always returns true because the DrawingToolLayer
     * always thinks the OMGraphic should be edited.
     */
    public boolean shouldEdit(OMGraphic omgr) {
        return true;
    }

    JPanel box;
    JComboBox jcb;

    public Component getGUI() {
        if (box == null) {

            String interString = i18n.get(DrawingToolLayer.class,
                    "QUERY_HEADER",
                    "What would you like to do?");

            box = PaletteHelper.createVerticalPanel(interString);

            GridBagLayout gridbag = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();
            box.setLayout(gridbag);

            jcb = new JComboBox(getActions());
            c.gridx = GridBagConstraints.REMAINDER;
            gridbag.setConstraints(jcb, c);
            box.add(jcb);

            JPanel goPanel = new JPanel();
            gridbag.setConstraints(goPanel, c);
            box.add(goPanel);

            interString = i18n.get(DrawingToolLayer.class, "OK", "OK");
            JButton button = new JButton(interString);
            interString = i18n.get(DrawingToolLayer.class,
                    "OK",
                    I18n.TOOLTIP,
                    "Do action and dismiss window.");
            button.setToolTipText(interString);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    doIt();
                    hidePalette();
                }
            });
            goPanel.add(button);

            interString = i18n.get(DrawingToolLayer.class, "Apply", "Apply");
            button = new JButton(interString);
            interString = i18n.get(DrawingToolLayer.class,
                    "Apply",
                    I18n.TOOLTIP,
                    "Do action and leave window up.");
            button.setToolTipText(interString);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    doIt();
                }
            });
            goPanel.add(button);

            interString = i18n.get(DrawingToolLayer.class, "Cancel", "Cancel");
            button = new JButton(interString);
            interString = i18n.get(DrawingToolLayer.class,
                    "Cancel",
                    I18n.TOOLTIP,
                    "Do nothing and dismiss window.");
            button.setToolTipText(interString);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    hidePalette();
                }
            });
            goPanel.add(button);

        }

        return box;
    }

    protected void doIt() {
        if (jcb != null) {
            ((javax.swing.Action) jcb.getSelectedItem()).actionPerformed(null);
        }
    }

    /**
     * You can override this class if you want to provide more, fewer or
     * different actions to the user.
     * 
     * @return Vector of Actions
     */
    protected Vector getActions() {
        Vector actions = new Vector();

        actions.add(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                saveOMGraphics();
            }

            public String toString() {
                return i18n.get(DrawingToolLayer.class, "SAVE_MAP", "Save map");
            }
        });
        actions.add(new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                OMGraphicList list = getList();
                if (list != null && list.size() > 0) {
                    EsriShapeExport ese = new EsriShapeExport(list, getProjection(), null);
                    ese.export();
                } else {
                    String message = i18n.get(DrawingToolLayer.class,
                            "SHAPE_ERROR_MESSAGE",
                            "There's nothing on the map for this layer to save.");
                    fireRequestMessage(message);
                }
            }

            public String toString() {
                return i18n.get(DrawingToolLayer.class,
                        "SHAPE_SAVE_MAP",
                        "Save map as Shape file(s)");
            }
        });
        actions.add(new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                setList(load());
                doPrepare();
            }

            public String toString() {
                return i18n.get(DrawingToolLayer.class,
                        "RELOAD",
                        "Re-load map from file");
            }
        });
        actions.add(new AbstractAction() {

            public void actionPerformed(ActionEvent e) {
                Inspector inspector = new Inspector();
                inspector.inspectPropertyConsumer(DrawingToolLayer.this);
            }

            public String toString() {
                return i18n.get(DrawingToolLayer.class,
                        "PREFERENCES",
                        "Change preferences");
            }
        });

        return actions;
    }

    /**
     * Get the current OMGraphicList and save it out to the file named in this
     * class. If that's null, the user will be asked for one.
     * 
     */
    public void saveOMGraphics() {
        if (fileName == null) {
            fileName = new String[1];
        }
        if (fileName[0] == null) {
            fileName[0] = FileUtils.getFilePathToSaveFromUser(i18n.get(DrawingToolLayer.class,
                    "CHOOSE_SAVE",
                    "Choose file to use to save layer:"));
        }

        if (fileName[0] != null) {
            OMGraphicList list = getList();

            if (list != null) {
                try {
                    FileOutputStream fos = new FileOutputStream(new File(fileName[0]));
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(list);
                    oos.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Load the data from the file set in this layer.
     * 
     * @return OMGraphicList loaded from fileName.
     */
    public OMGraphicList load() {
        OMGraphicList list = null;

        if (fileName != null) {
            try {
                List graphicList = new ArrayList();
                for (int i = 0; i < fileName.length; i++) {
                    URL url = PropUtils.getResourceOrFileOrURL(fileName[i]);
                    if (url != null) {
                        if (fileName[i].endsWith("shp")) {
                            DbfTableModel dbf = DbfTableModel.getDbfTableModel(PropUtils.getResourceOrFileOrURL(fileName[i].replaceAll(".shp",
                                    ".dbf")));
                            ;
                            list = EsriGraphicList.getEsriGraphicList(url,
                                    null,
                                    dbf);
                        } else {
                            ObjectInputStream ois = new ObjectInputStream(url.openStream());
                            list = (OMGraphicList) ois.readObject();
                            ois.close();
                        }
                        for (int j = 0; j < list.size(); j++) {
                            graphicList.add(list.getOMGraphicAt(j));
                        }
                    }
                }
                OMGraphicList fullList = new OMGraphicList(graphicList);
                return fullList;
            } catch (FileNotFoundException e) {
                if (DTL_DEBUG) {
                    e.printStackTrace();
                }
            } catch (StreamCorruptedException sce) {
                sce.printStackTrace();
                fireRequestMessage(i18n.get(DrawingToolLayer.class,
                        "LOAD_ERROR",
                        "The file doesn't appear to be a valid map file"));
            } catch (IOException e) {
                if (DTL_DEBUG) {
                    e.printStackTrace();
                }
            } catch (ClassNotFoundException e) {
                if (DTL_DEBUG) {
                    e.printStackTrace();
                }
            } catch (ClassCastException cce) {
                if (DTL_DEBUG) {
                    cce.printStackTrace();
                }
            }
        }
        // Something went wrong, we don't want to overwrite something
        // that might potentially be something else if a save is
        // called for later.
        fileName = null;
        return new OMGraphicList();
    }

    /**
     * A flag to provide a tooltip over OMGraphics to click to edit.
     */
    public void setShowHints(boolean show) {
        showHints = show;
    }

    public boolean getShowHints() {
        return showHints;
    }

    /**
     * Query that an OMGraphic can be highlighted when the mouse moves over it.
     * If the answer is true, then highlight with this OMGraphics will be
     * called, in addition to getInfoText() and getToolTipTextFor()
     */
    public boolean isHighlightable(OMGraphic omg) {
        return showHints;
    }

    /**
     * Query that an OMGraphic is selectable.
     */
    public boolean isSelectable(OMGraphic omg) {
        DrawingTool dt = getDrawingTool();
        return (shouldEdit(omg) && dt != null && dt.canEdit(omg.getClass()));
    }

    String editInstruction = i18n.get(DrawingToolLayer.class,
            "CLICK_TO_EDIT",
            "Click to edit.");

    /**
     * Query for what text should be placed over the information bar when the
     * mouse is over a particular OMGraphic.
     */
    public String getInfoText(OMGraphic omg) {
        DrawingTool dt = getDrawingTool();
        if (dt != null && dt.canEdit(omg.getClass())) {
            return editInstruction;
        } else {
            return null;
        }
    }

    /**
     * Query for what tooltip to display for an OMGraphic when the mouse is over
     * it.
     */
    public String getToolTipTextFor(OMGraphic omgr) {
        OMDrawingTool dt = getDrawingTool();
        if (shouldEdit(omgr) && dt.canEdit(omgr.getClass())
                && !dt.isActivated()) {
            return editInstruction;
        } else {
            return null;
        }
    }

    /**
     * GestureResponsePolicy method.
     */
    public void select(OMGraphicList omgl) {
        super.select(omgl);
        if (omgl != null && omgl.size() > 0) {
            if (omgl.size() == 1) {
                edit(omgl.getOMGraphicAt(0));
            } else {
                edit(omgl);
            }
        }
    }

    public void edit(OMGraphic omg) {

        OMDrawingTool dt = getDrawingTool();

        if (dt != null && dt.canEdit(omg.getClass())) {

            // if (dt.isEditing(omg)) {
            // dt.deselect(omg);
            // return;
            // }

            dt.resetBehaviorMask();

            MapMouseMode omdtmm = dt.getMouseMode();
            if (!omdtmm.isVisible()) {
                dt.setMask(OMDrawingTool.PASSIVE_MOUSE_EVENT_BEHAVIOR_MASK);
            }

            MapMouseInterpreter mmi = (MapMouseInterpreter) getMapMouseListener();

            MouseEvent mevent = null;
            if (mmi != null) {
                mevent = mmi.getCurrentMouseEvent();
            }

            if (omg.isSelected()) {
                omg.deselect();
            }

            if (dt.select(omg, this, mevent)) {
                // OK, means we're editing - let's lock up the
                // MouseMode
                if (DTL_DEBUG) {
                    Debug.output("DTL: starting edit of OMGraphic...");
                }

                // Check to see if the DrawingToolMouseMode wants to
                // be invisible. If it does, ask the current
                // active MouseMode to be the proxy for it...
                if (!omdtmm.isVisible() && mevent instanceof MapMouseEvent) {
                    MapMouseMode mmm = ((MapMouseEvent) mevent).getMapMouseMode();
                    if (mmm.actAsProxyFor(omdtmm,
                            MapMouseSupport.PROXY_DISTRIB_MOUSE_MOVED
                                    & MapMouseSupport.PROXY_DISTRIB_MOUSE_DRAGGED)) {
                        if (DTL_DEBUG) {
                            Debug.output("DTL: Setting " + mmm.getID()
                                    + " as proxy for drawing tool");
                        }
                        setProxyMouseMode(mmm);
                    } else {
                        // WHOA, couldn't get proxy lock - bail
                        if (DTL_DEBUG) {
                            Debug.output("DTL: couldn't get proxy lock on "
                                    + mmm.getID()
                                    + " deactivating internal drawing tool");
                        }
                        dt.deactivate();
                    }
                } else {
                    if (DTL_DEBUG) {
                        Debug.output("DTL: MouseMode wants to be visible("
                                + (omdtmm.isVisible())
                                + "), or MouseEvent is not a MapMouseEvent("
                                + !(mevent instanceof MapMouseEvent) + ")");
                    }
                }
            } else {
                if (DTL_DEBUG) {
                    Debug.output("DTL.edit: dt.select returns false, avoiding modification over "
                            + omg.getClass().getName());
                }
            }
        }
    }

    public String getFileName() {
        return fileName[0];
    }

    public void setSFileName(String serializedFile) {
        this.fileName[0] = serializedFile;
    }
}
