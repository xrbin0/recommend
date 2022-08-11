package com.xrbin.ddpt.model.oldModel;

import com.xrbin.ddpt.model.ObjContext;

import java.util.Objects;

public class CSInvocation {
    String invocation;
    ObjContext ctx;

    public CSInvocation(String invocation, ObjContext ctx) {
        this.invocation = invocation;
        this.ctx = ctx;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CSInvocation csMethod = (CSInvocation) o;
        return Objects.equals(invocation, csMethod.invocation) &&
                Objects.equals(ctx, csMethod.ctx);
    }

    @Override
    public int hashCode() {
        return Objects.hash(invocation, ctx);
    }

    public String getMethod() {
        return invocation;
    }

    public void setMethod(String invocation) {
        this.invocation = invocation;
    }

    public ObjContext getCtx() {
        return ctx;
    }

    public void setCtx(ObjContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public String toString() {
        return ctx + "\t" + invocation;
    }
}
