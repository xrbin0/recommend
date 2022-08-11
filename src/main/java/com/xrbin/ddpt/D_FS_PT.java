package com.xrbin.ddpt;

import com.xrbin.ddpt.model.*;
import com.xrbin.utils.StaticData;
import com.xrbin.utils.Statistics;
import com.xrbin.utils.util;
import jas.Pair;
import soot.*;
import soot.jimple.*;
import soot.jimple.internal.*;
import soot.shimple.internal.SPhiExpr;

import java.util.*;

public class D_FS_PT {
    public LocatePointer query;
    public Vector<LocatePointer> queries;

    private Statistics stat = new Statistics();
    private final WholeProgramCFG wpCFG;
    private final IntroValueFlowGraph ivfg;

    private final static DatabaseManager database = DatabaseManager.getInstance();
//    private static final Logger logger = LoggerFactory.getLogger(D_FS_PT.class);

    public D_FS_PT(WholeProgramCFG c, IntroValueFlowGraph ivfg) {
        wpCFG = c;
        this.ivfg = ivfg;
    }

    public HashSet<CSAllocation> query(LocatePointer lv) {
        if(utils.TEST2FILE) {
            util.writeFilelnWithPrefix("\n-------- query lv: " + lv, "TestForTest");
        }
        else {
            System.out.println("\n-------- query lv: " + lv);
        }
        HashSet<CSAllocation> res = new HashSet<>();
        if (lv == null) return res;
        this.query = lv;
        analysis();
        if(utils.RESULT && utils.modle == utils.DEBUG) {
            outVPT();
        }
        queries.forEach(q -> res.addAll(vptGet(q)));
        return res;
    }

    private HashMap<LocatePointer, HashSet<CSAllocation>> vpt = new HashMap<>();
    private HashMap<Allocation, Vector<CSAllocation>> insenAlloToSenAllo = new HashMap<>();
    private HashMap<Unit, HashMap<VFGvalue, LocatePointer>> locatePointers = new HashMap<>();
    private HashMap<Pair<Unit, Pair<ObjContext, VFGvalue>>, LocatePointer> staticFieldLPs = new HashMap<>();
    private HashMap<Pair<Unit, Pair<ObjContext, VFGvalue>>, LocatePointer> csLocatePointers = new HashMap<>();

    // 初步控制worklist的求解顺序
    private final Comparator<LocatePointer> comparator = new Comparator<LocatePointer>() {
        @Override
        public int compare(LocatePointer o1, LocatePointer o2) {
            return lvWeight.get(o2) - lvWeight.get(o1);
        }
    };
    private Map<LocatePointer, Integer> lvWeight = new HashMap<>();
    private PriorityQueue<LocatePointer> worklist = new PriorityQueue<>(comparator);
    private Map<Unit, HashSet<LocatePointer>> unitToLocatePointer = new HashMap<>();

