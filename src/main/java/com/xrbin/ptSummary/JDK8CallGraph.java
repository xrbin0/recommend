package com.xrbin.ptSummary;

import com.xrbin.ddpt.*;
import com.xrbin.utils.util;
import org.apache.log4j.PropertyConfigurator;
import soot.*;
import soot.options.Options;
import soot.shimple.Shimple;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JDK8CallGraph extends BodyTransformer {

    public static HashMap<String, Body> bodies = new HashMap<>();
    public static HashMap<String, SootMethod> methods = new HashMap<>();
    public static HashMap<String, Set<String>> jdkCallGraph = new HashMap<>();

    public static Map<SootMethod, List<SootMethod>> methodToSuccs = new HashMap<>();
    public static Map<SootMethod, Integer> methodToDepth = new HashMap<>();
    public static List<SootMethod> entryMethod = new ArrayList<>();

    public static void main(String[] args) {
        getAllClass();
        readData();
//        buildCallGraph();

        methods.forEach((k, method) -> {
            if(!method.isPrivate()) {
                entryMethod.add(method);
            }
        });

        entryMethod.forEach(k -> util.writeFilelnWithPrefix(k + "", "entryMethod"));

        Queue<SootMethod> queue1 = new ArrayDeque<>(entryMethod);
        Queue<SootMethod> queue2 = new ArrayDeque<>();
        Queue<SootMethod> temp;
        Set<SootMethod> color = new HashSet<>();

        int count_loop = 0;
        while (!queue1.isEmpty()) {
            count_loop += 1;
            while (!queue1.isEmpty()) {
                SootMethod cur = queue1.poll();
                if(color.add(cur)) {
                    if (!methodToDepth.containsKey(cur)) {
                        methodToDepth.put(cur, count_loop);
                    }
                    if (methodToSuccs.containsKey(cur)) {
                        queue2.addAll(methodToSuccs.get(cur));
                    }
                }
            }
            temp = queue1;
            queue1 = queue2;
            queue2 = temp;
        }

        methodToDepth.forEach((k, d) -> util.writeFilelnWithPrefix(k + "\t" + d, "methodToDepth"));
    }

    private static void readData() {
        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/recommend/logs/jdk8CallGraph");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
//            boolean f = true;
            while ((line = br.readLine()) != null) {
//                if(f) {
                    jdkCallGraph.computeIfAbsent(line.split("\t")[0], k -> new HashSet<>()).add(line.split("\t")[1]);
                    if (methods.containsKey(line.split("\t")[0]) && methods.containsKey(line.split("\t")[1])) {
                        methodToSuccs.computeIfAbsent(methods.get(line.split("\t")[0]), k -> new ArrayList<>()).add(methods.get(line.split("\t")[1]));
                    }
//                    f = false;
//                    util.writeFilelnWithPrefix(line, "JDK8CallGraph");
//                }
//                else {
//                    f = true;
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int count0 = 0;
    public static void getAllClass() {
        util.getTime(" begin ");

        setSootClassPath();//设置classpath
        setOptions();//设置soot的选项

        getClassUnderDir();

        util.getTime("to shimple - begin");
        Set<SootClass> classes = ConcurrentHashMap.<SootClass>newKeySet();
        classes.addAll(Scene.v().getClasses());
        util.plnR("class.size() = " + classes.size());
        classes.forEach(eachClass -> {
//                System.out.println(c.toString());
                if (eachClass.isApplicationClass() && eachClass.getMethods() != null) {
                    count0++;
                    eachClass.getMethods().forEach(m -> {
                        try {
                            if (m.getSource() != null) { // 抽象类虚函数
                                if (!m.hasActiveBody()) {
                                    m.retrieveActiveBody();
                                }
                                Body body = m.getActiveBody();
                                body = Shimple.v().newBody(body);
                                m.setActiveBody(body); // 2022-07-19 19:48:32 这个可把我害苦了

                                String parameters = "(";
                                for(Type pt : m.getParameterTypes()) {
                                    parameters = parameters.concat(pt.toString() + ",");
                                }
                                if(parameters.endsWith(",")) {
                                    parameters = parameters.substring(0, parameters.length() - 1);
                                }
                                parameters = parameters + ")";
//                                System.out.println(m.getDeclaringClass() + ":" + m.getName() + parameters);
                                methods.put(m.getDeclaringClass() + ":" + m.getName() + parameters, m);
                                bodies.put(m.toString(), body);
                            }
                            else {
//                                System.err.println(m);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
        });

        util.plnB("count0 = " + count0);
        util.plnB("bodies.size() = " + bodies.size());
    }

    public static void buildCallGraph() {
        util.getTime(" buildCallGraph begin ");

        try (
                FileReader reader = new FileReader("/home/xrbin/Java_Project/java-callgraph-master/target/jdk8callGraph");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                if(line.startsWith("C:")) {
                    System.out.print("");
                }
                else if(line.startsWith("M:")) {
                    try {

                        line = line.replace("M:", "");
                        String m1 = line.split(" ")[0];
                        String m2 = line.split(" ")[1].substring(3);
                        if(methods.containsKey(m1) && methods.containsKey(m2)) {
                            SootMethod sm1 = methods.get(m1);
//                            util.plnB(m1 + "\t" + sm1.toString());

                            String parameters1 = "(";
                            if (sm1.getParameterCount() > 0) {
                                for (Type pt : sm1.getParameterTypes()) {
                                    parameters1 = parameters1.concat(pt.toString() + ",");
                                }
                            }
                            if (parameters1.endsWith(",")) {
                                parameters1 = parameters1.substring(0, parameters1.length() - 1);
                            }
                            parameters1 = parameters1 + ")";
                            String caller = "<" + sm1.getDeclaringClass() + ": " + sm1.getReturnType() + " " + sm1.getName() + parameters1 + ">";

                            SootMethod sm2 = methods.get(m2);
//                            util.plnB(m2 + "\t" + sm2.toString());

                            String parameters2 = "(";
                            if (sm2.getParameterCount() > 0) {
                                for (Type pt : sm2.getParameterTypes()) {
                                    parameters2 = parameters2.concat(pt.toString() + ",");
                                }
                            }
                            if (parameters2.endsWith(",")) {
                                parameters2 = parameters2.substring(0, parameters2.length() - 1);
                            }
                            parameters2 = parameters2 + ")";
                            String callee = "<" + sm2.getDeclaringClass() + ": " + sm2.getReturnType() + " " + sm2.getName() + parameters2 + ">";

                            methodToSuccs.computeIfAbsent(sm1, m -> new ArrayList<>()).add(sm2);
                            util.writeFilelnWithPrefix(caller + "\t" + callee, "jdk8CallGraph");
                            util.writeFilelnWithPrefix(sm1.toString() + "\t" + sm2.toString(), "jdk8CallGraph");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.err.println("\t" + line);
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void internalTransform(Body body, String phaseName, Map<String, String> options) {

    }

    private static void setSootClassPath() {
        Vector<String> jars = new Vector<>();
        jars.add("/home/xrbin/doop-benchmarks/JREs/jre1.8/lib/rt.jar");
        jars.add("/home/xrbin/doop-benchmarks/JREs/jre1.8/lib/jce.jar");
        jars.add("/home/xrbin/doop-benchmarks/JREs/jre1.8/lib/jsse.jar");
        StringBuffer cp = new StringBuffer(".");
        jars.forEach(s -> cp.append(File.pathSeparator).append(s));
        cp.append(File.pathSeparator).append(utils.JARPTAH);
        System.setProperty("soot.class.path", cp.toString());
    }

    private static void getClassUnderDir() {
        Vector<String> jars = new Vector<>();
        jars.add("/home/xrbin/doop-benchmarks/JREs/jre1.8/lib/rt.jar");
        jars.add("/home/xrbin/doop-benchmarks/JREs/jre1.8/lib/jce.jar");
        jars.add("/home/xrbin/doop-benchmarks/JREs/jre1.8/lib/jsse.jar");

        jars.forEach(j -> {
            for (String clzName : SourceLocator.v().getClassesUnder(j)) {
                try {
                    Scene.v().loadClass(clzName, SootClass.BODIES).setApplicationClass();
                } catch (Exception e) {
                    System.err.println("api class: " + clzName + " -- " + e.getMessage());
                }
            }
        });
    }

    private static void setOptions() {

        String log4jConfPath = utils.LOG4JP;
        PropertyConfigurator.configure(log4jConfPath);

//        soot.options.Options.v().set_whole_program(true);
//        soot.options.Options.v().setPhaseOption("cg", "verbose:false");
//        soot.options.Options.v().setPhaseOption("cg", "trim-clinit:true");

//        soot.options.Options.v().set_output_dir("sootOutput");
        soot.options.Options.v().setPhaseOption("jb", "use-original-names:true");
        soot.options.Options.v().setPhaseOption("jb", "model-lambdametafactory:false");
        soot.options.Options.v().set_via_shimple(true);
        soot.options.Options.v().set_output_format(Options.output_format_shimple);

//        soot.options.Options.v().set_full_resolver(true);
        soot.options.Options.v().set_allow_phantom_refs(true);
        soot.options.Options.v().set_src_prec(Options.src_prec_class);
//        soot.options.Options.v().set_prepend_classpath(true);
        soot.options.Options.v().set_keep_line_number(true);
        soot.options.Options.v().set_whole_program(true);
//        soot.options.Options.v().set_process_dir(Collections.singletonList("sootOutput"));
        soot.options.Options.v().set_wrong_staticness(Options.wrong_staticness_ignore);
    }
}
