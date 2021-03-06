/*
 * $Header: /cvs/distapps/openmap/src/openmap/com/bbn/openmap/image/wms/CapabilitiesSupport.java,v 1.1.2.7 2009/01/15 04:58:31 dietrick Exp $
 *
 * Copyright 2001-2005 OBR Centrum Techniki Morskiej, All rights reserved.
 *
 */
package com.bbn.openmap.image.wms;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.bbn.openmap.image.ImageServer;
import com.bbn.openmap.image.WMTConstants;
import com.bbn.openmap.layer.util.http.HttpConnection;
import com.bbn.openmap.proj.coords.BoundingBox;
import com.bbn.openmap.proj.coords.CoordinateReferenceSystem;

/**
 * @version $Header:
 *          /cvs/CVS_LEBA/external/openmap/openmap/src/openmap/com/bbn/openmap/wms/CapabilitiesSupport.java,v
 *          1.1 2006/03/21 10:27:54 tomrak Exp $
 * @author pitek
 */
public class CapabilitiesSupport {

    public static final String WMSPrefix = ImageServer.OpenMapPrefix + "wms.";

    public static final int FMT_GETCAPS = 0;

    public static final int FMT_GETMAP = 1;

    public static final int FMT_GETFEATUREINFO = 2;

    public static final int FMT_EXCEPTIONS = 3;

    public static final int FMT_MAIN = 3;

    private List[] formatsList = { null, null, null, null };

    private String[] onlineResourcesList = { null, null, null, null };

    private List keywordsList = null;

    private String wmsTitle = null;

    private String wmsAbstract = null;

    private int updateSequence = 1;

    private List wmslayers = new ArrayList();

    private String layersTitle;

    private Collection crsCodes = CoordinateReferenceSystem.getCodes();

    /**
     * Creates a new instance of CapabilitiesSupport
     * 
     * @param requestHandler
     * @param requestProperties
     */
    public CapabilitiesSupport(Properties props, String scheme, String hostName, int port, String path)
            throws WMSException {

        
        wmsTitle = props.getProperty(WMSPrefix + "Title", "Sample Title");
        wmsAbstract = props.getProperty(WMSPrefix + "Abstract", "Sample Abstract");
        layersTitle = props.getProperty(WMSPrefix + "LayersTitle", "Sample Layer List");
        String[] strKeywords = props.getProperty(WMSPrefix + "Keyword", "").split(" ");
        List keywords = Arrays.asList(strKeywords);
        setKeywords(keywords);

        String url = scheme + "://" + hostName + ":" + port + path;
        setOnlineResource(FMT_MAIN, url);
        setOnlineResource(FMT_GETMAP, url);
        setOnlineResource(FMT_GETCAPS, url);
        setOnlineResource(FMT_GETFEATUREINFO, url);

        List al = new ArrayList();
        al.add("application/vnd.ogc.wms_xml");
        setFormats(FMT_GETCAPS, al);

        al.clear();
        al.add(HttpConnection.CONTENT_PLAIN);
        al.add(HttpConnection.CONTENT_HTML);
        // TODO: support other formats like application/vnd.ogc.gml and text/xml?
        // TODO: configurable or perhaps gettable from the FeatureInfoResponse
        setFormats(FMT_GETFEATUREINFO, al);

        al.clear();
        al.add("application/vnd.ogc.se_xml");
        setFormats(FMT_EXCEPTIONS, al);
    }

    /**
     * @return
     */
    private Document generateCapabilitiesDocument() {
        
        Document doc;
        
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            
            DOMImplementation impl = builder.getDOMImplementation();
            DocumentType doctype = impl.createDocumentType("wms", "WMT_MS_Capabilities",
                    "http://schemas.opengis.net/wms/1.1.1/WMS_MS_Capabilities.dtd");
            doc = impl.createDocument(null, "WMT_MS_Capabilities", doctype);
        } catch (javax.xml.parsers.ParserConfigurationException ex) {
            throw new RuntimeException("Cannot create new Xml Document:" + ex.getMessage());
        }
        
        Element root = doc.getDocumentElement();
        root.setAttribute("version", "1.1.1");
        root.setAttribute("updateSequence", Integer.toString(updateSequence));

        Element service = doc.createElement("Service");
        service.appendChild(textnode(doc, "Name", "OGC:WMS"));
        service.appendChild(textnode(doc, "Title", wmsTitle));
        service.appendChild(textnode(doc, "Abstract", wmsAbstract));

        if (!keywordsList.isEmpty()) {
            Element keywordListElement = doc.createElement("KeywordList");
            for (int i = 0; i < keywordsList.size(); i++) {
                keywordListElement.appendChild(textnode(doc, "Keyword", (String) keywordsList.get(i)));
            }
            service.appendChild(keywordListElement);
        }