    private void init() {
        stat = new Statistics();
        worklist = new PriorityQueue<>(comparator); // bugbug: worklist, init with comparator
        unitToLocatePointer = new HashMap<>();
        locatePointers = new HashMap<>();
        csLocatePointers = new HashMap<>();
        vpt = new HashMap<>();
        lvWeight = new HashMap<>();
        insenAlloToSenAllo = new HashMap<>();
        insenAlloToSenAllo.put(Allocation.null_pseudo_heap, new Vector<>());
        staticFieldLPs = new HashMap<>();
    }

//    LocatePointer cur = null;
    private void analysis() {
        init();
        stat.point("--begin--");

        if (!backAnalysis()) {
            if(utils.TEST2FILE) {
                util.writeFilelnWithPrefix("------------------- worklist.size() = " + worklist.size(), "TestForTest");
                util.writeFilelnWithPrefix("------------------- overSizeCount   = " + overSizeCount,   "TestForTest");
                util.writeFilelnWithPrefix("----analysis----" + query, "TestForTest" );
            }
            else {
                System.out.print("worklist.size() = " + worklist.size());
                System.out.println(", overSizeCount = " + overSizeCount);
                System.out.println("----analysis----" + StaticData.G + query + StaticData.E);
            }
            return;
        }
        else if(utils.STAT) {
            if(utils.TEST2FILE) {
                util.writeFilelnWithPrefix("------------------- worklist.size() = " + worklist.size(), "TestForTest");
                util.writeFilelnWithPrefix("------------------- overSizeCount   = " + overSizeCount,   "TestForTest");
            }
            else {
                System.out.print("worklist.size() = " + worklist.size());
                System.out.println(", overSizeCount = " + overSizeCount);
            }
//            return;
        }
        stat.point( "--backward analysis--");

        if(!utils.BUILDVFGACCURATE) {
            flowSenPartOfVFG();
            stat.point( "--refine vfg--");
        }

        while (!worklist.isEmpty()) {
            LocatePointer cur = worklist.poll();
            Unit curUnit = cur.getU();
            VFGvalue curValue = cur.getV();
            ObjContext curCtx = cur.getCtx();

//            logger.info(cur);
            util.myprintln("\n---------------cur---------------" + cur, utils.ANALYZE);

            if (curUnit instanceof JAssignStmt) {
                JAssignStmt assignStmt = (JAssignStmt) curUnit;
                Value left = assignStmt.getLeftOp();
                Value right = assignStmt.getRightOp();

                if (left != null && left.equals(right)) {
                    System.out.println("Just for fun.");
                }
                else if (left instanceof JimpleLocal && right instanceof JimpleLocal) { // Assign
                    for (Unit pred : ivfg.getPreds(curUnit)) {
                        for (VFGvalue vv : ivfg.getEdgeValue(pred, curUnit)) {
                            LocatePointer lpPred = getCsLocatePointer(pred, vv, curCtx);
                            addToVpt(cur, lpPred);
                        }
                    }
                }
                else if (left instanceof JimpleLocal && right instanceof StaticFieldRef) { // Assign
                    for (Unit pred : ivfg.getPreds(curUnit)) {
                        for (VFGvalue vv : ivfg.getEdgeValue(pred, curUnit)) {
                            LocatePointer lpPred = staticFieldLPs.get(new Pair<>(pred, new Pair<>(ObjContext.allContext, vv)));
                            addToVpt(cur, lpPred);
                        }
                    }
                }
                else if (left instanceof JimpleLocal && right instanceof JCastExpr) { // Assign
                    for (Unit pred : ivfg.getPreds(curUnit)) {
                        for (VFGvalue vv : ivfg.getEdgeValue(pred, curUnit)) {
                            LocatePointer lpPred = getCsLocatePointer(pred, vv, cur.getCtx());
//                            vptGet(lpPred).forEach(csa -> {
//                                if (Allocation.equalsType(csa.getO().getClassType(), ((JCastExpr) right).getCastType().toString())) { // is it? 这里不太对，子类型应该是要处理一下的
//                                    addToVpt(cur, csa);
//                                }
//                            });
                            addToVpt(cur, lpPred);
                        }
                    }
                }
                else if (left instanceof JimpleLocal && right instanceof InstanceFieldRef) { // Load: p = q.f

                    Vector<CSAllocation> objectsOfQ_f = new Vector<>();

                    // HidenMethodField, look for from doop
//                    ivfg.getPreds(curU).forEach(pred ->
//                            ivfg.getEdgeValue(pred, curU).forEach(vv -> {
//                                if (ivfg.isHidenMethodField(new LocatePointer(pred, vv, wpCFG.getMethodOf(pred).toString()))
//                                        && DatabaseManager.getInstance().instanceFieldVPT.get(vv) != null) {
//                                    util.myprintln("\t\t--biuld vfg son--1--" + cur, StaticData.B, utils.HIDENMETHOD);
//                                    util.myprintln("\t\t\t--biuld vfg son--2--" + new LocatePointer(pred, vv, wpCFG.getMethodOf(pred).toString()), StaticData.B, utils.HIDENMETHOD);
//                                    DatabaseManager.getInstance().instanceFieldVPT.get(vv).forEach(allocation -> {
//                                        if (insenAlloToSenAllo.containsKey(allocation)) {
//                                            objectsOfQ_f.addAll(insenAlloToSenAllo.get(allocation));
//                                        }
//                                        else {
//                                            objectsOfQ_f.add(new CSAllocation(allocation)); // is it?
//                                        }
//                                    });
//                                }
//                            })
//                    );

                    // all objects of q

                        Vector<CSAllocation> objectsOfQ = new Vector<>();
                        ivfg.getPreds(curUnit).forEach(pred ->
                                ivfg.getEdgeValue(pred, curUnit).forEach(vv -> {
                                    if (vv.isLocal()) { // can't delete
                                        if (vv.getValue().equals(((InstanceFieldRef) right).getBase())) {
                                            LocatePointer lp = getCsLocatePointer(pred, ((InstanceFieldRef) right).getBase(), curCtx);
                                            if (vptGet(lp) != null) {
                                                objectsOfQ.addAll(vptGet(lp));
                                            }
                                        }
                                    }
                                })
                        );


//                    for (CSAllocation o : objectsOfQ) {
//                        System.out.println("-----------objectsOfQ: " + o);
//                    }

                    // all objects of q.f
                    // 每次都要全部检查一遍，效率很低
                    for (CSAllocation o : objectsOfQ) {
                        if (utils.isConstantAllocation(o.getO().toString())) continue;
                        VFGvalue v = new VFGvalue(new Field(o, ((InstanceFieldRef) right).getField().toString()));
                        ivfg.getPreds(curUnit).forEach(pred ->
                                ivfg.getEdgeValue(pred, curUnit).forEach(vv -> {
                                    if (vv.isField()) {
                                        if (o.getO().equals(((Field) vv.getValue()).getO().getO()) &&
                                                ((InstanceFieldRef) right).getField().toString().equals(((Field) vv.getValue()).getField())) {
                                            Vector<LocatePointer> lps = getCsLocatePointer(pred, v);
                                            lps.forEach(lp -> { // bugbug
                                                if (VFGvalue.equal(lp.getV(), v)) {
//                                                    util.plnBG("\t" + lp);
                                                    objectsOfQ_f.addAll(vptGet(lp));
                                                }
                                            });
                                        }
                                    }
                                })
                        );
                    }

//                    for (CSAllocation o : objectsOfQ_f) {
//                        System.out.println("-----------objectsOfQ_f: " + o);
//                    }

//                    for (CSAllocation a : objectsOfQ_f) {
//                        util.plnG(a.toString());
//                    }

                    addToVpt(cur, objectsOfQ_f);
                }
                else if (left instanceof JimpleLocal && right instanceof JNewExpr) { // Alloc
//                    addToVpt(cur, makeAlloc(u)); // done while back Analysis
                    // 调整优先级队列里面的lp顺序
                    worklistAddSucc(cur);
                }
                else if (left instanceof JimpleLocal && right instanceof SPhiExpr) { // Phi func
                    // 这里不需要处理所有的变量，其实在值流图那边就已经处理好了。
                    ivfg.getPreds(curUnit).forEach(pred ->
                            ivfg.getEdgeValue(pred, curUnit).forEach(vv -> {
                                LocatePointer lpPred = getCsLocatePointer(pred, vv, curCtx);
                                addToVpt(cur, lpPred);
                            })
                    );
                }
                else if (left instanceof JimpleLocal && right instanceof InvokeExpr) {
                    if(utils.isHardMethod(((InvokeExpr) right).getMethod())) {
                        String var = wpCFG.getMethodOf(curUnit) + "/" + left.toString();
//                        System.out.println("isHardMethod: " + var);
                        Variable v = new Variable(curCtx, var);
                        if (database.objCtxVpt.containsKey(v)) {
                            addToVpt(cur, database.objCtxVpt.get(v));
//                            util.writeFilelnWithPrefix(curUnit.toString(), "hidenAppMethodCall");
                        }
                    }
                    else {
                        for (Unit pred : ivfg.getPreds(curUnit)) {
                            for (VFGvalue vv : ivfg.getEdgeValue(pred, curUnit)) {
                                // base and args
                                if (VFGvalue.equal(vv, curValue)) {
                                    Value leftPred = getLV(pred, vv);
                                    LocatePointer lv = getCsLocatePointer(pred, leftPred, curCtx);
                                    addToVpt(cur, lv);
                                    // break;
                                }
                                // ret
                                if (left.equals(curValue.getValue()) && pred instanceof JReturnStmt &&
                                        ((JReturnStmt) pred).getOp().equals(vv.getValue())) {
                                    Value retValue = getLV(pred, vv);
                                    InvokeExpr ie = (InvokeExpr) ((AssignStmt) curUnit).getRightOp();

                                    if (ie.getMethod().getReturnType().toString().contains("java.lang.StringBuilder")) {
                                        addToVpt(cur, CSAllocation.string_builder);
                                    }
                                    else if (ie.getMethod().getReturnType().toString().contains("java.lang.StringBuffer")) {
                                        addToVpt(cur, CSAllocation.string_buffer);
                                    }
                                    else if (ie.getMethod().getReturnType().toString().contains("java.lang.StringBuilder")) {
                                        addToVpt(cur, CSAllocation.string_builder);
                                    }
                                    else if (((ReturnStmt)pred).getOp() instanceof StringConstant) {
                                        addToVpt(cur, CSAllocation.string_constant);
                                    }
                                    else if (ie instanceof JVirtualInvokeExpr
                                            || ie instanceof JSpecialInvokeExpr
                                            || ie instanceof JInterfaceInvokeExpr
                                    ) {
                                        if (vpt.containsKey(getCsLocatePointer(curUnit, ((AbstractInstanceInvokeExpr) ie).getBase(), curCtx))) {
                                            HashSet<CSAllocation> csas = vptGet(getCsLocatePointer(curUnit, ((AbstractInstanceInvokeExpr) ie).getBase(), curCtx));
                                            csas.forEach(csa -> {
                                                LocatePointer lpp = getCsLocatePointer(pred, retValue, new ObjContext(csa.getO(), csa.getCxt().getO1()));
//                                                util.plnP("left instanceof JimpleLocal && right instanceof InvokeExpr: " + lpp.toString());
                                                addToVpt(cur, lpp);
                                            });
                                        }
                                    }
                                    else if (ie instanceof JStaticInvokeExpr) {
                                        LocatePointer lpp = getCsLocatePointer(pred, retValue, curCtx);
                                        addToVpt(cur, lpp);
                                    }
                                    else if (ie instanceof JDynamicInvokeExpr) {
                                        // TODO
                                    }
                                    else {

                                    }
//                                break; // the break is wrong, not only one return flow to (a = b.f, a)
                                }
                            }
                        }
                    }
                }
                else if (left instanceof JimpleLocal && right instanceof ArrayRef) { // Assign
                    for (Unit pred : ivfg.getPreds(curUnit)) {
                        for (VFGvalue vv : ivfg.getEdgeValue(pred, curUnit)) {
                            LocatePointer lpPred = getCsLocatePointer(pred, vv, curCtx);
                            addToVpt(cur, lpPred);
                        }
                    }
                }
                else if (left instanceof JimpleLocal && right instanceof NewArrayExpr) { // Assign
                    if(left.equals(right)) {
                        System.out.println("Just for fun.");
                    }
                }
                else if (left instanceof JimpleLocal && right instanceof JNewMultiArrayExpr) { // Assign
                    if(left.equals(right)) {
                        System.out.println("Just for fun.");
                    }
                }
                else if (left instanceof JimpleLocal) {
                    if (right instanceof StringConstant) {
                        addToVpt(cur, CSAllocation.string_constant);
                    }
                    // not appear
                    else if (right instanceof ClassConstant) {
                        addToVpt(cur, new CSAllocation(new Allocation(((ClassConstant) right).getValue())));
                    }
                    else if (right instanceof NumericConstant) {

                    }
                    else if (right instanceof NullConstant) {
                        addToVpt(cur, CSAllocation.null_pseudo_heap);
                    }
                    else {
                        util.writeFileln("", "logs/moreJimpleStmt");
                        util.writeFileln(curUnit.getClass() + "\t" + curUnit.toString(), "logs/moreJimpleStmt");
                        util.writeFileln("\t" + ((JAssignStmt) curUnit).getRightOp().getClass() + "\t" + ((JAssignStmt) curUnit).getRightOp().toString(), "logs/moreJimpleStmt");
                        util.writeFileln("\t" + ((JAssignStmt) curUnit).getRightOp().getType() + "\t" + ((JAssignStmt) curUnit).getRightOp().toString(), "logs/moreJimpleStmt");
                        util.writeFileln("\t" + ((JAssignStmt) curUnit).getLeftOp().getClass() + "\t" + ((JAssignStmt) curUnit).getLeftOp().toString(), "logs/moreJimpleStmt");
                        util.writeFileln("\t" + ((JAssignStmt) curUnit).getRightOp().getType() + "\t" + ((JAssignStmt) curUnit).getRightOp().toString(), "logs/moreJimpleStmt");
                    }
                }
                else if (left instanceof InstanceFieldRef && right instanceof JimpleLocal) { // Store

//                    if(curV.isField()) {
//
//                    }
//                    else if(curV.isLocal()) {
//
//                    }
//                    else {
//
//                    }

                    if(ivfg.isHidenMethodField(cur)) {
                        util.myprintln("\t--biuld vfg son--3--" + cur, StaticData.B, utils.HIDENMETHOD);
//                        if(hidenFieldGetFlag.add(cur)) {
//                            System.out.println("curV = " + curV);
//                        if(database.isenVpt.containsKey(wpCFG.getMethodOf(curUnit).toString() + "/" + right.toString())) {
//                            database.isenVpt.get(wpCFG.getMethodOf(curUnit).toString() + "/" + right.toString()).forEach(
//                                    allocation -> {
//                                        if (insenAlloToSenAllo.containsKey(allocation)) {
//                                            addToVpt(cur, insenAlloToSenAllo.get(allocation));
////                                                vptGet(cur).addAll(insenAlloToSenAllo.get(allocation));
//                                        }
//                                        else {
//                                            addToVpt(cur, new CSAllocation(allocation));
////                                                vptGet(cur).add(new CSAllocation(allocation)); // is it?
//                                        }
//                                    }
//                            );
//                            worklistAddSucc(cur);
//                        }

//                        System.out.println("isHardMethod: " + var);
                        String var = wpCFG.getMethodOf(curUnit) + "/" + right.toString();
                        Variable v = new Variable(curCtx, var);
                        if(database.objCtxVpt.containsKey(v)) {
                            addToVpt(cur, database.objCtxVpt.get(v));
                            worklistAddSucc(cur);
                        }
//                        if (DatabaseManager.getInstance().instanceFieldVPT.get(new VFGvalue(new Field(
//                                new CSAllocation(((Field) curV.getValue()).getO().getO()), ((Field) curV.getValue()).getField()
//                        ))) != null) {
//                            DatabaseManager.getInstance().instanceFieldVPT.get(new VFGvalue(new Field(
//                                    new CSAllocation(((Field) curV.getValue()).getO().getO()), ((Field) curV.getValue()).getField()
//                            ))).forEach(
//                                    allocation -> {
//                                        if (insenAlloToSenAllo.containsKey(allocation)) {
//                                            addToVpt(cur, insenAlloToSenAllo.get(allocation));
////                                                vptGet(cur).addAll(insenAlloToSenAllo.get(allocation));
//                                        }
//                                        else {
//                                            addToVpt(cur, new CSAllocation(allocation));
////                                                vptGet(cur).add(new CSAllocation(allocation)); // is it?
//                                        }
//                                    }
//                            );
//                            worklistAddSucc(cur);
//                        }
//                        }
                        continue;
                    }

                    // p = q; --p--> b.f = p;
                    // b = q; --b--> b.f = p;
                    LocatePointer base = null;
                    LocatePointer from = null;
                    for (Unit pred : ivfg.getPreds(curUnit)) {
                        for (VFGvalue vv : ivfg.getEdgeValue(pred, curUnit)) {
                            if (vv.isLocal()) {
                                if (vv.getValue().equals(((InstanceFieldRef) left).getBase())) {
                                    base = getCsLocatePointer(pred, vv, curCtx);
                                }
                                else if (vv.getValue().equals(right)) {
                                    from = getCsLocatePointer(pred, vv, curCtx);
                                }
                            }
                        }
                    }

                    if(base != null && from != null) {
                        for (CSAllocation o : vptGet(base)) {
                            if (utils.isConstantAllocation(o.getO().toString())) continue;
//                            Vector<LocatePointer> lvs = getCsLocatePointer(curU, new VFGvalue(new Field(o, (((InstanceFieldRef) left).getField().toString()))));
//                            for (LocatePointer lv : lvs) {
//                                addToVpt(lv, from);
//                            }
                            addToVpt(getCsLocatePointer(curUnit, new VFGvalue(new Field(o, (((InstanceFieldRef) left).getField().toString()))), curCtx), from);
                        }
                    }


                    // a.f = q; --o.f--> b.f = p;
//                    if (curValue.getValue().equals(((InstanceFieldRef) left).getBase())) {
//                        for (Unit pred : ivfg.getPreds(curUnit)) {
//                            for (VFGvalue vv : ivfg.getEdgeValue(pred, curUnit)) {
//                                if (vv.isField()) {
////                                if(((Field)(vv.getValue())).)
//                                    if (vpt.containsKey(base) && vptGet(base).size() == 1 && vptGet(base).contains(((Field) (vv.getValue())).getO())) {
//                                        // a.f = q; o.f
//                                        // b.f = p;
//                                        // if (b --only--> o) strong update
//                                        // if (b -> objects) transmit pts of o.f
//                                    }
////                                else if(vpt.containsKey(base) && vptGet(base).size() == 1 && !vptGet(base).contains(((Field)(vv.getValue())).getO())) {
//                                    else if (vpt.containsKey(base) && vptGet(base).size() >= 1) {
//                                        // Combine two cases
//                                        Vector<LocatePointer> lvs = getCsLocatePointer(pred, vv);
//
//                                        if (lvs.size() == 1) {
//                                            LocatePointer lpPred = lvs.get(0);
//                                            if (cur.getV().isField() && vpt.containsKey(cur) && vpt.containsKey(lpPred)) {
//                                                addToVpt(cur, lpPred);
//                                            }
//                                        }
//                                        else {
//                                            // System.exit(-1);
//                                        }
//                                    }
//                                    // else if (|pts of base| == 0)
//                                }
//                            }
//                        }
//                    }

                }
                else if (left instanceof InstanceFieldRef && right instanceof Constant) { // Store
                    if (ivfg.isHidenMethodField(cur)) {
                        util.myprintln("\t\t--biuld vfg son--4--" + cur, StaticData.B, utils.HIDENMETHOD);
                        addToVpt(cur, CSAllocation.string_constant);
                        continue;
                    }

                    // b = q; --b--> b.f = p;
                    LocatePointer base = null;
                    for (Unit pred : ivfg.getPreds(curUnit)) {
                        for (VFGvalue vv : ivfg.getEdgeValue(pred, curUnit)) {
                            if (vv.isLocal()) {
                                if (vv.getValue().equals(((InstanceFieldRef) left).getBase())) {
                                    base = getCsLocatePointer(pred, vv, curCtx);
                                }
                            }
                        }
                    }

                    if (base != null) {
                        if (right instanceof StringConstant) {
                            for (CSAllocation o : vptGet(base)) {
                                if (utils.isConstantAllocation(o.getO().toString())) continue;
                                getCsLocatePointer(curUnit, new VFGvalue(new Field(o, (((InstanceFieldRef) left).getField().toString())))).forEach(
                                        lv -> addToVpt(lv, CSAllocation.string_constant)
                                );
                            }
                        }
                        else if (right instanceof ClassConstant) {
                            for (CSAllocation o : vptGet(base)) {
                                if (utils.isConstantAllocation(o.getO().toString())) continue;
                                getCsLocatePointer(curUnit, new VFGvalue(new Field(o, (((InstanceFieldRef) left).getField().toString())))).forEach(
                                        lv -> addToVpt(lv, new CSAllocation(new Allocation(((ClassConstant) right).getValue())))
                                );
                            }
                        }
                        else if (right instanceof NullConstant) {
                            for (CSAllocation o : vptGet(base)) {
                                if (utils.isConstantAllocation(o.getO().toString())) continue;
                                getCsLocatePointer(curUnit, new VFGvalue(new Field(o, (((InstanceFieldRef) left).getField().toString())))).forEach(
                                        lv -> addToVpt(lv, CSAllocation.null_pseudo_heap)
                                );
                            }
                        }
                        else if (right instanceof NumericConstant) {

                        }
                        else {
                            util.writeFileln("", "logs/moreJimpleStmt");
                            util.writeFileln(curUnit.getClass() + "\t" + curUnit.toString(), "logs/moreJimpleStmt");
                            util.writeFileln("\t" + ((JAssignStmt) curUnit).getRightOp().getClass() + "\t" + ((JAssignStmt) curUnit).getRightOp().toString(), "logs/moreJimpleStmt");
                            util.writeFileln("\t" + ((JAssignStmt) curUnit).getRightOp().getType() + "\t" + ((JAssignStmt) curUnit).getRightOp().toString(), "logs/moreJimpleStmt");
                            util.writeFileln("\t" + ((JAssignStmt) curUnit).getLeftOp().getClass() + "\t" + ((JAssignStmt) curUnit).getLeftOp().toString(), "logs/moreJimpleStmt");
                            util.writeFileln("\t" + ((JAssignStmt) curUnit).getRightOp().getType() + "\t" + ((JAssignStmt) curUnit).getRightOp().toString(), "logs/moreJimpleStmt");
                        }
                    }
                }
                else if (left instanceof StaticFieldRef && right instanceof JimpleLocal) { // static Assign

                    if (ivfg.isHidenMethodField(cur)) {
                        util.myprintln("\t\t--biuld vfg son--5--" + cur, StaticData.B, utils.HIDENMETHOD);
                        if (!utils.isPrimitiveType(left.getType()) && DatabaseManager.getInstance().staticVpt.get((curValue.getValue()).toString()) != null) {
                            addToVpt(cur, DatabaseManager.getInstance().staticVpt.get((curValue.getValue()).toString()));
                            worklistAddSucc(cur);
                        }
                        continue;
                    }

                    for (Unit pred : ivfg.getPreds(curUnit)) {
                        for (VFGvalue vv : ivfg.getEdgeValue(pred, curUnit)) {
//                            LocatePointer lpPred = getCsLocatePointer(pred, vv, ObjContext.allContext);
                            getCsLocatePointer(pred, vv).forEach(lpPred -> addToVpt(cur, lpPred));
                        }
                    }
                }
                else if (left instanceof StaticFieldRef && right instanceof Constant) {
                    if(ivfg.isHidenMethodField(cur)) {
                        util.myprintln("\t\t--biuld vfg son--6--" + cur, StaticData.B, utils.HIDENMETHOD);
                        addToVpt(cur, CSAllocation.string_constant);
                        worklistAddSucc(cur);
                        continue;
                    }
                    if (right instanceof StringConstant) {
                        addToVpt(cur, CSAllocation.string_constant);
                    }
                    else if (right instanceof ClassConstant) {
                        addToVpt(cur, new CSAllocation(new Allocation(((ClassConstant) right).getValue())));
                    }
                    else if (right instanceof NullConstant) {
                        addToVpt(cur, CSAllocation.null_pseudo_heap);
                    }
                    else if (right instanceof NumericConstant) {

                    }
                    else {
                        util.writeFileln("", "logs/moreJimpleStmt");
                        util.writeFileln(curUnit.getClass() + "\t" + curUnit.toString(), "logs/moreJimpleStmt");
                        util.writeFileln("\t" + ((JAssignStmt) curUnit).getRightOp().getClass() + "\t" + ((JAssignStmt) curUnit).getRightOp().toString(), "logs/moreJimpleStmt");
                        util.writeFileln("\t" + ((JAssignStmt) curUnit).getRightOp().getType() + "\t" + ((JAssignStmt) curUnit).getRightOp().toString(), "logs/moreJimpleStmt");
                        util.writeFileln("\t" + ((JAssignStmt) curUnit).getLeftOp().getClass() + "\t" + ((JAssignStmt) curUnit).getLeftOp().toString(), "logs/moreJimpleStmt");
                        util.writeFileln("\t" + ((JAssignStmt) curUnit).getRightOp().getType() + "\t" + ((JAssignStmt) curUnit).getRightOp().toString(), "logs/moreJimpleStmt");
                    }
                }
                else if (left instanceof ArrayRef && right instanceof JimpleLocal) {
                    for (Unit pred : ivfg.getPreds(curUnit)) {
                        for (VFGvalue vv : ivfg.getEdgeValue(pred, curUnit)) {
                            LocatePointer lpPred = getCsLocatePointer(pred, vv, curCtx);
                            addToVpt(cur, lpPred);
                        }
                    }
                }
                else if (left instanceof ArrayRef && right instanceof Constant) {
                    if (right instanceof StringConstant) {
                        addToVpt(cur, CSAllocation.string_constant);
                    }
                    else if (right instanceof ClassConstant) {
                        addToVpt(cur, new CSAllocation(new Allocation(((ClassConstant) right).getValue())));
                    }
                    else if (right instanceof NullConstant) {
                        addToVpt(cur, CSAllocation.null_pseudo_heap);
                    }
                    else if (right instanceof NumericConstant) {

                    }
                    else {
                        util.writeFileln("", "logs/moreJimpleStmt");
                        util.writeFileln(curUnit.getClass() + "\t" + curUnit.toString(), "logs/moreJimpleStmt");
                        util.writeFileln("\t" + ((JAssignStmt) curUnit).getRightOp().getClass() + "\t" + ((JAssignStmt) curUnit).getRightOp().toString(), "logs/moreJimpleStmt");
                        util.writeFileln("\t" + ((JAssignStmt) curUnit).getRightOp().getType() + "\t" + ((JAssignStmt) curUnit).getRightOp().toString(), "logs/moreJimpleStmt");
                        util.writeFileln("\t" + ((JAssignStmt) curUnit).getLeftOp().getClass() + "\t" + ((JAssignStmt) curUnit).getLeftOp().toString(), "logs/moreJimpleStmt");
                        util.writeFileln("\t" + ((JAssignStmt) curUnit).getRightOp().getType() + "\t" + ((JAssignStmt) curUnit).getRightOp().toString(), "logs/moreJimpleStmt");
                    }
                }
                else {
                    util.writeFileln("", "logs/moreJimpleStmt");
                    util.writeFileln(curUnit.getClass() + "\t" + curUnit.toString(), "logs/moreJimpleStmt");
                    util.writeFileln("\t" + ((JAssignStmt) curUnit).getRightOp().getClass() + "\t" + ((JAssignStmt) curUnit).getRightOp().toString(), "logs/moreJimpleStmt");
                    util.writeFileln("\t" + ((JAssignStmt) curUnit).getRightOp().getType() + "\t" + ((JAssignStmt) curUnit).getRightOp().toString(), "logs/moreJimpleStmt");
                    util.writeFileln("\t" + ((JAssignStmt) curUnit).getLeftOp().getClass() + "\t" + ((JAssignStmt) curUnit).getLeftOp().toString(), "logs/moreJimpleStmt");
                    util.writeFileln("\t" + ((JAssignStmt) curUnit).getLeftOp().getType() + "\t" + ((JAssignStmt) curUnit).getLeftOp().toString(), "logs/moreJimpleStmt");
                }
            }
            else if (curUnit instanceof JIdentityStmt) {
                for (Unit pred : ivfg.getPreds(curUnit)) {
//                    util.plnP("JIdentityStmt pred: " + pred);
                    if ((pred instanceof AssignStmt && ((AssignStmt) pred).getRightOp() instanceof InvokeExpr)
                            || pred instanceof JInvokeStmt
                    ) {
                        InvokeExpr ie;
                        if (pred instanceof AssignStmt) {
                            ie = (InvokeExpr) ((AssignStmt) pred).getRightOp();
                        }
                        else {
                            ie = ((JInvokeStmt) pred).getInvokeExpr();
                        }
//                        util.plnP("JIdentityStmt pred: " + pred);

                        if (ie instanceof JVirtualInvokeExpr || ie instanceof JSpecialInvokeExpr || ie instanceof JInterfaceInvokeExpr) {
                            getCsLocatePointer(pred, ((AbstractInstanceInvokeExpr) ie).getBase()).forEach(
                                    lp -> vptGet(lp).forEach(
                                            csa -> {
//                                                util.plnP("JIdentityStmt: " + csa.toString());
//                                                util.plnB("JIdentityStmt: " + curCtx);
                                                if (csa.getCxt().getO1().equals(curCtx.getO2()) && csa.getO().equals(curCtx.getO1())) {
                                                    for (VFGvalue vv : ivfg.getEdgeValue(pred, curUnit)) {
                                                        if (vv.isLocal()) { //
//                                                            util.plnP("\tJIdentityStmt: " + csa.toString());
                                                            addToVpt(cur, getCsLocatePointer(pred, vv, lp.getCtx()));
                                                        }
                                                        else if (vv.isConstant()) { //
                                                            addToVpt(cur, CSAllocation.string_constant);
                                                        }
                                                    }
                                                }
                                            }
                                    )
                            );
                        }
                        else if (ie instanceof JStaticInvokeExpr) {
//                            util.plnP("JIdentityStmt: " + ie);
                            for (VFGvalue vv : ivfg.getEdgeValue(pred, curUnit)) {
                                if (vv.isLocal()) {
                                    addToVpt(cur, getCsLocatePointer(pred, vv, curCtx));
                                }
                                else if (vv.isConstant()) {
                                    addToVpt(cur, CSAllocation.string_constant);
                                }
                            }
                        }
                        else if (ie instanceof JDynamicInvokeExpr) {
                            // TODO
                            util.plnP("JDynamicInvokeExpr: " + wpCFG.getMethodOf(curUnit) + " - " + curUnit);
                        }
                        else {
                            util.plnP("?InvokeExpr: " + wpCFG.getMethodOf(curUnit) + " - " + curUnit);
                        }
                    }
                }
            }
            else if (curUnit instanceof JInvokeStmt) {
                for (Unit pred : ivfg.getPreds(curUnit)) {
                    for (VFGvalue vv : ivfg.getEdgeValue(pred, curUnit)) {
                        if(VFGvalue.equal(vv, curValue)) { // bugbug bug
                            Value leftPred = getLV(pred, vv);
                            LocatePointer lpPred = getCsLocatePointer(pred, leftPred, curCtx);
                            addToVpt(cur, lpPred);
                            // break;
                        }
                    }
                }
            }
            else if (curUnit instanceof JReturnVoidStmt) {
                if(curUnit.toString().equals("Happy")) {
                    System.out.println("Happy!");
                }
            }
            else if (curUnit instanceof JReturnStmt) {
                ivfg.getPreds(curUnit).forEach(pred ->
                        ivfg.getEdgeValue(pred, curUnit).forEach(vv -> {
                            LocatePointer lpPred = getCsLocatePointer(pred, vv, curCtx);
                            addToVpt(cur, lpPred);
                        })
                );
            }
            else {
                System.err.println("while(!worklist.isEmpty()): " + curUnit);
            }
        }
        stat.point( "--analysis--");

        // not much need for that
//        if (strongUpdate()) {
//            analysis();
//        }
    }

