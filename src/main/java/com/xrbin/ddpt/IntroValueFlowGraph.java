package com.xrbin.ddpt;

import com.xrbin.ddpt.model.*;
import com.xrbin.utils.StaticData;
import com.xrbin.utils.util;
import jas.Pair;
import soot.*;
import soot.jimple.*;
import soot.jimple.internal.*;
import soot.shimple.internal.SPhiExpr;
import java.util.*;

// --biuld vfg--[<<immutable-context>>, <SUPAtest.test1: void main(java.lang.String[])>/new SUPAtest.test1/0]	<SUPAtest.test1: void func11()> -- class soot.jimple.internal.AssignStmt	bbb1_1.<SUPAtest.B: SUPAtest.A a> = $stack20
public class IntroValueFlowGraph {
    public static Value nullValue = new JimpleLocal("null", new Type() {
        @Override
        public String toString() {
            return "null";
        }
    });
    private final WholeProgramCFG wpCFG;
    private final DatabaseManager database = DatabaseManager.getInstance();

    private final Map<Unit, List<Unit>> unitToSuccs = new HashMap<>();
    private final Map<Unit, List<Unit>> unitToPreds = new HashMap<>();
    private final Map<Unit, HashMap<Unit, HashSet<VFGvalue>>> vfgValue = new HashMap<>();

    private final HashMap<SootMethod, HashSet<Pair<Unit, Field>>> methodFieldDefine = new HashMap<>();
    private final HashMap<SootMethod, HashSet<Pair<Unit, Field>>> methodFieldUse = new HashMap<>();
    private final HashMap<SootMethod, HashSet<Pair<Unit, String>>> methodStaticFieldUse = new HashMap<>();

    private final HashMap<Field, HashSet<SootMethod>> fieldDefUseMethod = new HashMap<>();
    private final HashMap<String, HashSet<SootMethod>> staticFieldUseMethod = new HashMap<>();

    private final HashMap<Field, HashSet<Unit>> fieldDefine = new HashMap<>();
    private final HashMap<Field, HashSet<Unit>> fieldUse = new HashMap<>();
//    private final HashMap<StaticFieldRef, HashSet<Unit>> staticFieldUse = new HashMap<>(); // wrong, wordless.
    private final HashMap<String, HashSet<Unit>> staticFieldUse = new HashMap<>();

    private final HashMap<LocatePointer, Boolean> hidenMethodField = new HashMap<>(); //
    public final boolean isHidenMethodField(LocatePointer l) {
        LocatePointer lp;
        if (l.getV().isField()) {
            CSAllocation csa = new CSAllocation(new Allocation(((Field)l.getV().getValue()).getO().getO().toString()));
            String f = ((Field)l.getV().getValue()).getField();
            lp = new LocatePointer(l.getU(), new VFGvalue(new Field(csa, f)), l.getMethod());
        }
        else {
            lp = new LocatePointer(l.getU(), l.getV(), l.getMethod());
        }
        return hidenMethodField.containsKey(lp);
    }

    public IntroValueFlowGraph(WholeProgramCFG c) {
        wpCFG = c;
        for(String key : wpCFG.getBodys().keySet()) {
            SootMethod curM = wpCFG.getBodys().get(key).getMethod();
            methodFieldUse.put(curM, new HashSet<>());
            methodFieldDefine.put(curM, new HashSet<>());
            methodStaticFieldUse.put(curM, new HashSet<>());

            for( Unit u : wpCFG.getBodys().get(key).getUnits()) {
                util.myprintln(u.toString(), utils.USEDEF);
                u.getDefBoxes().forEach(vb -> {
                    Value v = vb.getValue();
                    if (v instanceof InstanceFieldRef) {
                        JInstanceFieldRef ifr = (JInstanceFieldRef) v;
                        String var = wpCFG.getMethodOf(u) + "/" + ifr.getBase().toString();
                        if (database.vptInsen.containsKey(var)) {
                            for (CSAllocation o : database.vptInsen.get(var)) {
                                if (!utils.isConstantAllocation(o.getO().getO())) {
                                    fieldDefine.computeIfAbsent(new Field(o, ifr.getField().toString()), k -> new HashSet<>()).add(u);
                                    fieldDefUseMethod.computeIfAbsent(new Field(o, ifr.getField().toString()), k -> new HashSet<>()).add(curM);
                                    methodFieldDefine.get(curM).add(new Pair<>(u, new Field(o, ifr.getField().toString())));
                                }
                            }
                        }
                    }
                });

                u.getUseBoxes().forEach(vb -> {
                    Value v = vb.getValue();
                    if (v instanceof InstanceFieldRef) {
                        JInstanceFieldRef ifr = (JInstanceFieldRef) v;
                        String var = wpCFG.getMethodOf(u) + "/" + ifr.getBase().toString();
                        if (database.vptInsen.containsKey(var)) {
                            for (CSAllocation o : database.vptInsen.get(var)) {
                                if (!utils.isConstantAllocation(o.getO().getO())) {
                                    fieldUse.computeIfAbsent(new Field(o, ifr.getField().toString()), k -> new HashSet<>()).add(u);
                                    fieldDefUseMethod.computeIfAbsent(new Field(o, ifr.getField().toString()), k -> new HashSet<>()).add(curM);
                                    methodFieldUse.get(curM).add(new Pair<>(u, new Field(o, ifr.getField().toString())));
                                }
                            }
                        }
                    }
                    else if (v instanceof StaticFieldRef) {
                        staticFieldUse.computeIfAbsent(v.toString(), k -> new HashSet<>()).add(u);
                        staticFieldUseMethod.computeIfAbsent(v.toString(), k -> new HashSet<>()).add(curM);
                        methodStaticFieldUse.get(curM).add(new Pair<>(u, v.toString()));
                    }
                });
            }
        }
    }

    private int irCount = 0;
    private final HashSet<Unit> flagMainProcess = new HashSet<>();
    public void buildVFG() {

        util.getTime("ivfg - buildVFGClinit");
        buildVFGClinit();

        util.getTime("ivfg - buildVFG");
        Stack<Unit> worklist = new Stack<>();
        wpCFG.getHeads().forEach(worklist::push);

        HashSet<SootMethod> flagM = new HashSet<>();
        while (!worklist.empty()) {// better than <for>: for (Unit u : wpCFG) {
            Unit u = worklist.pop();
            flagM.add(wpCFG.getMethodOf(u));
            if (!flagMainProcess.add(u)) continue;
            worklist.addAll(wpCFG.getSuccsOf(u));
            irCount++;
            buildVFGForOneUnit(u);
        }

//        System.out.println("--buildVFG-- " + flagM.size());
        System.out.println("--------------------------------------------------------- irCount = " + irCount);
        System.out.println("--------------------------------------------------------- vfgEdegs = " + vfgEdegs);

//        hidenMethodField.forEach((k, v) -> {
//            util.writeFilelnWithPrefix(k.toString(), "hidenMethodField");
//        });

    }

