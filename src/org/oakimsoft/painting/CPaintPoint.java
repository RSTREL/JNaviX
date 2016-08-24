/**
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




package org.oakimsoft.painting;
import java.awt.Color;

import org.oakimsoft.data_type_support.MyPoint3d;

import com.bbn.openmap.LatLonPoint;

/**
 *
 * @author Oakim
 */
    // точка рисования
    public class CPaintPoint {

        public int recType = 0; // типа записи 0 линия 1 -Заметка
        public LatLonPoint point;
        public Color color;
        public boolean endLine;
        public String notetext;


        public CPaintPoint(){
            point=new LatLonPoint(0d,0d);
            endLine = false;
        }
       

        public void clear() {
            point.setLatLon(0d,0d);
            endLine = false;
            notetext = null;

        }
    }
