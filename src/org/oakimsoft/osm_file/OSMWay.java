package org.oakimsoft.osm_file;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

import com.bbn.openmap.LatLonPoint;

public class OSMWay{
	public long id = -1;
	public int recType = -1;
	public TreeMap<String,String> tags = null;
	public TreeMap<String,String> relationTags = null;
	public ArrayList<OSMNode> nodes = null;
	public ArrayList<OSMRelation> relations = null;
	public LatLonPoint centerPoint = new LatLonPoint();
	public Double radius = 0d;
	
	
	public TreeMap<String,String> ways1 = null;
	
	public OSMWay(){
		id = -1;
		tags =  new TreeMap<String,String>();
		relationTags = null;
		nodes = new ArrayList<OSMNode>();
		relations = new ArrayList<OSMRelation>();
		
	}
	
	public void  clear(){
		id = -1;
		tags.clear();
		relationTags = null;
		nodes.clear();
		relations.clear();
	}
	
	public void assign(OSMWay vWay){
		this.clear();
		this.id = vWay.id;

		Collection<String> c = tags.values();
	    Iterator<String> itr = c.iterator();
	    while(itr.hasNext()){
	      this.tags.put(itr.next(),vWay.tags.get(itr.next()));
		}

	    
	    Iterator<OSMNode> itr2 = vWay.nodes.iterator();
	    while(itr2.hasNext()){
		      this.nodes.add(itr2.next());
		}

	    
	    
	    
		
	}
}

