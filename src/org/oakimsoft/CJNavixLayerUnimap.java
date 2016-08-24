package org.oakimsoft;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

import org.oakimsoft.common.CLog;
import org.oakimsoft.common.JNavixActions;
import org.oakimsoft.common.JNavixColors;
import org.oakimsoft.mp_file.CMPFile;
import org.oakimsoft.mp_file.CMPFile.CMPRecord;
import org.oakimsoft.osm_file.OSMFile;
import org.oakimsoft.osm_file.OSMPaintCfg;
import org.oakimsoft.osm_file.OSMPaintCfg.CPaintingRec;
import org.oakimsoft.osm_file.OSMWay;

import com.bbn.openmap.LatLonPoint;

public class CJNavixLayerUnimap extends CJNaviXLayer {

	public JButton		jbtnOpen		= new JButton();
	public JButton		jbtnProps		= new JButton();
	public JButton		jbtnAutocenter	= new JButton();
	public JLabel		jlbl			= new JLabel();

	public CMPFile		mpFile			= null;
	public OSMFile		osmFile			= null;
	public OSMPaintCfg	osmFileCfg		= null;

	public CJNavixLayerUnimap() {
		this.layerType = 1;
		this.visible = true;
		this.comment = "Map";

		jlbl.setText("Map ");
		jlbl.setForeground(Color.blue);
		this.toolbar.add(this.jlbl);

		jbtnOpen.setText("Open");
		jbtnOpen.setActionCommand(JNavixActions.UI_UnimapBtnOpen);
		jbtnOpen.setIcon(new ImageIcon(this.getClass().getResource("/imgs16x16/fileopen.png")));
		this.toolbar.add(this.jbtnOpen);

		jbtnProps.setText("Properties");
		jbtnProps.setActionCommand(JNavixActions.UI_UnimapBtnProperties);
		jbtnProps.setIcon(new ImageIcon(this.getClass().getResource("/imgs16x16/document-properties.png")));
		this.toolbar.add(this.jbtnProps);

		jbtnAutocenter.setText("Autocenter");
		jbtnAutocenter.setActionCommand(JNavixActions.UI_MainBtnAutocenter);
		this.toolbar.add(jbtnAutocenter);

	}