    private int overSizeCount = 0;
    private final static int overSize = 20000;
    private boolean  backAnalysis() {
        // backward analysis: find all relative LocatePointer
        util.myprintln("||----------------------------------------backAnalysis----------------------------------------||\n", utils.BACKANALYSIS);


        Stack<Pair<LocatePointer, Unit>> s = new Stack<>();
        HashSet<LocatePointer> color = new HashSet<>();

        s.push(new Pair<>(query, null));
        color.add(query);

        // 先获取所有的 上下文敏感的query
        queries = getCsLocatePointer(query.getU(), query.getV());

        // is a NewExpr
        Unit u = query.getU();
        if (u instanceof AssignStmt && ((AssignStmt) u).getRightOp() instanceof NewExpr) {
            queries.forEach(q -> {
                Unit uu = q.getU();
                util.myprintln("\t------new1------backAnalysis: " + wpCFG.getMethodOf(query.getU()) + " ----- " + query, utils.BACKANALYSIS);
                Vector<LocatePointer> lps = getCsLocatePointer(query.getU(), query.getV());
                Allocation key = makeAlloc(uu);
                insenAlloToSenAllo.computeIfAbsent(key, k -> new Vector<>());
//                getNewContext(uu).forEach(ctx -> insenAlloToSenAllo.get(key).add(makeAlloc(uu, ctx)));
                util.myprintln("\t------new2------backAnalysis: " + wpCFG.getMethodOf(query.getU()) + " ----- " + query, utils.BACKANALYSIS);
                for (LocatePointer lp : lps) {
                    CSAllocation allos = makeAlloc(uu, lp.getCtx());
                    addToVpt(lp, allos);
                }
            });
            return true;
        }


        // is a invoke, with <String-builder, String-buffer> return value
        Unit uu = query.getU();
        if(((AssignStmt)uu).getRightOp() instanceof InvokeExpr) {
            InvokeExpr ie = (InvokeExpr) ((AssignStmt) uu).getRightOp();
            if(ie.getMethod().getReturnType().toString().contains("StringBuilder")) {
                queries.forEach(q -> addToVpt(q, CSAllocation.string_builder));
                return true;
            }
            else if(ie.getMethod().getReturnType().toString().contains("StringBuffer")) {
                queries.forEach(q -> addToVpt(q, CSAllocation.string_buffer));
                return true;
            }
            else if(utils.isPrimitiveType(ie.getMethod().getReturnType())) {
                queries.forEach(q -> addToVpt(q, CSAllocation.string_buffer));
                return true;
            }
        }

        queries.forEach(this::worklistAddBackAnalysis);

        // firstly, all allocations
        // 直接逆着值流图dfs(cfl-call-return: 方法 是不走method头部出去)
        while (!s.empty()) {
            Unit curInvokeUnit = s.peek().getO2();
            LocatePointer cur = s.pop().getO1();
            Unit curU = cur.getU();
            util.myprintln("-------new-----backAnalysis: " + wpCFG.getMethodOf(cur.getU()) + " ----- " + cur, utils.BACKANALYSIS);
//            utils.myprintln(wpCFG.getMethodOf(cur.getU()) + ": " + cur.getU() + " --- " + cur.getV().getValue(), utils.BACKANALYSIS);
            for (Unit pred : ivfg.getPreds(cur.getU())) {
                Unit tempU = curInvokeUnit;
                // 2022-03-09 16:34:07, 从一个方法头部出去都是错的，CFL正确的那个已经走过了，DFS不需要再走一次，因此可以跳过。
                if (curInvokeUnit != null
                        && !wpCFG.getMethodOf(curU).toString().equals(wpCFG.getMethodOf(pred).toString())
                        && (pred instanceof InvokeExpr
                        || (pred instanceof JAssignStmt && ((JAssignStmt) pred).getRightOp() instanceof InvokeExpr))
                ) {
                    continue;
                }

                if (pred instanceof ReturnStmt || pred instanceof ReturnVoidStmt) {
                    tempU = curU;
                }
                for (VFGvalue vv : ivfg.getEdgeValue(pred, curU)) {
                    LocatePointer curLp = getLocatePointer(pred, vv);
                    if (color.add(curLp)) {
                        if(vv.isField() || vv.isStaticFieldRef()) {
                            s.push(new Pair<>(curLp, null));
                        }
                        else if(vv.isLocal()) {
                            s.push(new Pair<>(curLp, tempU));
                        }

                        // NewExpr
                        Unit uuu = curLp.getU();
                        if (uuu instanceof AssignStmt && ((AssignStmt) uuu).getRightOp() instanceof NewExpr) {
                            util.myprintln("\t------new1------backAnalysis: " + wpCFG.getMethodOf(curLp.getU()) + " ----- " + curLp, utils.BACKANALYSIS);
                            Vector<LocatePointer> lps = getCsLocatePointer(curLp.getU(), curLp.getV());
                            Allocation key = makeAlloc(uuu);
                            insenAlloToSenAllo.computeIfAbsent(key, k -> new Vector<>());
//                            getNewContext(uuu).forEach(ctx -> insenAlloToSenAllo.get(key).add(makeAlloc(uuu, ctx)));

                            util.myprintln("\t------new2------backAnalysis: " + wpCFG.getMethodOf(curLp.getU()) + " ----- " + curLp, utils.BACKANALYSIS);

                            lps.forEach(lp -> {
                                CSAllocation allos = makeAlloc(uuu, lp.getCtx());
                                insenAlloToSenAllo.get(key).add(allos);
                                addToVpt(lp, allos);
//                                utils.myprintln("\t\t------new------backAnalysis: " + lp, utils.BACKANALYSIS);
                            });
                        }
                    }
                }
            }
            if(color.size() > overSize) {
                overSizeCount++;
                return false;
            }
        }

        // is a invoke
        // 相关解释看下一部分
        Unit uuu = query.getU();
        if(((AssignStmt)uuu).getRightOp() instanceof InvokeExpr) {
            InvokeExpr ie = (InvokeExpr) ((AssignStmt) uuu).getRightOp();
            if (ie instanceof JVirtualInvokeExpr) {
                JVirtualInvokeExpr jvi = (JVirtualInvokeExpr) ie;
                getCsLocatePointer(uuu, jvi.getBase()).forEach(lp -> {
                    util.myprintln("\t-----middle1----backAnalysis: " + wpCFG.getMethodOf(lp.getU()) + ": " + lp, utils.BACKANALYSIS);
                    worklistAddBackAnalysis(lp);
                });
            }
            else if (ie instanceof JSpecialInvokeExpr) {
                JSpecialInvokeExpr jvi = (JSpecialInvokeExpr) ie;
                getCsLocatePointer(uuu, jvi.getBase()).forEach(lp -> {
                    util.myprintln("\t-----middle2----backAnalysis: " + wpCFG.getMethodOf(lp.getU()) + ": " + lp, utils.BACKANALYSIS);
                    worklistAddBackAnalysis(lp);
                });
            }
            else if (ie instanceof JStaticInvokeExpr) {
                // no ThisRef
            }
            else if (ie instanceof JDynamicInvokeExpr) {
                // no ThisRef
            }
            else if (ie instanceof JInterfaceInvokeExpr) {
                JInterfaceInvokeExpr jvi = (JInterfaceInvokeExpr) ie;
                getCsLocatePointer(uuu, jvi.getBase()).forEach(lp -> {
                    util.myprintln("\t-----middle3----backAnalysis: " + wpCFG.getMethodOf(lp.getU()) + ": " + lp, utils.BACKANALYSIS);
                    worklistAddBackAnalysis(lp);
                });
            }
            ie.getArgs().forEach(arg -> {
                        if (!utils.isPrimitiveType(arg.getType())) {
                            getCsLocatePointer(uuu, arg).forEach(lp -> {
                                util.myprintln("\t-----middle4----backAnalysis: " + wpCFG.getMethodOf(lp.getU()) + ": " + lp, utils.BACKANALYSIS);
                                worklistAddBackAnalysis(lp);
                            });
                        }
                    }
            );
        }

        // next, other instructs
        // 对于每一个<l, v>对，都去看它的值流图父节点，并且将其加入到worklist中，应该是要将所有可能对该<l, v>对产生影响的<l, v>对都加入到worklist
        s.push(new Pair<>(query, null));
        color.clear();
        while (!s.empty()) {
            Unit curInvokeUnit = s.peek().getO2();
            LocatePointer cur = s.pop().getO1();
            Unit curU = cur.getU();
//            logger.debug(cur.toString());
//            System.out.println(cur.toString());
            util.myprintln("-----others----backAnalysis: " + cur, utils.BACKANALYSIS);

            // bugbug 2022-04-13 11:25:54
            if ((curU instanceof AssignStmt && ((AssignStmt) curU).getRightOp() instanceof InvokeExpr) || curU instanceof JInvokeStmt) {
                InvokeExpr ie = null;
                if (curU instanceof AssignStmt) {
                    ie = (InvokeExpr) ((AssignStmt) curU).getRightOp();
                }
                else {
                    ie = ((JInvokeStmt) curU).getInvokeExpr();
                }

                util.myprintln("\t-----others--InvokeExpr--backAnalysis: " + ie.getType() + " -- " + ie.getClass(), utils.BACKANALYSIS);

                if (ie instanceof JVirtualInvokeExpr || ie instanceof JSpecialInvokeExpr || ie instanceof JInterfaceInvokeExpr) {
                    AbstractInstanceInvokeExpr jvi = (AbstractInstanceInvokeExpr) ie;
                    if (getCsLocatePointer(curU, jvi.getBase()).size() == 0) {
                        util.myprintln("\t-----others----backAnalysis size() == 0: " + ie, utils.BACKANALYSIS);
                    }
                    getCsLocatePointer(curU, jvi.getBase()).forEach(lp -> {
                        util.myprintln("\t-----others----backAnalysis: " + wpCFG.getMethodOf(lp.getU()) + ": " + lp, utils.BACKANALYSIS);
                        worklistAddBackAnalysis(lp);
                    });
                }
                else if (ie instanceof JStaticInvokeExpr) {
                    // no ThisRef
                }
                else if (ie instanceof JDynamicInvokeExpr) {
                    // no ThisRef
                }
                ie.getArgs().forEach(arg -> {
                            if (!utils.isPrimitiveType(arg.getType())) {
                                getCsLocatePointer(curU, arg).forEach(lp -> {
                                    util.myprintln("\t-----middle3----backAnalysis: " + wpCFG.getMethodOf(lp.getU()) + ": " + lp, utils.BACKANALYSIS);
                                    worklistAddBackAnalysis(lp);
                                });
                            }
                        }
                );
            }

            for (Unit pred : ivfg.getPreds(cur.getU())) {
                Unit tempU = curInvokeUnit;
                // 2022-03-09 16:34:07, 从一个方法头部出去都是错的，CFL正确的那个已经走过了，DFS不需要再走一次，因此可以跳过。
                if (curInvokeUnit != null
                        && !wpCFG.getMethodOf(curU).toString().equals(wpCFG.getMethodOf(pred).toString())
                        && (pred instanceof InvokeExpr
                        || (pred instanceof JAssignStmt && ((JAssignStmt) pred).getRightOp() instanceof InvokeExpr))
                ) {
                    continue;
                }
                // 2022-02-23 16:35:47
//                if (curU instanceof IdentityStmt && (pred instanceof InvokeExpr ||
//                        (pred instanceof JAssignStmt && ((JAssignStmt) pred).getRightOp() instanceof InvokeExpr))) {
//                    if (!pred.equals(curInvokeUnit)) {
//                        //continue;
//                    }
//                    else if (pred.equals(curInvokeUnit)) {
//
//                    }
//                }
                if (pred instanceof ReturnStmt || pred instanceof ReturnVoidStmt) {
                    tempU = curU;
                }

                for (VFGvalue vv : ivfg.getEdgeValue(pred, curU)) {
                    LocatePointer curLp = getLocatePointer(pred, vv);
//                    utils.myprintln("\t-----others0----backAnalysis: " + curLp, utils.BACKANALYSIS);
                    if (color.add(curLp)) {
                        if (vv.isField() || vv.isStaticFieldRef()) {
                            s.push(new Pair<>(curLp, null));
                        }
                        else if (vv.isLocal()) {
                            s.push(new Pair<>(curLp, tempU));
                        }
                        util.myprintln("\t-----others0----backAnalysis: " + curLp, utils.BACKANALYSIS);
                        if (curLp.getV().isField()) {
                            getCsFiledLocatePointer(pred, vv).forEach(flp -> {
                                        util.myprintln("\t\t-----others0.5----backAnalysis: " + wpCFG.getMethodOf(flp.getU()) + ": " + flp, utils.BACKANALYSIS);
                                        getCsLocatePointer(flp.getU(), flp.getV()).forEach(lp -> {
                                            util.myprintln("\t\t-----others1----backAnalysis: " + wpCFG.getMethodOf(lp.getU()) + ": " + lp, utils.BACKANALYSIS);
                                            worklistAddBackAnalysis(lp);
                                        });
                                    }
                            );
                        }
                        else if (curLp.getV().isStaticFieldRef()) {
                            getCsLocatePointer(curLp.getU(), (StaticFieldRef) curLp.getV().getValue()).forEach(lp -> {
                                util.myprintln("\t\t-----others2----backAnalysis: " + wpCFG.getMethodOf(lp.getU()) + ": " + lp, utils.BACKANALYSIS);
                                worklistAddBackAnalysis(lp);
                            });
                        }
                        else {
                            getCsLocatePointer(curLp.getU(), curLp.getV()).forEach(lp -> {
                                util.myprintln("\t\t-----others3----backAnalysis: " + wpCFG.getMethodOf(lp.getU()) + ": " + lp, utils.BACKANALYSIS);
                                worklistAddBackAnalysis(lp);
                            });
                        }
                    }

                }
            }
            if (worklist.size() > overSize) {
                overSizeCount++;
                return false;
            }
//            System.out.println("--backAnalysis-- cc = " + cc++);
        }

        util.myprintln("||----------------------------------------backAnalysis----------------------------------------||\n", utils.BACKANALYSIS);
        return true;
    }

