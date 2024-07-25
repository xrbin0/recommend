import com.xrbin.ddpt.Main;
import com.xrbin.ddpt.Query;
import com.xrbin.ddpt.model.Allocation;
import com.xrbin.ddpt.model.CSAllocation;
import com.xrbin.ddpt.model.DatabaseManager;
import com.xrbin.ddpt.model.LocatePointer;
import com.xrbin.ddpt.utils;
import com.xrbin.utils.util;
import soot.Local;
import soot.jimple.AssignStmt;

import java.util.HashMap;
import java.util.HashSet;

public class TestBasic {
    public static void main(String[] args) {
        utils.CLINIT = true;
        utils.STAT = true;

        utils.USEDEF = false;
        utils.WPCFG = false;
        utils.HIDENMETHOD = false;
        utils.BUILDVFG = false;
        utils.BACKANALYSIS = false;
        utils.WORKLISTADD = false;
        utils.ANALYZE = false;
        utils.STRONGUPDATE = false;
        utils.RESULT = false;

        utils.JARPTAH = "/home/xrbin/Java_Project/DDPT_test/build/libs/all.jar";
        utils.MAINCLASS = "com.xrbin.ddptTest.test.test1";
        utils.DATABASE = utils.DOOPWORKPLACE + "/out/all/" + "/database/";

        Main.main(args);
        answersInit();
        testAllBasic();

        util.pln("");
        answers.forEach((k, v1) -> {
            HashSet<Allocation> v2 = standardAnswers.get(k);
            v1.forEach(o -> {
                if(!v2.contains(o)) {
                    util.plnR(o.toString());
                }
            });
            v2.forEach(o -> {
                if(!v1.contains(o)) {
                    util.plnBG(o.toString());
                }
            });
        });
    }

    private static void testAllBasic() {
        query("<com.xrbin.ddptTest.test.test1: void func0()>/a");
        query("<com.xrbin.ddptTest.test.test1: void func1()>/a");
        query("<com.xrbin.ddptTest.test.test1: void func2()>/l5");
        query("<com.xrbin.ddptTest.test.test1: void func3()>/a");
        query("<com.xrbin.ddptTest.test.test1: void func4()>/a");
        query("<com.xrbin.ddptTest.test.test1: void func5()>/a_2");
        query("<com.xrbin.ddptTest.test.test1: void func6()>/a");
        query("<com.xrbin.ddptTest.test.test1: void func7()>/o2");
        query("<com.xrbin.ddptTest.test.test1: void func8()>/id2");
        query("<com.xrbin.ddptTest.test.test1: void func10()>/a");
        query("<com.xrbin.ddptTest.test.test1: void func11()>/a");
        query("<com.xrbin.ddptTest.test.test1: void func20()>/o3");
        query("<com.xrbin.ddptTest.test.test1: void func20()>/o4");
        query("<com.xrbin.ddptTest.test.clinit: void func0()>/a_2");
        query("<com.xrbin.ddptTest.test.clinit: void func1()>/a");
        query("<com.xrbin.ddptTest.cflrBackAnalysis.test1: void func2(java.lang.Object)>/oooo");
        query("<com.xrbin.ddptTest.cflrHidenField.test1: void func0()>/a4");
        query("<com.xrbin.ddptTest.cflrHidenField.test1: void func0()>/a5");
    }

