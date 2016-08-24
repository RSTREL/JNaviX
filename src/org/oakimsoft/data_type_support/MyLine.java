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

package org.oakimsoft.data_type_support;

/**
 * Класс линия на плоскости
 * 
 * @author Oakim
 */
public class MyLine {
	public double A = 0; // Параметр А
	public double B = 0; // Параметр B
	public double C = 0; // Параметр C

	/**
	 * Создани пустой линии
	 */

	public MyLine() {
		A = 0d;
		B = 0d;
		C = 0d;
	}

	/**
	 * Создание линии по двум точкам
	 * 
	 * @param fp1
	 * @param fp2
	 */
	public MyLine(MyPoint3d fp1, MyPoint3d fp2) {
		A = fp1.y - fp2.y;
		B = fp2.x - fp1.x;
		C = -(fp1.x * A + fp1.y * B);
	}

}
