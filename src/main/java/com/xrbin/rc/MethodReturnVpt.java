package com.xrbin.rc;

import com.xrbin.ddpt.IntroValueFlowGraph;
import com.xrbin.ddpt.Main;
import com.xrbin.ddpt.WholeProgramCFG;
import com.xrbin.ddpt.model.*;

import com.xrbin.ddpt.utils;
import com.xrbin.utils.StaticData;
import com.xrbin.utils.util;
import fj.Hash;
import soot.*;
import soot.jimple.*;
import soot.jimple.internal.*;
import soot.shimple.internal.SPhiExpr;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;
import java.util.Vector;

import static com.xrbin.rc.RecommendCtxSen.projectNameInsen;

public class MethodReturnVpt {
    public static boolean out = false;
    public SootMethod sm;
    public WholeProgramCFG wpCFG;
    public IntroValueFlowGraph ivfg;
    public HashSet<CSAllocation> thisVpt;
    public static DatabaseManager database = DatabaseManager.getInstance();

    public Vector<HashSet<CSAllocation>> argsVpt = new Vector<>();
    public HashMap<Value, HashSet<CSAllocation>> returnVarsVpt = new HashMap<>();

    public static void main(String[] args) {
//        utils.JARPTAH = "/home/xrbin/Java_Project/jieba-analysis-master/target/jieba-analysis-1.0.3-SNAPSHOT.jar";
        utils.JARPTAH = "/home/xrbin/Java_Project/jar/rapidoid-commons-5.5.5.jar";
//        utils.JARPTAH = "/home/xrbin/Java_Project/DDPT_test/build/libs/all.jar";

        utils.DATABASE = "/home/xrbin/doophome/out/" + projectNameInsen + "/database/";

        Main.run();
        Vector<HashSet<CSAllocation>> argsVpt = new Vector<>();
        Main.wpCFG.getUnits().forEach(unit -> {
            if(unit.toString().startsWith("virtualinvoke t0.<com.xrbin.ddptTest.recommendTest.RecommendTest: void func0")) {
                VirtualInvokeExpr vie = (VirtualInvokeExpr)((InvokeStmt) unit).getInvokeExpr();
                vie.getArgs().forEach(arg -> {
                    String var = Main.wpCFG.getMethodOf(unit).toString() + "/" + arg;
                    if(database.nameToPname.containsKey(var)) {
                        var = database.nameToPname.get(var);
                        argsVpt.add(getDatabaseVarVptSet(var));
                    }
                    else {
                        argsVpt.add(new HashSet<>());
                        util.plnB(var);
                    }
                });
                HashSet<CSAllocation> thisVpt = new HashSet<>();
                String thisVar = Main.wpCFG.getMethodOf(unit).toString() + "/" + vie.getBase();
                if(database.nameToPname.containsKey(thisVar)) {
                    thisVar = database.nameToPname.get(thisVar);
                    thisVpt.addAll(getDatabaseVarVptSet(thisVar));
                }
                else {
                    util.plnB(thisVar);
                }

                String methodName = Main.wpCFG.getMethodOf(unit).toString() + "/" + Main.wpCFG.getMethodOf(unit).getDeclaringClass() + "." + vie.getMethod().getName();
                    util.plnP("\tmethodName: " + methodName);
                methodName = database.mil.get(methodName + "/" + utils.getLineNumber(unit));
                    util.plnP("\tmethodName: " + methodName);

                if (database.backCg.get(methodName) != null) {
                    for (String cm : database.backCg.get(methodName)) {
                        if (Main.wpCFG.getBodys().containsKey(cm)) {
                            MethodReturnVpt m = new MethodReturnVpt(Main.wpCFG.getBodys().get(cm).getMethod(), Main.ivfg, Main.wpCFG, thisVpt, argsVpt);
                            m.analysis();
                        }
                    }
                }
            }
        });
    }

    public MethodReturnVpt(SootMethod sm, IntroValueFlowGraph ivfg, WholeProgramCFG wpCfg, HashSet<CSAllocation> thisVpt, Vector<HashSet<CSAllocation>> argsVpt) {
        this.sm = sm;
        this.ivfg = ivfg;
        this.wpCFG = wpCfg;
        this.thisVpt = thisVpt;
        this.argsVpt = argsVpt;
    }

