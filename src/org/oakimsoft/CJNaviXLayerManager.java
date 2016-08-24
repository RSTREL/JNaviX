/**
 * 
 */
package org.oakimsoft;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.io.File;
import java.util.ArrayList;

import org.oakimsoft.data_type_support.CLayerParameters;

import com.bbn.openmap.LatLonPoint;

public class CJNaviXLayerManager {
	public CLayerParameters layerParameters = new CLayerParameters();
	public ArrayList<CJNaviXLayer> arrLayers = null;
	public CJNaviXLayer focusedLayer = null;
	public CJNavixGlobalManager globManager=null;

	public CJNaviXLayerManager() {
		arrLayers = new ArrayList<CJNaviXLayer>(200);
	}

	public int addNewLayer(int eLayerType, Dimension screenDimension) {

		switch (eLayerType) {
		case 1:
			CJNavixLayerUnimap h1 = new CJNavixLayerUnimap();
			if (h1 != null) {
				this.arrLayers.add(0, h1);
				this.focusedLayer = h1;
				h1.parametersLink = this.layerParameters;
				h1.parametersLink.setMercatorParameters(screenDimension);
				h1.myScreenDim.setSize(screenDimension);
				h1.globManager = this.globManager;
				// return 0;
			}
			;
			break;
		case 2:
			CJNavixLayerDrawBoard h2 = new CJNavixLayerDrawBoard();
			if (h2 != null) {
				this.arrLayers.add(0, h2);
				this.focusedLayer = h2;
				h2.parametersLink = this.layerParameters;
				h2.parametersLink.setMercatorParameters(screenDimension);
				h2.myScreenDim.setSize(screenDimension);
				h2.globManager = this.globManager;
				// return 0;
			}
			;
			break;
		case 3:
			CJNavixLayerGPSTrack h3 = new CJNavixLayerGPSTrack();
			if (h3 != null) {
				this.arrLayers.add(0, h3);
				this.focusedLayer = h3;
				h3.parametersLink = this.layerParameters;
				h3.parametersLink.setMercatorParameters(screenDimension);
				h3.myScreenDim.setSize(screenDimension);
				h3.globManager = this.globManager;
				// return 0;
			}
			;
			break;
		case 4:
			CJNavixLayerPOI h4 = new CJNavixLayerPOI();
			if (h4 != null) {
				this.arrLayers.add(0, h4);
				this.focusedLayer = h4;
				// return 0;
			}
			;
			break;
		}

		return 0;

	}

	/**
	 * Список слоев в порядке убывания
	 * 
	 * @param eLayerList
	 */
	public void getListOfLayes(ArrayList<String> eLayerList) {
		eLayerList.clear();
		for (int i = 0; i < this.arrLayers.size(); i++) {
			CJNaviXLayer layer = arrLayers.get(i);
			StringBuilder sb = new StringBuilder();
			sb.append(layer.layerType).append("|").append(layer.visible)
					.append("|");
			sb.append(layer.useOwnParameters).append("|").append(layer.comment);
			if (layer == this.focusedLayer) {
				sb.append("|").append("+");
			} else {
				sb.append("|").append(" ");
			}
			eLayerList.add(sb.toString());
		}
	}

	/**
	 * Установить фокус на слое с указанным номером
	 * 
	 * @param layerNumber
	 *            номер слоя
	 * @return 0 - прошло успешно
	 */
	public int setFocusedLayer(Integer layerNumber) {
		if ((layerNumber < 0) || (layerNumber > arrLayers.size() - 1)) {
			return 1;
		}
		this.focusedLayer = this.arrLayers.get(layerNumber);
		return 0;
	}

	public boolean isFocusedVisible() {
		if (this.focusedLayer != null)
			return this.focusedLayer.visible;
		else
			return false;

	}

	
	public void setFocusedVisible(boolean Visible) {
		if (this.focusedLayer != null)
			this.focusedLayer.visible = Visible;
	}

	public boolean isFocusedUseOwnParams() {
		if (this.focusedLayer != null)
			return this.focusedLayer.useOwnParameters;
		else
			return false;

	}

	public void setFocusedOwnParams(boolean Params) {
		if (this.focusedLayer != null)
			this.focusedLayer.useOwnParameters = Params;
		if (Params) {
			this.focusedLayer.parametersLink = this.focusedLayer.layerParameters;
		    this.focusedLayer.layerParameters.setMercatorParameters( new Dimension(this.layerParameters.mapCenterMeracatorPoint.getWidth() , this.layerParameters.mapCenterMeracatorPoint.getHeight()));
		}else{
			this.focusedLayer.parametersLink = this.layerParameters;
		}

	}
	
	
	/**
	 * Установить цвет. Применимо для Sketch.
	 * @param vColor
	 */
	public void setFocusedColor(Color vColor){
		if ((this.focusedLayer != null)&&(this.focusedLayer.layerType==2)){
			((CJNavixLayerDrawBoard)(this.focusedLayer)).setCurrentColor(vColor);
			;
		}
	}
	
	public void setFocusedClear(){
		if ((this.focusedLayer != null)&&(this.focusedLayer.layerType==2)){
			((CJNavixLayerDrawBoard)(this.focusedLayer)).clear();
			;
		}
	}

	
	
	public Color getFocusedColor(){
		if ((this.focusedLayer != null)&&(this.focusedLayer.layerType==2)){
			return 	((CJNavixLayerDrawBoard)(this.focusedLayer)).getCurrentColor();
		}else{
			return null;
		}
		
	}
	

