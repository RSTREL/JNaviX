// **********************************************************************
//
//<copyright>
//
//BBN Technologies, a Verizon Company
//10 Moulton Street
//Cambridge, MA 02138
//(617) 873-8000
//
//Copyright (C) BBNT Solutions LLC. All rights reserved.
//
//</copyright>
//**********************************************************************
//
//$Source:
///cvs/darwars/ambush/aar/src/com/bbn/ambush/mission/MissionHandler.java,v
//$
//$RCSfile: GIFSymbolImageMaker.java,v $
//$Revision: 1.1.2.4 $
//$Date: 2005/02/11 22:51:27 $
//$Author: dietrick $
//
//**********************************************************************

package com.bbn.openmap.tools.symbology.milStd2525;


public class GIFSymbolImageMaker extends BasicSymbolImageMaker {

    /**
     *  
     */
    public GIFSymbolImageMaker() {}

    public GIFSymbolImageMaker(String dataPath) {
        super(dataPath);
    }

    public String getFileExtension() {
        return ".gif";
    }
    
}