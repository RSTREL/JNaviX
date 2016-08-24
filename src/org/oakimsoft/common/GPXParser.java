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
package org.oakimsoft.common;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
public class GPXParser {

    File file;
    ArrayList pointCollection;

    public class myHandler extends DefaultHandler {

        private boolean readchars = true;
        public ArrayList pointCollection;
        private CGPGGA gpgga_common = new CGPGGA();
        private StringBuffer curelem = new StringBuffer();

        public void startElement(String uri, String localName,
                String qName, Attributes attributes)
                throws SAXException {
            curelem.delete(0, curelem.length());
            curelem.append(qName);
            //   System.out.print(qName + "  >  ");

            if (curelem.toString().equals("trkpt")) {
                gpgga_common.clear();
            }

            if (attributes != null) {
                for (int i = 0; i < attributes.getLength(); i++) {
                    if (curelem.toString().equals("trkpt") && attributes.getLocalName(i).equals("lat")) {
                        gpgga_common.Latitude = Double.parseDouble(attributes.getValue(i));
                    }
                    if (curelem.toString().equals("trkpt") && attributes.getLocalName(i).equals("lon")) {
                        gpgga_common.Longitude = Double.parseDouble(attributes.getValue(i));
                    }

                }
            }
            readchars = true;
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {	// no op
            if (qName.equalsIgnoreCase("trkpt")) {
                CGPGGA gpgga = new CGPGGA();
                gpgga.Assign(gpgga_common);
                this.pointCollection.add(gpgga);
            }
            if (qName.equalsIgnoreCase("trk")) {
                CGPGGA gpgga = new CGPGGA();
                this.pointCollection.add(gpgga);
            }
        }

        public void characters(char ch[], int start, int length)
                throws SAXException {


            if (readchars) {
                if (curelem.toString().equals("ele")) {
                    gpgga_common.Altitude = Double.parseDouble(new String(ch, start, length));
                }
                if (curelem.toString().equals("time")) {
                    try {
                        String vl = new String(ch, start, length);
                        gpgga_common.UTCTime = vl;
                        gpgga_common.seconds = Integer.parseInt(gpgga_common.UTCTime.substring(11, 13)) * 3600 + Integer.parseInt(gpgga_common.UTCTime.substring(14, 16)) * 60 + Integer.parseInt(gpgga_common.UTCTime.substring(17, 19));
                        DateFormat dateFormat = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'Z'");
                        Date date = (Date) dateFormat.parse(vl);
                        gpgga_common.UTCMills = date.getTime();
                    } catch (ParseException ex) {
                        Logger.getLogger(GPXParser.class.getName()).log(Level.SEVERE, null, ex);
                    }



                }

                readchars = false;

            }

        }
    }

    public GPXParser(ArrayList alPoints, File p_file) {
        file = p_file;
        pointCollection = alPoints;

    }

    public void pocessData() {

        if (file.getAbsoluteFile().getName().toUpperCase().endsWith(".GPX")) {
            try {
                InputStream bin_input_stream = new FileInputStream(file.getAbsoluteFile());
                DataInputStream bin_input = new DataInputStream(bin_input_stream);

                myHandler handler = new myHandler();
                handler.pointCollection = pointCollection;
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
