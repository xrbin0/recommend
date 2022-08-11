package com.xrbin.rc;

import com.xrbin.ddpt.IntroValueFlowGraph;
import com.xrbin.ddpt.Main;
import com.xrbin.ddpt.model.*;
import com.xrbin.ddpt.utils;
import com.xrbin.rc.model.StoreInstanceField;
import com.xrbin.utils.Statistics;
import com.xrbin.utils.util;
import jas.Pair;
import soot.*;
import soot.jimple.*;
import soot.jimple.internal.*;
import soot.toolkits.graph.DirectedGraph;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class RecommendCtxSen {
    public static DatabaseManager database = DatabaseManager.getInstance();
    public static Statistics stat = new Statistics();
    public static Vector<String> rcVar = new Vector<>();
    public static HashSet<String> rcMethod = new HashSet<>();

//    public static String projectName2Obj = "fop-2obj";
//    public static String projectNameInsen = "fop-insen";
//    public static String projectName2Obj = "DDPT-2obj";
//    public static String projectNameInsen = "DDPT-insen";
    public static String projectName2Obj = "jieba-2obj";
    public static String projectNameInsen = "jieba-insen";
//    public static String projectName2Obj = "paoding-2obj";
//    public static String projectNameInsen = "paoding-insen";

    public static String path = "/home/xrbin/Desktop/doophome/out/" + projectNameInsen + "/database/";

    public static boolean flag_main = true;

    static {
        try {
            Runtime.getRuntime().exec("rm logs/argDiff");
        } catch (Exception e) {
            System.err.print("");
        }
    }
// /home/xrbin/Desktop/doophome/NVPT-jieba-appVar
    public static void main(String[] args) {


//        utils.JARPTAH = "/home/xrbin/Desktop/Java_Project/fop/build/fop.jar";
//        utils.MAINCLASS = "org.apache.fop.cli.Main";
//        utils.DATABASE = "/home/xrbin/Desktop/doophome/out/" + projectNameInsen + "/database/";

        utils.JARPTAH = "/home/xrbin/Desktop/Java_Project/jieba-analysis-master/target/jieba-analysis-1.0.3-SNAPSHOT.jar";
        utils.MAINCLASS = "com.qianxinyao.analysis.jieba.keyword.TFIDFAnalyzer";
        utils.DATABASE = "/home/xrbin/Desktop/doophome/out/" + projectNameInsen + "/database/";

//        utils.JARPTAH = "/home/xrbin/Desktop/Java_Project/DDPT_test/build/libs/all.jar";
//        utils.MAINCLASS = "com.xrbin.ddptTest.test.test1";
//        utils.DATABASE = "/home/xrbin/Desktop/doophome/out/all-2obj/" + "/database/";

//        utils.JARPTAH = "/home/xrbin/Desktop/DDPT/build/libs/DDPT-1.0-SNAPSHOT.jar";
//        utils.MAINCLASS = "com.Test";
//        utils.DATABASE = "/home/xrbin/Desktop/doophome/out/" + projectNameInsen +"/database/";

//        utils.JARPTAH = "/home/xrbin/Desktop/Java_Project/elasticsearch-analysis-paoding-master/target/elasticsearch-analysis-paoding-1.2.0.jar";
//        utils.MAINCLASS = "net.paoding.analysis.analyzer.PaodingAnalyzer";
//        utils.DATABASE = "/home/xrbin/Desktop/doophome/out/" + projectNameInsen +"/database/";

//        appVarToFeedbacks();

//        rc1();
//        rc2();
//        rc3();
//        rc4();

//        sort1("1");
//        sort1("2");
//        sort1("3");

//        util1(1);
//        util1(2);
//        util1(3);

        spiltFile();
//        vptSubVpt();
//        sort1("");
//        sort1("rc");

//        sort1("");
//        init();
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
                FileReader reader = new FileReader("/home/xrbin/Desktop/recommend/logs/appVar-jieba");
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
                FileReader reader = new FileReader("/home/xrbin/Desktop/recommend/logs/recommendVarAndScore-rc");
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
                FileReader reader = new FileReader(path + "VarPointsTo.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] sa = line.split("\t");
                database.vptInsen.computeIfAbsent(sa[3], k -> new HashSet<>()).add(new Allocation(sa[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader("/home/xrbin/Desktop/doophome/out/" + projectName2Obj + "/database/VarPointsTo.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] sa = line.split("\t");
                database.vptInsen.computeIfAbsent(sa[3], k -> new HashSet<>()).add(new Allocation(sa[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader("/home/xrbin/Desktop/recommend/logs/rc" + index);
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
        for (String m : database.cgEdge.keySet()) {
//            if(database.appMethod.contains(m)) {
//                System.out.println(m);
            HashSet<Allocation> countBaseObject = new HashSet<>();
            database.cgEdge.get(m).forEach(methodInvoke -> {
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

        rcVarToFeedbacks("/home/xrbin/Desktop/doophome/NVPT/NVPT1", "1");
        sort1("1");
    }

    public static int countInvoke = 0;

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
            String m = s.getMethod();
            if (database.cgEdge.containsKey(m)) {
                database.cgEdge.get(m).forEach(actualM -> {
                    if (database.vptInsen.containsKey(database.methodInvokeToBase.get(actualM))) {
                        countInvokeRc4 += database.vptInsen.get(database.methodInvokeToBase.get(actualM)).size();
                    }
                });
                if (countInvokeRc4 > 1) {
                    HashSet<Allocation> Os = database.vptInsen.get(s.getBase());
                    if (Os != null) {
                        for (Allocation o : Os) {
                            Field f = new Field(new CSAllocation(o), s.getSignature());
                            if (database.load.containsKey(f)) {
                                rcVar.addAll(database.load.get(f));
                            }
                        }
                    }
                }
            }

        });

        rcVar.forEach(s -> {
            if (database.vptInsen.containsKey(s)) {
//                util.writeFilelnWithPrefix(s, "recommendVar-rc2");
//              util.plnG(s);
            }
            util.writeFilelnWithPrefix(s, "recommendVar-rc2");
        });

        util.plnB("rcVar.size() = " + rcVar.size() + "");

        rcVarToFeedbacks("/home/xrbin/Desktop/doophome/NVPT/NVPT2", "2");
        sort1("2");
    }

    public static void rc3() {
        try {
            Runtime.getRuntime().exec("rm logs/rc3");
            Runtime.getRuntime().exec("rm logs/recommendVar-rc3");
            Runtime.getRuntime().exec("rm logs/recommendVarAndScore-rc3");
        } catch (Exception e) {
            System.err.print("");
        }

        if(flag_main) {
            Main.run();
            flag_main = false;
        }
        /*
        对于所有被多次调用的方法的调用点接收返回值的变量 -> a = b.f()中的a
         */

        int count = 0;
        for (String m : database.cgEdge.keySet()) {
//            if(database.appMethod.contains(m)) {
//                System.out.println(m);
            HashSet<Allocation> countBaseObject = new HashSet<>();
            database.cgEdge.get(m).forEach(methodInvoke -> {
//                    System.out.println("\t" + methodInvoke);
                if (database.vpt2obj.containsKey(database.methodInvokeToBase.get(methodInvoke)))
                    countBaseObject.addAll(database.vpt2obj.get(database.methodInvokeToBase.get(methodInvoke)));
            });
            // 方法被多次调用（对于对象上下文敏感来说，只要base指向的所有对象超过1就算）
            if (countBaseObject.size() > 1) {
//                System.out.println("\t" + m);
                count++;
                database.cgEdge.get(m).forEach(methodInvoke -> {
                    if (database.vpt2obj.containsKey(database.methodInvokeToBase.get(methodInvoke))) {
                        rcVar.add(database.assignReturnValue.get(methodInvoke));
                    }
                });
            }
//            }
        }
        System.out.println("count = " + count);

        rcVar.forEach(s -> {
            util.writeFilelnWithPrefix(s, "recommendVar-rc3");
        });

        util.plnB("rcVar.size() = " + rcVar.size() + "");

        rcVarToFeedbacks("/home/xrbin/Desktop/doophome/NVPT/NVPT3", "3");
        sort1("3");
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
            for (String m : database.cgEdge.keySet()) {
                indexToArg = new HashMap<>();
                HashSet<Allocation> countBaseObject = new HashSet<>();
                database.cgEdge.get(m).forEach(methodInvoke -> {
                    if (database.vpt2obj.containsKey(database.methodInvokeToBase.get(methodInvoke))) {
                        if (countBaseObject.addAll(database.vpt2obj.get(database.methodInvokeToBase.get(methodInvoke)))) {
                            countBaseObj++;
                        }
                    }
                });
                if (countBaseObj > 1) {
                    Body b = Main.wpCFG.getBodys().get(m);
                    if (b == null) continue;

                    if (database.cgEdge.containsKey(m)) {
                        database.cgEdge.get(m).forEach(invokeIntr -> {
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
                                            for (Allocation o : database.vptInsen.get(arg1)) {
                                                if (!database.vptInsen.get(arg2).contains(o)) {
                                                    flag = true;
                                                }
                                            }
                                            if (!flag) {
                                                for (Allocation o : database.vptInsen.get(arg2)) {
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
            for (String m : database.cgEdge.keySet()) {
                HashSet<Allocation> countBaseObject = new HashSet<>();
                database.cgEdge.get(m).forEach(methodInvoke -> {
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
                    if (database.cgEdge.containsKey(m) && rcMethod.contains(m)) {
                        if (args(m)) {
                            System.out.println("yes\t\t" + m);
                            database.cgEdge.get(m).forEach(actualM -> {
                                if (database.vptInsen.containsKey(database.methodInvokeToBase.get(actualM))) {
                                    countInvokeRc4 += database.vptInsen.get(database.methodInvokeToBase.get(actualM)).size();
                                }
                            });
                            if (countInvokeRc4 > 1 && database.vptInsen.containsKey(s.getBase()) && database.vptInsen.get(s.getBase()).size() > 1) {
                                HashSet<Allocation> Os = database.vptInsen.get(s.getBase());
                                if (Os != null) {
                                    for (Allocation o : Os) {
                                        Field f = new Field(new CSAllocation(o), s.getSignature());
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

        rcVarToFeedbacks("/home/xrbin/Desktop/doophome/NVPT/NVPT4", "4");
        sort1("4");
    }

    public static boolean res = false;
    public static HashMap<String, HashSet<String>> indexToArg = new HashMap<>();

    public static boolean args(String m) {
        res = false;
        indexToArg = new HashMap<>();
        if (database.cgEdge.containsKey(m)) {
            database.cgEdge.get(m).forEach(invokeIntr -> {
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
                                for (Allocation o : database.vptInsen.get(arg1)) {
                                    if (!database.vptInsen.get(arg2).contains(o)) {
                                        util.writeFilelnWithPrefix(m + "\n\t" + arg1 + "\n\t" + arg2, "argDiff");
                                        return true;
                                    }
                                }
                                for (Allocation o : database.vptInsen.get(arg2)) {
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
                                for (Allocation o : database.vptInsen.get(var)) {
                                    Field f = new Field(new CSAllocation(o), ifr.getField().toString());
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

    public static boolean sort1Flag = false;
    public static String curVar = "";
    public static String curRcVar = "";
    public static Integer[] curVars = { 125,
            99,
            77,
            63,
            96,
            57,
            97,
            98,
            100,
            101,
            102,
            114,
    };
    public static HashSet<String> curVarsSet = new HashSet<>();
    public static HashMap<String, Integer> rcVarToInteger = new HashMap<>();
    public static void sort1(String rc) {
        curVarsSet.add(curVar);

        try {
            Runtime.getRuntime().exec("rm logs/recommendVarAndScore-rc" + rc);
            Runtime.getRuntime().exec("rm logs/ivfgDfs");
            Runtime.getRuntime().exec("rm logs/ivfgDfsOnly");
        } catch (Exception e) {
            System.err.print("");
        }

        if (flag_main) {
            Main.run();
            flag_main = false;
        }

        try (
                FileReader reader = new FileReader("/home/xrbin/Desktop/recommend/logs/appVarJieba" + rc);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            int i = 0;
            while ((line = br.readLine()) != null) {
//                System.out.println("line = " + line);
//                rcVar.add(line);
                rcVar.add(line.split("\t")[0]);
                rcVarToInteger.put(line.split("\t")[0], i);
                if(Arrays.asList(curVars).contains(i)) {
                    curVarsSet.add(line.split("\t")[0]);
                }
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        HashMap<Unit, String> unitToRcVar = new HashMap<>();
        HashMap<String, Integer> rcVarToScore = new HashMap<>();
        Main.wpCFG.getUnits().forEach(unit -> {
//            if(Main.wpCFG.getMethodOf(unit).toString().equals("<sun.util.locale.LocaleObjectCache: java.lang.Object get(java.lang.Object)>")) {
//                System.out.println(unit);
//            }
            if ((unit instanceof AssignStmt) && ((AssignStmt) unit).getLeftOp() instanceof Local) {
                String l = Main.wpCFG.getMethodOf(unit).toString() + "/" + ((AssignStmt) unit).getLeftOp().toString();
                if (database.nameToPname.containsKey(l)) {
                    if (rcVar.contains(database.nameToPname.get(l))) {
                        rcVar.remove(database.nameToPname.get(l));
                        unitToRcVar.put(unit, database.nameToPname.get(l));
                    }
                }
            }
            else if (unit instanceof IdentityStmt) {
                String l = Main.wpCFG.getMethodOf(unit).toString() + "/" + ((IdentityStmt) unit).getLeftOp().toString();
                if (database.nameToPname.containsKey(l)) {
                    if (rcVar.contains(database.nameToPname.get(l))) {
                        rcVar.remove(database.nameToPname.get(l));
                        unitToRcVar.put(unit, database.nameToPname.get(l));
                    }
                }
            }
            else if (unit instanceof JReturnStmt) {
                String l = Main.wpCFG.getMethodOf(unit).toString() + "/" + ((JReturnStmt) unit).getOp().toString();
                if (database.nameToPname.containsKey(l)) {
                    if (rcVar.contains(database.nameToPname.get(l))) {
                        rcVar.remove(database.nameToPname.get(l));
                        unitToRcVar.put(unit, database.nameToPname.get(l));
                    }
                }
            }
        });

        rcVar.clear();
        unitToRcVar.keySet().forEach(k -> {
//            System.out.println(k + "\t" + unitToRcVar.get(k));
//            rcVar.add(unitToRcVar.get(k));
        });
        unitToRcVar.keySet().forEach(k -> {
            if(curVarsSet.contains(unitToRcVar.get(k))) {
                rcVar.add(unitToRcVar.get(k));
                System.out.println(k + "\t" + unitToRcVar.get(k));
                curRcVar = unitToRcVar.get(k);
                sort1Flag = true;

                database.vpt2obj = new HashMap<>();
                try (
                        FileReader reader = new FileReader("/home/xrbin/Desktop/doophome/out/jieba-fb" + rcVarToInteger.get(unitToRcVar.get(k)) + "/database/VarPointsTo.csv");
                        BufferedReader br = new BufferedReader(reader)
                ) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] sa = line.split("\t");
                        database.vpt2obj.computeIfAbsent(sa[3], key -> new HashSet<>()).add(new Allocation(sa[1]));
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                sort1Flag = false;
            }
//            sort1Flag = curVar.equals(unitToRcVar.get(k));
//            System.out.println("-------------------- To Sort -------------------" + unitToRcVar.get(k));

            rcVarToScore.put(unitToRcVar.get(k), dfsSort1(k));
        });

        rcVar.forEach(s -> {
//            if (database.vptInsen.containsKey(s)) {
            util.writeFilelnWithPrefix(s + "\t" + rcVarToScore.get(s), "recommendVarAndScore-rc" + rc);
//            }
        });
    }

    public static int index = 0;
    public static int cc = 0;
    public static int ccc = 0;
    public static Unit u;
    public static VFGvalue vv;
    public static String predRetVar = "";
    public static HashSet<String> retVar = new HashSet<>();
    public static Stack<Pair<Unit, VFGvalue>> dfsStack = new Stack<>();
    public static Integer dfsSort1(Unit root) {
        int res = 0;
        dfsStack = new Stack<>();

        if (false) {
            HashSet<Pair<Unit, VFGvalue>> color = new HashSet<>(); // VFGvalue是来自上一个unit的可能被改变的值
            if (root instanceof AssignStmt || root instanceof IdentityStmt) {
                dfsStack.push(new Pair<>(root, null));
            }
            while (!dfsStack.empty()) {
                Pair<Unit, VFGvalue> uv = dfsStack.pop();
                u = uv.getO1();
                vv = uv.getO2();

                if (vv == null) { // 处理第一个(root)
                    if (u instanceof AssignStmt) {
                        for (Unit succ : Main.ivfg.getSuccs(u)) {
                            Main.ivfg.getEdgeValue(u, succ).forEach(vvv -> {
                                if (vvv.getValue().equals(((AssignStmt) u).getLeftOp())) {
                                    dfsStack.push(new Pair<>(succ, new VFGvalue(((AssignStmt) u).getLeftOp())));
                                }
                            });
                        }
                    }
                    else if (u instanceof IdentityStmt) {
                        for (Unit succ : Main.ivfg.getSuccs(u)) {
                            Main.ivfg.getEdgeValue(u, succ).forEach(vvv -> {
                                if (vvv.getValue().equals(((IdentityStmt) u).getLeftOp())) {
                                    dfsStack.push(new Pair<>(succ, new VFGvalue(((IdentityStmt) u).getLeftOp())));
                                }
                            });
                        }
                    }
                    continue;
                }

                if (color.add(uv)) {
//                System.out.println(uv.getO1());
                    unitToVarToDiffVPT(u);
                    res += unitToVarToVPTSize(u);
                    // 对比值流图和指针集上的差距
                    for (Unit succ : Main.ivfg.getSuccs(u)) {

                        if (u instanceof JAssignStmt) { // 无论前面来的是什么都会对left造成影响
                            JAssignStmt assignStmt = (JAssignStmt) u;
                            Value left = assignStmt.getLeftOp();
                            Value right = assignStmt.getRightOp();

                            if (left instanceof Local) {
                                VFGvalue curV = new VFGvalue(left);
//                            dfsStack.push(new Pair<>(succ, curV));  // a = ? 无论值流的值是涉密 ？是什么，a的值都有可能收到影响，但是可以细分，a = f()不传递，但其实可以通过返回值间接传递
                                if (right instanceof InstanceInvokeExpr && succ instanceof IdentityStmt) {
                                    if (vv.equals(new VFGvalue(left))) {
                                        // 对应returnStmt里面的特殊处理
                                        Main.ivfg.getEdgeValue(u, succ).forEach(vvvvvv -> {
                                            if (vvvvvv.equals(vv)) {
                                                dfsStack.push(new Pair<>(succ, vv));
                                            }
                                        });
                                    }
                                    else if (succ.toString().contains("@parameter")) {
                                        invokeArgDiffVpt((InstanceInvokeExpr) right, succ);
                                    }
                                    else if (succ.toString().contains("this")) {
                                        if (((InstanceInvokeExpr) right).getBase().equals(vv.getValue())) {
                                            invokeThisDiffVpt((InstanceInvokeExpr) right, succ);
                                        }
                                    }
                                }
                                else if (right instanceof StaticInvokeExpr && succ instanceof IdentityStmt) {
                                    if (vv.equals(new VFGvalue(left))) {
                                        // 对应returnStmt里面的特殊处理
                                        dfsStack.push(new Pair<>(succ, vv));
                                    }
                                    else if (succ.toString().contains("@parameter")) {
                                        invokeArgDiffVpt((StaticInvokeExpr) right, succ);
                                    }
                                }
                                else if (right instanceof InstanceFieldRef) {
                                    // 两种值流都导向同一个结果
                                    if (((InstanceFieldRef) right).getBase().equals(vv.getValue())) {
//                                        dfsStack.push(new Pair<>(succ, curV));
                                    }
                                    else {
                                        dfsStack.push(new Pair<>(succ, curV));
                                    }
                                }
                                else if (right instanceof StaticFieldRef) {
                                    dfsStack.push(new Pair<>(succ, curV));
                                }
                                else if (right instanceof CastExpr) {
                                    continue;
                                }
                                else {
                                    dfsStack.push(new Pair<>(succ, curV));
                                }
                            }
                            else if (left instanceof ArrayRef) {
                                dfsStack.push(new Pair<>(succ, new VFGvalue(((ArrayRef) left).getBase())));
                            }
                            else if (left instanceof InstanceFieldRef) {
                                if (succ instanceof AssignStmt && ((AssignStmt) succ).getRightOp() instanceof InstanceFieldRef) {
                                    if (vv.getValue().equals(((InstanceFieldRef) left).getBase())) {
                                        for (VFGvalue vvvv : Main.ivfg.getEdgeValue(u, succ)) {
                                            if (vvvv.equals(vv)) {
//                                                dfsStack.push(new Pair<>(succ, vvvv));
                                            }
                                        }
                                    }
                                    else if (vv.getValue().equals(right)) {
                                        for (VFGvalue vvvv : Main.ivfg.getEdgeValue(u, succ)) {
                                            dfsStack.push(new Pair<>(succ, vvvv));
                                        }
                                    }
                                }
                            }
                            else if (left instanceof StaticFieldRef) {
                                for (VFGvalue vvvv : Main.ivfg.getEdgeValue(u, succ)) {
                                    dfsStack.push(new Pair<>(succ, vvvv));
                                }
                            }
                        }
                        else if (u instanceof JIdentityStmt) {
                            Value left = ((JIdentityStmt) u).getLeftOp();
                            Value right = ((JIdentityStmt) u).getRightOp();
                            if (left instanceof Local) {
                                dfsStack.push(new Pair<>(succ, new VFGvalue(left)));
                            }
                        }
                        else if (u instanceof JInvokeStmt) {
                            if (((JInvokeStmt) u).getInvokeExpr() instanceof InstanceInvokeExpr) {
                                if (succ.toString().contains("@parameter")) {
                                    invokeArgDiffVpt((InstanceInvokeExpr) ((JInvokeStmt) u).getInvokeExpr(), succ);
                                }
                                else if (succ.toString().contains("this")) {
                                    if (((InstanceInvokeExpr) ((JInvokeStmt) u).getInvokeExpr()).getBase().equals(vv.getValue())) {
                                        invokeThisDiffVpt((InstanceInvokeExpr) ((JInvokeStmt) u).getInvokeExpr(), succ);
                                    }
                                }
                            }
                            else if (((JInvokeStmt) u).getInvokeExpr() instanceof StaticInvokeExpr) {
                                invokeArgDiffVpt((StaticInvokeExpr) ((JInvokeStmt) u).getInvokeExpr(), succ);
                            }
                        }
                        else if (u instanceof JReturnStmt) {
                            boolean out = false;
                            for (VFGvalue vvvv : Main.ivfg.getEdgeValue(u, succ)) {
                                if (succ instanceof AssignStmt && ((AssignStmt) succ).getRightOp() instanceof InterfaceInvokeExpr) {
                                    JAssignStmt assignStmt = (JAssignStmt) succ;
                                    Value left = assignStmt.getLeftOp();
                                    Value right = assignStmt.getRightOp();

                                    if (left instanceof Local && right instanceof InterfaceInvokeExpr) {
                                        InterfaceInvokeExpr v = (InterfaceInvokeExpr) right;
                                        String methodName = Main.wpCFG.getMethodOf(succ).toString() + "/" + v.getMethod().getDeclaringClass() + "." + v.getMethod().getName();
                                        methodName = DatabaseManager.getInstance().mil.get(methodName + "/" + utils.getLineNumber(succ));

                                        if (out) System.out.println("\n" + u + "\t" + methodName);
                                        if (DatabaseManager.getInstance().cg.get(methodName) != null) {
                                            retVar = new HashSet<>();
                                            for (String cm : DatabaseManager.getInstance().cg.get(methodName)) {
                                                if (out) System.out.println("\tcm :" + cm);
                                                DirectedGraph<Unit> cfg = Main.wpCFG.getCfgs().get(cm);
                                                cfg.getTails().forEach(tail -> {
                                                    if (tail instanceof JReturnStmt) {
                                                        String ret = Main.wpCFG.getMethodOf(tail).toString() + "/" + ((JReturnStmt) tail).getOp().toString();
                                                        if (database.nameToPname.containsKey(ret)) {
                                                            if (out) System.out.println("\t" + ret);
                                                            retVar.add(database.nameToPname.get(ret));
                                                        }
                                                        if (database.nameToPname.containsKey(ret) && u.equals(tail)) {
                                                            predRetVar = database.nameToPname.get(ret);
                                                        }
                                                    }
                                                });
                                            }
                                            if (!predRetVar.equals("")) {
                                                if (out) System.out.println("\t\t" + u + "\t" + succ);
                                                if (out) System.out.println("\t\t\t" + predRetVar);
                                                HashSet<Allocation> allO = new HashSet<>(database.vptInsen.get(predRetVar));
                                                for (String var : retVar) {
                                                    if (!var.equals(predRetVar)) {
                                                        allO.removeAll(database.vptInsen.get(var));
                                                    }
                                                }
                                                if (out) System.out.println("\t\t\t\t" + allO.size());
                                                if (allO.size() > 1) {
                                                    dfsStack.push(new Pair<>(succ, new VFGvalue(((JAssignStmt) succ).getLeftOp())));
                                                }
                                            }
                                        }
                                    }
                                }
                                else if (succ instanceof AssignStmt && ((AssignStmt) succ).getRightOp() instanceof StaticInvokeExpr) {
                                    dfsStack.push(new Pair<>(succ, vvvv));
                                }
                                else if (succ instanceof InvokeStmt) {

                                }
                                else {
                                    dfsStack.push(new Pair<>(succ, vvvv));
                                }
                            }
                        }
                    }
                }
            }
            return res;
        }
        else {
            HashSet<Pair<Unit, VFGvalue>> color = new HashSet<>(); // VFGvalue是来自上一个unit的可能被改变的值
            if (root instanceof AssignStmt) {
                dfsStack.push(new Pair<>(root, new VFGvalue(((AssignStmt)root).getLeftOp())));
            }
            else if (root instanceof IdentityStmt) {
                dfsStack.push(new Pair<>(root, new VFGvalue(((IdentityStmt)root).getLeftOp())));
            }

            while (!dfsStack.empty()) {
                Pair<Unit, VFGvalue> uv = dfsStack.pop();
                u = uv.getO1();
                vv = uv.getO2();
                if (color.add(uv)) {
                    unitToVarToDiffVPT(u);
                    res += 1;//unitToVarToVPTSize(u);
                    // 对比值流图和指针集上的差距
                    for (Unit succ : Main.ivfg.getSuccs(u)) {
//                        System.out.println("\n" + succ);
                        for(VFGvalue vvv : Main.ivfg.getEdgeValue(u, succ)) {
//                            System.out.println("\t" + vvv);
                            if(vvv.equals(vv)) {
                                if (succ instanceof JAssignStmt) { // 无论前面来的是什么都会对left造成影响
                                    JAssignStmt assignStmt = (JAssignStmt) succ;
                                    Value left = assignStmt.getLeftOp();
                                    Value right = assignStmt.getRightOp();
                                    VFGvalue curV = new VFGvalue(left);

                                    if (left instanceof Local) {
                                        if(u instanceof ReturnStmt) {
                                            dfsStack.push(new Pair<>(succ, curV));
                                        }
                                        if (right instanceof InstanceInvokeExpr) {
                                            InstanceInvokeExpr iie = (InstanceInvokeExpr) right;
                                            if(iie.getBase().equals(vv.getValue())) {
                                                dfsStack.push(new Pair<>(succ, vv));
                                            }
                                            else {
                                                iie.getArgs().forEach(arg -> {
                                                    if(arg.equals(vv.getValue())) {
                                                        dfsStack.push(new Pair<>(succ, vv));
                                                    }
                                                });
                                            }
                                        }
                                        else if (right instanceof StaticInvokeExpr) {
                                            StaticInvokeExpr iie = (StaticInvokeExpr) right;
                                            iie.getArgs().forEach(arg -> {
                                                if(arg.equals(vv.getValue())) {
                                                    dfsStack.push(new Pair<>(succ, vv));
                                                }
                                            });
                                        }
                                        else if (right instanceof InstanceFieldRef) {
                                            dfsStack.push(new Pair<>(succ, curV));
                                            System.out.print("");
                                        }
                                        else if (right instanceof StaticFieldRef) {
                                            dfsStack.push(new Pair<>(succ, curV));
                                            System.out.print("");
                                        }
                                        else if (right instanceof CastExpr) {
                                            dfsStack.push(new Pair<>(succ, curV));
                                            System.out.print("");
                                        }
                                        else {
                                            dfsStack.push(new Pair<>(succ, curV));
                                            System.out.print("");
                                        }
                                    }
                                    else if (left instanceof ArrayRef) {
                                        dfsStack.push(new Pair<>(succ, new VFGvalue(((ArrayRef) left).getBase())));
                                    }
                                    else if (left instanceof InstanceFieldRef) {
                                        for (Unit succsucc : Main.ivfg.getSuccs(succ)) {
                                            for (VFGvalue vvvvvv : Main.ivfg.getEdgeValue(succ, succsucc)) {
                                                dfsStack.push(new Pair<>(succ, vvvvvv));
                                            }
                                        }
                                    }
                                    else if (left instanceof StaticFieldRef) {
                                        for (Unit succsucc : Main.ivfg.getSuccs(succ)) {
                                            for (VFGvalue vvvvvv : Main.ivfg.getEdgeValue(succ, succsucc)) {
                                                dfsStack.push(new Pair<>(succ, vvvvvv));
                                            }
                                        }
                                    }
                                }
                                else if (succ instanceof JIdentityStmt) {
                                    Value left = ((JIdentityStmt) succ).getLeftOp();
                                    Value right = ((JIdentityStmt) succ).getRightOp();
                                    if (left instanceof Local) {
                                        dfsStack.push(new Pair<>(succ, new VFGvalue(left)));
                                    }
                                }
                                else if (succ instanceof JInvokeStmt) {
                                    if (((JInvokeStmt) succ).getInvokeExpr() instanceof InstanceInvokeExpr) {
                                        InstanceInvokeExpr iie = (InstanceInvokeExpr) ((JInvokeStmt) succ).getInvokeExpr();
                                        if(iie.getBase().equals(vv.getValue())) {
                                            dfsStack.push(new Pair<>(succ, vv));
                                        }
                                        else {
                                            iie.getArgs().forEach(arg -> {
                                                if(arg.equals(vv.getValue())) {
                                                    dfsStack.push(new Pair<>(succ, vv));
                                                }
                                            });
                                        }
                                    }
                                    else if (((JInvokeStmt) succ).getInvokeExpr() instanceof StaticInvokeExpr) {
                                        StaticInvokeExpr iie = (StaticInvokeExpr) ((JInvokeStmt) succ).getInvokeExpr();
                                        iie.getArgs().forEach(arg -> {
                                            if(arg.equals(vv.getValue())) {
                                                dfsStack.push(new Pair<>(succ, vv));
                                            }
                                        });
                                    }
                                }
                                else if (succ instanceof JReturnStmt) {
                                    dfsStack.push(new Pair<>(succ, vv));
                                }
                                else {
                                    dfsStack.push(new Pair<>(succ, vv));
                                }
                            }
                        }
                    }
                }
            }
            return res;
        }
    }

    public static int invokeThisDiffVpt(InstanceInvokeExpr v, Unit succ) {
        if (true) return 0;
        int res = 0;
        Value vvvvvv = ((IdentityStmt) succ).getLeftOp();
        boolean out = false;
        index = 0;
        predRetVar = "";
        retVar = new HashSet<>();
        if (out) System.out.println("\n" + u + "\t" + succ + "\t" + vv);
        predRetVar = Main.wpCFG.getMethodOf(u).toString() + "/" + v.getBase().toString();
        if (database.nameToPname.containsKey(predRetVar)) {
            predRetVar = database.nameToPname.get(predRetVar);
        }

        if (database.vptInsen.containsKey(predRetVar)) {
            Main.ivfg.getPreds(succ).forEach(pred -> {
                InstanceInvokeExpr iie = null;
                if (pred instanceof AssignStmt && ((AssignStmt) pred).getRightOp() instanceof InstanceInvokeExpr) {
                    iie = (InstanceInvokeExpr) ((AssignStmt) pred).getRightOp();
                }
                else if (pred instanceof InvokeStmt) {
                    iie = (InstanceInvokeExpr) ((InvokeStmt) pred).getInvokeExpr();
                }
                if (iie != null) {
                    if (out) System.out.println("" + "\tpred: " + pred);

                    String base = Main.wpCFG.getMethodOf(pred).toString() + "/" + ((InstanceInvokeExpr)((Stmt) pred).getInvokeExpr()).getBase();
                    if (database.nameToPname.containsKey(base)) {
                        base = database.nameToPname.get(base);
                        if (out) System.out.println("" + "\t\tbase: " + base);
                        retVar.add(base);
                    }
                }
            });

            if (out) System.out.println("\t\t\t" + predRetVar);
            HashSet<Allocation> allO = new HashSet<>(database.vptInsen.get(predRetVar));
            for (String var : retVar) {
                if (!var.equals(predRetVar)) {
                    if (out) System.out.println("\t\t\t" + var);
                    allO.removeAll(database.vptInsen.get(var));
                }
            }
            if (out) System.out.println("\t\t\t\t" + allO.size());
            if (allO.size() > 1) {
                dfsStack.push(new Pair<>(succ, new VFGvalue(vvvvvv)));
            }
        }

        return res;
    }

    public static int invokeArgDiffVpt(InstanceInvokeExpr v, Unit succ) {
        int res = 0;
        Value vvvvvv = ((IdentityStmt) succ).getLeftOp();
        boolean out = false
                ;
        index = 0;
        predRetVar = "";
        retVar = new HashSet<>();
        if (out) System.out.println("\n" + u + "\t" + succ + "\t" + vv);
        for (Value l : v.getArgs()) {
            if (vv.getValue().equals(l)) {
                predRetVar = Main.wpCFG.getMethodOf(u).toString() + "/" + l.toString();
                if (database.nameToPname.containsKey(predRetVar)) {
                    predRetVar = database.nameToPname.get(predRetVar);
                }
                break;
            }
            index++;
        }
        if(!succ.toString().contains("@parameter" + index)) return 0;

        if (out) System.out.println("" + "\tindex = " + index);

        if (!predRetVar.equals("") && database.vptInsen.containsKey(predRetVar)) {
            Main.ivfg.getPreds(succ).forEach(pred -> {
                InstanceInvokeExpr iie = null;
                if (pred instanceof AssignStmt && ((AssignStmt) pred).getRightOp() instanceof InstanceInvokeExpr) {
                    iie = (InstanceInvokeExpr) ((AssignStmt) pred).getRightOp();
                }
                else if (pred instanceof InvokeStmt) {
                    iie = (InstanceInvokeExpr) ((InvokeStmt) pred).getInvokeExpr();
                }
                if (iie != null) {
                    if (out) System.out.println("" + "\tpred: " + pred);

                    int i = 0;
                    for (Value l : iie.getArgs()) {
                        String arg = Main.wpCFG.getMethodOf(pred).toString() + "/" + l.toString();
                        if (database.nameToPname.containsKey(arg)) {
                            arg = database.nameToPname.get(arg);
                            if (i == index && !predRetVar.equals(arg)) {
                                if (out) System.out.println("" + "\t\targ: " + arg);
                                retVar.add(arg);
                            }
                        }
                        i++;
                    }
                }
            });

            if (out) System.out.println("\t\t\t" + predRetVar);
            HashSet<Allocation> allO = new HashSet<>(database.vptInsen.get(predRetVar));
            for (String var : retVar) {
                if (!var.equals(predRetVar)) {
                    if (out) System.out.println("\t\t\t" + var);
                    allO.removeAll(database.vptInsen.get(var));
                }
            }
            if (out) System.out.println("\t\t\t\t" + allO.size());
            if (allO.size() > 1) {
                dfsStack.push(new Pair<>(succ, new VFGvalue(vvvvvv)));
            }
        }

        return res;
    }

    public static int invokeArgDiffVpt(StaticInvokeExpr v, Unit succ) {
        int res = 0;
        Value vvvvvv = ((IdentityStmt) succ).getLeftOp();
        boolean out = false;
        index = 0;
        predRetVar = "";
        retVar = new HashSet<>();
        if (out) System.out.println("\n" + u + "\t" + succ + "\t" + vv);
        for (Value l : v.getArgs()) {
            if (vv.getValue().equals(l)) {
                predRetVar = Main.wpCFG.getMethodOf(u).toString() + "/" + l.toString();
                if (database.nameToPname.containsKey(predRetVar)) {
                    predRetVar = database.nameToPname.get(predRetVar);
                }
                break;
            }
            index++;
        }
        if(!succ.toString().contains("@parameter" + index)) return 0;
        if (out) System.out.println("" + "\tindex = " + index);

        if (!predRetVar.equals("") && database.vptInsen.containsKey(predRetVar)) {
            Main.ivfg.getPreds(succ).forEach(pred -> {
                StaticInvokeExpr si = null;
                if (pred instanceof AssignStmt && ((AssignStmt) pred).getRightOp() instanceof StaticInvokeExpr) {
                    si = (StaticInvokeExpr) ((AssignStmt) pred).getRightOp();
                }
                else if (pred instanceof InvokeStmt) {
                    si = (StaticInvokeExpr) ((InvokeStmt) pred).getInvokeExpr();
                }
                if (si != null) {
                    if (out) System.out.println("" + "\tpred: " + pred);

                    int i = 0;
                    for (Value l : si.getArgs()) {
                        String arg = Main.wpCFG.getMethodOf(pred).toString() + "/" + l.toString();
                        if (database.nameToPname.containsKey(arg)) {
                            arg = database.nameToPname.get(arg);
                            if (i == index && !predRetVar.equals(arg)) {
                                if (out) System.out.println("" + "\t\targ: " + arg);
                                retVar.add(arg);
                            }
                        }
                        i++;
                    }
                }
            });

            if (out) System.out.println("\t\t\t" + predRetVar);
            HashSet<Allocation> allO = new HashSet<>(database.vptInsen.get(predRetVar));
            for (String var : retVar) {
                if (!var.equals(predRetVar)) {
                    if (out) System.out.println("\t\t\t" + var);
                    allO.removeAll(database.vptInsen.get(var));
                }
            }
            if (out) System.out.println("\t\t\t\t" + allO.size());
            if (allO.size() > 1) {
                dfsStack.push(new Pair<>(succ, new VFGvalue(vvvvvv)));
            }
        }

        return res;
    }

    public static int unitToVarToVPTSize(Unit u) {
        int res = 0;
        if (Main.wpCFG.getMethodOf(u) != null) {
            String var = Main.wpCFG.getMethodOf(u).toString() + "/";
            if (u instanceof JAssignStmt) { // 无论前面来的是什么都会对left造成影响
                JAssignStmt u0 = (JAssignStmt) u;
                Value left = u0.getLeftOp();
                Value right = u0.getRightOp();
                if (left instanceof Local) {

                }
                else if (left instanceof ArrayRef) {
                    left = ((ArrayRef) left).getBase();
                }
                else if (left instanceof InstanceFieldRef) {

                }
                else if (left instanceof StaticFieldRef) {

                }
                var = var + left.toString();
            }
            else if (u instanceof JIdentityStmt) {
                var = var + ((JIdentityStmt) u).getLeftOp().toString();
            }
            else if (u instanceof JInvokeStmt) {
                var = var + vv.toString();
            }
            else if (u instanceof JReturnStmt) {
                var = var + ((JReturnStmt) u).getOp().toString();
            }

            if (database.nameToPname.containsKey(var)) {
                res += database.vptInsen.get(database.nameToPname.get(var)).size();
            }
            else {
                res += 1;
            }
        }
        else {
            res++;
        }
        return res;
    }

    public static void unitToVarToDiffVPT(Unit u) {
        if (sort1Flag && Main.wpCFG.getMethodOf(u) != null) {
//                    util.writeFilelnWithPrefix(Main.wpCFG.getMethodOf(u) + "\t" + u, "ivfgDfs");
            String var = Main.wpCFG.getMethodOf(u).toString() + "/";
            if (u instanceof JAssignStmt) { // 无论前面来的是什么都会对left造成影响
                JAssignStmt u0 = (JAssignStmt) u;
                Value left = u0.getLeftOp();
                Value right = u0.getRightOp();
                if (left instanceof Local) {

                }
                else if (left instanceof ArrayRef) {
                    left = ((ArrayRef) left).getBase();
                }
                else if (left instanceof InstanceFieldRef) {

                }
                else if (left instanceof StaticFieldRef) {

                }
                var = var + left.toString();
            }
            else if (u instanceof JIdentityStmt) {
                var = var + ((JIdentityStmt) u).getLeftOp().toString();
            }
            else if (u instanceof JInvokeStmt) {
                var = var + vv.toString();
            }
            else if (u instanceof JReturnStmt) {
                var = var + ((JReturnStmt) u).getOp().toString();
            }
            if (database.nameToPname.containsKey(var)) {
                ccc = 0;
                cc = 0;
                String v = database.nameToPname.get(var);
                if (database.vpt2obj.containsKey(v) && database.vpt2obj.containsKey(v)) {
                    cc += database.vptInsen.get(v).size();
                    ccc += database.vptInsen.get(v).size() - database.vpt2obj.get(v).size();
                    if (ccc == 0) {
                        ccc += 1;
                    }
                }
                if (ccc > 1) {
                    util.writeFilelnWithPrefix("-----" + "\t" + cc + "\t" + ccc + "\t" + var + "\t" + u, "ivfgDfs" + rcVarToInteger.get(curRcVar));
                    util.writeFilelnWithPrefix("-----" + "\t" + cc + "\t" + ccc + "\t" + var + "\t" + u, "ivfgDfsOnly" + rcVarToInteger.get(curRcVar));
                }
                else {
                    util.writeFilelnWithPrefix("--=--" + "\t" + cc + "\t" + ccc + "\t" + var + "\t" + u, "ivfgDfs" + rcVarToInteger.get(curRcVar));
                }
            }
            else {
                util.writeFilelnWithPrefix("=====" + "\t" + Main.wpCFG.getMethodOf(u) + "\t" + u, "ivfgDfs" + rcVarToInteger.get(curRcVar));
            }
        }
    }

    public static void spiltFile() {

//        String inputFile = "/home/xrbin/Desktop/recommend/logs/recommendVar-rc";
        String inputFile = "/home/xrbin/Desktop/recommend/logs/appVarJieba";
        String outputDir = "/home/xrbin/Desktop/doophome/NVPT/";
        int interval = 1;
        DatabaseManager.getInstance().readData();

        try (
                FileReader reader = new FileReader(inputFile);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            int i = 0;
            int fileIndex = 0;
            rcVar = new Vector<>();
            while ((line = br.readLine()) != null) {
                rcVar.add(line);
                if (++i == interval) {
//                    interval++;
//                    if (fileIndex == 12 || fileIndex == 16 || fileIndex == 22 || fileIndex == 43
//                            || fileIndex == 23 || fileIndex == 25 || fileIndex == 33
//                            || fileIndex == 35 || fileIndex == 36 || fileIndex == 37
//                            || fileIndex == 38 || fileIndex == 39 || fileIndex == 40) {
//                        rcVar.forEach(v -> {
//                            util.writeFilelnWithPrefix(v, "rc");
//                        });
//                    }
//                    if (fileIndex == 0 || fileIndex == 11 || fileIndex == 33
//                            || fileIndex == 49 || fileIndex == 50 || fileIndex == 51
//                            || fileIndex == 53 || fileIndex == 66 || fileIndex == 70
//                            || fileIndex == 98 || fileIndex == 104 || fileIndex == 116
//                            || fileIndex == 137 || fileIndex == 140 || fileIndex == 141
//                            || fileIndex == 144 || fileIndex == 182 || fileIndex == 195
//                            || fileIndex == 204 || fileIndex == 251 || fileIndex == 252
//                            || fileIndex == 265 || fileIndex == 271 || fileIndex == 273
//                            || fileIndex == 283 || fileIndex == 299 || fileIndex == 322
//                            || fileIndex == 324 || fileIndex == 328 || fileIndex == 335
//                            || fileIndex == 366 || fileIndex == 378 || fileIndex == 379
//                            || fileIndex == 426 || fileIndex == 429 || fileIndex == 431
//                            || fileIndex == 439 || fileIndex == 467 || fileIndex == 470
//                            || fileIndex == 476 || fileIndex == 493 || fileIndex == 494
//                            || fileIndex == 496 || fileIndex == 526 || fileIndex == 42) {
//                        rcVar.forEach(v -> {
//                            util.writeFilelnWithPrefix(v, "rc");
//                        });
//                    }
                    rcVarToFeedbacks(outputDir + "NVPT" + fileIndex, "0");
                    rcVar = new Vector<>();
                    i = 0;
                    fileIndex++;
                }
            }
//            rcVarToFeedbacks(outputDir + "NVPT" + fileIndex, "0");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int countt = 0;
    public static int counttAll = 0;
    public static int counttAllPred = 0;

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
                                osw.write(a + "\t" + var + "\n");
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
        util.writeFileln((counttAll - counttAllPred) + "", "/home/xrbin/Desktop/doophome/NVPT/fbNum");
        counttAllPred = counttAll;
    }

    public static void appVarToFeedbacks() {

        try (
                FileReader reader = new FileReader(path + "AVPT.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
//                System.out.println(line);
                String[] sa = line.split("\t");
                database.appInsenVpt.computeIfAbsent(sa[3], k -> new HashSet<>()).add(new Allocation(sa[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader("/home/xrbin/Desktop/doophome/out/" + projectName2Obj + "/database/AVPT.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
//                System.out.println(line);
                String[] sa = line.split("\t");
                database.appVpt2Obj.computeIfAbsent(sa[3], k -> new HashSet<>()).add(new Allocation(sa[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

//        try {
//            File file = new File("/home/xrbin/Desktop/doophome/NVPT/NVPT0");
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

        try {
            Runtime.getRuntime().exec("rm logs/jieba/vptDiff");
            Runtime.getRuntime().exec("rm logs/jieba/vptDiffNumber");
        } catch (Exception e) {
            System.err.print("");
        }

        database.vptInsen.forEach((var, os) -> {
            if (database.vpt2obj.containsKey(var)) {
                countVptDiff = 0;
                os.forEach(o -> {
                    if (!database.vpt2obj.get(var).contains(o)) {
                        util.writeFilelnWithPrefix(var + "\t" + o, "/jieba/vptDiff");
                        countVptDiff++;
                    }
                });
                if (countVptDiff != 0) {
                    util.writeFilelnWithPrefix(var + "\t" + countVptDiff, "/jieba/vptDiffNumber");
                }
            }
        });

    }
}
