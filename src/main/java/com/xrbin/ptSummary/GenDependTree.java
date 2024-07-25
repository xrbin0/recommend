package com.xrbin.ptSummary;

import com.xrbin.ddpt.CallGraph;
import com.xrbin.ddpt.IntroValueFlowGraph;
import com.xrbin.ddpt.WholeProgramCFG;
import com.xrbin.ddpt.model.*;
import com.xrbin.utils.util;
import soot.Body;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class GenDependTree {
    public static CallGraph cg;
    public static TypeSystem ts;
    public static WholeProgramCFG wpCFG;
    public static IntroValueFlowGraph ivfg;
    public static HashMap<String, Body> bodys = new HashMap<>();

    private final static HashMap<String, Integer> allMethod = new HashMap<>();
    private final static Vector<String> allMethodArray = new Vector<>();

    private final static Map<String, List<String>> classToSuccs = new HashMap<>();
    private final static Map<String, List<String>> classToPreds = new HashMap<>();

    private final static Map<Integer, HashSet<Integer>> dagLpsToSuccs = new HashMap<>();
    private final static Map<Integer, HashSet<Integer>> dagLpsToPreds = new HashMap<>();

//    private final static Map<HashSet<LocatePointer>, HashSet<HashSet<LocatePointer>>> dagLpsToSuccs = new HashMap<>();
//    private final static Map<HashSet<LocatePointer>, HashSet<HashSet<LocatePointer>>> dagLpsToPreds = new HashMap<>();

    public static void genDependTree() {
        try {
            Runtime.getRuntime().exec("rm /home/xrbin/IdeaProjects/DDPT/logs/dpTree");
            Runtime.getRuntime().exec("rm /home/xrbin/IdeaProjects/DDPT/logs/nodeValue");
            Runtime.getRuntime().exec("rm /home/xrbin/IdeaProjects/DDPT/logs/dependSize");
        } catch (Exception e) {
            e.printStackTrace();
        }

        util.getTime("genDependTree - begin");

        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/recommend/logs/jdk8CallGraph");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String caller = line.split("\t")[0];
                String callee = line.split("\t")[1];
//                util.writeFilelnWithPrefix(caller + "\t" + callee, "jdkclass2");
                if (bodys.containsKey(caller) && bodys.containsKey(callee)) {
                    if(!allMethod.containsKey(caller)) {
//                        System.out.println("caller: " + caller);
                        allMethod.put(caller, allMethodArray.size());
                        allMethodArray.add(caller);
                    }
                    if(!allMethod.containsKey(callee)) {
//                        System.out.println("callee: " + callee);
                        allMethod.put(callee, allMethodArray.size());
                        allMethodArray.add(callee);
                    }
//                    System.out.println(caller + "\t" + callee);
                    classToSuccs.computeIfAbsent(caller, k -> new ArrayList<>()).add(callee);
                    classToPreds.computeIfAbsent(callee, k -> new ArrayList<>()).add(caller);
                    classToSuccs.computeIfAbsent(callee, k -> new ArrayList<>());
                    classToPreds.computeIfAbsent(caller, k -> new ArrayList<>());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        classToSuccs.keySet().forEach(lp -> {
            util.writeFilelnWithPrefix("\n" + lp.toString(), "dpTree");
            classToSuccs.get(lp).forEach(succ -> {
                util.writeFilelnWithPrefix("\t" + succ.toString(), "dpTree");
            });
        });

        buildDAG();
    }


    private static int[] dfn;
    private static int[] low;
    private static int[] stack;
    private static int[] sc;
    private static int[] inDegree;
    private static int[] outDegree;
    private static int[] nodeValue;
    private static int nodeCount = 0;
    private static int tp = 0, dfncnt = 0, sccount = 0;
    private static final HashSet<String> inStack = new HashSet<>();
    private static void buildDAG() {
        util.getTime("buildDAG scc - begin");
        System.out.println("allMethodArray.size() = " + allMethodArray.size());

        sc = new int[allMethodArray.size() + 16]; // sc - allLpArray
        dfn = new int[allMethodArray.size() + 16];
        low = new int[allMethodArray.size() + 16];
        stack = new int[allMethodArray.size() + 16];
        inDegree = new int[allMethodArray.size() + 16];
        outDegree = new int[allMethodArray.size() + 16];
        nodeValue = new int[allMethodArray.size() + 16];
        for(int i = 0; i < allMethodArray.size(); i++) {
            if(dfn[i] == 0) {
                tarjan(i);
            }
        }

        System.out.println("sccount = " + sccount + ", nodeCount = " + nodeCount);

        util.getTime("buildDAG graph - begin");

        int[] result = new int[sccount + 16];
//        int[] result2 = new int[sccount + 16];
//        Vector<HashSet<Integer>> reachable = new Vector<>();
        BitSet[] reachableSet = new BitSet[sccount + 16];
        for(int i = 0;i < sccount + 1;i++) {
            result[i] = 0;
//            result2[i] = 0;
//            reachable.add(new HashSet<>());
            dagLpsToSuccs.put(i, new HashSet<>());
            dagLpsToPreds.put(i, new HashSet<>());
        }

        for(int i = 0; i < allMethodArray.size(); i++) {
            for(String succ : classToSuccs.get(allMethodArray.elementAt(i))) {
                int curInt = sc[i];
                int succInt = sc[allMethod.get(succ)];
                if(succInt != curInt) {
                    dagLpsToSuccs.get(curInt).add(succInt);
                    dagLpsToPreds.get(succInt).add(curInt);
                }
            }
        }

        util.getTime("topological sorting - begin");
        Queue<Integer> worklist = new ArrayDeque<>();

        dagLpsToSuccs.keySet().forEach(n -> {
            inDegree[n] = dagLpsToPreds.get(n).size();
            outDegree[n] = dagLpsToSuccs.get(n).size();
            if(outDegree[n] == 0) {
                worklist.add(n);
            }
        });

        int temp = 0;
        int worklistcount = 0;
        while (!worklist.isEmpty()) {
            int cur = worklist.poll();
            reachableSet[cur] = new BitSet(sccount + 16);
            dagLpsToSuccs.get(cur).forEach(succ -> {
                reachableSet[cur].or(reachableSet[succ]);
                reachableSet[cur].set(succ, true);
//                reachable.get(cur).addAll(reachable.get(succ));
//                reachable.get(cur).add(succ);
                inDegree[succ]--;
                if(inDegree[succ] == 0) {
                    reachableSet[succ] = null;
//                    reachable.get(succ).clear();
                }
            });
            for(int i = 0; i < sccount; i++) {
                if(reachableSet[cur].get(i)) {
                    result[cur] += nodeValue[i];
                }
            }
            result[cur] += temp;
//            result[cur] = reachableSet[cur].cardinality();
//            result2[cur] = reachable.get(cur).size();
            dagLpsToPreds.get(cur).forEach(pred -> {
                outDegree[pred]--;
                if(outDegree[pred] == 0) {
                    worklist.offer(pred);
                }
            });
            worklistcount++;
            if(worklistcount % 1000 == 0) {
                System.out.println("worklistcount = " + worklistcount + ", nodeCount = " + nodeCount);
            }
            if(worklistcount == sccount - 512) {
                temp = 100000000;
//                break;
            }
            if(worklistcount == sccount - 1024) {
                temp = 10000000;
//                break;
            }
            if(worklistcount == sccount - 2048) {
                temp = 1000000;
//                break;
            }
            if(worklistcount == sccount - 6144) {
                temp = 100000;
//                break;
            }
        }
        System.out.println("worklistcount = " + worklistcount + ", nodeCount = " + nodeCount);

        for(int i = 0; i < sccount; i++) {
            util.writeFilelnWithPrefix(result[i] + "", "dependSize");
//            util.writeFilelnWithPrefix(result2[i] + "", "dependSize-hashset");
        }
        for(int i = 0; i < sccount; i++) {
            util.writeFilelnWithPrefix(nodeValue[i] + "", "nodeValue");
        }

        for (int i = 0; i < allMethodArray.size(); i++) {
            util.writeFilelnWithPrefix(allMethodArray.elementAt(i) + "\t" + sc[i], "dependSizeIndexMap");
        }

//        allLpArray.forEach(lp -> {
//            HashSet<LocatePointer> scc = lpToSCCNode.get(lp);
//            lpToSuccs.get(lp).forEach(succ -> {
//                HashSet<LocatePointer> sccsucc = lpToSCCNode.get(succ);
//                dagLpsToSuccs.computeIfAbsent(scc, k -> new HashSet<>()).add(sccsucc);
//                dagLpsToPreds.computeIfAbsent(sccsucc, k -> new HashSet<>()).add(scc);
//            });
//        });

    }

    private static void tarjan(int u) {
//        System.out.println("tarjan u = " + u);
        low[u] = dfn[u] = ++dfncnt;
        stack[++tp] = u;
        inStack.add(allMethodArray.elementAt(u));
        for (String lp : classToSuccs.get(allMethodArray.elementAt(u))) {
            if (dfn[allMethod.get(lp)] == 0) { // 未被访问过
                tarjan(allMethod.get(lp));
                low[u] = Math.min(low[u], low[allMethod.get(lp)]);
            }
            else if (inStack.contains(lp)) {
                low[u] = Math.min(low[u], dfn[allMethod.get(lp)]);
            }
        }

        if (dfn[u] == low[u]) {
            Vector<Integer> scc = new Vector<>();
            int ccount = 1;
            while (stack[tp] != u) {
                scc.add(stack[tp]);
                nodeCount++;
                ccount++;
                sc[stack[tp]] = sccount;
//                sccLp.add(allLpArray.elementAt(stack[tp]));
                inStack.remove(allMethodArray.elementAt(stack[tp]));
                tp--;
            }
            scc.add(stack[tp]);
            sc[stack[tp]] = sccount;
            nodeCount++;
//            sccLp.add(allLpArray.elementAt(stack[tp]));
            inStack.remove(allMethodArray.elementAt(stack[tp]));
            tp--;

            nodeValue[sccount] = ccount;
            sccount++;

            if(ccount > 1) {
                util.writeFilelnWithPrefix("", "scc");
                scc.forEach(index -> util.writeFilelnWithPrefix(allMethodArray.elementAt(index), "scc"));
            }

//            sccLp.forEach(slp -> lpToSCCNode.put(slp, sccLp));
//            System.out.println(u + " - lp: " + allLpArray.get(u));
        }
    }
}
