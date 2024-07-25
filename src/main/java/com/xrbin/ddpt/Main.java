package com.xrbin.ddpt;

import com.xrbin.ddpt.model.DatabaseManager;
import com.xrbin.ddpt.model.TypeSystem;
import com.xrbin.utils.AllJimpleStmts;
import com.xrbin.utils.util;

import soot.*;
import soot.BodyTransformer;
import soot.Unit;
import soot.options.Options;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.PropertyConfigurator;
import soot.shimple.Shimple;

import static java.lang.Thread.sleep;


/**
 * @author xrbin0@163.com
 * @date 3/22/21
 */

public class Main extends BodyTransformer {
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
        run();
    }

    public static void run() {
//        String s1 = "213dfshgcvgfj";
//        String s2 = "213dfshgcvgfj";
//        System.out.println(s1.hashCode());
//        System.out.println(s2.hashCode());
//        HashSet<String> aa = new HashSet<>();
//        aa.add(s1);
//        aa.add(s2);
//        for(String s : aa) {
//            System.out.println(s);
//        }
//        // System.exit(-1);

        util.getTime("main - begin");

        setSootClassPath();//设置classpath
        setOptions();//设置soot的选项

        Main s = new Main();
        s.getClassUnderDir();

        utils.ExternalJar.forEach(j -> {
            Scene.v().extendSootClassPath(j);
        });

//        for (String extraClass : utils.ExternalJar) {
//            System.out.println("Marking class to resolve: " + extraClass);
//            Scene.v().addBasicClass(extraClass, SootClass.BODIES);
//        }

        Scene.v().loadNecessaryClasses();

//        setApplicationClass();

//        PackManager.v().getPack("stp").add(new Transform("stp.Main", s));
//        PackManager.v().runPacks();

        HashSet<String> noReachMethod = new HashSet<>(reachableMethod);

        util.getTime("to shimple - begin");
        ts = new TypeSystem();
        Set<SootClass> classes = ConcurrentHashMap.<SootClass>newKeySet();
        classes.addAll(Scene.v().getClasses());
        util.plnR("class.size() = " + classes.size());
        classes.forEach(c -> {
            if (reachableClass.contains(c.toString())) {
                ts.add(c);
//                System.out.println(c.toString());
                c.getMethods().forEach(m -> {
                    if (reachableMethod.contains(m.toString())) {
                        try {
                            if (m.getSource() != null) {
                                methods.put(m.toString(), m);
                                if (!m.hasActiveBody()) {
                                    m.retrieveActiveBody();
                                }
//                            if (m.getSource() != null && m.getSource().getBody(m, "stp") != null) {
//                                util.getTime(c.getMethods().toString() + "to shimple - 1");
//                                Body body = m.getSource().getBody(m, "stp");
//                            util.getTime(c.getMethods().toString() + "to shimple - 1");
                                Body body = m.getActiveBody();
//                            util.getTime(c.getMethods().toString() + "to shimple - 2");
                                body = Shimple.v().newBody(body);
//                            util.getTime(c.getMethods().toString() + "to shimple - 3");
                                m.setActiveBody(body); // 2022-07-19 19:48:32 这个可把我害苦了
//                            util.getTime(c.getMethods().toString() + "to shimple - 4");
                                if (reachableMethod.contains(body.getMethod().toString())) {
                                    bodys.put(m.getSignature(), body);
                                    noReachMethod.remove(m.toString());
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
                            }
                            else {
//                            System.err.println(m);
                            }
//                            util.getTime(c.getMethods().toString() + "to shimple - 5");
//                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.exit(1);
                        }
                    }
                });
            }
        });

        ts.run();
        noReachMethod.forEach(m -> {
            util.writeFilelnWithPrefix(m, "noReachMethod-jieba");
        });

        util.plnR("methodCount.size() = " + bodys.size());

        util.getTime("read data - begin");
        DatabaseManager.getInstance().readData();

        util.getTime("cg - begin");
        cg = new CallGraph(bodys, mainMethod);
        cg.buildCG();

        util.getTime("wpc - begin");
        wpCFG = new WholeProgramCFG(bodys, mainMethod);
        wpCFG.buildCFG();
        cg.createMethodToField();

//        AllJimpleStmts.main(wpCFG.getUnits());
//        System.exit(0);

//        Stack<Unit> stack = new Stack<>();
//        HashSet<Unit> flag = new HashSet<>();
//        HashSet<SootMethod> flagM = new HashSet<>();
//        stack.push(c.heads.get(0));
//        while(!stack.empty()) {
//            Unit u = stack.pop();
//            if(flag.add(u)) {
//                c.getSuccsOf(u).forEach(succ -> {
//                    stack.push(u);
//                });
//                if(flagM.add(c.getMethodOf(u))) {
//                    System.out.println(c.getMethodOf(u));
//                }
//            }
//            System.out.println(c.getMethodOf(u));
//        }
//        if (true) return;

        ivfg = new IntroValueFlowGraph(wpCFG);
        util.getTime("ivfg - begin");
        ivfg.buildVFG();
        if (true) return;

//        System.exit(1);
//        util.getTime("analysis - begin");
        ddpt = new D_FS_PT(wpCFG, ivfg);
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

    private void getClassUnderDir() {
        for (String clzName : SourceLocator.v().getClassesUnder(utils.JARPTAH)) {
//            System.err.println("api class: " + clzName);
//            Scene.v().getSootClass(clzName).setApplicationClass();
            try {
                Scene.v().loadClass(clzName, SootClass.BODIES).setApplicationClass();
//                System.out.println("api class: " + clzName);
            } catch (Exception e) {
                System.err.println("api class: " + clzName + " -- " + e.getMessage());
            }
        }

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


        try (
                FileReader reader = new FileReader(utils.DATABASE + "Reachable.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
//                util.plnB("------ reachableMethod ------" + line);
                reachableMethod.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (
                FileReader reader = new FileReader(utils.DATABASE + "ReachableClass.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
//                util.plnB("------ reachableMethod ------" + line);
                reachableClass.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void internalTransform(Body body,
                                     String phaseName,
                                     Map<String, String> options) {

        if (body.getMethod().toString().contains("<com.xrbin.ddptTest.test.test1: void main(java.lang.String[])>") && body.getMethod().toString().contains("test1")) {
//            utils.writeFileln("---- " + body.getMethod().toString(), StaticData.DEBUGOUTPUTFILE);
            for (Unit u : body.getUnits()) {
//                utils.writeFileln("\t---- " + u.toString(), StaticData.DEBUGOUTPUTFILE);
//                System.out.println(u);
            }
        }
//        for (Unit u : body.getUnits()) {
//            if(u instanceof AssignStmt && u.toString().contains("null")) {
//                System.out.println(((AssignStmt) u).getRightOp().getClass());
//                System.out.println(((AssignStmt) u).getRightOp().getType());
//            }
//        }
//        if (body.getMethod().toString().contains("com.xrbin.ddptTest")) {
//            System.out.println(body.getMethod());
//        }
        if (reachableMethod.contains(body.getMethod().toString())) {
            bodys.put(body.getMethod().toString(), body);
//            if (body.getMethod().toString().contains(Scene.v().getMainClass().toString()) && body.getMethod().isMain()) {
//                mainMethod = body.getMethod().toString();
//                System.out.println("mainMethod: " + mainMethod);
//            }
        }

    }
}