	// public void mouseDragged(MouseEvent me) {
	// me.translatePoint((int)
	// (this.parametersLink.mapCenterMeracatorPoint.getWidth() / 2.0), (int)
	// (this.parametersLink.mapCenterMeracatorPoint.getHeight() / 2.0));
	// if (this.dragMode == this.IMAGE_DRAG) {
	// this.offsetDrawing.setLocation(this.pntClick.x - me.getX(),
	// this.pntClick.y - me.getY());
	// }
	// }
	//
	// public void mousePressed(MouseEvent me) {
	// me.translatePoint((int)
	// (this.parametersLink.mapCenterMeracatorPoint.getWidth() / 2.0), (int)
	// (this.parametersLink.mapCenterMeracatorPoint.getHeight() / 2.0));
	// if (me.getModifiers() == 8) {
	// pntClick.setLocation(me.getX(), me.getY());
	// this.offsetDrawing.setLocation(0, 0);
	// this.dragMode = this.IMAGE_DRAG;
	// CLog.info("MIDDLE");
	// }
	// }
	//
	//
	// public void mouseClicked(MouseEvent me) {
	// super.mouseClicked(me);
	// if (me.getClickCount() == 2) {
	// CLog.info(String.valueOf(this.parametersLink.mapCenterMeracatorPoint.getWidth()));
	// CLog.info(String.valueOf(this.parametersLink.mapCenterMeracatorPoint.getHeight()));
	// me.translatePoint((int)
	// (this.parametersLink.mapCenterMeracatorPoint.getWidth() / 2.0), (int)
	// (this.parametersLink.mapCenterMeracatorPoint.getHeight() / 2.0));
	// CLog.info(String.valueOf(me.getX()));
	// CLog.info(String.valueOf(me.getY()));
	// LatLonPoint llp = new
	// LatLonPoint(this.parametersLink.mapCenterMeracatorPoint.inverse(me.getX(),
	// me.getY()));
	// this.parametersLink.center.setLatLon(llp);
	// this.parametersLink.mapCenterMeracatorPoint.setCenter(llp);
	// globManager.actionPerformed(new ActionEvent(this,
	// MouseEvent.MOUSE_CLICKED, JNavixActions.APP_UpdateMapPicture));
	// globManager.actionPerformed(new ActionEvent(this,
	// MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateMapPicture));
	// globManager.actionPerformed(new ActionEvent(this,
	// MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateInfoPanel));
	//
	// }
	//
	// }
	//
	// public void mouseWheelMoved(MouseWheelEvent mwe) {
	// if (mwe.getWheelRotation() >0){
	// if (globManager.LayerManager.getFocusedLayerScale() * 1.1 <=
	// (globManager.LayerManager.getFocusedLayerScaleMax())){
	// globManager.LayerManager.setFocusedLayerScale(globManager.LayerManager.getFocusedLayerScale()
	// * 1.1);
	// }
	// } else {
	// if (globManager.LayerManager.getFocusedLayerScale() / 1.1 >=
	// (globManager.LayerManager.getFocusedLayerScaleMin())){
	// globManager.LayerManager.setFocusedLayerScale(globManager.LayerManager.getFocusedLayerScale()
	// / 1.1);
	// }
	// }
	// globManager.actionPerformed(new ActionEvent(this,
	// MouseEvent.MOUSE_CLICKED, JNavixActions.APP_UpdateMapPicture));
	// globManager.actionPerformed(new ActionEvent(this,
	// MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateMapPicture));
	// globManager.actionPerformed(new ActionEvent(this,
	// MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateInfoPanel));
	//
	// }
	//
	// public void mouseReleased(MouseEvent me) {
	// if (this.dragMode == this.IMAGE_DRAG) {
	// this.dragMode = this.DO_NOTHING;
	// me.translatePoint((int)
	// (this.parametersLink.mapCenterMeracatorPoint.getWidth() / 2.0), (int)
	// (this.parametersLink.mapCenterMeracatorPoint.getHeight() / 2.0));
	// LatLonPoint llp = new
	// LatLonPoint(this.parametersLink.mapCenterMeracatorPoint.inverse(
	// (int) (this.parametersLink.mapCenterMeracatorPoint.getWidth() /
	// 2.0)+this.offsetDrawing.x,
	// (int) (this.parametersLink.mapCenterMeracatorPoint.getHeight() /
	// 2.0)+this.offsetDrawing.y ));
	// this.parametersLink.center.setLatLon(llp);
	// this.parametersLink.mapCenterMeracatorPoint.setCenter(llp);
	// this.offsetDrawing.setLocation(0, 0);
	// globManager.actionPerformed(new ActionEvent(this,
	// MouseEvent.MOUSE_CLICKED, JNavixActions.APP_UpdateMapPicture));
	// globManager.actionPerformed(new ActionEvent(this,
	// MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateMapPicture));
	// globManager.actionPerformed(new ActionEvent(this,
	// MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateInfoPanel));
	// }
	// }

	public void loadFile(File vFile) {
		if (vFile.getAbsoluteFile().getName().toUpperCase().endsWith(".MP")) {
			mpFile = new CMPFile();
			mpFile.load(vFile);
			this.minDD.setLatLon(mpFile.minDD);
			this.maxDD.setLatLon(mpFile.maxDD);

		}
		if (vFile.getAbsoluteFile().getName().toUpperCase().endsWith(".OSM")) {
			osmFile = new OSMFile();
			osmFile.load(vFile);
			this.minDD.setLatLon(osmFile.minDD);
			this.maxDD.setLatLon(osmFile.maxDD);

			osmFileCfg = new OSMPaintCfg();
		}

	}

	@Override
	public void paintLayer(Graphics2D g2d) {
		// TODO Auto-generated method stub
		super.paintLayer(g2d);
		if (this.mpFile != null) {
			this.paintMPFile(g2d);
		}
		if (this.osmFile != null) {
			this.paintOSMFile(g2d);
		}

	}

