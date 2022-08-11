package com.xrbin.ddpt.model;

import java.util.Objects;

public class Field {
    private CSAllocation o;
    private String field;

    public Field(CSAllocation o, String field) {
        this.o = o;
        this.field = field;
    }

    public CSAllocation getO() {
        return o;
    }

    public void setO(CSAllocation o) {
        this.o = o;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    @Override
    public String toString() {
        return "[" + o + "]" + "." + field;
    }

    @Override
    public boolean equals(Object o1) {
        if (this == o1) return true;
        if (!(o1 instanceof Field)) return false;
        Field field1 = (Field) o1;
        return Objects.equals(getO(), field1.getO()) &&
                Objects.equals(getField(), field1.getField());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getO(), getField());
    }
}
