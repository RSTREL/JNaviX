/**
 *  управляющий класс, передается в UI компоненты.
 *  В него передаются команды с кнопок.. Он отдает приказы на менеджер слоев
 */
package org.oakimsoft;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.oakimsoft.common.ActionObserver;
import org.oakimsoft.common.CLog;
import org.oakimsoft.common.JNavixActions;
import org.oakimsoft.data_type_support.CTrackParameters;


public class CJNavixGlobalManager implements ActionListener, MouseListener, MouseMotionListener, MouseWheelListener {
	public CJNaviXLayerManager LayerManager;
	public BufferedImage biMainCanvas;
	public Dimension myScreenDim;
	public JToolBar tbMain = new JToolBar();
	public  CTrackParameters trackParameters = new  CTrackParameters();
	public TreeMap<String,String> registry = new TreeMap<String,String>();
	

	
    /**
	 * Базовая канва для отображения
	 */
	public BufferedImage biBase; // Основа изображения (без поверхностных
									// рисований)
	
	private ArrayList<ActionObserver> observers;

	public CJNavixGlobalManager() {
		// Очистка рассылки сообщений
		observers = new ArrayList<ActionObserver>(10);
		observers.clear();
		
	
		// Размер экрана
		Toolkit tk = Toolkit.getDefaultToolkit();
		myScreenDim = tk.getScreenSize();
		
		// плюс базовая канва, на которую всё будет рисоваться
		biBase = new BufferedImage(myScreenDim.width, myScreenDim.height,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = (Graphics2D) biBase.getGraphics();
		g2d.setBackground(Color.white);
		g2d.clearRect(0, 0, myScreenDim.width, myScreenDim.height);
		
		
		trackParameters.add(0, 40, Color.darkGray , true);
		trackParameters.add(40, 60, Color.green , true);
		trackParameters.add(60, 90, Color.yellow , true);
		trackParameters.add(90, 500, Color.red , true);
		

	}

	public void registerObserver(ActionObserver ao) {
		this.observers.add(ao);
	}

	public void removeObserver(ActionObserver ao) {
		int i = observers.indexOf(ao);
		if (i >= 0)
			observers.remove(i);
	}

	public void sendCommnad(String eCommand) {

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		
		CLog.info(this,"Start of action "+e.getActionCommand());  
		




		if (e.getActionCommand().equals(JNavixActions.APP_LayerAddMap)) {
			LayerManager.addNewLayer(1, this.myScreenDim);
			this.updatePicture();
			this.actionPerformed(new ActionEvent(this,MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateToolbar));			
			this.actionPerformed(new ActionEvent(this,MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateInfoPanel));	
			this.actionPerformed(new ActionEvent(this,MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateMapPicture));	
		}
		
		
		if (e.getActionCommand().equals(JNavixActions.APP_LayerAddSketch)) {
			LayerManager.addNewLayer(2, this.myScreenDim);
			this.updatePicture();
			this.actionPerformed(new ActionEvent(this,MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateToolbar));			
			this.actionPerformed(new ActionEvent(this,MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateInfoPanel));			
			this.actionPerformed(new ActionEvent(this,MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateMapPicture));	
		}
		
		
		
		
		if (e.getActionCommand().equals(JNavixActions.APP_LayerAddGPSTrack)) {
			LayerManager.addNewLayer(3, this.myScreenDim);
			this.updatePicture();
			this.actionPerformed(new ActionEvent(this,MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateToolbar));			
			this.actionPerformed(new ActionEvent(this,MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateInfoPanel));			
			this.actionPerformed(new ActionEvent(this,MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateMapPicture));	
		}
	
		
		if (e.getActionCommand().startsWith(JNavixActions.APP_LayerFocused)) {
			String[] ls = e.getActionCommand().split("\\|");
			LayerManager.setFocusedLayer(Integer.valueOf(ls[1]));
			this.updatePicture();
			this.actionPerformed(new ActionEvent(this,MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateToolbar));			
			this.actionPerformed(new ActionEvent(this,MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateInfoPanel));
			this.actionPerformed(new ActionEvent(this,MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateMapPicture));	
		}
		
		

		if (e.getActionCommand().startsWith(JNavixActions.APP_LayerUpFocused)) {
			LayerManager.moveFocusedUp();
			this.updatePicture();
			this.actionPerformed(new ActionEvent(this,MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateToolbar));			
			this.actionPerformed(new ActionEvent(this,MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateInfoPanel));			
			this.actionPerformed(new ActionEvent(this,MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateMapPicture));	
		}

		if (e.getActionCommand().startsWith(JNavixActions.APP_LayerDnFocused)) {
			LayerManager.moveFocusedDown();
			this.updatePicture();
			this.actionPerformed(new ActionEvent(this,MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateToolbar));			
			this.actionPerformed(new ActionEvent(this,MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateInfoPanel));			
			this.actionPerformed(new ActionEvent(this,MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateMapPicture));	
		}


		/***************************************************************
		 * DrawBoard команды
		 ****************************************************************/
		if (e.getActionCommand().equals(JNavixActions.UI_DrawBoardBtnColor)) {
			JDialog jdlg = new JDialog() ;
			Color color1 = JColorChooser.showDialog(jdlg, "Цвет линии", this.LayerManager.getFocusedColor()); 			
			if (color1!=null){
     			this.LayerManager.setFocusedColor(color1);
			}	
		}
		if (e.getActionCommand().equals(JNavixActions.UI_DrawBoardBtnSave)) {
			JDialog jdlg = new JDialog() ;
		    JFileChooser chooser = new JFileChooser();
		    
		    FileNameExtensionFilter filter = new FileNameExtensionFilter(
		        "SketchFile", "sketch");
		    chooser.setFileFilter(filter);
		    chooser.setDialogTitle("Save Sketch as file...");
		    chooser.setApproveButtonText("Save");		    
		    if (this.registry.get(JNavixActions.UI_DrawBoardBtnSave)!=null)
		    	chooser.setCurrentDirectory(new File(this.registry.get(JNavixActions.UI_DrawBoardBtnSave)));		    
		    int returnVal = chooser.showOpenDialog(jdlg);
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		       System.out.println("You chose to open this file: " +
		            chooser.getSelectedFile().getName());
		       this.registry.put(JNavixActions.UI_DrawBoardBtnSave,chooser.getSelectedFile().getAbsolutePath() );
		    }
		}
		
		if (e.getActionCommand().equals(JNavixActions.UI_DrawBoardBtnOpen)) {
			JDialog jdlg = new JDialog() ;
		    JFileChooser chooser = new JFileChooser();
		    FileNameExtensionFilter filter = new FileNameExtensionFilter(
		        "SketchFile", "sketch");
		    chooser.setFileFilter(filter);
		    chooser.setDialogTitle("Open Sketch file...");
		    if (this.registry.get(JNavixActions.UI_DrawBoardBtnOpen)!=null)
		    	chooser.setCurrentDirectory(new File(this.registry.get(JNavixActions.UI_DrawBoardBtnOpen)));
		    
		    int returnVal = chooser.showOpenDialog(jdlg);
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		       System.out.println("You chose to open this file: " +
		            chooser.getSelectedFile().getName());
		       this.registry.put(JNavixActions.UI_DrawBoardBtnOpen,chooser.getSelectedFile().getAbsolutePath() );
		    }
		}		

		
		if (e.getActionCommand().equals(JNavixActions.UI_DrawBoardBtnClear)) {
			this.LayerManager.setFocusedClear();
			this.actionPerformed(new ActionEvent(this,MouseEvent.MOUSE_CLICKED, JNavixActions.APP_UpdateMapPicture));			
			this.actionPerformed(new ActionEvent(this,MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateMapPicture));	
		}
		

