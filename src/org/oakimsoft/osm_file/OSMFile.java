package org.oakimsoft.osm_file;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.oakimsoft.common.CLog;
import org.oakimsoft.data_type_support.CAbstractMap;

import com.bbn.openmap.LatLonPoint;

public class OSMFile extends CAbstractMap {
	public static final int					RECTYPE_HIGHWAY				= 1;
	public static final int					RECTYPE_BARRIER				= 2;
	public static final int					RECTYPE_CYCLEWAY			= 3;
	public static final int					RECTYPE_WATERWAY			= 4;
	public static final int					RECTYPE_RAILWAY				= 5;
	public static final int					RECTYPE_AEROWAY				= 6;
	public static final int					RECTYPE_AERIALWAY			= 7;
	public static final int					RECTYPE_PUBLIC_TRANSPORT	= 8;
	public static final int					RECTYPE_POWER				= 9;
	public static final int					RECTYPE_MAN_MADE			= 10;
	public static final int					RECTYPE_BUILDING			= 11;
	public static final int					RECTYPE_LEISURE				= 12;
	public static final int					RECTYPE_AMENITY				= 13;
	public static final int					RECTYPE_OFFICE				= 14;
	public static final int					RECTYPE_SHOP				= 15;
	public static final int					RECTYPE_CRAFT				= 16;
	public static final int					RECTYPE_EMERGENCY			= 17;
	public static final int					RECTYPE_TOURISM				= 18;
	public static final int					RECTYPE_HISTORIC			= 19;
	public static final int					RECTYPE_LANDUSE				= 20;
	public static final int					RECTYPE_MILITARY			= 21;
	public static final int					RECTYPE_NATURAL				= 22;
	public static final int					RECTYPE_GEOLOGICAL			= 23;
	public static final int					RECTYPE_ROUTE				= 24;
	public static final int					RECTYPE_BOUNDARY			= 25;
	public static final int					RECTYPE_SPORT				= 26;

	public TreeMap<Long, OSMNode>			nodeCollection;
	public TreeMap<Long, OSMWay>			wayCollection;
	public TreeMap<Long, OSMRelation>		relationCollection;
	public static TreeMap<String, Integer>	recName2RecType				= new TreeMap<String, Integer>();
	public static TreeMap<Integer, String>	recType2RecName				= new TreeMap<Integer, String>();