    // 只有局部变量的指向，静态变量和域都直接查表
    public HashMap<Value, HashSet<CSAllocation>> vpt = new HashMap<>();
    public HashSet<CSAllocation> analysis() {
        HashSet<CSAllocation> res = new HashSet<>();
        Stack<Unit> worklist = new Stack<>();
        if (wpCFG.getBodys().get(sm.toString()).getParameterLocals().size() == argsVpt.size()) {
            for (int i = 0; i < wpCFG.getBodys().get(sm.toString()).getParameterLocals().size(); i++) {
                util.plnP((wpCFG.getBodys().get(sm.toString()).getParameterLocals().get(i)).toString());
                getVarVptSet(wpCFG.getBodys().get(sm.toString()).getParameterLocals().get(i)).addAll(argsVpt.get(i));
            }
        }
        else {
            System.err.println("wrong args size.");
        }
        getVarVptSet(wpCFG.getBodys().get(sm.toString()).getThisLocal()).addAll(thisVpt);

//        while (flag) {
//            worklist.clear();
        wpCFG.getBodys().get(sm.toString()).getUnits().forEach(worklist::push);
        while (!worklist.empty()) {
            Unit curUnit = worklist.pop();
            if(out) System.out.println("--------worklist analysis--------" + Main.wpCFG.getMethodOf(curUnit) + "\t" + curUnit);

            if (curUnit instanceof JAssignStmt) {
                JAssignStmt assignStmt = (JAssignStmt) curUnit;
                Value left = assignStmt.getLeftOp();
                Value right = assignStmt.getRightOp();

                if (left != null && left.equals(right)) {
                    if(out) System.out.println("Just for fun.");
                }
                else if (left instanceof JimpleLocal && right instanceof JimpleLocal) { // Assign
                    if (getVarVptSet(left).addAll(getVarVptSet(right))) {
                        worklist.addAll(ivfg.getSuccs(curUnit));
                    }
                }
                else if (left instanceof JimpleLocal && right instanceof StaticFieldRef) { // Assign
                    if (getVarVptSet(left).addAll(getStaticFiledVptSet(right.toString()))) {
                        worklist.addAll(ivfg.getSuccs(curUnit));
                    }
                }
                else if (left instanceof JimpleLocal && right instanceof JCastExpr) { // Assign
//                    if(out) System.out.println();
                    boolean tempFlag = false;
                    for(CSAllocation csa : getVarVptSet(((JCastExpr) right).getOp())) {
                        if(csa.getO().getO().contains("new ")){
                            String s = csa.getO().getO().split("new ")[1];
                            s = s.split("/")[0];
                            SootClass csaType = Main.ts.strToClass.get(s);
                            if(Main.ts.getSuperInterface(csaType).contains(Main.ts.typeToClass.get(right.getType()))) {
                                getVarVptSet(left).add(csa);
                                tempFlag = true;
                            }
                            if(Main.ts.getSuperClass(csaType).contains(Main.ts.typeToClass.get(right.getType()))) {
                                getVarVptSet(left).add(csa);
                                tempFlag = true;
                            }
                        }
                    }
                    if (tempFlag) {
                        worklist.addAll(ivfg.getSuccs(curUnit));
                    }
                }
                else if (left instanceof JimpleLocal && right instanceof InstanceFieldRef) { // Load: p = q.f
                    boolean tempFlag = false;
                    for (CSAllocation csAllocation : getVarVptSet(((InstanceFieldRef) right).getBase())) {
                        Field f = new Field(csAllocation, ((InstanceFieldRef) right).getField().toString());
                        if (getVarVptSet(left).addAll(getInstansFiledVptSet(f))) {
                            tempFlag = true;
                            if(out) System.out.println("---------------------- here ----------------------");
                        }
                    }
                    if(tempFlag) {
                        worklist.addAll(ivfg.getSuccs(curUnit));
                    }
                }
                else if (left instanceof JimpleLocal && right instanceof JNewExpr) {
                    if (getVarVptSet(left).add(makeAlloc(curUnit))) {
                        worklist.addAll(ivfg.getSuccs(curUnit));
                    }
                }
                else if (left instanceof JimpleLocal && right instanceof SPhiExpr) { // Phi function
                    boolean tempFlag = false;
                    for (Value value : ((SPhiExpr) right).getValues()) {
                        if (getVarVptSet(left).addAll(getVarVptSet(value))) tempFlag = true;
                    }
                    if(tempFlag) {
                        worklist.addAll(ivfg.getSuccs(curUnit));
                    }
                }
                else if (left instanceof JimpleLocal && right instanceof ArrayRef) { // Assign
                    if (getVarVptSet(left).addAll(getVarVptSet(((ArrayRef) right).getBase()))) {
                        worklist.addAll(ivfg.getSuccs(curUnit));
                    }
                }
                else if (left instanceof JimpleLocal && right instanceof NewArrayExpr) { // Assign
                    if (left.equals(right)) {
                        if(out) System.out.println("Just for fun.");
                    }
                }
                else if (left instanceof JimpleLocal && right instanceof JNewMultiArrayExpr) { // Assign
                    if (left.equals(right)) {
                        if(out) System.out.println("Just for fun.");
                    }
                }
                else if (left instanceof JimpleLocal && right instanceof InvokeExpr) { // a = b.f(arg);
                    String var = wpCFG.getMethodOf(curUnit).toString() + "/" + left.toString();
                    if (getVarVptSet(left).addAll(getDatabaseVarVptSet(var))) {
                        ivfg.getSuccs(curUnit).forEach(u -> {
                            if(out) System.out.println("InvokeExpr\t" + u);
                            if(wpCFG.getMethodOf(u).equals(wpCFG.getMethodOf(curUnit))) {
                                if(out) System.out.println("\tInvokeExpr\t" + u);
                                worklist.add(u);
                            }
                        });
                    }
                }
                else if (left instanceof JimpleLocal) {
                    // nothis
//                            util.writeFileln("else if (left instanceof JimpleLocal)\t" + curUnit.getClass() + "\t" + curUnit.toString(), "moreJimpleStmt");
                }
                else if (left instanceof InstanceFieldRef && right instanceof JimpleLocal) {

                }
                else if (left instanceof InstanceFieldRef && right instanceof Constant) { // Store a.f = "asd";

                    if (right instanceof StringConstant) {

                    }
                    else if (right instanceof ClassConstant) {

                    }
                    else if (right instanceof NullConstant) {

                    }
                    else if (right instanceof NumericConstant) {

                    }
                    else {

                    }

                }
                else if (left instanceof StaticFieldRef && right instanceof JimpleLocal) { // static Assign A.f = b;

                }
            }
            else if (curUnit instanceof JIdentityStmt) {
//                    JIdentityStmt identityStmt = (JIdentityStmt) curUnit;
//                    Value left = identityStmt.getLeftOp();
//                    Value right = identityStmt.getRightOp();
//
//                    getVarVptSet(left).addAll(getVarVptSet(right));
            }
            else if (curUnit instanceof JInvokeStmt) {

            }
            else if (curUnit instanceof JReturnVoidStmt) {
                if (curUnit.toString().equals("Happy")) {
                    if(out) System.out.println("Happy!");
                }
            }
            else if (curUnit instanceof JReturnStmt) {
                if(out) System.out.println("return var vpt");
                returnVarsVpt.computeIfAbsent(((JReturnStmt) curUnit).getOp(), k -> new HashSet<>()).
                        addAll(getVarVptSet(((JReturnStmt) curUnit).getOp()));
            }
            else {
//                System.err.println("while(!worklist.isEmpty()): " + curUnit);
            }
        }
//        }
        outVPT();
        returnVarsVpt.forEach((v, k) -> res.addAll(k));
        return res;
    }