	public void moveFocusedUp() {
		if (this.focusedLayer != null) {
			int indx = this.arrLayers.indexOf(this.focusedLayer);
			if (indx > 0) {
				CJNaviXLayer tmpLayer;
				tmpLayer = this.arrLayers.get(indx - 1);
				this.arrLayers.set(indx - 1, this.arrLayers.get(indx));
				this.arrLayers.set(indx, tmpLayer);
			}
		}
	}

	public void moveFocusedDown() {
		if (this.focusedLayer != null) {
			int indx = this.arrLayers.indexOf(this.focusedLayer);
			if (indx < this.arrLayers.size() - 1) {
				CJNaviXLayer tmpLayer;
				tmpLayer = this.arrLayers.get(indx + 1);
				this.arrLayers.set(indx + 1, this.arrLayers.get(indx));
				this.arrLayers.set(indx, tmpLayer);
			}
		}
	}

	/**
	 * Удалить фокусный слой
	 */
	public void removeFocused() {
		if (this.focusedLayer != null) {
			this.arrLayers.remove(this.focusedLayer);
			if (this.arrLayers.size() > 0)
				this.focusedLayer = this.arrLayers.get(0);
			else
				this.focusedLayer = null;
		}

	}
	
	
	public void setFocusedLayerScale(Double value){
		if (this.focusedLayer != null) {
			this.focusedLayer.parametersLink.scale = value;
			this.focusedLayer.parametersLink.mapCenterMeracatorPoint.setScale(this.focusedLayer.parametersLink.scale.floatValue());
		}else{
			this.layerParameters.scale = value;
			// this.layerParameters.mapCenterMeracatorPoint.setScale(this.focusedLayer.parametersLink.scale.floatValue());			
		}
	}

	public Double getFocusedLayerScaleMin(){
		if (this.focusedLayer != null) {
			return (double)(this.focusedLayer.parametersLink.mapCenterMeracatorPoint.getMinScale());
		}else{
			return (double)(this.layerParameters.mapCenterMeracatorPoint.getMinScale());
		}
	}	

	public Double getFocusedLayerScaleMax(){
		if (this.focusedLayer != null) {
			return (double)(this.focusedLayer.parametersLink.mapCenterMeracatorPoint.getMaxScale());
		}else{
			return (double)(this.layerParameters.mapCenterMeracatorPoint.getMaxScale());
		}
	}		
	
	public Double getFocusedLayerScale(){
		if (this.focusedLayer != null) {
			return this.focusedLayer.parametersLink.scale;
		}else{
			return this.layerParameters.scale;
		}
	}


	public void setFocusedLayerAzimuth(Double value){
		if (this.focusedLayer != null) {
			this.focusedLayer.parametersLink.azimuth = value;
		}else{
			this.layerParameters.azimuth = value;
		}
	}

	public Double getFocusedLayerAzimuth(){
		if (this.focusedLayer != null) {
			return this.focusedLayer.parametersLink.azimuth;
		}else{
			return this.layerParameters.azimuth;
		}
	}

	public void setFocusedLayerCenter(LatLonPoint value){
		if (this.focusedLayer != null) {
			this.focusedLayer.parametersLink.center.setLatLon(value);
			this.focusedLayer.parametersLink.mapCenterMeracatorPoint.setCenter(this.focusedLayer.parametersLink.center);
		}else{
			this.layerParameters.center.setLatLon(value);
			this.layerParameters.mapCenterMeracatorPoint.setCenter(this.focusedLayer.parametersLink.center);
			
		}
	}
	
	public void setFocusedLayerAutocenter(){
		if (this.focusedLayer != null) {
			
			this.focusedLayer.parametersLink.center.setLatLon(new LatLonPoint(
					(this.focusedLayer.minDD.getLatitude()+this.focusedLayer.maxDD.getLatitude())/2.0,
					(this.focusedLayer.minDD.getLongitude()+this.focusedLayer.maxDD.getLongitude())/2.0
					));
			this.focusedLayer.parametersLink.mapCenterMeracatorPoint.setCenter(this.focusedLayer.parametersLink.center);
		}else{
			return;
			
		}
	}	
	

	public LatLonPoint getFocusedLayerCenter(){
		if (this.focusedLayer != null) {
			return this.focusedLayer.parametersLink.center;
		}else{
			return this.layerParameters.center;
		}
	}
	

	public void loadGPSTrackFile(File vFile){
		if ((this.focusedLayer != null)&&(this.focusedLayer.layerType==3)){
			((CJNavixLayerGPSTrack)(this.focusedLayer)).loadGPSTrackFile(vFile);
			((CJNavixLayerGPSTrack)(this.focusedLayer)).updateStatistics();
		}		
	}
	
	public void loadMapFile(File vFile){
		if ((this.focusedLayer != null)&&(this.focusedLayer.layerType==1)){
			((CJNavixLayerUnimap)(this.focusedLayer)).loadFile(vFile);
			((CJNavixLayerUnimap)(this.focusedLayer)).updateStatistics();
		}		
		
	}

	
	
	
	

	/**
	 * Удаление всех слоев
	 */
	public void clear() {
		this.arrLayers.clear();
	}
	
	
	public void updatePicture(Graphics2D g2d){
		for (int i = this.arrLayers.size()-1; i >= 0; i--) {
			CJNaviXLayer layer = arrLayers.get(i);
			if (layer.visible)
				layer.paintLayer(g2d);
		}
		
		
	}
	

}
