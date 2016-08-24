package org.oakimsoft.osm_file;


import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

import com.bbn.openmap.LatLonPoint;

public class OSMTag{
	public long id = -1;
	public LatLonPoint latLon = null;
	public TreeMap<String,String> values = null;
	
	public OSMTag(){
		id = -1;
		latLon = new LatLonPoint(0d,0d);
		values =  new TreeMap<String,String>();
	}
	
	public void  clear(){
		id = -1;
		latLon.setLatLon(0d,0d);
		values.clear();
	}
	
	public void assign(OSMTag vNode){
		this.clear();
		this.id = vNode.id;
		this.latLon.setLatLon(vNode.latLon);

		Collection<String> c = values.values();
	    Iterator<String> itr = c.iterator();
	    while(itr.hasNext()){
	      this.values.put(itr.next(),vNode.values.get(itr.next()));
		}
	}
}