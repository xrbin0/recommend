import com.xrbin.ddpt.*;
import com.xrbin.ddpt.model.*;
import com.xrbin.utils.StaticData;
import com.xrbin.utils.util;
import soot.Local;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.internal.JAssignStmt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Vector;

public class Test {
    public static int count = 0;
    public static Random ra = new Random();
    private static final boolean COMPAREDOOP = false;

    public static void main(String[] args) {
//        if(true) {
//            System.out.println(System.getProperty("java.library.path"));
//            Ddlog.main(new String[0]);
//            return;
//        }

        utils.FILE = true;
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

        utils.JARPTAH = "/home/xrbin/Desktop/Java_Project/fop/build/fop.jar";
        utils.MAINCLASS = "org.apache.fop.cli.Main";
        utils.DATABASE = utils.DOOPWORKPLACE + "/out/fop/" + "/database/";

        utils.JARPTAH = "/home/xrbin/Desktop/Java_Project/jieba-analysis-master/target/jieba-analysis-1.0.3-SNAPSHOT.jar";
        utils.MAINCLASS = "com.qianxinyao.analysis.jieba.keyword.TFIDFAnalyzer";
        utils.DATABASE = "/home/xrbin/Desktop/doophome/out/" + "jieba-2obj" +"/database/";

        utils.JARPTAH = "/home/xrbin/Desktop/Java_Project/DDPT_test/build/libs/all.jar";
        utils.MAINCLASS = "com.xrbin.ddptTest.test.test1";
        utils.DATABASE = utils.DOOPWORKPLACE + "/out/all/" + "/database/";

        Main.main(args);

//        testRandom();
//        testFile();
    }

    private static int conut_index = 0;
    private static final int index_mul = 6;
    private static final int index_min = 128 * index_mul;
    private static final int index_max = 128 * (index_mul + 10);
    private static void testRandom() {
        Vector<LocatePointer> lvs = new Vector<>();
        for (Unit u : Main.wpCFG) {
//            System.out.println(u);
//            for(ValueBox vb : u.getUseBoxes()) {
//                util.plnG("\t" + vb.getValue().toString());
//            }
//            for(ValueBox vb : u.getDefBoxes()) {
//                util.plnB("\t" + vb.getValue().toString());
//            }
//            if(u.toString().contains("l0")) {
//                System.out.println("\nl0;" + u);
//            }

            if (u instanceof JAssignStmt
                    && DatabaseManager.getInstance().appMethod.contains(Main.wpCFG.getMethodOf(u).toString())
                    && Main.reachableMethod.contains(Main.wpCFG.getMethodOf(u).toString())
            ) {
                JAssignStmt assignStmt = (JAssignStmt) u;
                Value left = assignStmt.getLeftOp();
                Value right = assignStmt.getRightOp();

//                if (u.toString().equals(lvString)) {
//                    VFGvalue vv = new VFGvalue((JimpleLocal) ((JAssignStmt) u).getLeftOp());
//                    lvs.add(getLocatePointer(ocu, vv));
//                }
                if (left instanceof Local
//                        && randomGet(0X8)
                        && !Main.wpCFG.getMethodOf(u).toString().contains("clinit")
                        && !left.getType().toString().equals("StringBuilder")
                        && !left.getType().toString().equals("StringBuffer")
                        && !utils.isHardMethod(Main.wpCFG.getMethodOf(u))
                        && !utils.isPrimitiveType(left.getType())
                        && !right.toString().contains("dynamic")
                ) {
//                    conut_index = conut_index + 1;
//                    if (lvs.size() < 512
//                            && index_min <= conut_index && index_max >= conut_index
//                    ) {
                    lvs.add(new LocatePointer(u, new VFGvalue(((JAssignStmt) u).getLeftOp()), Main.wpCFG.getMethodOf(u).toString()));
//                    }
                }
            }
        }

        System.out.println("\nlvs.size() = " + lvs.size());
        lvs.forEach(l -> {
            util.writeFilelnWithPrefix(l.getMethod() + "/" + l.getV().getValue().toString(), "query");

            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss:SSS");
            String formatStr = formatter.format(new Date());
            if (utils.TEST2FILE) {
                util.writeFilelnWithPrefix("\n" + formatStr, "TestForTest");
            }
            else {
                System.out.print("\n" + StaticData.R + formatStr + StaticData.E);
            }

            HashSet<CSAllocation> objs = Main.ddpt.query(l);
            HashSet<Allocation> insenObjs = new HashSet<>();
            objs.forEach(o -> insenObjs.add(o.getO()));

            if (utils.TEST2FILE) {
                util.writeFilelnWithPrefix("\n" + l.toString(), "TestForTest");
                insenObjs.forEach(o -> util.writeFilelnWithPrefix("\t" + o.getO(), "TestForTest"));
            }
            else {
                System.out.println("\n" + l);
                objs.forEach(o -> System.out.println("\t" + o.getO()));

                util.plnP("\n" + l);
                insenObjs.forEach(o -> util.plnP("\t" + o.getO()));
            }

            String var = Main.wpCFG.getMethodOf(l.getU()) + "/" + ((Local) ((AssignStmt) l.getU()).getLeftOp()).getName();
            if (DatabaseManager.getInstance().vptInsen.containsKey(var)) {
                if (COMPAREDOOP) {
                    insenObjs.forEach(o -> {
                        if (!DatabaseManager.getInstance().vptInsen.get(var).contains(o)) {
                            if (utils.TEST2FILE) {
                                util.writeFilelnWithPrefix("\n" + l, "moreThanDoop");
                                util.writeFilelnWithPrefix("\t\t" + o.getO(), "moreThanDoop");
                            }
                            else {
                                util.plnR("\t\t" + o.getO());
                            }
                        }
                    });
                    DatabaseManager.getInstance().vptInsen.get(var).forEach(o -> {
                        if (!insenObjs.contains(o)) {
                            if (utils.TEST2FILE) {
                                util.writeFilelnWithPrefix("\n" + l, "lessThanDoop");
                                util.writeFilelnWithPrefix("\t\t" + o.getO(), "lessThanDoop");
                            }
                            else {
                                util.plnY("\t\t" + o.getO());
                            }
                        }
                    });
                }
                else {
                    DatabaseManager.getInstance().vptInsen.get(var).forEach(o -> util.plnBG("\t" + o.getO()));
                }
            }
        });
    }

