package com.xrbin.rc.model;

public class LoadInstanceField {
    public String method;
    public String base;
    public String signature;
    public String to;

    public LoadInstanceField(String method, String base, String signature, String to) {
        this.method = method;
        this.base = base;
        this.signature = signature;
        this.to = to;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
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

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return method + "\n\t" + to + " = \n\t" + base + "." + signature;
    }
}
