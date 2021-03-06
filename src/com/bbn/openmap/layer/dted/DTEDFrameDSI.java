// **********************************************************************
// 
// <copyright>
// 
//  BBN Technologies
//  10 Moulton Street
//  Cambridge, MA 02138
//  (617) 873-8000
// 
//  Copyright (C) BBNT Solutions LLC. All rights reserved.
// 
// </copyright>
// **********************************************************************
// 
// $Source:
// /cvs/distapps/openmap/src/openmap/com/bbn/openmap/layer/dted/DTEDFrameDSI.java,v
// $
// $RCSfile: DTEDFrameDSI.java,v $
// $Revision: 1.2.2.1 $
// $Date: 2004/10/14 18:27:04 $
// $Author: dietrick $
// 
// **********************************************************************

package com.bbn.openmap.layer.dted;

import java.io.IOException;

import com.bbn.openmap.io.BinaryBufferedFile;
import com.bbn.openmap.io.BinaryFile;
import com.bbn.openmap.io.FormatException;
import com.bbn.openmap.util.Debug;

public class DTEDFrameDSI {

    //DSI fields in order of appearance - filler has been left out.
    public char sec_code;
    public String sec_rel;
    public String sec_handling;
    public String sec_res;
    public String series;
    public String ser_ref_num;
    public String ser_res;
    public int data_ed;
    public char merge_version;
    public String maint_date;
    public String merge_date;
    public String maint_desc;
    public String prod_code;
    public String prod_res;
    public String spec;
    public int spec_amen;
    public String product_date;
    public String vert_datum;
    public String horiz_datum;
    public String dc_system;
    public String compilation_date;
    public String date_res;
    public float lat_origin;
    public float lon_origin;
    public float sw_lat;
    public float sw_lon;
    public float nw_lat;
    public float nw_lon;
    public float ne_lat;
    public float ne_lon;
    public float se_lat;
    public float se_lon;
    public float orient_ang;
    public int lat_post_interval;
    public int lon_post_interval;
    public int num_lat_lines;
    public int num_lon_points;
    public int part_cell;