        Element onlineResource = doc.createElement("OnlineResource");
        onlineResource.setAttribute("xmlns:xlink", "http://www.w3.org/1999/xlink");
        onlineResource.setAttribute("xlink:type", "simple");
        onlineResource.setAttribute("xlink:href", onlineResourcesList[FMT_MAIN]);
        service.appendChild(onlineResource);

        service.appendChild(textnode(doc, "Fees", "none"));
        service.appendChild(textnode(doc, "AccessConstraints", "none"));
        root.appendChild(service);

        Node capability = doc.createElement("Capability");
        Element request = doc.createElement("Request");

        request.appendChild(requestcap(doc, WMTConstants.GETCAPABILITIES, formatsList[FMT_GETCAPS], "Get",
                onlineResourcesList[FMT_GETCAPS]));
        request.appendChild(requestcap(doc, WMTConstants.GETMAP, formatsList[FMT_GETMAP], "Get",
                onlineResourcesList[FMT_GETMAP]));
        request.appendChild(requestcap(doc, WMTConstants.GETFEATUREINFO, formatsList[FMT_GETFEATUREINFO],
                "Get", onlineResourcesList[FMT_GETFEATUREINFO]));
        capability.appendChild(request);

        Element exceptionElement = doc.createElement("Exception");
        for (int i = 0; i < formatsList[FMT_EXCEPTIONS].size(); i++) {
            exceptionElement.appendChild(textnode(doc, "Format", (String) formatsList[FMT_EXCEPTIONS].get(i)));
        }
        capability.appendChild(exceptionElement);

        capability.appendChild(createLayersElement(doc));
        root.appendChild(capability);