    private void buildVFGForOneUnit(Unit u) {

//        if(wpCFG.getMethodOf(u).toString().equals("<org.apache.fop.render.print.PagesMode: void <init>(java.lang.String)>")
////                && u instanceof AssignStmt
////                && ((AssignStmt) u).getLeftOp().toString().equals("this")
//        ){
//            utils.BUILDVFG = true;
//        } else {
//            utils.BUILDVFG = false;
//        }
//        util.pln("--biuld vfg--" + wpCFG.getMethodOf(u) + " -- " + u.getClass() + "\t" + StaticData.G + u + StaticData.E);
//        if(DatabaseManager.getInstance().appMethod.contains(wpCFG.getMethodOf(u).toString())) {
//            util.myprintln("--biuld vfg--" + wpCFG.getMethodOf(u) + " -- " + u.getClass() + "\t" + StaticData.G + u + StaticData.E, StaticData.E, utils.BUILDVFG);
//        }
//        util.writeFilelnWithPrefix("--biuld vfg--" + wpCFG.getMethodOf(u) + " -- " + u.getClass() + "\t" + u, "ivfg");
        if (u instanceof AssignStmt) {
            AssignStmt u0 = (AssignStmt) u;
            Value left = u0.getLeftOp();
            Value right = u0.getRightOp();


            if (left instanceof JimpleLocal && !utils.isPrimitiveType(left.getType())) {
                dfs(u, u, (JimpleLocal) left);
            }
            else if (left instanceof InstanceFieldRef && !utils.isPrimitiveType(((InstanceFieldRef) left).getField().getType())) {
//                    System.out.println("\t--------------" + ((JInstanceFieldRef) left).getBase().toString() + "." + ((JInstanceFieldRef) left).getField());
                JInstanceFieldRef ifr = (JInstanceFieldRef) left;
//                    System.out.println("--------------" + wpCFG.getMethodOf(u).toString() + "/" + ifr.getBase().toString());
//                    Variable var = new Variable(u.getCtx(), wpCFG.getMethodOf(u).getMethod() + "/" + ifr.getBase().toString());
                String var = wpCFG.getMethodOf(u) + "/" + ifr.getBase().toString();
//                    util.plnG("--buildVFG - store: " + var);
                if (database.vptInsen.containsKey(var)) {
                    for (CSAllocation o : database.vptInsen.get(var)) {
                        if (!utils.isConstantAllocation(o.getO().getO())) {
                            if (!utils.BUILDVFGACCURATE) {
                                // 2022-01-14 20:41:08
                                Field f = new Field(o, ifr.getField().toString());
                                //                                System.out.println("\t\t--------------" + f);
                                if (fieldUse.containsKey(f)) {
                                    for (Unit to : fieldUse.get(f)) {
                                        addEdge(u, to, new VFGvalue(f));
                                    }
                                }
                                if (fieldDefine.containsKey(f)) {
                                    for (Unit to : fieldDefine.get(f)) {
                                        addEdge(u, to, new VFGvalue(f));
                                    }
                                }
                                // 2022-01-14 20:41:08
                            }
                            else {
                                if(!utils.isPrimitiveType(ifr.getType())) {
                                    dfsStoreWithCg(u, new Field(o, ifr.getField().toString()));
                                }
                            }
                        }
                    }
                }
            }
            else if (left instanceof ArrayRef) {
                ArrayRef ar = (ArrayRef) left;
                if(!utils.isPrimitiveType(ar.getBase().getType())) {
                    dfs(u, u, (JimpleLocal) ar.getBase());
                }
            }
            else if (left instanceof StaticFieldRef && !utils.isPrimitiveType(left.getType())) {
                if (true) {
                    if (staticFieldUse.containsKey(left.toString())) {
                        staticFieldUse.get(left.toString()).forEach(to -> addEdge(u, to, new VFGvalue(left)));
                    }
                }
                else {
                    StaticFieldRef sfr = (StaticFieldRef) left;
                    dfs(u, u, sfr);
                }
            }
            else {
//                    System.err.println("buildVFG() - if (u instanceof AssignStmt): " + u);
            }

            // args: arg1, arg2, ...
            // this: base
            // return unit -> p
            if (right instanceof InvokeExpr) {
                if (utils.isHardMethod(((InvokeExpr) right).getMethod())) {
//                    util.plnP(((InvokeExpr) right).getMethod().toString());
                    buildVFGForHidenMethodField(u);
                    return;
                }

                InvokeExpr ie = (InvokeExpr) right;
                for (Unit succ : wpCFG.getSuccsOf(u)) {
                    if (succ instanceof JIdentityStmt && !wpCFG.getMethodOf(succ).equals(wpCFG.getMethodOf(u))) { // is it?
                        Unit para = succ;

                        while (para instanceof JIdentityStmt && !(((JIdentityStmt) para).getRightOp() instanceof ParameterRef)) {
//                        while (!(para instanceof JIdentityStmt && ((JIdentityStmt) para).getRightOp() instanceof ParameterRef)) { // bug
                            para = wpCFG.getSuccsOf(para).get(0);
                        }

                        for (Value v : ie.getArgs()) {
                            if (!(para instanceof JIdentityStmt && ((JIdentityStmt) para).getRightOp() instanceof ParameterRef)) {
                                break;
                            }
                            if (v instanceof Local && !utils.isPrimitiveType(v.getType())) {
                                addEdge(u, para, new VFGvalue((JimpleLocal) v));
                            }
                            if (v instanceof StringConstant) {
                                addEdge(u, para, new VFGvalue((Constant) v));
                            }
                            if (wpCFG.getSuccsOf(para) == null) break;
                            para = wpCFG.getSuccsOf(para).get(0);
                        }
                    }
                    // return unit -> p
                    if (wpCFG.getMethodOf(succ) != null && !wpCFG.getMethodOf(succ).equals(wpCFG.getMethodOf(u))) {
                        for (Unit end : wpCFG.getTailsOfMethod(wpCFG.getMethodOf(succ))) {
                            if (end instanceof JReturnStmt) {
                                JReturnStmt jrs = (JReturnStmt) end;
                                if (jrs.getOp() != null && jrs.getOp() instanceof Local) {
                                    addEdge(end, u, new VFGvalue((JimpleLocal) jrs.getOp()));
                                }
                            }
                            else if (end instanceof JReturnVoidStmt) {
                                addEdge(end, u, new VFGvalue((JimpleLocal) nullValue)); // 2021-10-15 16:55:18 a big bug
                            }
                        }
                    }
                }

                // Thisref
                if (left instanceof JimpleLocal) {
                    if (right instanceof JVirtualInvokeExpr) {
                        JVirtualInvokeExpr jvi = (JVirtualInvokeExpr) ((AssignStmt) u).getRightOp();
                        for (Unit succ : wpCFG.getSuccsOf(u)) {
                            if (!wpCFG.getMethodOf(succ).equals(wpCFG.getMethodOf(u))) {
                                // p = q.f(arg1, arg2, ... )
                                // base: q
                                if (succ instanceof JIdentityStmt && ((JIdentityStmt) succ).getRightOp() instanceof ThisRef) {
                                    if (jvi.getBase() instanceof JimpleLocal) {
                                        addEdge(u, succ, new VFGvalue((JimpleLocal) jvi.getBase())); // VFGvalue diff with rightOp, but no problem
                                    }
                                }
                            }
                        }
                    }
                    else if (right instanceof JSpecialInvokeExpr) {
                        JSpecialInvokeExpr jvi = (JSpecialInvokeExpr) ((AssignStmt) u).getRightOp();
                        for (Unit succ : wpCFG.getSuccsOf(u)) {
                            if (!wpCFG.getMethodOf(succ).equals(wpCFG.getMethodOf(u))) {
                                // p = q.f(arg1, arg2, ... )
                                // base: q
                                if (succ instanceof JIdentityStmt && ((JIdentityStmt) succ).getRightOp() instanceof ThisRef) {
                                    if (jvi.getBase() instanceof JimpleLocal) {
                                        addEdge(u, succ, new VFGvalue((JimpleLocal) jvi.getBase())); // VFGvalue diff with rightOp, but no problem
                                    }
                                }
                            }
                        }
                    }
                    else if (right instanceof JStaticInvokeExpr) {
                        // no ThisRef
                    }
                    else if (right instanceof JDynamicInvokeExpr) {

                    }
                    else if (right instanceof JInterfaceInvokeExpr) {

                    }
                    else {

                    }
                }
            }

        }
        else if (u instanceof JIdentityStmt) {// method arguments
            JIdentityStmt u0 = (JIdentityStmt) u;
            Value left = u0.getLeftOp();
            Value right = u0.getRightOp();

            if (left instanceof Local && (right instanceof ThisRef || right instanceof ParameterRef) && !utils.isPrimitiveType(left.getType())) {
                dfs(u, u, (JimpleLocal) left);
            }
            else if (left instanceof Local && right instanceof CaughtExceptionRef) {
                // buildVFG() - if (u instanceof JIdentityStmt): $stack8 := @caughtexception
//                    
//                    dfs(u, u, (JimpleLocal) left);
//                    
            }
            else {
//                System.err.println("buildVFG() - if (u instanceof JIdentityStmt): " + u);
//                        util.plnY(u.getClass() + "\t" + u.toString());
//                        util.plnY("\t" + ((JIdentityStmt) u).getRightOp().getClass() + "\t" + ((JIdentityStmt) u).getRightOp().toString());
//                        util.plnY("\t" + ((JIdentityStmt) u).getLeftOp().getClass() + "\t" + ((JIdentityStmt) u).getLeftOp().toString());
            }
        }
        else if (u instanceof JInvokeStmt) {
            if(u.toString().equals("main")) {
                return;
            }
//                if ((((JInvokeStmt) u).getInvokeExpr()).getMethod().toString().contains("keySet")) {
//                    util.pBG((((JInvokeStmt) u).getInvokeExpr()).getMethod().toString());
//                    continue;
//                }
            if (utils.isHardMethod((((JInvokeStmt) u).getInvokeExpr()).getMethod())) {
//                util.plnP((((JInvokeStmt) u).getInvokeExpr()).getMethod().toString());
                buildVFGForHidenMethodField(u);
                return;
            }
            JInvokeStmt u0 = (JInvokeStmt) u;
            InvokeExpr ie = u0.getInvokeExpr();

            for (Unit succ : wpCFG.getSuccsOf(u)) {
                if (succ instanceof JIdentityStmt && wpCFG.getMethodOf(succ) != wpCFG.getMethodOf(u)) { // is it?
                    Unit para = succ;

                    while (para instanceof JIdentityStmt && !(((JIdentityStmt) para).getRightOp() instanceof ParameterRef)) {
//                        while (!(para instanceof JIdentityStmt && ((JIdentityStmt) para).getRightOp() instanceof ParameterRef)) { // bug
                        para = wpCFG.getSuccsOf(para).get(0);
                    }

                    for (Value v : ie.getArgs()) {
                        if (!(para instanceof JIdentityStmt && ((JIdentityStmt) para).getRightOp() instanceof ParameterRef)) {
                            break;
                        }
                        if (v instanceof Local && !utils.isPrimitiveType(v.getType())) {
                            addEdge(u, para, new VFGvalue((JimpleLocal) v));
                        }
                        if (wpCFG.getSuccsOf(para) == null) break;
                        para = wpCFG.getSuccsOf(para).get(0);
                    }

                }

                // return unit
                if (wpCFG.getMethodOf(succ) != null && wpCFG.getMethodOf(succ) != wpCFG.getMethodOf(u)) {
                    for (Unit end : wpCFG.getTailsOfMethod(wpCFG.getMethodOf(succ))) {
                        if (end instanceof JReturnStmt) {
                            JReturnStmt jrs = (JReturnStmt) end;
                            if (jrs.getOp() != null && jrs.getOp() instanceof Local && !utils.isPrimitiveType(jrs.getOp().getType())) {
                                addEdge(end, u, new VFGvalue((JimpleLocal) jrs.getOp()));
                            }
                        }
                        else if (end instanceof JReturnVoidStmt) {
                            addEdge(end, u, new VFGvalue((JimpleLocal) nullValue)); // 2021-10-15 16:55:18 a big bug
                        }
                    }
                }
            }

            if (ie instanceof JVirtualInvokeExpr) {
                JVirtualInvokeExpr jvi = (JVirtualInvokeExpr) ie;
                for (Unit succ : wpCFG.getSuccsOf(u)) {
                    if (wpCFG.getMethodOf(succ) != wpCFG.getMethodOf(u)) {
                        // p = q.f(arg1, arg2, ... )
                        // base: q
                        if (succ instanceof JIdentityStmt && ((JIdentityStmt) succ).getRightOp() instanceof ThisRef) {
                            if (jvi.getBase() instanceof JimpleLocal) {
                                addEdge(u, succ, new VFGvalue((JimpleLocal) jvi.getBase())); // VFGvalue diff with rightOp, but no problem
                            }
                        }
                    }
                }
            }
            else if (ie instanceof JSpecialInvokeExpr) {
                JSpecialInvokeExpr jvi = (JSpecialInvokeExpr) ie;
                for (Unit succ : wpCFG.getSuccsOf(u)) {
                    if (wpCFG.getMethodOf(succ) != wpCFG.getMethodOf(u)) {
                        // p = q.f(arg1, arg2, ... )
                        // base: q
                        if (succ instanceof JIdentityStmt && ((JIdentityStmt) succ).getRightOp() instanceof ThisRef) {
                            if (jvi.getBase() instanceof JimpleLocal) {
                                addEdge(u, succ, new VFGvalue((JimpleLocal) jvi.getBase())); // VFGvalue diff with rightOp, but no problem
                            }
                        }
                    }
                }
            }
            else if (ie instanceof JStaticInvokeExpr) {
                // no ThisRef
            }
            else if (ie instanceof JDynamicInvokeExpr) {
                // no ThisRef
            }
            else if (ie instanceof JInterfaceInvokeExpr) {
                JInterfaceInvokeExpr jvi = (JInterfaceInvokeExpr) ie;
                for (Unit succ : wpCFG.getSuccsOf(u)) {
                    if (wpCFG.getMethodOf(succ) != wpCFG.getMethodOf(u)) {
                        // p = q.f(arg1, arg2, ... )
                        // base: q
                        if (succ instanceof JIdentityStmt && ((JIdentityStmt) succ).getRightOp() instanceof ThisRef) {
                            if (jvi.getBase() instanceof JimpleLocal) {
                                addEdge(u, succ, new VFGvalue((JimpleLocal) jvi.getBase())); // VFGvalue diff with rightOp, but no problem
                            }
                        }
                    }
                }
            }
            else {

            }
        }
        else if (u instanceof JThrowStmt) {
//              TODO
        }
        else if (u instanceof JReturnStmt) {
//              handle above
        }
        else if (u instanceof JReturnVoidStmt) {
//              nothing to do
        }
        else if (u instanceof JTableSwitchStmt) {
//              nothing to do
        }
        else if (u instanceof JLookupSwitchStmt) {
//              nothing to do
        }
        else if (u instanceof JGotoStmt || u instanceof JBreakpointStmt || u instanceof JNopStmt || u instanceof JRetStmt ||
                u instanceof JEnterMonitorStmt || u instanceof JExitMonitorStmt || u instanceof JIfStmt
        ) {

        }
        else {

        }
    }