    public DTEDFrameDSI(BinaryFile binFile) {

        try {
            binFile.seek(DTEDFrame.UHL_SIZE);

            //  For now, this is not error checking, just marking space
            binFile.skipBytes(3);

            sec_code = binFile.readChar();
            sec_rel = binFile.readFixedLengthString(2);
            sec_handling = binFile.readFixedLengthString(27);
            sec_res = binFile.readFixedLengthString(26);
            series = binFile.readFixedLengthString(5);
            ser_ref_num = binFile.readFixedLengthString(15);
            ser_res = binFile.readFixedLengthString(8);
            String test;
            //          data_ed =
            // Integer.parseInt(binFile.readFixedLengthString(2), 10);
            try {
                test = binFile.readFixedLengthString(2);
                data_ed = Integer.parseInt(test, 10);
            } catch (NumberFormatException nfe) {
                Debug.message("dted",
                        "DTEDFrameDSI: Data Edition number bad, using 0");
                data_ed = 0;
            }

            merge_version = binFile.readChar();
            maint_date = binFile.readFixedLengthString(4);
            merge_date = binFile.readFixedLengthString(4);
            maint_desc = binFile.readFixedLengthString(4);
            prod_code = binFile.readFixedLengthString(8);
            prod_res = binFile.readFixedLengthString(16);
            spec = binFile.readFixedLengthString(9);

            //              spec_amen =
            // Integer.parseInt(binFile.readFixedLengthString(2), 10);
            try {
                test = binFile.readFixedLengthString(2);
                spec_amen = Integer.parseInt(test, 10);
            } catch (NumberFormatException nfe) {
                Debug.message("dted",
                        "DTEDFrameDSI: Spec Amednment number bad, using 0");
                spec_amen = 0;
            }

            product_date = binFile.readFixedLengthString(4);
            vert_datum = binFile.readFixedLengthString(3);
            horiz_datum = binFile.readFixedLengthString(5);
            dc_system = binFile.readFixedLengthString(10);
            compilation_date = binFile.readFixedLengthString(4);
            date_res = binFile.readFixedLengthString(22);
            lat_origin = DTEDFrameUtil.stringToLat(binFile.readFixedLengthString(9));
            lon_origin = DTEDFrameUtil.stringToLon(binFile.readFixedLengthString(10));
            sw_lat = DTEDFrameUtil.stringToLat(binFile.readFixedLengthString(7));
            sw_lon = DTEDFrameUtil.stringToLon(binFile.readFixedLengthString(8));
            nw_lat = DTEDFrameUtil.stringToLat(binFile.readFixedLengthString(7));
            nw_lon = DTEDFrameUtil.stringToLon(binFile.readFixedLengthString(8));
            ne_lat = DTEDFrameUtil.stringToLat(binFile.readFixedLengthString(7));
            ne_lon = DTEDFrameUtil.stringToLon(binFile.readFixedLengthString(8));
            se_lat = DTEDFrameUtil.stringToLat(binFile.readFixedLengthString(7));
            se_lon = DTEDFrameUtil.stringToLon(binFile.readFixedLengthString(8));

            //              orient_ang =
            // Float.valueOf(binFile.readFixedLengthString(9)).floatValue();
            try {
                test = binFile.readFixedLengthString(9);
                orient_ang = Float.valueOf(test).floatValue();
            } catch (NumberFormatException nfe) {
                Debug.message("dted",
                        "DTEDFrameDSI: orient angle number bad, using 0");
                orient_ang = 0f;
            }

            lat_post_interval = Integer.parseInt(binFile.readFixedLengthString(4),
                    10);
            lon_post_interval = Integer.parseInt(binFile.readFixedLengthString(4),
                    10);
            num_lat_lines = Integer.parseInt(binFile.readFixedLengthString(4),
                    10);
            num_lon_points = Integer.parseInt(binFile.readFixedLengthString(4),
                    10);

            //              part_cell =
            // Integer.parseInt(binFile.readFixedLengthString(2), 10);
            try {
                test = binFile.readFixedLengthString(2);
                part_cell = Integer.parseInt(test, 10);
            } catch (NumberFormatException nfe) {
                Debug.message("dted",
                        "DTEDFrameDSI: partial cell number bad, using 0");
                part_cell = 0;
            }
        } catch (IOException e) {
            Debug.error("DTEDFrameDSI: File IO Error!\n" + e.toString());
        } catch (FormatException f) {
            Debug.error("DTEDFrameDSI: File IO Format error!\n" + f.toString());
        } catch (NumberFormatException nfe) {
            // If we catch a number format exception here, too bad.
            // The producer should have filled in those values, since
            // they reflect the location of the frame and should be
            // filled in.
            Debug.error("DTEDFrameDSI: Number format error!\n" + nfe.toString());
        }
    }

    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append("***DSI***" + "\n");
        s.append("  sec_code: " + sec_code);
        s.append("  sec_rel: " + sec_rel + "\n");
        s.append("  sec_handling: " + sec_handling + "\n");
        s.append("  sec_res: " + sec_res + "\n");
        s.append("  series: " + series + "\n");
        s.append("  ser_ref_num: " + ser_ref_num + "\n");
        s.append("  ser_res: " + ser_res + "\n");
        s.append("  data_ed: " + data_ed + "\n");
        s.append("  merge_version: " + merge_version + "\n");
        s.append("  maint_date: " + maint_date + "\n");
        s.append("  merge_date: " + merge_date + "\n");
        s.append("  maint_desc: " + maint_desc + "\n");
        s.append("  prod_code: " + prod_code + "\n");
        s.append("  prod_res: " + prod_res + "\n");
        s.append("  spec: " + spec + "\n");
        s.append("  spec_amen: " + spec_amen + "\n");
        s.append("  product_date: " + product_date + "\n");
        s.append("  vert_datum: " + vert_datum + "\n");
        s.append("  horiz_datum: " + horiz_datum + "\n");
        s.append("  dc_system: " + dc_system + "\n");
        s.append("  compilation_date: " + compilation_date + "\n");
        s.append("  date_res: " + date_res + "\n");
        s.append("  lat_origin: " + lat_origin + "\n");
        s.append("  lon_origin: " + lon_origin + "\n");
        s.append("  sw_lat: " + sw_lat + "\n");
        s.append("  sw_lon: " + sw_lon + "\n");
        s.append("  nw_lat: " + nw_lat + "\n");
        s.append("  nw_lon: " + nw_lon + "\n");
        s.append("  ne_lat: " + ne_lat + "\n");
        s.append("  ne_lon: " + ne_lon + "\n");
        s.append("  se_lat: " + se_lat + "\n");
        s.append("  se_lon: " + se_lon + "\n");
        s.append("  orient_ang: " + orient_ang + "\n");
        s.append("  lat_post_interval: " + lat_post_interval + "\n");
        s.append("  lon_post_interval: " + lon_post_interval + "\n");
        s.append("  num_lat_lines: " + num_lat_lines + "\n");
        s.append("  num_lon_points: " + num_lon_points + "\n");
        s.append("  part_cell: " + part_cell + "\n");
        return s.toString();
    }

    public static void main(String args[]) {
        Debug.init();
        if (args.length < 1) {
            Debug.output("dtedframe_dsi:  Need a path/filename");
            System.exit(0);
        }

        Debug.output("DTEDFrameDSI: using frame " + args[0]);

        java.io.File file = new java.io.File(args[0]);

        try {

            BinaryFile binFile = new BinaryBufferedFile(file);
            //          BinaryFile binFile = new BinaryFile(file);
            DTEDFrameDSI dfd = new DTEDFrameDSI(binFile);
            Debug.output(dfd.toString());

        } catch (java.io.FileNotFoundException e) {
            Debug.error("DTEDFrameDSI: file " + args[0] + " not found");
            System.exit(-1);
        } catch (IOException e) {
            Debug.error("DTEDFrameDSI: File IO Error!\n" + e.toString());
        }
    }
}

