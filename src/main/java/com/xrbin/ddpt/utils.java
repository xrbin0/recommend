package com.xrbin.ddpt;

import com.xrbin.ddpt.model.DatabaseManager;
import com.xrbin.utils.util;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.tagkit.LineNumberTag;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class utils {
    public final static String CONCAT = "--utils.CONCAT--";

    public final static String MEHTODINVOCATIONLINE = "MethodInvocation-Line.csv";
    public final static String INSENVPT = "Stats_Simple_InsensVarPointsTo.csv";
    public final static String ASSIGNHEAPALLOCATION = "AssignHeapAllocation.csv";
//    public final static String APPMETHOD = "ApplicationMethod.csv";
    public final static String APPMETHOD = "ReachableApp.csv";
    public final static String CTXRESPONSE = "ContextResponse.csv";
    public final static String IFPT = "InstanceFieldPointsTo.csv";
    public final static String AINSENVPT = "VarPointsToAPP.csv";
    public final static String SFPT = "StaticFieldPointsTo.csv";
    public final static String CALLGRAGH = "CallGraphEdge.csv";
    public final static String LOG4JP = "src/log4j.properties";
    public final static String DEBUGOUTPUTFILE = "logs/output";
    public final static String VPT = "VarPointsTo.csv";
    public final static String AVPT = "AVPT.csv";

    public static String PATH; //
    public static String JARPTAH;
    public static String MAINCLASS;
    public static String DATABASE;

    public final static String DOOPWORKPLACE = "/home/xrbin/Desktop/yanniss-doop-r-4.24.10/";

    
    public static int DEBUG = 0;
    public static int RUN = 1;
    public static int modle = DEBUG;
    public static boolean FILE = false;
    public static boolean TEST2FILE = false;

    // output
    public static boolean STAT = false;
    public static boolean CLINIT = true;
    public static boolean USEDEF = false;
    public static boolean WPCFG = false;
    public static boolean HIDENMETHOD = false;
    public static boolean BUILDVFG = false;
    public static boolean BACKANALYSIS = false;
    public static boolean WORKLISTADD = false;
    public static boolean ANALYZE = false;
    public static boolean STRONGUPDATE = false;
    public static boolean RESULT = false;

    public static boolean HIDENMETHODS = true;
    public static boolean BUILDVFGACCURATE = false;


    public static HashSet<String> methods = new HashSet<>();
    public static ArrayList<String> ExternalJar = new ArrayList<>();

    public static String varNameDoopToSrc(String varName) {
//        System.out.println(varName);
        if (varName.contains("$$A_")) {
            varName = varName.split("\\$\\$A_")[0] + varName.split("\\$\\$A_")[1];
        }
        if (varName.contains("#_")) {
            varName = varName.split("#_")[0];
        }
        return varName;
    }

    public static String varNameDoopToShimple(String varName) {
//        System.out.println(varName);
        if (varName.contains("$$A_")) {
            varName = varName.split("\\$\\$A_")[0] + varName.split("\\$\\$A_")[1];
        }
        if (varName.contains("#_")) {
            varName = varName.split("#_")[0];
        }
        return varName;
    }

    public static String varNameToDoopName(SootMethod m, String varName) {
        return m.toString() + "/" + varName;
    }

    public static String varNameToDoopName(String m, String varName) {
        return m + "/" + varName;
    }

    public static int getLineNumber(Unit u) {
        if (u.hasTag("LineNumberTag")) {
            LineNumberTag tag = (LineNumberTag) u.getTag(("LineNumberTag"));
            if (tag != null) {
                return tag.getLineNumber();
            }
        }
        // System.exit(-1);
        return -1;
    }

    public static boolean isPrimitiveType(Type t) {
        return t.toString().equals("int") ||
        t.toString().equals("byte") ||
        t.toString().equals("long") ||
        t.toString().equals("char") ||
        t.toString().equals("float") ||
        t.toString().equals("short") ||
        t.toString().equals("double") ||
        t.toString().equals("boolean");
    }

    public static boolean isConstantAllocation(String a) {
        return a.equals("<<null pseudo heap>>") ||
                a.equals("<<string-builder>>") ||
                a.equals("<<string-buffer>>") ||
                a.equals("<<string-constant>>");
    }

    public static boolean isHardMethod(SootMethod m) {
        boolean res = false;

        if (methods.contains(m.toString())) {
            res = true;
        }

        if (!DatabaseManager.getInstance().appMethod.contains(m.toString())) {
            res = true;
        }

        if (!HIDENMETHODS) {
            res = false;
        }
        res = false;

        return res;

    }

    public static void randomMVPT() {
        try (
                FileReader reader = new FileReader(utils.DOOPWORKPLACE + "/out/all/database/AVPT.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            int i = 0;
            String line;
            while ((line = br.readLine()) != null) {
                String[] a = line.split("\t");
                a[0] = DatabaseManager.rmBrackets(a[0]);
                String ctx1 = DatabaseManager.rmBrackets(a[2]).split(", ")[0];
                String ctx2 = DatabaseManager.rmBrackets(a[2]).split(", ")[1];
                if(i % 15 == 0) {
                    System.out.println(a[0] + "\t" + a[1] + "\t" + ctx1 + "\t" + ctx2 + "\t" + a[3]);
                    util.writeFileln(a[0] + "\t" + a[1] + "\t" + ctx1 + "\t" + ctx2 + "\t" + a[3], utils.DOOPWORKPLACE + "/out/all/database/MVPT.facts");
                }
                i++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    static {
        try {
            Runtime.getRuntime().exec("rm logs/HidenMethodFieldProcess");
            Runtime.getRuntime().exec("rm logs/hidenMethodField");
            Runtime.getRuntime().exec("rm logs/recommendVar_v1");
            Runtime.getRuntime().exec("rm logs/hidenAppMethod");
            Runtime.getRuntime().exec("rm logs/methodToCtx");
//            Runtime.getRuntime().exec("rm logs/TestForTest");
            Runtime.getRuntime().exec("rm logs/invoke");
            Runtime.getRuntime().exec("rm logs/output");
            Runtime.getRuntime().exec("rm logs/ivfg");
        } catch (Exception e) {
            System.err.print(e.toString());
        }

        // ./doop -i /home/xrbin/Desktop/Java_Project/DDPT_test/build/libs/cflrHF.jar  -a 2-object-sensitive+heap --generate-jimple --id cflrHF --main com.xrbin.ddptTest.cflrBackAnalysis.cflrHidenField
        {

//            PATH = utils.DOOPWORKPLACE + "/out/all/";
//            JARPTAH = "/home/xrbin/Desktop/Java_Project/DDPT_test/build/libs/all.jar";
//            MAINCLASS = "com.xrbin.ddptTest.test.test1";

//            PATH = utils.DOOPWORKPLACE + "/out/fop/";
//            JARPTAH = "/home/xrbin/Desktop/Java_Project/fop/build/fop.jar";
//            MAINCLASS = "org.apache.fop.cli.Main";

//            PATH = utils.DOOPWORKPLACE + "/out/jieba/";
//            JARPTAH = "/home/xrbin/Desktop/Java_Project/jieba-analysis-master/target/jieba-analysis-1.0.3-SNAPSHOT.jar";
//            MAINCLASS = "com.qianxinyao.analysis.jieba.keyword.TFIDFAnalyzer";

//            PATH = utils.DOOPWORKPLACE + "/out/cflrHF/";
//            JARPTAH = "/home/xrbin/Desktop/Java_Project/DDPT_test/build/libs/cflrHF.jar";
//            MAINCLASS = "com.xrbin.ddptTest.cflrHidenField.test1";

//            PATH = utils.DOOPWORKPLACE + "/out/all/";
//            JARPTAH = "/home/xrbin/Desktop/Java_Project/DDPT_test/build/libs/all.jar";
//            MAINCLASS = "com.xrbin.ddptTest.test.test1";

//            PATH = utils.DOOPWORKPLACE + "/out/func20/";
//            JARPTAH = "/home/xrbin/Desktop/Java_Project/DDPT_test/build/libs/func20.jar";
//            MAINCLASS = "com.xrbin.ddptTest.test.test1";

//            PATH = utils.DOOPWORKPLACE + "/out/clinit/";
//            JARPTAH = "/home/xrbin/Desktop/Java_Project/DDPT_test/build/libs/clinit.jar";
//            MAINCLASS = "com.xrbin.ddptTest.test.test1";


            DATABASE = PATH + "/database/";
            DATABASE = PATH + "/facts/";
        }

        {
//            StaticData.methods.add("<java.util.Map: java.util.Set keySet()>");
//            StaticData.methods.add("<java.util.HashMap: java.util.Set keySet()>");
//            StaticData.methods.add("<java.util.HashMap: java.lang.Object get(java.lang.Object)>");
//            StaticData.methods.add("<java.lang.String: java.lang.String trim()>");
//            StaticData.methods.add("<java.lang.String: java.lang.String[] split(java.lang.String)>");
//            StaticData.methods.add("<java.lang.Double: java.lang.Double valueOf(double)>");
//            StaticData.methods.add("<java.util.List: java.lang.Object get(int)>");
//            StaticData.methods.add("<java.util.Map: java.lang.Object get(java.lang.Object)>");
//            StaticData.methods.add("<java.util.ArrayList: java.lang.Object[] elementData>");
//            StaticData.methods.add("<java.util.HashMap: java.util.HashMap$Node getNode(int,java.lang.Object)>");
//            StaticData.methods.add("<java.util.Hashtable: java.lang.Object get(java.lang.Object)>");
//            StaticData.methods.add("");
//            StaticData.methods.add("");
//            StaticData.methods.add("");
//            StaticData.methods.add("");
//            StaticData.methods.add("<java.util.ArrayList$Itr: boolean hasNext()>");
//            StaticData.methods.add("<java.util.Iterator: java.lang.Object next()>");
            methods.add("<com.xrbin.ddptTest.cflrHidenField.test1: void func1()>");
            methods.add("<com.xrbin.ddptTest.test.ForFunc20: java.lang.Object func(java.lang.Object,java.lang.Object,java.lang.Object)>");
        }

        {
            ExternalJar.add("/home/xrbin/Desktop/doop-benchmarks/JREs/jre1.8/lib/rt.jar");
            ExternalJar.add("/home/xrbin/Desktop/doop-benchmarks/JREs/jre1.8/lib/jce.jar");
            ExternalJar.add("/home/xrbin/Desktop/doop-benchmarks/JREs/jre1.8/lib/jsse.jar");

            ExternalJar.add("/home/xrbin/Desktop/Java_Project/fop/lib/build/xmlunit-1.2.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/fop/lib/build/qdox-1.12.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/fop/lib/build/pmd-4.2.5.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/fop/lib/build/objenesis-1.0.0.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/fop/lib/build/mockito-core-1.8.5.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/fop/lib/build/jaxen-1.1.1.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/fop/lib/build/hamcrest.core-1.1.0.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/fop/lib/build/asm-3.1.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/fop/lib/xmlgraphics-commons-1.5.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/fop/lib/xml-apis-ext-1.3.04.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/fop/lib/xml-apis-1.3.04.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/fop/lib/xercesImpl-2.7.1.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/fop/lib/xalan-2.7.0.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/fop/lib/servlet-2.2.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/fop/lib/serializer-2.7.0.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/fop/lib/commons-logging-1.0.4.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/fop/lib/commons-io-1.3.1.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/fop/lib/batik-all-1.7.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/fop/lib/avalon-framework-4.2.0.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/fop/build/fop.war");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/fop/build/fop-hyph.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/fop/build/fop-sandbox.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/fop/build/fop-transcoder.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/fop/build/fop-transcoder-allinone.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/apache-ant/ant.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/apache-ant/ant-antlr.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/apache-ant/ant-apache-bcel.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/apache-ant/ant-apache-bsf.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/apache-ant/ant-apache-log4j.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/apache-ant/ant-apache-oro.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/apache-ant/ant-apache-regexp.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/apache-ant/ant-apache-resolver.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/apache-ant/ant-apache-xalan2.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/apache-ant/ant-commons-logging.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/apache-ant/ant-commons-net.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/apache-ant/ant-imageio.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/apache-ant/ant-jai.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/apache-ant/ant-javamail.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/apache-ant/ant-jdepend.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/apache-ant/ant-jmf.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/apache-ant/ant-jsch.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/apache-ant/ant-junit.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/apache-ant/ant-junit4.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/apache-ant/ant-junitlauncher.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/apache-ant/ant-launcher.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/apache-ant/ant-netrexx.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/apache-ant/ant-swing.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/apache-ant/ant-testutil.jar");
            ExternalJar.add("/home/xrbin/Desktop/Java_Project/apache-ant/ant-xz.jar");

            ExternalJar.add("/home/xrbin/Desktop/DDPT/libs/soot-4.2.1-jar-with-dependencies.jar");
            ExternalJar.add("/home/xrbin/.m2/repository/javax/annotation/javax.annotation-api/1.3.2/javax.annotation-api-1.3.2.jar");
            ExternalJar.add("/home/xrbin/.m2/repository/log4j/log4j/1.2.17/log4j-1.2.17.jar");
            ExternalJar.add("/home/xrbin/.m2/repository/ca/mcgill/sable/polyglot/2006/polyglot-2006.jar");
            ExternalJar.add("/home/xrbin/.m2/repository/ca/mcgill/sable/jasmin/3.0.2/jasmin-3.0.2.jar");
            ExternalJar.add("/home/xrbin/.gradle/caches/modules-2/files-2.1/org.slf4j/slf4j-api/1.7.21/139535a69a4239db087de9bab0bee568bf8e0b70/slf4j-api-1.7.21.jar");
            ExternalJar.add("/home/xrbin/.gradle/caches/modules-2/files-2.1/org.jetbrains/annotations/19.0.0/efbff6752f67a7c9de3e4251c086a88e23591dfd/annotations-19.0.0.jar");
        }
    }
}
