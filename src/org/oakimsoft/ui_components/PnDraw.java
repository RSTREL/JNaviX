    /*
Class MapDrawPanel
Copyright (C) 2009  Oakim

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package org.oakimsoft.ui_components;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.logging.Logger;

import javax.swing.JPanel;

import org.oakimsoft.CJNavixGlobalManager;
import org.oakimsoft.common.ActionObserver;
import org.oakimsoft.common.CLog;
import org.oakimsoft.common.JNavixActions;

/**
 * Расширение JPanel для отрисовки карты.
 * @author Oakim
 */
public class PnDraw extends JPanel implements MouseListener,
        KeyListener,
        ActionListener,
        MouseMotionListener,
        MouseWheelListener,
        ActionObserver {
	
	private CJNavixGlobalManager globManager;


    public PnDraw(CJNavixGlobalManager eGlobManager) {
		
    	
    	if (eGlobManager!=null){
    		globManager = eGlobManager;
    		globManager.registerObserver(this);
    		
    	}
    	

        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
        this.addKeyListener(this);

        this.setDoubleBuffered(true);
        //this.setIgnoreRepaint(true);
        this.setOpaque(false);

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Rectangle rr = this.getBounds();

        if (globManager != null){
        	if (globManager.LayerManager.focusedLayer != null){
        		g.drawImage(globManager.biBase, -(globManager.biBase.getWidth() - rr.width) / 2 - globManager.LayerManager.focusedLayer.offsetDrawing.x , 
        				                        -(globManager.biBase.getHeight() - rr.height) / 2 - globManager.LayerManager.focusedLayer.offsetDrawing.y , null);
        	}else{
        		g.drawImage(globManager.biBase, -(globManager.biBase.getWidth() - rr.width) / 2, -(globManager.biBase.getHeight() - rr.height) / 2, null);
        	}
        }
    }


    public void mouseClicked(MouseEvent me) {
    	me.translatePoint((int)(-this.getWidth()/2), (int)(-this.getHeight()/2));
    	this.globManager.mouseClicked(me);
    }

    public void mouseEntered(MouseEvent me) {
    	me.translatePoint((int)(-this.getWidth()/2), (int)(-this.getHeight()/2));
    	this.globManager.mouseEntered(me);
    }

    public void mouseExited(MouseEvent me) {
    	me.translatePoint((int)(-this.getWidth()/2), (int)(-this.getHeight()/2));
    	this.globManager.mouseExited(me);
    }

    public void mousePressed(MouseEvent me) {
    	me.translatePoint((int)(-this.getWidth()/2), (int)(-this.getHeight()/2));
    	this.globManager.mousePressed(me);
    }

    public void mouseReleased(MouseEvent me) {
    	me.translatePoint((int)(-this.getWidth()/2), (int)(-this.getHeight()/2));
    	this.globManager.mouseReleased(me);
    }

    public void mouseDragged(MouseEvent me) {
    	me.translatePoint((int)(-this.getWidth()/2), (int)(-this.getHeight()/2));
    	this.globManager.mouseDragged(me);
    	
    }

    public void mouseMoved(MouseEvent me) {
    	me.translatePoint((int)(-this.getWidth()/2), (int)(-this.getHeight()/2));
    	this.globManager.mouseMoved(me);
    }

    public void actionPerformed(ActionEvent ae) {
    	this.globManager.actionPerformed(ae);

    }

    public void mouseWheelMoved(MouseWheelEvent e) {
    	this.globManager.mouseWheelMoved(e);
    }
    ;

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
    }

    ;

    public void keyReleased(KeyEvent e) {
    }
    

    @Override
	public void subscribedAction(ActionEvent ae) {
		if (ae.getActionCommand().startsWith(JNavixActions.UI_UpdateMapPicture)) {
			this.repaint();
			CLog.info(this, "Done action "+JNavixActions.UI_UpdateMapPicture);

		}

	}
    
    
    

    ;

}
