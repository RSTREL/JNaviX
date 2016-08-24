    /*Class GeomUtil - some geometric utilities
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
package org.oakimsoft.common;


import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import org.oakimsoft.data_type_support.MyLine;
import org.oakimsoft.data_type_support.MyPoint3d;

/**
 * Набор утилит для геометрических преобразований
 * @author user
 */
public class GeomUtils {

    static private double epsilon = 0.000000005; // точность сравнений

    /**
     * Расстояние на плоскости
     */
    static public double getDistance(double pnt1x, double pnt1y, double pnt2x, double pnt2y) {
        return Math.sqrt(Math.pow(pnt1x - pnt2x, 2) + Math.pow(pnt1y - pnt2y, 2));
    }

    /**
     * Проверка расстояния от точки x,y до отрезка на плоскости
     */
    static public boolean checkDistanceToLine(double x, double y, double lx1, double ly1, double lx2, double ly2, double Radius) {
        boolean Result = false;

        if (getDistance(x, y, lx1, ly1) <= Radius) {
            return true;
        }
        if (getDistance(x, y, lx2, ly2) <= Radius) {
            return true;
        }

        double a1, b1, c1, a, b, R, k, z, x1, x2, y1, y2, ch;
        if ((lx1 == lx2) && (lx2 == ly1) && (ly1 == ly2)) {
            return Result;
        }
        //коэффициенты уравнения окружности в центре a;b и радиусом R
        a = x;
        b = y;
        R = Radius;
        if (lx1 == lx2) {
            z = lx1;
            a1 = 1;
            b1 = -2 * y;
            c1 = b * b + Math.pow(z - a, 2) - R * R;

            if ((b1 * b1 - 4 * a1 * c1) < 0) {
                return Result;
            }


            y1 = (-b1 + Math.sqrt(b1 * b1 - 4 * a1 * c1)) / (2 * a1);

            y2 = (-b1 - Math.sqrt(b1 * b1 - 4 * a1 * c1)) / (2 * a1);
            x1 = lx1;
            x2 = lx2;
        } else {
            //коэффициенты уравнения прямой чеерз две точки
            k = (ly2 - ly1) / (lx2 - lx1);
            z = ly2 - lx2 * k;
            a1 = 1 + k * k;
            b1 = 2 * k * z - 2 * a - 2 * k * b;
            c1 = a * a + z * z - 2 * b * z + b * b - R * R;

            if ((b1 * b1 - 4 * a1 * c1) < 0) {
                return Result;
            }

            x1 = (-b1 + Math.sqrt(b1 * b1 - 4 * a1 * c1)) / (2 * a1);
            x2 = (-b1 - Math.sqrt(b1 * b1 - 4 * a1 * c1)) / (2 * a1);
            y1 = k * x1 + z;
            y2 = k * x2 + z;

        }
        if (lx1 > lx2) {
            ch = lx1;
            lx1 = lx2;
            lx2 = ch;
        }
        if (ly1 > ly2) {
            ch = ly1;
            ly1 = ly2;
            ly2 = ch;
        }
        if ((lx1 <= x1) && (x1 <= lx2) && (ly1 <= y1) && (y1 <= ly2)) {
            Result = true;
        }
        if ((lx1 <= x2) && (x2 <= lx2) && (ly1 <= y2) && (y2 <= ly2)) {
            Result = true;
        }
        return Result;
    }

    /**
     *
     * Лежит ли точка на отрезке
     * @param a начало отрезка
     * @param b конец отрезка
     * @param p точка
     * @return истина-ложь
     */
    static public boolean isPointAtSegm(MyPoint3d a, MyPoint3d b, MyPoint3d p) {
        return Math.abs(getDistance(a.x, a.y, b.x, b.y) - getDistance(a.x, a.y, p.x, p.y) - getDistance(p.x, p.y, b.x, b.y)) < epsilon;
    }