	private void paintMPFile(Graphics2D g2d) {
		if (this.biBackground == null) {
			this.biBackground = new BufferedImage(myScreenDim.width, myScreenDim.height, BufferedImage.TYPE_INT_ARGB);
		}

		if ((!this.fullRepaint) && (this.paintedParameters.equals(this.parametersLink))) {
			g2d.drawImage(this.biBackground, 0, 0, null);
			this.fullRepaint = false;
			CLog.info(this, "Unimap Light Update");
			return;
		}
		CLog.info(this, "Unimap. Full update started");

		Graphics2D localg2d = (Graphics2D) this.biBackground.getGraphics();
		Composite originalComposite = localg2d.getComposite();
		localg2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		Color bgColor = new Color(255, 255, 255, 1);
		localg2d.setBackground(bgColor);
		localg2d.clearRect(0, 0, myScreenDim.width, myScreenDim.height);

		this.paintedParameters.assign(this.parametersLink);

		Iterator<CMPRecord> itc = mpFile.alData.iterator();
		while (itc.hasNext()) {
			localg2d.setColor(Color.RED);
			localg2d.setStroke(new BasicStroke(1, 1, 1));
			CMPRecord cmpr = itc.next();
			Polygon p = new Polygon();

			if (cmpr.Data != null) {
				if (cmpr.RecType == 1)
					continue;

				localg2d.setColor(Color.magenta);
				localg2d.setBackground(Color.magenta);

				int[] xs = new int[cmpr.Data.length];
				int[] ys = new int[cmpr.Data.length];

				// forest
				if ((cmpr.RecType == 3) && (cmpr.Type.matches("0x0|0x50|0x17|0x14|0x8b"))) {
					localg2d.setColor(JNavixColors.Forest);
					localg2d.setBackground(JNavixColors.Forest);
					if (this.parametersLink.scale > 1500000)
						continue;
				}
				if ((cmpr.RecType == 3) && cmpr.Type.matches("0x(28|29|3[0-9|a-f]|4[0-9]|51|4c)")) {
					localg2d.setColor(JNavixColors.WaterArea);
					localg2d.setBackground(JNavixColors.WaterArea);
				}
				if ((cmpr.RecType == 2) && cmpr.Type.matches("0x([0123456789abcd]|42|1a)")) {
					localg2d.setColor(JNavixColors.Street);
					localg2d.setBackground(JNavixColors.Street);
					if (!(cmpr.Type.matches("0x([012])")) && (this.parametersLink.scale > 1500000))
						continue;
					if (cmpr.Type.matches("0x([01])")) {
						localg2d.setStroke(new BasicStroke(5, 1, 1));

					}

					if (cmpr.Type.matches("0x([02])")) {
						localg2d.setStroke(new BasicStroke(3, 1, 1));

					}

				}
				if ((cmpr.RecType == 3) && cmpr.Type.matches("0x6b")) {
					localg2d.setColor(JNavixColors.Street);
					localg2d.setBackground(JNavixColors.Street);
				}
				if ((cmpr.RecType == 2) && cmpr.Type.matches("0x14")) {
					localg2d.setColor(JNavixColors.Railway);
					localg2d.setBackground(JNavixColors.Railway);
					if (this.parametersLink.scale > 1500000)
						continue;

				}
				if ((cmpr.RecType == 2) && cmpr.Type.matches("0x(1f|26|18|44)")) {
					localg2d.setColor(JNavixColors.WaterLine);
					localg2d.setBackground(JNavixColors.WaterLine);
					if (this.parametersLink.scale > 1500000)
						continue;

				}
				if ((cmpr.RecType == 3) && cmpr.Type.matches("0x1|0x2")) {
					localg2d.setColor(JNavixColors.CivArea);
					localg2d.setBackground(JNavixColors.CivArea);
					if (this.parametersLink.scale > 1500000)
						continue;
				}
				if ((cmpr.RecType == 3) && cmpr.Type.matches("0x1a")) {
					localg2d.setColor(JNavixColors.Mortal);
					localg2d.setBackground(JNavixColors.Mortal);
					if (this.parametersLink.scale > 1500000)
						continue;
				}

				if ((cmpr.RecType == 3) && cmpr.Type.matches("0x3")) {
					localg2d.setColor(JNavixColors.GreenArea);
					localg2d.setBackground(JNavixColors.GreenArea);
					if (this.parametersLink.scale > 1500000)
						continue;
				}
				if ((cmpr.RecType == 3) && cmpr.Type.matches("0x4d")) {
					localg2d.setColor(JNavixColors.Glacier);
					localg2d.setBackground(JNavixColors.Glacier);
					if (this.parametersLink.scale > 1500000)
						continue;
				}
				if ((cmpr.RecType == 2) && cmpr.Type.matches("0x16")) {
					localg2d.setColor(JNavixColors.Pedestrian);
					localg2d.setBackground(JNavixColors.Pedestrian);
					if (this.parametersLink.scale > 1500000)
						continue;
				}

				if ((cmpr.RecType == 2) && cmpr.Type.matches("0x45")) {
					localg2d.setColor(JNavixColors.CivArea);
					localg2d.setBackground(JNavixColors.CivArea);
					if (this.parametersLink.scale > 1500000)
						continue;
				}
				if ((cmpr.RecType == 2) && cmpr.Type.matches("0x29")) {
					localg2d.setColor(JNavixColors.PowerLine);
					localg2d.setBackground(JNavixColors.PowerLine);
					if (this.parametersLink.scale > 1500000)
						continue;
				}
				if ((cmpr.RecType == 2) && cmpr.Type.matches("0x41")) {
					localg2d.setColor(JNavixColors.Railway);
					localg2d.setBackground(JNavixColors.Railway);
					if (this.parametersLink.scale > 1500000)
						continue;
				}

				if ((cmpr.RecType == 2) && cmpr.Type.matches("0x1(c|d|e)")) {
					localg2d.setColor(JNavixColors.Border);
					localg2d.setBackground(JNavixColors.Border);
					if (!(cmpr.Type.matches("0x1(c|e)")) && (this.parametersLink.scale > 3000000))
						continue;

				}

				// Island
				if ((cmpr.RecType == 3) && cmpr.Type.matches("0x80")) {
					localg2d.setColor(JNavixColors.Background);
					localg2d.setBackground(JNavixColors.Background);
				}
				if ((cmpr.RecType == 3) && cmpr.Type.matches("0xc")) {
					localg2d.setColor(JNavixColors.IndustrialArea);
					localg2d.setBackground(JNavixColors.IndustrialArea);
					if (this.parametersLink.scale > 1500000)
						continue;
				}

				if ((cmpr.RecType == 3) & (cmpr.Type.matches("0x13|0x18|0x19|0x6c|0xb"))) {
					localg2d.setColor(JNavixColors.Buildings);
					localg2d.setBackground(JNavixColors.Buildings);
					if (this.parametersLink.scale > 1500000)
						continue;
				}

				// if (localg2d.getColor() == Color.magenta) {
				// System.out.println(cmpr.RecType + "  " + cmpr.Type + " ");
				// }

				Point pntStart = new Point();
				for (int i = 0; i < cmpr.Data.length; i++) {

					pntStart.setLocation(this.parametersLink.mapCenterMeracatorPoint.forward(cmpr.Data[i]));
					xs[i] = pntStart.x;
					ys[i] = pntStart.y;
					p.addPoint(pntStart.x, pntStart.y);

				}
				if (this.parametersLink.mapCenterMeracatorPoint.isPlotable(cmpr.Data[0])) {

					if (cmpr.RecType == 2) {
						localg2d.drawPolyline(xs, ys, cmpr.Data.length);
					}
					if (cmpr.RecType == 3) {
						localg2d.fillPolygon(p);
					}
				}

			}

		}

		itc = null;
		itc = mpFile.alData.iterator();
		Font prevfont = localg2d.getFont();

		Font font = new Font(Font.SANS_SERIF, Font.BOLD, 14);
		Font font2 = new Font(Font.SANS_SERIF, Font.BOLD, 10);

		while (itc.hasNext()) {
			localg2d.setColor(Color.RED);
			CMPRecord cmpr = itc.next();

			if (cmpr.Data != null) {
				if (cmpr.RecType != 1)
					continue;
				if (cmpr.Label == null) {
					continue;
				}

				if ((cmpr.Level == 0) && (this.parametersLink.scale > 1500000)) {
					continue;
				}
				if ((cmpr.Level > 0) && (this.parametersLink.scale < 1500000)) {
					continue;
				}

				localg2d.setColor(Color.magenta);
				localg2d.setBackground(Color.magenta);

				// int[] xs = new int[cmpr.Data.length];
				// int[] ys = new int[cmpr.Data.length];

				// Rectangle2D bounds = font.getStringBounds(cmpr.Label,
				// localg2d.getFontRenderContext());

				Point pntStart = new Point();
				pntStart.setLocation(this.parametersLink.mapCenterMeracatorPoint.forward(cmpr.Data[0]));

				if (cmpr.Type.matches("0x500")) {
					if ((cmpr.Level == 0)) {
						localg2d.setFont(font);
					} else {
						localg2d.setFont(font2);
					}

					localg2d.setColor(JNavixColors.CityLabel);
					localg2d.drawString(cmpr.Label, pntStart.x, pntStart.y);

				}

			}
		}

		localg2d.setFont(prevfont);

		localg2d.setComposite(originalComposite);
		g2d.drawImage(this.biBackground, 0, 0, null);

	}