    private final HashSet<SootMethod> hidenAppMethod = new HashSet<>();
    private final HashSet<Unit> flagSonProcessAll = new HashSet<>();
    private void buildVFGForHidenMethodField(Unit root) {
//        util.plnP("--biuld vfg son start--" + wpCFG.getMethodOf(root) + " -- " + root);
        Stack<Unit> worklist = new Stack<>();

        // big bug bug 2022-04-13 09:46:57
        // root 肯定有invoke，把所有的callee加入到worklist
        wpCFG.getSuccsOf(root).forEach(succ -> {
            if(!wpCFG.getMethodOf(succ).equals(wpCFG.getMethodOf(root))) {
                worklist.add(succ);
            }
        });

//        util.writeFilelnWithPrefix("\n--------------" + wpCFG.getMethodOf(root), "hidenMethodFieldProcess");
        while (!worklist.empty()) { // better than <for>
            Unit u = worklist.pop();
//            if(wpCFG.getMethodOf(u).toString().contains("com.xrbin.ddptTest")) {
//                util.plnP("buildVFGForHidenMethodField with " + u);
//            }
            if (!flagSonProcessAll.add(u)) continue;
            if (database.appMethod.contains(wpCFG.getMethodOf(u).toString()) && hidenAppMethod.add(wpCFG.getMethodOf(u))) {
//                util.writeFilelnWithPrefix(wpCFG.getMethodOf(u).toString(), "hidenAppMethod");
            }

//            if(DatabaseManager.getInstance().appMethod.contains(wpCFG.getMethodOf(u).toString()))
//                util.writeFilelnWithPrefix(wpCFG.getMethodOf(u).toString() + u, "hidenMethodFieldProcess");


            // cfl-R, 不走return边，只走callsite边
            if(!(u instanceof JReturnStmt || u instanceof JReturnVoidStmt || u instanceof JThrowStmt))
                worklist.addAll(wpCFG.getSuccsOf(u));


//            utils.myprintln("--biuld vfg son--" + wpCFG.getMethodOf(u) + " -- " + u.getClass() + "\t" + StaticData.G + u + StaticData.E, StaticData.E, utils.HIDENMETHOD);
//            util.plnP("--biuld vfg son--" + wpCFG.getMethodOf(u) + " -- " + u.getClass());
            if (u instanceof AssignStmt) {
                AssignStmt u0 = (AssignStmt) u;
                Value left = u0.getLeftOp();
                Value right = u0.getRightOp();

                // 遍历所有的实例域和静态域的def点，保存的是<l, v>对，注意：这里保存的是不带上下文的
                if (left instanceof InstanceFieldRef) {
//                    System.out.println("\t--------------" + ((JInstanceFieldRef) left).getBase().toString() + "." + ((JInstanceFieldRef) left).getField());
                    JInstanceFieldRef ifr = (JInstanceFieldRef) left;
//                    System.out.println("--------------" + wpCFG.getMethodOf(u).toString() + "/" + ifr.getBase().toString());
//                    Variable var = new Variable(u.getCtx(), wpCFG.getMethodOf(u).getMethod() + "/" + ifr.getBase().toString());
                    String var = wpCFG.getMethodOf(u) + "/" + ifr.getBase().toString();
//                    util.plnG("--buildVFG - store: " + var);
                    if (database.vptInsen.containsKey(var)) {
                        for (CSAllocation o : database.vptInsen.get(var)) {
                            if (!utils.isConstantAllocation(o.getO().getO())) {
                                LocatePointer lpp = new LocatePointer(u,
                                        new VFGvalue(new Field(o, ((JInstanceFieldRef) left).getField().toString())),
                                        wpCFG.getMethodOf(u).toString(),
                                        ObjContext.allContext);
                                if (!utils.isPrimitiveType(((JInstanceFieldRef) left).getField().getType())) {
                                    hidenMethodField.put(lpp, true);
//                                    if (!database.instanceFieldVPT.containsKey(lpp.getV())) {
//                                        System.out.println("--biuld vfg son--" + lpp + "\n");
//                                    }
//                                    util.plnP("--biuld vfg son--InstanceFieldRef--" + wpCFG.getMethodOf(u) + " -- " + u.getClass());
//                                    if(lpp.toString().contains())
                                    if (utils.BUILDVFGACCURATE) {
                                        if (!utils.isPrimitiveType(ifr.getType()) && !utils.isConstantAllocation(o.getO().getO())) {
                                            dfsStoreWithCg(u, new Field(o, ifr.getField().toString()));
                                        }
                                    }
                                    else {
                                        // 2022-01-14 20:41:08
                                        Field f = new Field(o, ifr.getField().toString());
                                        //                                System.out.println("\t\t--------------" + f);
                                        if (fieldUse.containsKey(f)) {
                                            for (Unit to : fieldUse.get(f)) {
                                                addEdge(u, to, new VFGvalue(f));
                                            }
                                        }
                                        if (fieldDefine.containsKey(f)) {
                                            for (Unit to : fieldDefine.get(f)) {
                                                addEdge(u, to, new VFGvalue(f));
                                            }
                                        }
                                        // 2022-01-14 20:41:08
                                    }
                                }
                            }
                        }
                    }
                }
                else if (left instanceof StaticFieldRef) {
                    StaticFieldRef sfr = (StaticFieldRef) left;
                    LocatePointer lpp = new LocatePointer(u,
                            new VFGvalue(sfr),
                            wpCFG.getMethodOf(u).toString(),
                            ObjContext.allContext);
                    if(!utils.isPrimitiveType(sfr.getType())) {
                        hidenMethodField.put(lpp, true);
//                    util.plnP("--biuld vfg son--staticFieldUse--" + wpCFG.getMethodOf(u) + " -- " + u.getClass());
                        if (true) {
                            if (staticFieldUse.containsKey(left.toString())) {
                                staticFieldUse.get(left.toString()).forEach(to -> addEdge(u, to, new VFGvalue(left)));
                            }
                        }
                        else {
                            dfs(u, u, sfr);
                        }
                    }
                }
            }
        }
    }

