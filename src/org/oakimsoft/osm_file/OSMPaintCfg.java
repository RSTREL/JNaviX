package org.oakimsoft.osm_file;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.TreeMap;

public class OSMPaintCfg {
	public static final int OBJTYPE_LINE = 1; 
	public static final int OBJTYPE_AREA = 2;
	public static final int OBJTYPE_POINT = 3;
	public static final int INNERTYPE_SOLID = 1;
	public static final int INNERTYPE_DASHED = 2;
	public static final int INNERTYPE_NONE = 3;
	public static final int INNERTYPE_SOLID_BORDERED = 4;
	public static final int INNERTYPE_DASHED_BORDERED = 5;
	public static final int LINE_UOM_PIXELS = 1;
	public static final int LINE_UOM_METERS = 2;
	
	
	
	public class CPaintingRec{
		public int objType = 0;
		public int innerType = 0;
		public int lineUoM = 0;
		public int lineWidth = 0;
		public Color innerColor = null;
		public Color outerColor = null;
		public Double minScale = -1d;
		public Double maxScale = -1d;
		
	}
	
	public TreeMap<String,CPaintingRec>  parameters = new TreeMap<String,CPaintingRec>();
	
	
	public  OSMPaintCfg(){
		this.loadPrameters();
	}
	
	public void loadPrameters(){
		
        BufferedReader str_input;
        this.parameters.clear();

        try {
			str_input = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/cfg/osmpaint.cfg"), "utf8"));
	        String Line = null;
	        while ((Line = str_input.readLine()) != null) {
	        	if (Line.isEmpty()){
	        		continue;
	        	}
	        	if (Line.trim().startsWith("#")){
	        		continue;
	        	}
	        	String words[];
	        	words = Line.split(";");
	        	if (words.length < 8){
	        		continue;
	        	}
	        	
	        	CPaintingRec pr = new CPaintingRec();
	        	if (words[2].equalsIgnoreCase("A")){
	        		pr.objType = OSMPaintCfg.OBJTYPE_AREA;	
	        	}
	        	if (words[2].equalsIgnoreCase("L")){
	        		pr.objType = OSMPaintCfg.OBJTYPE_LINE;	
	        	}
	        	if (words[2].equalsIgnoreCase("P")){
	        		pr.objType = OSMPaintCfg.OBJTYPE_POINT;	
	        	}
	        	if (words[3].equalsIgnoreCase("S")){
	        		pr.innerType = OSMPaintCfg.INNERTYPE_SOLID;	
	        	}	        	
	        	if (words[3].equalsIgnoreCase("D")){
	        		pr.innerType = OSMPaintCfg.INNERTYPE_DASHED;	
	        	}	        	
	        	if (words[3].equalsIgnoreCase("N")){
	        		pr.innerType = OSMPaintCfg.INNERTYPE_NONE;	
	        	}	        	
	        	if (words[3].equalsIgnoreCase("SB")){
	        		pr.innerType = OSMPaintCfg.INNERTYPE_SOLID_BORDERED;	
	        	}	        	
        	
	        	if (words[3].equalsIgnoreCase("DB")){
	        		pr.innerType = OSMPaintCfg.INNERTYPE_DASHED_BORDERED;	
	        	}	        	
	        	if (words[4].equalsIgnoreCase("P")){
	        		pr.lineUoM = OSMPaintCfg.LINE_UOM_PIXELS;	
	        	}	        	
	        	if (words[4].equalsIgnoreCase("M")){
	        		pr.lineUoM = OSMPaintCfg.LINE_UOM_METERS;	
	        	}	        	

	        	pr.lineWidth = Integer.parseInt(words[5]);
	        	int r = 0, g = 0, b = 0 ;
	        	r = Integer.parseInt(words[6].substring(0, 3));
	        	g = Integer.parseInt(words[6].substring(3, 6));
	        	b = Integer.parseInt(words[6].substring(6, 9));
	        	
	        	
	        	pr.innerColor = new Color( r,g,b );
	        	r = Integer.parseInt(words[7].substring(0, 3));
	        	g = Integer.parseInt(words[7].substring(3, 6));
	        	b = Integer.parseInt(words[7].substring(6, 9));	        	
	        	pr.outerColor = new Color( r,g,b );

	        	if (words.length >= 10){
		        	pr.minScale = Double.parseDouble(words[8]);
		        	pr.maxScale = Double.parseDouble(words[9]);
	        	}

	        	
	        	parameters.put(words[0].toLowerCase().trim().concat("-").concat(words[1].toLowerCase().trim()) , pr);
	         
	        		
	        	
	        	
	        	
	        	System.out.println(Line);
	        }

        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		
	}
	
}
