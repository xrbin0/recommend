package com.xrbin.ddpt.model.oldModel;

import com.xrbin.ddpt.model.Context;
import soot.Unit;
import soot.UnitPrinter;
import soot.jimple.internal.AbstractStmt;

import java.util.Objects;

public class CallsiteContext implements Context {
    private Unit ctx1, ctx2;

    public static Unit initContextObj = new AbstractStmt() {
        @Override
        public Object clone() {
            return null;
        }

        @Override
        public boolean fallsThrough() {
            return false;
        }

        @Override
        public boolean branches() {
            return false;
        }

        @Override
        public void toString(UnitPrinter up) { }

        public String toString() {
            return "<<immutable-context>>";
        }
    };
    public static CallsiteContext initContext = new CallsiteContext();
//    public static CallsiteContext initContext = new CallsiteContext(initContextObj, initContextObj);

    // In fact, no context, insen.
    public static Unit allContextObj = new AbstractStmt() {
        @Override
        public Object clone() {
            return null;
        }

        @Override
        public boolean fallsThrough() {
            return false;
        }

        @Override
        public boolean branches() {
            return false;
        }

        @Override
        public void toString(UnitPrinter up) { }

        public String toString() {
            return "<<allUse-context>>";
        }
    };
    public static CallsiteContext allContext = new CallsiteContext();
//    public static CallsiteContext allContext = new CallsiteContext(allContextObj, allContextObj);

//    public CallsiteContext(Unit ctx1, Unit ctx2) {
//        this.ctx1 = ctx1;
//        this.ctx2 = ctx2;
//
////        this.ctx1 = initContextObj;
////        this.ctx2 = initContextObj;
//    }
//
//    public CallsiteContext(Unit ctx1) {
//        this.ctx1 = ctx1;
//        this.ctx2 = initContextObj;
//
////        this.ctx1 = initContextObj;
//    }

    public CallsiteContext() {
        this.ctx1 = initContextObj;
        this.ctx2 = initContextObj;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CallsiteContext context = (CallsiteContext) o;

        if(this.ctx1.equals(allContextObj) && this.ctx2.equals(allContextObj)) return true;

        if(context.ctx1.equals(allContextObj) && context.ctx2.equals(allContextObj)) return true;

        return Objects.equals(ctx1, context.ctx1) &&
                Objects.equals(ctx2, context.ctx2);
    }

    @Override
    public int hashCode() {
        return 0;
    }

    public Unit getO1() {
        return ctx1;
    }

    public void setO1(Unit ctx1) {
        this.ctx1 = ctx1;
    }

    public Unit getO2() {
        return ctx2;
    }

    public void setO2(Unit ctx2) {
        this.ctx2 = ctx2;
    }

    public void setctx(Unit ctx1, Unit ctx2) {
        this.ctx1 = ctx1;
        this.ctx2 = ctx2;
    }

    @Override
    public String toString() {
        return "[" + ctx2 + ", " + ctx1 + "]";
    }
}
