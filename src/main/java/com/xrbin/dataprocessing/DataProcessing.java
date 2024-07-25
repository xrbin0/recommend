package com.xrbin.dataprocessing;

import com.xrbin.utils.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author xrbin
 * @date 2023/11/1 下午9:57
 */
public class DataProcessing {
    public static String task = "task01";
//    public static String task = "task02";
    public static void main(String[] args) {
        varCanCompare();

//        sameVars();
//        makeBatch();
//        func01();
//        vpt();
//        noResultDependSize();
//        timeCompare02();
//        resCompare("CSOpt", task);
//        resCompare("CSOptCache", task);
//        resCompare("CSOptCacheSummary", task);
//        compareCan();
//        compare02();
//        varNumber();

//        varCanNumber06();
//        time06();

//        varCanNumberSimple("CSOpt", task);
//        timeSimple("CSOpt", task);
//        varCanNumberSimple("", task);
//        varCanNumberSimple("CSOptCache", task);
//        timeSimple("CSOptCache", task);
//        varCanNumberSimple("CSOptCacheSummary", task);
//        timeSimple("CSOptCacheSummary", task);

//        func01();
//        batchResCompare();

//        timeBatch(names, "CSOptCacheBatch");
//        timeBatch(names, "CSOptCacheBatchSummary");

//        varCanbatch("CSOptCacheBatch", task);
//        timeBatch("CSOptCacheBatch", task);
//        varCanbatch("CSOptCacheBatchSummary", task);
//        timeBatch("CSOptCacheBatchSummary", task);

//        vpt();

//        dpSize();
    }


    public static void sameVars() {
//        sameVars("luindex");
//        sameVars("lusearch");
//        sameVars("bloat");
//        sameVars("pmd");
//        sameVars("chart");
//        sameVars("eclipse");
//        sameVars("jython");
//        sameVars("antlr");
//        sameVars("fop");
//        sameVars("hsqldb");
//        sameVars("xalan");
        sameVars("hedc");
        sameVars("weblech");
        sameVars("ftp");
    }

    public static void func01() {
//        func01("luindex");
//        func01("lusearch");
//        func01("chart");
//        func01("bloat");
//        func01("eclipse");
//        func01("pmd");
//        func01("jython");
        func01("hedc");
        func01("weblech");
        func01("ftp");
    }

    public static void func01(String testName) {
        Set<String> strings01 = new HashSet<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/allvar/" + testName + "AllReachableCastVarsSameBatch");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.contains("----------")) {
                    strings01.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(strings01.size());
    }


    public static void makeBatch() {
//        makeBatch("luindex");
//        makeBatch("lusearch");
//        makeBatch("bloat");
//        makeBatch("pmd");
//        makeBatch("chart");
//        makeBatch("eclipse");
//        makeBatch("jython");
//        makeBatch("antlr");
//        makeBatch("fop");
//        makeBatch("hsqldb");
//        makeBatch("xalan");
        makeBatch("hedc");
        makeBatch("weblech");
        makeBatch("ftp");
    }

    public static void makeBatch(String testName) {
        Map<String, Set<String>> batch = new HashMap<>();

        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/allvar/" + testName + "AllReachableCastVarsSame");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(":")) {
                    String s = line.split("\t")[0];
                    if (s.contains(".")) {
                        String s1 = s.substring(0, s.lastIndexOf("."));
                        if (s1.contains(".")) {
                            String key = s1.substring(0, s1.lastIndexOf("."));
                            batch.computeIfAbsent(key, k -> new HashSet<>()).add(line);
                        }
                        else {
                            batch.computeIfAbsent("no .", k -> new HashSet<>()).add(line);
                        }
                    }
                    else {
                        batch.computeIfAbsent("no .", k -> new HashSet<>()).add(line);
                    }
                }
                else {
                    System.out.println(line + "did not contain \":\"");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        batch.forEach((k,v) -> {
//            util.writeFilelnWithPrefix(v.size() + "", "BatchSize/" + testName + "BatchSize");
            v.forEach(v1 -> {
                util.writeFileln(v1, "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/allvar/" + testName + "AllReachableCastVarsSameBatch");
            });
            util.writeFileln("------------------------------------------------", "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/allvar/" + testName + "AllReachableCastVarsSameBatch");
        });
    }

