package com.xrbin.ddpt.model;

import fj.Hash;
import soot.SootClass;
import soot.Type;
import soot.Unit;

import java.util.*;

public class TypeSystem {
    public final Map<SootClass, HashSet<SootClass>> superInterface = new HashMap<>();
    public final Map<SootClass, HashSet<SootClass>> subInterface = new HashMap<>();
    public final Map<SootClass, HashSet<SootClass>> superClass = new HashMap<>();
    public final Map<SootClass, HashSet<SootClass>> subClass = new HashMap<>();

    public final Map<String, SootClass> strToClass = new HashMap<>();
    public final Map<Type, SootClass> typeToClass = new HashMap<>();
    public final Map<String, Type> strToType = new HashMap<>();

    public final Set<SootClass> color = new HashSet<>();

    public void add(SootClass sc) {
//        if(strToClass.containsKey(sc.toString()) || typeToClass.containsKey(sc.getType())) {
//            System.out.println(" --- wrong --- " + sc.toString() + "\t" + sc.getType());
//        }

        f(sc);

        superInterface.get(sc).add(sc);
        subInterface.get(sc).add(sc);
        superClass.get(sc).add(sc);
        subClass.get(sc).add(sc);

        if(sc.isInterface()) {
            sc.getInterfaces().forEach(iinterface -> {
                superInterface.get(sc).add(iinterface);
                f(iinterface);
            });
            if(sc.hasSuperclass()) {
                superClass.get(sc).add(sc.getSuperclass());
                f(sc.getSuperclass());
            }
        }
        else if(sc.isConcrete()) {
            sc.getInterfaces().forEach(iinterface -> {
                superInterface.get(sc).add(iinterface);
                f(iinterface);
            });
            if(sc.hasSuperclass()) {
                superClass.get(sc).add(sc.getSuperclass());
                f(sc.getSuperclass());
            }
        }
        else if(sc.isAbstract()) {
            sc.getInterfaces().forEach(iinterface -> {
                superInterface.get(sc).add(iinterface);
                f(iinterface);
            });
            if(sc.hasSuperclass()) {
                superClass.get(sc).add(sc.getSuperclass());
                f(sc.getSuperclass());
            }
        }
        else if(sc.isInnerClass()) {
            sc.getInterfaces().forEach(iinterface -> {
                superInterface.get(sc).add(iinterface);
                f(iinterface);
            });
            if(sc.hasSuperclass()) {
                superClass.get(sc).add(sc.getSuperclass());
                f(sc.getSuperclass());
            }
        }
        else {
            System.out.println(sc.toString() + "\t" + sc.getType());
        }
    }

    public void f(SootClass sc){
        if(color.add(sc)) {
            strToType.put(sc.toString(), sc.getType());
            strToClass.put(sc.toString(), sc);
            typeToClass.put(sc.getType(), sc);
            superInterface.put(sc, new HashSet<>());
            subInterface.put(sc, new HashSet<>());
            superClass.put(sc, new HashSet<>());
            subClass.put(sc, new HashSet<>());
        }
    }

    public HashSet<SootClass> getSuperClass(SootClass sc) {
        return superClass.computeIfAbsent(sc, k -> new HashSet<>());
    }

    public HashSet<SootClass> getSuperInterface(SootClass sc) {
        return superInterface.computeIfAbsent(sc, k -> new HashSet<>());
    }

    public HashSet<SootClass> getSubClass(SootClass sc) {
        return subClass.computeIfAbsent(sc, k -> new HashSet<>());
    }

    public HashSet<SootClass> getSubInterface(SootClass sc) {
        return superInterface.computeIfAbsent(sc, k -> new HashSet<>());
    }

    public void run() {
        boolean flag = true;
        while (flag) {
            flag = false;
            for(SootClass sc : superInterface.keySet()) {
                HashSet<SootClass> temp = new HashSet<>(superInterface.get(sc));
                for(SootClass superSc : temp){
                    if(getSuperInterface(sc).addAll(getSuperInterface(superSc))) flag = true;
                }
            }
            for(SootClass sc : superClass.keySet()) {
                HashSet<SootClass> temp = new HashSet<>(superClass.get(sc));
                for(SootClass superSc : temp){
                    if(getSuperClass(sc).addAll(getSuperClass(superSc))) flag = true;
                }
            }
        }

    }



}
