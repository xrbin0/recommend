package com.xrbin.test;

import com.xrbin.ddpt.utils;
import com.xrbin.utils.util;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.*;
import soot.options.Options;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static soot.SootClass.BODIES;

/**
 * @author xrbin
 * @date 2023/8/26 上午11:01
 */

/**
 * @author xrbin
 * @date 2023/5/25 上午9:51
 */

public class Test01 {
    private static final Logger logger = LoggerFactory.getLogger(Test01.class);

    static {
        PropertyConfigurator.configure("/home/xrbin/IdeaProjects/MyDB/src/main/resources/log4j.properties");
    }

    public static void main(String... args) {
//        varCanCompare2();
//        func1("ftp");
//        func1("hedc");
//        func1("weblech");

//        func2("ftp");
//        func2("hedc");
//        func2("weblech");

//        func3("luindex");
//        func3("lusearch");
    }

    public static void func1(String name) {
        Map<String, Set<String>> insenCg = new HashMap<>();
        Map<String, Set<String>> objCg = new HashMap<>();
        Map<String, Set<String>> objPts = new HashMap<>();
        Map<String, Set<String>> insenPts = new HashMap<>();
        Set<String> reachables = new HashSet<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/doophome/out/" + name + "-2obj/database/AppCallGraphEdge.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                objCg.computeIfAbsent(line.split("\t")[1], k -> new HashSet<>()).add(line.split("\t")[3]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader("/home/xrbin/doophome/out/" + name + "-micro/database/aaAppCallGraphEdge.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                insenCg.computeIfAbsent(line.split("\t")[0], k -> new HashSet<>()).add(line.split("\t")[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader("/home/xrbin/doophome/out/" + name + "-micro/database/aaAppVarPointsTo.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String var = line.split("\t")[1];
                reachables.add(var.split("/")[0]);
                insenPts.computeIfAbsent(line.split("\t")[1], k -> new HashSet<>()).add(line.split("\t")[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader("/home/xrbin/doophome/out/" + name + "-2obj/database/VarPointsToApp.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                objPts.computeIfAbsent(line.split("\t")[1], k -> new HashSet<>()).add(line.split("\t")[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String s : insenCg.keySet()) {
            if (objCg.containsKey(s)) {
                if (objCg.get(s).size() < insenCg.get(s).size()) {
//                    System.out.println(s);
                    for (String s1 : insenCg.get(s)) {
                        if (!objCg.get(s).contains(s1)) {
                            if (s.contains(name)) {
                                util.writeFileln(s + "\t" + s1, "/home/xrbin/OtherProjects/yanniss-doop-4.24.10-s/docs/doop-as-lib/log/NCG-" + name + ".facts");
//                            System.out.println(s + "\t" + s1);
                            }
                        }
                    }
                }
            }
            else {
//                System.out.println(s);
                for (String s1 : insenCg.get(s)) {
                    if (s.contains(name)) {
                        util.writeFileln(s + "\t" + s1, "/home/xrbin/OtherProjects/yanniss-doop-4.24.10-s/docs/doop-as-lib/log/NCG-" + name + ".facts");
//                    System.out.println(s + "\t" + s1);
                    }
                }
            }
        }

//        for (String s : insenPts.keySet()) {
//            if (objPts.containsKey(s)) {
//                boolean f = true;
//                for (String s1 : insenPts.get(s)) {
//                    if (!objPts.get(s).contains(s1)) {
//                        if (f) {
//                            f = false;
//                        }
//                        System.out.println(s1 + "\t" + s);
//                    }
//                }
//            }
//            else {
//                for (String s1 : insenPts.get(s)) {
//                    System.out.println(s1 + "\t" + s);
//
//                }
//            }
//        }

//        System.out.println("----------------------------");
//        for (String reachable : reachables) {
//            System.out.println("---------------------------- " + reachable);
//        }


//        objCg.forEach((k, v) -> {
//            v.forEach(vv -> {
//                System.out.println(k + "\t" + vv);
//            });
//        });
    }

    public static void func2(String name) {
        Map<String, Set<String>> objCg = new HashMap<>();
        Map<String, Set<String>> insenCg = new HashMap<>();
        Map<String, Set<String>> fbCg = new HashMap<>();

        Map<String, Set<String>> objPts = new HashMap<>();
        Map<String, Set<String>> insenPts = new HashMap<>();
        Map<String, Set<String>> fbPts = new HashMap<>();

        Set<String> reachablesObj = new HashSet<>();
        Set<String> reachables1 = new HashSet<>();
        Set<String> reachables2 = new HashSet<>();
        int countCg = 0, countPts = 0;
        int countobjCg = 0, countinsenCg = 0, countfbCg = 0;
        int countobjPts = 0, countinsenPts = 0, countfbPts = 0;
        try (
                FileReader reader = new FileReader("/home/xrbin/doophome/out/" + name + "-2obj/database/AppCallGraphEdge.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                countobjCg++;
                objCg.computeIfAbsent(line.split("\t")[1], k -> new HashSet<>()).add(line.split("\t")[3]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader("/home/xrbin/doophome/out/" + name + "-micro/database/aaAppCallGraphEdge.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                countCg++;
                countinsenCg++;
                insenCg.computeIfAbsent(line.split("\t")[0], k -> new HashSet<>()).add(line.split("\t")[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader("/home/xrbin/doophome/out/" + name + "-micro-f/database/aaAppCallGraphEdge.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                countCg--;
                countfbCg++;
                fbCg.computeIfAbsent(line.split("\t")[0], k -> new HashSet<>()).add(line.split("\t")[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader("/home/xrbin/doophome/out/" + name + "-2obj/database/VarPointsToApp.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                countobjPts++;
                String var = line.split("\t")[1];
                reachablesObj.add(var.split("/")[0]);
                objPts.computeIfAbsent(line.split("\t")[1], k -> new HashSet<>()).add(line.split("\t")[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader("/home/xrbin/doophome/out/" + name + "-micro/database/aaAppVarPointsTo.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                countPts++;
                countinsenPts++;
                String var = line.split("\t")[1];
                reachables1.add(var.split("/")[0]);
                insenPts.computeIfAbsent(line.split("\t")[1], k -> new HashSet<>()).add(line.split("\t")[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader("/home/xrbin/doophome/out/" + name + "-micro-f/database/aaAppVarPointsTo.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                countPts--;
                countfbPts++;
                String var = line.split("\t")[1];
                reachables2.add(var.split("/")[0]);
                fbPts.computeIfAbsent(line.split("\t")[1], k -> new HashSet<>()).add(line.split("\t")[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        util.writeFileln(name + "," + countobjCg + "," + countinsenCg + "," + countfbCg
                + "," + countobjPts + "," + countinsenPts + "," + countfbPts
                + "," + reachablesObj.size() + "," + reachables1.size() + "," + reachables2.size(), "/home/xrbin/IdeaProjects/recommend/logs/recommand/rall.csv");

    }

    public static void func3(String name) {
        Map<String, Set<String>> fbPts1 = new HashMap<>();
        Map<String, Set<String>> fbPts2 = new HashMap<>();
        String prefix1 = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/task01/";
//        String prefix1 = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/task01/";
        String n1 = "CSOptSummary";
        String prefix2 = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/task01/";
//        String prefix1 = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/task01/";
        String n2 = "CSOptCacheSummary";
        try (
                FileReader reader = new FileReader( prefix1 + name + "/" + name + n1 + "/AllResult");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            String curVar = "";
            while ((line = br.readLine()) != null) {
                if (line.startsWith("\t")) {
                    fbPts1.get(curVar).add(line);
                }
                else {
                    curVar = line;
                    fbPts1.computeIfAbsent(line, k -> new HashSet<>());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (
                FileReader reader = new FileReader(prefix2 + name + "/" + name + n2 + "/AllResult");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            String curVar = "";
            while ((line = br.readLine()) != null) {
                if (line.startsWith("\t")) {
                    fbPts2.get(curVar).add(line);
                }
                else {
                    curVar = line;
                    fbPts2.computeIfAbsent(line, k -> new HashSet<>());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String s : fbPts2.keySet()) {
            if (fbPts1.containsKey(s) && fbPts2.get(s).size() != 0 && fbPts1.get(s).size() == 0) {
                System.out.println("s1 = " + s);
            }
            else if (!fbPts1.containsKey(s)) {
                System.out.println("s2 = " + s);
            }
        }

        for (String s : fbPts1.keySet()) {
            if (fbPts2.containsKey(s) && fbPts1.get(s).size() != 0 && fbPts2.get(s).size() == 0) {
                System.out.println("s3 = " + s);
            }
            else if (!fbPts2.containsKey(s)) {
                System.out.println("s4 = " + s);
            }
        }
    }

    public static void varCanCompare2() {
        List<String> list01 = new ArrayList<>();
        List<String> list02 = new ArrayList<>();
        List<String> list03 = new ArrayList<>();
        List<String> list04 = new ArrayList<>();
        List<String> list05 = new ArrayList<>();
        varCanCompare2(list01, "/home/xrbin/IdeaProjects/recommend/logs/result-500/result.csv");
        varCanCompare2(list02, "/home/xrbin/IdeaProjects/recommend/logs/result-1000/result.csv");
        varCanCompare2(list03, "/home/xrbin/IdeaProjects/recommend/logs/result-5000/result.csv");
        varCanCompare2(list04, "/home/xrbin/IdeaProjects/recommend/logs/result-10000/result.csv");
        varCanCompare2(list05, "/home/xrbin/IdeaProjects/recommend/logs/result-100000/result.csv");

        list01.forEach(l -> com.xrbin.utils.util.writeFileln(l, "/home/xrbin/IdeaProjects/recommend/logs/result/result.csv"));
        list02.forEach(l -> com.xrbin.utils.util.writeFileln(l, "/home/xrbin/IdeaProjects/recommend/logs/result/result.csv"));
        list03.forEach(l -> com.xrbin.utils.util.writeFileln(l, "/home/xrbin/IdeaProjects/recommend/logs/result/result.csv"));
        list04.forEach(l -> com.xrbin.utils.util.writeFileln(l, "/home/xrbin/IdeaProjects/recommend/logs/result/result.csv"));
        list05.forEach(l -> com.xrbin.utils.util.writeFileln(l, "/home/xrbin/IdeaProjects/recommend/logs/result/result.csv"));
//        for (int i = 0; i < 9; i++) {
//            com.xrbin.utils.util.writeFileln(list01.get(i), "/home/xrbin/IdeaProjects/recommend/logs/result/result.csv");
//            com.xrbin.utils.util.writeFileln(list02.get(i), "/home/xrbin/IdeaProjects/recommend/logs/result/result.csv");
//            com.xrbin.utils.util.writeFileln(list03.get(i), "/home/xrbin/IdeaProjects/recommend/logs/result/result.csv");
//            com.xrbin.utils.util.writeFileln(list04.get(i), "/home/xrbin/IdeaProjects/recommend/logs/result/result.csv");
//            com.xrbin.utils.util.writeFileln(list05.get(i), "/home/xrbin/IdeaProjects/recommend/logs/result/result.csv");
//        }

    }

    public static void varCanCompare2(List<String> list, String path) {
        try (
                FileReader reader = new FileReader(path);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                list.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