    public void buildVFGClinit() {
        if(!utils.CLINIT) return;

        HashSet<Body> allBodies = new HashSet<>();
        wpCFG.bodys.keySet().forEach(key -> {
            if(wpCFG.bodys.get(key).getMethod().isStaticInitializer()
//                    && DatabaseManager.getInstance().appMethod.contains(wpCFG.getBodys().get(key).toString())
//                    && wpCFG.bodys.get(key).getMethod().toString().contains("test.clinit")
            ) {
                allBodies.add(wpCFG.bodys.get(key));
            }
        });
//        System.out.println("allClinitBodies.size() = " + allBodies.size());

        Stack<Unit> worklist = new Stack<>();
        allBodies.forEach(b -> worklist.addAll(b.getUnits()));

//        util.plnR("buildVFGClinit worklist.size(): " + worklist.size());
//        worklist.forEach(u -> {
//            if(wpCFG.getMethodOf(u).toString().equals("<org.apache.fop.render.print.PagesMode: void <init>(java.lang.String)>")) {
//                util.plnP("buildVFGClinit with " + u);
//            }
//        });
//        HashSet<SootMethod> flagM = new HashSet<>();
        while (!worklist.empty()) {
            Unit u = worklist.pop();
//            if(wpCFG.getMethodOf(u).toString().equals("<org.apache.fop.render.print.PagesMode: void <init>(java.lang.String)>")) {
//                util.plnP("buildVFGClinit with " + u);
//            }
//            if(flagM.add(wpCFG.getMethodOf(u))) {
//                System.out.println("--buildVFGClinit-- "  + flagM.size() + " -- " + wpCFG.getMethodOf(u));
//            }
            if(!flagMainProcess.add(u)) continue; // bugbug 原来没有！ 取反 我人都傻了
            worklist.addAll(wpCFG.getSuccsOf(u));
            irCount++;
            buildVFGForOneUnit(u);
        }
    }

