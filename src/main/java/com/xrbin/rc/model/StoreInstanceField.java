package com.xrbin.rc.model;

public class StoreInstanceField {
    public String method;
    public String from;
    public String base;
    public String signature;

    public StoreInstanceField(String method, String from, String base, String signature) {
        this.method = method;
        this.from = from;
        this.base = base;
        this.signature = signature;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String toString() {
        return method + "\n\t"+ base + "." + signature + " = " + from ;
    }
}
