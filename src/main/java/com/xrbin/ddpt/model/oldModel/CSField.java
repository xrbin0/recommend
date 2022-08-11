package com.xrbin.ddpt.model.oldModel;

import com.xrbin.ddpt.model.Field;
import com.xrbin.ddpt.model.ObjContext;

import java.util.Objects;

public class CSField {
    ObjContext ctx;
    Field field;

    public CSField(ObjContext ctx, Field field) {
        this.ctx = ctx;
        this.field = field;
    }

    public CSField(Field field) {
        this.ctx = ObjContext.allContext;
        this.field = field;
    }

    public ObjContext getCtx() {
        return ctx;
    }

    public void setCtx(ObjContext ctx) {
        this.ctx = ctx;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CSField csField = (CSField) o;
        return Objects.equals(ctx, csField.ctx) &&
                Objects.equals(field, csField.field);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ctx, field);
    }

    @Override
    public String toString() {
        return "CSField{" +
                "ctx=" + ctx +
                ", field=" + field +
                '}';
    }
}