    private final HashSet<Unit> colorDfsLocal = new HashSet<>();
    private void dfs(Unit from, Unit u, JimpleLocal l) {
        colorDfsLocal.clear();

        if(u == null || utils.isPrimitiveType(l.getType())) return;
        Stack<Unit> worklist = new Stack<>();
        worklist.add(u);

        while (!worklist.empty()) {
            u = worklist.pop();
            if (u == null || !colorDfsLocal.add(u)) continue;
            if (wpCFG.getMethodOf(u) != null && !wpCFG.getMethodOf(from).equals(wpCFG.getMethodOf(u))) continue; // is it?

            worklist.addAll(wpCFG.getSuccsOf(u));

            if (!from.equals(u) && useJudge(u, l)) {
                addEdge(from, u, new VFGvalue(l));
            }
        }

    }

    private final HashSet<Unit> colorDfsSField = new HashSet<>();
    private void dfs(Unit from, Unit u, StaticFieldRef sf) {
        colorDfsSField.clear();

        if(utils.isPrimitiveType(sf.getType())) return;
        Stack<Unit> worklist = new Stack<>();
        worklist.add(u);

        while (!worklist.empty()) {
            u = worklist.pop();
            if (u == null || !colorDfsSField.add(u)) continue;
            worklist.addAll(wpCFG.getSuccsOf(u));

            if (!from.equals(u)) {
                if (u instanceof AssignStmt) {
                    if (((AssignStmt) u).getLeftOp() instanceof StaticFieldRef) {
                        if (((StaticFieldRef) ((AssignStmt) u).getLeftOp()).getField().equals(sf.getField())) {

//                            return;
                        }
                    }
                    else if (((AssignStmt) u).getRightOp() instanceof StaticFieldRef) {
                        if (((StaticFieldRef) ((AssignStmt) u).getRightOp()).getField().equals(sf.getField())) {
                            addEdge(from, u, new VFGvalue(sf));
                        }
                    }
                }
            }
        }
    }

    boolean flagInter = true; // a.f = b; a.f = c; a is assigned in where?
    private final HashSet<Unit> colorDfsStoreInter = new HashSet<>();
    public void dfsStoreInter(Unit u, Unit from, Field l) {
        // 这里出过一个大bug，看起来像是每个语句被访问了几次，但其实只有一次，出现几次是因为 a.f = b; 的a指向几个对象，然后调用这个函数几次，Field l是不一样的，哭了。
        flagInter = true;
        colorDfsStoreInter.clear();

        JInstanceFieldRef ifrLFrom = (JInstanceFieldRef) ((AssignStmt) u).getLeftOp();

        Stack<Unit> worklist = new Stack<>();
        for (Unit succ : wpCFG.getSuccsOf(from)) {
            worklist.push(succ);
        }

        while (!worklist.empty()) {
            Unit cur = worklist.pop();
//            util.plnP("dfsStore(Unit u, Field l): " + cur);
            if (!colorDfsStoreInter.add(cur)){
                continue;
            }

            if (cur instanceof AssignStmt &&
                    ((AssignStmt) cur).getLeftOp().equals((
                            (JInstanceFieldRef)(((AssignStmt) u).getLeftOp())).getBase())
            ) {
//                util.plnP("-------------------------flag = false;");
                flagInter = false;
            }

            if (useJudge(cur, l) && cur instanceof  AssignStmt) {
                Value ifrL = ((AssignStmt) cur).getLeftOp();
                Value ifrR = ((AssignStmt) cur).getRightOp();
                JInstanceFieldRef ifr;

//                util.plnY(u.getCtx() + ":\t" + ((AssignStmt) u).getLeftOp().toString());
//                util.plnY(u.getCtx() + ":\t" + ((AssignStmt) u).getLeftOp().toString());

                // a.f = b; a.f = c;
                if (ifrL instanceof JInstanceFieldRef
                        && ((JInstanceFieldRef) ifrL).getBase().equals(ifrLFrom.getBase())
                        && ((JInstanceFieldRef) ifrL).getField().equals(ifrLFrom.getField())
                        && flagInter
                ) {
                    continue; // 2021-10-05 09:11:35 is it？
                }
//                System.out.println("\t" + u);

                // a.f = q; b.f = p; b -> one object o (note method args)
                if (ifrL instanceof JInstanceFieldRef) {
                    ifr = (JInstanceFieldRef) ifrL;
//                    Variable var = new Variable(cur.getCtx(), wpCFG.getMethodOf(cur).getMethod() + "/" + ifr.getBase().toString());
                    String var = wpCFG.getMethodOf(u) + "/" + ifr.getBase().toString();
//                    util.plnG("--dfsStore: " + var);
                    if (database.vptInsen.containsKey(var) && database.vptInsen.get(var) != null) {
                        if (database.vptInsen.get(var).size() == 1) {
                            continue; // dfs end here, because its son nodes don't get in worklist
                        }
                        else if (database.vptInsen.get(var).size() > 1) {
//                            addEdge(u, cur, new VFGvalue(l));
                        }
                        // else {} no this situation
                    }
                }
                else if (ifrR instanceof JInstanceFieldRef) {
                    addEdge(u, cur, new VFGvalue(l));
                }
//                else {
//                    System.out.println("======Strong update: " + cur);
//                }
            }
            for (Unit succ : wpCFG.getSuccsOf(cur)) {
                worklist.push(succ);
            }
        }
    }

