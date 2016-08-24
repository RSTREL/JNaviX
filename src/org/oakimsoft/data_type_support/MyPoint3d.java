/*
Class MyPoint3d
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
 * Точка трехмерного пространства
 * 
 * @author Oakim
 */
public class MyPoint3d {

	public double x = 0;
	public double y = 0;
	public double z = 0;

	private double epsilon = 0.0000005; // точность сравнений

	public MyPoint3d() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

	public MyPoint3d(Double x, Double y, Double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public MyPoint3d(MyPoint3d mfp) {
		this.x = mfp.x;
		this.y = mfp.y;
		this.z = mfp.z;
	}

	public void Assign(MyPoint3d xyz) {
		this.x = xyz.x;
		this.y = xyz.y;
		this.z = xyz.z;
	}

	public void Plus(MyPoint3d xyz) {
		this.x = this.x + xyz.x;
		this.y = this.y + xyz.y;
		this.z = this.x + xyz.z;
	}

	public void Minus(MyPoint3d xyz) {
		this.x = this.x - xyz.x;
		this.y = this.y - xyz.y;
		this.z = this.x - xyz.z;
	}

	/**
	 * Вращение вокруг оси Z
	 */
	public MyPoint3d RotateZ(Double angleRad) {
		MyPoint3d Result = new MyPoint3d(0.0, 0.0, 0.0);
		Result.x = this.x * (Math.cos(angleRad)) + this.y
				* (-Math.sin(angleRad)) + this.z * (0) + 1 * (0);
		Result.y = this.x * Math.sin(angleRad) + this.y * Math.cos(angleRad)
				+ this.z * (0) + 1 * (0);
		Result.z = this.x * (0) + this.y * (0) + this.z * (1) + 1 * (0);

		return Result;
	}

	/**
	 * Вращение вокруг оси X
	 */
	public MyPoint3d RotateX(Double angleRad) {
		MyPoint3d Result = new MyPoint3d(0.0, 0.0, 0.0);
		Result.x = this.x * (1) + this.y * (0) + this.z * (0) + 1 * (0);

		Result.y = this.x * (0) + this.y * (Math.cos(angleRad)) + this.z
				* (-Math.sin(angleRad)) + 1 * (0);

		Result.z = this.x * (0) + this.y * (Math.sin(angleRad)) + this.z
				* (Math.cos(angleRad)) + 1 * (0);
		return Result;
	}

	/**
	 * Вращение вокруг оси Y
	 */
	public MyPoint3d RotateY(Double angleRad) {
		MyPoint3d Result = new MyPoint3d(0.0, 0.0, 0.0);
		Result.x = this.x * (Math.cos(angleRad)) + this.y * (0) + this.z
				* (Math.sin(angleRad)) + 1 * (0);

		Result.y = this.x * (0) + this.y * (1) + this.z * (0) + 1 * (0);

		Result.z = this.x * (-Math.sin(angleRad)) + this.y * (0) + this.z
				* (Math.cos(angleRad)) + 1 * (0);
		return Result;
	}

	/**
	 * Длина вектора * @return
	 */
	public double getLenght() {
		return Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2));
	}

	/**
	 * Расстояние на плоскости OXY до точки pnt
	 */
	public double getDistance(MyPoint3d pnt) {
		return Math.sqrt(Math.pow(this.x - pnt.x, 2)
				+ Math.pow(this.y - pnt.y, 2));
	}

	/**
	 * Расстояние на плоскости OXY
	 */
	public double getDistance(MyPoint3d pnt1, MyPoint3d pnt2) {
		return Math.sqrt(Math.pow(pnt1.x - pnt2.x, 2)
				+ Math.pow(pnt1.y - pnt2.y, 2));
	}

	/**
	 * Параллельный перенос
	 */
	public MyPoint3d Translate(Double tx, Double ty, Double tz) {
		return new MyPoint3d(this.x + tx, this.y + ty, this.z + tz);
	}

	/**
	 * Масштабирование
	 */
	public MyPoint3d Scale(Double sx, Double sy, Double sz) {
		return new MyPoint3d(this.x * sx, this.y * sy, this.z * sz);
	}

	/**
	 * Масштабирование
	 */
	public int getXint() {
		return (int) this.x;
	}

	public int getYint() {
		return (int) this.y;
	}

	public boolean equal(MyPoint3d mfp) {
		if ((Math.abs(this.x - mfp.x) < this.epsilon)
				&& (Math.abs(this.y - mfp.y) < this.epsilon)
				&& (Math.abs(this.z - mfp.z) < this.epsilon))
			return true;
		else
			return false;
	}

	/**
	 * Расстояние от точки до прямой на плоскости
	 * 
	 * @param line0
	 *            точка прямой
	 * @param line1
	 *            точка прямой
	 * @return расстояние до прямой (положительные и отрицательные значения!!!)
	 */

	public double getDistanceToLine2D(MyPoint3d line0, MyPoint3d line1) {
		return ((line0.y - line1.y) * this.x + (line1.x - line0.x) * this.y + (line0.x
				* line1.y - line1.x * line0.y))
				/ Math.sqrt(Math.pow(line1.x - line0.x, 2)
						+ Math.pow(line1.y - line0.y, 2));
	}

	/**
	 * Расстояние от точки до отрезка на плоскости
	 * 
	 * @param line0
	 *            начало отрезка
	 * @param line1
	 *            конец отрезка
	 * @return модуль расстояния
	 */

	public double getDistanceToSegment2D(MyPoint3d line0, MyPoint3d line1) {
		double dist1 = Math.abs(this.getDistance(line0)); // расстояние до конца
															// отрезка
		double dist2 = Math.abs(this.getDistance(line1)); // расстояние до конца
															// отрезка
		double distR = Math.abs(line0.getDistance(line1)); // расстояние до
															// конца отрезка
		// расстояние до середины отрезка
		// double distM=Math.abs(this.getDistance(new
		// MyPoint3d((line0.x+line1.x)/2,(line0.y+line1.y)/2,0d))); //расстояние
		// до середины отрезка
		// расстояние до прямой
		double distL = Math.abs(getDistanceToLine2D(line0, line1));
		// Если точка лежит над отрезком
		double angle0 = Math
				.acos(((this.x - line0.x) * (line1.x - line0.x) + (this.y - line0.y)
						* (line1.y - line0.y))
						/ (dist1 * distR));
		double angle1 = Math
				.acos(((this.x - line1.x) * (line0.x - line1.x) + (this.y - line1.y)
						* (line0.y - line1.y))
						/ (dist2 * distR));
		if ((angle0 <= Math.PI / 2) && (angle1 <= Math.PI / 2)) {
			return Math.abs(distL);
		} else {
			return Math.min(dist1, dist2);
		}
	}

}
