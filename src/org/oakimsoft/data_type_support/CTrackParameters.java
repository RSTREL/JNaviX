/**
 * Свойства GPS track
 */
package org.oakimsoft.data_type_support;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

public final class CTrackParameters {
	public class CSpeedRange{
		public int minValue = 0; 
		public int maxValue = 0;
		public Color color = null;
		public boolean visible = true;
	}
	
	ArrayList<CSpeedRange> speedRanges = null;
		

	public CTrackParameters() {
		speedRanges = new ArrayList<CSpeedRange>(0);
		this.clear();
	}
 
	public  void add(int minKMH, int maxKMH, Color color, boolean visible){
		CSpeedRange csr = new CSpeedRange();
		csr.minValue = minKMH;
		csr.maxValue = maxKMH;
		csr.color = new Color(color.getRGB());
		csr.visible = visible;
		speedRanges.add(csr);
		
	}
	
	public  Color  getColor(Double speed){
		Iterator<CSpeedRange> it = speedRanges.iterator();
		while (it.hasNext()){
			CSpeedRange csr = it.next();
			if ((speed>=csr.minValue) && (speed<csr.maxValue)){
				return csr.color;
			}
			
		}
		return Color.gray;
		
		
		
	}
	
	
	public void clear() {
		speedRanges.clear();
	}
	

	

	
	

}