	public OSMFile() {

		recName2RecType.put("highway"
				, OSMFile.RECTYPE_HIGHWAY);
		recName2RecType.put("barrier"
				, OSMFile.RECTYPE_BARRIER);
		recName2RecType.put("cycleway"
				, OSMFile.RECTYPE_CYCLEWAY);
		recName2RecType.put("waterway"
				, OSMFile.RECTYPE_WATERWAY);
		recName2RecType.put("railway"
				, OSMFile.RECTYPE_RAILWAY);
		recName2RecType.put("aeroway"
				, OSMFile.RECTYPE_AEROWAY);
		recName2RecType.put("aerialway"
				, OSMFile.RECTYPE_AERIALWAY);
		recName2RecType.put("public_transport"
				, OSMFile.RECTYPE_PUBLIC_TRANSPORT);
		recName2RecType.put("power"
				, OSMFile.RECTYPE_POWER);
		recName2RecType.put("man_made"
				, OSMFile.RECTYPE_MAN_MADE);
		recName2RecType.put("building"
				, OSMFile.RECTYPE_BUILDING);
		recName2RecType.put("leisure"
				, OSMFile.RECTYPE_LEISURE);
		recName2RecType.put("amenity"
				, OSMFile.RECTYPE_AMENITY);
		recName2RecType.put("office"
				, OSMFile.RECTYPE_OFFICE);
		recName2RecType.put("shop"
				, OSMFile.RECTYPE_SHOP);
		recName2RecType.put("craft"
				, OSMFile.RECTYPE_CRAFT);
		recName2RecType.put("emergency"
				, OSMFile.RECTYPE_EMERGENCY);
		recName2RecType.put("tourism"
				, OSMFile.RECTYPE_TOURISM);
		recName2RecType.put("historic"
				, OSMFile.RECTYPE_HISTORIC);
		recName2RecType.put("landuse"
				, OSMFile.RECTYPE_LANDUSE);
		recName2RecType.put("military"
				, OSMFile.RECTYPE_MILITARY);
		recName2RecType.put("natural"
				, OSMFile.RECTYPE_NATURAL);
		recName2RecType.put("geological"
				, OSMFile.RECTYPE_GEOLOGICAL);
		recName2RecType.put("route"
				, OSMFile.RECTYPE_ROUTE);
		recName2RecType.put("boundary"
				, OSMFile.RECTYPE_BOUNDARY);
		recName2RecType.put("sport"
				, OSMFile.RECTYPE_SPORT);

		Iterator<Entry<String, Integer>> it = recName2RecType.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Integer> pairs = it.next();
			recType2RecName.put(pairs.getValue(), pairs.getKey());
		}
	}

	
	public void load(File vFile) {
		nodeCollection = new TreeMap<Long, OSMNode>();
		wayCollection = new TreeMap<Long, OSMWay>();
		relationCollection = new TreeMap<Long, OSMRelation>();
		Iterator<Entry<Long, OSMWay>> it2 = null;
		Iterator<Entry<Long, OSMRelation>> itRel = null;

		TreeMap<Long, OSMWay> localWayCollection = new TreeMap<Long, OSMWay>();

		OSMParser OSMparser = new OSMParser(nodeCollection, localWayCollection, relationCollection, vFile);
		OSMparser.pocessData();

		CLog.info("Loaded nodes " + String.valueOf(nodeCollection.size()));
		CLog.info("Loaded ways " + String.valueOf(localWayCollection.size()));
		CLog.info("Loaded relations " + String.valueOf(relationCollection.size()));

		// Определим для каждого WAY его тип
		it2 = null;
		it2 = localWayCollection.entrySet().iterator();
		while (it2.hasNext()) {
			Map.Entry<Long, OSMWay> pairs = it2.next();
			OSMWay osmway = localWayCollection.get(pairs.getKey());
			osmway.recType = this.getRecTypeByTag(osmway.tags);
		}

		// Определим для каждого WAY границы расположения в видк центра и
		// радиуса описанной окружности
		it2 = null;
		it2 = localWayCollection.entrySet().iterator();
		while (it2.hasNext()) {
			Map.Entry<Long, OSMWay> pairs = it2.next();
			OSMWay way = pairs.getValue();
			float minLat = 0f, minLon = 0f, maxLat = 0f, maxLon = 0f;
			for (int i = 0; i < way.nodes.size(); i++) {
				if (way.nodes.get(i) != null) {
					if (i == 0) {
						minLat = way.nodes.get(i).latLon.getLatitude();
						maxLat = way.nodes.get(i).latLon.getLatitude();
						minLon = way.nodes.get(i).latLon.getLongitude();
						maxLon = way.nodes.get(i).latLon.getLongitude();
					}
					if (minLat > way.nodes.get(i).latLon.getLatitude()) {
						minLat = way.nodes.get(i).latLon.getLatitude();
					}
					if (maxLat < way.nodes.get(i).latLon.getLatitude()) {
						maxLat = way.nodes.get(i).latLon.getLatitude();
					}
					if (minLon > way.nodes.get(i).latLon.getLongitude()) {
						minLon = way.nodes.get(i).latLon.getLongitude();
					}
					if (maxLon < way.nodes.get(i).latLon.getLongitude()) {
						maxLon = way.nodes.get(i).latLon.getLongitude();
					}
				}
			}
			way.centerPoint.setLatLon((minLat + maxLat) / 2.0, (minLon + maxLon) / 2.0);
			if ((maxLon - minLon) > (maxLat - minLat)) {
				way.radius = (maxLon - minLon) / 2.0;
			} else {
				way.radius = (maxLat - minLat) / 2.0;
			}
		}

		// привяжем Ways к Relation
		itRel = null;
		itRel = relationCollection.entrySet().iterator();
		while (itRel.hasNext()) {
			Map.Entry<Long, OSMRelation> pairs = itRel.next();
			OSMRelation osmrelation = pairs.getValue();
//			if (osmrelation.id == 149308) {
//				System.out.println("FOUND");
//			}
			osmrelation.recType = this.getRecTypeByTag(osmrelation.tags);
			Iterator<Entry<Long, String>> itways = osmrelation.ways.entrySet().iterator();
			while (itways.hasNext()) {
				Map.Entry<Long, String> pair1 = itways.next();
				if (localWayCollection.get(pair1.getKey()) != null) {

					OSMWay localWay = localWayCollection.get(pair1.getKey());
//					if (localWay.id == 122109776) {
//						System.out.println("FOUND");
//					}
					localWay.relations.add(osmrelation);
					if (osmrelation.recType != -1)
						localWay.relationTags = osmrelation.tags;
					// Если WAY был ранее не опознан, добовляем опознавалку
					// из RELATION
					if (localWay.recType == -1) {
						localWay.recType = osmrelation.recType;
					}
				}
			}
		}

		
		
		// Перетасуем WAY в правильной последовательности нанесения на карту
		Long keyval = 0l;
		it2 = null;
		it2 = localWayCollection.entrySet().iterator();
		while (it2.hasNext()) {
			Map.Entry<Long, OSMWay> pairs = it2.next();
			OSMWay osmway = localWayCollection.get(pairs.getKey());
			wayCollection.put(keyval++, osmway);
		}

		// it2 = null;
		// it2 = localWayCollection.entrySet().iterator();
		// while (it2.hasNext()){
		// Map.Entry<Long,OSMWay> pairs = it2.next();
		// OSMWay osmway = localWayCollection.get(pairs.getKey());
		// if (osmway.tags.get("natural") != null){
		// wayCollection.put(keyval ++, osmway);
		// }
		// }
		//
		// it2 = null;
		// it2 = localWayCollection.entrySet().iterator();
		// while (it2.hasNext()){
		// Map.Entry<Long,OSMWay> pairs = it2.next();
		// OSMWay osmway = localWayCollection.get(pairs.getKey());
		// if (osmway.tags.get("landuse") != null){
		// wayCollection.put(keyval ++, osmway);
		// }
		// }
		//
		//
		// it2 = null;
		// it2 = localWayCollection.entrySet().iterator();
		// while (it2.hasNext()){
		// Map.Entry<Long,OSMWay> pairs = it2.next();
		// OSMWay osmway = localWayCollection.get(pairs.getKey());
		// if (osmway.tags.get("waterway") != null){
		// wayCollection.put(keyval ++, osmway);
		// }
		// }
		//
		//
		//
		//
		// it2 = null;
		// it2 = localWayCollection.entrySet().iterator();
		// while (it2.hasNext()){
		// Map.Entry<Long,OSMWay> pairs = it2.next();
		// OSMWay osmway = localWayCollection.get(pairs.getKey());
		// if (osmway.tags.get("highway") != null){
		// wayCollection.put(keyval++ , osmway);
		// }
		// }
		//
		// it2 = null;
		// it2 = localWayCollection.entrySet().iterator();
		// while (it2.hasNext()){
		// Map.Entry<Long,OSMWay> pairs = it2.next();
		// OSMWay osmway = localWayCollection.get(pairs.getKey());
		// if (osmway.tags.get("railway") != null){
		// wayCollection.put(keyval++ , osmway);
		// }
		// }
		//
		//
		// it2 = null;
		// it2 = localWayCollection.entrySet().iterator();
		// while (it2.hasNext()){
		// Map.Entry<Long,OSMWay> pairs = it2.next();
		// OSMWay osmway = localWayCollection.get(pairs.getKey());
		// if (osmway.tags.get("barrier") != null){
		// wayCollection.put(keyval++ , osmway);
		// }
		// }
		//
		//
		//
		//
		// it2 = null;
		// it2 = localWayCollection.entrySet().iterator();
		// while (it2.hasNext()){
		// Map.Entry<Long,OSMWay> pairs = it2.next();
		// OSMWay osmway = localWayCollection.get(pairs.getKey());
		// if (osmway.tags.get("building") != null){
		// wayCollection.put(keyval++ , osmway);
		// }
		// }
		//
		// it2 = null;
		// it2 = localWayCollection.entrySet().iterator();
		// while (it2.hasNext()){
		// Map.Entry<Long,OSMWay> pairs = it2.next();
		// OSMWay osmway = localWayCollection.get(pairs.getKey());
		// if (osmway.tags.get("man_made") != null){
		// wayCollection.put(keyval++ , osmway);
		// }
		// }
		//

		localWayCollection.clear();

		Iterator<Entry<Long, OSMNode>> it = nodeCollection.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Long, OSMNode> pairs = it.next();

			OSMNode osmnode = nodeCollection.get(pairs.getKey());
			LatLonPoint llp = osmnode.latLon;
			if (this.minDD == null) {
				this.minDD = new LatLonPoint(llp);
				this.maxDD = new LatLonPoint(llp);
			}

			if (llp.getLatitude() < this.minDD.getLatitude()) {
				this.minDD.setLatitude(llp.getLatitude());
			}
			if (llp.getLatitude() > this.maxDD.getLatitude()) {
				this.maxDD.setLatitude(llp.getLatitude());
			}
			if (llp.getLongitude() < this.minDD.getLongitude()) {
				this.minDD.setLongitude(llp.getLongitude());
			}
			if (llp.getLongitude() > this.maxDD.getLongitude()) {
				this.maxDD.setLongitude(llp.getLongitude());
			}

		}
	}

	public int getRecTypeByTag(TreeMap<String, String> pTags) {
		Iterator<Entry<String, Integer>> it = null;
		it = this.recName2RecType.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Integer> pairs = it.next();
			if (pTags.get(pairs.getKey()) != null) {
				return pairs.getValue();
			}
		}
		return -1;
	}

	public void getObjectsByDD(LatLonPoint llp) {
		Iterator<Entry<Long, OSMWay>> it2 = this.wayCollection.entrySet().iterator();
		int count = 0;
		float closestDist = 100.0f;
		OSMWay closestWay = null;

		while (it2.hasNext()) {
			Map.Entry<Long, OSMWay> pairs = it2.next();
			OSMWay way = this.wayCollection.get(pairs.getKey());
			if ((llp.getLatitude() >= way.centerPoint.getLatitude() - way.radius) &&
					(llp.getLatitude() <= way.centerPoint.getLatitude() + way.radius) &&
					(llp.getLongitude() >= way.centerPoint.getLongitude() - way.radius) &&
					(llp.getLongitude() <= way.centerPoint.getLatitude() + way.radius)) {
				for (int i = 0; i < way.nodes.size() - 1; i++) {
					if ((way.nodes.get(i) != null) && (way.nodes.get(i + 1) != null)) {
						float d1 = way.nodes.get(i).latLon.distance(llp);
						float d2 = way.nodes.get(i + 1).latLon.distance(llp);
						float d3 = way.nodes.get(i + 1).latLon.distance(way.nodes.get(i + 1).latLon);
						if (d1 + d2 - d3 < closestDist) {
							closestDist = d1 + d2 - d3;
							closestWay = way;

						}

					}
				}
				count++;
			}
		}
		CLog.info(this, "Objects " + count);
		CLog.info(this, closestWay.toString());
		CLog.info(this, closestWay.id + " ID ");
		if (closestWay != null) {

			Iterator<Entry<String, String>> itx = closestWay.tags.entrySet().iterator();

			while (itx.hasNext()) {
				Map.Entry<String, String> pairs = itx.next();
				// String key = pairs.getKey();
				CLog.info(this, pairs.getKey() + " " + pairs.getValue());
			}
			itx = null;
			if (closestWay.relationTags != null) {
				itx = closestWay.relationTags.entrySet().iterator();
				CLog.info(this, "From relation");

				while (itx.hasNext()) {
					Map.Entry<String, String> pairs = itx.next();
					// String key = pairs.getKey();
					CLog.info(this, pairs.getKey() + " " + pairs.getValue());
				}
			}

		}
	}

}
