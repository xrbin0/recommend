package com.xrbin.ddpt.model;

import soot.jimple.Constant;
import soot.jimple.StaticFieldRef;
import soot.jimple.internal.JimpleLocal;

import java.util.Objects;

public class VFGvalue {
    JimpleLocal l;
    Field f;
    StaticFieldRef sf;
    Constant c;

    int type; // 0: Local, 1: Field, 2: StaticFieldRef, 3: Constant

    public static int LOCAL = 0, FIELD = 1, STATICFIELDREF = 2, CONSTANT = 3;

    public VFGvalue(JimpleLocal l) {
        this.l = l;
        type = VFGvalue.LOCAL;
    }

    public VFGvalue(Field f) {
        this.f = f;
        type = VFGvalue.FIELD;
    }

    public VFGvalue(StaticFieldRef sf) {
        this.sf = sf;
        type = VFGvalue.STATICFIELDREF;
    }

    public VFGvalue(Constant sf) {
        this.c = sf;
        type = VFGvalue.CONSTANT;
    }

    public VFGvalue(Object o) {
        if(o instanceof StaticFieldRef) {
            this.sf = (StaticFieldRef)o;
            type = VFGvalue.STATICFIELDREF;
        }
        else if (o instanceof Field) {
            this.f = (Field) o;
            type = VFGvalue.FIELD;
        }
        else if (o instanceof JimpleLocal) {
            this.l = (JimpleLocal) o;
            type = VFGvalue.LOCAL;
        }
        else if (o instanceof Constant) {
            this.c = (Constant) o;
            type = VFGvalue.CONSTANT;
        }
        else {
            type = -1;
//            System.err.println("VFGvalue init, wrong class type" + o.toString());
//            System.exit(-1);
        }
    }

    public Object getValue() {
        if (type == VFGvalue.LOCAL) {
            return l;
        }
        else if (type == VFGvalue.FIELD) {
            return f;
        }
        else if (type == VFGvalue.STATICFIELDREF) {
            return sf;
        }
        else if (type == VFGvalue.CONSTANT) {
            return c;
        }
        else {
            return new Object();
        }
    }

    public int getType() {
        return type;
    }

    public boolean isLocal() {
        return type == LOCAL;
    }

    public boolean isField() {
        return type == FIELD;
    }

    public boolean isStaticFieldRef() {
        return type == STATICFIELDREF;
    }

    public boolean isConstant() {
        return type == CONSTANT;
    }

    public static boolean equal(VFGvalue v1, VFGvalue v2) {
        if (v1 == null && v2 != null) return false;
        if (v1 != null && v2 == null) return false;
        if (v1 == null) return true;

        if(v1.type == v2.type) {
            if (v1.type == VFGvalue.LOCAL) {
                return v1.l.equals(v2.l);
            }
            else if (v1.type == VFGvalue.FIELD) {
                return (v1.f.getO().getO().equals(v2.f.getO().getO()) && v1.f.getField().equals(v2.f.getField()));
            }
            else if (v1.type == VFGvalue.STATICFIELDREF) {
                return v1.sf.toString().equals(v2.sf.toString());
            }
            else if (v1.type == VFGvalue.CONSTANT) {
                return v1.c.equals(v2.c);
            }
            else {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VFGvalue)) return false;
        VFGvalue vfGvalue = (VFGvalue) o;
        return (type == vfGvalue.type && type == 0 && Objects.equals(l, vfGvalue.l)) ||
                (type == vfGvalue.type && type == 1 && Objects.equals(f, vfGvalue.f)) || // f.toString() is wrong, bug bug
                (type == vfGvalue.type && type == 3 && Objects.equals(c.toString(), vfGvalue.c.toString())) ||
                (type == vfGvalue.type && type == 2 && Objects.equals(sf.toString(), vfGvalue.sf.toString())); // wordless
    }

    @Override
    public int hashCode() {
        switch (type) {
            case 0: return Objects.hash(l);
            case 1: return Objects.hash(f);
            case 2: return Objects.hash(sf.toString());
            case 3: return Objects.hash(c.toString());
            default: return 0;

        }
    }

    @Override
    public String toString() {
        if (type == 0) {
            return l.getName();
        }
        else if (type == 1) {
            return f.toString();
        }
        else if (type == 2) {
            return sf.getField().toString();
        }
        else if (type == 3) {
            return c.toString();
        }
        return "";
    }
}

