package com.xrbin.ddpt;

import com.xrbin.ddpt.model.LocatePointer;
import com.xrbin.ddpt.model.VFGvalue;
import soot.Local;
import soot.Unit;
import soot.jimple.internal.JAssignStmt;

public class Query {
    public static LocatePointer find(WholeProgramCFG wpc, String name) {
        LocatePointer res = null;
        String displayVarName = name.substring(name.indexOf("/") + 1);
        String methodName = name.substring(0, name.indexOf("/")); // include declareClass and declareMothed
//        util.pln("Query methodName: " + methodName);
        if(wpc.methodToUnits.containsKey(methodName)) {
            for (Unit u : wpc.methodToUnits.get(methodName)) {
                if (u instanceof JAssignStmt && ((JAssignStmt) u).getLeftOp() instanceof Local) {
//                util.plnB("Query Local: " + ((JAssignStmt) u).getLeftOp() + " - " + displayVarName);
                }
                if (u instanceof JAssignStmt && ((JAssignStmt) u).getLeftOp() instanceof Local &&
                        ((JAssignStmt) u).getLeftOp().toString().equals(displayVarName)) {
                    res = new LocatePointer(u, new VFGvalue(((JAssignStmt) u).getLeftOp()), methodName);
                    break;
                }
            }
        }
        else {
            return null;
        }
        return res;
    }
}
