package com.xrbin.ddpt.model;

import java.util.Objects;

public class Variable {
    ObjContext ctx;
    String name;

    public Variable(ObjContext ctx, String name) {
        this.ctx = ctx;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Variable variable = (Variable) o;
        return toString().equals(variable.toString());
//        return Objects.equals(ctx, variable.ctx) && Objects.equals(name, variable.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ctx, name);
    }

    @Override
    public String toString() {
        return ctx + "\t" + name;
    }

    public ObjContext getCtx() {
        return ctx;
    }

    public void setCtx(ObjContext ctx) {
        this.ctx = ctx;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
