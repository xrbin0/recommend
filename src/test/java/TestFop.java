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

public class TestFop {
    public static void main(String[] args) {
        utils.FILE = true;
        utils.CLINIT = true;

        utils.USEDEF = false;
        utils.WPCFG = false;
        utils.HIDENMETHOD = false;
        utils.BUILDVFG = false;
        utils.BACKANALYSIS = true;
        utils.WORKLISTADD = true;
        utils.ANALYZE = true;
        utils.STRONGUPDATE = false;
        utils.RESULT = false;

        utils.JARPTAH = "/home/xrbin/Java_Project/fop/build/fop.jar";
        utils.MAINCLASS = "org.apache.fop.cli.Main";
        utils.DATABASE = utils.DOOPWORKPLACE + "/out/fop/" + "/database/";

        Main.main(args);

        test_fop();
    }

    private static void test_fop() {

        // -- bug
        query("<org.apache.fop.complexscripts.fonts.GlyphSubstitutionTable$Ligature: java.lang.String toString()>/$stack13");
        query("");
        query("");
        query("");
        query("");

        // -- wrong
//        query("<org.apache.fop.fonts.type1.Type1FontLoader: void read()>/ioe#11"); //@caughtexception
//        (soa = newarray (int)[ns], [<<allUse-context>>, <<allUse-context>>]	<org.apache.fop.complexscripts.fonts.OTFAdvancedTypographicTableReader: void readMultipleSubTableFormat1(int,int,long,int)>/soa)
//        query("<org.apache.fop.fonts.truetype.TTFFile$UnicodeMapping: boolean equals(java.lang.Object)>/m");
//        query("<org.apache.fop.render.awt.viewer.PreviewDialog: void setScaleToFitWindow()>/$stack2");
        query("");
        query("");
        query("");
        query("");
        query("");
        /*
        (entryTo = (java.util.Map$Entry) $stack28, [<<allUse-context>>, <<allUse-context>>]	<org.apache.fop.fonts.type1.AFMFile: java.util.Map createXKerningMapEncoded()>/entryTo)
            <<null pseudo heap>>
                <java.util.HashMap: java.util.HashMap$TreeNode newTreeNode(int,java.lang.Object,java.lang.Object,java.util.HashMap$Node)>/new java.util.HashMap$TreeNode/0
                <java.util.HashMap: java.util.HashMap$TreeNode replacementTreeNode(java.util.HashMap$Node,java.util.HashMap$Node)>/new java.util.HashMap$TreeNode/0
                <java.util.HashMap: java.util.HashMap$Node replacementNode(java.util.HashMap$Node,java.util.HashMap$Node)>/new java.util.HashMap$Node/0
                <<null pseudo heap>>
                <java.util.HashMap: java.util.HashMap$Node newNode(int,java.lang.Object,java.lang.Object,java.util.HashMap$Node)>/new java.util.HashMap$Node/0
         */

        // -- long time
//        query("<org.apache.fop.fonts.type1.PFMInputStream: java.lang.String readString()>/$stack5"); // maybe dead loop
        query("");
        query("");
        query("");
        query("");
        query("");
        query("");

        // -- more accurate
        query("");
        query("");
        query("");
        query("");
        query("");
        query("");

        // -- over size

        // fop wrong
        // <org.apache.fop.complexscripts.fonts.OTFAdvancedTypographicTableReader: void constructLookupsLanguage(java.util.Map,java.lang.String,java.lang.String,java.util.Map)>/$stack20
        // <java.nio.ByteBufferAsFloatBufferB: void <init>(java.nio.ByteBuffer)>/new java.lang.AssertionError/0
        // <org.apache.fop.fonts.truetype.FontFileReader: byte read()>/$stack7
        //	<org.apache.fop.fonts.truetype.FontFileReader: byte read()>/new java.io.EOFException/0

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
