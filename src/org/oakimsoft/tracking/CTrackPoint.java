/*
 *  Copyright (C) 2010 user
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.oakimsoft.tracking;

import org.oakimsoft.data_type_support.CGPGGA;

/**
 *
 * @author user
 */
    public class CTrackPoint {

        public int TrackNumber = 0; // Номер трека
        public CGPGGA gpgga;
        public CTrackPoint( ){
            this.TrackNumber =  0;
            this.gpgga = new CGPGGA();
        }

        public void Assign(CTrackPoint ctp){
            this.TrackNumber=ctp.TrackNumber;
            this.gpgga.Assign(ctp.gpgga);
        }

    };