package com.xrbin.ddpt;

import com.xrbin.ddpt.model.*;
import com.xrbin.utils.StaticData;
import com.xrbin.utils.util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.*;
import soot.jimple.*;
import soot.jimple.internal.*;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.DirectedGraph;
import soot.util.Chain;
import soot.util.HashChain;

import java.util.*;

public class WholeProgramCFG implements DirectedGraph<Unit> {
    private static final Logger logger = LoggerFactory.getLogger(WholeProgramCFG.class);
    protected DatabaseManager database = DatabaseManager.getInstance();

    protected List<Unit> heads = new ArrayList<>();
    protected List<Unit> tails = new ArrayList<>();

    protected Chain<Unit> unitChain = new HashChain<>();
    protected Map<Unit, List<Unit>> unitToSuccs = new HashMap<>();
    protected Map<Unit, List<Unit>> unitToPreds = new HashMap<>();

    protected Map<SootMethod, List<Unit>> methodToHeads = new HashMap<>();
    protected Map<SootMethod, List<Unit>> methodToTails = new HashMap<>();
    protected Map<String,  HashSet<Unit>> methodToUnits = new HashMap<>();

    protected SootMethod mainMethod;
    protected HashMap<String, Body> bodys;
    protected Map<Unit, SootMethod> unitToMethod = new HashMap<>();
    protected HashMap<String, DirectedGraph<Unit>> cfgs = new HashMap<>();

    protected HashSet<String> cfgFlag = new HashSet<>();