    private static void testFile() {
        try (
                FileReader reader = new FileReader("/home/xrbin/Desktop/DDPT/logs/recommendVar_v0");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                query(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void query(String query) {
        if(query.equals("")) return;

        LocatePointer l = Query.find(Main.wpCFG, query);
        if(l == null) return;

        HashSet<CSAllocation> objs = Main.ddpt.query(l);
        HashSet<Allocation> insenObjs = new HashSet<>();
        objs.forEach(o -> insenObjs.add(o.getO()));

        if (utils.TEST2FILE) {
            util.writeFilelnWithPrefix("\n" + l.toString(), "TestForTest");
            insenObjs.forEach(o -> util.writeFilelnWithPrefix("\t" + o.getO(), "TestForTest"));
        }
        else {
            System.out.println("\n" + l);
            objs.forEach(o -> System.out.println("\t" + o.getO()));

            util.plnP("\n" + l);
            insenObjs.forEach(o -> util.plnP("\t" + o.getO()));
        }

        String var = Main.wpCFG.getMethodOf(l.getU()) + "/" + ((Local) ((AssignStmt) l.getU()).getLeftOp()).getName();
        if (DatabaseManager.getInstance().vptInsen.containsKey(var)) {
            if(insenObjs.size() == 0) {
                util.writeFilelnWithPrefix(query, "recommendVar_v1");
            }
            if (COMPAREDOOP) {
                insenObjs.forEach(o -> {
                    if (!DatabaseManager.getInstance().vptInsen.get(var).contains(o)) {
                        if (utils.TEST2FILE) {
                            util.writeFilelnWithPrefix("\n" + l, "moreThanDoop");
                            util.writeFilelnWithPrefix("\t\t" + o.getO(), "moreThanDoop");
                        }
                        else {
                            util.plnR("\t\t" + o.getO());
                        }
                    }
                });
                DatabaseManager.getInstance().vptInsen.get(var).forEach(o -> {
                    if (!insenObjs.contains(o)) {
                        if (utils.TEST2FILE) {
                            util.writeFilelnWithPrefix("\n" + l, "lessThanDoop");
                            util.writeFilelnWithPrefix("\t\t" + o.getO(), "lessThanDoop");
                        }
                        else {
                            util.plnY("\t\t" + o.getO());
                        }
                    }
                });
            }
            else {
                if (utils.TEST2FILE) {
                    DatabaseManager.getInstance().vptInsen.get(var).forEach(o -> util.writeFilelnWithPrefix("\t" + o.getO(), "TestForTest"));
                }
                else {
                    DatabaseManager.getInstance().vptInsen.get(var).forEach(o -> util.plnBG("\t" + o.getO()));
                }
            }
        }
    }

    public static boolean randomGet(int a) {
        return ra.nextInt(a) == 0;
    }
}