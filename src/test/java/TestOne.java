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

import java.util.HashSet;

public class TestOne {
    public static void main(String[] args) {
        utils.FILE = false;
        utils.CLINIT = false;

        utils.USEDEF = false;
        utils.WPCFG = false;
        utils.HIDENMETHOD = true;
        utils.BUILDVFG = false;
        utils.BACKANALYSIS = true;
        utils.WORKLISTADD = true;
        utils.ANALYZE = true;
        utils.STRONGUPDATE = false;
        utils.RESULT = true;

        utils.JARPTAH = "/home/xrbin/Desktop/Java_Project/DDPT_test/build/libs/staticInvoke.jar";
        utils.MAINCLASS = "com.xrbin.ddptTest.staticInvoke.test1";
        utils.DATABASE = utils.DOOPWORKPLACE + "/out/staticInvoke/" + "/database/";
//        utils.JARPTAH = "/home/xrbin/Desktop/Java_Project/DDPT_test/build/libs/all.jar";
//        utils.MAINCLASS = "com.xrbin.ddptTest.test.test1";
//        utils.DATABASE = utils.DOOPWORKPLACE + "/out/all/" + "/database/";

        Main.main(args);

        testOne();
    }

    private static void testOne() {
//        query("<com.xrbin.ddptTest.test.test1: void func20()>/o2");
//        query("<com.xrbin.ddptTest.test.test1: void func20()>/o3");
//        query("<com.xrbin.ddptTest.test.test1: void func20()>/o4");
//        query("<com.xrbin.ddptTest.test.test1: void func7()>/o2");
//        query("<com.xrbin.ddptTest.test.test1: void func8()>/id2");
        query("<com.xrbin.ddptTest.staticInvoke.A: java.lang.Object func3(java.lang.Object)>/o2");
        query("");
        query("");
    }

    public static void query(String query) {
        if(query.equals("")) return;

        LocatePointer l = Query.find(Main.wpCFG, query);
        if(l == null) return;

        HashSet<CSAllocation> objs = Main.ddpt.query(l);
        HashSet<Allocation> insenObjs = new HashSet<>();
        objs.forEach(o -> insenObjs.add(o.getO()));

        System.out.println("\n" + l);
        objs.forEach(o -> System.out.println("\t" + o.getO()));

        util.plnP("\n" + l);
        insenObjs.forEach(o -> util.plnP("\t" + o.getO()));

        String var = Main.wpCFG.getMethodOf(l.getU()) + "/" + ((Local)((AssignStmt)l.getU()).getLeftOp()).getName();
        if (DatabaseManager.getInstance().vptInsen.containsKey(var)) {
            DatabaseManager.getInstance().vptInsen.get(var).forEach(o -> util.plnBG("\t" + o.getO()));
            insenObjs.forEach(o -> {
                if (!DatabaseManager.getInstance().vptInsen.get(var).contains(o)) {
                    util.plnR("\t\t" + o.getO());
                }
            });
            DatabaseManager.getInstance().vptInsen.get(var).forEach(o -> {
                if (!insenObjs.contains(o)) {
                    util.plnY("\t\t" + o.getO());
                }
            });
        }
    }

}
