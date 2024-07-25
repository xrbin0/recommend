package com.xrbin.batch;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author xrbin
 * @date 2023/8/22 下午10:18
 */
public class BatchTool {
    public static void main(String[] args) {
        func1();
    }

    public static Set<Variable> allVars = new HashSet<>();
    public static Map<String, Variable> methodVars = new HashMap<>();
    public static Map<String, Variable> classVars = new HashMap<>();
    public static Map<String, Variable> packageVars = new HashMap<>();

    public static void func1() {
        Set<String> vars = new HashSet<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Qilin_ddpt/logs/luindex/luindexTimeAll");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.split("\t")[0];
                try {
                    vars.add(line);
                    Variable variable = new Variable(line);
//                    System.out.println(variable.getMethodName());
//                    System.out.println(variable.getVarName());
//                    System.out.println(variable.getClassName());
//                    System.out.println(variable.getPackageName());

                    allVars.add(variable);
                    methodVars.put(variable.getMethodName(), variable);
                    classVars.put(variable.getClassName(), variable);
                    packageVars.put(variable.getPackageName(), variable);
                } catch (Exception e) {
                    System.err.println(line);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(vars.size());
    }
}
