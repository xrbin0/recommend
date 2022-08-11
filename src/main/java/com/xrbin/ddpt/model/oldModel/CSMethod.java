package com.xrbin.ddpt.model.oldModel;

import com.xrbin.ddpt.model.ObjContext;

import java.util.Objects;

public class CSMethod {
    String method;
    ObjContext ctx;

    public CSMethod(String method, ObjContext ctx) {
        this.method = method;
        this.ctx = ctx;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CSMethod csMethod = (CSMethod) o;
//        return Objects.equals(method, csMethod.method) &&
//                Objects.equals(ctx, csMethod.ctx);
        return this.toString().equals(csMethod.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, ctx);
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public ObjContext getCtx() {
        return ctx;
    }

    public void setCtx(ObjContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public String toString() {
        return ctx + "\t" + method;
    }
}
