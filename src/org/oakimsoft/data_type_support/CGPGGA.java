/*
Class GPGGA -  GPGGA support for GPS receiver
Copyright (C) 2009  Oakim

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package org.oakimsoft.data_type_support;

/**
 * 
 * @author Oakim
 */
public class CGPGGA {

	public int TrackNumber; // Номер трека в списке
	public String UTCTime;
	public long UTCMills = 0;

	public int seconds;
	public Double Latitude;
	public String LatitudeT;
	public Double Longitude;
	public String LongitudeT;
	public Integer FixQual;
	public Integer SateliteNumb;
	public Double HorDilution;
	public Double Altitude;
	public String AltUnit;
	public Double HgtGeoid;
	public String HgtGeoidUnit;
	public String Empty1;
	public String Empty2;
	public String CheckSum;
	public Double timeFromPrev; // seconds
	public Double distFromPrev; // meters per second

	public CGPGGA() {
		this.UTCMills = 0;
		this.UTCTime = "";
		this.seconds = 0;
		this.Latitude = new Double(0.0);
		this.LatitudeT = "";
		this.Longitude = new Double(0.0);
		this.LongitudeT = "";
		this.FixQual = new Integer(0);
		this.SateliteNumb = new Integer(0);
		this.HorDilution = new Double(0.0);
		this.Altitude = new Double(0.0);
		this.AltUnit = "";
		this.HgtGeoid = new Double(0.0);
		this.HgtGeoidUnit = "";
		this.Empty1 = "";
		this.Empty2 = "";
		this.CheckSum = "";
		this.timeFromPrev = 0d;
		this.distFromPrev = 0d;
	}

	public void clear() {
		this.UTCMills = 0;
		this.UTCTime = "";
		this.seconds = 0;
		this.Latitude = new Double(0.0);
		this.LatitudeT = "";
		this.Longitude = new Double(0.0);
		this.LongitudeT = "";
		this.FixQual = new Integer(0);
		this.SateliteNumb = new Integer(0);
		this.HorDilution = new Double(0.0);
		this.Altitude = new Double(0.0);
		this.AltUnit = "";
		this.HgtGeoid = new Double(0.0);
		this.HgtGeoidUnit = "";
		this.Empty1 = "";
		this.Empty2 = "";
		this.CheckSum = "";
		this.timeFromPrev = 0d;
		this.distFromPrev = 0d;
	}

	/**
	 * Construcor. Parses the input string from NMEA file
	 * 
	 */
	public int parseLine(String filerow) {
		int result = 0;
		this.clear();

		String[] parts = filerow.split(",");
		if (parts.length != 15) {
			System.out.println("GPGGA: Wrong quantity " + filerow);
			result = 1;
			return result;
		}
		if (!parts[0].equals("$GPGGA")) {
			System.out.println("GPGGA: Did not found prefix $GPGGA");
			result = 2;
			return result;
		}
		if (parts[6].equals("0")) {
			// / System.out.println("GPGGA: 0000000000000");
			result = 3;
			return result;
		}

		try {

			this.UTCTime = parts[1];
			this.seconds = Integer.parseInt(this.UTCTime.substring(0, 2))
					* 3600 + Integer.parseInt(this.UTCTime.substring(2, 4))
					* 60 + Integer.parseInt(this.UTCTime.substring(4, 6));
			this.Latitude = Double.parseDouble(parts[2]) / 100;
			this.Latitude = Math.round(this.Latitude)
					+ (this.Latitude - Math.round(this.Latitude)) * 100 / 60;

			this.LatitudeT = parts[3];
			this.Longitude = Double.parseDouble(parts[4]) / 100;
			this.Longitude = Math.round(this.Longitude)
					+ (this.Longitude - Math.round(this.Longitude)) * 100 / 60;
			this.LongitudeT = parts[5];
			this.FixQual = Integer.parseInt(parts[6]);
			this.SateliteNumb = Integer.parseInt(parts[7]);
			this.HorDilution = Double.parseDouble(parts[8]);
			this.Altitude = Double.parseDouble(parts[9]);
			this.AltUnit = parts[10];
			this.HgtGeoid = Double.parseDouble(parts[11]);
			this.HgtGeoidUnit = parts[12];
			// this.Empty1 = parts[13];
			// this.Empty2 = parts[14];
			this.CheckSum = parts[14];
		} catch (Exception e) {
			System.out.println("GPGGA parsing problem: " + filerow);
			System.out.println(e.toString());
		}
		return 0;
	}

	public void Assign(CGPGGA p_gpgga) {
		this.UTCTime = p_gpgga.UTCTime;
		this.UTCMills = p_gpgga.UTCMills;
		this.seconds = p_gpgga.seconds;
		this.Latitude = p_gpgga.Latitude;
		this.LatitudeT = p_gpgga.LatitudeT;
		this.Longitude = p_gpgga.Longitude;
		this.LongitudeT = p_gpgga.LongitudeT;
		this.FixQual = p_gpgga.FixQual;
		this.SateliteNumb = p_gpgga.SateliteNumb;
		this.HorDilution = p_gpgga.HorDilution;
		this.Altitude = p_gpgga.Altitude;
		this.AltUnit = p_gpgga.AltUnit;
		this.HgtGeoid = p_gpgga.HgtGeoid;
		this.HgtGeoidUnit = p_gpgga.HgtGeoidUnit;
		this.Empty1 = p_gpgga.Empty1;
		this.Empty2 = p_gpgga.Empty2;
		this.CheckSum = p_gpgga.CheckSum;
		this.timeFromPrev = p_gpgga.timeFromPrev;
		this.distFromPrev = p_gpgga.distFromPrev;
	}
}
