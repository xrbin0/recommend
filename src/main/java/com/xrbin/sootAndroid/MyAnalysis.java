package com.xrbin.sootAndroid;

import java.util.Iterator;
import java.util.Map;

import soot.*;
import soot.jimple.*;
import soot.jimple.internal.*;
import soot.toolkits.graph.*;
import soot.options.Options;
import soot.tagkit.LineNumberTag;
import soot.tagkit.Tag;
import soot.toolkits.graph.CompleteBlockGraph;
import soot.toolkits.scalar.ArraySparseSet;
import soot.util.Chain;
import soot.util.HashChain;

/**
 * @author xrbin
 * @date 2023/3/17 下午4:22
 */

public class MyAnalysis extends BodyTransformer{
    protected void internalTransform(final Body body,String phase, @SuppressWarnings("rawtypes")Map options) {
        for (SootClass c : Scene.v().getApplicationClasses()) {
            System.out.println("[sootClass]" + c);
            for (SootMethod m : c.getMethods()) {
                //System.out.println("[sootMethod]"+m);

                if (m.isConcrete()) {
                    Body b = m.retrieveActiveBody();
                    // System.out.println("[body]"+b);

                    Iterator<Unit> i = b.getUnits().snapshotIterator();
                    while (i.hasNext()) {
                        Unit u = i.next();
                    }
                }
            }
        }
    }

}