		/***************************************************************
		 * Unimap команды
		 ****************************************************************/		
		if (e.getActionCommand().equals(JNavixActions.UI_UnimapBtnOpen)) {
			JDialog jdlg = new JDialog() ;
		    JFileChooser chooser = new JFileChooser();
		    
		    FileNameExtensionFilter filter = new FileNameExtensionFilter(
		        "Polish format *.mp ", "mp");
		    chooser.setFileFilter(filter);
		    
		    FileNameExtensionFilter filterOSM = new FileNameExtensionFilter(
			        "OpenStreetMap *.osm", "osm");
			chooser.setFileFilter(filterOSM);
		    
		    chooser.setDialogTitle("Open map file...");
		    if (this.registry.get(JNavixActions.UI_UnimapBtnOpen)!=null)
		    	chooser.setCurrentDirectory(new File(this.registry.get(JNavixActions.UI_UnimapBtnOpen)));
		    int returnVal = chooser.showOpenDialog(jdlg);
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		       this.registry.put(JNavixActions.UI_UnimapBtnOpen,chooser.getSelectedFile().getAbsolutePath() );
		       LayerManager.loadMapFile(chooser.getSelectedFile());
		       LayerManager.focusedLayer.comment = chooser.getSelectedFile().getName();
		       this.actionPerformed(new ActionEvent(this,MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateLayersList));			
		       this.actionPerformed(new ActionEvent(this,MouseEvent.MOUSE_CLICKED, JNavixActions.UI_MainBtnAutocenter));			
		       
	       
		    }
		}		
		
		

		/***************************************************************
		 * GPSTrack команды
		 ****************************************************************/		
		if (e.getActionCommand().equals(JNavixActions.UI_GPSTrackBtnOpen)) {
			JDialog jdlg = new JDialog() ;
		    JFileChooser chooser = new JFileChooser();
		    
		    FileNameExtensionFilter filter = new FileNameExtensionFilter(
		        "Track (*.gpx,*.nm)", "gpx","nm");
		    chooser.setFileFilter(filter);
		    
		    chooser.setDialogTitle("Open track file...");
		    if (this.registry.get(JNavixActions.UI_GPSTrackBtnOpen)!=null)
		    	chooser.setCurrentDirectory(new File(this.registry.get(JNavixActions.UI_GPSTrackBtnOpen)));
		    int returnVal = chooser.showOpenDialog(jdlg);
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		       this.registry.put(JNavixActions.UI_GPSTrackBtnOpen,chooser.getSelectedFile().getAbsolutePath() );
		       LayerManager.loadGPSTrackFile(chooser.getSelectedFile());
		       LayerManager.focusedLayer.comment = chooser.getSelectedFile().getName();
		       this.actionPerformed(new ActionEvent(this,MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateLayersList));			
		       this.actionPerformed(new ActionEvent(this,MouseEvent.MOUSE_CLICKED, JNavixActions.UI_MainBtnAutocenter));			
		       
		    }
		}			
		
		
		/***************************************************************
		 * Main команды
		 ****************************************************************/		
		if (e.getActionCommand().equals(JNavixActions.UI_MainBtnAutocenter)) {
			LayerManager.setFocusedLayerAutocenter();
			this.actionPerformed(new ActionEvent(this,MouseEvent.MOUSE_CLICKED, JNavixActions.APP_UpdateMapPicture));			
			this.actionPerformed(new ActionEvent(this,MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateMapPicture));
			this.actionPerformed(new ActionEvent(this,MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateInfoPanel));
			return;			
		}
		
		
		
		
		
		if (e.getActionCommand().startsWith(JNavixActions.UI_CallCellClicked)) {
			String[] ls = e.getActionCommand().split("\\|");
			LayerManager.setFocusedLayer(Integer.valueOf(ls[2]));
			if (ls[1].equals("2")) {
				LayerManager
						.setFocusedVisible(!LayerManager.isFocusedVisible());
				this.updatePicture();
				
			}
			if (ls[1].equals("3")) {
				LayerManager.setFocusedOwnParams(!LayerManager
						.isFocusedUseOwnParams());
				this.updatePicture();
			}
		}

		if (e.getActionCommand().startsWith(JNavixActions.APP_LayerRemoveFocused)) {
			LayerManager.removeFocused();
			this.updatePicture();
			this.actionPerformed(new ActionEvent(this,MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateToolbar));
			this.actionPerformed(new ActionEvent(this,MouseEvent.MOUSE_CLICKED, JNavixActions.APP_UpdateMapPicture));			
			this.actionPerformed(new ActionEvent(this,MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateMapPicture));			
			
		}
		
		
		if (e.getActionCommand().equals(JNavixActions.APP_UpdateMapPicture)) {
			this.updatePicture();
	    }
			
		
		

		// Рассылка сообщений подписчикам
		for (Iterator<ActionObserver> i = observers.iterator(); i.hasNext();) {
			ActionObserver l = i.next();
			try {
				l.subscribedAction(e);
			} catch (RuntimeException exc) {
				i.remove();
			}
		}
	}

	public void mouseClicked(MouseEvent me) {
		if (LayerManager != null){
			if (LayerManager.focusedLayer != null){
				if (LayerManager.focusedLayer.visible)
				LayerManager.focusedLayer.mouseClicked(me);	
				
			}
		}
	}

	public void mouseEntered(MouseEvent me) {
		if (LayerManager != null){
			if (LayerManager.focusedLayer != null){
				if (LayerManager.focusedLayer.visible)
				LayerManager.focusedLayer.mouseEntered(me);		
			}
		}
	}

	public void mouseExited(MouseEvent me) {
		if (LayerManager != null){
			if (LayerManager.focusedLayer != null){
				if (LayerManager.focusedLayer.visible)
				LayerManager.focusedLayer.mouseExited(me);		
			}
		}
	}

	public void mousePressed(MouseEvent me) {
		if (LayerManager != null){
			if (LayerManager.focusedLayer != null){
				if (LayerManager.focusedLayer.visible)
				LayerManager.focusedLayer.mousePressed(me);		
//				this.actionPerformed(new ActionEvent(this,MouseEvent.MOUSE_CLICKED, JNavixActions.APP_UpdateMapPicture));			
//				this.actionPerformed(new ActionEvent(this,MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateMapPicture));			
			}
		}
	}

	public void mouseReleased(MouseEvent me) {
		if (LayerManager != null){
			if (LayerManager.focusedLayer != null){
				if (LayerManager.focusedLayer.visible)
				LayerManager.focusedLayer.mouseReleased(me);	
				this.actionPerformed(new ActionEvent(this,MouseEvent.MOUSE_CLICKED, JNavixActions.APP_UpdateMapPicture));			
				this.actionPerformed(new ActionEvent(this,MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateMapPicture));			
     		}
		}
	}

	public void mouseDragged(MouseEvent me) {
		if (LayerManager != null){
			if (LayerManager.focusedLayer != null){
				if (LayerManager.focusedLayer.visible)
				LayerManager.focusedLayer.mouseDragged(me);		
				this.actionPerformed(new ActionEvent(this,MouseEvent.MOUSE_CLICKED, JNavixActions.APP_UpdateMapPicture));			
				this.actionPerformed(new ActionEvent(this,MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateMapPicture));	
			}
		}
	}

	public void mouseMoved(MouseEvent me) {
		if (LayerManager != null){
			if (LayerManager.focusedLayer != null){
				if (LayerManager.focusedLayer.visible)
				LayerManager.focusedLayer.mouseMoved(me);		
			}
		}
	}
	

    public void mouseWheelMoved(MouseWheelEvent mwe) {
    	if (LayerManager != null){
			if (LayerManager.focusedLayer != null){
				if (LayerManager.focusedLayer.visible)
				LayerManager.focusedLayer.mouseWheelMoved(mwe);		
			}
		}
    	
    }
    ;

    public void keyTyped(KeyEvent e) {
		if (LayerManager != null){
			if (LayerManager.focusedLayer != null){
				if (LayerManager.focusedLayer.visible)
				LayerManager.focusedLayer.keyTyped(e);		
			}
		}
    }

    public void keyPressed(KeyEvent e) {
		if (LayerManager != null){
			if (LayerManager.focusedLayer != null){
				if (LayerManager.focusedLayer.visible)
				LayerManager.focusedLayer.keyPressed(e);		
			}
		}
    }


    public void keyReleased(KeyEvent e) {
		if (LayerManager != null){
			if (LayerManager.focusedLayer != null){
				if (LayerManager.focusedLayer.visible)
				LayerManager.focusedLayer.keyReleased(e);		
			}
		}
    	
    }
    
    public void clearPicture(Graphics2D g2d){
    	g2d.setBackground(Color.white);
    	g2d.clearRect(0, 0, this.myScreenDim.width, this.myScreenDim.height);
    	
    }
    


    public void updatePicture( ){
    	this.clearPicture((Graphics2D)(this.biBase.getGraphics()));
    	LayerManager.updatePicture((Graphics2D)(this.biBase.getGraphics()));
  	
    }
    

    
    

}