    boolean flag = true; // a.f = b; a.f = c; a is assigned in where?
    private final HashSet<Unit> colorDfsStore = new HashSet<>();
    public void dfsStoreIntro(Unit u, Unit from, Field l) {
        // 这里出过一个大bug，看起来像是每个语句被访问了几次，但其实只有一次，出现几次是因为 a.f = b; 的a指向几个对象，然后调用这个函数几次，Field l是不一样的，哭了。
        flag = true;
        colorDfsStore.clear();

        JInstanceFieldRef ifrLFrom = (JInstanceFieldRef) ((AssignStmt) u).getLeftOp();

        Stack<Unit> worklist = new Stack<>();
        wpCFG.getSuccsOf(from).forEach(worklist::push);

        while (!worklist.empty()) {
            Unit cur = worklist.pop();
//            util.plnP("dfsStore(Unit u, Field l): " + cur);
            if (!colorDfsStore.add(cur) || !wpCFG.getMethodOf(from).equals(wpCFG.getMethodOf(cur))){
                continue;
            }

            if (cur instanceof AssignStmt &&
                    ((AssignStmt) cur).getLeftOp().equals((
                            (JInstanceFieldRef)(((AssignStmt) u).getLeftOp())).getBase())
            ) {
//                util.plnP("-------------------------flag = false;");
                flag = false;
            }

            if (useJudge(cur, l) && cur instanceof  AssignStmt) {
                Value ifrL = ((AssignStmt) cur).getLeftOp();
                Value ifrR = ((AssignStmt) cur).getRightOp();
                JInstanceFieldRef ifr;

//                util.plnY(u.getCtx() + ":\t" + ((AssignStmt) u).getLeftOp().toString());
//                util.plnY(u.getCtx() + ":\t" + ((AssignStmt) u).getLeftOp().toString());

                // (1) a.f = b; (2) a.f = c;
                if (ifrL instanceof JInstanceFieldRef
                        && ((JInstanceFieldRef) ifrL).getBase().equals(ifrLFrom.getBase())
                        && ((JInstanceFieldRef) ifrL).getField().equals(ifrLFrom.getField())
                        && flag
                ) {
                    // 并不需要这个flag，思考def use的关系，(1)到a的定值点可达，那(1)到(2)必然可达，因为a的定值点到(1)(2)必然都是可达的
                    continue; // 2021-10-05 09:11:35 is it？
                }
//                System.out.println("\t" + u);

                // a.f = q; b.f = p; b -> one object o (note method args)
                if (ifrL instanceof JInstanceFieldRef) {
                    ifr = (JInstanceFieldRef) ifrL;
//                    Variable var = new Variable(cur.getCtx(), wpCFG.getMethodOf(cur).getMethod() + "/" + ifr.getBase().toString());
                    String var = wpCFG.getMethodOf(u) + "/" + ifr.getBase().toString();
//                    util.plnG("--dfsStore: " + var);
                    if (database.vptInsen.containsKey(var) && database.vptInsen.get(var) != null) {
                        if (database.vptInsen.get(var).size() == 1) {
                            // 意味着值流会在这里被截断，must的重新定值
                            continue; // dfs end here, because its son nodes don't get in worklist
                        }
                        else if (database.vptInsen.get(var).size() > 1) {
                            addEdge(u, cur, new VFGvalue(l));
                        }
                        // else {} no this situation
                    }
                }
                else if (ifrR instanceof JInstanceFieldRef) {
                    addEdge(u, cur, new VFGvalue(l));
                }
//                else {
//                    System.out.println("======Strong update: " + cur);
//                }
            }
            wpCFG.getSuccsOf(cur).forEach(worklist::push);
        }
    }

    private final HashSet<Unit> colorDfsStoreWithCg = new HashSet<>();
    public void dfsStoreWithCg(Unit u, Field l) {
        // 这里出过一个大bug，看起来像是每个语句被访问了几次，但其实只有一次，出现几次是因为 a.f = b; 的a指向几个对象，然后调用这个函数几次，Field l是不一样的，哭了。

        // 得走 return 边回到调用该方法的方法。

        colorDfsCg.clear();
        colorDfsStoreWithCg.clear();
        flag = true; // a.f = b; a.f = c; a is assigned in where?

        JInstanceFieldRef ifrLFrom = (JInstanceFieldRef) ((AssignStmt) u).getLeftOp();

        Stack<Unit> worklist = new Stack<>();
        for (Unit succ : wpCFG.getSuccsOf(u)) {
            worklist.push(succ);
        }

        List<Unit> invokes = new ArrayList<>();
        while (!worklist.empty()) {
            Unit cur = worklist.pop();
//            util.plnP("dfsStoreWithCg(Unit u, Field l): " + cur);
            if (!colorDfsStoreWithCg.add(cur)) {
                continue;
            }

            if ((cur instanceof AssignStmt && ((AssignStmt) cur).getRightOp() instanceof InvokeExpr)
                    || cur instanceof InvokeStmt
            ) {
                invokes.add(cur);
            }

            if (cur instanceof AssignStmt &&
                    ((AssignStmt) cur).getLeftOp().equals((
                            (JInstanceFieldRef) (((AssignStmt) u).getLeftOp())).getBase())
            ) {
//                util.plnP("-------------------------flag = false;");
                flag = false;
            }

            if (useJudge(cur, l) && cur instanceof AssignStmt) {
                Value ifrL = ((AssignStmt) cur).getLeftOp();
                Value ifrR = ((AssignStmt) cur).getRightOp();
                JInstanceFieldRef ifr;

//                util.plnY(u.getCtx() + ":\t" + ((AssignStmt) u).getLeftOp().toString());
//                util.plnY(u.getCtx() + ":\t" + ((AssignStmt) u).getLeftOp().toString());

                // a.f = b; a.f = c;
                if (ifrL instanceof JInstanceFieldRef
                        && ((JInstanceFieldRef) ifrL).getBase().equals(ifrLFrom.getBase())
                        && ((JInstanceFieldRef) ifrL).getField().equals(ifrLFrom.getField())
                        && flag
                ) {
                    continue; // 2021-10-05 09:11:35 is it？
                }
//                System.out.println("\t" + u);

                // a.f = q; b.f = p; b -> one object o (note method args)
                if (ifrL instanceof JInstanceFieldRef) {
                    ifr = (JInstanceFieldRef) ifrL;
//                    Variable var = new Variable(cur.getCtx(), wpCFG.getMethodOf(cur).getMethod() + "/" + ifr.getBase().toString());
                    String var = wpCFG.getMethodOf(u) + "/" + ifr.getBase().toString();
//                    util.plnG("--dfsStore: " + var);
                    if (database.vptInsen.containsKey(var) && database.vptInsen.get(var) != null) {
                        if (database.vptInsen.get(var).size() == 1) {
                            continue; // dfs end here, because its son nodes don't get in worklist
                        }
                        else if (database.vptInsen.get(var).size() > 1) {
                            addEdge(u, cur, new VFGvalue(l));
                        }
                        // else {} no this situation
                    }
                }
                else if (ifrR instanceof JInstanceFieldRef) {
                    addEdge(u, cur, new VFGvalue(l));
                }
//                else {
//                    System.out.println("======Strong update: " + cur);
//                }
            }

            if(cur instanceof ReturnVoidStmt || cur instanceof ReturnStmt) {
                worklist.addAll(wpCFG.getSuccsOf(cur));
            }
            else {
                wpCFG.getSuccsOf(cur).forEach(succ -> {
                    if(wpCFG.getMethodOf(cur).equals(wpCFG.getMethodOf(succ))) {
                        worklist.add(succ);
                    }
                });
            }
        }

        invokes.forEach(invoke -> {
            InvokeExpr ie;
            if(invoke instanceof AssignStmt && ((AssignStmt) invoke).getRightOp() instanceof InvokeExpr) {
                ie = (InvokeExpr)((AssignStmt) invoke).getRightOp();
            }
            else {
                ie = ((InvokeStmt)invoke).getInvokeExpr();
            }

            String methodName =  wpCFG.getMethodOf(invoke).toString() + "/" + ie.getMethod().getDeclaringClass() + "." + ie.getMethod().getName();
//            util.plnB("\tmethodName: " + methodName);
            methodName = database.mil.get(methodName + "/" + utils.getLineNumber(invoke));
//            util.plnB("\tmethodName: " + methodName);
            if (database.cg.get(methodName) != null) {
                for (String cm : database.cg.get(methodName)) {
                    if (wpCFG.bodys.containsKey(cm)) {
                        dfsCg(wpCFG.bodys.get(cm).getMethod(), u, l);
                    }
                }
            }
        });

//        Main.cg.getSuccsOf(wpCFG.getMethodOf(u)).forEach(m -> {
//            if(!m.getO2()) {
//                dfsCg2(m.getO1(), u, l);
//            }
//        });
    }

