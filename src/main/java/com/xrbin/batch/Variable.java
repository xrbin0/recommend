package com.xrbin.batch;

/**
 * @author xrbin
 * @date 2023/8/22 下午10:19
 */
public class Variable {
    private final String originName;
    private final String methodName;
    private final String varName;
    private String className;
    private String packageName;

    // <org.apache.lucene.index.SegmentMerger: void closeReaders()>/$stack6
    public Variable(String name) {
        originName = name;
        methodName = name.split("/")[0];
        varName = name.split("/")[1];
        className = methodName.split(": ")[0];
        className = className.replace("<", "");
        packageName = "";
        if (className.contains(".")) {
            packageName = className.substring(0, className.lastIndexOf("."));
        }
    }

    public String getOriginName() {
        return originName;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getVarName() {
        return varName;
    }

    public String getClassName() {
        return className;
    }

    public String getPackageName() {
        return packageName;
    }
}