    private final HashSet<Unit> colors = new HashSet<>();
    private final Vector<Pair<Pair<Unit, Unit>, VFGvalue>> toBeDel = new Vector<>();
    private void flowSenPartOfVFG() {
        colors.clear();
//        utils.BUILDVFG = true;

        worklist.forEach(lp -> {
//            System.out.println("-------flowSenPartOfVFG-------" + lp);
            Unit u = lp.getU();
            if (!colors.add(u)) return;
            toBeDel.clear();

            u.getDefBoxes().forEach(vb -> {
                Value v = vb.getValue();
                if (v instanceof InstanceFieldRef) {
                    InstanceFieldRef ifr = (InstanceFieldRef) v;
//                    System.out.println("--------------" + wpCFG.getMethodOf(u).toString() + "/" + ifr.getBase().toString());
//                    Variable var = new Variable(u.getCtx(), wpCFG.getMethodOf(u).getMethod() + "/" + ifr.getBase().toString());
                    String var = wpCFG.getMethodOf(u) + "/" + ifr.getBase().toString();
//                    util.plnG("--buildVFG - fieldUse: " + u + var);
                    if (database.vptInsen.containsKey(var)) {
                        ivfg.getSuccs(u).forEach(succ ->
                            ivfg.getEdgeValue(u, succ).forEach(vv ->
                                    toBeDel.add(new Pair<>(new Pair<>(u, succ), vv)))
                        );
                    }
                }
            });

            toBeDel.forEach(d -> ivfg.deleteEdge(d.getO1().getO1(), d.getO1().getO2(), d.getO2()));

            u.getDefBoxes().forEach(vb -> {
                Value v = vb.getValue();
                if (v instanceof InstanceFieldRef) {
                    InstanceFieldRef ifr = (InstanceFieldRef) v;
//                    System.out.println("--------------" + wpCFG.getMethodOf(u).toString() + "/" + ifr.getBase().toString());
//                    Variable var = new Variable(u.getCtx(), wpCFG.getMethodOf(u).getMethod() + "/" + ifr.getBase().toString());
                    String var = wpCFG.getMethodOf(u) + "/" + ifr.getBase().toString();
//                    util.plnG("--buildVFG - fieldUse: " + u + var);
                    if (database.vptInsen.containsKey(var)) {
                        for (Allocation o : database.vptInsen.get(var)) {
                            if(utils.isConstantAllocation(o.getO())) continue;
                            ivfg.dfsStoreWithCg(u, new Field(new CSAllocation(o), ifr.getField().toString()));
//                            ivfg.dfsStoreInter(u, u, new Field(new CSAllocation(o), ifr.getField().toString()));
                        }
                    }
                }
            });

//            ivfg.getSuccs(u).forEach(succ -> {
//                ivfg.getEdgeValue(u, succ).forEach(vv -> {
//                    if (vv.isField() && !reachable(u, succ)) {
//                        toBeDel.add(new Pair<>(new Pair<>(u, succ), vv));
//                    }
//                });
//            });
        });
//        toBeDel.forEach(d -> {
//            ivfg.deleteEdge(d.getO1().getO1(), d.getO1().getO2(), d.getO2());
//        });
    }