    public void buildCFG() {
        init();

        Queue<String> worklist = new ArrayDeque<>();

        if(utils.CLINIT) {
            bodys.keySet().forEach(key -> {
//            util.plnR("\nkey: " + bodys.get(key).getMethod().getName());
                if (bodys.get(key).getMethod().isStaticInitializer()
//            && bodys.get(key).getMethod().toString().contains("test.clinit")
                ) {
//                utils.writeFileln("\tkey: " + key, StaticData.DEBUGOUTPUTFILE);
                    worklist.offer(key);
                }
            });
        }

        Vector<String> methodCount = new Vector<>();
        worklist.offer(mainMethod.toString());
        // method by method
        while (!worklist.isEmpty()) {
            String cur = worklist.poll();
            if (!cfgFlag.add(cur)) continue;
            if (!bodys.containsKey(cur)) continue;
//            if (bodys.get(cur).getMethod().toString().contains("<org.apache.fop.render.print.PagesMode: void <init>(java.lang.String)>")) {
////            utils.writeFileln("---- " + body.getMethod().toString(), StaticData.DEBUGOUTPUTFILE);
//                for (Unit u : bodys.get(cur).getUnits()) {
////                utils.writeFileln("\t---- " + u.toString(), StaticData.DEBUGOUTPUTFILE);
//                    System.out.println(u);
//                }
//            }
            methodCount.add(cur);
            methodToUnits.put(cur, new HashSet<>());
            HashSet<Unit> units = methodToUnits.get(cur);

//            util.plnBG("cur method: " + cur);
//            utils.writeFileln("\ncur method: " + cur, StaticData.DEBUGOUTPUTFILE);

            DirectedGraph<Unit> cfg = cfgs.get(cur);
            Body b = bodys.get(cur);

            // copy cur method
            for (Unit u : b.getUnits()) {
                units.add(u);
//                if (!unitChain.contains(u)) {
//                    util.plnP("\t: " + u);
                    unitChain.addLast(u);
//                }
                if (cfg.getSuccsOf(u) != null) {
                    copyList(unitToSuccs, u, cfg.getSuccsOf(u));
                }
                if (cfg.getPredsOf(u) != null) {
                    copyList(unitToPreds, u, cfg.getPredsOf(u));
                }
                SootMethod cm = b.getMethod();
//                util.plnG(u + ": " + cm);
                unitToMethod.put(u, cm);
            }
//            util.plnG(b.getMethod().toString());
            methodToHeads.computeIfAbsent(bodys.get(cur).getMethod(), k -> new ArrayList<>());
            methodToTails.computeIfAbsent(bodys.get(cur).getMethod(), k -> new ArrayList<>());
            methodToHeads.get(bodys.get(cur).getMethod()).addAll(cfg.getHeads());
            methodToTails.get(bodys.get(cur).getMethod()).addAll(cfg.getTails());

            // copy part of callee method units of cur method
            for (Unit u : b.getUnits()) {
                InvokeExpr v = null;
                if (u instanceof JInvokeStmt) {
                    v = ((JInvokeStmt) u).getInvokeExpr();
                }
                else if (u instanceof JAssignStmt && ((JAssignStmt) u).containsInvokeExpr()) {
                    v = ((JAssignStmt) u).getInvokeExpr();
                }

                if (v != null) {

                    String methodName = b.getMethod().toString() + "/" + v.getMethod().getDeclaringClass() + "." + v.getMethod().getName();
//                    util.plnP("\tmethodName: " + methodName);
                    methodName = database.mil.get(methodName + "/" + utils.getLineNumber(u));
//                    util.plnP("\tmethodName: " + methodName);

                    if (database.cg.get(methodName) != null) {
                        for (String cm : database.cg.get(methodName)) {
                            util.myprintln("\t" + cm, StaticData.G, utils.WPCFG);

                            if (bodys.containsKey(cm)) {
                                util.myprintln("\t" + cm, StaticData.R, utils.WPCFG);

                                worklist.offer(cm);
                                DirectedGraph<Unit> cfgTo = cfgs.get(cm);

                                // caller's call-unit connect with callee's heads
                                copyList(unitToSuccs, u, cfgTo.getHeads());
                                cfgTo.getHeads().forEach(head -> copyList(unitToPreds, head, u));

                                // callee's tails connect with call-site's succs
                                // if(tail instanceof ReturnVoidStmt || tail instanceof ReturnStmt) { // throw is also tail
                                cfgTo.getTails().forEach(tail -> copyList(unitToSuccs, tail, cfg.getSuccsOf(u)));
                                cfg.getSuccsOf(u).forEach(succ -> copyList(unitToPreds, succ, cfgTo.getTails()));

                                // must not cut edge between call statement and its next statement
                            }
                        }
                    }
                }
            }
        }


//        this.unitChain.addFirst(entrance);
//        this.getPredsOf(heads.get(0)).add(entrance);
//        this.unitToSuccs.put(entrance, new ArrayList<>());
//        this.unitToPreds.put(entrance, new ArrayList<>());
//        this.getSuccsOf(entrance).add(heads.get(0));
//        this.getPredsOf(entrance).add(entrance);
//        this.getSuccsOf(entrance).add(entrance);
//        heads.clear();
//        heads.add(entrance);

//        for (Unit u : unitChain) {
//            if (!u.equals(entrance) && getMethodOf(u).toString().contains("<org.apache.fop.render.print.PagesMode: void <init>(java.lang.String)>")) {
//                System.out.println(u);
//                getSuccsOf(u).forEach(succ -> {
//                    System.out.println("\t" + succ);
//                });
//            }
//        }

        // all clinit conect with the main method's heads
        if(utils.CLINIT) {
            cfgs.keySet().forEach(key -> {
                DirectedGraph<Unit> cfg = cfgs.get(key);
                if (bodys.get(key).getMethod().isStaticInitializer()) {
                    cfg.getTails().forEach(tail -> heads.forEach(head -> unitToSuccs.get(tail).add(head)));
                }
            });
        }

        util.plnR("methodCount.size() = " + methodCount.size());
//        printCFG();
    }

    protected void init(){
        // construct all methods' CFG
        for(String key : bodys.keySet()){
            DirectedGraph<Unit> cfg = new BriefUnitGraph(bodys.get(key));
            cfgs.put(key, cfg);
        }

        DirectedGraph<Unit> c = cfgs.get(mainMethod.toString());
        heads = new ArrayList<>();
        tails = new ArrayList<>();
//        util.plnR(mainMethod.toString());

        if(c.getHeads() != null) {
            heads.addAll(c.getHeads());
        }
        if(c.getTails() != null) {
            tails.addAll(c.getTails());
        }
    }

    public SootMethod getMainMethod() {
        return mainMethod;
    }

    public HashMap<String, Body> getBodys() {
        return bodys;
    }

    public HashMap<String, DirectedGraph<Unit>> getCfgs() {
        return cfgs;
    }

