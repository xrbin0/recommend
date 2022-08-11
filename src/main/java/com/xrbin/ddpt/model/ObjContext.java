package com.xrbin.ddpt.model;

import java.util.Objects;

public class ObjContext implements Context{

    public static Allocation initContextObj = new Allocation("<<immutable-context>>");
    public static ObjContext initContext = new ObjContext(initContextObj, initContextObj);

    // In fact, no context, insen.
    public static Allocation allContextObj = new Allocation("<<allUse-context>>");
    public static ObjContext allContext = new ObjContext(allContextObj, allContextObj);
    private Allocation o1, o2;

    public ObjContext(Allocation o1, Allocation o2) {
        this.o1 = o1;
        this.o2 = o2;

//        this.o1 = initContextObj;
//        this.o2 = initContextObj;
    }

    public ObjContext(Allocation o) {
        this.o1 = o;
        this.o2 = allContextObj;

//        this.o1 = initContextObj;
    }

    public ObjContext() {
        this.o1 = allContextObj;
        this.o2 = allContextObj;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjContext objContext = (ObjContext) o;

        if(this.o1.equals(allContextObj) && this.o2.equals(allContextObj)) return true;

        if(objContext.o1.equals(allContextObj) && objContext.o2.equals(allContextObj)) return true;

        if(objContext.o2.equals(allContextObj) && objContext.o1.equals(o1)) return true;

        if(o2.equals(allContextObj) && objContext.o1.equals(o1)) return true;

        return Objects.equals(o1, objContext.o1) && Objects.equals(o2, objContext.o2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(o1, o2);
    }

    public Allocation getO1() {
        return o1;
    }

    public void setO1(Allocation o1) {
        this.o1 = o1;
    }

    public Allocation getO2() {
        return o2;
    }

    public void setO2(Allocation o2) {
        this.o2 = o2;
    }

    public void setctx(Allocation o2) {
        this.o2 = o2;
    }

    @Override
    public String toString() {
        return "[" + o2 + ", " + o1 + "]";
    }
}
