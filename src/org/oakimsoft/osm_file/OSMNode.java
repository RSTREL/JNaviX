package org.oakimsoft.osm_file;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

import com.bbn.openmap.LatLonPoint;

public class OSMNode{
	public long id = -1;
	public int recType = -1;
	public LatLonPoint latLon = null;
	public TreeMap<String,String> tags = null;
	
	public OSMNode(){
		id = -1;
		latLon = new LatLonPoint(0d,0d);
		tags =  new TreeMap<String,String>();
	}
	
	public void  clear(){
		id = -1;
		latLon.setLatLon(0d,0d);
		tags.clear();
	}
	
	public void assign(OSMNode vNode){
		this.clear();
		this.id = vNode.id;
		this.latLon.setLatLon(vNode.latLon);

		Collection<String> c = tags.values();
	    Iterator<String> itr = c.iterator();
	    while(itr.hasNext()){
	      this.tags.put(itr.next(),vNode.tags.get(itr.next()));
		}
	}
}