    private void outVPT() {
        if(out) System.out.println("\n||---------------------------------------------result-----------------------------------------||");
        vpt.keySet().forEach(v -> {
            if(out) System.out.println("\n" + sm.toString() + "/" + v);
            vpt.get(v).forEach(csAllocation -> {
                if(out) System.out.println("\t" + csAllocation.toString());
            });
        });
        if(out) System.out.println("\n||---------------------------------------------result-----------------------------------------||\n");
    }


    public HashSet<CSAllocation> getVarVptSet(Value v) {
        vpt.computeIfAbsent(v, k -> new HashSet<>());
        return vpt.get(v);
    }

    public static HashSet<CSAllocation> getDatabaseVarVptSet(String v) {
        HashSet<CSAllocation> res = new HashSet<>();
        if(database.nameToPname.containsKey(v) && database.vptInsen.containsKey(database.nameToPname.get(v))) {
            res.addAll(database.vptInsen.get(database.nameToPname.get(v)));
        }
        return res;
    }

    public static HashSet<CSAllocation> getInstansFiledVptSet(Field f) {
        HashSet<CSAllocation> res = new HashSet<>();
        if(database.csInstanceFieldVPT.containsKey(f)) {
            return database.csInstanceFieldVPT.get(f);
        }
        else {
            if(out) System.out.println("!database.csInstanceFieldVPT.containsKey(f): " + f);
        }
        return res;
    }

    public static HashSet<CSAllocation> getStaticFiledVptSet(String s) {
        HashSet<CSAllocation> res = new HashSet<>();
        if(database.staticVpt.containsKey(s)) {
            return database.staticVpt.get(s);
        }
        return res;
    }

    public static CSAllocation makeAlloc(Unit u) {
        ObjContext ctx = ObjContext.allContext;
        JAssignStmt u0 = (JAssignStmt) u;
        Value right = u0.getRightOp();
        if(!(right instanceof NewExpr)) {
            // System.exit(-1);
            return null;
        }
        if(right.toString().contains("new java.lang.StringBuilder")) {
            return CSAllocation.string_builder;
        }
        if(right.toString().contains("new java.lang.StringBuffer")) {
            return CSAllocation.string_buffer;
        }

        if(!database.assignHeapAllocation.containsKey(Main.wpCFG.getMethodOf(u).toString() + "/" + right.toString() + "---" + utils.getLineNumber(u))) {
//            util.plnR("makeAlloc" + wpCFG.getMethodOf(u).toString() + "/" + right.toString() + "---" + utils.getLineNumber(u));
//             System.exit(-1);
            return CSAllocation.not_in_doop;
        }

        String obj = database.assignHeapAllocation.get(
                Main.wpCFG.getMethodOf(u).toString() + "/" + right.toString() + "---" + utils.getLineNumber(u));

        if(obj == null) {
            return CSAllocation.not_in_doop;
        }

        return new CSAllocation(ctx, new Allocation(obj));
//        unitToAllocation.put(u, new Allocation(obj));
//        getNewContext(u).forEach(csCtx -> { res.add(new Allocation(csCtx, obj)); }); // bugbug 这毫无精度可言
//        if(out) System.out.println("--makeAlloc(Unit u, ObjContext ctx)--: " + res);
    }
    
}