        return doc;
    }
    
    private Element createLayersElement(Document doc) {
        Element layers = doc.createElement("Layer");
        layers.appendChild(textnode(doc, "Title", layersTitle));
        for (Iterator it = crsCodes.iterator(); it.hasNext();) {
            layers.appendChild(textnode(doc, "SRS", (String) it.next()));
        }
        
        // append bounding boxes
        appendLatLonBoundingBox(doc, layers);
        for (Iterator it = crsCodes.iterator(); it.hasNext();) {
            appendSRSBoundingBox(doc, layers, (String) it.next());
        }
        
        // append layers
        for (Iterator it = wmslayers.iterator(); it.hasNext();) {
            IWmsLayer wmsLayer = (IWmsLayer) it.next();
            createLayerElement(doc, layers, wmsLayer);
        }
        return layers;
    }
    
    private void createLayerElement(Document doc, Element layers, IWmsLayer wmsLayer){
        org.w3c.dom.Element layerElement = (org.w3c.dom.Element) node(doc, "Layer");
        layerElement.setAttribute("queryable", wmsLayer.isQueryable() ? "1" : "0");
        // implied layerElement.setAttribute("cascaded", "0");
        layerElement.setAttribute("opaque", "0");
        layerElement.setAttribute("noSubsets", "0");
        // implied layerElement.setAttribute("fixedWidth", "0");
        // implied layerElement.setAttribute("fixedHeight", "0");

        layerElement.appendChild(textnode(doc, "Name", wmsLayer.getWmsName()));
        layerElement.appendChild(textnode(doc, "Title", wmsLayer.getTitle()));
        if (wmsLayer.getAbstract() != null) {
            layerElement.appendChild(textnode(doc, "Abstract", wmsLayer.getAbstract()));
        }

        // add styles
        IWmsLayerStyle[] styles = wmsLayer.getStyles();
        if (styles != null) {
            for (int i = 0; i < styles.length; i++) {
                IWmsLayerStyle style = styles[i];
                org.w3c.dom.Element styleElement = (org.w3c.dom.Element) node(doc, "Style");
                styleElement.appendChild(textnode(doc, "Name", style.getName())); // "default"
                styleElement.appendChild(textnode(doc, "Title", style.getTitle())); // "Default
                // style"
                if (style.getAbstract() != null) {
                    styleElement.appendChild(textnode(doc, "Abstract", style.getAbstract()));
                }
                layerElement.appendChild(styleElement);
            }
        }

        // add nested layers
        if (wmsLayer instanceof IWmsNestedLayer) {
            IWmsLayer[] nestedLayers = ((IWmsNestedLayer) wmsLayer).getNestedLayers();
            if (nestedLayers != null) {
                for (int i = 0; i < nestedLayers.length; i++) {
                    createLayerElement(doc, layerElement, nestedLayers[i]);
                }
            }
        }
        
        layers.appendChild(layerElement);
    }

    /**
     */
    public void incUpdateSequence() {
        updateSequence++;
    }

    /**
     * @param request
     * @param formats
     * @return
     */
    public boolean setFormats(int request, List formats) {
        switch (request) {
        case FMT_GETMAP:
        case FMT_GETCAPS:
        case FMT_GETFEATUREINFO:
        case FMT_EXCEPTIONS:
            formatsList[request] = new ArrayList(formats);
            break;
        default:
            return false;
        }
        return true;
    }

    /**
     * @param which
     * @param url
     * @return
     */
    public boolean setOnlineResource(int which, String url) {
        switch (which) {
        case FMT_GETMAP:
        case FMT_GETCAPS:
        case FMT_GETFEATUREINFO:
        case FMT_MAIN:
            onlineResourcesList[which] = url;
            break;
        default:
            return false;
        }
        return true;
    }

    /**
     * @param keywordsList
     * @return
     */
    public void setKeywords(List keywordsList) {
        this.keywordsList = keywordsList;
    }


    public void addLayer(IWmsLayer wmsLayer) {
        wmslayers.add(wmsLayer);
    }
    
    public void setLayersTitle(String title) {
        this.layersTitle = title;
    }

    private void appendLatLonBoundingBox(Document doc, Element layers) {
        org.w3c.dom.Element e1 = (org.w3c.dom.Element) node(doc, "LatLonBoundingBox");
        e1.setAttribute("minx", "-180");
        e1.setAttribute("miny", "-90");
        e1.setAttribute("maxx", "180");
        e1.setAttribute("maxy", "90");
        layers.appendChild(e1);
    }

    private void appendSRSBoundingBox(Document doc, Element layers, String crsCode) {
        CoordinateReferenceSystem crs = CoordinateReferenceSystem.getForCode(crsCode);
        BoundingBox bbox = crs.getBoundingBox();
        if (bbox == null) {
            return;
        }
        org.w3c.dom.Element e1 = (org.w3c.dom.Element) node(doc, "BoundingBox");
        e1.setAttribute("SRS", crs.getCode());
        e1.setAttribute("minx", Double.toString(bbox.getMinX()));
        e1.setAttribute("miny", Double.toString(bbox.getMinY()));
        e1.setAttribute("maxx", Double.toString(bbox.getMaxX()));
        e1.setAttribute("maxy", Double.toString(bbox.getMaxY()));
        layers.appendChild(e1);
    }
    
    /**
     * Generate String out of the XML document object
     * 
     * @throws IOException, TransformerException, TransformerConfigurationException
     */
    public String generateXMLString() throws IOException, TransformerConfigurationException,
                    TransformerException {
        
        StringWriter strWriter = new StringWriter();
        Transformer tr = TransformerFactory.newInstance().newTransformer();
        tr.setOutputProperty(OutputKeys.INDENT, "yes");
        tr.setOutputProperty(OutputKeys.METHOD, "xml");
        tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        tr.setOutputProperty(OutputKeys.VERSION, "1.0");
        tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        Document document = generateCapabilitiesDocument();
        
        // system id not transformed by default transformer
        tr.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, document.getDoctype().getSystemId());
        
        // Serialize XML Document
        tr.transform(new DOMSource(document), new StreamResult(strWriter));
        return strWriter.toString();
    }
    
    /**
     * @param Name
     * @param Text
     * @return
     */
    private Node textnode(Document doc, String Name, String text) {
        Element e1 = doc.createElement(Name);
        if (text == null) {
            text = "";
        }
        Node n = doc.createTextNode(text);
        e1.appendChild(n);
        return e1;
    }

    /**
     * @param Name
     * @return
     */
    private Node node(Document doc, String Name) {
        return doc.createElement(Name);
    }

    /**
     * @param requestName like "GetMap"
     * @param formatList
     * @param methodName like "Get" or "Post"
     * @param url
     * @return
     */
    private Node requestcap(Document doc, String requestName, List formatList, String methodName, String url) {
        Element onlineResourceElement = doc.createElement("OnlineResource");
        onlineResourceElement.setAttribute("xmlns:xlink", "http://www.w3.org/1999/xlink");
        onlineResourceElement.setAttribute("xlink:type", "simple");
        onlineResourceElement.setAttribute("xlink:href", url);
        Element methodNode = doc.createElement(methodName);
        methodNode.appendChild(onlineResourceElement);

        Element httpNode = doc.createElement("HTTP");
        httpNode.appendChild(methodNode);

        Element dcpTypeNode = doc.createElement("DCPType");
        dcpTypeNode.appendChild(httpNode);
        
        Element requestNameNode = doc.createElement(requestName);
        for (int i = 0; i < formatList.size(); i++) {
            requestNameNode.appendChild(textnode(doc, "Format", (String) formatList.get(i)));
        }
        requestNameNode.appendChild(dcpTypeNode);

        return requestNameNode;
    }

}
