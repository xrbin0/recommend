package com.xrbin.ddpt.model.oldModel;

import com.xrbin.ddpt.model.ObjContext;
import soot.Unit;

import java.util.Objects;

public class ObjCtxUnit {
    Unit unit;
    ObjContext ctx;

    public ObjCtxUnit(Unit unit, ObjContext ctx) {
        this.unit = unit;
        this.ctx = ctx;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjCtxUnit that = (ObjCtxUnit) o;
        return Objects.equals(unit, that.unit) &&
                Objects.equals(ctx, that.ctx);
    }

    @Override
    public int hashCode() {
        return Objects.hash(unit, ctx);
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public ObjContext getCtx() {
        return ctx;
    }

    public void setCtx(ObjContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public String toString() {
        return ctx + "\t" + unit.toString();
    }
}
