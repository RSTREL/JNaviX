/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2009  Oakim
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package org.oakimsoft.osm_file;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.oakimsoft.data_type_support.CGPGGA;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * @author user
 */
public class OSMParser {

	File file;
	TreeMap<Long,OSMNode> nodeCollection = null;
	TreeMap<Long,OSMWay>  wayCollection = null;
	TreeMap<Long,OSMRelation>  relationCollection = null;

	public class myHandler extends DefaultHandler {

		TreeMap<Long,OSMNode> nodeCollection = null;
		TreeMap<Long,OSMWay>  wayCollection = null;
		TreeMap<Long,OSMRelation>  relationCollection = null;

		private boolean readchars = true;
		private CGPGGA gpgga_common = new CGPGGA();
		private StringBuffer curelem = new StringBuffer();
		private OSMNode currentOSMNode = new OSMNode();
		private OSMWay currentOSMWay = new OSMWay();
		private OSMRelation currentOSMRelation = new OSMRelation();
		private int currentType = -1;
		private String currentTagKey ;
		private String currentTagValue ;
		
		
		
				
		

		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

			curelem.delete(0, curelem.length());
 			curelem.append(qName);
			// System.out.println(qName + " Start  <  ");

			if (qName.toString().equals("node")) {
				currentType = 1;
				currentOSMNode = null;
				currentOSMNode = new OSMNode(); 
			}
			if (qName.toString().equals("way")) {
				currentType = 2;
				currentOSMWay = null;
				currentOSMWay = new OSMWay(); 
			}
			if (qName.toString().equals("relation")) {
				currentType = 3;
				currentOSMRelation = null;
				currentOSMRelation = new OSMRelation(); 
			}

			
			if (attributes != null) {
				if (curelem.toString().equals("member") && (currentType == 3)){
					String lvType = null;
					String lvRef = null;
					String lvRole = null;
					for (int i = 0; i < attributes.getLength(); i++) {
						if (attributes.getLocalName(i).equals("type"))
							lvType = attributes.getValue(i);
						if (attributes.getLocalName(i).equals("ref"))
							lvRef = attributes.getValue(i);
						if (attributes.getLocalName(i).equals("role"))
							lvRole = attributes.getValue(i);
					}
					if ((lvType != null)&&(lvRef != null)&&(lvRole != null)){
						if(lvType.equals("node")){
//	    					if (nodeCollection.get(Long.parseLong(lvRef))!=null)
								  currentOSMRelation.nodes.put((Long.parseLong(lvRef)),lvRole );							
						}
						if(lvType.equals("way")){
// 	    					if (wayCollection.get(Long.parseLong(lvRef))!=null)
								  currentOSMRelation.ways.put((Long.parseLong(lvRef)),lvRole );							
						}
						if(lvType.equals("relation")){
// 	    					if (relationCollection.get(Long.parseLong(lvRef))!=null)
								  currentOSMRelation.relations.put(Long.parseLong(lvRef),lvRole );							
						}
					}
				}
				
				
				for (int i = 0; i < attributes.getLength(); i++) {
					if (curelem.toString().equals("node") && attributes.getLocalName(i).equals("id")) {
						currentOSMNode.id = Integer.parseInt(attributes.getValue(i));
// 						gpgga_common.Latitude = Double.parseDouble(attributes.getValue(i));
					}
					if (curelem.toString().equals("node") && attributes.getLocalName(i).equals("lon")) {
  						currentOSMNode.latLon.setLongitude((float)Double.parseDouble(attributes.getValue(i)));
					}
					if (curelem.toString().equals("node") && attributes.getLocalName(i).equals("lat")) {
  						currentOSMNode.latLon.setLatitude((float)Double.parseDouble(attributes.getValue(i)));
					}

					if (curelem.toString().equals("way") && attributes.getLocalName(i).equals("id")) {
						currentOSMWay.id = Integer.parseInt(attributes.getValue(i));
					}
					if (curelem.toString().equals("relation") && attributes.getLocalName(i).equals("id")) {
						currentOSMRelation.id = Integer.parseInt(attributes.getValue(i));
					}

					if (curelem.toString().equals("tag") && attributes.getLocalName(i).equals("k")) {
						currentTagKey = attributes.getValue(i);
					}
					if (curelem.toString().equals("tag") && attributes.getLocalName(i).equals("v")) {
						currentTagValue = attributes.getValue(i);
					}

					if (curelem.toString().equals("nd") && attributes.getLocalName(i).equals("ref")) {
						if (currentType == 2){
							if (nodeCollection.get(Long.parseLong(attributes.getValue(i)))!=null)
							  currentOSMWay.nodes.add(nodeCollection.get(Long.parseLong(attributes.getValue(i))) );
						}
					}
//					if (curelem.toString().equals("nd") && attributes.getLocalName(i).equals("ref")) {
//						if (currentType == 2){
//							if (nodeCollection.get(Long.parseLong(attributes.getValue(i)))!=null)
//							  currentOSMWay.nodes.add(nodeCollection.get(Long.parseLong(attributes.getValue(i))) );
//						}
//					}
					
					
				}
			}
			readchars = true;
		}