    public static void sameVars(String testName) {
        String file1 = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/allvar/" + testName + "AllReachableCastVars";
        String file2 = "/home/xrbin/IdeaProjects/MyDB/logs/allvar/" + testName + "AllVars";
        String file3 = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/allvar/" + testName + "01";
        String file4 = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/allvar/" + testName + "SameVars";
        String file5 = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/dacapoCiPts/" + testName + "Insen";
        Set<String> strings01 = new HashSet<>();
        try (
                FileReader reader = new FileReader(file1);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                strings01.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println(strings01.size());

        Set<String> strings02 = new HashSet<>();
        try (
                FileReader reader = new FileReader(file2);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                strings02.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        Map<String, Set<String>> map = new HashMap<>();
        try (
                FileReader reader = new FileReader(file5);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            String curVar = "";
            while ((line = br.readLine()) != null) {
                if (line.startsWith("\t")) {
                    map.get(curVar).add(line);
                }
                else {
                    curVar = line;
                    map.computeIfAbsent(line, k -> new HashSet<>());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

//        System.out.println(strings02.size());

//        Set<String> strings04 = new HashSet<>();
//        try (
//                FileReader reader = new FileReader(file3);
//                BufferedReader br = new BufferedReader(reader)
//        ) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                strings04.add(line);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        int cc = 0;
        for (String s : strings01) {
            if (strings02.contains(s) && map.containsKey(s) && map.get(s).size() > 0) {
                util.writeFileln(s, "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/allvar/" + testName + "AllReachableCastVarsSame");
                cc++;
            }
        }

        System.out.println(testName + "\t" + cc);
//        for (String s : strings01) {
//            if (strings02.contains(s) && strings04.contains(s)) {
////                if (strings02.contains(s)) {
//                System.out.println(s);
//            }
//        }
//        Set<String> strings05 = new HashSet<>();
//        try (
//                FileReader reader = new FileReader(file4);
//                BufferedReader br = new BufferedReader(reader)
//        ) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                strings05.add(line);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println(testName + " SameVars: " + strings05.size());

//        Set<String> strings03 = new HashSet<>();
//        strings02.forEach(s -> {
//            if (strings01.contains(s)) {
//                strings03.add(s);
////                util.writeFileln(s, "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/allvar/" + testName + "SameVars");
//            }
//        });
//        System.out.println(testName + " SameVars: " + strings03.size());
//        dataraceFind(testName);
    }

    public static void reachableMethod() {
        reachableMethod("luindex");
        reachableMethod("lusearch");
        reachableMethod("bloat");
        reachableMethod("antlr");
        reachableMethod("fop");
        reachableMethod("pmd");
        reachableMethod("hsqldb");
        reachableMethod("chart");
        reachableMethod("eclipse");
        reachableMethod("xalan");
        reachableMethod("jython");
    }

    public static void reachableMethod(String testName) {
        String file1 = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/allvar/" + testName + "AllVars";
        String file2 = "/home/xrbin/IdeaProjects/MyDB/logs/allvar/" + testName + "AllVars";
        Set<String> strings01 = new HashSet<>();
        try (
                FileReader reader = new FileReader(file1);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                strings01.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(strings01.size());

        Set<String> strings02 = new HashSet<>();
        try (
                FileReader reader = new FileReader(file2);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                strings02.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(strings02.size());

        Set<String> rm1 = new HashSet<>();
        Set<String> rm2 = new HashSet<>();

        strings01.forEach(v -> rm1.add(v.split("/")[0]));
        strings02.forEach(v -> rm2.add(v.split("/")[0]));

        for (String s : rm1) {
            util.writeFileln(s, "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/rm1/" + testName + "RM");
        }
        for (String s : rm2) {
            util.writeFileln(s, "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/rm2/" + testName + "RM");
        }
    }

    public static void dataraceFind(String testName) {
        String file1 = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/allvar/" + testName + "01";
        String file2 = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/allvar2/" + testName + "SameVars";
        Map<Integer, Set<String>> strings01 = new HashMap<>();
        try (
                FileReader reader = new FileReader(file1);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            Integer integer = 0;
            while ((line = br.readLine()) != null) {
                if (!line.contains("----------------")) {
                    strings01.computeIfAbsent(integer, k -> new HashSet<>()).add(line);
                }
                else {
                    integer++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Set<String> strings02 = new HashSet<>();
        try (
                FileReader reader = new FileReader(file2);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                strings02.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        strings01.forEach((k, v) -> {
//            if (strings02.containsAll(v)) {
//                System.out.println(k);
//            }
            v.forEach(var -> {
                if (!strings02.contains(var)) {
//                    System.out.println(var);
                }
                else {
                    util.writeFileln(var, "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/allvar/" + testName + "02");
                }
            });
            util.writeFileln("------------------------------------------------", "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/allvar/" + testName + "02");
        });
    }

    public static void vpt() {
        String file0 = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/dacapoCiPts/luindexInsen";
        Map<String, Set<String>> map = new HashMap<>();
        try (
                FileReader reader = new FileReader(file0);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            String curVar = "";
            while ((line = br.readLine()) != null) {
                if (line.startsWith("\t")) {
                    map.get(curVar).add(line);
                }
                else {
                    curVar = line;
                    map.computeIfAbsent(line, k -> new HashSet<>());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String file1 = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/luindex/luindexCSOpt/AllResult";
        Map<String, Set<String>> map01 = new HashMap<>();
        try (
                FileReader reader = new FileReader(file1);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            String curVar = "";
            while ((line = br.readLine()) != null) {
                if (line.startsWith("\t")) {
                    map01.get(curVar).add(line);
                }
                else {
                    curVar = line;
                    map01.computeIfAbsent(line, k -> new HashSet<>());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String file2 = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/luindex/luindexCSOptCache/AllResult";
        Map<String, Set<String>> map02 = new HashMap<>();
        try (
                FileReader reader = new FileReader(file2);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            String curVar = "";
            while ((line = br.readLine()) != null) {
                if (line.startsWith("\t")) {
                    map02.get(curVar).add(line);
                }
                else {
                    curVar = line;
                    map02.computeIfAbsent(line, k -> new HashSet<>());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String s : map01.keySet()) {
            if (map.containsKey(s) && map.get(s).size() > 0) {
                if (!map02.containsKey(s)) {
                    System.out.println(1 + "\t" + s);
                }
                else if (map01.get(s).size() > 0 && map02.get(s).size() == 0) {
                    System.out.println(2 + "\t" + s);
                }
                else if (map01.get(s).size() == 0 && map02.get(s).size() == 0) {
                    System.out.println(3 + "\t" + s);
                }
                else if (map01.get(s).size() == 0 && map02.get(s).size() > 0) {
                    System.out.println(4 + "\t" + s);
                }
            }
        }

        System.out.println("--------------------------------------------------------------------");

        for (String s : map02.keySet()) {
            if (map.containsKey(s) && map.get(s).size() > 0) {
                if (!map01.containsKey(s)) {
                    System.out.println(1 + "\t" + s);
                }
                else if (map02.get(s).size() > 0 && map01.get(s).size() == 0) {
                    System.out.println(2 + "\t" + s);
                }
                else if (map02.get(s).size() == 0 && map01.get(s).size() == 0) {
                    System.out.println(3 + "\t" + s);
                }
                else if (map02.get(s).size() == 0 && map01.get(s).size() > 0) {
                    System.out.println(4 + "\t" + s);
                }
            }
        }

//        int count = 0;
//        for (String s : map02.keySet()) {
//            if (map02.get(s).size() > 0) {
//                count++;
//            }
//        }
//        System.out.println(count);
    }

    public static void fileDiffDependSize() {
        String file1 = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/luindex_cs/DependTreeSize";
        String file2 = "/home/xrbin/IdeaProjects/MyDB/logs/luindex-yes/isEmptyVar";
        String file2_1 = "/home/xrbin/IdeaProjects/MyDB/logs/luindex-yes/notDoneVar";
        Set<String> strings01 = new HashSet<>();
        try (
                FileReader reader = new FileReader(file2);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                strings01.add(line.split("\t")[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(strings01.size());
        try (
                FileReader reader = new FileReader(file2_1);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                strings01.add(line.split("\t")[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(strings01.size());

        Set<String> strings02 = new HashSet<>();
        Map<String, String> map = new HashMap<>();
        try (
                FileReader reader = new FileReader(file1);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                strings02.add(line.split("\t")[0]);
                map.put(line.split("\t")[0], line.split("\t")[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(strings02.size());

        strings01.forEach(s -> {
            if (!strings02.contains(s)) {
                util.writeFilelnWithPrefix(s, "onDemand/luindexDiffItNot");
            }
        });
        strings02.forEach(s -> {
            if (!strings01.contains(s)) {
//                System.out.println(s + "\t" + map.get(s));
                util.writeFilelnWithPrefix(s + "\t" + map.get(s), "onDemand/luindexDiffMeNot");
            }
        });
    }

    public static void fileDiffCsNoResDependSize() {
        String file1 = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/luindex_cs_simple02/noRes";
        String file2 = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/luindexNoSM/DependTreeSizeAll";
        Set<String> strings01 = new HashSet<>();
        Map<String, String> map01 = new HashMap<>();
        try (
                FileReader reader = new FileReader(file1);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                strings01.add(line.split("\t")[0]);
                map01.put(line.split("\t")[0], line.split("\t")[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(strings01.size());

        Set<String> strings02 = new HashSet<>();
        Map<String, String> map02 = new HashMap<>();
        try (
                FileReader reader = new FileReader(file2);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                strings02.add(line.split("\t")[0]);
                map02.put(line.split("\t")[0], line.split("\t")[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(strings02.size());

        map01.forEach((k, v) -> {
            if (map02.containsKey(k)) {
                if (Integer.parseInt(map02.get(k)) < 100) {
                    System.out.println(k + "\t" + map01.get(k) + "\t" + map02.get(k));
                }
            }
        });

    }

    // noRes
    public static void noResult() {
        String testname = "pmd";
        Map<String, String> noResVarMap = new HashMap<>();
        Set<String> noResVar = new HashSet<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/" + testname + "/" + testname + "/noRes");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                noResVar.add(line.split("\t")[0]);
                noResVarMap.put(line.split("\t")[0], line.split("\t")[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Set<String>> pts2obj = new HashMap<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/" + testname + "/" + testname + "_2obj/luindex_insen");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            String var = "";
            while ((line = br.readLine()) != null) {
                try {
                    if (!line.startsWith("\t")) {
                        var = line;
                        pts2obj.computeIfAbsent(var, k -> new HashSet<>());
                    }
                    else {
                        String o1 = line.replace("\t", "");
                        pts2obj.computeIfAbsent(var, k -> new HashSet<>()).add(o1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String s : noResVar) {
            if (pts2obj.containsKey(s) && pts2obj.get(s).size() != 0) {
                boolean flag = true;
                for (String s1 : pts2obj.get(s)) {
                    if (s1.contains("Error") || s1.contains("Exception")) {
                        flag = false;
                    }
                }
                if (flag && !s.contains("Exception")) {
                    util.writeFilelnWithPrefix(s + "\t" + noResVarMap.get(s), "onDemand/noResVar");
                }
            }
            else if (!pts2obj.containsKey(s)) {
                System.out.println(s);
            }
        }
    }

    // noRes DependSize
    public static void noResultDependSize() {
        Set<String> noResVar = new HashSet<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/luindex/luindex3/noRes");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                noResVar.add(line.split("\t")[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, String> varDependSize = new HashMap<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/luindex/luindex2/DependTreeSizeAll");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                varDependSize.put(line.split("\t")[0], line.split("\t")[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        Map<String, String> noResVarDependSize = new HashMap<>();
        noResVar.forEach(var -> {
            if (var.contains("!!!!!!!!!!!!!!!!!!!")
                    || var.contains("<org.apache.lucene.index.SegmentMerger: void mergeTerms()>")
                    || var.contains("<org.apache.lucene.index.SegmentMerger: void bufferSkip(int)>")
                    || var.contains("<org.apache.lucene.index.IndexWriter: void mergeSegments(int,int)>")
//                    || var.contains("")
//                    || var.contains("")
//                    || var.contains("")
//                    || var.contains("")
//                    || var.contains("")
//                    || var.contains("")
            ) {}
            else {
                noResVarDependSize.put(var, varDependSize.get(var));
                System.out.println(var + "\t" + varDependSize.get(var));
            }
        });
    }

    // 用户方法调用库方法
    // add get
    // 容器里面的数据流固定
    public static void findNot() {
        String file1 = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/luindexNoSM01/DependTreeSize";
        String file1_2 = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/luindexNoSM01/noRes";
        String file2 = "/home/xrbin/IdeaProjects/MyDB/logs/luindex/notDoneVar";
        String file2_2 = "/home/xrbin/IdeaProjects/MyDB/logs/luindex/isEmptyVar";
        Set<String> strings01 = new HashSet<>();
        try (
                FileReader reader = new FileReader(file1);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String s1 = line.split("\t")[0];
                String s2 = line.split("\t")[1];
//                if (Integer.parseInt(s2) < 400) {
                strings01.add(s1);
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (
                FileReader reader = new FileReader(file1_2);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String s1 = line.split("\t")[0];
                String s2 = line.split("\t")[1];
//                if (Integer.parseInt(s2) < 400) {
                strings01.remove(s1);
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(strings01.size());

        Set<String> strings02 = new HashSet<>();
        try (
                FileReader reader = new FileReader(file2);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                strings02.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (
                FileReader reader = new FileReader(file2_2);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                strings02.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (
                FileReader reader = new FileReader(file1_2);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String s1 = line.split("\t")[0];
                String s2 = line.split("\t")[1];
//                if (Integer.parseInt(s2) < 400) {
                strings02.remove(s1);
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(strings02.size());

        String file3 = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/luindexNoSM/luindexTimeAll";
        String file4 = "/home/xrbin/IdeaProjects/MyDB/logs/luindex/varTimeAll";
        Map<String, Integer> varTimeMe = new HashMap<>();
        Map<String, Integer> varTimeIt = new HashMap<>();
        try (
                FileReader reader = new FileReader(file3);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                varTimeMe.put(line.split("\t")[0], Integer.parseInt(line.split("\t")[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (
                FileReader reader = new FileReader(file4);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                varTimeIt.put(line.split("\t")[0], Integer.parseInt(line.split("\t")[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        strings01.forEach(s -> {
            if (!strings02.contains(s)) {
                util.writeFilelnWithPrefix(s + "\t" + varTimeIt.get(s), "onDemand/luindexDiffMeNot");
            }
        });
        strings02.forEach(s -> {
            if (!strings01.contains(s)) {
                util.writeFilelnWithPrefix(s + "\t" + varTimeMe.get(s), "onDemand/luindexDiffItNot");
            }
        });
    }

    public static void compareTime() {
        String file1 = "/home/xrbin/IdeaProjects/MyDB/logs/luindex/varTimeAll";
        String file2 = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/luindexSM/luindexTimeAll";
        Map<String, Integer> map01 = new HashMap<>();
        try (
                FileReader reader = new FileReader(file1);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                map01.put(line.split("\t")[0], Integer.parseInt(line.split("\t")[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(map01.keySet().size());

        Map<String, Integer> map02 = new HashMap<>();
        try (
                FileReader reader = new FileReader(file2);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                map02.put(line.split("\t")[0], Integer.parseInt(line.split("\t")[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(map02.keySet().size());


        map01.keySet().forEach(s -> {
            if (map02.containsKey(s)) {
                util.writeFilelnWithPrefix(map01.get(s) + "\t" + map02.get(s) + "\t" + s, "onDemand/compareTime");
            }
        });
    }

    public static void compareCan() {
//        compareCan("luindex");
//        compareCan("lusearch");
//        compareCan("bloat");
//        compareCan("pmd");
//        compareCan("eclipse");
//        compareCan("jython");
//        compareCan("chart");
    }

    public static void compareCan(String name, String prefix) {
        mkDir(name + prefix);
        String file0 = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/" + name + "/" + name + prefix + "" + "/AllVar";
        String file00 = "/home/xrbin/IdeaProjects/MyDB/logs/" + name + "/" + name + "1/luindexVars";

        String file1 = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/" + name + "/" + name + prefix + "" + "/TimeAll";
        String file10 = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/" + name + "/" + name + prefix + "" + "/alreadyRes";
        String file2 = "/home/xrbin/IdeaProjects/MyDB/logs/" + name + "/" + name + "" + "1/varTimeAll";

        Set<String> vars = new HashSet<>();
        try (
                FileReader reader = new FileReader(file0);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.contains("---")) {
                    vars.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Set<String> alreadyRes = new HashSet<>();
        try (
                FileReader reader = new FileReader(file10);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.contains("---")) {
                    alreadyRes.add(line.split("\t")[0]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Set<String> vars2 = new HashSet<>();
        try (
                FileReader reader = new FileReader(file00);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.contains("---")) {
                    vars2.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Integer> map01 = new HashMap<>();
        try (
                FileReader reader = new FileReader(file1);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                map01.put(line.split("\t")[0], Integer.parseInt(line.split("\t")[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        alreadyRes.forEach(v -> map01.put(v, 0));

        Map<String, Integer> map02 = new HashMap<>();
        try (
                FileReader reader = new FileReader(file2);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                map02.put(line.split("\t")[0], Integer.parseInt(line.split("\t")[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String s : map01.keySet()) {
            if (!map02.containsKey(s)) {
                util.writeFilelnWithPrefix(s, "onDemand/" + name + prefix + "/weCanItCant");
            }
        }
        for (String s : map02.keySet()) {
            if (!map01.containsKey(s)) {
                util.writeFilelnWithPrefix(s, "onDemand/" + name + prefix + "/itCanWeCant");
            }
        }

//        System.out.println(name + ": " + itTimeSm + "\t" + myTimeSm);
    }

    public static void resCompare(String prefix, String taskName) {
        resCompare("luindex", prefix, taskName);
        resCompare("lusearch", prefix, taskName);
        resCompare("bloat", prefix, taskName);
        resCompare("pmd", prefix, taskName);
        resCompare("eclipse", prefix, taskName);
        resCompare("chart", prefix, taskName);
        resCompare("jython", prefix, taskName);
    }

    public static void timeCompare02() {
//        timeCompare02("luindex");
//        timeCompare02("lusearch");
//        timeCompare02("bloat");
//        timeCompare02("chart");
//        timeCompare02("pmd");
//        timeCompare02("eclipse");
//        timeCompare02("jython");
    }

    public static void compare02(String prefix) {
        compare02("luindex", prefix);
        compare02("lusearch", prefix);
        compare02("chart", prefix);
        compare02("bloat", prefix);
        compare02("eclipse", prefix);
        compare02("pmd", prefix);
        compare02("jython", prefix);
    }

    public static void varNumber() {
        Vector<String> names = new Vector<>();
        names.add("luindex");
        names.add("lusearch");
        names.add("chart");
        names.add("bloat");
        names.add("eclipse");
        names.add("pmd");
        names.add("jython");
        varNumber(names);
    }

    public static void compare02() {
//        compare02("");
//        compare02("3");
//        compare02("SMCacheCSOpt");
        compare02("CacheCSOpt");
//        compare02("");
    }

//    public static String index = "";
//    public static String index = "3";
//    public static String index = "SMCacheCSOpt";
    public static void compare02(String name, String prefix) {
        mkDir(name + prefix);
//        resCompare(name, prefix);
//        timeCompare02(name, prefix);
    }


    public static void varNumber(Vector<String> names) {
        names.forEach(name -> {
            Set<String> varsMine = new HashSet<>();
            try (
                    FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/allvar/" + name + "02");
                    BufferedReader br = new BufferedReader(reader)
            ) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.contains("--------")) {
                        varsMine.add(line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.print(varsMine.size() + "\n");
        });
        System.out.println("");
        names.forEach(name -> {
            Set<String> varsMine = new HashSet<>();
            try (
                    FileReader reader = new FileReader("/home/xrbin/IdeaProjects/MyDB/logs/" + name + "/" + name + "1/luindexVars");
                    BufferedReader br = new BufferedReader(reader)
            ) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.contains("--------")) {
                        varsMine.add(line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.print(varsMine.size() + "\t");
        });
        System.out.println("");
    }

    public static void varCanNumber06() {
        Vector<String> names = new Vector<>();
        names.add("luindex");
        names.add("lusearch");
        names.add("chart");
        names.add("bloat");
        names.add("eclipse");
        names.add("pmd");
        names.add("jython");
        varCanNumber06(names);
    }

    public static int resVar06 = 0;
    public static void varCanNumber06(Vector<String> names) {
        names.forEach(name -> {
            resVar06 = 0;
            Map<String, Set<String>> ptsDB = new HashMap<>();
            try (
                    FileReader reader = new FileReader("/home/xrbin/IdeaProjects/MyDB/logs/" + name + "/" + name + "/allResult");
                    BufferedReader br = new BufferedReader(reader)
            ) {
                String line;
                String var = "";
                while ((line = br.readLine()) != null) {
                    try {
                        if (!line.startsWith("\t")) {
                            String var_origin = line.split("\t")[3];
                            var = var_origin.split(" =")[0];
                            ptsDB.computeIfAbsent(var, k -> new HashSet<>());
                        }
                        else {
                            ptsDB.computeIfAbsent(var, k -> new HashSet<>()).add(line.replace("\t", ""));
                        }
                    } catch (Exception e) {
                        System.err.println(line);
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            Set<String> vars = new HashSet<>();
            try (
                    FileReader reader = new FileReader("/home/xrbin/IdeaProjects/MyDB/logs/" + name + "/" + name + "/luindexVars");
                    BufferedReader br = new BufferedReader(reader)
            ) {
                String line;
                while ((line = br.readLine()) != null) {
                    vars.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (String s : ptsDB.keySet()) {
                if (ptsDB.get(s).size() > 0) {
                    resVar06++;
                }
            }

            System.out.println(resVar06 + "\t" + vars.size());
        });
    }



    public static void resCompare06() {
        Vector<String> names = new Vector<>();
        names.add("luindex");
        names.add("lusearch");
        names.add("chart");
        names.add("bloat");
        names.add("eclipse");
        names.add("pmd");
        names.add("jython");
        varCanNumber06(names);
    }

    public static void resCompare06(Vector<String> names) {
        names.forEach(name -> {
            resVar06 = 0;
            Map<String, Set<String>> ptsDB = new HashMap<>();
            try (
                    FileReader reader = new FileReader("/home/xrbin/IdeaProjects/MyDB/logs/" + name + "/" + name + "Task01/allResult");
                    BufferedReader br = new BufferedReader(reader)
            ) {
                String line;
                String var = "";
                while ((line = br.readLine()) != null) {
                    try {
                        if (!line.startsWith("\t")) {
                            String var_origin = line.split("\t")[3];
                            var = var_origin.split(" =")[0];
                            ptsDB.computeIfAbsent(var, k -> new HashSet<>());
                        }
                        else {
                            String o = line.replace("\t", "");
                            o = o.split(", ")[0];
                            ptsDB.computeIfAbsent(var, k -> new HashSet<>()).add(o);
                        }
                    } catch (Exception e) {
                        System.err.println(line);
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            Map<String, Set<String>> insenPts = new HashMap<>();
            try (
                    FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/" + "dacapoCiPts/" + name + "Insen");
                    BufferedReader br = new BufferedReader(reader)
            ) {
                String line;
                String var = "";
                while ((line = br.readLine()) != null) {
                    try {
                        if (!line.startsWith("\t")) {
                            var = line;
                            insenPts.computeIfAbsent(line, k -> new HashSet<>());
                        }
                        else {
                            insenPts.computeIfAbsent(var, k -> new HashSet<>()).add(line.replace("\t", ""));
                        }
                    } catch (Exception e) {
                        System.err.println(line);
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void varCanNumberSimple(String prefix, String taskName) {
        Vector<String> names = new Vector<>();
        names.add("luindex");
        names.add("lusearch");
        names.add("chart");
        names.add("bloat");
        names.add("eclipse");
        names.add("pmd");
        names.add("jython");
        varCanNumberSimple(names, prefix, taskName);
    }

    public static int resVarSimple = 0;
    public static void varCanNumberSimple(Vector<String> names, String prefix, String taskName) {
        names.forEach(name -> {
            resVarSimple = 0;
            Map<String, Set<String>> pts = new HashMap<>();
            try (
                    FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/" + taskName + "/" + name + "/" + name + prefix + "/AllResult");
                    BufferedReader br = new BufferedReader(reader)
            ) {
                String line;
                String var = "";
                while ((line = br.readLine()) != null) {
                    try {
                        if (!line.startsWith("\t")) {
                            var = line;
                            pts.computeIfAbsent(line, k -> new HashSet<>());
                        }
                        else {
                            if (line.contains("(AllocNode") && !line.contains("null")) {
//                            System.out.println(line);
                                String insenO = line.substring(line.indexOf("(AllocNode"), line.indexOf(">,") + 2);
                                pts.computeIfAbsent(var, k -> new HashSet<>()).add(insenO.replace("\t", ""));
                            }
                            else if (line.contains("(AllocNode") && line.contains("null")) {
//                            System.out.println(line);
                                String insenO = line.substring(line.indexOf("(AllocNode"), line.indexOf("null"));
                                pts.computeIfAbsent(var, k -> new HashSet<>()).add(insenO.replace("\t", ""));
                            }
                            else {
                                pts.computeIfAbsent(var, k -> new HashSet<>()).add(line.replace("\t", ""));
                            }
                        }
                    } catch (Exception e) {
                        System.err.println(line);
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (String s : pts.keySet()) {
                if (pts.get(s).size() > 0) {
//                    if (name.equals("bloat")) {
//                        System.out.println(s);
//                    }
                    resVarSimple++;
                }
            }

            System.out.println(resVarSimple);
        });
    }

    public static void varCanCompare() {
        System.out.println("allvars count06 count countSameVar countVS06 countVS2obj " +
                "(count - countSameVar) (count06 - countSameVar) sum(countTimeMine) sum(countTime06)");
        varCanCompare("luindex");
        varCanCompare("lusearch");
        varCanCompare("chart");
//        varCanCompare("bloat");
        varCanCompare("eclipse");
        varCanCompare("pmd");
//        varCanCompare("jython");
        varCanCompare("hedc");
        varCanCompare("ftp");

//        varCanCompare2("luindex");
//        varCanCompare2("lusearch");
//        varCanCompare2("chart");
//        varCanCompare2("bloat");
//        varCanCompare2("eclipse");
//        varCanCompare2("pmd");
//        varCanCompare2("jython");
//        varCanCompare2("hedc");
//        varCanCompare2("ftp");
    }
    public static void varCanCompare(String name) {
        Set<String> allvars = new HashSet<>();
        Map<String, Set<String>> pts06 = new HashMap<>();
        Map<String, Set<String>> pts2obj = new HashMap<>();
        Map<String, Set<String>> pts2insen = new HashMap<>();
        Map<String, Set<String>> ptsMine = new HashMap<>();
        Map<String, Set<String>> ptsMine00 = new HashMap<>();
        Map<String, Integer> dependSizeMine = new HashMap<>();
        Map<String, Integer> timeMine = new HashMap<>();
        Map<String, Integer> time06 = new HashMap<>();

//        String suffixName = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/task01-500-jdksummary/" + name + "/" + name + "CSOptSummary/";
//        String suffixName = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/task01/" + name + "/" + name + "CSOptSummary/";
//        String suffixName = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/task01/" + name + "/" + name + "CSOptCacheSummary/";
        String suffixName = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/task01/" + name + "/" + name + "CSOptCache/";
//        String suffixName = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/task00/" + name + "/" + name + "/";

        try (
                FileReader reader = new FileReader("/home/xrbin/doophome/out/" + name + "-2obj/database/Stats_Simple_InsensVarPointsTo.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.contains("<<null pseudo heap>>")) {
                    pts2obj.computeIfAbsent(varNameDoopToShimple(line.split("\t")[1]), k -> new HashSet<>()).add(line.split("\t")[0]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader("/home/xrbin/doophome/out/" + name + "-insen/database/Stats_Simple_InsensVarPointsTo.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.contains("<<null pseudo heap>>")) {
                    pts2insen.computeIfAbsent(varNameDoopToShimple(line.split("\t")[1]), k -> new HashSet<>()).add(line.split("\t")[0]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader(suffixName + "/AllVar");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                allvars.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader(suffixName + "/DependTreeSizeSmall");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                dependSizeMine.put(line.split("\t")[0], Integer.parseInt(line.split("\t")[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader(suffixName + "/TimeAll");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                timeMine.put(line.split("\t")[0], Integer.parseInt(line.split("\t")[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/MyDB/logs/" + name + "/" + name + "Task02/varTimeAll");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                time06.put(line.split("\t")[0], Integer.parseInt(line.split("\t")[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/MyDB/logs/" + name + "/" + name + "Task02/allResult");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            String var = "";
            while ((line = br.readLine()) != null) {
                try {
                    if (!line.startsWith("\t")) {
                        var = line.split("\t")[3];
                        pts06.computeIfAbsent(var, k -> new HashSet<>());
                    }
                    else {
                        if (line.contains("AllocNode") && !line.contains("null")) {
//                            System.out.println(line);
                            String insenO = line.substring(line.indexOf("AllocNode"), line.indexOf(">,") + 2);
                            pts06.computeIfAbsent(var, k -> new HashSet<>()).add(insenO.replace("\t", ""));
                        }
                    }
                } catch (Exception e) {
                    System.err.println(line);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader(suffixName + "/AllResult");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            String var = "";
            while ((line = br.readLine()) != null) {
                try {
                    if (!line.startsWith("\t")) {
                        var = line;
                        ptsMine.computeIfAbsent(line, k -> new HashSet<>());
                    }
                    else {
                        if (line.contains("(AllocNode") && line.contains(",")) {
                            ptsMine.computeIfAbsent(var, k -> new HashSet<>()).add(line.substring(line.indexOf("(") + 1, line.indexOf(",")));
                        }
                        else {
//                            System.out.println("!(line.contains(\"(\") && line.contains(\",\"))" + line);
                        }
                        ptsMine00.computeIfAbsent(var, k -> new HashSet<>()).add(line);
                    }
                } catch (Exception e) {
                    System.err.println(line);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> count = new ArrayList<>();
        List<Integer> countTimeMine = new ArrayList<>();
        List<Integer> countTime06 = new ArrayList<>();
        List<String> countSameVar = new ArrayList<>();
        List<String> countVS2obj = new ArrayList<>();
        List<String> countVS06 = new ArrayList<>();
        List<String> count06 = new ArrayList<>();
        Set<String> allvars02 = new HashSet<>(allvars);

        ptsMine.forEach((k, v) -> {
            if (v.size() > 0) {
                if (pts2obj.containsKey(k)) {
                    if (v.size() <= pts2obj.get(k).size() && v.size() > 0) {
                        countVS2obj.add("0");
//                    System.out.println(k + "\t" + v.size() + "\t" + pts2insen.get(k).size() + "\t" + pts2obj.get(k).size());
                    }
                    util.writeFileln(v.size() + "\t" + "\t" + pts2obj.get(k).size() + "\t" + k, "/home/xrbin/IdeaProjects/recommend/logs/result/" + name + "VS2obj");
                }
                if (pts06.containsKey(k)) {
                    if (v.size() > 0) {
                        countSameVar.add("0");
                        if (v.size() <= pts06.get(k).size()) {
                            countVS06.add("0");
//                System.out.println(k + "\t" + v.size() + "\t" + pts2insen.get(k).size() + "\t" + pts2obj.get(k).size());
                        }
                        countTimeMine.add(timeMine.get(k));
                        countTime06.add(time06.get(k));
                        util.writeFileln(v.size() + "\t" + pts06.get(k).size() + "\t" + k, "/home/xrbin/IdeaProjects/recommend/logs/result/" + name + "VS06");
                        //                System.out.println(k + "\t" + timeMine.get(k) + "\t" + time06.get(k) + "\t" + ptsMine.get(k).size() + "\t" + pts06.get(k).size());
                    }
                }
                count.add("0");
                allvars02.remove(k);
            }
//            System.out.println(k + "\t" + v.size() + "\t" + pts2insen.get(k).size() + "\t" + pts2obj.get(k).size());
        });

        pts06.forEach((k, v) -> {
            if (v.size() > 0) {
                count06.add("0");
            }
        });

        System.out.println( allvars.size() + "\t" + count06.size() + "\t" + count.size() + "\t" + countSameVar.size() + "\t" + countVS06.size() + "\t" + countVS2obj.size()
                + "\t" + (count.size() - countSameVar.size()) + "\t" + (count06.size() - countSameVar.size()) + "\t" + sum(countTimeMine) + "\t" + sum(countTime06) + "\t" + name + "\t");
        util.writeFileln( name + "," + allvars.size() + "," + count06.size() + "," + count.size() + "," + countSameVar.size()
                + "," + countVS06.size() + "," + countVS2obj.size() + "," + (count.size() - countSameVar.size()) + "," + (count06.size() - countSameVar.size()) + ","
                + sum(countTimeMine) + "," + sum(countTime06), "/home/xrbin/IdeaProjects/recommend/logs/result/result" + ".csv");

//        System.out.println(count.size() + "\t" + name);
//        System.out.println(count06.size() + "\t" + name);
//        System.out.println(countVS2obj.size() + "\t" + name);
//        System.out.println(countVS06.size() + "\t" + name);
//        System.out.println(dependSizeMine.size() + "\t" + name);

//        for (String s : pts06.keySet()) {
//            if (ptsMine.containsKey(s)) {
//                System.out.println(s + "\t" + ptsMine.get(s).size() + "\t" + pts06.get(s).size());
//            }
//            else {
//                System.out.println("--------------\t" + s);
//            }
//        }
//        System.out.println("--------------------------------------------------------------");
//        for (String s : pts06.keySet()) {
//            if (ptsMine.containsKey(s) && ptsMine.get(s).size() == 0) {
//                System.out.println(s + "\t" + ptsMine.get(s).size() + "\t" + pts06.get(s).size() + "\t" + dependSizeMine.get(s));
//            }
//        }
    }

    public static void varCanCompare2(String name) {
        Set<String> allvars = new HashSet<>();
        Map<String, Set<String>> pts06 = new HashMap<>();
        Map<String, Set<String>> ptsMine = new HashMap<>();
        Map<String, Set<String>> ptsMine00 = new HashMap<>();
        Map<String, Integer> dependSizeMine = new HashMap<>();
        Map<String, Integer> timeMine = new HashMap<>();
        Map<String, Integer> time06 = new HashMap<>();

        String suffixName = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/task01/" + name + "/" + name + "CSOptSummary/";
//        String suffixName = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/task00/" + name + "/" + name + "/";

        try (
                FileReader reader = new FileReader(suffixName + "/AllVar");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                allvars.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader(suffixName + "/DependTreeSizeSmall");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                dependSizeMine.put(line.split("\t")[0], Integer.parseInt(line.split("\t")[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader(suffixName + "/TimeAll");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                timeMine.put(line.split("\t")[0], Integer.parseInt(line.split("\t")[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/MyDB/logs/" + name + "/" + name + "Task02/varTimeAll");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                time06.put(line.split("\t")[0], Integer.parseInt(line.split("\t")[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/MyDB/logs/" + name + "/" + name + "Task02/allResult");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            String var = "";
            while ((line = br.readLine()) != null) {
                try {
                    if (!line.startsWith("\t")) {
                        var = line.split("\t")[3];
                        pts06.computeIfAbsent(var, k -> new HashSet<>());
                    }
                    else {
                        if (line.contains("AllocNode") && !line.contains("null")) {
//                            System.out.println(line);
                            String insenO = line.substring(line.indexOf("AllocNode"), line.indexOf(">,") + 2);
                            pts06.computeIfAbsent(var, k -> new HashSet<>()).add(insenO.replace("\t", ""));
                        }
                    }
                } catch (Exception e) {
                    System.err.println(line);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader(suffixName + "/AllResult");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            String var = "";
            while ((line = br.readLine()) != null) {
                try {
                    if (!line.startsWith("\t")) {
                        var = line;
                        ptsMine.computeIfAbsent(line, k -> new HashSet<>());
                    }
                    else {
                        if (line.contains("(AllocNode") && line.contains(",")) {
                            ptsMine.computeIfAbsent(var, k -> new HashSet<>()).add(line.substring(line.indexOf("(") + 1, line.indexOf(",")));
                        }
                        else {
//                            System.out.println("!(line.contains(\"(\") && line.contains(\",\"))" + line);
                        }
                        ptsMine00.computeIfAbsent(var, k -> new HashSet<>()).add(line);
                    }
                } catch (Exception e) {
                    System.err.println(line);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> count = new ArrayList<>();
        List<Integer> countTimeMine = new ArrayList<>();
        List<Integer> countTime06 = new ArrayList<>();
        List<String> countSameVar = new ArrayList<>();
        List<String> countVS06 = new ArrayList<>();
        List<String> count06 = new ArrayList<>();
        Set<String> allvars02 = new HashSet<>(allvars);

        ptsMine.forEach((k, v) -> {
            if (v.size() > 0) {
                if (pts06.containsKey(k)) {
                    countSameVar.add("0");
                    if (v.size() <= pts06.get(k).size()) {
                        countVS06.add("0");
//                System.out.println(k + "\t" + v.size() + "\t" + pts2insen.get(k).size() + "\t" + pts2obj.get(k).size());
                    }
                    countTimeMine.add(timeMine.get(k));
                    countTime06.add(time06.get(k));
                    util.writeFileln(v.size() + "\t" + pts06.get(k).size() + "\t" + k, "/home/xrbin/IdeaProjects/recommend/logs/result/" + name + "VS06");
                    //                System.out.println(k + "\t" + timeMine.get(k) + "\t" + time06.get(k) + "\t" + ptsMine.get(k).size() + "\t" + pts06.get(k).size());
                }
//            System.out.println(k + "\t" + v.size() + "\t" + pts2insen.get(k).size() + "\t" + pts2obj.get(k).size());
                count.add("0");
                allvars02.remove(k);
            }
        });

        pts06.forEach((k, v) -> {
            if (v.size() > 0) {
                count06.add("0");
            }
        });

        System.out.println( name + "," + allvars.size() + "," + count06.size() + "," + count.size() + "," + countSameVar.size() + "," + countVS06.size()
                + "," + (count.size() - countSameVar.size()) + "," + (count06.size() - countSameVar.size()) + "," + sum(countTimeMine) + "," + sum(countTime06));
        util.writeFileln( name + "," + allvars.size() + "," + count06.size() + "," + count.size() + "," + countSameVar.size()
                + "," + countVS06.size() + "," + (count.size() - countSameVar.size()) + "," + (count06.size() - countSameVar.size()) + ","
                + sum(countTimeMine) + "," + sum(countTime06), "/home/xrbin/IdeaProjects/recommend/logs/result/result" + ".csv");
//        System.out.println(count.size() + "\t" + name);
//        System.out.println(count06.size() + "\t" + name);
//        System.out.println(countVS2obj.size() + "\t" + name);
//        System.out.println(countVS06.size() + "\t" + name);
//        System.out.println(dependSizeMine.size() + "\t" + name);

//        for (String s : pts06.keySet()) {
//            if (ptsMine.containsKey(s)) {
//                System.out.println(s + "\t" + ptsMine.get(s).size() + "\t" + pts06.get(s).size());
//            }
//            else {
//                System.out.println("--------------\t" + s);
//            }
//        }
//        System.out.println("--------------------------------------------------------------");
//        for (String s : pts06.keySet()) {
//            if (ptsMine.containsKey(s) && ptsMine.get(s).size() == 0) {
//                System.out.println(s + "\t" + ptsMine.get(s).size() + "\t" + pts06.get(s).size() + "\t" + dependSizeMine.get(s));
//            }
//        }
    }

    public static int sum(List<Integer> is) {
        int res = 0;
        for (Integer i : is) {
//            System.out.println(i);
            if (i != null) {
                res += i;
            }
        }
        return res;
    }

    public static void dpSize() {
        dpSize("luindex");
        dpSize("lusearch");
        dpSize("chart");
        dpSize("bloat");
        dpSize("eclipse");
        dpSize("pmd");
        dpSize("jython");
    }

    public static void dpSize(String name) {
        int sum = 0;
        try (
//                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/task0101/" + name + "/" + name + "CSOptCache/DependTreeSizeAll");
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/task01/" + name + "/" + name + "CSOptCache/DependTreeSizeAll");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                sum += Integer.parseInt(line.split("\t")[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(sum + "\t\t" + name);
    }

    public static void time06() {
        Vector<String> names = new Vector<>();
        names.add("luindex");
        names.add("lusearch");
        names.add("chart");
        names.add("bloat");
        names.add("eclipse");
        names.add("pmd");
        names.add("jython");
        time06(names);
    }

    public static int timeAll06 = 0;
    public static void time06(Vector<String> names) {
        names.forEach(name -> {
            timeAll06 = 0;
            try (
                    FileReader reader = new FileReader("/home/xrbin/IdeaProjects/MyDB/logs/" + name + "/" + name + "/varTimeAll");
                    BufferedReader br = new BufferedReader(reader)
            ) {
                String line;
                while ((line = br.readLine()) != null) {
                    timeAll06 += Integer.parseInt(line.split("\t")[1]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println(timeAll06);
        });
    }


    public static void timeSimple(String prefix, String taskName) {
        Vector<String> names = new Vector<>();
        names.add("luindex");
        names.add("lusearch");
        names.add("chart");
        names.add("bloat");
        names.add("eclipse");
        names.add("pmd");
        names.add("jython");
        timeSimple(names, prefix, taskName);
    }

    public static int timeAllSimple = 0;
    public static void timeSimple(Vector<String> names, String prefix, String taskName) {
        names.forEach(name -> {
            timeAllSimple = 0;
            try (
                    FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/" + taskName + "/" + name + "/" + name + prefix + "/TimeAll");
                    BufferedReader br = new BufferedReader(reader)
            ) {
                String line;
                while ((line = br.readLine()) != null) {
                    timeAllSimple += Integer.parseInt(line.split("\t")[1]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println(timeAllSimple);
        });
    }

    public static void timeBatch(String kind, String taskName) {
        Vector<String> names = new Vector<>();
        names.add("luindex");
        names.add("lusearch");
        names.add("chart");
        names.add("bloat");
        names.add("eclipse");
        names.add("pmd");
        names.add("jython");
        timeBatch(names, kind, taskName);
//        timeBatch(names, "CSOptCacheBatch");
//        timeBatch(names, "CSOptCacheBatchSummary");
    }

    public static int timeAllBatch = 0;
    public static void timeBatch(Vector<String> names, String prefix, String taskName) {
        names.forEach(name -> {
            timeAllBatch = 0;
            try (
                    FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/" + taskName + "/" + name + "/" + name + prefix + "/Time");
                    BufferedReader br = new BufferedReader(reader)
            ) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.contains("-----------------------------time")) {
                        timeAllBatch += Integer.parseInt(line.split("\t")[1]);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println(timeAllBatch);
        });
    }

    public static void resCompare(String name, String prefix, String task) {
        mkDir(name + prefix);
        Map<String, Set<String>> pts = new HashMap<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/" + task + "/" + name + "/" + name + prefix + "/AllResult");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            String var = "";
            while ((line = br.readLine()) != null) {
                try {
                    if (!line.startsWith("\t")) {
                        var = line;
                        pts.computeIfAbsent(line, k -> new HashSet<>());
                    }
                    else {
                        if (line.contains("(AllocNode") && !line.contains("null")) {
//                            System.out.println(line);
                            String insenO = line.substring(line.indexOf("(AllocNode"), line.indexOf(">,") + 2);
                            pts.computeIfAbsent(var, k -> new HashSet<>()).add(insenO.replace("\t", ""));
                        }
                        else if (line.contains("(AllocNode") && line.contains("null")) {
//                            System.out.println(line);
                            String insenO = line.substring(line.indexOf("(AllocNode"), line.indexOf("null"));
                            pts.computeIfAbsent(var, k -> new HashSet<>()).add(insenO.replace("\t", ""));
                        }
                        else {
                            pts.computeIfAbsent(var, k -> new HashSet<>()).add(line.replace("\t", ""));
                        }
                    }
                } catch (Exception e) {
                    System.err.println(line);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Set<String>> insenPts = new HashMap<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/" + "dacapoCiPts/" + name + "Insen");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            String var = "";
            while ((line = br.readLine()) != null) {
                try {
                    if (!line.startsWith("\t")) {
                        var = line;
                        insenPts.computeIfAbsent(line, k -> new HashSet<>());
                    }
                    else {
                        insenPts.computeIfAbsent(var, k -> new HashSet<>()).add(line.replace("\t", ""));
                    }
                } catch (Exception e) {
                    System.err.println(line);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

//        Map<String, Set<String>> ptsDB = new HashMap<>();
//        try (
//                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/MyDB/logs/" + task + "/" + name + "/" + name + "1/allResult");
//                BufferedReader br = new BufferedReader(reader)
//        ) {
//            String line;
//            String var = "";
//            while ((line = br.readLine()) != null) {
//                try {
//                    if (!line.startsWith("\t")) {
//                        String var_origin = line.split("\t")[3];
//                        var = var_origin.split(" =")[0];
//                        ptsDB.computeIfAbsent(var, k -> new HashSet<>());
//                    }
//                    else {
//                        ptsDB.computeIfAbsent(var, k -> new HashSet<>()).add(line.replace("\t", ""));
//                    }
//                } catch (Exception e) {
//                    System.err.println(line);
//                    e.printStackTrace();
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        Set<String> sameVars = new HashSet<>();
        pts.keySet().forEach(var -> {
            if (insenPts.containsKey(var)) {
                sameVars.add(var);
            }
        });
//        System.out.println(name + ": " + sameVars.size());

        sameVars.forEach(var -> {
            util.writeFilelnWithPrefix(pts.get(var).size() + "\t" + insenPts.get(var).size() + "\t" + (insenPts.get(var).size() - pts.get(var).size()) + "\t" + var,
                    "onDemand/" + name + prefix + "/"  + name + "_resCompare");
            util.writeFilelnWithPrefix(pts.get(var).size() + "\t" + insenPts.get(var).size() + "\t" + (insenPts.get(var).size() - pts.get(var).size()) + "\t" + var,
                    "onDemand/" + name + prefix + "/"  + name + "_resCompareDetail");
            pts.get(var).forEach(o -> util.writeFilelnWithPrefix("\t" + o, "onDemand/" + name + prefix + "/"  + name + "_resCompareDetail"));
            insenPts.get(var).forEach(o -> util.writeFilelnWithPrefix("\t" + "\t" + o, "onDemand/" + name + prefix + "/"  + name + "_resCompareDetail"));
            if (pts.get(var).size() == 0 && insenPts.get(var).size() > 0) {
                util.writeFilelnWithPrefix(var + "\t" + insenPts.get(var).size(), "onDemand/" + name + prefix + "/" + name + "_noResVar");
                insenPts.get(var).forEach(o -> util.writeFilelnWithPrefix("\t" + o, "onDemand/" + name + prefix + "/"  + name + "_noResVar"));
            }
            if (pts.get(var).size() == 0) {
                util.writeFilelnWithPrefix(var + "\t" + insenPts.get(var).size(), "onDemand/" + name + prefix + "/" + name + "_allNoResVar");
            }
        });

        int resVar06 = 0;
        int resVarMine = 0;

        for (String s : pts.keySet()) {
            if (pts.get(s).size() > 0) {
                resVarMine++;
            }
        }
//        for (String s : ptsDB.keySet()) {
//            if (ptsDB.get(s).size() > 0) {
//                resVar06++;
//            }
//        }

//        Set<String> sameVars02 = new HashSet<>();
//        pts.keySet().forEach(var -> {
//            if (ptsDB.containsKey(var)) {
//                sameVars02.add(var);
//            }
//        });
//        System.out.println(name + ": " + sameVars02.size());

//        sameVars02.forEach(var -> {
//            util.writeFilelnWithPrefix(pts.get(var).size() + "\t" + ptsDB.get(var).size() + "\t" + (ptsDB.get(var).size() - pts.get(var).size()) + "\t" + var,
//                    "onDemand/" + name + "/"  + name + "_resCompareDB");
//            util.writeFilelnWithPrefix(pts.get(var).size() + "\t" + ptsDB.get(var).size() + "\t" + (ptsDB.get(var).size() - pts.get(var).size()) + "\t" + var,
//                    "onDemand/" + name + "/"  + name + "_resCompareDetailDB");
//            pts.get(var).forEach(o -> util.writeFilelnWithPrefix("\t" + o, "onDemand/" + name + "/"  + name + "_resCompareDetailDB"));
//            ptsDB.get(var).forEach(o -> util.writeFilelnWithPrefix("\t" + "\t" + o, "onDemand/" + name + "/"  + name + "_resCompareDetailDB"));
//            if (pts.get(var).size() == 0 && ptsDB.get(var).size() > 0) {
//                util.writeFilelnWithPrefix(var + "\t" + ptsDB.get(var).size(), "onDemand/" + name + "/" + name + "_noResVarDB");
//                ptsDB.get(var).forEach(o -> util.writeFilelnWithPrefix("\t" + o, "onDemand/" + name + "/"  + name + "_noResVarDB"));
//            }
//            if (pts.get(var).size() == 0) {
//                util.writeFilelnWithPrefix(var + "\t" + ptsDB.get(var).size(), "onDemand/" + name + "/" + name + "_allNoResVarDB");
//            }
//        });

    }

    public static void varCanbatch(String kind, String taskName) {
        Vector<String> names = new Vector<>();
        names.add("luindex");
        names.add("lusearch");
        names.add("chart");
        names.add("bloat");
        names.add("eclipse");
        names.add("pmd");
        names.add("jython");
        varCanbatch(names, kind, taskName);
//        varCanbatch(names, "CSOptCacheBatch");
//        varCanbatch(names, "CSOptCacheBatchSummary");
    }

    public static void varCanbatch(Vector<String> names, String prefix, String taskName) {
        names.forEach(name -> {
            mkDir(name + prefix);
            Map<String, Set<String>> pts = new HashMap<>();
            try (
                    FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/" + taskName + "/" + name + "/" + name + prefix + "/AllResult");
                    BufferedReader br = new BufferedReader(reader)
            ) {
                String line;
                String var = "";
                while ((line = br.readLine()) != null) {
                    try {
                        if (!line.startsWith("\t")) {
                            var = line;
                            pts.computeIfAbsent(line, k -> new HashSet<>());
                        }
                        else {
                            if (line.contains("(AllocNode") && !line.contains("null")) {
//                            System.out.println(line);
                                String insenO = line.substring(line.indexOf("(AllocNode"), line.indexOf(">,") + 2);
                                pts.computeIfAbsent(var, k -> new HashSet<>()).add(insenO.replace("\t", ""));
                            }
                            else if (line.contains("(AllocNode") && line.contains("null")) {
//                            System.out.println(line);
                                String insenO = line.substring(line.indexOf("(AllocNode"), line.indexOf("null"));
                                pts.computeIfAbsent(var, k -> new HashSet<>()).add(insenO.replace("\t", ""));
                            }
                            else {
                                pts.computeIfAbsent(var, k -> new HashSet<>()).add(line.replace("\t", ""));
                            }
                        }
                    } catch (Exception e) {
                        System.err.println(line);
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            Map<String, Set<String>> insenPts = new HashMap<>();
            try (
                    FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/" + "dacapoCiPts/" + name + "Insen");
                    BufferedReader br = new BufferedReader(reader)
            ) {
                String line;
                String var = "";
                while ((line = br.readLine()) != null) {
                    try {
                        if (!line.startsWith("\t")) {
                            var = line;
                            insenPts.computeIfAbsent(line, k -> new HashSet<>());
                        }
                        else {
                            insenPts.computeIfAbsent(var, k -> new HashSet<>()).add(line.replace("\t", ""));
                        }
                    } catch (Exception e) {
                        System.err.println(line);
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            Set<String> successQueries = new HashSet<>();
            try (
                    FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/" + taskName + "/" + name + "/" + name + prefix + "/successQuery");
                    BufferedReader br = new BufferedReader(reader)
            ) {
                String line;
                while ((line = br.readLine()) != null) {
                    successQueries.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            Set<String> sameVars = new HashSet<>();
            pts.keySet().forEach(var -> {
                if (insenPts.containsKey(var)) {
                    sameVars.add(var);
                }
            });

            sameVars.forEach(var -> {
                if (successQueries.contains(var)) {
                    util.writeFilelnWithPrefix(pts.get(var).size() + "\t" + insenPts.get(var).size() + "\t" + (insenPts.get(var).size() - pts.get(var).size()) + "\t" + var,
                            "onDemand/" + name + prefix + "/" + name + "_resCompare");
                    util.writeFilelnWithPrefix(pts.get(var).size() + "\t" + insenPts.get(var).size() + "\t" + (insenPts.get(var).size() - pts.get(var).size()) + "\t" + var,
                            "onDemand/" + name + prefix + "/" + name + "_resCompareDetail");
                    pts.get(var).forEach(o -> util.writeFilelnWithPrefix("\t" + o, "onDemand/" + name + prefix + "/" + name + "_resCompareDetail"));
                    insenPts.get(var).forEach(o -> util.writeFilelnWithPrefix("\t" + "\t" + o, "onDemand/" + name + prefix + "/" + name + "_resCompareDetail"));
                    if (pts.get(var).size() == 0 && insenPts.get(var).size() > 0) {
                        util.writeFilelnWithPrefix(var + "\t" + insenPts.get(var).size(), "onDemand/" + name + prefix + "/" + name + "_noResVar");
                        insenPts.get(var).forEach(o -> util.writeFilelnWithPrefix("\t" + o, "onDemand/" + name + prefix + "/" + name + "_noResVar"));
                    }
                    if (pts.get(var).size() == 0) {
                        util.writeFilelnWithPrefix(var + "\t" + insenPts.get(var).size(), "onDemand/" + name + prefix + "/" + name + "_allNoResVar");
                    }
                }
            });

            int resVar06 = 0;
            int resVarMine = 0;

            for (String s : pts.keySet()) {
                if (pts.get(s).size() > 0) {
//                    if (name.equals("bloat")) {
//                        System.out.println(s);
//                    }
                    resVarMine++;
                }
            }
            System.out.println(resVarMine);
        });
    }

    public static void batchResCompare() {
        String prefix = "CSOptCacheBatch";
//        prefix = "CSOptCacheBatch";
        batchResCompare("luindex", "CSOptCacheBatch");
        batchResCompare("lusearch", "CSOptCacheBatch");
        batchResCompare("bloat", "CSOptCacheBatch");
        batchResCompare("pmd", "CSOptCacheBatch");
        batchResCompare("eclipse", "CSOptCacheBatch");
        batchResCompare("chart", "CSOptCacheBatch");
        batchResCompare("jython", "CSOptCacheBatch");
    }

    public static void batchResCompare(String name, String prefix) {
        mkDir(name + prefix);
        Map<String, Set<String>> pts = new HashMap<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/" + name + "/" + name + prefix + "/AllResult");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            String var = "";
            while ((line = br.readLine()) != null) {
                try {
                    if (!line.startsWith("\t")) {
                        var = line;
                        pts.computeIfAbsent(line, k -> new HashSet<>());
                    }
                    else {
                        if (line.contains("(AllocNode") && !line.contains("null")) {
//                            System.out.println(line);
                            String insenO = line.substring(line.indexOf("(AllocNode"), line.indexOf(">,") + 2);
                            pts.computeIfAbsent(var, k -> new HashSet<>()).add(insenO.replace("\t", ""));
                        }
                        else if (line.contains("(AllocNode") && line.contains("null")) {
//                            System.out.println(line);
                            String insenO = line.substring(line.indexOf("(AllocNode"), line.indexOf("null"));
                            pts.computeIfAbsent(var, k -> new HashSet<>()).add(insenO.replace("\t", ""));
                        }
                        else {
                            pts.computeIfAbsent(var, k -> new HashSet<>()).add(line.replace("\t", ""));
                        }
                    }
                } catch (Exception e) {
                    System.err.println(line);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Set<String>> insenPts = new HashMap<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/" + "dacapoCiPts/" + name + "Insen");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            String var = "";
            while ((line = br.readLine()) != null) {
                try {
                    if (!line.startsWith("\t")) {
                        var = line;
                        insenPts.computeIfAbsent(line, k -> new HashSet<>());
                    }
                    else {
                        insenPts.computeIfAbsent(var, k -> new HashSet<>()).add(line.replace("\t", ""));
                    }
                } catch (Exception e) {
                    System.err.println(line);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Set<String> successQueries = new HashSet<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/" + name + "/" + name + prefix + "/successQuery");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                successQueries.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Set<String> sameVars = new HashSet<>();
        pts.keySet().forEach(var -> {
            if (insenPts.containsKey(var)) {
                sameVars.add(var);
            }
        });

        sameVars.forEach(var -> {
            if (successQueries.contains(var)) {
                util.writeFilelnWithPrefix(pts.get(var).size() + "\t" + insenPts.get(var).size() + "\t" + (insenPts.get(var).size() - pts.get(var).size()) + "\t" + var,
                        "onDemand/" + name + prefix + "/" + name + "_resCompare");
                util.writeFilelnWithPrefix(pts.get(var).size() + "\t" + insenPts.get(var).size() + "\t" + (insenPts.get(var).size() - pts.get(var).size()) + "\t" + var,
                        "onDemand/" + name + prefix + "/" + name + "_resCompareDetail");
                pts.get(var).forEach(o -> util.writeFilelnWithPrefix("\t" + o, "onDemand/" + name + prefix + "/" + name + "_resCompareDetail"));
                insenPts.get(var).forEach(o -> util.writeFilelnWithPrefix("\t" + "\t" + o, "onDemand/" + name + prefix + "/" + name + "_resCompareDetail"));
                if (pts.get(var).size() == 0 && insenPts.get(var).size() > 0) {
                    util.writeFilelnWithPrefix(var + "\t" + insenPts.get(var).size(), "onDemand/" + name + prefix + "/" + name + "_noResVar");
                    insenPts.get(var).forEach(o -> util.writeFilelnWithPrefix("\t" + o, "onDemand/" + name + prefix + "/" + name + "_noResVar"));
                }
                if (pts.get(var).size() == 0) {
                    util.writeFilelnWithPrefix(var + "\t" + insenPts.get(var).size(), "onDemand/" + name + prefix + "/" + name + "_allNoResVar");
                }
            }
        });

        int resVar06 = 0;
        int resVarMine = 0;

        for (String s : pts.keySet()) {
            if (pts.get(s).size() > 0) {
                resVarMine++;
            }
        }
        System.out.println(resVarMine);
    }

    public static void timeCompare02(String name, String prefix) {
        String file1 = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/" + name + "/" + name + prefix + "/TimeAll";

        Map<String, Integer> timeMap = new HashMap<>();
        try (
                FileReader reader = new FileReader(file1);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            int integer;
            while ((line = br.readLine()) != null) {
                integer = Integer.parseInt(line.split("\t")[1]);
                timeMap.put(line.split("\t")[0], integer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Integer> timeMapDB = new HashMap<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/MyDB/logs/" + name + "/" + name + "1/varTimeAll");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    timeMapDB.put(line.split("\t")[0], Integer.parseInt(line.split("\t")[1]));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        int itTime = 0;
        int myTime = 0;

        Set<String> sameVars = new HashSet<>();
        for (String s : timeMapDB.keySet()) {
            if (timeMap.containsKey(s)) {
                sameVars.add(s);
            }
        }

        for (String s : sameVars) {
            myTime += timeMap.get(s);
            itTime += timeMapDB.get(s);
            util.writeFilelnWithPrefix(timeMap.get(s) + "\t" + timeMapDB.get(s) + "\t" + s, "onDemand/" + name + prefix + "/"  + name + "_timeCompare");
        }

//        for (String s : timeMap02.keySet()) {
//            myTime += timeMap02.get(s);
//        }

//        System.out.println(timeMap.keySet().size() + "\t" + timeMapDB.keySet().size() + "\t" + sameVars.size() + ", itsTime = " + itTime + "ms, myTime = " + myTime + "ms\t" + name);
        System.out.println("itsTime = " + itTime + ", myTime = " + myTime);
    }

    public static void t() {
        ArrayList<String> a = new ArrayList<>();
        a.clone();
    }

    public static void dacapo_batch() {
        dacapo_batch("luindex");
        dacapo_batch("lusearch");
        dacapo_batch("bloat");
        dacapo_batch("pmd");
        dacapo_batch("chart");
        dacapo_batch("eclipse");
        dacapo_batch("jython");
//        dacapo_batch("hsqldb");
//        dacapo_batch("xalan");
//        dacapo_batch("antlr");
//        dacapo_batch("fop");
    }
    public static Map<Integer, Set<String>> dacapoAllVars;
    public static Map<Integer, Set<String>> dacapoAllVarsSelect;
    public static void dacapo_batch(String testName) {
        util.rmFile("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/allvar/" + testName + "03");
        dacapoAllVars = new HashMap<>();
        Set<String> vars = new HashSet<>();
        Integer integer = 0;
        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/allvar/" + testName + "02");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("-----------------------------")) {
                    integer++;
                    dacapoAllVars.computeIfAbsent(integer, k -> new HashSet<>());
                }
                else {
                    vars.add(line);
                    dacapoAllVars.computeIfAbsent(integer, k -> new HashSet<>()).add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("" + testName + " var size: " + vars.size());
//        if (true) return;

        int i = 30;
        int size = 0;
        int batchSize = 0;
        for (; i > 0; i--) {
            size = 0;
            batchSize = 0;
            dacapoAllVarsSelect = new HashMap<>();
            for (Integer integer1 : dacapoAllVars.keySet()) {
                if (dacapoAllVars.get(integer1).size() >= i && dacapoAllVars.get(integer1).size() <= 50) {
                    if (!dacapoAllVarsSelect.containsKey(integer1)) {
                        batchSize++;
                        size += dacapoAllVars.get(integer1).size();
                        dacapoAllVarsSelect.computeIfAbsent(integer1, k -> new HashSet<>(dacapoAllVars.get(integer1)));
                    }
                }
            }
            if (size > 200) {
                break;
            }
        }
        System.out.println("" + testName + ": " + i + ", batchSize = " + batchSize + ", size = " + size);

        dacapoAllVarsSelect.forEach((k, v) -> {
            v.forEach(var -> util.writeFileln(var, "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/allvar/" + testName + "03"));
            util.writeFileln("------------------------------------------------", "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/allvar/" + testName + "03");
        });
    }

    public static String varNameDoopToShimple(String varName) {
//        System.out.println(varName);
        if (varName.contains("$$A_")) {
            varName = varName.split("\\$\\$A_")[0] + varName.split("\\$\\$A_")[1];
        }
        if (varName.contains("#_")) {
            varName = varName.split("#_")[0];
        }
        return varName;
    }

    public static void mkDir(String suffix) {
        String dirStr = "/home/xrbin/IdeaProjects/recommend/logs/onDemand/" + suffix;
        File directory = new File(dirStr);

        if (directory.exists()) {
//            System.err.println("mk exists");
            for (File file : Objects.requireNonNull(directory.listFiles())) {
                if (!file.isDirectory()) {
                    file.delete();
                }
            }
            if (directory.delete()) {
//                System.err.println("mk delete");
            }
        }

        if (!directory.mkdir()) {
//            System.err.println("mk error");
        }
    }
}
