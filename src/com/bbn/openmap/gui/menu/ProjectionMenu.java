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
// $Source: /cvs/distapps/openmap/src/openmap/com/bbn/openmap/gui/menu/ProjectionMenu.java,v $
// $RCSfile: ProjectionMenu.java,v $
// $Revision: 1.3.2.4 $
// $Date: 2007/01/26 14:12:30 $
// $Author: dietrick $
// 
// **********************************************************************

package com.bbn.openmap.gui.menu;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.event.ProjectionListener;
import com.bbn.openmap.event.ProjectionSupport;
import com.bbn.openmap.gui.AbstractOpenMapMenu;
import com.bbn.openmap.proj.Projection;
import com.bbn.openmap.proj.ProjectionException;
import com.bbn.openmap.proj.ProjectionFactory;
import com.bbn.openmap.proj.ProjectionLoader;
import com.bbn.openmap.util.Debug;


/**
 * Provides ProjectionMenu items for selecting Projection type.
 */
public class ProjectionMenu extends AbstractOpenMapMenu implements
        ActionListener, ProjectionListener, PropertyChangeListener {

    public static final String defaultText = "Projection";

    protected transient ProjectionSupport projectionSupport = new ProjectionSupport(this);
    protected transient Projection projection;
    protected transient Component projComponent;
    public final static transient String projCmd = "setProj";

    /**
     * Create the projection submenu.
     */
    public ProjectionMenu() {
        super();
        setText(i18n.get(this, "projectionMenu", defaultText));

    }

    public void configure(List loaders) {
        removeAll();
        JRadioButtonMenuItem rb;
        ButtonGroup group = new ButtonGroup();

        for (Iterator it = loaders.iterator(); it.hasNext();) {
            Object obj = it.next();
            if (obj instanceof ProjectionLoader) {
                ProjectionLoader pl = (ProjectionLoader) obj;

                rb = new JRadioButtonMenuItem(pl.getPrettyName());
                rb.setActionCommand(projCmd);
                String plclassname = pl.getProjectionClass().getName();
                rb.setName(plclassname);
                rb.setToolTipText(pl.getDescription());
                rb.addActionListener(this);
                group.add(rb);
                add(rb);
            }
        }

        setProjection(projection);
    }

    public void actionPerformed(ActionEvent ae) {
        String command = ae.getActionCommand();

        Debug.message("projectionmenu", "ProjectionMenu.actionPerformed(): "
                + command);

        if (command == projCmd) {
            JRadioButtonMenuItem rb = (JRadioButtonMenuItem) (ae.getSource());
            String projclassname = rb.getName();
            Debug.message("projectionmenu", "ProjectionMenu new proj name: "
                    + projclassname);
            try {
                Projection newProj = ProjectionFactory.makeProjection(projclassname,
                        projection);
                fireProjectionChanged(newProj);
            } catch (ProjectionException pe) {
                rb.setEnabled(false);
            }
        }
    }

    public void propertyChange(PropertyChangeEvent pce) {
        if (pce.getPropertyName() == ProjectionFactory.AvailableProjectionProperty) {
            configure((List) pce.getNewValue());
        }
    }

    //------------------------------------------------------------
    // ProjectionListener interface
    //------------------------------------------------------------

    /**
     * The Map projection has changed, in order to baseline new
     * changes as a result of menu options being selected.
     * 
     * @param e ProjectionEvent
     */
    public void projectionChanged(ProjectionEvent e) {
        if (Debug.debugging("projectionmenu")) {
            System.out.println("ProjectionMenu.projectionChanged()");
        }

        Projection newProj = e.getProjection();
        if (projection == null || (!projection.equals(newProj))) {
            setProjection((Projection) newProj.makeClone());
            Object source = e.getSource();
            if (source instanceof Component) {
                projComponent = (Component) source;
            }
        }
    }

    /**
     * Set the projection. This changes the setting of the projection
     * radio button menu.
     * 
     * @param aProjection Projection
     */
    protected synchronized void setProjection(Projection aProjection) {
        projection = aProjection;

        if (projection == null) {
            return;
        }

        String newProjClassName = projection.getClass().getName();

        // Change the selected projection type menu item
        for (int i = 0; i < getItemCount(); i++) {
            JMenuItem item = getItem(i);
            if (newProjClassName.equals(item.getName())) {
                if (Debug.debugging("projectionmenu")) {
                    Debug.output("ProjectionMenu | setting " + item.getName()
                            + " as active");
                }
                item.setSelected(true);
                return;
            }
        }
    }

    /**
     * Convenience function for setting up listeners
     */
    public void setupListeners(MapBean map) {
        Debug.message("projectionmenu", "ProjectionMenu | setupListeners");
        addProjectionListener(map);
        map.addProjectionListener(this);
    }

    /**
     * Convenience function for undoing set up listeners
     */
    public void undoListeners(MapBean map) {
        removeProjectionListener(map);
        map.removeProjectionListener(this);
    }

    /*----------------------------------------------------------------------
     * Projection Support - for broadcasting projection changed events
     *----------------------------------------------------------------------*/
    /**
     * Add a ProjectionListener to this menu and its components.
     */
    protected synchronized void addProjectionListener(ProjectionListener l) {
        projectionSupport.addProjectionListener(l);
    }

    /**
     * Remove a ProjectionListener from this menu and its components.
     */
    protected synchronized void removeProjectionListener(ProjectionListener l) {
        projectionSupport.removeProjectionListener(l);
    }

    /**
     * Fire the changed projection from the support.
     */
    public void fireProjectionChanged(Projection p) {
        projectionSupport.fireProjectionChanged(p);
    }

    public void findAndInit(Object someObj) {
        if (someObj instanceof MapBean) {
            setupListeners((MapBean) someObj);
        }
        if (someObj instanceof ProjectionFactory) {
            ((ProjectionFactory) someObj).addPropertyChangeListener(this);
        }
    }

    public void findAndUndo(Object someObj) {
        if (someObj instanceof MapBean) {
            undoListeners((MapBean) someObj);
        }

        if (someObj instanceof ProjectionFactory) {
            ((ProjectionFactory) someObj).removePropertyChangeListener(this);
        }
    }
}
