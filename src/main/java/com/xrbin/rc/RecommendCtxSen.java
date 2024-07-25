package com.xrbin.rc;

import com.xrbin.ddpt.IntroValueFlowGraph;
import com.xrbin.ddpt.Main;
import com.xrbin.ddpt.model.*;
import com.xrbin.ddpt.utils;
import com.xrbin.rc.model.SortCtxSen;
import com.xrbin.utils.Statistics;
import com.xrbin.utils.util;
import soot.*;
import soot.jimple.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class RecommendCtxSen {
    public static DatabaseManager database = DatabaseManager.getInstance();

    public static Statistics stat = new Statistics();
    public static Vector<String> rcVar = new Vector<>();
    public static HashSet<String> rcMethod = new HashSet<>();

//    public static String projectName2Obj = "DDPT-2obj";
//    public static String projectNameInsen = "DDPT-insen";
//    public static String projectName2Obj = "paoding-2obj";
//    public static String projectNameInsen = "paoding-insen";
//    public static String projectName2Obj = "jieba-2obj";
//    public static String projectNameInsen = "jieba-insen";
//    public static String projectName2Obj = "all-2obj";
//    public static String projectNameInsen = "all-insen";

    //    public static String projectName2Obj = "fop-2obj";
//    public static String projectNameInsen = "fop-insen";
//    public static String projectName2Obj = "bloat-2obj";
//    public static String projectNameInsen = "bloat-insen";
//    public static String projectName2Obj = "antlr-2obj";
//    public static String projectNameInsen = "antlr-insen";
    public static String projectName2Obj = "ftp-2obj";
    public static String projectNameInsen = "ftp-insen";
//    public static String projectName2Obj = "xalan-2obj";
//    public static String projectNameInsen = "xalan-insen";

    public static String path = "/home/xrbin/doophome/out/" + projectNameInsen + "/database/";

    public static boolean flag_main = true;

    static {
        try {
            Runtime.getRuntime().exec("rm logs/argDiff");
        } catch (Exception e) {
            System.err.print("");
        }
    }

    public static void main(String[] args) {
        utils.DATABASE = "/home/xrbin/doophome/out/" + projectNameInsen + "/database/";
        utils.MAINCLASS = "com.guichaguri.minimalftp.CustomServer";
        utils.JARPTAH = "/home/xrbin/Desktop/MinimalFTP-master/build/libs/MinimalFTP-1.0.6.jar";

//        utils.JARPTAH = "/home/xrbin/Java_Project/fop/build/fop.jar";
//        utils.MAINCLASS = "org.apache.fop.cli.Main";
//        utils.DATABASE = "/home/xrbin/doophome/out/" + projectNameInsen + "/database/";

//        utils.JARPTAH = "/home/xrbin/Java_Project/jieba-analysis-master/target/jieba-analysis-1.0.3-SNAPSHOT.jar";
//        utils.MAINCLASS = "com.qianxinyao.analysis.jieba.keyword.TFIDFAnalyzer";

//        utils.JARPTAH = "/home/xrbin/Java_Project/jar/rapidoid-commons-5.5.5.jar";
//        utils.JARPTAH = "/home/xrbin/Java_Project/DDPT_test/build/libs/all.jar";

//        utils.JARPTAH = "/home/xrbin/doop-benchmarks/dacapo-2006/bloat.jar";
//        utils.JARPTAH = "/home/xrbin/doop-benchmarks/dacapo-2006/fop.jar";
//        utils.JARPTAH = "/home/xrbin/doop-benchmarks/dacapo-2006/antlr.jar";
//        utils.JARPTAH = "/home/xrbin/doop-benchmarks/dacapo-2006/xalan.jar";
//        utils.MAINCLASS = "Harness";


//        Main.run();

//        rc1();
//        rc2();
//        rc2plus();
//        rc3();
//        rc3plus();
//        rc3plusSingleVar();
//        rc4();
//        util.equalDir("/home/rrong/Desktop/d1/", "/home/rrong/Desktop/d2/");
//        util.equalDir("/home/rrong/Desktop/d3/", "/home/rrong/Desktop/d2/");

//        func6();
//        func7();
//        func8();
//        func9();
//        func10();
//        func11();
//        func12();

//        SortCtxSen.sort1("1");
//        SortCtxSen.sort1("2");
//        SortCtxSen.sort1("3");

//        util1(1);
//        util1(2);
//        util1(3);

//        func1();

//        DatabaseManager.getInstance().readData();
//        falseAlarmSize("/home/xrbin/IdeaProjects/recommend/logs/appVar", "/home/xrbin/IdeaProjects/recommend/logs/appVarFalseAlarmSize");
//        falseAlarmSize("/home/xrbin/IdeaProjects/recommend/logs/recommendVar-rc3", "/home/xrbin/IdeaProjects/recommend/logs/recommendVarFalseAlarmSize1");
//        falseAlarmSize("/home/xrbin/IdeaProjects/recommend/logs/appVar", "/home/xrbin/IdeaProjects/recommend/logs/jiebaAppVarFalseAlarmSize1");
//        falseAlarm("/home/xrbin/IdeaProjects/recommend/logs/recommendVar-rc3", "/home/xrbin/IdeaProjects/recommend/logs/recommendVar3FalseAlarm1");
//        falseAlarmSize("/home/xrbin/IdeaProjects/recommend/logs/recommendVar-rc3-all", "/home/xrbin/IdeaProjects/recommend/logs/recommendVar-rc3AllFalseAlarmSize");


//        appVarToFeedbacks();
//        spiltFile();
//        vptSubVpt();
//        SortCtxSen.sort1("");
//        SortCtxSen.sort1("rc");

//        SortCtxSen.sort1("");
//        SortCtxSen.sort2("");
//        init();
//        func2();
//        func3();
//        func4();
//        func13();
//        func13_02();
//        func14();
//        func14_01();
//        func14_antlr();
//        func14_luindex();
//        func15();
//        func16();
//        func17();
//        func18();
//        fileSame();
//        fileDiff02();
//        fileDiffDependSize();
//        fileDiffCsNoResDependSize();
//        findNot();
//        noResult();
//        compareTime();
//        resRight();
//        batchTimeCompare();
//        timeCompare02("luindex");
//        timeCompare02("lusearch");
//        timeCompare02("bloat");

//        try (
//                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/recommend/logs/csSameVpt_jdk6");
//                BufferedReader br = new BufferedReader(reader)
//        ) {
//            Set<String> s = new HashSet<>();
//            String line;
//            while ((line = br.readLine()) != null) {
//                s.add(line.split("\t")[1]);
//            }
//            s.forEach(v -> {
//                util.writeFilelnWithPrefix(v, "csSameVptVar_jdk6");
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        sameVars();
//        dacapo_batch();
//        compareCan();
//        reachableMethod();

//        timeCompare("luindex");
//        timeCompare("lusearch");
//        timeCompare("pmd");
//        timeCompare("bloat");
        timeCompare02("luindex");

//        batchTimeCompare("luindex");
//        batchTimeCompare("lusearch");
//        batchTimeCompare("pmd");
//        batchTimeCompare("bloat");
    }

    public static void init() {

        try {
            Runtime.getRuntime().exec("rm logs/result_score");
            Runtime.getRuntime().exec("rm logs/result_vptsize");
        } catch (Exception e) {
            System.err.print("");
        }

        Vector<String> r1 = new Vector<>();
        Vector<String> r11 = new Vector<>();
        Vector<String> r2 = new Vector<>();
        Vector<String> r21 = new Vector<>();
        Vector<String> r2_sort = new Vector<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/recommend/logs/appVarJieba");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                r1.add(line.split("\t")[0]);
                r11.add(line.split("\t")[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/recommend/logs/recommendVarAndScore-rc");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String num = line.split("\t")[1];
                while (num.length() < 8) {
                    num = "0".concat(num);
                }
                r2_sort.add(num + "\t" + line.split("\t")[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Collections.sort(r2_sort);
        r2_sort.forEach(s -> {
            r2.add(0, s.split("\t")[1]);
            String num = s.split("\t")[0];
            while (num.startsWith("0")) {
                num = num.substring(1);
            }
            r21.add(0, num);
        });

        r2.forEach(r -> {
            if (r1.contains(r)) {
                int ir2 = r2.indexOf(r);
                int ir1 = r1.indexOf(r);
                util.writeFilelnWithPrefix(r + "\t" + ir2 + "\t" + ir1 + "\t" + r11.get(ir1) + "\t" + r21.get(ir2), "result_score");
            }
        });

        r1.forEach(r -> {
            if (r2.contains(r)) {
                int ir2 = r2.indexOf(r);
                int ir1 = r1.indexOf(r);
                util.writeFilelnWithPrefix(r + "\t" + ir2 + "\t" + ir1 + "\t" + r11.get(ir1) + "\t" + r21.get(ir2), "result_vptsize");
            }
        });
    }

    public static void util1(int index) {
        try (
                FileReader reader = new FileReader(utils.DATABASE + "VarPointsTo.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] sa = line.split("\t");
                database.vptInsen.computeIfAbsent(sa[3], k -> new HashSet<>()).add(new CSAllocation(new Allocation(sa[1])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader("/home/xrbin/doophome/out/" + projectName2Obj + "/database/VarPointsTo.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] sa = line.split("\t");
                database.vptInsen.computeIfAbsent(sa[3], k -> new HashSet<>()).add(new CSAllocation(new Allocation(sa[1])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/recommend/logs/rc" + index);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
//                System.out.println(line);
                if (line.split("\t")[1].equals("0")) {
//                    System.out.println(line.split("\t")[0] + "\t" + database.vptInsen.get(line.split("\t")[0]).size());
                    util.writeFilelnWithPrefix(line.split("\t")[0] + "\t" + database.vptInsen.get(line.split("\t")[0]).size(), "nodiff-rc" + index);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void rc1() {
        try {
            Runtime.getRuntime().exec("rm logs/rc1");
            Runtime.getRuntime().exec("rm logs/recommendVar-rc1");
            Runtime.getRuntime().exec("rm logs/recommendVarAndScore-rc1");
        } catch (Exception e) {
            System.err.print("");
        }

        if (flag_main) {
            Main.run();
            flag_main = false;
        }

        int count = 0;
        /*
        对于每个方法，先找到所有的调用点，如果被调用次数大于1（此处考虑上下文敏感，也就是base指向的对象的数量），被认为有可能有误报
        这些方法中的每一个方法的参数，如果参数类型不是基本类型，那么认为这些参数都是有可能出现误报的
         */
        for (String m : database.backCg.keySet()) {
//            if(database.appMethod.contains(m)) {
//                System.out.println(m);
            HashSet<CSAllocation> countBaseObject = new HashSet<>();
            database.backCg.get(m).forEach(methodInvoke -> {
//                    System.out.println("\t" + methodInvoke);
                if (database.vpt2obj.containsKey(database.methodInvokeToBase.get(methodInvoke)))
                    countBaseObject.addAll(database.vpt2obj.get(database.methodInvokeToBase.get(methodInvoke)));
            });
            // 方法被多次调用（对于对象上下文敏感来说，只要base指向的所有对象超过1就算）
            if (countBaseObject.size() > 1) {
//                    System.out.println("\t" + m);
                count++;
                rcMethod.add(m);
            }
//            }
        }
        System.out.println("count = " + count);

        rcMethod.forEach(s -> {
            if (Main.bodys.containsKey(s)) {
                Body b = Main.bodys.get(s);
                b.getParameterRefs().forEach(v -> {
                    if (!utils.isPrimitiveType(v.getType())) {
                        b.getUnits().forEach(u -> {
                            if (u instanceof IdentityStmt && ((IdentityStmt) u).getRightOp().equals(v)) {
//                                insenVpt.keySet().forEach(var -> {
//                                    if((b.getMethod() + "/" + ((IdentityStmt) u).getLeftOp().toString()).equals(utils.processingName(var))) {
//                                        rcVar.add(var);
//                                    }
//                                });
                                if (database.nameToPname.containsKey((b.getMethod() + "/" + ((IdentityStmt) u).getLeftOp().toString()))) {
                                    rcVar.add(database.nameToPname.get((b.getMethod() + "/" + ((IdentityStmt) u).getLeftOp().toString())));
                                }
                            }
                        });
                    }
                });
            }
        });

        rcVar.forEach(s -> {
            util.writeFilelnWithPrefix(s, "recommendVar-rc1");
        });

        util.plnB("rcVar.size() = " + rcVar.size() + "");

        rcVarToFeedbacks("/home/xrbin/doophome/NVPT/NVPT1", "1");
        SortCtxSen.sort1("1");
    }

    public static void rc2() {
        try {
            Runtime.getRuntime().exec("rm logs/rc2");
            Runtime.getRuntime().exec("rm logs/recommendVar-rc2");
            Runtime.getRuntime().exec("rm logs/recommendVarAndScore-rc2");
        } catch (Exception e) {
            System.err.print("");
        }

        if (flag_main) {
            Main.run();
            flag_main = false;
        }
        /*
            对于每个store，其所在的方法如果有多个上下文（根据callgraph和调用的点的base指向的对象的个数）
         */
//        for (Field key : database.storeToMethod.keySet()) {
//            countInvoke = 0;
//            String m = database.storeToMethod.get(key);
//            if(database.cgEdge.containsKey(m)) {
//                database.cgEdge.get(m).forEach(actualM -> {
//                    if (database.vptInsen.containsKey(database.methodInvokeToBase.get(actualM))) {
//                        countInvoke += database.vptInsen.get(database.methodInvokeToBase.get(actualM)).size();
//                    }
//                });
//                if (countInvoke > 1) {
////                    for (Field f : load.keySet()) {
//                        if (load.containsKey(key)) {
//                            rcVar.addAll(load.get(key));
//                        }
////                    }
//                }
//            }
//        }

        database.store.forEach(s -> {
            countInvokeRc4 = 0;
            HashSet<CSAllocation> allocations = new HashSet<>();
            String m = s.getMethod();
            if (database.backCg.containsKey(m)) {
                database.backCg.get(m).forEach(actualM -> {
                    if (database.vptInsen.containsKey(database.methodInvokeToBase.get(actualM))) {
                        allocations.addAll(database.vptInsen.get(database.methodInvokeToBase.get(actualM)));
                    }
                });
                if (allocations.size() > 1) {

                    database.backCg.get(m).forEach(methodInvoke -> {
                        HashMap<String, String> ap = database.actualParam.get(methodInvoke);
                        String base = database.methodInvokeToBase.get(methodInvoke);
                        HashSet<CSAllocation> thisVpt;
                        Vector<HashSet<CSAllocation>> argsVpt = new Vector<>();
                        if (database.vptInsen.containsKey(base)) {
                            thisVpt = new HashSet<>(database.vptInsen.get(base));
                        }


//                        MethodReturnVpt mrv = new MethodReturnVpt(Main.wpCFG.getBodys().get(m).getMethod(), Main.ivfg, Main.wpCFG, )
                    });

                    HashSet<CSAllocation> Os = database.vptInsen.get(s.getBase());
                    if (Os != null) {
                        for (CSAllocation o : Os) {
                            Field f = new Field(o, s.getSignature());
                            if (database.load.containsKey(f)) {
//                                if (s.base.contains("this")) {
                                rcVar.addAll(database.load.get(f));
//                                }
                            }
                        }
                    }
                }
            }

        });

        HashSet<String> rcVarSet = new HashSet<>(rcVar);
        rcVarSet.forEach(s -> util.writeFilelnWithPrefix(s, "recommendVar-rc2"));
        util.plnB("rcVarSet.size() = " + rcVarSet.size() + "");

        falseAlarmSize("/home/xrbin/IdeaProjects/recommend/logs/recommendVar-rc2", "/home/xrbin/IdeaProjects/recommend/logs/recommendVar-rc2FalseAlarmSize");
//        rcVarToFeedbacks("/home/xrbin/doophome/NVPT/NVPT2", "2");
//        SortCtxSen.sort1("2");
    }

    public static void rc2plus() {
        try {
            Runtime.getRuntime().exec("rm logs/rc2");
            Runtime.getRuntime().exec("rm logs/recommendVar-rc2");
            Runtime.getRuntime().exec("rm logs/recommendVarAndScore-rc2");
        } catch (Exception e) {
            System.err.print("");
        }

        if (flag_main) {
            Main.run();
            flag_main = false;
        }
        /*
            对于每个store，其所在的方法如果有多个上下文（根据callgraph和调用的点的base指向的对象的个数）
         */

        Main.wpCFG.getUnits().forEach(unit -> {
            if (unit instanceof AssignStmt && ((AssignStmt) unit).getLeftOp() instanceof InstanceFieldRef) {
                System.out.println(unit);

                Value fieldBaseValue = ((InstanceFieldRef) ((AssignStmt) unit).getLeftOp()).getBase();
                Value fromValue = ((AssignStmt) unit).getRightOp();
                String fieldBase = Main.wpCFG.getMethodOf(unit) + "/" + fieldBaseValue.toString();
                String from = Main.wpCFG.getMethodOf(unit) + "/" + fromValue.toString();

                String fieldSignture = ((InstanceFieldRef) ((AssignStmt) unit).getLeftOp()).getField().toString();

                System.out.println("\t" + fieldBase + from);
                if (database.nameToPname.containsKey(fieldBase) && database.nameToPname.containsKey(from)) {
                    fieldBase = database.nameToPname.get(fieldBase);
                    from = database.nameToPname.get(from);
                }
                if (!database.vptInsen.containsKey(fieldBase) || !database.vptInsen.containsKey(from)) {
                    return;
                }

                HashSet<CSAllocation> fieldBaseVpt = database.vptInsen.get(fieldBase);
                HashSet<CSAllocation> fromVpt = database.vptInsen.get(from);

                HashSet<CSAllocation> allocations = new HashSet<>();
                String m = Main.wpCFG.getMethodOf(unit).toString();
                if (database.backCg.containsKey(m)) {
                    System.out.println("\t\t" + m);
                    database.backCg.get(m).forEach(mi -> {
                        if (database.vptInsen.containsKey(database.methodInvokeToBase.get(mi))) {
                            allocations.addAll(database.vptInsen.get(database.methodInvokeToBase.get(mi)));
                        }
                    });
                    if (allocations.size() > 1) {
                        database.backCg.get(m).forEach(methodInvoke -> {
                            HashMap<String, String> ap = database.actualParam.get(methodInvoke);
                            String base = database.methodInvokeToBase.get(methodInvoke);

                            Vector<HashSet<CSAllocation>> argsVpt = new Vector<>();
                            if (database.nameToPname.containsKey(base) && database.vptInsen.containsKey(database.nameToPname.get(base))) {
                                HashSet<CSAllocation> thisVpt = new HashSet<>(database.vptInsen.get(database.nameToPname.get(base)));
                                if (ap != null) {
                                    ap.forEach((num, var) -> {
                                        HashSet<CSAllocation> temp = new HashSet<>();
                                        argsVpt.add(temp);
                                        if (database.vptInsen.containsKey(var)) {
                                            temp.addAll(database.vptInsen.get(var));
                                        }
                                    });
                                }
                                MethodReturnVpt mrv = new MethodReturnVpt(Main.wpCFG.getBodys().get(m).getMethod(), Main.ivfg, Main.wpCFG, thisVpt, argsVpt);
                                mrv.analysis();
                                HashSet<CSAllocation> fieldBaseVptSingle = mrv.getVarVptSet(fieldBaseValue);
                                HashSet<CSAllocation> fromVptsingle = mrv.getVarVptSet(fromValue);
                                System.out.println(fieldBaseVptSingle.size() + "\t" + fromVptsingle.size());

                                HashSet<CSAllocation> temp1 = new HashSet<>(fieldBaseVpt);
                                temp1.removeAll(fieldBaseVptSingle);
                                HashSet<CSAllocation> temp2 = new HashSet<>(fromVpt);
                                temp2.removeAll(fromVptsingle);

                                if (temp1.size() > 0 && temp2.size() > 0) {
                                    HashSet<CSAllocation> Os = database.vptInsen.get(database.nameToPname.get(Main.wpCFG.getMethodOf(unit) + "/" + fieldBaseValue.toString()));
                                    if (Os != null) {
                                        for (CSAllocation o : Os) {
                                            Field f = new Field(o, fieldSignture);
                                            if (database.load.containsKey(f)) {
//                                if (s.base.contains("this")) {
                                                rcVar.addAll(database.load.get(f));
//                                }
                                            }
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });

        HashSet<String> rcVarSet = new HashSet<>(rcVar);
        rcVarSet.forEach(s -> util.writeFilelnWithPrefix(s, "recommendVar-rc2"));
        util.plnB("rcVarSet.size() = " + rcVarSet.size() + "");

        falseAlarmSize("/home/xrbin/IdeaProjects/recommend/logs/recommendVar-rc2", "/home/xrbin/IdeaProjects/recommend/logs/recommendVar-rc2FalseAlarmSize");
//        rcVarToFeedbacks("/home/xrbin/doophome/NVPT/NVPT2", "2");
//        SortCtxSen.sort1("2");
    }

    public static void rc3() {
        try {
            Runtime.getRuntime().exec("rm logs/rc3");
            Runtime.getRuntime().exec("rm logs/recommendVar-rc3");
            Runtime.getRuntime().exec("rm logs/recommendVarAndScore-rc3");
        } catch (Exception e) {
            System.err.print("");
        }

        if (flag_main) {
            Main.run();
            flag_main = false;
        }
        /*
        对于所有被多次调用的方法的调用点接收返回值的变量 -> a = b.f()中的a
         */

        int count = 0;
        for (String m : database.backCg.keySet()) {
//            if(database.appMethod.contains(m)) {
//                System.out.println(m);
            HashSet<CSAllocation> countBaseObject = new HashSet<>();
            database.backCg.get(m).forEach(methodInvoke -> {
//                System.out.println("\t" + methodInvoke);
//                System.out.println("\t" + database.methodInvokeToBase.get(methodInvoke));
//                System.out.println("\t" + database.vptInsen.containsKey(database.methodInvokeToBase.get(methodInvoke)));
                if (database.vptInsen.containsKey(database.methodInvokeToBase.get(methodInvoke))) {
                    countBaseObject.addAll(database.vptInsen.get(database.methodInvokeToBase.get(methodInvoke)));
                }
            });
            // 方法被多次调用（对于对象上下文敏感来说，只要base指向的所有对象超过1就算）
//            if (countBaseObject.size() > 1) {
//                System.out.println("\t" + m);
            count++;
            database.backCg.get(m).forEach(methodInvoke -> {
                if (database.vptInsen.containsKey(database.methodInvokeToBase.get(methodInvoke))) {
                    String returnVar = database.assignReturnValue.get(methodInvoke);
                    if (returnVar != null) {
                        returnVar = returnVar.split("/")[0];
                        if (database.appMethod.contains(returnVar)) {
//                                if (invokeArgDiffVptSize(m, methodInvoke) && invokeThisDiffVptSize(m, methodInvoke)) {
                            rcVar.add(database.assignReturnValue.get(methodInvoke));
//                                }
                        }
                    }
                }
            });
//            }
//            }
        }
        System.out.println("count = " + count);
        util.plnB("rcVar.size() = " + rcVar.size() + "");

//        rcVar.clear();
//        try (
//                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/recommend/logs/recommendVar-rc3FalseAlarmSizeAccurate");
//                BufferedReader br = new BufferedReader(reader)
//        ) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                rcVar.add(line.split("\t")[2]);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        HashSet<String> rcVarSet = new HashSet<>();
        rcVar.forEach(v -> {
            if (database.vptInsen.containsKey(v) && database.vptInsen.get(v).size() > 1) {
                rcVarSet.add(v);
            }
        });

        rcVarSet.forEach(s -> {
            if (rcVar.contains(s)) {
                util.writeFilelnWithPrefix(s, "recommendVar-rc3-all");
            }
        });
        util.plnB("rcVarSet.size() = " + rcVarSet.size() + "");

//        falseAlarmSize("/home/xrbin/IdeaProjects/recommend/logs/recommendVar-rc3-all", "/home/xrbin/IdeaProjects/recommend/logs/recommendVar-rc3AllFalseAlarmSize");
//        rcVarToFeedbacks("/home/xrbin/doophome/NVPT/NVPT3", "3");
//        SortCtxSen.sort1("3");
    }

    public static HashMap<String, HashSet<CSAllocation>> rc3vptInsen = new HashMap<>();

    public static void rc3plus() {
        try {
            Runtime.getRuntime().exec("rm logs/rc3");
            Runtime.getRuntime().exec("rm logs/recommendVar-rc3");
            Runtime.getRuntime().exec("rm logs/recommendVarAndScore-rc3");
        } catch (Exception e) {
            System.err.print("");
        }

        if (flag_main) {
            Main.run();
            flag_main = false;
//            return;
        }
        /*
        对于所有被多次调用的方法的调用点接收返回值的变量 -> a = b.f()中的a
         */

        int count = 0;
        HashSet<String> methodInvokes = new HashSet<>();
        HashSet<String> rcVarSet = new HashSet<>();
        for (String m : database.backCg.keySet()) {
//            if(database.appMethod.contains(m)) {
//                System.out.println(m);
            HashSet<CSAllocation> countBaseObject = new HashSet<>();
            database.backCg.get(m).forEach(methodInvoke -> {
//                System.out.println("\t" + methodInvoke);
//                System.out.println("\t" + database.methodInvokeToBase.get(methodInvoke));
//                System.out.println("\t" + database.vptInsen.containsKey(database.methodInvokeToBase.get(methodInvoke)));
                if (database.vptInsen.containsKey(database.methodInvokeToBase.get(methodInvoke))) {
                    countBaseObject.addAll(database.vptInsen.get(database.methodInvokeToBase.get(methodInvoke)));
                }
            });
            // 方法被多次调用（对于对象上下文敏感来说，只要base指向的所有对象超过1就算）
            if (countBaseObject.size() > 1) {
//                System.out.println("\t" + m);
                count++;
                database.backCg.get(m).forEach(methodInvoke -> {
                    if (invokeArgDiffVptSize(m, methodInvoke) && invokeThisDiffVptSize(m, methodInvoke)) {
                        methodInvokes.add(methodInvoke);
                    }
                });
            }
        }

        methodInvokes.forEach(methodInvoke -> {
            database.cg.get(methodInvoke).forEach(m -> {
                if (database.vptInsen.containsKey(database.methodInvokeToBase.get(methodInvoke))) {
                    String returnVar = database.assignReturnValue.get(methodInvoke);
                    if (returnVar != null) {
                        String returnVarMethod = returnVar.split("/")[0];
                        if (database.appMethod.contains(returnVarMethod)) {
                            if (database.vptInsen.containsKey(returnVar)
                                    && database.vptInsen.get(returnVar).size() > 1
                                    && returnVarPTIn(returnVar, methodInvoke)
                            ) {
                                rcVar.add(returnVar);
                                HashMap<String, String> ap = database.actualParam.get(methodInvoke);
                                String base = database.methodInvokeToBase.get(methodInvoke);

                                HashSet<CSAllocation> thisVpt = new HashSet<>();
                                Vector<HashSet<CSAllocation>> argsVpt = new Vector<>();
                                if (database.nameToPname.containsKey(base) && database.vptInsen.containsKey(database.nameToPname.get(base))) {
                                    thisVpt.addAll(database.vptInsen.get(database.nameToPname.get(base)));
                                }
                                if (ap != null) {
                                    ap.forEach((num, var) -> {
                                        HashSet<CSAllocation> temp = new HashSet<>();
                                        argsVpt.add(temp);
                                        if (database.vptInsen.containsKey(var)) {
                                            temp.addAll(database.vptInsen.get(var));
                                        }
                                    });
                                }
                                if (Main.wpCFG.getBodys().containsKey(m)) {
//                                        System.out.println("\n" + methodInvoke + "\t" + m);
                                    MethodReturnVpt mrv = new MethodReturnVpt(Main.wpCFG.getBodys().get(m).getMethod(), Main.ivfg, Main.wpCFG, thisVpt, argsVpt);
                                    rc3vptInsen.computeIfAbsent(returnVar, k -> new HashSet<>()).addAll(mrv.analysis());
                                }
                            }
                        }
                    }
                }
            });
        });

        rcVar.forEach(v -> {
            if (database.vptInsen.containsKey(v) && database.vptInsen.get(v).size() > 1) {
                rcVarSet.add(v);
            }
        });

        rcVar.clear();
        try {
            File file = new File("/home/xrbin/IdeaProjects/recommend/logs/recommendVar-rc3FalseAlarmSizeAccurate");
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);//指定以UTF-8格式写入文件
            rcVarSet.forEach(var -> {
                if (rc3vptInsen.containsKey(var) && database.vptInsen.containsKey(var) && database.vpt2obj.containsKey(var)) {
                    rcVar.add(var);
                    HashSet<CSAllocation> temp = new HashSet<>(database.vptInsen.get(var));
                    int size = temp.size();
                    temp.removeAll(rc3vptInsen.get(var));
                    try {
                        osw.write(size + "\t" + temp.size() + "\t" + var + "\n");
                    } catch (Exception e) {
                        System.err.println();
                    }
                }
                else {
//                    System.out.println("-=-=no var = " + var);
                }
            });
            osw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        rcVar.forEach(s -> util.writeFilelnWithPrefix(s, "recommendVar-rc3"));

        System.out.println("count = " + count);
        util.plnB("rcVarSet.size() = " + rcVarSet.size() + "");
        falseAlarmSize("/home/xrbin/IdeaProjects/recommend/logs/recommendVar-rc3", "/home/xrbin/IdeaProjects/recommend/logs/recommendVar-rc3FalseAlarmSize");
    }

    public static void rc3plusSingleVar() {
        try {
            Runtime.getRuntime().exec("rm logs/rc3");
            Runtime.getRuntime().exec("rm logs/recommendVar-rc3");
            Runtime.getRuntime().exec("rm logs/recommendVarAndScore-rc3");
        } catch (Exception e) {
            System.err.print("");
        }

        if (flag_main) {
            Main.run();
            flag_main = false;
        }
        /*
        对于所有被多次调用的方法的调用点接收返回值的变量 -> a = b.f()中的a
         */

        int count = 0;
        HashSet<String> methodInvokes = new HashSet<>();
        HashSet<String> rcVarSet = new HashSet<>();
        for (String m : database.backCg.keySet()) {
//            if(database.appMethod.contains(m)) {
//                System.out.println(m);
            database.backCg.get(m).forEach(methodInvoke -> {
//                System.out.println("\t" + methodInvoke);
//                System.out.println("\t" + database.methodInvokeToBase.get(methodInvoke));
//                System.out.println("\t" + database.vptInsen.containsKey(database.methodInvokeToBase.get(methodInvoke)));
                if (database.assignReturnValue.containsKey(methodInvoke)
                        && database.assignReturnValue.get(methodInvoke).equals(
//                                "<org.rapidoid.collection.AbstractCollectionDecorator: java.util.Iterator iterator()>/$stack3"
                        "<org.rapidoid.commons.RapidoidInfo: void <clinit>()>/$stack7"
//                                "<org.rapidoid.collection.AbstractCollectionDecorator: java.util.Iterator iterator()>/$stack3"
//                                "<org.rapidoid.collection.AbstractCollectionDecorator: java.util.Iterator iterator()>/$stack3"
//                                "<org.rapidoid.collection.AbstractCollectionDecorator: java.util.Iterator iterator()>/$stack3"
                )
                ) {
                    String returnVar = database.assignReturnValue.get(methodInvoke);
                    if (returnVar != null) {
                        String returnVarMethod = returnVar.split("/")[0];
                        if (database.appMethod.contains(returnVarMethod)) {
                            if (database.vptInsen.containsKey(returnVar) && database.vptInsen.get(returnVar).size() > 1
                            ) {
                                rcVarSet.add(returnVar);
                                HashMap<String, String> ap = database.actualParam.get(methodInvoke);
                                String base = database.methodInvokeToBase.get(methodInvoke);

                                HashSet<CSAllocation> thisVpt = new HashSet<>();
                                Vector<HashSet<CSAllocation>> argsVpt = new Vector<>();
                                if (database.nameToPname.containsKey(base) && database.vptInsen.containsKey(database.nameToPname.get(base))) {
                                    thisVpt.addAll(database.vptInsen.get(database.nameToPname.get(base)));
                                }
                                if (ap != null) {
                                    ap.forEach((num, var) -> {
                                        HashSet<CSAllocation> temp = new HashSet<>();
                                        argsVpt.add(temp);
                                        if (database.vptInsen.containsKey(var)) {
                                            temp.addAll(database.vptInsen.get(var));
                                        }
                                    });
                                }
                                if (Main.wpCFG.getBodys().containsKey(m)) {
                                    System.out.println("\n" + methodInvoke + "\t" + m);
                                    MethodReturnVpt mrv = new MethodReturnVpt(Main.wpCFG.getBodys().get(m).getMethod(), Main.ivfg, Main.wpCFG, thisVpt, argsVpt);
                                    rc3vptInsen.computeIfAbsent(returnVar, k -> new HashSet<>()).addAll(mrv.analysis());
                                }
                            }
                        }
                    }
                }
            });
        }

        System.out.println();
        rc3vptInsen.keySet().forEach(var -> {
            System.out.println(var);
            rc3vptInsen.get(var).forEach(csa -> {
                System.out.println("\t" + csa);
            });
        });

        try {
            File file = new File("/home/xrbin/IdeaProjects/recommend/logs/recommendVar-rc3FalseAlarmSizeAccurate");
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);//指定以UTF-8格式写入文件
            rcVarSet.forEach(var -> {
                if (rc3vptInsen.containsKey(var) && database.vpt2obj.containsKey(var)) {
                    HashSet<CSAllocation> temp = new HashSet<>(rc3vptInsen.get(var));
                    int size = temp.size();
                    temp.removeAll(database.vpt2obj.get(var));
                    try {
                        osw.write(size + "\t" + temp.size() + "\t" + var + "\n");
                    } catch (Exception e) {
                        System.err.println();
                    }
                }
                else {
//                    System.out.println("-=-=no var = " + var);
                }
            });
            osw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        rcVarSet.forEach(s -> util.writeFilelnWithPrefix(s, "recommendVar-rc3"));

        System.out.println("count = " + count);
        util.plnB("rcVarSet.size() = " + rcVarSet.size() + "");
    }

    public static boolean invokeArgDiffVptSizeRes = false;

    public static boolean invokeArgDiffVptSize(String method, String methodInvoke) {
        HashMap<String, String> fp = database.formalParam.get(method);
        HashMap<String, String> ap = database.actualParam.get(methodInvoke);
        if (!database.formalParam.containsKey(method) || !database.actualParam.containsKey(methodInvoke)) return true;

//        System.out.println(method + "\t" + methodInvoke);
        invokeArgDiffVptSizeRes = false;
        fp.keySet().forEach(num -> {
            if (ap.containsKey(num)) {
                if (database.vptInsen.containsKey(fp.get(num)) && database.vptInsen.containsKey(ap.get(num))) {
                    HashSet<CSAllocation> temp = new HashSet<>(database.vptInsen.get(fp.get(num)));
                    temp.removeAll(database.vptInsen.get(ap.get(num)));
                    removeRedudancy(temp);
                    if (temp.size() > 0) invokeArgDiffVptSizeRes = true;
                }
            }
        });

        return invokeArgDiffVptSizeRes;
    }

    public static boolean invokeThisDiffVptSize(String method, String methodInvoke) {
        String base = database.methodInvokeToBase.get(methodInvoke);
        String thisVar = database.thisVar.get(method);
        if (!database.thisVar.containsKey(method) || !database.methodInvokeToBase.containsKey(methodInvoke))
            return true;

//        System.out.println(method + "\t" + methodInvoke);
        if (database.vptInsen.containsKey(base) && database.vptInsen.containsKey(thisVar)) {
            HashSet<CSAllocation> temp = new HashSet<>(database.vptInsen.get(thisVar));
            temp.removeAll(database.vptInsen.get(base));
            removeRedudancy(temp);
            return !temp.isEmpty();
        }
        return true;
    }

    // 函数返回值所有指向的变量都是在函数内部（或后部）生成的，这种不受上下文不敏感影响。
    public static boolean returnVarPTIn(String var, String methodInvoke) {
        HashSet<CSAllocation> varPt = new HashSet<>(database.vptInsen.get(var));
        Stack<SootMethod> worklist = new Stack<>();
        HashSet<SootMethod> color = new HashSet<>();
        database.cg.get(methodInvoke).forEach(m -> {
            if (Main.wpCFG.getBodys().containsKey(m)) {
                worklist.push(Main.wpCFG.getBodys().get(m).getMethod());
            }
        });

        while (!worklist.empty()) {
            SootMethod sm = worklist.pop();
            if (color.add(sm) && Main.wpCFG.getBodys().containsKey(sm.toString())) {
                Main.wpCFG.getBodys().get(sm.toString()).getUnits().forEach(u -> {
                    if (u instanceof AssignStmt && ((AssignStmt) u).getRightOp() instanceof NewExpr) {
                        CSAllocation csa = MethodReturnVpt.makeAlloc(u);
                        varPt.remove(csa);
                    }
                    else if (u instanceof AssignStmt && ((AssignStmt) u).getRightOp() instanceof StaticFieldRef) {
                        if (database.staticVpt.containsKey(((AssignStmt) u).getRightOp().toString())) {
                            varPt.removeAll(database.staticVpt.get(((AssignStmt) u).getRightOp().toString()));
                        }
                    }
                });
                Main.cg.getSuccsOf(sm).forEach(succ -> {
                    if (succ.getO2()) {
                        worklist.push(succ.getO1());
                    }
                });
            }
        }

        return varPt.size() > 0;
    }

    public static void removeRedudancy(HashSet<CSAllocation> allocations) {
        allocations.remove(CSAllocation.main_method_array_content);
        allocations.remove(CSAllocation.not_in_doop);
        allocations.remove(CSAllocation.null_pseudo_heap);
        allocations.remove(CSAllocation.string_buffer);
        allocations.remove(CSAllocation.string_builder);
        allocations.remove(CSAllocation.string_constant);
    }

    public static int countBaseObj = 0;
    public static int countInvokeRc4 = 0;

    public static void rc4() {
        try {
            Runtime.getRuntime().exec("rm logs/rc4");
            Runtime.getRuntime().exec("rm logs/recommendVar-rc4");
            Runtime.getRuntime().exec("rm logs/recommendVarAndScore-rc4");
        } catch (Exception e) {
            System.err.print("");
        }

        if (flag_main) {
            Main.run();
            flag_main = false;
        }
        /*
        对于每个store，其所在的方法如果有多个上下文（根据callgraph和调用的点的base指向的对象的个数）
        store的base和methodInvoke的base是否一样
         */


        if (false) {
            for (String m : database.backCg.keySet()) {
                indexToArg = new HashMap<>();
                HashSet<CSAllocation> countBaseObject = new HashSet<>();
                database.backCg.get(m).forEach(methodInvoke -> {
                    if (database.vpt2obj.containsKey(database.methodInvokeToBase.get(methodInvoke))) {
                        if (countBaseObject.addAll(database.vpt2obj.get(database.methodInvokeToBase.get(methodInvoke)))) {
                            countBaseObj++;
                        }
                    }
                });
                if (countBaseObj > 1) {
                    Body b = Main.wpCFG.getBodys().get(m);
                    if (b == null) continue;

                    if (database.backCg.containsKey(m)) {
                        database.backCg.get(m).forEach(invokeIntr -> {
                            if (database.actualParam.containsKey(invokeIntr)) {
                                database.actualParam.get(invokeIntr).keySet().forEach(i ->
                                        indexToArg.computeIfAbsent(i, k -> new HashSet<>()).add(database.actualParam.get(invokeIntr).get(i))
                                );
                            }
                        });
                    }


                    boolean flag;
                    for (String key : indexToArg.keySet()) {
                        flag = false;
                        for (String arg1 : indexToArg.get(key)) {
                            for (String arg2 : indexToArg.get(key)) {
                                if (database.vptInsen.containsKey(arg1) && database.vptInsen.containsKey(arg2)) {
                                    if (!arg1.equals(arg2)) {
                                        if (database.vptInsen.get(arg1).size() == database.vptInsen.get(arg2).size()) {
                                            for (CSAllocation o : database.vptInsen.get(arg1)) {
                                                if (!database.vptInsen.get(arg2).contains(o)) {
                                                    flag = true;
                                                }
                                            }
                                            if (!flag) {
                                                for (CSAllocation o : database.vptInsen.get(arg2)) {
                                                    if (!database.vptInsen.get(arg1).contains(o)) {
                                                        flag = true;
                                                    }
                                                }
                                            }
                                            if (flag) {
                                                for (Unit u : b.getUnits()) {
                                                    if (u instanceof IdentityStmt && u.toString().contains("@parameter" + key)
                                                            && !utils.isPrimitiveType(((IdentityStmt) u).getRightOp().getType())
                                                    ) {
//                                                        System.out.println("-------\t" + m + "\t" + u);
//                                                        System.out.println("-------\t\t" + arg1);
//                                                        System.out.println("-------\t\t" + arg2);
                                                        dfsRc4(u);
                                                        break;
                                                    }
                                                }
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            if (flag) break;
                        }
                        if (flag) break;
                    }

//                rcMethod.add(m);
//                if(rcMethod.size() > 100) return;
                }
            }
        }


        if (true) {

            int count = 0;
            for (String m : database.backCg.keySet()) {
                HashSet<CSAllocation> countBaseObject = new HashSet<>();
                database.backCg.get(m).forEach(methodInvoke -> {
                    if (database.vpt2obj.containsKey(database.methodInvokeToBase.get(methodInvoke)))
                        countBaseObject.addAll(database.vpt2obj.get(database.methodInvokeToBase.get(methodInvoke)));
                });
                if (countBaseObject.size() > 1) {
                    count++;
                    rcMethod.add(m);
                }
            }
            System.out.println("count = " + count);

            rcMethod.forEach(s -> {
                if (Main.bodys.containsKey(s)) {
                    Body b = Main.bodys.get(s);
                    b.getParameterRefs().forEach(v -> {
                        if (!utils.isPrimitiveType(v.getType())) {
                            b.getUnits().forEach(u -> {
                                if (u instanceof IdentityStmt && ((IdentityStmt) u).getRightOp().equals(v)) {
//                                insenVpt.keySet().forEach(var -> {
//                                    if((b.getMethod() + "/" + ((IdentityStmt) u).getLeftOp().toString()).equals(utils.processingName(var))) {
//                                        rcVar.add(var);
//                                    }
//                                });
                                    if (database.nameToPname.containsKey((b.getMethod() + "/" + ((IdentityStmt) u).getLeftOp().toString()))) {
                                        rcVar.add(database.nameToPname.get((b.getMethod() + "/" + ((IdentityStmt) u).getLeftOp().toString())));
                                    }
                                }
                            });
                        }
                    });
                }
            });

            database.store.forEach(s -> {
                if (s.getBase().contains("this#_0") || s.getBase().contains("l0#_0")) {
                    countInvokeRc4 = 0;
                    String m = s.getMethod();
                    if (database.backCg.containsKey(m) && rcMethod.contains(m)) {
                        if (args(m)) {
                            System.out.println("yes\t\t" + m);
                            database.backCg.get(m).forEach(actualM -> {
                                if (database.vptInsen.containsKey(database.methodInvokeToBase.get(actualM))) {
                                    countInvokeRc4 += database.vptInsen.get(database.methodInvokeToBase.get(actualM)).size();
                                }
                            });
                            if (countInvokeRc4 > 1 && database.vptInsen.containsKey(s.getBase()) && database.vptInsen.get(s.getBase()).size() > 1) {
                                HashSet<CSAllocation> Os = database.vptInsen.get(s.getBase());
                                if (Os != null) {
                                    for (CSAllocation o : Os) {
                                        Field f = new Field(o, s.getSignature());
                                        util.writeFilelnWithPrefix(s + "\n\t" + f + "\n\t" + database.load.get(f), "sToL");
                                        if (database.load.containsKey(f)) {
                                            rcVar.addAll(database.load.get(f));
                                        }
                                    }
                                }
                            }

                        }
                        else {
                            System.out.println("no\t\t" + m);
                        }
                    }
                }
            });
        }

        rcVar.forEach(s -> {
            if (database.vptInsen.containsKey(s)) {
                util.writeFilelnWithPrefix(s, "recommendVar-rc4");
            }
        });

        util.plnB("rc4Count = " + rc4Count + "");
        util.plnB("rcVar.size() = " + rcVar.size() + "");
        util.plnB("rcMethod.size() = " + rcMethod.size() + "");

        rcVarToFeedbacks("/home/xrbin/doophome/NVPT/NVPT4", "4");
        SortCtxSen.sort1("4");
    }

    public static boolean res = false;
    public static HashMap<String, HashSet<String>> indexToArg = new HashMap<>();

    public static boolean args(String m) {
        res = false;
        indexToArg = new HashMap<>();
        if (database.backCg.containsKey(m)) {
            database.backCg.get(m).forEach(invokeIntr -> {
                if (database.actualParam.containsKey(invokeIntr)) {
                    database.actualParam.get(invokeIntr).keySet().forEach(i ->
                            indexToArg.computeIfAbsent(i, k -> new HashSet<>()).add(database.actualParam.get(invokeIntr).get(i))
                    );
                }
            });
        }

        for (String key : indexToArg.keySet()) {
            for (String arg1 : indexToArg.get(key)) {
                for (String arg2 : indexToArg.get(key)) {
                    if (database.vptInsen.containsKey(arg1) && database.vptInsen.containsKey(arg2)) {
                        if (!arg1.equals(arg2)) {
                            if (database.vptInsen.get(arg1).size() == database.vptInsen.get(arg2).size()) {
                                for (CSAllocation o : database.vptInsen.get(arg1)) {
                                    if (!database.vptInsen.get(arg2).contains(o)) {
                                        util.writeFilelnWithPrefix(m + "\n\t" + arg1 + "\n\t" + arg2, "argDiff");
                                        return true;
                                    }
                                }
                                for (CSAllocation o : database.vptInsen.get(arg2)) {
                                    if (!database.vptInsen.get(arg1).contains(o)) {
                                        util.writeFilelnWithPrefix(m + "\n\t" + arg1 + "\n\t" + arg2, "argDiff");
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return res;
    }

    public static int rc4Count = 0;

    public static void dfsRc4(Unit root) {
        rc4Count++;
        IntroValueFlowGraph ivfg = Main.ivfg;
        Stack<Unit> s = new Stack<>();
        HashSet<Unit> color = new HashSet<>();
        SootMethod sm = Main.wpCFG.getMethodOf(root);

        s.push(root);
        if (sm != null) {
            while (!s.empty()) {
                Unit u = s.pop();
                if (color.add(u)) {
//                if (color.add(u) && Main.wpCFG.getMethodOf(u).equals(sm)) {
                    ivfg.getSuccs(u).forEach(succ -> {
                        if (succ instanceof AssignStmt && ((AssignStmt) succ).getLeftOp() instanceof InstanceFieldRef) {
                            InstanceFieldRef ifr = (InstanceFieldRef) ((AssignStmt) succ).getLeftOp();
                            String var = Main.wpCFG.getMethodOf(u).toString() + "/" + ifr.getBase().toString();
                            if (database.vptInsen.containsKey(var)) {
                                for (CSAllocation o : database.vptInsen.get(var)) {
                                    Field f = new Field(o, ifr.getField().toString());
                                    if (database.load.containsKey(f)) {
                                        rcVar.addAll(database.load.get(f));
                                    }
                                }
                            }
                        }
                        s.add(succ);
                    });
                }
            }
        }
    }

    public static void spiltFile() {

//        String inputFile = "/home/xrbin/IdeaProjects/recommend/logs/recommendVar-rc";
        String inputFile = "/home/xrbin/IdeaProjects/recommend/logs/recommendVar-rc2";
        String outputDir = "/home/xrbin/doophome/NVPT/";
        int interval = 10000000;
//        int interval = 1;
        DatabaseManager.getInstance().readData();

        try (
                FileReader reader = new FileReader(inputFile);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            int i = 0;
            int fileIndex = 1;
            rcVar = new Vector<>();
            while ((line = br.readLine()) != null) {
                rcVar.add(line);
                if (++i == interval) {
                    rcVarToFeedbacks(outputDir + "NVPT" + fileIndex, "0");
                    rcVar = new Vector<>();
                    i = 0;
                    fileIndex++;
                }
            }
            rcVarToFeedbacks(outputDir + "NVPT" + fileIndex, "0");
//            rcVarToFeedbacks("/home/xrbin/IdeaProjects/recommend/logs/appVptJiebaRc3", "0");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int countt = 0;
    public static int counttAll = 0;
    public static int counttAllPred = 0;

    public static void falseAlarmSize(String inputFile, String outputFile) {
        try (
                FileReader reader = new FileReader(inputFile);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            rcVar = new Vector<>();
            while ((line = br.readLine()) != null) {
                rcVar.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            File file = new File(outputFile);
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);//指定以UTF-8格式写入文件
            rcVar.forEach(var -> {
                if (database.vptInsen.containsKey(var) && database.vpt2obj.containsKey(var)) {
                    HashSet<CSAllocation> temp = new HashSet<>(database.vptInsen.get(var));
                    int size = temp.size();
                    temp.removeAll(database.vpt2obj.get(var));
                    try {
                        osw.write(size + "\t" + temp.size() + "\t" + var + "\n");
                    } catch (Exception e) {
                        System.err.println();
                    }
                }
                else {
//                    System.out.println("-=-=no var = " + var);
                }
            });
            osw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void falseAlarm(String inputFile, String outputFile) {

        DatabaseManager.getInstance().readData();

        if (true) {
            try (
                    FileReader reader = new FileReader(inputFile);
                    BufferedReader br = new BufferedReader(reader)
            ) {
                String line;
                rcVar = new Vector<>();
                while ((line = br.readLine()) != null) {
                    rcVar.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            rcVar.add("<java.lang.String: java.lang.String trim()>/$stack5");
        }

        try {
            File file = new File(outputFile);
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);//指定以UTF-8格式写入文件
            rcVar.forEach(var -> {
                if (database.vptInsen.containsKey(var) && database.vpt2obj.containsKey(var)) {
                    HashSet<CSAllocation> temp = new HashSet<>(database.vptInsen.get(var));
                    int size = temp.size();
                    temp.removeAll(database.vpt2obj.get(var));
                    temp.forEach(v -> {
                        try {
                            osw.write(v + "\t" + var + "\n");
                        } catch (Exception e) {
                            System.err.println();
                        }
                    });
                }
                else {
//                    System.out.println("-=-=no var = " + var);
                }
            });
            osw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void rcVarToFeedbacks(String outputFile, String rc) {
        try {
            File file = new File(outputFile);
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);//指定以UTF-8格式写入文件
            rcVar.forEach(var -> {
                if (database.vptInsen.containsKey(var) && database.vpt2obj.containsKey(var)) {
                    database.vptInsen.get(var).forEach(a -> {
                        if (!database.vpt2obj.get(var).contains(a)) {
                            try {
                                countt++;
                                counttAll++;
                                osw.write(a.getO() + "\t" + var + "\n");
                            } catch (Exception e) {
                                System.err.println();
                            }
                        }
                        else {
//                            util.writeFilelnWithPrefix("var -> allo: " + var + " -> " + a, "0");
                        }
                    });
//                    System.out.println("var = " + var + ": " + countt);
//                    util.writeFilelnWithPrefix(var + "\t" + countt, "rc" + rc);
                    countt = 0;
                }
                else {
//                    System.out.println("-=-=no var = " + var);
                }
            });
            osw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println("counttAll = " + counttAll);
//        System.out.println("counttAll - counttAllPred = " + (counttAll - counttAllPred));
//        System.out.println((counttAll - counttAllPred));
        util.writeFileln((counttAll - counttAllPred) + "", "/home/xrbin/doophome/NVPT/fbNum");
        counttAllPred = counttAll;
    }

    public static void appVarToFeedbacks() {

        try (
                FileReader reader = new FileReader(utils.DATABASE + "AVPT.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
//                System.out.println(line);
                String[] sa = line.split("\t");
                database.appInsenVpt.computeIfAbsent(sa[3], k -> new HashSet<>()).add(new CSAllocation(new Allocation(sa[1])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader("/home/xrbin/doophome/out/" + projectName2Obj + "/database/AVPT.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
//                System.out.println(line);
                String[] sa = line.split("\t");
                database.appVpt2Obj.computeIfAbsent(sa[3], k -> new HashSet<>()).add(new CSAllocation(new Allocation(sa[1])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

//        try {
//            File file = new File("/home/xrbin/doophome/NVPT/NVPT0");
//            FileOutputStream fos = new FileOutputStream(file);
//            OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);//指定以UTF-8格式写入文件
//            database.appInsenVpt.keySet().forEach(var -> {
//                if (database.appVpt2Obj.containsKey(var)) {
//                    database.appInsenVpt.get(var).forEach(o -> {
//                        if (!database.appVpt2Obj.get(var).contains(o)) {
//                            try {
//                                System.out.println(o + "\t" + var + "\n");
//                                osw.write(o + "\t" + var + "\n");
//                            } catch (Exception e) {
//                                System.out.print("");
//                            }
//                        }
//                    });
//                }
//            });
//            osw.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        database.appInsenVpt.keySet().forEach(var -> {
            if (database.appVpt2Obj.containsKey(var)) {
                util.writeFilelnWithPrefix(var, "appVarFop");
                util.writeFilelnWithPrefix(var + "\t" + database.appInsenVpt.get(var).size(), "appVarVptSizeFop");
            }
        });
    }

    public static int countVptDiff = 0;

    public static void vptSubVpt() {
        try (
                FileReader reader = new FileReader(utils.DATABASE + "VarPointsTo.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] sa = line.split("\t");
                database.vptInsen.computeIfAbsent(sa[3], k -> new HashSet<>()).add(new CSAllocation(new Allocation(sa[1])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        util.getTime("vpt2obj");
        try (
                FileReader reader = new FileReader("/home/xrbin/doophome/out/fb/database/VarPointsTo.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] sa = line.split("\t");
                database.vpt2obj.computeIfAbsent(sa[3], k -> new HashSet<>()).add(new CSAllocation(new Allocation((sa[1]))));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Runtime.getRuntime().exec("rm logs/vptDiff");
            Runtime.getRuntime().exec("rm logs/vptDiffNumber");
        } catch (Exception e) {
            System.err.print("");
        }

        database.vptInsen.forEach((var, os) -> {
            if (database.vpt2obj.containsKey(var)) {
                countVptDiff = 0;
                os.forEach(o -> {
                    if (!database.vpt2obj.get(var).contains(o)) {
                        util.writeFilelnWithPrefix(var + "\t" + o, "/vptDiff");
                        countVptDiff++;
                    }
                });
                if (countVptDiff != 0) {
                    util.writeFilelnWithPrefix(var + "\t" + countVptDiff, "/vptDiffNumber");
                }
            }
        });

    }

    public static void func1() {
        try (
                FileReader reader = new FileReader(utils.DATABASE + "VarPointsTo.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] sa = line.split("\t");
                database.vptInsen.computeIfAbsent(sa[3], k -> new HashSet<>()).add(new CSAllocation(new Allocation(sa[1])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader(utils.DATABASE + "AnyCallGraphEdge.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] sa = line.split("\t");
                database.backCg.computeIfAbsent(sa[1], k -> new HashSet<>()).add(sa[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader(utils.DATABASE + "VirtualMethodInvocation.facts");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
//                System.out.println(line);
                String[] sa = line.split("\t");
//                if(appMethodInsn.contains(sa[0])) {
                database.methodInvokeToBase.put(sa[0], sa[3]);
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader(utils.DATABASE + "SpecialMethodInvocation.facts");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
//                System.out.println(line);
                String[] sa = line.split("\t");
//                if(appMethodInsn.contains(sa[0])) {
                database.methodInvokeToBase.put(sa[0], sa[3]);
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
//            Runtime.getRuntime().exec("rm logs/vptDiff");
//            Runtime.getRuntime().exec("rm logs/vptDiffNumber");
        } catch (Exception e) {
            System.err.print("");
        }

        database.backCg.keySet().forEach(k -> {
            database.backCg.get(k).forEach(kk -> {
                if (kk.contains("java.lang.String.trim")) {
                    System.out.println(kk);
                    rcVar.add(database.methodInvokeToBase.get(kk));
                }
            });
        });
        HashSet<CSAllocation> vpts = new HashSet<>();

        rcVar.forEach(v -> vpts.addAll(database.vptInsen.get(v)));
        vpts.forEach(o -> util.writeFilelnWithPrefix(o.toString(), "vpt"));

//        rcVar.clear();
//        rcVar.add("<java.lang.String: java.lang.String trim()>/$stack5");
//
//        try {
//            File file = new File("");
//            FileOutputStream fos = new FileOutputStream(file);
//            OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);//指定以UTF-8格式写入文件
//            rcVar.forEach(var -> {
//                if (database.vptInsen.containsKey(var) && database.vpt2obj.containsKey(var)) {
//                    HashSet<CSAllocation> temp = new HashSet<>(database.vptInsen.get(var));
//                    int size = temp.size();
//                    temp.removeAll(database.vpt2obj.get(var));
//                    temp.forEach(v -> {
//                        try {
//                            osw.write(v + "\t" + var + "\n");
//                        } catch (Exception e) {
//                            System.err.println();
//                        }
//                    });
//                }
//                else {
////                    System.out.println("-=-=no var = " + var);
//                }
//            });
//            osw.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public static void func2() {
        database.readData();
        database.backCg.keySet().forEach(k -> {
            util.writeFilelnWithPrefix(k + "\t" + database.backCg.get(k).size(), "backCgSize");
        });
    }

    // 对比文本文件
    public static void func3() {
        Vector<String> vector = new Vector<>();
        HashSet<String> set = new HashSet<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/OtherProjects/yanniss-doop-4.24.10/yanniss-doop/out/antlr-ci/context-insensitive.dl");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                vector.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader("/home/xrbin/OtherProjects/yanniss-doop-4.24.10/yanniss-doop/out/antlr-ci-mainclass/context-insensitive.dl");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                set.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        vector.forEach(v -> {
            if (!set.contains(v)) {
                util.writeFilelnWithPrefix(v, "dlDiff");
            }
        });

    }

    public static void func4() {
        // 对比文件
        HashSet<String> same = new HashSet<>();
        HashSet<String> xalan = new HashSet<>();
        HashSet<String> antlr = new HashSet<>();
        HashSet<String> luindex = new HashSet<>();
        HashSet<String> fop = new HashSet<>();
        HashSet<String> pmd = new HashSet<>();
        HashSet<String> chart = new HashSet<>();
        HashSet<String> lusearch = new HashSet<>();
        HashSet<String> hsqldb = new HashSet<>();

//        readVpt("luindex", luindex);
//        readVpt("lusearch", lusearch);
//        readVpt("fop", fop);
//        readVpt("xalan", xalan);
//        readVpt("antlr", antlr);
//        readVpt("hsqldb", hsqldb);
//        readVpt("pmd", pmd);
//        readVpt("chart", chart);

//        readVpt("luindex", same);
//        readVpt("lusearch", same);
//        readVpt("fop", same);
//        readVpt("xalan", same);
//        readVpt("antlr", same);
//        readVpt("hsqldb", same);
//        readVpt("pmd", same);
//        readVpt("chart", same);

        readVpt("antlr", same);
//        readVptSame("xalan", same);
//        readVptSame("fop", same);
//        readVptSame("hsqldb", same);
//        readVptSame("pmd", same);
//        readVptSame("chart", same);
        readVptSame("luindex", same);
        readVptSame("lusearch", same);

//        hsqldb.forEach(s -> {
//            if(hsqldb.contains(s)
//                    && xalan.contains(s)
//                    && antlr.contains(s)
//                    && luindex.contains(s)
//                    && fop.contains(s)
//                    && pmd.contains(s)
//                    && chart.contains(s)
//                    && lusearch.contains(s)
//            ) {
//                util.writeFilelnWithPrefix(s, "insenSameVpt");
//            }
//        });

//        luindex.forEach(s -> {
//            if(luindex.contains(s)
//                    && antlr.contains(s)
//                    && lusearch.contains(s)
//            ) {
//                util.writeFilelnWithPrefix(s, "insenSameVpt_jdk6");
//            }
//        });

        same.forEach(s -> {
            util.writeFilelnWithPrefix(s, "csSameVpt_jdk6");
//            util.writeFilelnWithPrefix(s.split("\t")[1], "csSameVptVar_jdk6");
        });
    }

    public static boolean flag = true;
    public static Map<String, Set<String>> vpt1 = new HashMap<>();
    public static Map<String, Map<String, Set<String>>> vpt2 = new HashMap<>();

    public static void readVpt0(String id, HashSet<String> vpts) {
        try (
//                FileReader reader = new FileReader("/home/xrbin/doophome/out/" + id + "-2obj/database/Stats_Simple_InsensVarPointsTo.csv");
                FileReader reader = new FileReader("/home/xrbin/doophome/out/" + id + "-2obj/database/VarPointsTo.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] array = line.split("\t");
                vpt1.computeIfAbsent(array[3], k -> new HashSet<>()).add(array[1]);
                vpt2.computeIfAbsent(array[3], k -> new HashMap<>()).computeIfAbsent(array[2], kk -> new HashSet<>()).add(array[1]);
                vpts.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        vpt1.keySet().forEach(var -> {
            vpt2.get(var).keySet().forEach(ctx -> {
                if (!util.equalSet(vpt1.get(var), vpt2.get(var).get(ctx))) {
                    flag = false;
                }
            });
            if (flag) {
                util.writeFilelnWithPrefix(var, "ctxSameVpt" + id);
            }
            else {
                flag = true;
            }
        });

        System.out.println(id + " - size = " + vpts.size());
    }

    public static void readVpt(String id, HashSet<String> vpts) {
        vpts.clear();
        vpt1 = new HashMap<>();
        vpt2 = new HashMap<>();
        try (
//                FileReader reader = new FileReader("/home/xrbin/doophome/out/" + id + "-2obj/database/Stats_Simple_InsensVarPointsTo.csv");
                FileReader reader = new FileReader("/home/xrbin/doophome/out/" + id + "-2obj/database/VarPointsTo.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] array = line.split("\t");
                vpt1.computeIfAbsent(array[3], k -> new HashSet<>()).add(array[1]);
                vpt2.computeIfAbsent(array[3], k -> new HashMap<>()).computeIfAbsent(array[2], kk -> new HashSet<>()).add(array[1]);
//                vpts.add(line);
                vpts.add(array[1] + "\t" + array[3]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        vpt1.keySet().forEach(var -> {
            vpt2.get(var).keySet().forEach(ctx -> {
                if (!util.equalSet(vpt1.get(var), vpt2.get(var).get(ctx))) {
                    flag = false;
                }
            });
            if (flag) {
                util.writeFilelnWithPrefix(var, "ctxSameVpt" + id);
            }
            else {
                flag = true;
            }
        });

        System.out.println(id + " - size = " + vpts.size() + ", var.size = " + vpt1.size());
    }

    public static void readVptSame(String id, HashSet<String> same) {
        Set<String> set = new HashSet<>();
        List<String> reserved = new ArrayList<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/doophome/out/" + id + "-2obj/database/Stats_Simple_InsensVarPointsTo.csv");
//                FileReader reader = new FileReader("/home/xrbin/doophome/out/" + id + "-2obj/database/VarPointsTo.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                set.add(line.split("\t")[1]);
                if (same.contains(line)) {
                    reserved.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        same.clear();
        same.addAll(reserved);
        System.out.println(id + " - size = " + same.size());
        System.out.println("set - size = " + set.size());
    }

    //
//    CREATE_OP_CONTEXT(rel_535_delta_mainAnalysis_SystemThreadGroup_op_ctxt,
  //                    rel_535_delta_mainAnalysis_SystemThreadGroup->createContext());

    public static void func5() {
        List<String> list = new ArrayList<>();
        try (
                FileReader reader = new FileReader("/home/rrong/Desktop/0.txt");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                list.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        list.forEach(s -> {
            util.writeFileln("CREATE_OP_CONTEXT(" + s + "_op_ctxt," + s + "->createContext());\n" +
                            "std::cout << \"" + s + "\" << \"\\t\" << " + s + "->size()" + " << std::endl;"
                    , "/home/rrong/Desktop/1.txt");
        });
    }

    public static void func6() {
        Map<String, String> map = new HashMap<>();
        try (
                FileReader reader = new FileReader("/home/rrong/Desktop/3.txt");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] array = line.split("\t");
                map.computeIfAbsent(array[0], k -> "");
                map.put(array[0], map.get(array[0]) + "\t" + array[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        map.forEach((k, v) -> util.writeFileln(k + "\t" + v, "/home/rrong/Desktop/2.txt"));
    }

    public static void func7() {
        Set<String> set = new HashSet<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/doophome/out/antlr-insen/database/VarPointsToApp.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.contains("StringBuilder")
                        && !line.contains("StringBuffer")
                        && !line.contains("dynamic")
                        && !line.contains("parameter")
                        && !line.contains("this")
                        && !line.contains("stringconstant")
                ) {
                    set.add(utils.varNameDoopToShimple(line.split("\t")[1]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        set.forEach(s -> util.writeFileln(s, "/home/xrbin/IdeaProjects/Yulin/logs/antlrAppVar"));
    }

    public static void func8() {
        Set<String> set = new HashSet<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/doophome/out/chart-2obj/database/VarPointsTo.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String object = line.split("\t")[1];
                if (!object.contains("new")) {
                    set.add(object);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        set.forEach(s -> util.writeFileln(s, "/home/xrbin/Desktop/jiebaAppVar"));
    }

    public static void func9() {
        Map<String, Set<String>> setInsen = new HashMap<>();
        Map<String, Set<String>> set2Obj = new HashMap<>();
        Set<String> summaryVar = new HashSet<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/doophome/out/chart-2obj/database/VarPointsTo.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] object = line.split("\t");
                set2Obj.computeIfAbsent(object[3], k -> new HashSet<>()).add(object[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (
                FileReader reader = new FileReader("/home/xrbin/doophome/out/chart-2obj/database/Stats_Simple_InsensVarPointsTo.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] object = line.split("\t");
                setInsen.computeIfAbsent(object[1], k -> new HashSet<>()).add(object[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Yulin/logs/summaryVariable");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                summaryVar.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        summaryVar.forEach(var -> {
            if (util.equalSet(set2Obj.get(var), setInsen.get(var))) {
                util.writeFilelnWithPrefix(var, "complexSummaryVar");
            }
        });
    }

    public static void func10() {
        Map<String, Set<String>> setInsen = new HashMap<>();
        Set<String> storeVar = new HashSet<>();
        Set<String> summaryVar = new HashSet<>();
        // /home/xrbin/doophome/out/xalan-2obj/database/VirtualMethodInvocation.facts
        // /home/xrbin/doophome/out/xalan-2obj/database/SpecialMethodInvocation.facts
        try (
                FileReader reader = new FileReader("/home/xrbin/doophome/out/xalan-2obj/database/StoreInstanceField.facts");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] object = line.split("\t");
                storeVar.add(object[2]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader("/home/xrbin/doophome/out/xalan-2obj/database/Stats_Simple_InsensVarPointsTo.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] object = line.split("\t");
                setInsen.computeIfAbsent(object[1], k -> new HashSet<>()).add(object[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        setInsen.forEach((k, v) -> {
            if(v.size() > 20 && storeVar.contains(k)) {
                util.writeFileln(k, "/home/xrbin/doophome/out/xalan-2obj/database/BigPTSetVar.facts");
            }
        });


    }

    public static void func11() {
        Set<String> storeVar = new HashSet<>();
        Set<String> summaryVar = new HashSet<>();

        try (
                FileReader reader = new FileReader("/home/xrbin/doophome/out/xalan-2obj/database/StoreInstanceField.facts");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] object = line.split("\t");
                storeVar.add(object[2]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader("/home/xrbin/doophome/out/xalan-2obj/database/VirtualMethodInvocation.facts");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] object = line.split("\t");
                summaryVar.add(object[3]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (
                FileReader reader = new FileReader("/home/xrbin/doophome/out/xalan-2obj/database/SpecialMethodInvocation.facts");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] object = line.split("\t");
                summaryVar.add(object[3]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        summaryVar.forEach(k -> {
            if(storeVar.contains(k)) {
                util.writeFileln(k, "/home/xrbin/doophome/out/xalan-2obj/database/BigPTSetVar.facts");
//                util.writeFileln(k, "/home/xrbin/Desktop/BigPTSetVar.facts");
            }
        });


    }

    public static void func12() {
        Set<String> var = new HashSet<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Yulin/logs/summaryVariable1");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                if(!line.contains("[]")) {
                    util.writeFileln(line, "/home/xrbin/IdeaProjects/Yulin/logs/summaryVariable");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void func13() {

//        Set<String> pts0 = new HashSet<>();
//        try (
//                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/luindex_/AllVar");
//                BufferedReader br = new BufferedReader(reader)
//        ) {
//            String line;
//            String var = "";
//            while ((line = br.readLine()) != null) {
//                try {
//                    pts0.add(line);
//                } catch (Exception e) {
//                    System.err.println(line);
//                    e.printStackTrace();
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println(pts0.size());

        Map<String, Set<String>> pts = new HashMap<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/testDB/sootOutput/luindex-bk/allResult");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            String var = "";
            while ((line = br.readLine()) != null) {
                try {
                    if (!line.startsWith("\t")) {
                        String var_origin = line.split("\t")[3];
                        var = var_origin.split(" =")[0];
                        pts.computeIfAbsent(var, k -> new HashSet<>());
                    }
                    else {
                        pts.computeIfAbsent(var, k -> new HashSet<>()).add(line.replace("\t", ""));
                    }
                } catch (Exception e) {
                    System.err.println(line);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Set<String>> pts2obj = new HashMap<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/luindex_/luindex2obj");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            String var = "";
            while ((line = br.readLine()) != null) {
                try {
                    if (!line.startsWith("\t")) {
                        var = line;
                    }
                    else {
                        String o1 = line.replace("\t", "");
                        String o2 = o1.split(", ")[0];
                        pts2obj.computeIfAbsent(var, k -> new HashSet<>()).add(o2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Set<String> sameVar = new HashSet<>();
        pts.keySet().forEach(v -> {
            if (pts2obj.keySet().contains(v)) {
                sameVar.add(v);
            }
        });

        System.out.println(pts.keySet().size() + "\t" + pts2obj.size());

        for (String s : sameVar) {
            util.writeFileln(pts.get(s).size() - pts2obj.get(s).size() + "\t" + s + "\t" + pts.get(s).size() + "\t" + pts2obj.get(s).size(), "/home/xrbin/IdeaProjects/recommend/logs/onDemand/luindexVarPtsDiff02");
        }

        int ccc = 0;
        for (String s : pts.keySet()) {
            if (pts.get(s).size() == 0) {
                ccc++;
            }
        }

        System.out.println(pts.keySet().size() + "\t" + ccc);

    }

    public static void func13_02() {
        Map<String, Set<String>> pts = new HashMap<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/luindex_03/AllResult");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            String var = "";
            while ((line = br.readLine()) != null) {
                try {
                    if (!line.startsWith("\t")) {
                        var = line;
                    }
                    else {
                        pts.computeIfAbsent(var, k -> new HashSet<>()).add(line.replace("\t", ""));
                    }
                } catch (Exception e) {
                    System.err.println(line);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Set<String>> pts2obj = new HashMap<>();
//        try (
//                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/luindex/luindex2obj");
//                BufferedReader br = new BufferedReader(reader)
//        ) {
//            String line;
//            String var = "";
//            while ((line = br.readLine()) != null) {
//                try {
//                    if (!line.startsWith("\t")) {
//                        var = line;
//                    }
//                    else {
//                        String o1 = line.replace("\t", "");
//                        String o2 = o1.split(", ")[0];
//                        pts2obj.computeIfAbsent(var, k -> new HashSet<>()).add(o2);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        try (
                FileReader reader = new FileReader("/home/xrbin/doophome/out/luindex-2obj/database/VarPointsToApp.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            String var = "";
            while ((line = br.readLine()) != null) {
                try {
                    pts2obj.computeIfAbsent(line.split("\t")[1], k -> new HashSet<>()).add(line.split("\t")[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Set<String> sameVar = new HashSet<>();
        pts.keySet().forEach(v -> {
            if (pts2obj.containsKey(v)) {
                sameVar.add(v);
            }
        });

        System.out.println(pts.keySet().size() + "\t" + pts2obj.keySet().size());

        for (String s : sameVar) {
            System.out.println(pts.get(s).size() - pts2obj.get(s).size() + "\t" + s + "\t" + pts.get(s).size() + "\t" + pts2obj.get(s).size());
            util.writeFilelnWithPrefix(pts.get(s).size() - pts2obj.get(s).size() + "\t" + s + "\t" + pts.get(s).size() + "\t" + pts2obj.get(s).size(), "onDemand/luindexVarPtsDiff");
            util.writeFileln(pts.get(s).size() - pts2obj.get(s).size() + "\t" + s + "\t" + pts.get(s).size() + "\t" + pts2obj.get(s).size(), "/home/xrbin/IdeaProjects/recommend/logs/onDemand/luindexVarPtsDiff");
        }

    }

    public static void func14() {
        Map<String, String> vatTimeDB = new HashMap<>();
        Map<String, String> vatTimeMine = new HashMap<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/luindex_/luindexTimeAll");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            String var = "";
            while ((line = br.readLine()) != null) {
                try {
                    vatTimeMine.put(line.split("\t")[0], line.split("\t")[1]);
                } catch (Exception e) {
                    System.err.println(line);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/testDB/sootOutput/luindex_02/varTimeAll");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    vatTimeDB.put(line.split("\t")[0], line.split("\t")[1]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Set<String> sameVar = new HashSet<>();
        vatTimeMine.keySet().forEach(v -> {
            if (vatTimeDB.containsKey(v)) {
                sameVar.add(v);
            }
        });

        System.out.println(vatTimeMine.keySet().size() + "\t" + vatTimeDB.keySet().size() + "\t" + sameVar.size());

        for (String s : sameVar) {
            util.writeFilelnWithPrefix(s + "\t" + vatTimeMine.get(s) + "\t" + vatTimeDB.get(s), "onDemand/luindexVarTimeDiff");
        }

    }

    public static void func14_01() {


        Set<String> hugeDependTreeSize = new HashSet<>();
        Map<String, Integer> dependTreeSize = new HashMap<>();
        Map<String, Integer> vatTimeMine = new HashMap<>();
        Map<String, Integer> vatTimeMineWithMAXDT = new HashMap<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/luindex01/luindexTimeAll");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            String var = "";
            while ((line = br.readLine()) != null) {
                try {
                    vatTimeMine.put(line.split("\t")[0], Integer.parseInt(line.split("\t")[1]));
                } catch (Exception e) {
                    System.err.println(line);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/luindex/DependTreeSize");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                hugeDependTreeSize.add(line.split("\t")[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(hugeDependTreeSize.size());

        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/luindex000/DependTreeSize");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                hugeDependTreeSize.remove(line.split("\t")[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(hugeDependTreeSize.size());


        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/luindex01/DependTreeSizeAll");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    dependTreeSize.put(line.split("\t")[0], Integer.parseInt(line.split("\t")[1]));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(hugeDependTreeSize.size());

//        for (String s : dependTreeSize.keySet()) {
//            System.out.println(s);
//        }

        Map<String, Set<String>> pts = new HashMap<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/luindex000/AllResult");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            String var = "";
            while ((line = br.readLine()) != null) {
                try {
                    if (!line.startsWith("\t")) {
                        var = line;
                        pts.computeIfAbsent(var, k -> new HashSet<>());
                    }
                    else {
                        pts.computeIfAbsent(var, k -> new HashSet<>()).add(line.replace("\t", ""));
                    }
                } catch (Exception e) {
                    System.err.println(line);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        dependTreeSize = new HashMap<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/luindex01/DependTreeSizeAll");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    dependTreeSize.put(line.split("\t")[0], Integer.parseInt(line.split("\t")[1]));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Set<String>> pts2obj = new HashMap<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/doophome/out/luindex-insen/database/VarPointsToApp.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    pts2obj.computeIfAbsent(varNameDoopToShimple(line.split("\t")[1]), k -> new HashSet<>());
                    if (!line.contains("<<string-buffer>>")
                            && !line.contains("<<string-builder>>")
                            && !line.contains("<<null pseudo heap>>")
                            && !line.contains("Exception")
                            && !line.contains("Error")
                            && !line.contains("Exhausted")
                            && !line.contains("SunJCE_e$p")
                    ) {
                        pts2obj.computeIfAbsent(varNameDoopToShimple(line.split("\t")[1]), k -> new HashSet<>()).add(line.split("\t")[0]);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

//        Map<String, Set<String>> pts1 = new HashMap<>();
//        try (
//                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/luindex/AllResult");
//                BufferedReader br = new BufferedReader(reader)
//        ) {
//            String line;
//            String var = "";
//            while ((line = br.readLine()) != null) {
//                try {
//                    if (!line.startsWith("\t")) {
//                        var = line;
//                        pts1.computeIfAbsent(var, k -> new HashSet<>());
//                    }
//                    else {
//                        pts1.computeIfAbsent(var, k -> new HashSet<>()).add(line.replace("\t", ""));
//                    }
//                } catch (Exception e) {
//                    System.err.println(line);
//                    e.printStackTrace();
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        int cc = 0, ccc = 0;
//        for (String s : pts1.keySet()) {
//            if (pts1.get(s).size() == 0 && pts2obj.containsKey(s) && pts2obj.get(s).size() != 0) {
//                cc++;
//                System.out.println(s + "\t" + dependTreeSize.get(s));
//            }
//            else {
//                ccc++;
//            }
//        }
//        System.out.println(cc + "\t" + ccc);

        Set<String> sameVar = new HashSet<>();
        pts.keySet().forEach(v -> {
            if (pts2obj.containsKey(v)) {
                sameVar.add(v);
            }
        });

//        for (String s : hugeDependTreeSize) {
//            if (sameVar.contains(s) && pts.get(s).size() != 0) {
//                System.out.println(pts.get(s).size() - pts2obj.get(s).size() + "\t" + s + "\t" + pts.get(s).size() + "\t" + pts2obj.get(s).size());
//                util.writeFileln(pts.get(s).size() - pts2obj.get(s).size() + "\t" + s + "\t" + pts.get(s).size() + "\t" + pts2obj.get(s).size(), "/home/xrbin/IdeaProjects/recommend/logs/onDemand/luindexVarPtsDiff");
//            }
//        }
//
//        System.out.println("---------------------");
//        for (String s : hugeDependTreeSize) {
//            if (pts.containsKey(s) && pts.get(s).size() == 0) {
//                System.out.println(s);
//            }
//        }

        for (String s : pts.keySet()) {
            if (pts.get(s).size() == 0 && pts2obj.containsKey(s) && pts.get(s).size() < pts2obj.get(s).size()) {
                if (true || hugeDependTreeSize.contains(s)) {
                    util.writeFileln(s + "\t" + dependTreeSize.get(s), "/home/xrbin/IdeaProjects/recommend/logs/onDemand/luindexVarPtsZeroMin");
                    System.out.println("\n" + s + "\t" + dependTreeSize.get(s));
                    pts2obj.get(s).forEach(System.out::println);
                }
            }
            if (!pts2obj.containsKey(s)) {
//                System.out.println(s);
            }
        }

    }

    public static void func14_antlr() {
        Set<String> hugeDependTreeSize = new HashSet<>();
        Map<String, Integer> dependTreeSize = new HashMap<>();

        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/antlr/DependTreeSize");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                hugeDependTreeSize.add(line.split("\t")[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(hugeDependTreeSize.size());

        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/antlr00/DependTreeSize");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                hugeDependTreeSize.remove(line.split("\t")[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(hugeDependTreeSize.size());

        Map<String, Set<String>> pts = new HashMap<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/antlr00/AllResult");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            String var = "";
            while ((line = br.readLine()) != null) {
                try {
                    if (!line.startsWith("\t")) {
                        var = line;
                        pts.computeIfAbsent(var, k -> new HashSet<>());
                    }
                    else {
                        pts.computeIfAbsent(var, k -> new HashSet<>()).add(line.replace("\t", ""));
                    }
                } catch (Exception e) {
                    System.err.println(line);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Set<String>> pts2obj = new HashMap<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/doophome/out/antlr-2obj/database/VarPointsToApp.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    pts2obj.computeIfAbsent(varNameDoopToShimple(line.split("\t")[1]), k -> new HashSet<>());
                    if (!line.contains("SunJCE_e$p")
//                            && !line.contains("<<string-buffer>>")
//                            && !line.contains("<<string-builder>>")
                            && !line.contains("<<null pseudo heap>>")
                            && !line.contains("Exception")
                            && !line.contains("Error")
                            && !line.contains("Exhausted")
                    ) {
                        pts2obj.computeIfAbsent(varNameDoopToShimple(line.split("\t")[1]), k -> new HashSet<>()).add(line.split("\t")[0]);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String s : pts.keySet()) {
            if (pts.get(s).size() == 0 && pts2obj.containsKey(s) && pts.get(s).size() < pts2obj.get(s).size()) {
                if (false || hugeDependTreeSize.contains(s)) {
                    util.writeFileln(s + "\t" + pts.get(s).size() + "\t" + pts2obj.get(s).size(), "/home/xrbin/IdeaProjects/recommend/logs/onDemand/antlrVarPtsZeroMin");
//                    System.out.println("\n" + s + "\t" + dependTreeSize.get(s));
//                    pts2obj.get(s).forEach(System.out::println);
                }
            }

            if (pts2obj.containsKey(s) && pts.get(s).size() == pts2obj.get(s).size()) {
                if (false || hugeDependTreeSize.contains(s)) {
                    util.writeFileln(s + "\t" + pts.get(s).size() + "\t" + pts2obj.get(s).size(), "/home/xrbin/IdeaProjects/recommend/logs/onDemand/antlrVarPtsSame");
//                    System.out.println("\n" + s + "\t" + dependTreeSize.get(s));
//                    pts2obj.get(s).forEach(System.out::println);
                }
            }

            if (pts2obj.containsKey(s) && pts.get(s).size() > pts2obj.get(s).size()) {
                if (false || hugeDependTreeSize.contains(s)) {
                    util.writeFileln(s + "\t" + pts.get(s).size() + "\t" + pts2obj.get(s).size(), "/home/xrbin/IdeaProjects/recommend/logs/onDemand/antlrVarPtsMax");
//                    System.out.println("\n" + s + "\t" + dependTreeSize.get(s));
//                    pts2obj.get(s).forEach(System.out::println);
                }
            }

            if (!pts2obj.containsKey(s)) {
                System.out.println(s);
            }
        }

    }

    public static void func14_luindex() {
        Set<String> hugeDependTreeSize = new HashSet<>();
        Map<String, Integer> dependTreeSize = new HashMap<>();

        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/luindex0000/DependTreeSize");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                hugeDependTreeSize.add(line.split("\t")[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(hugeDependTreeSize.size());

        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/luindex/DependTreeSize");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                hugeDependTreeSize.remove(line.split("\t")[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(hugeDependTreeSize.size());

        Map<String, Set<String>> pts = new HashMap<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/luindex/AllResult");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            String var = "";
            while ((line = br.readLine()) != null) {
                try {
                    if (!line.startsWith("\t")) {
                        var = line;
                        pts.computeIfAbsent(var, k -> new HashSet<>());
                    }
                    else {
                        pts.computeIfAbsent(var, k -> new HashSet<>()).add(line.replace("\t", ""));
                    }
                } catch (Exception e) {
                    System.err.println(line);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Set<String>> pts2obj = new HashMap<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/doophome/out/luindex-2obj/database/VarPointsToApp.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    pts2obj.computeIfAbsent(varNameDoopToShimple(line.split("\t")[1]), k -> new HashSet<>());
                    if (!line.contains("SunJCE_e$p")
//                            && !line.contains("<<string-buffer>>")
//                            && !line.contains("<<string-builder>>")
                            && !line.contains("<<null pseudo heap>>")
                            && !line.contains("Exception")
                            && !line.contains("Error")
                            && !line.contains("Exhausted")
                    ) {
                        pts2obj.computeIfAbsent(varNameDoopToShimple(line.split("\t")[1]), k -> new HashSet<>()).add(line.split("\t")[0]);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        int c = 0;
        for (String s : hugeDependTreeSize) {
            if (pts.containsKey(s)) {
                c++;
            }
            else {
                System.out.println(s);
            }
        }
        System.out.println("pts.keySet().size() = " + pts.keySet().size());
        System.out.println("c = " + c);
        if (true) return;

        for (String s : pts.keySet()) {
            if (pts.get(s).size() == 0 && pts2obj.containsKey(s) && pts.get(s).size() < pts2obj.get(s).size()) {
                if (false || hugeDependTreeSize.contains(s)) {
                    util.writeFileln(s + "\t" + pts.get(s).size() + "\t" + pts2obj.get(s).size(), "/home/xrbin/IdeaProjects/recommend/logs/onDemand/luindexVarPtsZeroMin");
//                    System.out.println("\n" + s + "\t" + dependTreeSize.get(s));
//                    pts2obj.get(s).forEach(System.out::println);
                }
            }

            if (pts2obj.containsKey(s) && pts.get(s).size() < pts2obj.get(s).size()) {
                if (false || hugeDependTreeSize.contains(s)) {
                    util.writeFileln(s + "\t" + pts.get(s).size() + "\t" + pts2obj.get(s).size(), "/home/xrbin/IdeaProjects/recommend/logs/onDemand/luindexVarPtsMin");
//                    System.out.println("\n" + s + "\t" + dependTreeSize.get(s));
//                    pts2obj.get(s).forEach(System.out::println);
                }
            }

            if (pts2obj.containsKey(s) && pts.get(s).size() == pts2obj.get(s).size()) {
                if (false || hugeDependTreeSize.contains(s)) {
                    util.writeFileln(s + "\t" + pts.get(s).size() + "\t" + pts2obj.get(s).size(), "/home/xrbin/IdeaProjects/recommend/logs/onDemand/luindexVarPtsSame");
//                    System.out.println("\n" + s + "\t" + dependTreeSize.get(s));
//                    pts2obj.get(s).forEach(System.out::println);
                }
            }

            if (pts2obj.containsKey(s) && pts.get(s).size() > pts2obj.get(s).size()) {
                if (false || hugeDependTreeSize.contains(s)) {
                    util.writeFileln(s + "\t" + pts.get(s).size() + "\t" + pts2obj.get(s).size(), "/home/xrbin/IdeaProjects/recommend/logs/onDemand/luindexVarPtsMax");
//                    System.out.println("\n" + s + "\t" + dependTreeSize.get(s));
//                    pts2obj.get(s).forEach(System.out::println);
                }
            }

            if (!pts2obj.containsKey(s)) {
                System.out.println(s);
            }
        }

    }

    public static void resRight() {
        Map<String, Set<String>> pts = new HashMap<>();
        Map<String, Integer> dependTreeSize = new HashMap<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/luindexSM01/AllResult");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            String var = "";
            while ((line = br.readLine()) != null) {
                try {
                    if (!line.startsWith("\t")) {
                        var = line;
                        pts.computeIfAbsent(var, k -> new HashSet<>());
                    }
                    else {
                        pts.computeIfAbsent(var, k -> new HashSet<>()).add(line.replace("\t", ""));
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
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/luindexSM01/DependTreeSizeAll");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                dependTreeSize.put(line.split("\t")[0],Integer.parseInt(line.split("\t")[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(pts.keySet().size());
        System.out.println(dependTreeSize.size());

        Map<String, Set<String>> pts2obj = new HashMap<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/doophome/out/luindex-2obj/database/VarPointsToApp.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    pts2obj.computeIfAbsent(varNameDoopToShimple(line.split("\t")[1]), k -> new HashSet<>());
                    if (!line.contains("SunJCE_e$p")
//                            && !line.contains("<<string-buffer>>")
//                            && !line.contains("<<string-builder>>")
                            && !line.contains("<<null pseudo heap>>")
                            && !line.contains("Exception")
                            && !line.contains("Error")
                            && !line.contains("Exhausted")
                    ) {
                        pts2obj.computeIfAbsent(varNameDoopToShimple(line.split("\t")[1]), k -> new HashSet<>()).add(line.split("\t")[0]);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String s : pts.keySet()) {
            if (pts2obj.containsKey(s)) {

            }
            else {
//                System.out.
            }
        }

        Set<String> sameVar = new HashSet<>();
        pts.keySet().forEach(v -> {
            if (pts2obj.keySet().contains(v)) {
                sameVar.add(v);
            }
        });

        System.out.println(pts.keySet().size() + "\t" + pts2obj.size());

        int count = 0;
        for (String s : sameVar) {
            if (pts.get(s).size() == 0 && pts2obj.get(s).size() != 0) {
                System.out.println(s + "\t" + dependTreeSize.get(s));
            }

            if (pts.get(s).size() == 0) {
                count++;
            }
            util.writeFileln(pts.get(s).size() - pts2obj.get(s).size() + "\t" + s + "\t" + pts.get(s).size() + "\t" + pts2obj.get(s).size(), "/home/xrbin/IdeaProjects/recommend/logs/onDemand/luindexVarPtsDiff");
        }
        System.out.println("coutn = " + count);
    }

    public static void func15() {
        Map<String, Set<String>> pts = new HashMap<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/dependTree");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            String var = "";
            while ((line = br.readLine()) != null) {
                try {
                    if (!line.startsWith("\t")) {
                        var = line;
                    }
                    else {
                        pts.computeIfAbsent(var, k -> new HashSet<>()).add(line.replace("\t", ""));
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
            if (pts.get(s).size() > 10
                    && !s.contains("[")
                    && !s.contains("\\.")
            ) {
                util.writeFileln(s, "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/luindex/dependMany");
            }
        }

    }

    public static void func16() {
        Set<String> vars = new HashSet<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/luindex/noRes");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    vars.add(line.split("\t")[0]);
                } catch (Exception e) {
                    System.err.println(line);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(vars.size());

        Map<String, Set<String>> pts2obj = new HashMap<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/doophome/out/luindex-2obj/database/VarPointsToApp.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    pts2obj.computeIfAbsent(varNameDoopToShimple(line.split("\t")[1]), k -> new HashSet<>());
                    if (!line.contains("SunJCE_e$p")
//                            && !line.contains("<<string-buffer>>")
//                            && !line.contains("<<string-builder>>")
                            && !line.contains("<<null pseudo heap>>")
                            && !line.contains("Exception")
                            && !line.contains("Error")
                            && !line.contains("Exhausted")
                    ) {
                        pts2obj.computeIfAbsent(varNameDoopToShimple(line.split("\t")[1]), k -> new HashSet<>()).add(line.split("\t")[0]);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String var : vars) {
            if (pts2obj.containsKey(var) && pts2obj.get(var).size() > 0) {
                System.out.println(var);
            }
        }

//        Map<String, Set<String>> pts = new HashMap<>();
//        try (
//                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/testDB/sootOutput/luindex-bk/allResult");
//                BufferedReader br = new BufferedReader(reader)
//        ) {
//            String line;
//            String var = "";
//            while ((line = br.readLine()) != null) {
//                try {
//                    if (!line.startsWith("\t")) {
//                        String var_origin = line.split("\t")[3];
//                        var = var_origin.split(" =")[0];
//                        pts.computeIfAbsent(var, k -> new HashSet<>());
//                    }
//                    else {
//                        pts.computeIfAbsent(var, k -> new HashSet<>()).add(line.replace("\t", ""));
//                    }
//                } catch (Exception e) {
//                    System.err.println(line);
//                    e.printStackTrace();
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    public static void func17() {
        Set<String> vars = new HashSet<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/luindex/AllVar");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    // <org.apache.lucene.index.SegmentMerger: void closeReaders()>/$stack6
                    String methodName = line.split("/")[0];
                    String varName = line.split("/")[1];
                    String className = methodName.split(": ")[0];
                    className = className.replace("<", "");
                    String packageName = "";
                    if (className.contains(".")) {
                        packageName = className.substring(0, className.lastIndexOf("."));
                    }
                    System.out.println(methodName);
                    System.out.println(varName);
                    System.out.println(className);
                    System.out.println(packageName);
                    vars.add(line);
                    break;
                } catch (Exception e) {
                    System.err.println(line);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(vars.size());
    }

    public static void func18() {
        Set<String> vars0 = new HashSet<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/luindex/AllVar");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                vars0.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(vars0.size());

        Set<String> varsMine = new HashSet<>();
        Set<String> vars06 = new HashSet<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/lusearch/DependTreeSizeAll");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.split("\t")[0];
                varsMine.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/testDB/sootOutput/lusearch/notDoneVar");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.contains("EmptyPointsToSet")) {
                    line = line.split("\t")[0];
                    vars06.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Integer> dependTreeSize = new HashMap<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/lusearch/DependTreeSizeAll");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                dependTreeSize.put(line.split("\t")[0], Integer.parseInt(line.split("\t")[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(dependTreeSize.size());
        System.out.println(varsMine.size());
        System.out.println(vars06.size());

        vars06.forEach(var -> {
            if (varsMine.contains(var)) {
                util.writeFilelnWithPrefix(var, "onDemand/test");
                System.out.println(var + "\t" + dependTreeSize.get(var));
            }
        });
    }

    public static void fileSame() {
        String file1 = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/luindex_2obj/allReachableVars";
        String file2 = "/home/xrbin/IdeaProjects/MyDB/logs/luindexOrigin/allVar";
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


        strings02.forEach(s -> {
            if (strings01.contains(s)) {
                util.writeFilelnWithPrefix(s, "onDemand/fileSame");
            }
        });
    }

    public static void sameVars() {
        sameVars("luindex");
        sameVars("lusearch");
        sameVars("bloat");
//        sameVars("antlr");
//        sameVars("fop");
        sameVars("pmd");
//        sameVars("hsqldb");
        sameVars("chart");
        sameVars("eclipse");
//        sameVars("xalan");
        sameVars("jython");
    }

    public static void sameVars(String testName) {
        String file1 = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/allvar/" + testName + "AllVars";
        String file2 = "/home/xrbin/IdeaProjects/MyDB/logs/allvar/" + testName + "AllVars";
        String file3 = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/allvar/" + testName + "02";
        String file4 = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/allvar/" + testName + "SameVars";
//        Set<String> strings01 = new HashSet<>();
//        try (
//                FileReader reader = new FileReader(file1);
//                BufferedReader br = new BufferedReader(reader)
//        ) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                strings01.add(line);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println(strings01.size());
//
//        Set<String> strings02 = new HashSet<>();
//        try (
//                FileReader reader = new FileReader(file2);
//                BufferedReader br = new BufferedReader(reader)
//        ) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                strings02.add(line);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println(strings02.size());

        Set<String> strings04 = new HashSet<>();
        try (
                FileReader reader = new FileReader(file3);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                strings04.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(testName + " 02: " + strings04.size());

        Set<String> strings05 = new HashSet<>();
        try (
                FileReader reader = new FileReader(file4);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                strings05.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public static void fileDiff02() {
        String file1 = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/luindex_cs/TimeAll";
        String file2 = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/luindex_cs_simple/noRes";
        Set<String> strings01 = new HashSet<>();
        try (
                FileReader reader = new FileReader(file1);
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
                FileReader reader = new FileReader(file2);
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

//        strings01.forEach(s -> {
//            if (!strings02.contains(s)) {
//                util.writeFilelnWithPrefix(s, "onDemand/luindexDiffMeNot");
//            }
//        });
        strings02.forEach(s -> {
            if (strings01.contains(s)) {
                System.out.println(s + "\t" + map.get(s));
//                util.writeFilelnWithPrefix(s, "onDemand/luindexDiffItNot");
            }
        });
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
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/luindex/luindex/noRes");
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
            noResVarDependSize.put(var, varDependSize.get(var));
            System.out.println(var + "\t" + varDependSize.get(var));
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
        compareCan("luindex");
        compareCan("lusearch");
        compareCan("bloat");
        compareCan("pmd");
        compareCan("eclipse");
        compareCan("jython");
    }

    public static void compareCan(String name) {
        String file0 = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/" + name + "/" + name + "" + "2/AllVar";
        String file00 = "/home/xrbin/IdeaProjects/MyDB/logs/" + name + "/" + name + "" + "1/luindexVars";

        String file1 = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/" + name + "/" + name + "" + "2/TimeAll";
        String file10 = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/" + name + "/" + name + "" + "0/TimeAll";
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

        Map<String, Integer> map010 = new HashMap<>();
        try (
                FileReader reader = new FileReader(file10);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                map010.put(line.split("\t")[0], Integer.parseInt(line.split("\t")[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

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


        Set<String> sameVars = new HashSet<>();
        Set<String> sameVarsSM = new HashSet<>();

        for (String s : map010.keySet()) {
            if (map02.containsKey(s)) {
                sameVars.add(s);
            }
        }

        for (String s : map01.keySet()) {
            if (map02.containsKey(s)) {
                sameVarsSM.add(s);
            }
        }

        int itTime = 0;
        int itTimeSm = 0;
        int myTime = 0;
        int myTimeSm = 0;
        for (String s : sameVars) {
            itTime += map02.get(s);
            myTime += map010.get(s);
        }
        for (String s : sameVarsSM) {
            itTimeSm += map02.get(s);
            myTimeSm += map01.get(s);
        }

//        System.out.println(name + ": " + vars.size() + "\t" + map01.keySet().size() + "\t" + map010.keySet().size() + "\t" + map02.keySet().size());
        System.out.println(map01.keySet().size() + "\t" + map010.keySet().size() + "\t" + map02.keySet().size() + "\t" + vars.size() + "\t" + vars2.size() + "\t" + name);
//        System.out.println(name + ": " + itTime + "\t" + myTime);
//        System.out.println(name + ": " + itTimeSm + "\t" + myTimeSm);


    }

    public static void batchTimeCompare(String name) {
        String file1 = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/" + name + "NoSMBatch/Time";
        String file2 = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/" + name + "NoSM/TimeAll";
//        String file3 = "/home/xrbin/IdeaProjects/MyDB/logs/" + name + "-yes";
        Map<String, Integer> noBatchEachTime = new HashMap<>();
        try (
                FileReader reader = new FileReader(file2);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                Integer time = Integer.parseInt(line.split("\t")[1]);
                noBatchEachTime.putIfAbsent(line.split("\t")[0], time);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

//        Map<String, Integer> eachTime06 = new HashMap<>();
//        try (
//                FileReader reader = new FileReader(file3);
//                BufferedReader br = new BufferedReader(reader)
//        ) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                Integer time1 = Integer.parseInt(line.split("\t")[1]);
//                Integer time2 = Integer.parseInt(line.split("\t")[2]);
//                eachTime06.putIfAbsent(line.split("\t")[0], time1 + time2);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        int batchTime = 0;
        int batchCount = 0;
        Set<String> vars = new HashSet<>();
        try (
                FileReader reader = new FileReader(file1);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("----------------")) {
                    batchCount++;
                    batchTime += Integer.parseInt(line.split("\t")[1]);
                }
                else {
                    vars.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        int noBatchTime = 0;
        for (String var : vars) {
            if (noBatchEachTime.containsKey(var)) {
                noBatchTime += noBatchEachTime.get(var);
            }
            else {
//                System.out.println(var);
            }
//            if (noBatchEachTime.containsKey(var)) {
//                noBatchTime += noBatchEachTime.get(var);
//            }
//            else {
//                System.out.println(var);
//            }
        }

        System.out.println("----" + name + "----" + batchCount + "----" + vars.size());
        System.out.println("batchTime = " + batchTime);
        System.out.println("noBatchTime = " + noBatchTime);
    }

    public static void timeCompare(String name) {
        String file1 = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/" + name + "NoSM/TimeAll";
        String file2 = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/" + name + "SM/TimeAll";
        String file3 = "/home/xrbin/IdeaProjects/MyDB/logs/" + name + "-yes/varTimeAll";

        Map<String, Integer> timeMap = new HashMap<>();
        try (
                FileReader reader = new FileReader(file1);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            Integer integer = 0;
            while ((line = br.readLine()) != null) {
                integer = Integer.parseInt(line.split("\t")[1]);
                timeMap.put(line.split("\t")[0], integer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Integer> smTimeMap = new HashMap<>();
//        try (
//                FileReader reader = new FileReader(file2);
//                BufferedReader br = new BufferedReader(reader)
//        ) {
//            String line;
//            Integer integer = 0;
//            while ((line = br.readLine()) != null) {
//                integer = Integer.parseInt(line.split("\t")[1]);
//                smTimeMap.put(line.split("\t")[0], integer);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        Map<String, Integer> myDBTimeMap = new HashMap<>();
        try (
                FileReader reader = new FileReader(file3);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            Integer integer = 0;
            while ((line = br.readLine()) != null) {
                integer = Integer.parseInt(line.split("\t")[1]);
                myDBTimeMap.put(line.split("\t")[0], integer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Set<String> sameVars = new HashSet<>();
        Set<String> sameVarsSM = new HashSet<>();

        for (String s : myDBTimeMap.keySet()) {
            if (timeMap.containsKey(s)) {
//                System.out.println(s);
                sameVars.add(s);
            }
        }

        for (String s : myDBTimeMap.keySet()) {
            if (smTimeMap.containsKey(s)) {
                sameVarsSM.add(s);
            }
        }

        int itTime = 0;
        int myTime = 0;
        for (String s : sameVars) {
            itTime += myDBTimeMap.get(s);
            myTime += timeMap.get(s);
        }

        System.out.println("\n" + name + " var size = " + sameVars.size() + ", itTime = " + itTime + ", myTime = " + myTime);
    }

    public static void timeCompare02(String name) {
        String file1 = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/" + name + "/" + name + "/TimeAll";
        String file2 = "/home/xrbin/IdeaProjects/Qilin_ddpt/logs/" + name + "/" + name + "2/TimeAll";

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

        Map<String, Integer> timeMap02 = new HashMap<>();
        try (
                FileReader reader = new FileReader(file2);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            int integer;
            while ((line = br.readLine()) != null) {
                integer = Integer.parseInt(line.split("\t")[1]);
                timeMap02.put(line.split("\t")[0], integer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        int itTime = 0;
        int myTime = 0;

        for (String s : timeMap.keySet()) {
            itTime += timeMap.get(s);
        }
        for (String s : timeMap02.keySet()) {
            myTime += timeMap02.get(s);
        }

        System.out.println(timeMap.keySet().size() + "\t" + timeMap02.keySet().size() + ", itTime = " + itTime + ", myTime = " + myTime);
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

}
