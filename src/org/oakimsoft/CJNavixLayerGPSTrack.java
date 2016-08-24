package org.oakimsoft;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

import org.oakimsoft.common.CLog;
import org.oakimsoft.common.JNavixActions;
import org.oakimsoft.tracking.CTrackManager;
import org.oakimsoft.tracking.CTrackPoint;
import org.oakimsoft.ui_components.PnLayerManager;

import com.bbn.openmap.LatLonPoint;

public class CJNavixLayerGPSTrack extends CJNaviXLayer {

	public JLabel jlbl = new JLabel();
	public JButton jbtnOpen = new JButton();
	public JButton jbtnProps = new JButton();
	public JButton jbtnAutocenter = new JButton();

	public CTrackManager trackManager = new CTrackManager();

	public CJNavixLayerGPSTrack() {
		this.layerType = 3;
		this.visible = true;
		this.comment = "GPSTrack";

		jlbl.setText("GPSTrack ");
		jlbl.setForeground(Color.blue);
		this.toolbar.add(this.jlbl);

		jbtnOpen.setText("Open");
		jbtnOpen.setActionCommand(JNavixActions.UI_GPSTrackBtnOpen);
		jbtnOpen.setIcon(new ImageIcon(this.getClass().getResource("/imgs16x16/fileopen.png")));
		this.toolbar.add(this.jbtnOpen);

		jbtnProps.setText("Properties");
		jbtnProps.setActionCommand(JNavixActions.UI_GPSTrackBtnProperties);
		jbtnProps.setIcon(new ImageIcon(this.getClass().getResource("/imgs16x16/document-properties.png")));
		this.toolbar.add(this.jbtnProps);		

		jbtnAutocenter.setText("Autocenter");
		jbtnAutocenter.setActionCommand(JNavixActions.UI_MainBtnAutocenter);
		this.toolbar.add(jbtnAutocenter);		
		
		
		
		
	}

	@Override
	public void updateStatistics() {
		super.updateStatistics();
		Iterator<CTrackPoint> it1 = trackManager.alTrackData.iterator();
		int i = 0;
		while (it1.hasNext()) {
			CTrackPoint ctp = it1.next();
			LatLonPoint llp = new LatLonPoint(ctp.gpgga.Latitude, ctp.gpgga.Longitude);
			if (i == 0) {
				this.minDD.setLatLon(llp);
				this.maxDD.setLatLon(llp);
			}
			i++;
			if (llp.getLatitude() < this.minDD.getLatitude()) {
				this.minDD.setLatitude(llp.getLatitude());
			}
			if (llp.getLatitude() > this.maxDD.getLatitude()) {
				this.maxDD.setLatitude(llp.getLatitude());
			}
			if (llp.getLongitude() < this.minDD.getLongitude()) {
				this.minDD.setLongitude(llp.getLongitude());
			}
			if (llp.getLongitude() > this.maxDD.getLongitude()) {
				this.maxDD.setLongitude(llp.getLongitude());
			}
		}
	}

	public void paintLayer(Graphics2D g2d) {
		if (this.biBackground == null) {
			this.biBackground = new BufferedImage(myScreenDim.width, myScreenDim.height, BufferedImage.TYPE_INT_ARGB);
		}

		if ((!this.fullRepaint) && (this.paintedParameters.equals(this.parametersLink))) {
			g2d.drawImage(this.biBackground, 0, 0, null);
			this.fullRepaint = false;
			CLog.info(this,"GPSTRack Light Update");			
			return;
		}
		CLog.info(this, "GPSTrack Full update of picture starting");
		Graphics2D localg2d = (Graphics2D) this.biBackground.getGraphics();
		Composite originalComposite = localg2d.getComposite();
		localg2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		Color bgColor = new Color(255, 255, 255, 1);
		localg2d.setBackground(bgColor);
		localg2d.clearRect(0, 0, myScreenDim.width, myScreenDim.height);
		this.paintedParameters.assign(this.parametersLink);

		
		localg2d.setStroke(new BasicStroke(5,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
		Iterator<CTrackPoint> it = this.trackManager.alTrackData.iterator();

		if (this.trackManager.alTrackData.size() > 0) {
			CTrackPoint ctpStart = this.trackManager.alTrackData.get(0);
			localg2d.setColor(Color.GREEN);
			LatLonPoint llp = new LatLonPoint(ctpStart.gpgga.Latitude, ctpStart.gpgga.Longitude);

			Point pntStart = new Point(this.parametersLink.mapCenterMeracatorPoint.forward(llp));
			Point pntFinish = new Point();
			int i = 0;
			while (it.hasNext()) {
				i++;
				if (i == 1)
					continue;
				CTrackPoint ctp = it.next();
				llp = new LatLonPoint(ctp.gpgga.Latitude, ctp.gpgga.Longitude);
				// g2d.setColor(cppStart.color);
				pntFinish = this.parametersLink.mapCenterMeracatorPoint.forward(llp);
				if (ctp.TrackNumber == ctpStart.TrackNumber) {
					localg2d.setColor(Color.GREEN);
					if (ctp.gpgga.timeFromPrev > 0){
						localg2d.setColor(globManager.trackParameters.getColor(ctp.gpgga.distFromPrev / ctp.gpgga.timeFromPrev * 36.0 / 10.0));
						
//						if (ctp.gpgga.distFromPrev / ctp.gpgga.timeFromPrev > 90.0 * 1000.0 / (60.0*60.0) ){
//							localg2d.setColor(Color.RED);
//						}
						
					}
					
					localg2d.drawLine(pntStart.x, pntStart.y, pntFinish.x, pntFinish.y);
				}
				pntStart.setLocation(pntFinish);
				ctpStart = ctp;
			}
		}
		localg2d.setComposite(originalComposite);
		g2d.drawImage(this.biBackground, 0, 0, null);
		
	}

	public void loadGPSTrackFile(File vFile) {
		trackManager.clear();
		trackManager.load(vFile);
	}


	
}
