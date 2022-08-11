package com.xrbin.ddpt.model;

import java.util.Objects;

public class Allocation {
    public static Allocation not_in_doop = new Allocation("<<not in doop>>");
    public static Allocation null_pseudo_heap = new Allocation("<<null pseudo heap>>");
    public static Allocation string_builder = new Allocation(  "<<string-builder>>");
    public static Allocation string_buffer = new Allocation(   "<<string-buffer>>");
    public static Allocation string_constant = new Allocation( "<<string-constant>>");
    public static Allocation main_method_array_content = new Allocation( "<<main method array content>>");

    String declareMethod;
    String classType;
    Integer count;
    String o;

    public Allocation(String o) {
        this.o = o;
        if(o.contains("new ")) {
            classType = o.split("new ")[1];
            if(classType.contains("/")) {
                classType = classType.split("/")[0];
            }
            else {
                classType = "";
            }
        }
        else if(o.equals("<<null pseudo heap>>")) {
            classType = "null";
        }
        else if(o.equals("<<string-builder>>")) {
            classType = "java.lang.StringBuilder";
        }
        else if(o.equals("<<string-buffer>>")) {
            classType = "java.lang.StringBuffer";
        }
        else if(o.equals("<<string-constant>>")) {
            classType = "java.lang.String";
        }
        else if(o.equals("<<main method array content>>")) {
            classType = "java.lang.String";
        }
        else {
            classType = "";
        }
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public String getDeclareMethod() {
        return declareMethod;
    }

    public void setDeclareMethod(String declareMethod) {
        this.declareMethod = declareMethod;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getO() {
        return o;
    }

    public void setO(String o) {
        this.o = o;
    }

    public static boolean equalsType(String a1, String a2) {
        if (a1.equals(a2)) return true;
        if(a1.equals(Allocation.null_pseudo_heap.getClassType())) return true;
        return (a2.equals(Allocation.null_pseudo_heap.getClassType()));
    }

    @Override
    public boolean equals(Object o1) {
        if (this == o1) return true;
        if (o1 == null || getClass() != o1.getClass()) return false;
        Allocation that = (Allocation) o1;
        return Objects.equals(o, that.o);
//        return Objects.equals(o, that.o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(o);
    }

    @Override
    public String toString() {
//        return "<" + declareMethod + ">/" + classType + "/" + count;
        return o;
    }
}
