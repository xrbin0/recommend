package com.xrbin.rc;

import com.xrbin.ddpt.model.Allocation;
import com.xrbin.ddpt.model.CSAllocation;
import com.xrbin.ddpt.model.Field;
import com.xrbin.ddpt.utils;
import com.xrbin.utils.StaticData;
import com.xrbin.utils.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class RecommendFlowsen {
    public static HashSet<String> rcVar = new HashSet<>();
    public static HashSet<Field> loadRight = new HashSet<>();
    public static HashMap<String, HashSet<String>> appVpt = new HashMap<>();
    public static HashMap<Field, HashSet<String>> storeLeft = new HashMap<>();
    public static HashMap<Field, HashSet<String>> load = new HashMap<>();
    public static HashMap<String, HashSet<Allocation>> isenVpt = new HashMap<>();
    public static String path = utils.DOOPWORKPLACE + "/out/fop/database/";

    static {
        try {
            Runtime.getRuntime().exec("rm logs/recommendVar");
        } catch (Exception e) {
            System.err.print("");
        }
    }

    public static void main(String[] args) {
        readData();
        for (Field key : storeLeft.keySet()) {
//            util.plnB(key.toString());
            if (storeLeft.get(key).size() > 1 && loadRight.contains(key)) {
//                util.plnB("\t" + key.toString());
                for (Field f : load.keySet()) {
//                    for (String var : load.get(f)) {
//                        rcVar.add(var);
//                    }
                    rcVar.addAll(load.get(f));
                }
            }
        }
        rcVar.forEach(s -> {
            if (appVpt.containsKey(s)) {
                util.writeFilelnWithPrefix(s, "recommendVar");
//            util.plnG(s);
            }
//            util.writeFilelnWithPrefix(s, "recommendVar");
        });

        util.plnB(rcVar.size() + "");
    }

    public static HashSet<String> recommand() {
        readData();
        for(Field key : storeLeft.keySet()) {
            if (storeLeft.get(key).size() > 1 && loadRight.contains(key)) {
                for (Field f : load.keySet()) {
                    rcVar.addAll(load.get(f));
                }
            }
        }
        for( String s : rcVar) {
            util.plnG(s);
        }
        return rcVar;
    }

    public static HashSet<String> recommand(String path) {
        readData();
        for(Field key : storeLeft.keySet()) {
            if (storeLeft.get(key).size() > 1 && loadRight.contains(key)) {
                for (Field f : load.keySet()) {
                    rcVar.addAll(load.get(f));
                }
            }
        }
        for( String s : rcVar) {
            util.plnG(s);
        }
        return rcVar;
    }

    public static void init(){
        rcVar = new HashSet<>();
        loadRight = new HashSet<>();
        appVpt = new HashMap<>();
        storeLeft = new HashMap<>();
        load = new HashMap<>();
    }

    public static void readData(){
        init();
        try (
                FileReader reader = new FileReader(path + "VarPointsToApp.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] sa = line.split("\t");
                if(!appVpt.containsKey(sa[1])){
                    appVpt.put(sa[1], new HashSet<>());
                }
                appVpt.get(sa[1]).add(sa[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println(appVpt.size());

        try (
                FileReader reader = new FileReader(path + "AppStoreInstanceField.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            int c1 = 0, c2 = 0;
            while ((line = br.readLine()) != null) {
//                System.out.println(line);
                String[] sa = line.split("\t");
                HashSet<String> Os = appVpt.get(sa[1]);
                if(Os == null) {
                    c1++;
//                    System.out.println("-------------------------------" + sa[1]);
                    continue;
                } else {
                    c2++;
                    for(String o : Os) {
                        Field f = new Field(new CSAllocation(new Allocation(o)), sa[2]);
                        storeLeft.computeIfAbsent(f, k -> new HashSet<>()).add(sa[1] + utils.CONCAT + storeLeft.get(f).size());
                    }
                }
            }
            System.out.println("---- c1 = " + StaticData.G + c1 + StaticData.E + ", c2 = " + StaticData.G + c2 + StaticData.E + "");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader(path + "AppLoadInstanceField.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
//                System.out.println(line);
                String[] sa = line.split("\t");
                HashSet<String> Os = appVpt.get(sa[0]);
                if(Os != null) {
                    for(String o : Os) {
                        Field f = new Field(new CSAllocation(new Allocation(o)), sa[1]);
                        loadRight.add(f);
                        load.computeIfAbsent(f, k -> new HashSet<>()).add(sa[2]);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
