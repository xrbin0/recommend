package com.xrbin.ddpt;

import com.xrbin.ddpt.model.Allocation;
import com.xrbin.ddpt.model.CSAllocation;
import com.xrbin.ddpt.model.DatabaseManager;
import com.xrbin.ddpt.model.Field;
import com.xrbin.utils.StaticData;
import com.xrbin.utils.util;
import jas.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.*;
import soot.jimple.AssignStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.util.Chain;
import soot.util.HashChain;

import java.util.*;

import static com.xrbin.ddpt.utils.MAIN;


public class CallGraph {
    private static final Logger logger = LoggerFactory.getLogger(CallGraph.class);
    protected DatabaseManager database = DatabaseManager.getInstance();

    protected Chain<SootMethod> methodChain = new HashChain<>();
    protected Map<SootMethod, List<Pair<SootMethod, Boolean>>> methodToSuccs = new HashMap<>(); // call: true; return: false;

    protected SootMethod mainMethod;
    protected HashMap<String, Body> bodys;

    protected HashSet<String> cgFlag = new HashSet<>();
    protected HashMap<SootMethod, Vector<Field>> methodToField = new HashMap<>();

    public void buildCG() {
        Queue<String> worklist = new ArrayDeque<>();

        if(utils.CLINIT) bodys.keySet().forEach(key -> { if (bodys.get(key).getMethod().isStaticInitializer()) worklist.offer(key); });

        if(MAIN) {
            worklist.offer(mainMethod.toString());
        }
        else {
            bodys.forEach((k, b) -> {
                if(b.getMethod().getName().equals("main")) {
                    worklist.offer(b.getMethod().toString());
                }
            });
        }

        while (!worklist.isEmpty()) {
            String cur = worklist.poll();
            if (!cgFlag.add(cur)) continue;
            if (!bodys.containsKey(cur)) continue;
            SootMethod curM = bodys.get(cur).getMethod();
            methodToSuccs.computeIfAbsent(curM, k -> new ArrayList<>());

            bodys.get(cur).getUnits().forEach(u -> {
                if((u instanceof AssignStmt && ((AssignStmt) u).getRightOp() instanceof InvokeExpr) ||
                        (u instanceof  InvokeStmt && ((InvokeStmt)u).getInvokeExpr() != null)) {
                    InvokeExpr ie;
                    if(u instanceof AssignStmt) {
                        ie = (InvokeExpr)((AssignStmt) u).getRightOp();
                    }
                    else {
                        ie = ((InvokeStmt) u).getInvokeExpr();
                    }
                    String methodName = cur + "/" + ie.getMethod().getDeclaringClass() + "." + ie.getMethod().getName();
                    methodName = database.mil.get(methodName + "/" + utils.getLineNumber(u));
//                    util.plnP("\tmethodName: " + methodName);
                    if (database.cg.get(methodName) != null) {
                        for (String cm : database.cg.get(methodName)) {
                            util.myprintln("\t" + cm, StaticData.G, utils.WPCFG);
                            if (bodys.containsKey(cm)) {
                                util.myprintln("\t" + cm, StaticData.R, utils.WPCFG);
                                worklist.offer(cm);
                                methodToSuccs.computeIfAbsent(bodys.get(cm).getMethod(), k -> new ArrayList<>());
                                methodToSuccs.get(curM).add(new Pair<>(bodys.get(cm).getMethod(), true));
                                methodToSuccs.get(bodys.get(cm).getMethod()).add(new Pair<>(curM, false));
                            }
                        }
                    }
                }
            });
        }
        System.out.println("CallGraph size = " + cgFlag.size());
    }

