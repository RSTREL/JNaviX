/**
 * 
 */
package org.oakimsoft;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.swing.JToolBar;

import org.oakimsoft.common.CLog;
import org.oakimsoft.common.JNavixActions;
import org.oakimsoft.data_type_support.CLayerParameters;

import com.bbn.openmap.LatLonPoint;

/**
 * @author Ruslan.Strelnikov
 * 
 */
public abstract class CJNaviXLayer implements MouseListener, KeyListener, ActionListener, MouseMotionListener, MouseWheelListener {
	/**
	 * Видимость слоя
	 */
	public boolean visible = false;
	/**
	 * Тип слоя: 0 - неопределенный 1 - UniMap 2-DrawBoard
	 */
	public int layerType = 0;
	/**
	 * Настройки слоя
	 */
	public CLayerParameters layerParameters = new CLayerParameters();
	/**
	 * Пользоваться собственными настройками
	 * */
	public boolean useOwnParameters = false;
	/**
	 * Ссылка на параметры, которыми в данный момент пользуется слой
	 */
	public CLayerParameters parametersLink;
	/**
	 * Комментарий
	 */
	public String comment;
	/**
	 * Минимальная координата, загруженных данных
	 */
	public LatLonPoint minDD = new LatLonPoint(0d, 0d);
	/**
	 * Максимальная координата, загруженных данных
	 */
	public LatLonPoint maxDD = new LatLonPoint(0d, 0d);

	/**
	 * Тулбар для уровня. Заполняется самим леером, выносится на экран при
	 * переключении слоев.
	 */
	public JToolBar toolbar = new JToolBar();

	/**
	 * Кеширование изображения
	 */
	protected BufferedImage biBackground = null;
	/**
	 * Необходимость полной перерисовки уровня. Устанавливаетс в True в случае
	 * изменений в изображении
	 */
	public boolean fullRepaint = false;
	/**
	 * Предыдущие настройки слоя, с которыми была произведенеа отрисовка
	 */
	protected CLayerParameters paintedParameters = new CLayerParameters();

	public Point pntClick = new Point();
	public int dragMode = 0;
	public final int DO_NOTHING = 0;
	public final int IMAGE_DRAG = 1;
	public boolean pntClicked = false;

	public Dimension myScreenDim = new Dimension();
	public Point offsetDrawing = new Point(0, 0);
	public CJNavixGlobalManager globManager = null;

	public CJNaviXLayer() {

	}

	public void mouseDragged(MouseEvent me) {
		// Middle button
		if (me.getModifiers() == 8) {
			me.translatePoint((int) (this.parametersLink.mapCenterMeracatorPoint.getWidth() / 2.0), (int) (this.parametersLink.mapCenterMeracatorPoint.getHeight() / 2.0));
			if (this.dragMode == this.IMAGE_DRAG) {
				this.offsetDrawing.setLocation(this.pntClick.x - me.getX(), this.pntClick.y - me.getY());
			}
		}
	}

	public void mousePressed(MouseEvent me) {
		// Middle Button
		if (me.getModifiers() == 8) {
			me.translatePoint((int) (this.parametersLink.mapCenterMeracatorPoint.getWidth() / 2.0), (int) (this.parametersLink.mapCenterMeracatorPoint.getHeight() / 2.0));
			pntClick.setLocation(me.getX(), me.getY());
			this.offsetDrawing.setLocation(0, 0);
			this.dragMode = this.IMAGE_DRAG;
			CLog.info("MIDDLE");
		}
	}

	public void mouseClicked(MouseEvent me) {
		
		
		if (me.getClickCount() == 2) {
			CLog.info(String.valueOf(this.parametersLink.mapCenterMeracatorPoint.getWidth()));
			CLog.info(String.valueOf(this.parametersLink.mapCenterMeracatorPoint.getHeight()));
			me.translatePoint((int) (this.parametersLink.mapCenterMeracatorPoint.getWidth() / 2.0), (int) (this.parametersLink.mapCenterMeracatorPoint.getHeight() / 2.0));
			CLog.info(String.valueOf(me.getX()));
			CLog.info(String.valueOf(me.getY()));
			LatLonPoint llp = new LatLonPoint(this.parametersLink.mapCenterMeracatorPoint.inverse(me.getX(), me.getY()));
			this.parametersLink.center.setLatLon(llp);
			this.parametersLink.mapCenterMeracatorPoint.setCenter(llp);
			globManager.actionPerformed(new ActionEvent(this, MouseEvent.MOUSE_CLICKED, JNavixActions.APP_UpdateMapPicture));
			globManager.actionPerformed(new ActionEvent(this, MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateMapPicture));
			globManager.actionPerformed(new ActionEvent(this, MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateInfoPanel));

		}

	}