	private void paintOSMFile(Graphics2D g2d) {
		if (this.biBackground == null) {
			this.biBackground = new BufferedImage(myScreenDim.width, myScreenDim.height, BufferedImage.TYPE_INT_ARGB);
		}

		if ((!this.fullRepaint) && (this.paintedParameters.equals(this.parametersLink))) {
			g2d.drawImage(this.biBackground, 0, 0, null);
			this.fullRepaint = false;
			CLog.info(this, "Unimap Light Update");
			return;
		}
		CLog.info(this, "Unimap. OSM Full update started");

		Graphics2D localg2d = (Graphics2D) this.biBackground.getGraphics();
		Composite originalComposite = localg2d.getComposite();
		localg2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		Color bgColor = new Color(255, 255, 255, 1);
		localg2d.setBackground(bgColor);
		localg2d.clearRect(0, 0, myScreenDim.width, myScreenDim.height);

		this.paintedParameters.assign(this.parametersLink);
		CPaintingRec paintCfg = null;

		Collection<OSMWay> c = osmFile.wayCollection.values();
		Iterator<OSMWay> itrWay = c.iterator();
		int drawType = 0; // 0 - nothing 1-road 2 doted 2-polygon
		float lineWidth1 = 1f;
		float lineWidth2 = 1f;
		Color lineColor1 = null;
		Color lineColor2 = null;
		while (itrWay.hasNext()) {
			OSMWay way = itrWay.next();
			localg2d.setColor(Color.RED);
			localg2d.setStroke(new BasicStroke(1f));
			drawType = 0;

			Polygon p = new Polygon();
			int[] xs = new int[way.nodes.size()];
			int[] ys = new int[way.nodes.size()];
			Point pntStart = new Point();

			for (int i = 0; i < way.nodes.size(); i++) {
				if (way.nodes.get(i) != null) {
					pntStart.setLocation(this.parametersLink.mapCenterMeracatorPoint.forward(way.nodes.get(i).latLon));
					xs[i] = pntStart.x;
					ys[i] = pntStart.y;
					p.addPoint(pntStart.x, pntStart.y);
				}

			}

			paintCfg = null;
			// paintCfg = this.osmFileCfg.parameters.get("landuse-hz");

			boolean showlost = false;
			String valname = "";
			if (way.recType != -1) {
				valname = OSMFile.recType2RecName.get(way.recType);
				String value = way.tags.get(valname);
				if ((value == null)) {
					if (way.relationTags != null) {
						value = way.relationTags.get(valname);
					} else {
						System.out.println(" ХЗ " + way.id);
					}
				}
				if (value != null) {
					paintCfg = this.osmFileCfg.parameters.get(valname.concat("-").concat(value.toLowerCase()));
					if ((paintCfg == null) && (showlost)) {
						System.out.println(valname.concat("-").concat(value.toLowerCase()));
					}
				}

			}else{
				paintCfg = this.osmFileCfg.parameters.get("landuse-hz");
			}

			// }
			// }
			/*
			 * /
			 * 
			 * /* if (way.tags.get("railway") != null) { String value =
			 * way.tags.get("railway"); paintCfg =
			 * this.osmFileCfg.parameters.get
			 * ("railway-".concat(value.toLowerCase())); if (paintCfg == null){
			 * System.out.println("railway-".concat(value.toLowerCase())); } }
			 * 
			 * if (way.tags.get("barrier") != null) { String value =
			 * way.tags.get("barrier"); paintCfg =
			 * this.osmFileCfg.parameters.get
			 * ("barrier-".concat(value.toLowerCase())); if (paintCfg == null){
			 * System.out.println("barrier-".concat(value.toLowerCase())); } }
			 * if (way.tags.get("waterway") != null) { String value =
			 * way.tags.get("waterway"); paintCfg =
			 * this.osmFileCfg.parameters.get
			 * ("waterway-".concat(value.toLowerCase())); if (paintCfg == null){
			 * System.out.println("waterway-".concat(value.toLowerCase())); }
			 * 
			 * }
			 * 
			 * if (way.tags.get("natural") != null) { String value =
			 * way.tags.get("natural"); paintCfg =
			 * this.osmFileCfg.parameters.get
			 * ("natural-".concat(value.toLowerCase())); if (paintCfg == null){
			 * System.out.println("natural-".concat(value.toLowerCase()));
			 * paintCfg = this.osmFileCfg.parameters.get("landuse-hz"); } }
			 * 
			 * if (way.tags.get("landuse") != null) { String value =
			 * way.tags.get("landuse"); paintCfg =
			 * this.osmFileCfg.parameters.get
			 * ("landuse-".concat(value.toLowerCase())); if (paintCfg == null){
			 * System.out.println("landuse-".concat(value.toLowerCase()));
			 * paintCfg = this.osmFileCfg.parameters.get("landuse-hz"); }
			 * 
			 * } if (way.tags.get("building") != null) { String value =
			 * way.tags.get("building"); paintCfg =
			 * this.osmFileCfg.parameters.get
			 * ("building-".concat(value.toLowerCase())); if (paintCfg == null){
			 * System.out.println("building-".concat(value.toLowerCase())); }
			 * 
			 * }
			 */

			if (paintCfg != null) {
				if (paintCfg.minScale != -1d) {
					if ((this.globManager.LayerManager.focusedLayer.parametersLink.scale > paintCfg.maxScale) ||
							(this.globManager.LayerManager.focusedLayer.parametersLink.scale < paintCfg.minScale))
						continue;

				}

				if (paintCfg.objType == OSMPaintCfg.OBJTYPE_LINE) {
					if (paintCfg.lineUoM == OSMPaintCfg.LINE_UOM_METERS) {
						lineWidth1 = (float) (paintCfg.lineWidth * 1000.0 / (this.globManager.LayerManager.focusedLayer.parametersLink.scale));
						lineWidth2 = lineWidth1 + 2;
					}
					if (paintCfg.lineUoM == OSMPaintCfg.LINE_UOM_PIXELS) {
						lineWidth1 = (float) (paintCfg.lineWidth);
						lineWidth2 = lineWidth1 + 2;
					}
					if (paintCfg.innerType == OSMPaintCfg.INNERTYPE_SOLID) {
						localg2d.setColor(paintCfg.innerColor);
						localg2d.setStroke(new BasicStroke(lineWidth1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
						localg2d.drawPolyline(xs, ys, way.nodes.size());
					}
					if (paintCfg.innerType == OSMPaintCfg.INNERTYPE_SOLID_BORDERED) {
						localg2d.setColor(paintCfg.outerColor);
						localg2d.setStroke(new BasicStroke(lineWidth2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
						localg2d.drawPolyline(xs, ys, way.nodes.size());
						localg2d.setColor(paintCfg.innerColor);
						localg2d.setStroke(new BasicStroke(lineWidth1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
						localg2d.drawPolyline(xs, ys, way.nodes.size());
					}
					if (paintCfg.innerType == OSMPaintCfg.INNERTYPE_DASHED) {
						float dash1[] = { 3f };
						localg2d.setColor(paintCfg.innerColor);
						localg2d.setStroke(new BasicStroke(lineWidth1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 3f, dash1, 0f));
						localg2d.drawPolyline(xs, ys, way.nodes.size());

					}
					if (paintCfg.innerType == OSMPaintCfg.INNERTYPE_DASHED_BORDERED) {
						localg2d.setColor(paintCfg.innerColor);
						localg2d.setStroke(new BasicStroke(lineWidth2 + 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
						localg2d.drawPolyline(xs, ys, way.nodes.size());
						float dash1[] = { 3f };
						localg2d.setColor(paintCfg.outerColor);
						localg2d.setStroke(new BasicStroke(lineWidth1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 3f, dash1, 0f));
						localg2d.drawPolyline(xs, ys, way.nodes.size());

					}

				}
				if (paintCfg.objType == OSMPaintCfg.OBJTYPE_AREA) {
					if (paintCfg.lineUoM == OSMPaintCfg.LINE_UOM_METERS) {
						lineWidth1 = (float) (paintCfg.lineWidth * 1000.0 / (this.globManager.LayerManager.focusedLayer.parametersLink.scale));
						lineWidth2 = lineWidth1 + 2;
					}
					if (paintCfg.lineUoM == OSMPaintCfg.LINE_UOM_PIXELS) {
						lineWidth1 = (float) (paintCfg.lineWidth);
						lineWidth2 = lineWidth1 + 2;
					}

					localg2d.setColor(paintCfg.innerColor);
					localg2d.setStroke(new BasicStroke(1.0f));
					localg2d.fillPolygon(xs, ys, way.nodes.size());

					if (paintCfg.innerType == OSMPaintCfg.INNERTYPE_SOLID) {
						localg2d.setColor(paintCfg.outerColor);
						localg2d.setStroke(new BasicStroke(lineWidth1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
						localg2d.drawPolyline(xs, ys, way.nodes.size());
					}

					if (paintCfg.innerType == OSMPaintCfg.INNERTYPE_DASHED) {
						float dash1[] = { 3f };
						localg2d.setColor(paintCfg.outerColor);
						localg2d.setStroke(new BasicStroke(lineWidth1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 3f, dash1, 0f));
						localg2d.drawPolyline(xs, ys, way.nodes.size());
					}

				}

			}

			// if (drawType == 1) {
			// localg2d.setColor(lineColor2);
			// localg2d.setStroke(new BasicStroke(lineWidth2,
			// BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
			// localg2d.drawPolyline(xs, ys, way.nodes.size());
			// localg2d.setColor(lineColor1);
			// localg2d.setStroke(new BasicStroke(lineWidth1,
			// BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
			// localg2d.drawPolyline(xs, ys, way.nodes.size());
			// }
			// if (drawType == 2) {
			// float dash1[] = {3f};
			// localg2d.setColor(lineColor1);
			// localg2d.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND,
			// BasicStroke.JOIN_BEVEL,3f,dash1,0f));
			// localg2d.drawPolyline(xs, ys, way.nodes.size());
			// }
			// if (drawType == 1001) {
			// localg2d.setColor(lineColor1);
			// localg2d.fillPolygon(xs, ys, way.nodes.size());
			// }

		}

		localg2d.setComposite(originalComposite);
		g2d.drawImage(this.biBackground, 0, 0, null);

	}

	@Override
	public void mouseClicked(MouseEvent me) {
		// TODO Auto-generated method stub
		if (me.getClickCount() == 1) {
			me.translatePoint((int) (this.parametersLink.mapCenterMeracatorPoint.getWidth() / 2.0), (int) (this.parametersLink.mapCenterMeracatorPoint.getHeight() / 2.0));
			LatLonPoint llp = new LatLonPoint(this.parametersLink.mapCenterMeracatorPoint.inverse(me.getX(), me.getY()));
			CLog.info(llp.toString());
			this.getInfoByDD(llp);

		}
		super.mouseClicked(me);
	}

	public void getInfoByDD(LatLonPoint llp) {
		if (this.osmFile != null) {
			this.osmFile.getObjectsByDD(llp);
		}

	}

}
