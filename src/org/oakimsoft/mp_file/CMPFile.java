package org.oakimsoft.mp_file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

import org.oakimsoft.data_type_support.CAbstractMap;
import org.oakimsoft.data_type_support.MyPoint3d;

import com.bbn.openmap.LatLonPoint;

public class CMPFile extends CAbstractMap{
	
	
	public class CMPRecord{
		/**
		 *  1 POI,  2 POLYLINE, 3 POLYGON
		 */
		public int RecType = 0; 
		public String Type;
		public long RoadID= 0;
		public int Level = 0;
		public String RouteParam;
		public LatLonPoint[] Data;
		public String Label;
		public String City;
		public long CityIdx = 0;
		public CMPRecord(){
		}
	}
	

	/**
	 * Коллекция с данными
	 */
	public ArrayList<CMPRecord> alData = new ArrayList<CMPRecord>(10);
	
	
	
	
	
	public CMPFile(){

	}
	
    private LatLonPoint[] convertDD2Points(String DDs) {
        String[] coors = DDs.split(",");
        MyPoint3d[] mfps = new MyPoint3d[coors.length / 2];
        Double prevX = 0d;
        Double prevY = 0d;
        int cnt = 0;
        for (int i = 0; i < coors.length / 2; i++) {
            cnt++;
            mfps[i] = new MyPoint3d();
            mfps[i].x = (Double.parseDouble(coors[i * 2].substring(1)));
            mfps[i].y = (Double.parseDouble(coors[i * 2 + 1].substring(0, coors[i * 2 + 1].length() - 1)));
            if ((mfps[i].y == prevY) && (mfps[i].x == prevX)) {
                mfps[i] = null;
                cnt--;
                continue;
            }
            prevX = mfps[i].x;
            prevY = mfps[i].y;
        }
        LatLonPoint[] mfps_ret = new LatLonPoint[cnt];
        cnt = 0;
        for (int i = 0; i < coors.length / 2; i++) {
            if (mfps[i] != null) {
                mfps_ret[cnt] = new LatLonPoint();
                mfps_ret[cnt].setLatLon(mfps[i].x,mfps[i].y);
                cnt++;

            }
        }
        return mfps_ret;
    }	
	
	
	public void load(File vFile){
	        int cnt = 0;
	        int rownumber = 0;
	        int number = 0;
	        try {
	            BufferedReader str_input;
	            str_input = new BufferedReader(new InputStreamReader(new FileInputStream(vFile.getAbsoluteFile()), "Cp1251"));
	            String Line = null;
	            int line_number = 0;
	            CMPRecord currentCMPR = null; 
	            
	            while ((Line = str_input.readLine()) != null) {
	                line_number++;
                
	                try {
	                    if (Line.isEmpty()) {
	                        continue;
	                    }
	                    if (Line.trim().equals("[POLYGON]")
	                            || Line.trim().equals("[POI]")
	                            || Line.trim().equals("[POLYLINE]")) {
	                        cnt++;
	                        rownumber = 0;
	                        currentCMPR = new CMPRecord();
	                        if (Line.trim().equals("[POI]"))
	                        		currentCMPR.RecType = 1;
	                        if (Line.trim().equals("[POLYLINE]"))
	                        		currentCMPR.RecType = 2;
	                        if (Line.trim().equals("[POLYGON]"))
	                        		currentCMPR.RecType = 3;
	                        continue;
	                    } else {
	                        rownumber++;
	                    }
                    
	                    if (Line.trim().equals("[END]")) {
	                        if (currentCMPR !=null){
	                        	if (currentCMPR.Data != null ) 
	                        	    this.alData.add(currentCMPR);
	                        	currentCMPR = null;
	                        }
	                        continue;
	                    	
	                    }
	                    
	                    if (Line.trim().startsWith("Type=")) {
	                        if (currentCMPR !=null)
	                        	currentCMPR.Type = Line.substring(5).trim();
	                    }
	                    if (Line.trim().startsWith("Data")) {
	                        if (currentCMPR !=null){
	                        	currentCMPR.Data = this.convertDD2Points(Line.substring(6).trim());
	                        	currentCMPR.Level = Integer.parseInt(Line.trim().substring(4,5));
	                        }
	                        
	                    }
	                    if (Line.trim().startsWith("Label=")) {
	                        if (currentCMPR !=null)
	                        	currentCMPR.Label = Line.substring(6).trim();
	                    }

	                } catch (Exception e) {
	                    System.err.println("Problem in MP file reading:LINE" + line_number + "  " + e.getMessage() + currentCMPR.Type + currentCMPR.Data);
	                }
	            }
	            str_input.close();
	            System.out.println("MP File loaded. "+ this.alData.size()+" records. ");

	        } catch (IOException e) {
	            System.err.println("Problems with MP File:");
	            System.err.println(e.getMessage());
	        } finally {
	        }
	        
	        Iterator<CMPRecord> itc = this.alData.iterator();
	        while(itc.hasNext()){
	        	CMPRecord cmpr = itc.next();
	        	if ((cmpr.Data !=null) &&
	        	   (cmpr.Data.length>0)){
	        		if (this.minDD == null){
	        			this.minDD = new LatLonPoint(cmpr.Data[0]);
	        			this.maxDD = new LatLonPoint(cmpr.Data[0]);
	        		}
	        		for (int i=0; i<cmpr.Data.length;i++){
	        			LatLonPoint llp = cmpr.Data[i];
	        		    if (llp.getLatitude()<this.minDD.getLatitude()){
	        		    	this.minDD.setLatitude(llp.getLatitude());
	        		    }
	        		    if (llp.getLatitude()>this.maxDD.getLatitude()){
	        		    	this.maxDD.setLatitude(llp.getLatitude());
	        		    }
	        		    if (llp.getLongitude()<this.minDD.getLongitude()){
	        		    	this.minDD.setLongitude(llp.getLongitude());
	        		    }
	        		    if (llp.getLongitude()>this.maxDD.getLongitude()){
	        		    	this.maxDD.setLongitude(llp.getLongitude());
	        		    }		        			
	        		}
	        	}
	        	
	        	
	        	
	        	
	        }
	        
	        
	        
	        
		
	}
		
	

}