    private boolean strongUpdate() {
        boolean result = false;
        util.myprintln("||----------------------------------------strongUpdate----------------------------------------||\n", utils.STRONGUPDATE);
        for (LocatePointer key : vpt.keySet()) {
            if (key.getV().getValue() instanceof JimpleLocal && vptGet(key).size() == 1) {
                if (ivfg.getSuccs(key.getU()) != null) {
//                    System.out.println("----------------------------strongUpdate: " + key.getU());
                    for (Unit u : ivfg.getSuccs(key.getU())) {
                        if (u instanceof JAssignStmt) {
                            JAssignStmt jas = (JAssignStmt) u;
                            Value leftV = jas.getLeftOp();
                            Value rightV = jas.getRightOp();
                            if (leftV instanceof InstanceFieldRef && rightV instanceof JimpleLocal &&
                                    ((InstanceFieldRef) leftV).getBase().equals(key.getV().getValue())) {
//                                l: p = ?; l': p.f = q; l'': a.f = b;
//                                p -> only one object o, l --{p}--> l', l'' --{o.f}--> l';
//                                delete edge(l'' --{o.f)}--> l') 别的field，还要起传递作用，不能删。
//                                System.out.println("----------------------------\tstrongUpdate: " + u);
                                List<VFGvalue> toBeDelete = new ArrayList<>();
                                List<Unit> toBeDeleteFrom = new ArrayList<>();
                                if (ivfg.getPreds(u) != null) {
                                    for (Unit pred : ivfg.getPreds(u)) {
                                        for (VFGvalue vv : ivfg.getEdgeValue(pred, u)) {
                                            if (vv.isField()) {
                                                if (vptGet(key).contains(((Field) (vv.getValue())).getO())) {
                                                    toBeDelete.add(vv);
                                                    toBeDeleteFrom.add(pred);
                                                }
                                            }
                                        }
                                    }
                                }
                                for (int i = 0; i < toBeDelete.size(); i++) {
                                    Unit from = toBeDeleteFrom.get(i);
                                    VFGvalue vv = toBeDelete.get(i);
                                    util.myprintln("delete: " + from + "\t->\t" + u + ": " + vv, utils.STRONGUPDATE);
                                    ivfg.deleteEdge(from, u, vv);
                                    result = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        util.myprintln("||----------------------------------------strongUpdate----------------------------------------||\n", utils.STRONGUPDATE);
        return result;
    }

    private void addToVpt(LocatePointer lp, CSAllocation a) {
//        util.plnY("addToVpt(LocatePointer lp, Allocation a)" + lp + ": " + a);
        int size = vptGet(lp).size();
        vptGet(lp).add(a);
        if (size < vptGet(lp).size()) {
            worklistAddSucc(lp);
        }
    }

    private void addToVpt(LocatePointer lp, Vector<CSAllocation> as) {
//        util.plnY("addToVpt(LocatePointer lp, Vector<Allocation> as)" + lp + ": " + a);
        int size = vptGet(lp).size();
        vptGet(lp).addAll(as);
        if (size < vptGet(lp).size()) {
            as.forEach(a -> util.myprintln(a.toString(), StaticData.BG, utils.ANALYZE));
            worklistAddSucc(lp);
        }
    }

    private void addToVpt(LocatePointer lp, HashSet<CSAllocation> as) {
//        util.plnY("addToVpt(LocatePointer lp, HashSet<Allocation> as)" + lp + ": " + a);
        int size = vptGet(lp).size();
        vptGet(lp).addAll(as);
        if (size < vptGet(lp).size()) {
            as.forEach(a -> util.myprintln(a.toString(), StaticData.BG, utils.ANALYZE));
            worklistAddSucc(lp);
        }
    }

    private void addToVpt(LocatePointer lpTo, LocatePointer lpFrom) {
//        util.plnY("addToVpt(LocatePointer lpTo, LocatePointer lpFrom)" + lpFrom + "\t->\t" + lpTo);
//        lpFrom.getU();
//        if(lpFrom == null) return; // bugbug
//        vptPut(lpTo); // bugbug: exchange with next line
        int size = vptGet(lpTo).size();
        vptGet(lpTo).addAll(vptGet(lpFrom));
        if (size < vptGet(lpTo).size()) {
            vptGet(lpFrom).forEach(a -> util.myprintln(a.toString(), StaticData.BG, utils.ANALYZE));
            worklistAddSucc(lpTo);
        }
    }

    private void vptPut(LocatePointer lv) {
        vpt.computeIfAbsent(lv, k -> new HashSet<>());
        // 可能会出现重复的 lp?
        getUnitToLocatePointer(lv.getU()).add(lv);
    }
    
    private HashSet<CSAllocation> vptGet(LocatePointer lv) {
        if(lv == null) return new HashSet<>();

        LocatePointer key;
        if(lv.getV().isLocal()) {
            vpt.computeIfAbsent(lv, k -> new HashSet<>());
            key = lv;
        }
        else if(lv.getV().isField()) {
            vpt.computeIfAbsent(lv, k -> new HashSet<>());
            key = lv;
        }
        else if(lv.getV().isStaticFieldRef()) {
            vpt.computeIfAbsent(lv, k -> new HashSet<>());
            key = new LocatePointer(lv.getU(), lv.getV(), lv.getMethod(), ObjContext.allContext);
        }
        else if(lv.getV().isConstant()) {
            vpt.computeIfAbsent(lv, k -> new HashSet<>());
            key = lv;
        }
        else {
            key = lv;
        }
        return vpt.get(key);
    }

    private Value getLV(Unit u, VFGvalue vv) {
        Value res = null;
        if (u instanceof JAssignStmt) {
            JAssignStmt jas = (JAssignStmt) u;
            Value left = jas.getLeftOp();
            if (vv.getValue().equals(left)) {
                // StaticFieldRef, Local
                return jas.getLeftOp();
            }
            else if (left instanceof ArrayRef) {
                if (((ArrayRef) left).getBase().equals(vv.getValue())) {
                    return ((ArrayRef) left).getBase();
                }
            }

            if (jas.getRightOp() instanceof InvokeExpr) {
                InvokeExpr ie = (InvokeExpr) jas.getRightOp();
                for (Value v : ie.getArgs()) {
                    if (vv.getValue().equals(v)) {
                        return v;
                    }
                }

                if (ie instanceof JVirtualInvokeExpr) {
                    JVirtualInvokeExpr jvi = (JVirtualInvokeExpr) ie;
                    if (jvi.getBase().equals(vv.getValue())) return jvi.getBase();
                }
                else if (ie instanceof JSpecialInvokeExpr) {
                    JSpecialInvokeExpr jvi = (JSpecialInvokeExpr) ie;
                    if (jvi.getBase().equals(vv.getValue())) return jvi.getBase();
                }
                else if (ie instanceof JStaticInvokeExpr) {
                    // no base
                }
                else if (ie instanceof JDynamicInvokeExpr) {

                }
                else if (ie instanceof JInterfaceInvokeExpr) {
                    JInterfaceInvokeExpr jvi = (JInterfaceInvokeExpr) ie;
                    if (jvi.getBase().equals(vv.getValue())) return jvi.getBase();
                }
            }
        }
        else if (u instanceof JIdentityStmt) {
            JIdentityStmt jis = (JIdentityStmt) u;
            res = jis.getLeftOp();
        }
        else if (u instanceof JReturnStmt) {
            res = ((JReturnStmt) u).getOp();
        }
        else if (u instanceof JInvokeStmt) {
            InvokeExpr ie = ((JInvokeStmt) u).getInvokeExpr();
            for (Value v : ie.getArgs()) {
                if (vv.getValue().equals(v)) {
                    return v;
                }
            }
            if (ie instanceof JVirtualInvokeExpr) {
                JVirtualInvokeExpr jvi = (JVirtualInvokeExpr) ie;
                if (jvi.getBase().equals(vv.getValue())) return jvi.getBase();
            }
            else if (ie instanceof JSpecialInvokeExpr) {
                JSpecialInvokeExpr jvi = (JSpecialInvokeExpr) ie;
                if (jvi.getBase().equals(vv.getValue())) return jvi.getBase();
            }
            else if (ie instanceof JStaticInvokeExpr) {

            }
            else if (ie instanceof JDynamicInvokeExpr) {

            }
            else if (ie instanceof JInterfaceInvokeExpr) {
                JInterfaceInvokeExpr jvi = (JInterfaceInvokeExpr) ie;
                if (jvi.getBase().equals(vv.getValue())) return jvi.getBase();
            }
        }

        if (vv.isLocal() && vv.getValue().equals(res)) {
            return res;
        }
        else {
            System.err.println("private Value getLV(Unit u, VFGvalue vv): " + u + " - " + vv);
        }
        return res;
    }

    private void worklistAddSucc(LocatePointer lp) {
        Unit u = lp.getU();

        // 直接影响的
        for (Unit succ : ivfg.getSuccs(u)) {
//                util.plnP(u + " ---- " + succ);
//                util.plnP("\t" + lp.getV());
//                util.plnP("\t" + ivfg.getEdgeValue(u, succ));
            ivfg.getEdgeValue(u, succ).forEach(vv -> { // 2022-05-04 20:54:08
                if (VFGvalue.equal(vv, lp.getV())) {
//                        util.plnG(u + " ---- " + succ);
                    for (LocatePointer succLp : getUnitToLocatePointer(succ)) {
                        worklist.remove(succLp);
                        worklistAddWithWeight(succLp);
                        util.myprintln("worklist add: " + succLp, StaticData.R, utils.WORKLISTADD);
                    }
                }
            });
            if (ivfg.getEdgeValue(u, succ).contains(lp.getV())) { // 2022-04-13 11:48:56

            }
//                for (LocatePointer succLp : getUnitToLocatePointer(succ)) {
//                    worklist.remove(succLp);
//                    worklistAddAnalysis(succLp);
//                    utils.myprintln("worklist add: " + succLp, StaticData.R, utils.WORKLISTADD);
//                }
        }


        // 间接影响的，比如 a = b.f(c, d) ,b,c,d的改变都会间接影响到a，即使是没有相关的值流
        // bug bugbug, 最后的||是2022-05-09加上去的。
        if ((u instanceof JAssignStmt && ((JAssignStmt) u).getRightOp() instanceof InvokeExpr) || u instanceof InvokeStmt) {
            for (Unit succ : ivfg.getSuccs(u)) {
                for (LocatePointer succLp : getUnitToLocatePointer(succ)) {
                    worklist.remove(succLp);
                    worklistAddWithWeight(succLp);
                    util.myprintln("worklist add: " + succLp, StaticData.R, utils.WORKLISTADD);
                }
            }
            for (LocatePointer locatePointer : getUnitToLocatePointer(u)) {
                if (VFGvalue.equal(locatePointer.getV(), lp.getV())) continue;
                worklist.remove(locatePointer);
                worklistAddWithWeight(locatePointer);
                util.myprintln("worklist add: " + locatePointer, StaticData.R, utils.WORKLISTADD);
            }
        }
    }

    private void worklistAddBackAnalysis(LocatePointer lv) {
        int weight = 0;
        Unit u = lv.getU();
        if (u instanceof JAssignStmt) {
            JAssignStmt u0 = (JAssignStmt) u;
            Value left = u0.getLeftOp();
            Value right = u0.getRightOp();
            if (left instanceof JimpleLocal && right instanceof JNewExpr) { // Alloc
                weight = 1024 * 1024;
            }
        }
        lvWeight.put(lv, weight);
        worklist.add(lv);
    }

    private void worklistAddWithWeight(LocatePointer lv) {
        // set weight
//        util.plnG(lv.toString());
        int weight = 0;
        Unit u = lv.getU();
        if (u instanceof JAssignStmt) {
            JAssignStmt u0 = (JAssignStmt) u;
            Value left = u0.getLeftOp();
            Value right = u0.getRightOp();
            if (left instanceof JimpleLocal && right instanceof JimpleLocal) { // Assign

            }
            else if (left instanceof JimpleLocal && right instanceof JCastExpr) { // Assign

            }
            else if (left instanceof JimpleLocal && right instanceof InstanceFieldRef) { // Load: p = q.f

            }
            else if (left instanceof InstanceFieldRef && right instanceof JimpleLocal) { // Store

            }
            else if (left instanceof JimpleLocal && right instanceof JNewExpr) { // Alloc
                weight = 1024 * 1024;
            }
            else if (left instanceof JimpleLocal && right instanceof SPhiExpr) { // Phi func

            }
            else if (left instanceof JimpleLocal && right instanceof InvokeExpr) {

            }
        }
        else if (u instanceof JIdentityStmt) {

        }
        else if (u instanceof JInvokeStmt) {

        }
        else if (u instanceof JReturnVoidStmt) {

        }
        else if (u instanceof JReturnStmt) {

        }
        if (!worklist.isEmpty()) { // 防止程序崩溃
            weight = lvWeight.get(worklist.peek()) + 1;
        }

        lvWeight.put(lv, weight);
        worklist.add(lv);
    }

    private void outVPT() {
        System.out.println("\n||---------------------------------------------result-----------------------------------------||");
//        System.out.println("----------------------");
//        System.out.println(lv);
//        for(Allocation a : vptGet(lv)) {
//            System.out.println("\t" + a);
//        }
//        System.out.println("----------------------");
        for (LocatePointer key : vpt.keySet()) {
            System.out.println("\n" + wpCFG.getMethodOf(key.getU()).toString() + key);
            for (CSAllocation a : vptGet(key)) {
                System.out.println("\t" + a.toString());
            }
        }
        System.out.println("||---------------------------------------------result-----------------------------------------||\n");
    }

    private CSAllocation makeAlloc(Unit u, ObjContext ctx) {
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

        if(!database.assignHeapAllocation.containsKey(wpCFG.getMethodOf(u).toString() + "/" + right.toString() + "---" + utils.getLineNumber(u))) {
//            util.plnR("makeAlloc" + wpCFG.getMethodOf(u).toString() + "/" + right.toString() + "---" + utils.getLineNumber(u));
//             System.exit(-1);
            return CSAllocation.not_in_doop;
        }

        String obj = database.assignHeapAllocation.get(
                wpCFG.getMethodOf(u).toString() + "/" + right.toString() + "---" + utils.getLineNumber(u));

        if(obj == null) {
            return CSAllocation.not_in_doop;
        }

        return new CSAllocation(ctx, new Allocation(obj));
//        unitToAllocation.put(u, new Allocation(obj));
//        getNewContext(u).forEach(csCtx -> { res.add(new Allocation(csCtx, obj)); }); // bugbug 这毫无精度可言
//        System.out.println("--makeAlloc(Unit u, ObjContext ctx)--: " + res);
    }

    private Allocation makeAlloc(Unit u) {

        JAssignStmt u0 = (JAssignStmt) u;
        Value right = u0.getRightOp();
        if(!(right instanceof NewExpr)) {
            // System.exit(-1);
            return null;
        }
        if(right.toString().contains("new java.lang.StringBuilder")) {
            return Allocation.string_builder;
        }
        if(right.toString().contains("new java.lang.StringBuffer")) {
            return Allocation.string_buffer;
        }

        if(!database.assignHeapAllocation.containsKey(wpCFG.getMethodOf(u).toString() + "/" + right.toString() + "---" + utils.getLineNumber(u))) {
//            util.plnR("makeAlloc" + wpCFG.getMethodOf(u).toString() + "/" + right.toString() + "---" + utils.getLineNumber(u));
//             System.exit(-1);
        }

        String obj = database.assignHeapAllocation.get(
                wpCFG.getMethodOf(u).toString() + "/" + right.toString() + "---" + utils.getLineNumber(u));

        if(obj == null) {
            return Allocation.not_in_doop;
        }

//        util.writeFilelnWithPrefix(u + " ==--== " + wpCFG.getMethodOf(u).toString() + "/" + right.toString() + "---" + utils.getLineNumber(u), "allocation");
        return new Allocation(obj);
    }

    private LocatePointer getLocatePointer(Unit u, VFGvalue vv) {
        locatePointers.computeIfAbsent(u, k -> new HashMap<>());
        return locatePointers.get(u).computeIfAbsent(vv, k -> new LocatePointer(u, vv, wpCFG.getMethodOf(u).toString()));
    }

    // 给域里的 对象部分 加上上下文
    private Vector<LocatePointer> getCsFiledLocatePointer(Unit u, VFGvalue vv) {
        Vector<LocatePointer> res = new Vector<>();

        if (vv.isField()) {
//            util.plnP(vv.toString());
            Vector<CSAllocation> als = new Vector<>();
            if (insenAlloToSenAllo.containsKey(((Field) vv.getValue()).getO().getO())) {
                als = insenAlloToSenAllo.get(((Field) vv.getValue()).getO().getO());
            }
            else {
                als.add(((Field) vv.getValue()).getO());
            }

            als.forEach(al -> {
                if (!utils.isConstantAllocation(al.toString())) {
                    res.add(new LocatePointer(u, new VFGvalue(new Field(al, ((Field) vv.getValue()).getField())), wpCFG.getMethodOf(u).toString()));
                }
            });

        }
        return res;
    }

    private Vector<LocatePointer> getCsLocatePointer(Unit u, VFGvalue vv) {
        Vector<LocatePointer> res = new Vector<>();
        getNewContext(u).forEach(ctx -> {
            Pair<Unit, Pair<ObjContext, VFGvalue>> ucv = new Pair<>(u, new Pair<>(ctx, vv));
            csLocatePointers.computeIfAbsent(ucv, k -> new LocatePointer(u, vv, wpCFG.getMethodOf(u).toString(), ctx));
            vptPut(csLocatePointers.get(ucv));
            res.add(csLocatePointers.get(ucv));
        });
        return res;
    }

    private Vector<LocatePointer> getCsLocatePointer(Unit u, Value v) {
        VFGvalue vv;
        if (v instanceof Local) {
            vv = new VFGvalue((JimpleLocal) v);
        }
        else if (v instanceof Constant) {
            vv = new VFGvalue((Constant) v);
        }
        else if (v instanceof ArrayRef) {
            vv = new VFGvalue(((ArrayRef) v).getBase());
        }
        else {
//            System.err.println("(Unit u, Value v)2: " + u + ", " + v.getClass() + "\t" + v);
            return new Vector<>();
        }

        return getCsLocatePointer(u, vv);
    }
    
    private Vector<LocatePointer> getCsLocatePointer(Unit u, StaticFieldRef v) {
        VFGvalue vv = new VFGvalue(v);
        Vector<LocatePointer> res = new Vector<>();

//        getNewContext(u).forEach(ctx -> {
//            Pair<Unit, Pair<ObjContext, VFGvalue>> ucv = new Pair<>(u, new Pair<>(ctx, vv));
//            LocatePointer lp = new LocatePointer(u, vv, wpCFG.getMethodOf(u).toString(), ctx);
//            staticFieldLPs.computeIfAbsent(ucv, k -> lp);
//            vptPut(staticFieldLPs.get(ucv));
//            res.add(staticFieldLPs.get(ucv));
//        });
        Pair<Unit, Pair<ObjContext, VFGvalue>> ucv = new Pair<>(u, new Pair<>(ObjContext.allContext, vv));
        staticFieldLPs.computeIfAbsent(ucv, k -> new LocatePointer(u, vv, wpCFG.getMethodOf(u).toString(), ObjContext.allContext));
        vptPut(staticFieldLPs.get(ucv));
        res.add(staticFieldLPs.get(ucv));
//           util.plnBG("getCsLocatePointer(Unit u, StaticFieldRef v): " + ucv + ": " + new LocatePointer(u, vv, wpCFG.getMethodOf(u).toString(), ObjContext.allContext));

        return res;
    }

    private LocatePointer getCsLocatePointer(Unit u, VFGvalue vv, ObjContext ctx) {
        Pair<Unit, Pair<ObjContext, VFGvalue>> ucv = new Pair<>(u, new Pair<>(ctx, vv));
        csLocatePointers.computeIfAbsent(ucv, k -> new LocatePointer(u, vv, wpCFG.getMethodOf(u).toString(), ctx));
        // 冗余
        vptPut(csLocatePointers.get(ucv));
        return csLocatePointers.get(ucv);
    }

    private LocatePointer getCsLocatePointer(Unit u, Value v, ObjContext ctx) {
        VFGvalue vv;
        if (v instanceof Local) {
            vv = new VFGvalue((JimpleLocal) v);
        }
        else if (v instanceof StaticFieldRef) {
            vv = new VFGvalue((StaticFieldRef) v);
            Pair<Unit, Pair<ObjContext, VFGvalue>> ucv = new Pair<>(u, new Pair<>(ObjContext.allContext, vv));
            staticFieldLPs.computeIfAbsent(ucv, k -> new LocatePointer(u, vv, wpCFG.getMethodOf(u).toString(), ObjContext.allContext));
            // 冗余
            vptPut(staticFieldLPs.get(ucv));
            return staticFieldLPs.get(ucv);
        }
        else if (v instanceof ArrayRef) {
            vv = new VFGvalue(((ArrayRef) v).getBase());
        }
        else {
//            System.err.println("(Unit u, Value v)2: " + u + ", " + v.getClass() + "\t" + v);
            return null;
        }

        return getCsLocatePointer(u, vv, ctx);
    }

    private final HashSet<SootMethod> colorGetNewContext = new HashSet<>();
    private final HashMap<SootMethod, Vector<ObjContext>> methodToContext = new HashMap<>();
    private Vector<ObjContext> getNewContext(Unit u) {
        SootMethod sm = wpCFG.getMethodOf(u);
        if (database.methodToCtx.containsKey(sm.toString())) {
            return database.methodToCtx.get(sm.toString());
        }
        else {
            colorGetNewContext.clear();
            return getNewContextRecursive(u);
        }
    }
    private Vector<ObjContext> getNewContextRecursive(Unit u) {


        SootMethod sm = wpCFG.getMethodOf(u);
        if(!methodToContext.containsKey(sm)) {
//        util.plnB(sm.toString());

            Vector<ObjContext> res = new Vector<>();

            if (sm.equals(wpCFG.getMainMethod())) {
                res.add(ObjContext.initContext);
            }
            else if (sm.isStaticInitializer()) {
                res.add(ObjContext.initContext);
            }
            else if (sm.isStatic()) {
                List<Unit> heads = wpCFG.getCfgs().get(sm.toString()).getHeads();
                if (heads.size() != 0) {
                    heads.forEach(
                            head -> wpCFG.getPredsOf(head).forEach(
                                    pred -> {
                                        if (colorGetNewContext.add(wpCFG.getMethodOf(pred))) { // bug bugbug , interesting, wrong again.
                                            res.addAll(getNewContextRecursive(pred));
                                        }
                                    }
                            )
                    );
                }
                else {
                    res.add(ObjContext.initContext);
                }
            }
            else if (!database.methodToCtx.containsKey(sm.toString())) {
                res.add(ObjContext.allContext);
//            System.err.println("-----------methodToCtx no this method1: " + sm.toString());
            }
            else {
//        int lineNum = utils.getLineNumber(u);
//        String method = sm.toString() + "/" + lineNum;
                try {
                    res.addAll(database.methodToCtx.get(sm.toString()));
                } catch (Exception e) {
                    System.err.println("-----------methodToCtx no this method2: " + sm.toString());
                }
            }

            methodToContext.put(sm, res);
        }

        return methodToContext.get(sm);
    }

    private HashSet<LocatePointer> getUnitToLocatePointer(Unit u) {
        return unitToLocatePointer.computeIfAbsent(u, k -> new HashSet<>());
    }
}