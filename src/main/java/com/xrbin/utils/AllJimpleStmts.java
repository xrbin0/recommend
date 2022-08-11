package com.xrbin.utils;


import com.xrbin.utils.util;
import soot.Body;
import soot.Local;
import soot.Unit;
import soot.Value;
import soot.jimple.*;
import soot.jimple.internal.*;
import soot.shimple.PhiExpr;
import soot.util.Chain;

import java.util.HashMap;
import java.util.HashSet;

public class AllJimpleStmts {
    public static void main(Chain<Unit> uc) {
        for (Unit u : uc) {
//                util.plnY(u.getClass() + "\t" + u.toString());
//                util.plnY("\t" + ((JAssignStmt) u).getRightOp().getClass() + "\t" + ((JAssignStmt) u).getRightOp().toString());
//                util.plnY("\t" + ((JAssignStmt) u).getLeftOp().getClass() + "\t" + ((JAssignStmt) u).getLeftOp().toString());

            if (u instanceof JAssignStmt) {
                JAssignStmt u0 = (JAssignStmt) u;
                Value left = u0.getLeftOp();
                Value right = u0.getRightOp();
                if (left instanceof Local) {
                    if (right instanceof Local) {

                    }
                    else if (right instanceof InvokeExpr) {
                        InvokeExpr ie = u0.getInvokeExpr();
                        if (ie instanceof JVirtualInvokeExpr) {

                        }
                        else if (ie instanceof JSpecialInvokeExpr) {

                        }
                        else if (ie instanceof JStaticInvokeExpr) {

                        }
                        else if (ie instanceof JDynamicInvokeExpr) {
                            util.writeFilelnWithPrefix(u.getClass() + "\t" + u.toString(), "invoke");
                        }
                        else if (ie instanceof JInterfaceInvokeExpr) {
//                            util.writeFilelnWithPrefix(u.getClass() + "\t" + u.toString(), "invoke");
                        }
                        else {
                            util.writeFilelnWithPrefix(u.getClass() + "\t" + u.toString(), "invoke");
                        }
                    }
                    else if (right instanceof NewExpr) {

                    }
                    else if (right instanceof NewArrayExpr) {

                    }
                    else if (right instanceof NewMultiArrayExpr) {

                    }
                    else if (right instanceof StringConstant) {

                    }
                    else if (right instanceof ClassConstant) {
//                        util.plnY(u.getClass() + "\t" + u.toString());
//                        util.plnY("\t" + ((JAssignStmt) u).getRightOp().getClass() + "\t" + ((JAssignStmt) u).getRightOp().toString());
//                        util.plnY("\t" + ((JAssignStmt) u).getRightOp().getType() + "\t" + ((JAssignStmt) u).getRightOp().toString());
//                        util.plnY("\t" + ((JAssignStmt) u).getLeftOp().getClass() + "\t" + ((JAssignStmt) u).getLeftOp().toString());
                    }
                    else if (right instanceof NumericConstant) {
//                        util.plnY(u.getClass() + "\t" + u.toString());
//                        util.plnY("\t" + ((JAssignStmt) u).getRightOp().getClass() + "\t" + ((JAssignStmt) u).getRightOp().toString());
//                        util.plnY("\t" + ((JAssignStmt) u).getRightOp().getType() + "\t" + ((JAssignStmt) u).getRightOp().toString());
//                        util.plnY("\t" + ((JAssignStmt) u).getLeftOp().getClass() + "\t" + ((JAssignStmt) u).getLeftOp().toString());
                    }
                    else if (right instanceof NullConstant) {

                    }
                    else if (right instanceof InstanceFieldRef) {
                        InstanceFieldRef ref = (InstanceFieldRef) right;

                    }
                    else if (right instanceof StaticFieldRef) {
                        StaticFieldRef ref = (StaticFieldRef) right;

                    }
                    else if (right instanceof ArrayRef) {
                        ArrayRef ref = (ArrayRef) right;

                    }
                    else if (right instanceof CastExpr) {
                        CastExpr cast = (CastExpr) right;
                        Value op = cast.getOp();

                    }
                    else if (right instanceof PhiExpr) {

                    }
                    else if (right instanceof BinopExpr) {

                    }
                    else if (right instanceof UnopExpr) {

                    }
                    else if (right instanceof InstanceOfExpr) {
                        // a instance of A
                        //                            util.plnY(u.getClass() + "\t" + u.toString());
                        //                            util.plnY(b.getMethod().toString() + "\t" + b.getMethod().getDeclaringClass());
                        //                            util.plnY("\t" + ((JAssignStmt) u).getRightOp().getClass() + "\t" + ((JAssignStmt) u).getRightOp().toString());
                        //                            util.plnY("\t" + ((JAssignStmt) u).getLeftOp().getClass() + "\t" + ((JAssignStmt) u).getLeftOp().toString());
                    }
                    else {
                        util.plnY(u.getClass() + "\t" + u.toString());
                        util.plnY("\t" + ((JAssignStmt) u).getRightOp().getClass() + "\t" + ((JAssignStmt) u).getRightOp().toString());
                        util.plnY("\t" + ((JAssignStmt) u).getLeftOp().getClass() + "\t" + ((JAssignStmt) u).getLeftOp().toString());
                    }
                }
                else {
                    if (right instanceof Local) {

                    }
                    else if (right instanceof StringConstant) {

                    }
                    else if (right instanceof NumericConstant) {

                    }
                    else if (right instanceof NullConstant) {

                    }
                    else if (right instanceof ClassConstant) {

                    }
                    else if (right instanceof MethodHandle) {

                    }
                    else {
                                                    util.plnY(u.getClass() + u.toString());
                                                    util.plnY("\t" + ((JAssignStmt) u).getRightOp().getClass() + ((JAssignStmt) u).getRightOp().toString());
                                                    util.plnY("\t" + ((JAssignStmt) u).getLeftOp().getClass() + ((JAssignStmt) u).getLeftOp().toString());
                    }

                    // arrays
                    //
                    // NoNullSupport: use the line below to remove Null Constants from the facts.
                    // if(left instanceof ArrayRef && rightLocal != null)
                    if (left instanceof ArrayRef) {

                    }
                    // NoNullSupport: use the line below to remove Null Constants from the facts.
                    // else if(left instanceof InstanceFieldRef && rightLocal != null)
                    else if (left instanceof InstanceFieldRef) {

                    }
                    // NoNullSupport: use the line below to remove Null Constants from the facts.
                    // else if(left instanceof StaticFieldRef && rightLocal != null)
                    else if (left instanceof StaticFieldRef) {

                    }
                    else {
                        //                            util.plnY(u.getClass() + "\t" + u.toString());
                        //                            util.plnY("\t" + ((JAssignStmt) u).getRightOp().getClass() + "\t" + ((JAssignStmt) u).getRightOp().toString());
                        //                            util.plnY("\t" + ((JAssignStmt) u).getLeftOp().getClass() + "\t" + ((JAssignStmt) u).getLeftOp().toString());
                    }
                }
            }
            else if (u instanceof JIdentityStmt) {
                Value left = ((JIdentityStmt) u).getLeftOp();
                Value right = ((JIdentityStmt) u).getRightOp();

                if (right instanceof CaughtExceptionRef) {
                    // make sure we can jump to statement we do not care about (yet)

                            /* Handled by ExceptionHandler generation (ExceptionHandler:FormalParam).

                               TODO Would be good to check more carefully that a caught
                               exception does not occur anywhere else.
                            */
                    //                        util.plnY(u.getClass() + "\t" + u.toString());
                    //                        util.plnY("\t" + ((JIdentityStmt) u).getRightOp().getClass() + "\t" + ((JIdentityStmt) u).getRightOp().toString());
                    //                        util.plnY("\t" + ((JIdentityStmt) u).getLeftOp().getClass() + "\t" + ((JIdentityStmt) u).getLeftOp().toString());
                }
                else if (left instanceof Local && right instanceof ThisRef) {

                }
                else if (left instanceof Local && right instanceof ParameterRef) {

                }
                else {
                    //                        util.plnY(u.getClass() + "\t" + u.toString());
                    //                        util.plnY("\t" + ((JIdentityStmt) u).getRightOp().getClass() + "\t" + ((JIdentityStmt) u).getRightOp().toString());
                    //                        util.plnY("\t" + ((JIdentityStmt) u).getLeftOp().getClass() + "\t" + ((JIdentityStmt) u).getLeftOp().toString());
                }
            }
            else if (u instanceof JInvokeStmt) {
                JInvokeStmt u0 = (JInvokeStmt) u;
                InvokeExpr ie = u0.getInvokeExpr();
                if (ie instanceof JVirtualInvokeExpr) {

                }
                else if (ie instanceof JSpecialInvokeExpr) {

                }
                else if (ie instanceof JStaticInvokeExpr) {

                }
                else if (ie instanceof JDynamicInvokeExpr) {
                    util.writeFilelnWithPrefix(u.getClass() + "\t" + u.toString(), "invoke");
                }
                else if (ie instanceof JInterfaceInvokeExpr) {
//                    util.writeFilelnWithPrefix(u.getClass() + "\t" + u.toString(), "invoke");
                }
                else {
                    util.writeFilelnWithPrefix(u.getClass() + "\t" + u.toString(), "invoke");
                }
            }
            else if (u instanceof JReturnVoidStmt) {

            }
            else if (u instanceof JReturnStmt) {
                Value v = ((JReturnStmt) u).getOp();
                if (v instanceof Local) {

                }
                else if (v instanceof StringConstant) {

                }
                else if (v instanceof ClassConstant) {

                }
                else if (v instanceof NumericConstant) {

                }
                else if (v instanceof MethodHandle) {

                }
                else if (v instanceof NullConstant) {

                }
                else {
                    //                        util.plnY(u.getClass() + "\t" + u.toString());
                }
            }
            else if (u instanceof JTableSwitchStmt) {
                //                    util.plnB(u.getClass() + "\t" + u.toString());
            }
            else if (u instanceof JLookupSwitchStmt) {
                //                    util.plnB(u.getClass() + "\t" + u.toString());
            }
            else if (u instanceof JThrowStmt) {
                //                    JThrowStmt jts = (JThrowStmt) u;
                //                    util.plnG(u.getClass() + "\t" + u.toString());
                //                    util.plnG(jts.getOp().toString());
            }
            else if (u instanceof JRetStmt) {
                //                    util.plnG(u.getClass() + "\t" + u.toString());
            }
            else if (u instanceof JEnterMonitorStmt || u instanceof JExitMonitorStmt) {
                //                    util.plnG(u.getClass() + "\t" + u.toString());
            }
            else if (u instanceof JGotoStmt || u instanceof JBreakpointStmt || u instanceof JNopStmt || u instanceof JIfStmt) {
                //                    util.plnB(u.getClass() + "\t" + u.toString());
            }
            else {
                //                    util.plnR(u.getClass() + "\t" + u.toString());
            }
        }
    }
}
