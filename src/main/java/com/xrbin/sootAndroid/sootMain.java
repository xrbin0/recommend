package com.xrbin.sootAndroid;
import java.io.File;
import java.util.Collections;

import soot.PackManager;
import soot.Scene;
import soot.Transform;
import soot.options.Options;

/**
 * @author xrbin
 * @date 2023/3/17 下午4:22
 */

public class sootMain {
    private static boolean SOOT_INITIALIZED = false;
    private final static String androidJAR="/home/xrbin/doop-benchmarks/Android/stubs/Android/Sdk/platforms/android-7/android.jar";
    private final static String androidJAR2="/home/xrbin/doop-benchmarks/Android/stubs/Android/Sdk/platforms/android-7/data/layoutlib.jar";
    private final static String appApk="/home/xrbin/Desktop/00748-Greedy-Mouse-1.1.apk";
    public static void initialiseSoot() {
        if(SOOT_INITIALIZED) {
            return;
        }

        Options.v().set_allow_phantom_refs(true);
        Options.v().set_prepend_classpath(true);
        Options.v().set_validate(true);

        Options.v().set_output_format(Options.output_format_jimple);
//      Options.v().set_output_format(Options.output_format_dex);
        Options.v().set_process_dir(Collections.singletonList(appApk));
        Options.v().set_force_android_jar(androidJAR);
        Options.v().set_src_prec(Options.src_prec_apk);
        Options.v().set_soot_classpath(androidJAR2);
        Scene.v().loadNecessaryClasses();

        SOOT_INITIALIZED=true;
    }

    public static void main(String[] args) {
        initialiseSoot();

        PackManager.v().getPack("jtp").add(new Transform("jtp.myAnalysis", new MyAnalysis()));
        PackManager.v().runPacks();
        PackManager.v().writeOutput();
    }
}