    public static void query(String query) {
        if (query.equals("")) return;

        LocatePointer l = Query.find(Main.wpCFG, query);
        if (l == null) return;

        HashSet<CSAllocation> objs = Main.ddpt.query(l);
        HashSet<Allocation> insenObjs = new HashSet<>();
        objs.forEach(o -> insenObjs.add(o.getO()));

        System.out.println("\n" + l);
        objs.forEach(o -> System.out.println("\t" + o.getO()));

        util.plnG("\n" + l);
        insenObjs.forEach(o -> util.plnG("\t" + o.getO()));

        String var = Main.wpCFG.getMethodOf(l.getU()) + "/" + ((Local) ((AssignStmt) l.getU()).getLeftOp()).getName();
        if (DatabaseManager.getInstance().vptInsen.containsKey(var)) {
            DatabaseManager.getInstance().vptInsen.get(var).forEach(o -> util.plnBG("\t" + o.getO()));
            insenObjs.forEach(o -> {
                if (!DatabaseManager.getInstance().vptInsen.get(var).contains(o)) {
                    util.plnR("\t\t" + o.getO());
                }
            });
//            DatabaseManager.getInstance().isenVpt.get(var).forEach(o -> {
//                if (!insenObjs.contains(o)) {
//                    util.plnY("\t\t" + o.getO());
//                }
//            });
        }

        TestBasic.answers.computeIfAbsent(query, k -> new HashSet<>());
        insenObjs.forEach(o -> TestBasic.answers.computeIfAbsent(query, k -> new HashSet<>()).add(o));
    }