    private final HashSet<SootMethod> colorDfsCg = new HashSet<>();
    private void dfsCg(SootMethod sm, Unit u, Field l) {
        Stack<SootMethod> worklist = new Stack<>();
        worklist.push(sm);
        while(!worklist.empty()) {
            SootMethod curM = worklist.pop();
            if (!colorDfsCg.add(curM)) {
                continue;
            }
            Main.cg.getSuccsOf(curM).forEach(m -> {
                // 只走call边，不走return边
                if (m.getO2())
                    worklist.push(m.getO1());

                if(fieldDefUseMethod.get(l).contains(curM))
                    wpCFG.getCfgs().get(curM.toString()).getHeads().forEach(from -> dfsStoreIntro(u, from, l));
            });
        }
    }

    private boolean useJudge(Unit u, JimpleLocal l) {
        if (u instanceof AssignStmt) {
            AssignStmt u0 = (AssignStmt) u;

            Value left = u0.getLeftOp();
            Value right = u0.getRightOp();
            if (left instanceof JimpleLocal) {
                if (right instanceof JimpleLocal) {
                    return right.equals(l);
                }
                else if (right instanceof JCastExpr) {
                    return ((JCastExpr) right).getOp().equals(l);
                }
                else if (right instanceof SPhiExpr) {
                    SPhiExpr spe = (SPhiExpr) right;
                    for (ValueBox vb : spe.getUseBoxes()) {
                        Value v = vb.getValue();
                        if (l.equivTo(v)) return true;
                    }
                }
                else if (right instanceof JInstanceFieldRef) {
                    Value base = ((JInstanceFieldRef) right).getBase();
                    return base.equals(l);
                }
                else if (right instanceof JVirtualInvokeExpr) {
                    JVirtualInvokeExpr jvi = (JVirtualInvokeExpr) right;
                    if (jvi.getBase().equals(l)) return true;
                    for (Value v : jvi.getArgs()) {
                        if (l.equivTo(v)) return true;
                    }
                }
                else if (right instanceof JSpecialInvokeExpr) {
                    JSpecialInvokeExpr jvi = (JSpecialInvokeExpr) right;
                    for (Value v : jvi.getArgs()) {
                        if (l.equivTo(v)) return true;
                    }
                    return jvi.getBase().equals(l);
                }
                else if (right instanceof JStaticInvokeExpr) {
                    JStaticInvokeExpr jvi = (JStaticInvokeExpr) right;
                    for (Value v : jvi.getArgs()) {
                        if (l.equivTo(v)) return true;
                    }
                    return false;
                }
                else if (right instanceof JInterfaceInvokeExpr) {
                    JInterfaceInvokeExpr jvi = (JInterfaceInvokeExpr) right;
                    for (Value v : jvi.getArgs()) {
                        if (l.equivTo(v)) return true;
                    }
                    return false;
                }
                else if (right instanceof JDynamicInvokeExpr) {
                    JDynamicInvokeExpr jvi = (JDynamicInvokeExpr) right;
                    for (Value v : jvi.getArgs()) {
                        if (l.equivTo(v)) return true;
                    }
                    return false;
                }
                else if (right instanceof ArrayRef) {
                    return ((ArrayRef) right).getBase().equals(l);
                }
                else {
                    return false;
                }
//                else if (right instanceof JNewExpr) {
//                    return false;
//                }
//                else if (right instanceof JNewArrayExpr) {
//                    return false;
//                }
//                else if (right instanceof JNewMultiArrayExpr) {
//                    return false;
//                }
//                else if (right instanceof StringConstant) {
//                    return false;
//                }
//                else if (right instanceof ClassConstant) {
//                    return false;
//                }
//                else if (right instanceof NumericConstant) {
//                    return false;
//                }
//                else if (right instanceof NullConstant) {
//                    return false;
//                }
//                else if (right instanceof BinopExpr) {
//                    return false;
//                }
//                else if (right instanceof UnopExpr) {
//                    return false;
//                }
//                else if (right instanceof StaticFieldRef) {
//                    return false;
//                }
//                else {
////                    System.err.println("useJudge(Unit u, JimpleLocal l): " + u + ": " + l);
//                }
            }
            else if (left instanceof StaticFieldRef || left instanceof ArrayRef) {
                if (right instanceof JimpleLocal) {
                    return right.equals(l);
                }
                else if (right instanceof JCastExpr) {
                    return ((JCastExpr) right).getOp().equals(l);
                }
//                else if (right instanceof ThisRef) {
//                    return right.equals(l);
//                }
                else if (right instanceof StringConstant) {
                    return false;
                }
                else if (right instanceof ClassConstant) {
                    return false;
                }
                else if (right instanceof NumericConstant) {
                    return false;
                }
                else if (right instanceof NullConstant) {
                    return false;
                }
                else if (right instanceof BinopExpr) {
                    return false;
                }
                else if (right instanceof UnopExpr) {
                    return false;
                }
                else {
                    System.err.println("useJudge(Unit u, JimpleLocal l): " + u + ": " + l);
                }
            }
            else if (left instanceof JInstanceFieldRef) {
                Value base = ((JInstanceFieldRef) left).getBase();
                if(base.equals(l)) return true;

                if (right instanceof JimpleLocal) {
                    return right.equals(l);
                }
                else if (right instanceof JCastExpr) {
                    return ((JCastExpr) right).getOp().equals(l);
                }
                else if (right instanceof ThisRef) {
                    return right.equals(l);
                }
                else if (right instanceof StringConstant) {
                    return false;
                }
                else if (right instanceof ClassConstant) {
                    return false;
                }
                else if (right instanceof NumericConstant) {
                    return false;
                }
                else if (right instanceof NullConstant) {
                    return false;
                }
                else if (right instanceof BinopExpr) {
                    return false;
                }
                else if (right instanceof UnopExpr) {
                    return false;
                }
                else {
                    System.err.println("useJudge(Unit u, JimpleLocal l): " + u + ": " + l);
                }
            }
            else {
                System.err.println("useJudge(Unit u, JimpleLocal l): " + u + ": " + l);
            }
        }
        else if (u instanceof JReturnStmt) {
            JReturnStmt jrs = (JReturnStmt) u;
            return l.equivTo(jrs.getOp());
        }
        else if (u instanceof JInvokeStmt) {
            InvokeExpr ie = ((JInvokeStmt) u).getInvokeExpr();
            if (ie instanceof JVirtualInvokeExpr) {
                JVirtualInvokeExpr jvi = (JVirtualInvokeExpr) ie;
                for (Value v : jvi.getArgs()) {
                    if (l.equivTo(v)) return true;
                }
                return jvi.getBase().equals(l);
            }
            else if (ie instanceof JSpecialInvokeExpr) {
                JSpecialInvokeExpr jvi = (JSpecialInvokeExpr) ie;
                for (Value v : jvi.getArgs()) {
                    if (l.equivTo(v)) return true;
                }
                return jvi.getBase().equals(l);
            }
            else if (ie instanceof JStaticInvokeExpr) {
                JStaticInvokeExpr jvi = (JStaticInvokeExpr) ie;
                for (Value v : jvi.getArgs()) {
                    if (l.equivTo(v)) return true;
                }
                return false;
            }
            else if (ie instanceof JDynamicInvokeExpr) {
                JDynamicInvokeExpr jvi = (JDynamicInvokeExpr) ie;
                for (Value v : jvi.getArgs()) {
                    if (l.equivTo(v)) return true;
                }
                return false;
            }
            else if (ie instanceof InterfaceInvokeExpr) {
                InterfaceInvokeExpr jvi = (InterfaceInvokeExpr) ie;
                for (Value v : jvi.getArgs()) {
                    if (l.equivTo(v)) return true;
                }
                return jvi.getBase().equals(l);
            }
            else {
//                System.err.println("useJudge(Unit u, JimpleLocal l): " + u + ": " + l);
            }
        }
        else if (u instanceof JIdentityStmt) {
            return false;
        }
        else {
            return false;
        }
        return false;
    }