    /**
     * принадлежит ли точка полигону
     * @param TestPolygon массив с точками - вершинами полигона
     * @param p исследуемая точка
     * @param TestRib проверять ли ребра
     * @return
     */
    static public boolean isPointInPolygon(MyPoint3d[] TestPolygon, MyPoint3d p, boolean TestRib) {
        boolean result = false;
        byte ToTheLeftofPoint, ToTheRightofPoint;
        int np;
        boolean OpenPolygon;
        double XIntersection;
        ToTheLeftofPoint = 0;
        ToTheRightofPoint = 0;
        OpenPolygon = false;

        /*{Prufen ob das Polygon geschlossen ist}
        {tests if the polygon is closed}*/

        if (!((TestPolygon[0].x == TestPolygon[TestPolygon.length - 1].x) &&
                (TestPolygon[0].y == TestPolygon[TestPolygon.length-1].y))) {
            OpenPolygon = true;
        }


        /*
        {Tests fur jedes Paar der Punkte, um zu sehen wenn die Seite zwischen
        ihnen, die horizontale Linie schneidet, die TestPoint durchlauft}
        {tests for each couple of points to see if the side between them
        crosses the horizontal line going through TestPoint}*/



        if (TestRib) {
            //если исследуемая точка совпадает с точкой многоугольника
            for (np = 0; np < TestPolygon.length-1; np++) {
                if (TestPolygon[np].equal(p)) {
                    result = true;
                    return result;
                }

                //если исследуемая точка лежит на ребре
                for (np = 0; np < TestPolygon.length - 2; np++) {
                    if (isPointAtSegm(TestPolygon[np], TestPolygon[np + 1], p)) {
                        return true;
                    }
                }
                if (isPointAtSegm(TestPolygon[np + 1], TestPolygon[0], p)) {
                    return true;
                }
            }
        }
//
//
        for (np = 1; np < TestPolygon.length-1; np++) {

            if (((TestPolygon[np - 1].y <= p.y) &&
                    (TestPolygon[np].y > p.y)) ||
                    ((TestPolygon[np - 1].y > p.y) &&
                    (TestPolygon[np].y <= p.y))) {
                //    {berechnet die x Koordinate des Schnitts}
                //    {computes the x coordinate of the intersection}
                XIntersection = TestPolygon[np - 1].x + ((TestPolygon[np].x - TestPolygon[np - 1].x) /
                        (TestPolygon[np].y - TestPolygon[np - 1].y)) * (p.y - TestPolygon[np - 1].y);
                if (XIntersection < p.x) {
                    ToTheLeftofPoint++;
                }
                if (XIntersection > p.x) {
                    ToTheRightofPoint++;
                }
            }
            //    {if the polygon is open, test for the last side}
        }
        if (OpenPolygon) {
            np = TestPolygon.length-1; // {Thanks to William Boyd - 03/06/2001}
            if (((TestPolygon[np].y <= p.y) &&
                    (TestPolygon[0].y > p.y)) ||
                    ((TestPolygon[np].y > p.y) &&
                    (TestPolygon[0].y <= p.y))) {
                XIntersection = TestPolygon[np].x +
                        ((TestPolygon[0].x - TestPolygon[np].x) /
                        (TestPolygon[0].y - TestPolygon[np].y)) * (p.y - TestPolygon[np].y);

                if (XIntersection < p.x) {
                    ToTheLeftofPoint++;
                }
                if (XIntersection > p.x) {
                    ToTheRightofPoint++;
                }
            }
        }

        if ((ToTheLeftofPoint % 2 == 1) && (ToTheRightofPoint % 2 == 1)) {
            return true;
        }
        return false;

    }

    /**
     * Расстояние от точки до прямой на плоскости
     * @param point точка на плоскости
     * @param line0 точка прямой
     * @param line1 точка прямой
     * @return расстояние до прямой
     */
    static public double getDistanceToLine(MyPoint3d point, MyPoint3d line0, MyPoint3d line1) {
        return ((line0.y - line1.y) * point.x + (line1.x - line0.x) * point.y + (line0.x * line1.y - line1.x * line0.y)) / Math.sqrt(Math.pow(line1.x - line0.x, 2) + Math.pow(line1.y - line0.y, 2));
    }