    public static HashMap<String, HashSet<Allocation>> answers = new HashMap<>();
    public static HashMap<String, HashSet<Allocation>> standardAnswers = new HashMap<>();
    public static void answersInit() {
        standardAnswers.computeIfAbsent("<com.xrbin.ddptTest.test.test1: void func0()>/a", k -> new HashSet<>()).add(new Allocation("<com.xrbin.ddptTest.test.test1: void func0()>/new com.xrbin.ddptTest.test.A/1"));
        standardAnswers.computeIfAbsent("<com.xrbin.ddptTest.test.test1: void func1()>/a", k -> new HashSet<>()).add(new Allocation("<com.xrbin.ddptTest.test.test1: void func1()>/new com.xrbin.ddptTest.test.A/1"));
        standardAnswers.computeIfAbsent("<com.xrbin.ddptTest.test.test1: void func1()>/a", k -> new HashSet<>()).add(new Allocation("<com.xrbin.ddptTest.test.test1: void func1()>/new com.xrbin.ddptTest.test.A/2"));
        standardAnswers.computeIfAbsent("<com.xrbin.ddptTest.test.test1: void func2()>/l5", k -> new HashSet<>()).add(new Allocation("<com.xrbin.ddptTest.test.test1: void func2()>/new com.xrbin.ddptTest.test.A/1"));
        standardAnswers.computeIfAbsent("<com.xrbin.ddptTest.test.test1: void func3()>/a", k -> new HashSet<>()).add(new Allocation("<com.xrbin.ddptTest.test.test1: void func3()>/new com.xrbin.ddptTest.test.A/2"));
        standardAnswers.computeIfAbsent("<com.xrbin.ddptTest.test.test1: void func3()>/a", k -> new HashSet<>()).add(new Allocation("<com.xrbin.ddptTest.test.test1: void func3()>/new com.xrbin.ddptTest.test.A/1"));
        standardAnswers.computeIfAbsent("<com.xrbin.ddptTest.test.test1: void func4()>/a", k -> new HashSet<>()).add(new Allocation("<com.xrbin.ddptTest.test.test1: void func4()>/new com.xrbin.ddptTest.test.A/2"));
        standardAnswers.computeIfAbsent("<com.xrbin.ddptTest.test.test1: void func4()>/a", k -> new HashSet<>()).add(new Allocation("<com.xrbin.ddptTest.test.test1: void func4()>/new com.xrbin.ddptTest.test.A/1"));
        standardAnswers.computeIfAbsent("<com.xrbin.ddptTest.test.test1: void func5()>/a_2", k -> new HashSet<>()).add(new Allocation("<com.xrbin.ddptTest.test.test1: void func5()>/new com.xrbin.ddptTest.test.A/2"));
        standardAnswers.computeIfAbsent("<com.xrbin.ddptTest.test.test1: void func6()>/a", k -> new HashSet<>()).add(new Allocation("<com.xrbin.ddptTest.test.test1: void func6()>/new com.xrbin.ddptTest.test.A/1"));
        standardAnswers.computeIfAbsent("<com.xrbin.ddptTest.test.test1: void func6()>/a", k -> new HashSet<>()).add(new Allocation("<com.xrbin.ddptTest.test.test1: void func6()>/new com.xrbin.ddptTest.test.A/2"));
        standardAnswers.computeIfAbsent("<com.xrbin.ddptTest.test.test1: void func7()>/o2", k -> new HashSet<>()).add(new Allocation("<com.xrbin.ddptTest.test.test1: void func7()>/new java.lang.String/1"));
        standardAnswers.computeIfAbsent("<com.xrbin.ddptTest.test.test1: void func8()>/id2", k -> new HashSet<>()).add(new Allocation("<com.xrbin.ddptTest.test.test1: void func8()>/new java.lang.String/1"));
        standardAnswers.computeIfAbsent("<com.xrbin.ddptTest.test.test1: void func10()>/a", k -> new HashSet<>()).add(new Allocation("<com.xrbin.ddptTest.test.test1: void func12()>/new com.xrbin.ddptTest.test.A/0"));
        standardAnswers.computeIfAbsent("<com.xrbin.ddptTest.test.test1: void func10()>/a", k -> new HashSet<>()).add(new Allocation("<com.xrbin.ddptTest.test.test1: void func10()>/new com.xrbin.ddptTest.test.A/1"));
        standardAnswers.computeIfAbsent("<com.xrbin.ddptTest.test.test1: void func10()>/a", k -> new HashSet<>()).add(new Allocation("<com.xrbin.ddptTest.test.test1: void func11()>/new com.xrbin.ddptTest.test.A/1"));
        standardAnswers.computeIfAbsent("<com.xrbin.ddptTest.test.test1: void func10()>/a", k -> new HashSet<>()).add(new Allocation("<com.xrbin.ddptTest.test.test1: void func12()>/new com.xrbin.ddptTest.test.A/1"));
        standardAnswers.computeIfAbsent("<com.xrbin.ddptTest.test.test1: void func10()>/a", k -> new HashSet<>()).add(new Allocation("<com.xrbin.ddptTest.test.test1: void func11()>/new com.xrbin.ddptTest.test.A/0"));
        standardAnswers.computeIfAbsent("<com.xrbin.ddptTest.test.test1: void func10()>/a", k -> new HashSet<>()).add(new Allocation("<com.xrbin.ddptTest.test.test1: void func10()>/new com.xrbin.ddptTest.test.A/0"));
        standardAnswers.computeIfAbsent("<com.xrbin.ddptTest.test.test1: void func11()>/a", k -> new HashSet<>()).add(new Allocation("<com.xrbin.ddptTest.test.test1: void func12()>/new com.xrbin.ddptTest.test.A/0"));
        standardAnswers.computeIfAbsent("<com.xrbin.ddptTest.test.test1: void func11()>/a", k -> new HashSet<>()).add(new Allocation("<com.xrbin.ddptTest.test.test1: void func10()>/new com.xrbin.ddptTest.test.A/1"));
        standardAnswers.computeIfAbsent("<com.xrbin.ddptTest.test.test1: void func11()>/a", k -> new HashSet<>()).add(new Allocation("<com.xrbin.ddptTest.test.test1: void func11()>/new com.xrbin.ddptTest.test.A/1"));
        standardAnswers.computeIfAbsent("<com.xrbin.ddptTest.test.test1: void func11()>/a", k -> new HashSet<>()).add(new Allocation("<com.xrbin.ddptTest.test.test1: void func12()>/new com.xrbin.ddptTest.test.A/1"));
        standardAnswers.computeIfAbsent("<com.xrbin.ddptTest.test.test1: void func11()>/a", k -> new HashSet<>()).add(new Allocation("<com.xrbin.ddptTest.test.test1: void func12()>/new com.xrbin.ddptTest.test.A/2"));
        standardAnswers.computeIfAbsent("<com.xrbin.ddptTest.test.test1: void func11()>/a", k -> new HashSet<>()).add(new Allocation("<com.xrbin.ddptTest.test.test1: void func11()>/new com.xrbin.ddptTest.test.A/0"));
        standardAnswers.computeIfAbsent("<com.xrbin.ddptTest.test.test1: void func11()>/a", k -> new HashSet<>()).add(new Allocation("<com.xrbin.ddptTest.test.test1: void func10()>/new com.xrbin.ddptTest.test.A/0"));
        standardAnswers.computeIfAbsent("<com.xrbin.ddptTest.test.test1: void func11()>/a", k -> new HashSet<>()).add(new Allocation("<com.xrbin.ddptTest.test.test1: void func11()>/new com.xrbin.ddptTest.test.A/2"));
        standardAnswers.computeIfAbsent("<com.xrbin.ddptTest.test.test1: void func20()>/o3", k -> new HashSet<>()).add(new Allocation("<com.xrbin.ddptTest.test.test1: void func20()>/new java.lang.Object/2"));
        standardAnswers.computeIfAbsent("<com.xrbin.ddptTest.test.test1: void func20()>/o4", k -> new HashSet<>()).add(new Allocation("<com.xrbin.ddptTest.test.test1: void func20()>/new java.lang.Object/2"));
        standardAnswers.computeIfAbsent("<com.xrbin.ddptTest.test.clinit: void func0()>/a_2", k -> new HashSet<>()).add(new Allocation("<com.xrbin.ddptTest.test.clinit: void <clinit>()>/new com.xrbin.ddptTest.test.A/1"));
        standardAnswers.computeIfAbsent("<com.xrbin.ddptTest.test.clinit: void func0()>/a_2", k -> new HashSet<>()).add(new Allocation("<com.xrbin.ddptTest.test.clinit: void <clinit>()>/new com.xrbin.ddptTest.test.A/2"));
        standardAnswers.computeIfAbsent("<com.xrbin.ddptTest.test.clinit: void func0()>/a_2", k -> new HashSet<>()).add(new Allocation("<com.xrbin.ddptTest.test.clinit: void func0()>/new com.xrbin.ddptTest.test.A/2"));
        standardAnswers.computeIfAbsent("<com.xrbin.ddptTest.test.clinit: void func1()>/a", k -> new HashSet<>()).add(new Allocation("<com.xrbin.ddptTest.test.clinit: void <clinit>()>/new com.xrbin.ddptTest.test.A/1"));
        standardAnswers.computeIfAbsent("<com.xrbin.ddptTest.test.clinit: void func1()>/a", k -> new HashSet<>()).add(new Allocation("<com.xrbin.ddptTest.test.clinit: void <clinit>()>/new com.xrbin.ddptTest.test.A/2"));
        standardAnswers.computeIfAbsent("<com.xrbin.ddptTest.cflrBackAnalysis.test1: void func2(java.lang.Object)>/oooo", k -> new HashSet<>()).add(new Allocation("<com.xrbin.ddptTest.cflrBackAnalysis.test1: java.lang.Object func1()>/new java.lang.Object/0"));
        standardAnswers.computeIfAbsent("<com.xrbin.ddptTest.cflrBackAnalysis.test1: void func2(java.lang.Object)>/oooo", k -> new HashSet<>()).add(new Allocation("<com.xrbin.ddptTest.cflrBackAnalysis.A: void <init>()>/new java.lang.Object/0"));
        standardAnswers.computeIfAbsent("<com.xrbin.ddptTest.cflrHidenField.test1: void func0()>/a4", k -> new HashSet<>()).add(new Allocation("<com.xrbin.ddptTest.cflrHidenField.test1: void func0()>/new com.xrbin.ddptTest.cflrHidenField.A/1"));
        standardAnswers.computeIfAbsent("<com.xrbin.ddptTest.cflrHidenField.test1: void func0()>/a5", k -> new HashSet<>()).add(new Allocation("<com.xrbin.ddptTest.cflrHidenField.test1: void func0()>/new com.xrbin.ddptTest.cflrHidenField.A/1"));
    }
}
