package com.xrbin.utils;

import com.xrbin.ddpt.utils;
import jas.Pair;

import javax.xml.crypto.Data;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

public class Statistics {
    private final Vector<Pair<Date, String>> stat = new Vector<>();

    public Statistics() { stat.add(new Pair<>(new Date(), "First.")); }

    public void point() {
        point("");
    }

    public void point(String s) {
        if(!utils.STAT) return;

        Date d = new Date();
        String res = "";
        
        res = res + "-- since first time: ";
        if(!utils.TEST2FILE) res = res + StaticData.G;
        res = res + (d.getTime() - stat.get(0).getO1().getTime());
        if(!utils.TEST2FILE) res = res + StaticData.E;
        res = res + " ms";
        while(res.length() < 40) res = res.concat(" ");
        
        if(utils.TEST2FILE) {
            util.writeFileWithPrefix(res, "TestForTest");
        } else {
            System.out.print(res);
        }

        res = " -- since last time: ";
        if(!utils.TEST2FILE) res = res + StaticData.G;
        res = res + (d.getTime() - stat.get(stat.size() - 1).getO1().getTime());
        if(!utils.TEST2FILE) res = res + StaticData.E;
        res = res + " ms";
        while(res.length() < 40) res = res.concat(" ");


        if(utils.TEST2FILE) {
            util.writeFilelnWithPrefix(res + ": " + s, "TestForTest");
        } else {
            System.out.println(res + ": " + s);
        }
        
        stat.add(new Pair<>(d, s));
    }
}
