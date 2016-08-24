package org.oakimsoft;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

import org.oakimsoft.common.JNavixActions;
import org.oakimsoft.painting.CPaintPoint;

import com.bbn.openmap.LatLonPoint;

public class CJNavixLayerDrawBoard extends CJNaviXLayer {
	public ArrayList<CPaintPoint> points;
	public Color currentColor = new Color(Color.BLUE.getRGB());

	public JLabel jlbl = new JLabel();
	public JButton jbtnSave = new JButton();
	public JButton jbtnColor = new JButton();
	public JButton jbtnClear = new JButton();
	public JButton jbtnOpen = new JButton();
	public JButton jbtnProps = new JButton();

	public CJNavixLayerDrawBoard() {
		this.layerType = 2;
		this.visible = true;
		this.comment = "Sketch";
		points = new ArrayList<CPaintPoint>(10);

		jlbl.setText("Sketch ");
		jlbl.setForeground(Color.blue);
		this.toolbar.add(this.jlbl);

		jbtnOpen.setText("Open");
		jbtnOpen.setActionCommand(JNavixActions.UI_DrawBoardBtnOpen);
		jbtnOpen.setIcon(new ImageIcon(this.getClass().getResource("/imgs16x16/fileopen.png")));
		this.toolbar.add(this.jbtnOpen);

		jbtnSave.setText("Save");
		jbtnSave.setActionCommand(JNavixActions.UI_DrawBoardBtnSave);
		jbtnSave.setIcon(new ImageIcon(this.getClass().getResource("/imgs16x16/filesave.png")));
		this.toolbar.add(this.jbtnSave);

		jbtnColor.setText("Color");
		jbtnColor.setActionCommand(JNavixActions.UI_DrawBoardBtnColor);
		this.toolbar.add(this.jbtnColor);

		jbtnClear.setText("Clear");
		jbtnClear.setActionCommand(JNavixActions.UI_DrawBoardBtnClear);
		this.toolbar.add(this.jbtnClear);

		jbtnProps.setText("Properties");
		jbtnProps.setActionCommand(JNavixActions.UI_DrawBoardBtnProperties);
		jbtnProps.setIcon(new ImageIcon(this.getClass().getResource("/imgs16x16/document-properties.png")));

		this.toolbar.add(this.jbtnProps);
	}

	public Color getCurrentColor() {
		return this.currentColor;
	}

	public void setCurrentColor(Color vColor) {
		this.currentColor = vColor;
	}

	public void clear() {
		points.clear();
		this.minDD.setLatLon(0d, 0d);
		this.maxDD.setLatLon(0d, 0d);
	}

	public void paintLayer(Graphics2D g2d) {
		g2d.setStroke(new BasicStroke(5,1,1));
		Iterator<CPaintPoint> it = this.points.iterator();
		if (this.points.size() > 0) {

			CPaintPoint cppStart = points.get(0);
			g2d.setColor(cppStart.color);
			Point pntStart = new Point(this.parametersLink.mapCenterMeracatorPoint.forward(cppStart.point));
			Point pntFinish = new Point();
			int i = 0;
			while (it.hasNext()) {
				i++;
				if (i == 1)
					continue;
				CPaintPoint cpp = it.next();
				g2d.setColor(cppStart.color);
				pntFinish = this.parametersLink.mapCenterMeracatorPoint.forward(cpp.point);
				if (!cppStart.endLine)
					g2d.drawLine(pntStart.x, pntStart.y, pntFinish.x, pntFinish.y);

				pntStart = pntFinish;
				cppStart = cpp;
			}
		}
	}

	public void mouseDragged(MouseEvent me) {
		super.mouseDragged(me);
		if (me.getModifiers() == 16) {
			me.translatePoint((int) (this.parametersLink.mapCenterMeracatorPoint.getWidth() / 2), (int) (this.parametersLink.mapCenterMeracatorPoint.getHeight() / 2));
			LatLonPoint llp = new LatLonPoint(this.parametersLink.mapCenterMeracatorPoint.inverse(me.getX(), me.getY()));
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
			CPaintPoint cpp = new CPaintPoint();
			cpp.color = this.currentColor;
			cpp.endLine = false;
			cpp.point = llp;
			points.add(cpp);
		}

	}

	public void mouseReleased(MouseEvent me) {
		super.mouseReleased(me);
		if (me.getModifiers() == 16) {
			me.translatePoint((int) (this.parametersLink.mapCenterMeracatorPoint.getWidth() / 2), (int) (this.parametersLink.mapCenterMeracatorPoint.getHeight() / 2));
			LatLonPoint llp = new LatLonPoint(this.parametersLink.mapCenterMeracatorPoint.inverse(me.getX(), me.getY()));
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
			CPaintPoint cpp = new CPaintPoint();
			cpp.color = this.currentColor;
			cpp.endLine = true;
			cpp.point = llp;
			points.add(cpp);
		}
	}

}
