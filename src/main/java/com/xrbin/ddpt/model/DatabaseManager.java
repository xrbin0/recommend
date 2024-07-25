package com.xrbin.ddpt.model;

import com.xrbin.ddpt.utils;
import com.xrbin.rc.RecommendCtxSen;
import com.xrbin.rc.model.StoreInstanceField;
import com.xrbin.utils.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Vector;

/**
 * @author xrbin0@163.com
 * @date 3/21/21
 */

public class DatabaseManager {
    private static DatabaseManager dt = null;

    public static DatabaseManager getInstance() {
        if (dt == null) {
            dt = new DatabaseManager();
        }
        return dt;
    }

    public final HashMap<String, Vector<ObjContext>> methodToCtx = new HashMap<>();

    public HashSet<String> appMethod = new HashSet<>();
    public HashMap<String, String> assignHeapAllocation = new HashMap<>();

    public Map<Variable, HashSet<CSAllocation>> objCtxVpt = new HashMap<>(); //
    public Map<String, HashSet<CSAllocation>> objCtxStaticVpt = new HashMap<>(); //
    public HashMap<String, HashSet<String>> varCtx = new HashMap<>();

    public Map<String, HashSet<CSAllocation>> staticVpt = new HashMap<>(); //
//    public Map<VFGvalue, HashSet<CSAllocation>> instanceFieldVPT = new HashMap<>(); //

    public HashMap<String, HashSet<String>> cg = new HashMap<>(); // CallGraphEdge.facts
    public HashMap<String, HashSet<String>> backCg = new HashMap<>(); // CallGraphEdge.facts
    public HashMap<String, String> mil = new HashMap<>(); // MethodInvocation-Line.facts


    public HashSet<Field> loadRight = new HashSet<>();
    public HashSet<StoreInstanceField> store = new HashSet<>();
    public HashMap<String, HashSet<CSAllocation>> vptInsen = new HashMap<>();
    public HashMap<String, HashSet<CSAllocation>> vpt2obj = new HashMap<>();
    public HashMap<Field, String> storeToMethod = new HashMap<>();
    public HashMap<Field, HashSet<String>> load = new HashMap<>();

    public HashMap<String, HashSet<CSAllocation>> appVpt2Obj = new HashMap<>();
    public HashMap<String, HashSet<CSAllocation>> appInsenVpt = new HashMap<>();
    public HashMap<String, String> nameToPname = new HashMap<>();

    public HashSet<String> appMethodInsn = new HashSet<>();
    public HashMap<String, HashMap<String, String>> actualParam = new HashMap<>();
    public HashMap<String, HashMap<String, String>> formalParam = new HashMap<>();
    public HashMap<String, String> assignReturnValue = new HashMap<>();
    public HashMap<String, String> methodInvokeToBase = new HashMap<>();
    public HashMap<String, String> thisVar = new HashMap<>();
    public Map<Field, HashSet<CSAllocation>> instanceFieldVPT = new HashMap<>(); //
    public Map<Field, HashSet<CSAllocation>> csInstanceFieldVPT = new HashMap<>(); //


    private DatabaseManager() { }

    public void readData() {
        cg.clear();
        mil.clear();
        varCtx.clear();
        vptInsen.clear();
        objCtxVpt.clear();
        objCtxStaticVpt.clear();
        assignHeapAllocation.clear();
        readData2();
    }

