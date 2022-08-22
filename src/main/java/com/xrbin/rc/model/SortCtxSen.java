package com.xrbin.rc.model;

import com.xrbin.ddpt.Main;
import com.xrbin.ddpt.model.Allocation;
import com.xrbin.ddpt.model.DatabaseManager;
import com.xrbin.ddpt.model.VFGvalue;
import com.xrbin.ddpt.utils;
import com.xrbin.rc.RecommendCtxSen;
import com.xrbin.utils.util;
import jas.Pair;
import soot.Local;
import soot.Unit;
import soot.Value;
import soot.jimple.*;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JIdentityStmt;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JReturnStmt;
import soot.toolkits.graph.DirectedGraph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class SortCtxSen {
    public static DatabaseManager database = DatabaseManager.getInstance();
    public static Vector<String> rcVar = new Vector<>();

    public static boolean sort1Flag = false;
    public static String curVar = "";
    public static String curRcVar = "";
    public static Integer[] curVars = { 125, 99, 77, 63, 96, 57, 97, 98, 100, 101, 102, 114,};
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

        if (RecommendCtxSen.flag_main) {
            Main.run();
            RecommendCtxSen.flag_main = false;
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

    public static void invokeThisDiffVpt(InstanceInvokeExpr v, Unit succ) {
        if (true) return;
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
    }

    public static void invokeArgDiffVpt(InstanceInvokeExpr v, Unit succ) {
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
        if(!succ.toString().contains("@parameter" + index)) return ;

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

    }

    public static void invokeArgDiffVpt(StaticInvokeExpr v, Unit succ) {
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
        if(!succ.toString().contains("@parameter" + index)) return;
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
}
