package com.xrbin.rc.model;

public class AssignLocal {
    public String method;
    public String from;
    public String to;

    public AssignLocal(String method, String from, String to) {
        this.method = method;
        this.from = from;
        this.to = to;
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

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
