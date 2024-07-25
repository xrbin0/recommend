package com.xrbin.ptSummary;

import com.xrbin.ddpt.*;
import com.xrbin.ddpt.model.TypeSystem;
import com.xrbin.utils.util;
import org.apache.log4j.PropertyConfigurator;
import soot.*;
import soot.options.Options;
import soot.shimple.Shimple;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class Summary {
    public static String mainMethod = "";
    public static HashMap<String, Body> bodys = new HashMap<>();
    public static HashMap<String, SootMethod> methods = new HashMap<>();
    public static HashSet<String> reachableMethod = new HashSet<>();
    public static HashSet<String> reachableClass = new HashSet<>();
    public static D_FS_PT ddpt;
    public static CallGraph cg;
    public static TypeSystem ts;
    public static WholeProgramCFG wpCFG;
    public static IntroValueFlowGraph ivfg;

    public static void main(String[] args) {

        Summary.run();

        GenDependTree.wpCFG = wpCFG;
        GenDependTree.bodys = bodys;
        GenDependTree.ivfg = ivfg;
        GenDependTree.cg = cg;
        GenDependTree.ts = ts;
        GenDependTree.genDependTree();
        func1();
    }

    public static void run() {
        util.getTime("main - begin");

        setSootClassPath();//设置classpath
        setOptions();//设置soot的选项

        getClassUnderDir();

        utils.ExternalJar.forEach(j -> Scene.v().extendSootClassPath(j));

        Scene.v().loadNecessaryClasses();

        util.getTime("to shimple - begin");
        ts = new TypeSystem();
        Set<SootClass> classes = ConcurrentHashMap.<SootClass>newKeySet();
        classes.addAll(Scene.v().getClasses());
        util.plnR("class.size() = " + classes.size());
        classes.forEach(c -> {
            ts.add(c);
//            System.out.println(c.toString());
            try {
                    c.getMethods().forEach(m -> {
                        try {
                            if (m.getSource() != null) {
                                methods.put(m.toString(), m);
                                if (!m.hasActiveBody()) {
                                    m.retrieveActiveBody();
                                }
                                Body body = m.getActiveBody();
                                body = Shimple.v().newBody(body);
                                m.setActiveBody(body); // 2022-07-19 19:48:32 这个可把我害苦了
                                bodys.put(m.getSignature(), body);
                                if (utils.MAIN) {
                                    if (c.toString().equals(utils.MAINCLASS)
                                            && body.getMethod().toString().contains(Scene.v().getMainClass().toString())
                                            && body.getMethod().isMain()
                                    ) {
                                        mainMethod = body.getMethod().toString();
                                        System.out.println("mainMethod: " + mainMethod);
                                    }
                                }
                                else {
                                    if (body.getMethod().toString().contains(Scene.v().getMainClass().toString()) && body.getMethod().isMain()) {
                                        mainMethod = body.getMethod().toString();
                                        System.out.println("mainMethod: " + mainMethod);
                                    }
                                }

                            }
                            else {
//                            System.err.println(m);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.exit(1);
                        }
                    });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


        ts.run();

//        util.plnR("methodCount.size() = " + bodys.size());
//
//        util.getTime("read data - begin");
//        DatabaseManager.getInstance().readData();
//
//        util.getTime("cg - begin");
//        cg = new CallGraph(bodys, mainMethod);
//        cg.buildCG();
//
//        util.getTime("wpc - begin");
//        wpCFG = new WholeProgramCFG(bodys, mainMethod);
//        wpCFG.buildCFG();
//        cg.createMethodToField();
//
//        ivfg = new IntroValueFlowGraph(wpCFG);
//        util.getTime("ivfg - begin");
//        ivfg.buildVFG();

//        System.exit(1);
//        util.getTime("analysis - begin");
//        ddpt = new D_FS_PT(wpCFG, ivfg);
//        util.getTime("analysis - end");

//        AllJimpleStmts.bodys = bodys;
//        AllJimpleStmts.reachableMethod = reachableMethod;
//        AllJimpleStmts.main(new String[0]);
    }

    private static void setSootClassPath() {
        StringBuffer cp = new StringBuffer(".");
        utils.ExternalJar.forEach(s -> cp.append(File.pathSeparator).append(s));
        cp.append(File.pathSeparator).append(utils.JARPTAH);
        System.setProperty("soot.class.path", cp.toString());
    }

    private static void getClassUnderDir() {
//        for (String clzName : SourceLocator.v().getClassesUnder(utils.JARPTAH)) {
////            System.err.println("api class: " + clzName);
////            Scene.v().getSootClass(clzName).setApplicationClass();
//            try {
//                Scene.v().loadClass(clzName, SootClass.BODIES).setApplicationClass();
////                System.out.println("api class: " + clzName);
//            } catch (Exception e) {
//                System.err.println("api class: " + clzName + " -- " + e.getMessage());
//            }
//        }

        utils.ExternalJar.forEach(ej -> {
            for (String clzName : SourceLocator.v().getClassesUnder(ej)) {
//            System.err.println("api class: " + clzName);
//            Scene.v().getSootClass(clzName).setApplicationClass();
                try {
                    Scene.v().loadClass(clzName, SootClass.BODIES).setApplicationClass();
//                System.out.println("api class: " + clzName);
                } catch (Exception e) {
                    System.err.println("api class: " + clzName + " -- " + e.getMessage());
                }
            }
        });
    }

    private static void setApplicationClass() {
        for (SootClass sc : Scene.v().getClasses()) {
            try {
                if (!sc.toString().contains("java.lang.OutOfMemoryError")) {
                    sc.setApplicationClass();
                }
//                System.out.println("api class: " + clzName);
            } catch (Exception e) {
                System.err.println("api class: " + sc + " -- " + e.getMessage());
            }
        }
    }

    private static void setOptions() {

        String log4jConfPath = utils.LOG4JP;
        PropertyConfigurator.configure(log4jConfPath);

//        soot.options.Options.v().set_whole_program(true);

//        soot.options.Options.v().set_main_class("Main6.Main6");
        soot.options.Options.v().set_main_class(utils.MAINCLASS);
//        soot.options.Options.v().set_main_class("com.qianxinyao.analysis.jieba.keyword.TFIDFAnalyzer");
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


//        try (
//                FileReader reader = new FileReader(utils.DATABASE + "Reachable.csv");
//                BufferedReader br = new BufferedReader(reader)
//        ) {
//            String line;
//            while ((line = br.readLine()) != null) {
////                util.plnB("------ reachableMethod ------" + line);
//                reachableMethod.add(line);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try (
//                FileReader reader = new FileReader(utils.DATABASE + "ReachableClass.csv");
//                BufferedReader br = new BufferedReader(reader)
//        ) {
//            String line;
//            while ((line = br.readLine()) != null) {
////                util.plnB("------ reachableMethod ------" + line);
//                reachableClass.add(line);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


    public static void func1() {
        Vector<Integer> vector = new Vector<>();

        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/recommend/logs/dependSize");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                vector.add(Integer.parseInt(line));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/recommend/logs/dependSizeIndexMap");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String method = line.split("\t")[0];
                String index = line.split("\t")[1];
                if(vector.elementAt(Integer.parseInt(index)) > 100000) {
                    util.writeFilelnWithPrefix(method, "method6144");
                }
                if(vector.elementAt(Integer.parseInt(index)) > 1000000) {
                    util.writeFilelnWithPrefix(method, "method6144");
                    util.writeFilelnWithPrefix(method, "method2048");
                }
                if(vector.elementAt(Integer.parseInt(index)) > 10000000) {
                    util.writeFilelnWithPrefix(method, "method6144");
                    util.writeFilelnWithPrefix(method, "method2048");
                    util.writeFilelnWithPrefix(method, "method1024");
                    util.writeFilelnWithPrefix(method, "method512");
                }
                if(vector.elementAt(Integer.parseInt(index)) > 100000000) {
                    util.writeFilelnWithPrefix(method, "method6144");
                    util.writeFilelnWithPrefix(method, "method2048");
                    util.writeFilelnWithPrefix(method, "method512");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