	public void mouseWheelMoved(MouseWheelEvent mwe) {
		if (mwe.getWheelRotation() > 0) {
			if (globManager.LayerManager.getFocusedLayerScale() * 1.1 <= (globManager.LayerManager.getFocusedLayerScaleMax())) {
				globManager.LayerManager.setFocusedLayerScale(globManager.LayerManager.getFocusedLayerScale() * 1.1);
			}
		} else {
			if (globManager.LayerManager.getFocusedLayerScale() / 1.1 >= (globManager.LayerManager.getFocusedLayerScaleMin())) {
				globManager.LayerManager.setFocusedLayerScale(globManager.LayerManager.getFocusedLayerScale() / 1.1);
			}
		}
		globManager.actionPerformed(new ActionEvent(this, MouseEvent.MOUSE_CLICKED, JNavixActions.APP_UpdateMapPicture));
		globManager.actionPerformed(new ActionEvent(this, MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateMapPicture));
		globManager.actionPerformed(new ActionEvent(this, MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateInfoPanel));

	}

	public void mouseReleased(MouseEvent me) {
		if (me.getModifiers() == 8) {
			if (this.dragMode == this.IMAGE_DRAG) {
				this.dragMode = this.DO_NOTHING;
				me.translatePoint((int) (this.parametersLink.mapCenterMeracatorPoint.getWidth() / 2.0), (int) (this.parametersLink.mapCenterMeracatorPoint.getHeight() / 2.0));
				LatLonPoint llp = new LatLonPoint(this.parametersLink.mapCenterMeracatorPoint.inverse((int) (this.parametersLink.mapCenterMeracatorPoint.getWidth() / 2.0) + this.offsetDrawing.x,
						(int) (this.parametersLink.mapCenterMeracatorPoint.getHeight() / 2.0) + this.offsetDrawing.y));
				this.parametersLink.center.setLatLon(llp);
				this.parametersLink.mapCenterMeracatorPoint.setCenter(llp);
				this.offsetDrawing.setLocation(0, 0);
				globManager.actionPerformed(new ActionEvent(this, MouseEvent.MOUSE_CLICKED, JNavixActions.APP_UpdateMapPicture));
				globManager.actionPerformed(new ActionEvent(this, MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateMapPicture));
				globManager.actionPerformed(new ActionEvent(this, MouseEvent.MOUSE_CLICKED, JNavixActions.UI_UpdateInfoPanel));
			}
		}
	}

	// public void mouseClicked(MouseEvent me) {
	// }

	public void mouseEntered(MouseEvent me) {
	}

	public void mouseExited(MouseEvent me) {
	}

	// public void mousePressed(MouseEvent me) {
	//
	// }

	// public void mouseReleased(MouseEvent me) {
	//
	// }

	// public void mouseDragged(MouseEvent me) {
	//
	// }

	public void mouseMoved(MouseEvent me) {

	}

	@Override
	public void actionPerformed(ActionEvent e) {

	}

	// public void mouseWheelMoved(MouseWheelEvent mwe) {
	// }
	// ;

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
	}

	/**
	 * Отрисовка на указанной канве
	 * 
	 * @param g2d
	 */
	public void paintLayer(Graphics2D g2d) {

	}

	/**
	 * Обновить статистику
	 */
	public void updateStatistics() {

	}

	/**
	 * Отрисовка на указанной канве
	 * 
	 * @param g2d
	 */
	public String getStatistics() {
		StringBuffer sb = new StringBuffer();

		sb.append("<html>No <i>stat</i> information");

		return sb.toString();

	}

}