    public void readData2() {
        util.getTime("appInsenVpt");
        try (
                FileReader reader = new FileReader(utils.DATABASE + "AVPT.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
//                System.out.println(line);
                String[] sa = line.split("\t");
                appInsenVpt.computeIfAbsent(sa[3], k -> new HashSet<>()).add(new CSAllocation(new Allocation(sa[1])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

//        util.getTime("appVpt2Obj");
//        try (
//                FileReader reader = new FileReader("/home/xrbin/doophome/out/" + RecommendCtxSen.projectName2Obj + "/database/AVPT.csv");
//                BufferedReader br = new BufferedReader(reader)
//        ) {
//            String line;
//            while ((line = br.readLine()) != null) {
////                System.out.println(line);
//                String[] sa = line.split("\t");
//                appVpt2Obj.computeIfAbsent(sa[3], k -> new HashSet<>()).add(new CSAllocation(sa[1]));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        util.getTime("vptInsen");
        try (
                FileReader reader = new FileReader(utils.DATABASE + "VarPointsTo.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] sa = line.split("\t");
                vptInsen.computeIfAbsent(sa[3], k -> new HashSet<>()).add(new CSAllocation(new Allocation(sa[1])));
                if (nameToPname.containsKey(utils.varNameDoopToShimple(sa[3])) && !nameToPname.get(utils.varNameDoopToShimple(sa[3])).equals(sa[3])) {
//                    System.err.println("------ nameToPname ------ " + sa[3]);
//                    System.exit(-1);
                }
                nameToPname.put(utils.varNameDoopToShimple(sa[3]), sa[3]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        util.getTime("vpt2obj");
        try (
                FileReader reader = new FileReader("/home/xrbin/doophome/out/" + RecommendCtxSen.projectName2Obj + "/database/VarPointsTo.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] sa = line.split("\t");
                vpt2obj.computeIfAbsent(sa[3], k -> new HashSet<>()).add(new CSAllocation(new Allocation(sa[1])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        util.getTime("StoreInstanceField");
        try (
//                FileReader reader = new FileReader(utils.DATABASE + "AppStoreInstanceField.csv");
                FileReader reader = new FileReader(utils.DATABASE + "StoreInstanceField.facts");
                BufferedReader br = new BufferedReader(reader)
        ) {
            // (?instruction:StoreInstanceField_Insn, ?index:number, ?from:Var, ?base:Var, ?signature:Field, ?method:Method)
            String line;
            int c1 = 0, c2 = 0;
            while ((line = br.readLine()) != null) {
//                System.out.println(line);
                String[] sa = line.split("\t");
                HashSet<CSAllocation> Os = vptInsen.get(sa[3]);
                if (Os == null) {
                    c1++;
//                    System.out.println("-------------------------------" + sa[1]);
                    continue;
                }
                else {
                    c2++;
                    for (CSAllocation o : Os) {
                        Field f = new Field(o, sa[4]);
                        store.add(new StoreInstanceField(sa[5], sa[2], sa[3], sa[4]));
                        storeToMethod.put(f, sa[5]);
                    }
                }
            }
//            System.out.println("---- c1 = " + StaticData.G + c1 + StaticData.E + ", c2 = " + StaticData.G + c2 + StaticData.E + "");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //.decl LoadInstanceField(?base:Var, ?sig:Field, ?to:Var, ?inmethod:Method)
        //.decl _LoadInstanceField(?instruction:LoadInstanceField_Insn, ?index:number, ?to:Var, ?base:Var, ?signature:Field, ?method:Method)
        util.getTime("LoadInstanceField");
        try (
//                FileReader reader = new FileReader(utils.DATABASE + "AppLoadInstanceField.csv");
                FileReader reader = new FileReader(utils.DATABASE + "LoadInstanceField.facts");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
//                System.out.println(line);
                String[] sa = line.split("\t");
                HashSet<CSAllocation> Os = vptInsen.get(sa[3]);
                if (Os != null) {
                    for (CSAllocation o : Os) {
                        Field f = new Field(o, sa[4]);
                        loadRight.add(f);
                        load.computeIfAbsent(f, k -> new HashSet<>()).add(sa[2]);
//                        System.out.println(sa[2]);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        util.getTime("appMethod");
        try (
                FileReader reader = new FileReader(utils.DATABASE + "ReachableApp.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                appMethod.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        util.getTime("methodInvokeToBase");
        try (
                FileReader reader = new FileReader(utils.DATABASE + "VirtualMethodInvocation.facts");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
//                System.out.println(line);
                String[] sa = line.split("\t");
//                if(appMethodInsn.contains(sa[0])) {
                methodInvokeToBase.put(sa[0], sa[3]);
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        util.getTime("methodInvokeToBase");
        try (
                FileReader reader = new FileReader(utils.DATABASE + "SpecialMethodInvocation.facts");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
//                System.out.println(line);
                String[] sa = line.split("\t");
//                if(appMethodInsn.contains(sa[0])) {
                methodInvokeToBase.put(sa[0], sa[3]);
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        util.getTime("ThisVar");
        try (
                FileReader reader = new FileReader(utils.DATABASE + "ThisVar.facts");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
//                System.out.println(line);
                String[] sa = line.split("\t");
//                if(appMethodInsn.contains(sa[0])) {
                thisVar.put(sa[0], sa[1]);
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        util.getTime("actualParam");
        try (
                FileReader reader = new FileReader(utils.DATABASE + "ActualParam.facts");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
//                System.out.println(line);
                String[] sa = line.split("\t");
//                if(appMethodInsn.contains(sa[1])) {
                actualParam.computeIfAbsent(sa[1], k -> new HashMap<>()).put(sa[0], sa[2]);
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        util.getTime("formalParam");
        try (
                FileReader reader = new FileReader(utils.DATABASE + "FormalParam.facts");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
//                System.out.println(line);
                String[] sa = line.split("\t");
//                if(appMethodInsn.contains(sa[1])) {
                formalParam.computeIfAbsent(sa[1], k -> new HashMap<>()).put(sa[0], sa[2]);
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        util.getTime("assignReturnValue");
        try (
                FileReader reader = new FileReader(utils.DATABASE + "AssignReturnValue.facts");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] sa = line.split("\t");
                assignReturnValue.put(sa[0], sa[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        util.getTime("instanceFieldVPT");
        try (
                FileReader reader = new FileReader(utils.DATABASE + "InstanceFieldPointsTo.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] str = line.split("\t");
                Field f = new Field(new CSAllocation(new Allocation(str[4])), str[2]);
                instanceFieldVPT.computeIfAbsent(f, k -> new HashSet<>()).add(new CSAllocation(new Allocation(str[1])));
                csInstanceFieldVPT.computeIfAbsent(f, k -> new HashSet<>()).add(new CSAllocation(new Allocation(str[1])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // static method
        try (
                FileReader reader = new FileReader(utils.DATABASE + utils.AVPT);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] a = line.split("\t");
                if(a[2].equals("1")) {
                    methodToCtx.computeIfAbsent(a[3].split(">/")[0] + ">", k -> new Vector<>()).add(ObjContext.initContext);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader(utils.DATABASE + utils.CALLGRAGH);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] v = line.split("\t");
                cg.computeIfAbsent(v[1], k -> new HashSet<>()).add(v[3]);
                backCg.computeIfAbsent(v[3], k -> new HashSet<>()).add(v[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader(utils.DATABASE + utils.MEHTODINVOCATIONLINE);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                //                System.out.println(line);
                String[] v = line.split("\t");
//                util.plnG(v[0].substring(0, v[0].lastIndexOf("/")) + "/" + v[1] + " --> " + v[0]);
                mil.put(v[0].substring(0, v[0].lastIndexOf("/")) + "/" + v[1], v[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader(utils.DATABASE + utils.APPMETHOD);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                appMethod.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader(utils.DATABASE + utils.ASSIGNHEAPALLOCATION);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] v = line.split("\t");
                if (v[2].contains("/")) {
//                    System.out.println("------------------ readAHA: " + v[2].substring(0, v[2].lastIndexOf("/")) + "---" + v[5]);
//                    System.out.println("------------------ readAHA: " + v[2]);
                    assignHeapAllocation.put(v[2].substring(0, v[2].lastIndexOf("/")) + "---" + v[5], v[2]);
                }
//                else {
//                    continue;
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

//        try (
//                FileReader reader = new FileReader(utils.DATABASE + utils.CTXRESPONSE);
//                BufferedReader br = new BufferedReader(reader)
//        ) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                while (line.startsWith(" ")) line = line.substring(1);
//                String[] str = line.split("\t");
//                if(appMethod.contains(str[4])) {
////                    util.writeFilelnWithPrefix(str[4] + " -> " + str[5], "methodToCtx");
//                    methodToCtx.computeIfAbsent(str[4], k -> new Vector<>()).add(
//                            new ObjContext(new Allocation(rmBrackets(str[5]).split(", ")[1]),new Allocation(rmBrackets(str[5]).split(", ")[0])));
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        System.out.println("load.size() = " + load.size());
        System.out.println("cgEdge.size() = " + cg.size());
        System.out.println("insenVpt.size() = " + vpt2obj.size());
        System.out.println("appMethod.size() = " + appMethod.size());
        System.out.println("loadRight.size() = " + loadRight.size());
        System.out.println("appVpt2Obj.size() = " + appVpt2Obj.size());
        System.out.println("appInsenVpt.size() = " + appInsenVpt.size());
        System.out.println("nameToPname.size() = " + nameToPname.size());
        System.out.println("actualParam.size() = " + actualParam.size());
        System.out.println("storeToMethod.size() = " + storeToMethod.size());
        System.out.println("appMethodInsn.size() = " + appMethodInsn.size());
        System.out.println("instanceFieldVPT.size() = " + instanceFieldVPT.size());
        System.out.println("assignReturnValue.size() = " + assignReturnValue.size());
        System.out.println("methodInvokeToBase.size() = " + methodInvokeToBase.size());
    }

    private void readVPT() {
        try (
                FileReader reader = new FileReader(utils.DATABASE + "VarPointsTo.csv");
                BufferedReader br = new BufferedReader(reader) 
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] str = line.split("\t");
                str[3] = utils.varNameDoopToShimple(str[3]);
                Variable v = new Variable(new ObjContext(new Allocation(rmBrackets(str[2]).split(", ")[1]), new Allocation(rmBrackets(str[2]).split(", ")[0])), str[3]);
                CSAllocation h = new CSAllocation(new ObjContext(new Allocation(rmBrackets(str[0]))), new Allocation(str[1]));
                objCtxVpt.computeIfAbsent(v, k -> new HashSet<>()).add(h);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader(utils.DATABASE + "AVPT.csv");
                BufferedReader br = new BufferedReader(reader) 
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] str = line.split("\t");
                str[3] = utils.varNameDoopToShimple(str[3]);
                Variable v = new Variable(new ObjContext(new Allocation(rmBrackets(str[2]).split(", ")[1]), new Allocation(rmBrackets(str[2]).split(", ")[0])), str[3]);
                CSAllocation h = new CSAllocation(new ObjContext(new Allocation(rmBrackets(str[0]))), new Allocation(str[1]));
                objCtxVpt.computeIfAbsent(v, k -> new HashSet<>()).add(h);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader(utils.DATABASE + utils.SFPT);
                BufferedReader br = new BufferedReader(reader) 
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] str = line.split("\t");
//                staticVpt.computeIfAbsent(str[2], k -> new HashSet<>()).add(new CSAllocation(new ObjContext(new Allocation(rmBrackets(str[0]))), new Allocation(str[1])));
                staticVpt.computeIfAbsent(str[2], k -> new HashSet<>()).add(new CSAllocation(new Allocation(str[1])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String rmBrackets(String s) {
        if(s.length() > 2 && s.startsWith("[") && s.endsWith("]")) {
            return s.substring(1, s.length() - 1);
        }
        else {
            // util.writeFilelnWithPrefix(s, "error");
            return s;
        }
    }
}

