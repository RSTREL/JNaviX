/**
 * Свойства слоя
 */
package org.oakimsoft.data_type_support;

import java.awt.Dimension;

import com.bbn.openmap.LatLonPoint;
import com.bbn.openmap.proj.Mercator;

public final class CLayerParameters {
	
	/**
	 * Координаты центра изображения
	 */
	public LatLonPoint center = new LatLonPoint(0d,0d);

	/**
	 * Масштаб (метр/пиксель)
	 */
	public Double scale = 100000d;
	
	/**
	 * Азимут (градусы)
	 */
	public Double azimuth = 0d;
	

	/**
	 * Для преобразования координат
	 */
	public Mercator mapCenterMeracatorPoint;	
	

	// установка параметров для преобразования координат
	public void setMercatorParameters(Dimension screenDim){
        if (this.mapCenterMeracatorPoint != null) {
            this.mapCenterMeracatorPoint = null;
        }
        LatLonPoint llp = new LatLonPoint();
        this.mapCenterMeracatorPoint = null;
        this.mapCenterMeracatorPoint = new Mercator(this.center, this.scale.floatValue(), screenDim.width,screenDim.height);
        this.mapCenterMeracatorPoint.setScale(this.scale.floatValue());
	}
	
	private LatLonPoint getLatLonFromScreen(int x, int y){
		return this.mapCenterMeracatorPoint.inverse(x,y);
		
	}	
	

	public CLayerParameters() {
		this.clear();
	}

	public void clear() {
		this.azimuth = 0d;
		this.scale = 1000000d;
		this.center.setLatLon(0d, 0d);
		this.mapCenterMeracatorPoint = new Mercator(this.center, this.scale.floatValue(), 1000,1000);		
	
	}
	
	public void assign(CLayerParameters layerParameters){
		this.azimuth = layerParameters.azimuth;
		this.scale = layerParameters.scale;
		this.center.setLatLon(layerParameters.center);
        this.mapCenterMeracatorPoint = null;
        this.mapCenterMeracatorPoint = new Mercator(this.center, this.scale.floatValue(),  layerParameters.mapCenterMeracatorPoint.getWidth(),layerParameters.mapCenterMeracatorPoint.getHeight());
	}
	

	public boolean equals(CLayerParameters layerParameters){
		if (this.azimuth != layerParameters.azimuth)
			return false;
		if (this.scale != layerParameters.scale)
			return false;
		if (this.center.getLatitude() != layerParameters.center.getLatitude())
			return false;
		if (this.center.getLongitude() != layerParameters.center.getLongitude())
			return false;
		return true;
	}
	
	

}