		public void endElement(String uri, String localName, String qName) throws SAXException { // no
																									// op
			if (qName.toUpperCase().equals("NODE")) {
				this.nodeCollection.put(currentOSMNode.id, currentOSMNode);
			}
			if (qName.toUpperCase().equals("WAY")) {
				this.wayCollection.put(currentOSMWay.id, currentOSMWay);
			}
			if (qName.toUpperCase().equals("RELATION")) {
				this.relationCollection.put(currentOSMRelation.id, currentOSMRelation);
			}
			if (qName.toUpperCase().equals("TAG")) {
				if (currentType == 1){
					currentOSMNode.tags.put(currentTagKey, currentTagValue);
				}
				if (currentType == 2){
					currentOSMWay.tags.put(currentTagKey, currentTagValue);
				}
				if (currentType == 3){
					currentOSMRelation.tags.put(currentTagKey, currentTagValue);
				}
			}
			
			
		}

		public void characters(char ch[], int start, int length) throws SAXException {

			if (readchars) {
				
				
				if (curelem.toString().equals("ele")) {
					gpgga_common.Altitude = Double.parseDouble(new String(ch, start, length));
				}
				if (curelem.toString().equals("time")) {
					try {
						String vl = new String(ch, start, length);
						gpgga_common.UTCTime = vl;
						gpgga_common.seconds = Integer.parseInt(gpgga_common.UTCTime.substring(11, 13)) * 3600 + Integer.parseInt(gpgga_common.UTCTime.substring(14, 16)) * 60
								+ Integer.parseInt(gpgga_common.UTCTime.substring(17, 19));
						DateFormat dateFormat = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'Z'");
						Date date = (Date) dateFormat.parse(vl);
						gpgga_common.UTCMills = date.getTime();
					} catch (ParseException ex) {
						Logger.getLogger(OSMParser.class.getName()).log(Level.SEVERE, null, ex);
					}
				}

				readchars = false;
			}

		}
	}


	public OSMParser(TreeMap<Long,OSMNode> vNodeCollection, TreeMap<Long,OSMWay>  vWayCollection , TreeMap<Long,OSMRelation>  vRelationCollection, File p_file) {
		file = p_file;
		nodeCollection = vNodeCollection;
		wayCollection =  vWayCollection;
		relationCollection =  vRelationCollection;

	}

	public void pocessData() {

		if (file.getAbsoluteFile().getName().toUpperCase().endsWith(".OSM")) {
			try {
				InputStream bin_input_stream = new FileInputStream(file.getAbsoluteFile());
				DataInputStream bin_input = new DataInputStream(bin_input_stream);

				myHandler handler = new myHandler();
				handler.wayCollection = this.wayCollection;
				handler.nodeCollection = this.nodeCollection;
				handler.relationCollection = this.relationCollection;
				
				
				SAXParserFactory factory = SAXParserFactory.newInstance();
				try {
					SAXParser saxParser = factory.newSAXParser();
					saxParser.parse(bin_input_stream, handler);
				} catch (Exception e) {
				}
				bin_input.close();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}

	}
}
