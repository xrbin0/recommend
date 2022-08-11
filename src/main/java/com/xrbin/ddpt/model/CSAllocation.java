package com.xrbin.ddpt.model;

import java.util.Objects;

public class CSAllocation {
    public static CSAllocation not_in_doop = new CSAllocation(Allocation.not_in_doop);
    public static CSAllocation null_pseudo_heap = new CSAllocation(Allocation.null_pseudo_heap);
    public static CSAllocation string_builder = new CSAllocation(Allocation.string_builder);
    public static CSAllocation string_buffer = new CSAllocation(Allocation.string_buffer);
    public static CSAllocation string_constant = new CSAllocation(Allocation.string_constant);
    public static CSAllocation main_method_array_content = new CSAllocation(Allocation.main_method_array_content);

    ObjContext ctx;
    Allocation allocation;

    public CSAllocation(ObjContext ctx, Allocation o) {
        this.ctx = ctx;
        this.allocation = o;
    }

    public CSAllocation(Allocation o) {
        this.ctx = ObjContext.allContext;
        this.allocation = o;
    }

    public Allocation getO() {
        return allocation;
    }

    public void setO(Allocation o) {
        this.allocation = o;
    }

    public ObjContext getCxt() {
        return ctx;
    }

    public void setCxt(ObjContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public boolean equals(Object o1) {
        if (this == o1) return true;
        if (o1 == null || getClass() != o1.getClass()) return false;
        CSAllocation that = (CSAllocation) o1;
        // 仿效doop的一层上下文
        return ctx.getO1().equals(that.ctx.getO1()) && Objects.equals(allocation, that.allocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ctx, allocation);
    }

    @Override
    public String toString() {
//        return "<" + declareMethod + ">/" + classType + "/" + count;
        return ctx + " " + allocation;
    }
}