    public WholeProgramCFG(HashMap<String, Body> bodys, String mainMethod) {
        this.bodys = bodys;
        try {
            this.mainMethod = bodys.get(mainMethod).getMethod();
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    protected void addEdge(Map<Unit, List<Unit>> unitToSuccs, Map<Unit, List<Unit>> unitToPreds, Unit head, Unit tail) {
        List<Unit> headsSuccs = unitToSuccs.computeIfAbsent(head, k -> new ArrayList<Unit>(3));
        // We expect this list to
        // remain short.
        if (!headsSuccs.contains(tail)) {
            headsSuccs.add(tail);
            List<Unit> tailsPreds = unitToPreds.computeIfAbsent(tail, k -> new ArrayList<Unit>());
            tailsPreds.add(head);
        }
    }

    public List<Unit> getHeads() {
        return heads;
    }

    public List<Unit> getTails() {
        return tails;
    }

    public Chain<Unit> getUnits() {
        return unitChain;
    }

    public List<Unit> getPredsOf(Unit u) {
        List<Unit> l = unitToPreds.get(u);
        if (l == null) {
            return Collections.emptyList();
        }

        return l;
    }

    public List<Unit> getSuccsOf(Unit u) {
        List<Unit> l = unitToSuccs.get(u);
        if (l == null) {
            return Collections.emptyList();
        }
        return l;
    }

    public SootMethod getMethodOf(Unit u) {
        return unitToMethod.get(u);
    }

    public List<Unit> getTailsOfMethod(SootMethod cm) {
        List<Unit> l = methodToTails.get(cm);
        if (l == null) {
            return Collections.emptyList();
        }
        return l;
    }

    public List<Unit> getHeadsOfMethod(SootMethod cm) {
        List<Unit> l = methodToHeads.get(cm);
        if (l == null) {
            return Collections.emptyList();
        }
        return l;
    }

    protected void copyList(Map<Unit, List<Unit>> map, Unit key, List<Unit> from) {
        if(!map.containsKey(key)) {
            map.put(key, new ArrayList<>());
        }

        for(Unit u : from) {
            map.get(key).add(u);
        }
    }

    protected void copyList(Map<Unit, List<Unit>> map, Unit key, Unit from) {
        if(!map.containsKey(key)) {
            map.put(key, new ArrayList<>());
        }
        map.get(key).add(from);
    }

    public int size() {
        return unitChain.size();
    }

    @Override
    public @org.jetbrains.annotations.NotNull Iterator<Unit> iterator() {
        return unitChain.iterator();
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (Unit u : unitChain) {
            buf.append("// preds: ").append(getPredsOf(u)).append('\n');
            buf.append(u).append('\n');
            buf.append("// succs ").append(getSuccsOf(u)).append('\n');
        }
        return buf.toString();
    }

    private void printCFG(){
        for(Unit u : unitChain) {
            util.plnG("\n" + u.toString());
            for(Unit succ : getSuccsOf(u)) {
                util.plnY("\t" + succ.toString());
            }
        }

        for(Unit u : unitChain) {
            util.plnG("\n" + u.toString());
            for(Unit pred : getPredsOf(u)) {
                util.plnY("\t" + pred.toString());
            }
        }

        for (Unit u : unitChain) {
//            System.out.println("\n" + u);
//            for(ValueBox vb : u.getUseBoxes()) {
//                util.plnG("\t" + vb.getValue().toString());
//            }
//            for(ValueBox vb : u.getDefBoxes()) {
//                util.plnB("\t" + vb.getValue().toString());
//            }

            if (u instanceof JAssignStmt) {
                JAssignStmt u0 = (JAssignStmt) u;
                Value left = u0.getLeftOp();
                Value right = u0.getRightOp();
//                if(left instanceof Local && randomGet()) {
//                    VFGvalue vv = new VFGvalue((JimpleLocal) ((JAssignStmt) u).getLeftOp());
//                    lv = getLocatePointer(u, vv);
//                    break;
//                }
            }
            else if (u instanceof JIdentityStmt) {
                JIdentityStmt u0 = (JIdentityStmt) u;
                Value left = u0.getLeftOp();
                Value right = u0.getRightOp();
//                System.out.println(u0);
//                System.out.println("\t" + left.getClass() + "\t" + left);
//                System.out.println("\t" + right.getClass() + "\t" + right);
            }
            else if (u instanceof JReturnStmt) {
                JReturnStmt u0 = (JReturnStmt) u;
//                System.out.println(u0);
//                System.out.println("\t" + u0.getOp());
                for (ValueBox vb : u0.getDefBoxes()) {
                    Value v = vb.getValue();
//                    System.out.println("\t" + v);
                }
            }
            else if (u instanceof JInvokeStmt) {
                JInvokeStmt u0 = (JInvokeStmt) u;
//                System.out.println(u0);
                u0.getInvokeExpr().getArgs();
                for (ValueBox vb : u0.getUseBoxes()) {
                    Value v = vb.getValue();
//                    System.out.println("\t" + v + "\t" + v.getType());
                }
            }
        }
    }

}