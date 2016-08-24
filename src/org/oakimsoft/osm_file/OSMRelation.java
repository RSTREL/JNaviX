package org.oakimsoft.osm_file;

import java.util.TreeMap;

public class OSMRelation {
	public long id = -1;
	public int recType = -1;
	public TreeMap<String,String> tags = null;
	public TreeMap<Long,String> nodes = null;
	public TreeMap<Long,String> ways = null;
	public TreeMap<Long,String> relations = null;
	
	
	public OSMRelation(){
		tags = new TreeMap<String,String>();
		nodes = new TreeMap<Long,String>();
		ways = new TreeMap<Long,String>();
		relations = new TreeMap<Long,String>();
	}


}
