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
// $Source: /cvs/distapps/openmap/src/openmap/com/bbn/openmap/util/propertyEditor/MultiDirectoryPropertyEditor.java,v $
// $RCSfile: MultiDirectoryPropertyEditor.java,v $
// $Revision: 1.4.2.4 $
// $Date: 2005/05/24 18:38:30 $
// $Author: dietrick $
// 
// **********************************************************************

package com.bbn.openmap.util.propertyEditor;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFileChooser;

/**
 * A PropertyEditor that brings up a JFileChooser panel that allows
 * the user to choose one or more directories. The user can also enter
 * information in the text field, and pressing the add button will
 * bring up a file chooser. Anything chosen in the file chooser will
 * be appended to what is currently in the text field.
 */
public class MultiDirectoryPropertyEditor extends FilePropertyEditor {

    protected char pathSeparator;

    /** Create MultiDirectoryPropertyEditor. */
    public MultiDirectoryPropertyEditor() {
        setPathSeparator(';');
    }

    public String getButtonTitle() {
        return "Add";
    }

    /**
     * Internal callback method that can be overridden by subclasses.
     * 
     * @return true for MultiDirectoryPropertyEditor.
     */
    public boolean isTextFieldEditable() {
        return true;
    }

    /**
     * Internal callback method that can be overridden by subclasses.
     * 
     * @return JFileChooser.DIRECTORIES_ONLY for MultiDirectoryPropertyEditor.
     */
    public int getFileSelectionMode() {
        return JFileChooser.DIRECTORIES_ONLY;
    }

    /**
     * Internal callback method that can be overridden by subclasses.
     * 
     * @return true for MultiDirectoryPropertyEditor.
     */
    public boolean isMultiSelectEnabled() {
        return true;
    }
    
    /**
     * Set the character to use when appending paths.
     */
    public void setPathSeparator(char c) {
        pathSeparator = c;
    }

    public char getPathSeparator() {
        return pathSeparator;
    }

    public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = getFileChooser();
        int returnVal = chooser.showOpenDialog((Component) null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {

            File[] choices = chooser.getSelectedFiles();
            for (int i = 0; i < choices.length; i++) {
                String newFilename = choices[i].getAbsolutePath();
                newFilename = cleanUpName(newFilename);
                append(newFilename);
            }
            firePropertyChange();
        }
    }

    /**
     * Add a path to the end of the current path. Uses the
     * pathSeparator between paths.
     */
    public void append(String addPath) {
        String currentPath = textField.getText();
        if (currentPath.equals("")) {
            setValue(addPath);
        } else {
            setValue(currentPath.concat(";" + addPath));
        }
    }
}