    /**
     * Расстояние от точки до отрезка на плоскости
     * @param point точка
     * @param line0 начало отрезка
     * @param line1 конец отрезка
     * @return
     */
    static public double getDistanceToSegment(MyPoint3d point, MyPoint3d line0, MyPoint3d line1) {
        double dist1 = point.getDistance(line0); //расстояние до конца отрезка
        double dist2 = point.getDistance(line1); //расстояние до конца отрезка
        //расстояние до середины отрезка
        double distM = point.getDistance(new MyPoint3d((line0.x + line1.x) / 2, (line0.y + line1.y) / 2, 0d)); //расстояние до середины отрезка
        // расстояние до прямой
        double distL = getDistanceToLine(point, line0, line1);
        //Если точка лежит над отрезком
        if ((dist1 >= distM) && (dist2 >= distM)) {
            return distL;
        } else {
            return Math.min(dist1, dist2);
        }
    }

    /**
     * Проверка на пересечение двух прямых
     * @param ml1
     * @param ml2
     * @return true/false
     */
    static public boolean isCrossing(MyLine ml1, MyLine ml2) {
        return (ml1.A * ml2.B - ml2.A * ml1.B != 0);
    }

    /**
     * получить точку пересечения прямых
     * @param ml1
     * @param ml2
     * @param crossPoint сюдабудет помещен результат пересечения
     * @return true - есть точка пересечения false  - нет токи пересечения
     */
    static public boolean getLineCross(MyLine ml1, MyLine ml2, MyPoint3d crossPoint){
        if (!isCrossing(ml1, ml2))
            return false;
        else{
            crossPoint.x=(ml1.B*ml2.C-ml2.B*ml1.C)/(ml1.A * ml2.B - ml2.A * ml1.B);
            crossPoint.y=(ml1.C*ml2.A-ml2.C*ml1.A)/(ml1.A * ml2.B - ml2.A * ml1.B);
            crossPoint.z=0;
            return true;
        }
    }

    
    static public Vector3f getNormal(MyPoint3d fp1,MyPoint3d fp2, MyPoint3d fp3 ){
        MyPoint3d v1 = new  MyPoint3d(fp1.x - fp2.x,fp1.y - fp2.y,fp1.z - fp2.z);
        MyPoint3d v2 = new  MyPoint3d(fp2.x - fp3.x,fp2.y - fp3.y,fp2.z - fp3.z);
        MyPoint3d nx  = new  MyPoint3d(
                v1.y * v2.z - v1.z * v2.y,
                v1.z * v2.x - v1.x * v2.z,
                v1.x * v2.y - v1.y * v2.x   );
        if (nx.getLenght()!=0){
            nx.Assign(nx.Scale(1/nx.getLenght(), 1/nx.getLenght(), 1/nx.getLenght()));
        }
        return new Vector3f((float)nx.x,(float)nx.y,(float)nx.z );
       
    }

        static public Vector3f getNormal(Point3d fp1,Point3d fp2, Point3d fp3 ){
        MyPoint3d v1 = new  MyPoint3d(fp1.x - fp2.x,fp1.y - fp2.y,fp1.z - fp2.z);
        MyPoint3d v2 = new  MyPoint3d(fp2.x - fp3.x,fp2.y - fp3.y,fp2.z - fp3.z);
        MyPoint3d nx  = new  MyPoint3d(
                v1.y * v2.z - v1.z * v2.y,
                v1.z * v2.x - v1.x * v2.z,
                v1.x * v2.y - v1.y * v2.x   );
        if (nx.getLenght()!=0){
            nx.Assign(nx.Scale(1/nx.getLenght(), 1/nx.getLenght(), 1/nx.getLenght()));
        }
        return new Vector3f((float)nx.x,(float)nx.y,(float)nx.z );

    }

    static public double[] getParabolic(MyPoint3d fp1,MyPoint3d fp2, MyPoint3d fp3){
        double x1,x2,x3,y1,y2,y3;
        x1=fp1.x;
        x2=fp2.x;
        x3=fp3.x;
        y1=fp1.y;
        y2=fp2.y;
        y3=fp3.y;

        double[] result = new double[3];
        result[0] =
                (y3-(x3*(y2-y1)+x2*y1-x1*y2)/(x2-x1))
                /
                (x3*(x3-x1-x2)+x1*x2)
                ;
        result[1] = (y2-y1)/(x2-x1)-result[0]*(x1+x2);
        result[2] = (x2*y1-x1*y2)/(x2-x1)+result[0]*x1*x2;
        return result;
    }


    
    

}
