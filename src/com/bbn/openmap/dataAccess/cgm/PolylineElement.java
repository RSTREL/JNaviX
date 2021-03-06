/*
 * <copyright> Copyright 1997-2003 BBNT Solutions, LLC under
 * sponsorship of the Defense Advanced Research Projects Agency
 * (DARPA).
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the Cougaar Open Source License as
 * published by DARPA on the Cougaar Open Source Website
 * (www.cougaar.org).
 * 
 * THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
 * PROVIDED 'AS IS' WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
 * IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
 * ANY WARRANTIES AS TO NON-INFRINGEMENT. IN NO EVENT SHALL COPYRIGHT
 * HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
 * DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
 * TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 * PERFORMANCE OF THE COUGAAR SOFTWARE. </copyright>
 */
package com.bbn.openmap.dataAccess.cgm;

import java.io.*;

public class PolylineElement extends Command {
    int X[], Y[];
    int X0[], Y0[];

    public PolylineElement(int ec, int eid, int l, DataInputStream in)
            throws IOException {
        super(ec, eid, l, in);
        int n = args.length / 4;
        X = new int[n];
        Y = new int[n];
        for (int i = 0; i < n; i++) {
            X[i] = makeInt(2 * i);
            Y[i] = makeInt(2 * i + 1);
        }
    }

    public String toString() {
        String s = "Polyline";
        for (int i = 0; i < X.length; i++)
            s = s + " [" + X[i] + "," + Y[i] + "]";
        return s;
    }

    public void scale(CGMDisplay d) {
        X0 = new int[X.length];
        Y0 = new int[X.length];
        for (int i = 0; i < X.length; i++) {
            X0[i] = d.x(X[i]);
            Y0[i] = d.y(Y[i]);
        }
    }

    public void paint(CGMDisplay d) {
        d.graphics().setColor(d.getLineColor());
        d.graphics().drawPolyline(X0, Y0, X0.length);
    }
}