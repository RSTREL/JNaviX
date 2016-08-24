/*Class CTrackManager - manager of loaded GPS tracks
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
package org.oakimsoft.tracking;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javax.swing.table.DefaultTableModel;

import org.oakimsoft.common.GPXParser;
import org.oakimsoft.data_type_support.CGPGGA;

import com.bbn.openmap.LatLonPoint;
import com.bbn.openmap.proj.RhumbCalculator;

/**
 * Управление треками.
 * 
 * @author user
 */
public class CTrackManager {

	public ArrayList<CTrackRecord> alTrackList; // Список треков
	public ArrayList<CTrackPoint> alTrackData; // Считанный NMEA file
	protected int nextTrackNumber = 1; // Номер следующего трека
	public DefaultTableModel dtmTracks = new DefaultTableModel() {
		private static final long serialVersionUID = 1L;

		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};

	public class CTrackRecord {

		public int TrackNumber = 0; // Номер трека
		long startSecond = 0; // Время начала
		long finishSecond = 0; // Время конца трека
		long count = 0; // колво точек

		boolean visible = true; // отображение трека на карте

		public void clear() {
			this.TrackNumber = 0;
			startSecond = 0;
			finishSecond = 0;
			visible = true;
			count = 0;
		}
	};

	public CTrackManager() {
		String[] headings = { "№", "Начало", "Завершение", "Виден" };
		this.dtmTracks.setColumnIdentifiers(headings);
		this.alTrackData = new ArrayList<CTrackPoint>(10);
		this.alTrackList = new ArrayList<CTrackRecord>(10);
	}

	public int load(File file) {
		int currentTrackNumber = this.nextTrackNumber;
		this.nextTrackNumber++;

		CTrackRecord ctr = new CTrackRecord();
		ctr.TrackNumber = currentTrackNumber;
		ctr.visible = true;

		// Загрузка NM файла
		if (file.getAbsoluteFile().getName().toUpperCase().endsWith(".NM")) {
			try {
				BufferedReader str_input;
				str_input = new BufferedReader(new InputStreamReader(new FileInputStream(file.getAbsoluteFile()), "Cp1251"));
				String Line = null;

				while ((Line = str_input.readLine()) != null) {
					try {
						if (Line.trim().startsWith("$GPGGA")) {
							CGPGGA gga = new CGPGGA();
							if (gga.parseLine(Line) == 0) {
								CTrackPoint ctp = new CTrackPoint();
								ctp.TrackNumber = ctr.TrackNumber;
								ctp.gpgga.Assign(gga);
								if (ctr.startSecond == 0) {
									ctr.startSecond = gga.UTCMills;
								}
								if (ctr.finishSecond == 0) {
									ctr.finishSecond = gga.UTCMills;
								}
								if (ctr.startSecond > gga.UTCMills) {
									ctr.startSecond = gga.UTCMills;
								}
								if (ctr.finishSecond < gga.UTCMills) {
									ctr.finishSecond = gga.UTCMills;
								}
								this.alTrackData.add(ctp);
							}
						}
					} catch (Exception e) {
						System.out.print("MapDataManager: Problem in NMEA reading:" + e.getMessage());
					}
				}
				str_input.close();
				System.out.println("MapDataManager: NMEA is loaded.");

			} catch (IOException e) {
				System.out.println("MapDataManager:Problems with NMEA:");
				System.out.println(e.getMessage());
			} finally {
			}
		}

		if (file.getAbsoluteFile().getName().toUpperCase().endsWith(".GPX")) {
			ArrayList alPoints = new ArrayList(10);
			GPXParser gpxparser = new GPXParser(alPoints, file);
			gpxparser.pocessData();
			Iterator it = alPoints.iterator();
			CGPGGA gga;
			CGPGGA prevGga = null;
			while (it.hasNext()) {
				gga = (CGPGGA) it.next();
				if (gga.Longitude == 0.0) {
					if (ctr.count > 0)
						this.alTrackList.add(ctr);
					ctr = null;
					ctr = new CTrackRecord();
					currentTrackNumber = this.nextTrackNumber;
					this.nextTrackNumber++;
					ctr.TrackNumber = currentTrackNumber;
					ctr.visible = true;
					continue;
				}
				CTrackPoint ctp = new CTrackPoint();
				ctp.TrackNumber = ctr.TrackNumber;
				// ctp.gpgga = new CGPGGA();
				ctp.gpgga.Assign(gga);
				if (ctr.startSecond == 0) {
					ctr.startSecond = gga.UTCMills;
				}
				if (ctr.finishSecond == 0) {
					ctr.finishSecond = gga.UTCMills;
				}
				if (ctr.startSecond > gga.UTCMills) {
					ctr.startSecond = gga.UTCMills;
				}
				if (ctr.finishSecond < gga.UTCMills) {
					ctr.finishSecond = gga.UTCMills;
				}
				ctr.count++;
				if (prevGga != null) {
					ctp.gpgga.timeFromPrev = (gga.UTCMills - prevGga.UTCMills) / 1000.0;
					ctp.gpgga.distFromPrev = (double) RhumbCalculator.getDistanceBetweenPoints(new LatLonPoint(gga.Latitude, gga.Longitude), new LatLonPoint(prevGga.Latitude, prevGga.Longitude));
				}

				this.alTrackData.add(ctp);
				prevGga = gga;

			}
		}
		if (ctr.count > 0)
			this.alTrackList.add(ctr);
		else
			--this.nextTrackNumber;
		return currentTrackNumber;
	}

	public void remove(int TrackNumber) {
		Iterator it = this.alTrackData.iterator();
		while (it.hasNext()) {
			CTrackPoint ctp = (CTrackPoint) it.next();
			if (ctp.TrackNumber == TrackNumber) {
				it.remove();
			}
		}
		it = null;
		it = this.alTrackList.iterator();
		while (it.hasNext()) {
			CTrackRecord ctr = (CTrackRecord) it.next();
			if (ctr.TrackNumber == TrackNumber) {
				it.remove();
			}
		}
		this.refreshTableModelValues();

	}

	public void setVisible(int TrackNumber, boolean bVisible) {
		Iterator<CTrackRecord> it = this.alTrackList.iterator();
		while (it.hasNext()) {
			CTrackRecord ctr = (CTrackRecord) it.next();
			if (ctr.TrackNumber == TrackNumber) {
				ctr.visible = bVisible;
			}
		}
		this.refreshTableModelValues();
	}

	public void initTableModel() {
		this.dtmTracks.setNumRows(0);
	}

	public void refreshTableModelValues() {
		this.dtmTracks.setRowCount(0);
		Iterator<CTrackRecord> it = this.alTrackList.iterator();
		while (it.hasNext()) {
			CTrackRecord ctr = (CTrackRecord) it.next();
			Object[] values = new Object[4];
			DateFormat dateFormat = new SimpleDateFormat("yyyy'-'MM'-'dd' 'HH':'mm':'ss' '");
			Date dt = new Date(ctr.startSecond);
			Date dt2 = new Date(ctr.finishSecond);

			values[0] = String.valueOf(ctr.TrackNumber);
			values[1] = dateFormat.format(dt);
			values[2] = dateFormat.format(dt2);
			if (ctr.visible) {
				values[3] = "Да";
			} else {
				values[3] = "---";
			}
			this.dtmTracks.addRow(values);
		}
	}

	public void clear() {
		this.alTrackList.clear();
		this.alTrackData.clear();
		this.dtmTracks.setRowCount(0);

	}

}
