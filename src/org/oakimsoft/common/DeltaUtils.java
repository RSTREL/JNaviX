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
package org.oakimsoft.common;

import java.awt.Color;

/**
 * Набор разных полезных утилит
 * @author user
 */
public class DeltaUtils {

    /**
     * Преобразовать к формату #int#int#int
     * @param pColor
     * @return
     */
    public static String colorToSharpStr(Color pColor) {
        StringBuilder sb = new StringBuilder();
        sb.append("#").append(String.valueOf(pColor.getRed()));
        sb.append("#").append(String.valueOf(pColor.getGreen()));
        sb.append("#").append(String.valueOf(pColor.getBlue()));
        return sb.toString();

    }

    /**
     * Преобразовать из формата #int#int#int
     * @param pColor
     * @return
     */
    public static Color SharpStrToColor(String pColor) {
        String[] colors = pColor.substring(1, pColor.length()).split("#");
        Color color = null;
        try{
         color = new Color(Integer.parseInt(colors[0]),
                Integer.parseInt(colors[1]),
                Integer.parseInt(colors[2]));
        }catch(Exception e){
            System.err.println(pColor+" is not valid color.");
            color = new Color(255,255,255);

        }finally{
        }
        return color;

    }
}
