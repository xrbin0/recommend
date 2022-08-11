package com.xrbin.ddpt.model;

import com.xrbin.ddpt.utils;
import com.xrbin.utils.StaticData;
import soot.Unit;

import java.util.Objects;

public class LocatePointer {
    Unit u;
    VFGvalue v;
    ObjContext ctx;
    String method;

    public LocatePointer(Unit u, VFGvalue v, String method) {
        this.u = u;
        this.v = v;
        this.method = method;
        this.ctx = ObjContext.allContext;
    }

    public LocatePointer(Unit u, VFGvalue v, String method, ObjContext ctx) {
        this.u = u;
        this.v = v;
        this.method = method;
        this.ctx = ctx;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocatePointer that = (LocatePointer) o;
        if(v.isLocal()) {
            return Objects.equals(u, that.u) &&
                    Objects.equals(v, that.v) &&
                    Objects.equals(ctx, that.ctx) &&
                    Objects.equals(method, that.method);
        }
        else if(v.isField()) {
//            if(v.equals(that.v)) utils.myprintln("\t\t--LocatePointer eq-- VFGValue", StaticData.B, utils.HIDENMETHOD);
//            if(u.equals(that.u)) utils.myprintln("\t\t--LocatePointer eq-- Unit", StaticData.B, utils.HIDENMETHOD);
//            if(ctx.equals(that.ctx)) utils.myprintln("\t\t--LocatePointer eq-- Ctx", StaticData.B, utils.HIDENMETHOD);
//            return Objects.equals(u, that.u) && Objects.equals(v, that.v); // wrong bugbug, don't know why
            return Objects.equals(u, that.u) &&
                    Objects.equals(v, that.v) &&
                    Objects.equals(ctx, that.ctx);
        }
        else if(v.isStaticFieldRef()) {
            return Objects.equals(u, that.u) && Objects.equals(v, that.v);
        }
        else {
            return Objects.equals(u, that.u) && Objects.equals(v, that.v) ;
        }
    }

    @Override
    public int hashCode() {
//        return 0;
        return Objects.hash(u, v);
    }

    public Unit getU() {
        return u;
    }

    public void setU(Unit u) {
        this.u = u;
    }

    public VFGvalue getV() {
        return v;
    }

    public void setV(VFGvalue v) {
        this.v = v;
    }

    public ObjContext getCtx() {
        return ctx;
    }

    public void setCtx(ObjContext ctx) {
        this.ctx = ctx;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public String toString() {
        if(v.isLocal()) {
            return "(" + u + ", " + ctx + "\t" + method + "/" + v + ")";
        }
        else if(v.isField()) {
            return "(" + u + ", " + ctx + ", " + v + ")";
        }
        else if(v.isStaticFieldRef()) {
            return "(" + u + ", " + ctx + ", " + v + ")";
        }
        else {
            return "(" + u + ", " + v + ")";
        }
    }
//    public String toString() {
//        return ctx + "\t" + method + "/" + v;
//    }
}