    private static boolean createMethodToFieldUseFlag;
    public void createMethodToField() {
        HashSet<SootMethod> color = new HashSet<>();
        Queue<Pair<SootMethod,Vector<Field>>> q = new ArrayDeque<>();
        Stack<SootMethod> s = new Stack<>();
        Main.wpCFG.bodys.forEach((bn,b) -> {
            if(b.getMethod().isStaticInitializer()) {
                s.push(b.getMethod());
            }
        });
        while (!s.empty()) {
            SootMethod sm = s.pop();
            if(color.add(sm)) {
                methodToField.computeIfAbsent(sm, k -> new Vector<>());
                createMethodToFieldUseFlag = true;
                methodToSuccs.get(sm).forEach(p -> {
                    if(p.getO2() && !color.contains(p.getO1())) {
                        s.push(p.getO1());
                        createMethodToFieldUseFlag = false;
                    }
                });
                if(createMethodToFieldUseFlag) {
                    q.offer(new Pair<>(sm, new Vector<>()));
                }
            }
        }
//        q.forEach(System.out::println);

        color.clear();
        while (!q.isEmpty()) {
            Pair<SootMethod, Vector<Field>> sv = q.poll();
            SootMethod sm = sv.getO1();
            methodToField.computeIfAbsent(sm, k -> new Vector<>());
            boolean flag = false;
            Vector<Field> toPred = new Vector<>();
            if(color.add(sm)) {
                dfsFindMethodField(sm);
                toPred.addAll(methodToField.get(sm));
                flag = true;
            }
            int size = methodToField.get(sm).size();
            methodToField.get(sm).addAll(sv.getO2());
            if(size < methodToField.get(sm).size()) {
                flag = true;
            }
            if(flag) {
                for(Pair<SootMethod, Boolean> p: methodToSuccs.get(sm)) {
                    if(!p.getO2()) {
                        q.offer(new Pair<>(p.getO1(), toPred));
                    }
                }
            }
        }

        methodToField.forEach((m, fs) -> {
//            if(database.appMethod.contains(m.toString())) {
//                methodToField.get(m).forEach(System.out::println);
//            }
            // util.writeFilelnWithPrefix(m.toString() + "\t" + fs.size(), "methodFieldSize");
        });
    }

    private void dfsFindMethodField(SootMethod sm) {
        bodys.get(sm.toString()).getUnits().forEach(u -> {
            InstanceFieldRef ifr = null;
            if(u instanceof AssignStmt && ((AssignStmt) u).getLeftOp() instanceof InstanceFieldRef) {
                ifr = (InstanceFieldRef) ((AssignStmt) u).getLeftOp();
            }
            else if(u instanceof AssignStmt && ((AssignStmt) u).getRightOp() instanceof InstanceFieldRef) {
                ifr = (InstanceFieldRef) ((AssignStmt) u).getRightOp();
            }
            if(ifr != null) {
                String var = Main.wpCFG.getMethodOf(u).toString() + "/" + ifr.getBase().toString();
                if (database.vptInsen.containsKey(var)) {
                    for (CSAllocation o : database.vptInsen.get(var)) {
                        Field f = new Field(o, ifr.getField().toString());
                        methodToField.get(sm).add(f);
                    }
//                    if(database.appMethod.contains(sm.toString())) {
//                        methodToField.get(sm).forEach(System.out::println);
//                    }
                }
            }
        });
    }

    public SootMethod getMainMethod() {
        return mainMethod;
    }

    public HashMap<String, Body> getBodys() {
        return bodys;
    }

    public CallGraph(HashMap<String, Body> bodys, String mainMethod) {
        this.bodys = bodys;
        if (MAIN) {
            try {
                this.mainMethod = bodys.get(mainMethod).getMethod();
            } catch (Exception e) {
                System.err.println(e.toString());
            }
        }
    }

    protected void addEdge(Map<SootMethod, List<SootMethod>> unitToSuccs, Map<SootMethod, List<SootMethod>> unitToPreds, SootMethod head, SootMethod tail) {
        List<SootMethod> headsSuccs = unitToSuccs.computeIfAbsent(head, k -> new ArrayList<SootMethod>(3));
        // We expect this list to
        // remain short.
        if (!headsSuccs.contains(tail)) {
            headsSuccs.add(tail);
            List<SootMethod> tailsPreds = unitToPreds.computeIfAbsent(tail, k -> new ArrayList<SootMethod>());
            tailsPreds.add(head);
        }
    }
    
    public Chain<SootMethod> getMethods() {
        return methodChain;
    }

    public List<Pair<SootMethod, Boolean>> getSuccsOf(SootMethod u) {
        List<Pair<SootMethod, Boolean>> l = methodToSuccs.get(u);
        if (l == null) {
            return Collections.emptyList();
        }
        return l;
    }

    protected void copyList(Map<SootMethod, List<SootMethod>> map, SootMethod key, List<SootMethod> from) {
        if(!map.containsKey(key)) {
            map.put(key, new ArrayList<>());
        }

        for(SootMethod u : from) {
            map.get(key).add(u);
        }
    }

    protected void copyList(Map<SootMethod, List<SootMethod>> map, SootMethod key, SootMethod from) {
        if(!map.containsKey(key)) {
            map.put(key, new ArrayList<>());
        }
        map.get(key).add(from);
    }

    public int size() {
        return methodChain.size();
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (SootMethod u : methodChain) {
            buf.append("// succs ").append(getSuccsOf(u)).append('\n');
        }
        return buf.toString();
    }

    private void printCG(){
        for(SootMethod u : methodChain) {
            util.plnG("\n" + u.toString());
            for(Pair<SootMethod, Boolean> succ : getSuccsOf(u)) {
                util.plnY("\t" + succ.getO1().toString());
            }
        }
    }

}