    private boolean useJudge(Unit u, Field l) {
        if (u instanceof AssignStmt) {
            AssignStmt assignStmt = (AssignStmt) u;
            Value left = assignStmt.getLeftOp();
            Value right = assignStmt.getRightOp();

            JInstanceFieldRef ifr;
            if (left instanceof JimpleLocal && right instanceof JInstanceFieldRef) {
                ifr = (JInstanceFieldRef) right;
            }
            else if (left instanceof JInstanceFieldRef) {
                ifr = (JInstanceFieldRef) left;
            }
            else {
                return false;
            }
//--biuld vfg--<com.xrbin.ddptTest.test.test1: void func6()> -- class soot.jimple.internal.AssignStmt	[32;1mbbb1_1.<com.xrbin.ddptTest.test.B: com.xrbin.ddptTest.test.A a> = a1[0m
            String var = wpCFG.getMethodOf(u) + "/" + ifr.getBase().toString();

//            if(ifr.toString().equals("bbb1_1.<com.xrbin.ddptTest.test.B: com.xrbin.ddptTest.test.A a>") && ) {
//
//            }

            if (database.vptInsen.containsKey(var)) {
                return database.vptInsen.get(var).contains(l.getO().getO()) && l.getField().equals(ifr.getField().toString());
            }
        }
        return false;
    }

    private static int vfgEdegs = 0;
    private void addEdge(Unit from, Unit to, VFGvalue vv) {
        if(from.equals(to)) return;
        if(utils.modle == utils.DEBUG) {
            if (DatabaseManager.getInstance().appMethod.contains(wpCFG.getMethodOf(from).toString())) {
                util.myprintln("------addEdge\t[" + wpCFG.getMethodOf(from) + ": " + from + "]\t->\t[" + wpCFG.getMethodOf(to) + ": " + to + "]: " + vv, StaticData.B, utils.BUILDVFG);
            }
        }
        vfgEdegs++;

        if (!unitToSuccs.containsKey(from)) {
            unitToSuccs.put(from, new ArrayList<>());
        }
        if (!unitToPreds.containsKey(to)) {
            unitToPreds.put(to, new ArrayList<>());
        }
        if (!unitToSuccs.get(from).contains(to)) {
            unitToSuccs.get(from).add(to);
        }
        if (!unitToPreds.get(to).contains(from)) {
            unitToPreds.get(to).add(from);
        }

        if (!vfgValue.containsKey(from)) {
            vfgValue.put(from, new HashMap<>());
        }
        if (!vfgValue.get(from).containsKey(to)) {
            vfgValue.get(from).put(to, new HashSet<>());
        }
        vfgValue.get(from).get(to).add(vv);
    }

    public void deleteEdge(Unit from, Unit to, VFGvalue vv) {
        if(utils.modle == utils.DEBUG) {
            if (DatabaseManager.getInstance().appMethod.contains(wpCFG.getMethodOf(from).toString())) {
                util.myprintln("------deleteEdge\t[" + from + "]\t->\t[" + to + "]: " + vv, StaticData.B, utils.BUILDVFG);
            }
        }
//        util.plnP("------deleteEdge\t[" + from + "]\t->\t[" + to + "]: " + vv + "\n");


        this.getEdgeValue(from, to).remove(vv);
        if (this.getEdgeValue(from, to).size() == 0) {
            if (unitToSuccs.containsKey(from)) {
                if (unitToSuccs.get(from).contains(to)) {
                    if (unitToPreds.containsKey(to)) {
                        if (unitToPreds.get(to).contains(from)) {
                            unitToSuccs.get(from).remove(to);
                            unitToPreds.get(to).remove(from);
                            return;
                        }
                    }
                }
            }
            System.err.println("deleteEdge(Unit from, Unit to, VFGvalue vv): wrong value flow graph.");
        }
    }

    public List<Unit> getSuccs(Unit u) {
        return unitToSuccs.computeIfAbsent(u, unit -> new ArrayList<>());
    }

    public List<Unit> getPreds(Unit u) {
        return unitToPreds.computeIfAbsent(u, unit -> new ArrayList<>());
    }

    public HashSet<VFGvalue> getEdgeValue(Unit from, Unit to) {
        if (vfgValue.get(from) == null) {
            vfgValue.computeIfAbsent(from, unit -> new HashMap<>());
        }
        if (vfgValue.get(from).get(to) == null) {
            vfgValue.get(from).computeIfAbsent(to, unit -> new HashSet<>());
        }
        return vfgValue.get(from).get(to);
    }

}
