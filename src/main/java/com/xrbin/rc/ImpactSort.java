package com.xrbin.rc;

import com.xrbin.utils.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * @author xrbin
 * @date 2023/3/29 下午2:48
 */

public class ImpactSort {
    public static void main(String[] args) {
//        selectVarLoadAssignReturn();

        try {
            Runtime.getRuntime().exec("rm /home/xrbin/IdeaProjects/recommend/logs/ftpfbVar");
            Runtime.getRuntime().exec("rm /home/xrbin/IdeaProjects/recommend/logs/paySimfbVar");
            Runtime.getRuntime().exec("rm /home/xrbin/IdeaProjects/recommend/logs/vdownfbVar");
            Runtime.getRuntime().exec("rm /home/xrbin/IdeaProjects/recommend/logs/dissectfbVar");
            Runtime.getRuntime().exec("rm /home/xrbin/IdeaProjects/recommend/logs/jspiderfbVar");
            Runtime.getRuntime().exec("rm /home/xrbin/IdeaProjects/recommend/logs/raytracerfbVar");
            Runtime.getRuntime().exec("rm /home/xrbin/IdeaProjects/recommend/logs/hedcfbVar");
            Runtime.getRuntime().exec("rm /home/xrbin/IdeaProjects/recommend/logs/ftp02fbVar");
            Runtime.getRuntime().exec("rm /home/xrbin/IdeaProjects/recommend/logs/ftp02VarPTDiffComplete");
            Runtime.getRuntime().exec("rm /home/xrbin/IdeaProjects/recommend/logs/hedcVarPTDiffComplete");
            Runtime.getRuntime().exec("rm /home/xrbin/IdeaProjects/recommend/logs/raytracerVarPTDiffComplete");
            Runtime.getRuntime().exec("rm /home/xrbin/IdeaProjects/recommend/logs/jspiderVarPTDiffComplete");
            Runtime.getRuntime().exec("rm /home/xrbin/IdeaProjects/recommend/logs/ftpVarPTDiffComplete");
            Runtime.getRuntime().exec("rm /home/xrbin/IdeaProjects/recommend/logs/paySimVarPTDiffComplete");
            Runtime.getRuntime().exec("rm /home/xrbin/IdeaProjects/recommend/logs/vdownVarPTDiffComplete");
            Runtime.getRuntime().exec("rm /home/xrbin/IdeaProjects/recommend/logs/dissectVarPTDiffComplete");
        } catch (Exception e) {
            e.printStackTrace();
        }

//        varPTDiff("paySim"); // var:491; isnen:1540; 2obj:527; diff:1016;
//        genNVPT("/home/xrbin/IdeaProjects/recommend/logs/paySimfbVar", "paySim");
//        genNVPT("/home/xrbin/IdeaProjects/Yulin/logs/paySimFbVar", "paySim");

//        varPTDiff("jspider");
//        genNVPT("/home/xrbin/IdeaProjects/Yulin/logs/jspiderSelectVar", "jspider");

//        varPTDiff("ftp");
//        genNVPT("/home/xrbin/IdeaProjects/Yulin/logs/ftpFbVar", "ftp");

//        varPTDiff("vdown");
//        genNVPT("/home/xrbin/IdeaProjects/Yulin/logs/vdownFbVar", "vdown");

//        varPTDiff("raytracer");
//        genNVPT("/home/xrbin/IdeaProjects/Yulin/logs/raytracerFbVar", "raytracer");

//        varPTDiff("dissect");
//        genNVPT("/home/xrbin/IdeaProjects/Yulin/logs/dissectFbVar", "dissect");
//        genNVPT();

//        varPTDiff("hedc");
//        genNVPT("/home/xrbin/IdeaProjects/recommend/logs/hedcfbVar", "hedc");

//        varPTDiff("ftp02");
//        genNVPT("/home/xrbin/IdeaProjects/recommend/logs/ftp02fbVar", "ftp02");

//        varPTDiff("weblech");
//        genNVPT("/home/xrbin/IdeaProjects/recommend/logs/weblechfbVar", "weblech");

        varPTDiff("rayt");
        genNVPT("/home/xrbin/IdeaProjects/recommend/logs/raytfbVar", "rayt");
    }

    public static void genNVPT(String varPath, String id) {
//        selectVarLoadAssignReturn();
//        selectVarLoadAssignReturn(id);

        try (
                FileReader reader = new FileReader("/home/xrbin/doophome/out/" + id + "-insen/database/ReachableApp.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                appMethod.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Set<String>> insenVarPointsTo = new HashMap<>();
        Map<String, Set<String>> tObjVarPointsTo = new HashMap<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/doophome/out/" + id + "-insen/database/VarPointsToApp.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] array = line.split("\t");
                insenVarPointsTo.computeIfAbsent(array[1], k -> new HashSet<>()).add(array[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (
                FileReader reader = new FileReader("/home/xrbin/doophome/out/" + id + "-2obj/database/VarPointsToApp.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] array = line.split("\t");
                tObjVarPointsTo.computeIfAbsent(array[1], k -> new HashSet<>()).add(array[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Set<String> variables = new HashSet<>();
        variables.addAll(tObjVarPointsTo.keySet());
        variables.addAll(insenVarPointsTo.keySet());

        for (String variable : variables) {
            tObjVarPointsTo.computeIfAbsent(variable, k -> new HashSet<>());
            insenVarPointsTo.computeIfAbsent(variable, k -> new HashSet<>());
        }

        Set<String> variables2 = new HashSet<>();
        try (
                FileReader reader = new FileReader(varPath);
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                variables2.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String variable : variables2) {
//            if (selectvariables.contains(variable) && variables.contains(variable))
                if (variables.contains(variable)) {
                    for (String s : insenVarPointsTo.get(variable)) {
                        if (!tObjVarPointsTo.get(variable).contains(s) && isAppObject(s)) {
                            util.writeFileln(s + "\t" + variable, "/home/xrbin/doophome/" + id + "feedback/NVPT.facts");
                        }
                    }
                }
        }

        int count = 1;
        for (String variable : variables2) {
//            if (selectvariables.contains(variable) && variables.contains(variable))
            if (variables.contains(variable)) {
                for (String s : insenVarPointsTo.get(variable)) {
                    if (!tObjVarPointsTo.get(variable).contains(s) && isAppObject(s)) {
                        util.writeFileln(s + "\t" + variable, "/home/xrbin/doophome/" + id + "feedback/NVPT" + count + ".facts");
                        count++;
                    }
                }
            }
        }

    }

    public static void genNVPT() {
        List<String> fbVpt = new ArrayList<>();
        List<String> selectedFbVpt = new ArrayList<>();
        fbVpt.add("");
        try (
                FileReader reader = new FileReader("/home/xrbin/doophome/feedback/NVPT.facts");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                fbVpt.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Set<String> lineNum = new HashSet<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/doophome/dissectResult/allResult");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            int i = 1;
            while ((line = br.readLine()) != null) {
                lineNum.add(line.split("\t")[0]);
                if (Integer.parseInt(line.split("\t")[0]) >= 10) {
                    selectedFbVpt.add(fbVpt.get(i));
                }
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        selectedFbVpt.forEach(k -> {
            util.writeFileln(k, "/home/xrbin/doophome/feedback/dissectNVPT.facts");
        });

    }

    public static Set<String> appMethod = new HashSet<>();
    public static void varPTDiff(String id) {
        Map<String, Set<String>> insenVarPointsTo = new HashMap<>();
        Map<String, Set<String>> tObjVarPointsTo = new HashMap<>();
        Map<String, Set<String>> diffObjVarPointsTo = new HashMap<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/doophome/out/" + id + "-insen/database/ReachableApp.csv");
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
                FileReader reader = new FileReader("/home/xrbin/doophome/out/" + id + "-insen/database/VarPointsToApp.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] array = line.split("\t");
                if (isAppObject(array[0])) {
//                    System.out.println(line);
                    insenVarPointsTo.computeIfAbsent(array[1], k -> new HashSet<>()).add(array[0]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (
                FileReader reader = new FileReader("/home/xrbin/doophome/out/" + id + "-2obj/database/VarPointsToApp.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] array = line.split("\t");
                if (isAppObject(array[0])) {
                    tObjVarPointsTo.computeIfAbsent(array[1], k -> new HashSet<>()).add(array[0]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Set<String> variables = new HashSet<>();
        variables.addAll(tObjVarPointsTo.keySet());
        variables.addAll(insenVarPointsTo.keySet());

        int countInsen = 0;
        int count2obj = 0;
        int countdiff = 0;
        for (String variable : variables) {
            tObjVarPointsTo.computeIfAbsent(variable, k -> new HashSet<>());
            insenVarPointsTo.computeIfAbsent(variable, k -> new HashSet<>());
            diffObjVarPointsTo.computeIfAbsent(variable, k -> new HashSet<>());

            diffObjVarPointsTo.get(variable).addAll(insenVarPointsTo.get(variable));
            diffObjVarPointsTo.get(variable).removeAll(tObjVarPointsTo.get(variable));

            countInsen += insenVarPointsTo.get(variable).size();
            count2obj += tObjVarPointsTo.get(variable).size();

            countdiff += diffObjVarPointsTo.get(variable).size();

            if (diffObjVarPointsTo.get(variable).size() > 0) {
                util.writeFilelnWithPrefix(variable, id + "VarPTDiffComplete");
                for (String s : insenVarPointsTo.get(variable)) {
                    util.writeFilelnWithPrefix("\t" + s, id + "VarPTDiffComplete");
                }
                util.writeFilelnWithPrefix("\n", id + "VarPTDiffComplete");
                for (String s : tObjVarPointsTo.get(variable)) {
                    util.writeFilelnWithPrefix("\t" + s, id + "VarPTDiffComplete");
                }
                util.writeFilelnWithPrefix("\n", id + "VarPTDiffComplete");
                for (String s : diffObjVarPointsTo.get(variable)) {
                    util.writeFilelnWithPrefix("\t" + s, id + "VarPTDiffComplete");
                }
                util.writeFilelnWithPrefix("\n", id + "VarPTDiffComplete");
//                if (!variable.contains("this")
//                        && !variable.contains("para")
//                ) {
                    util.writeFilelnWithPrefix(variable, id + "fbVar");
//                }
            }
            util.writeFilelnWithPrefix(variable, id + "AppVar");

            util.writeFilelnWithPrefix(variable + "\t" + insenVarPointsTo.get(variable).size() + "\t" + tObjVarPointsTo.get(variable).size()
                    + "\t" + diffObjVarPointsTo.get(variable).size(), id + "VarPTDiff");
        }

        System.out.println(variables.size() + "\t" + countInsen + "\t" + count2obj + "\t" + countdiff);
    }

    public static boolean isAppObject(String obj) {
        if (false) return true;
        if (obj.contains("new ") && obj.contains("/")) {
            String method = obj.split("/")[0];
            return appMethod.contains(method);
        }
        return false;
    }

    public static Set<String> selectvariables = new HashSet<>();
    public static void selectVarLoadAssignReturn(String id) {
        // /home/xrbin/IdeaProjects/Yulin/logs/ftpVarWrongUnit
        // /home/xrbin/IdeaProjects/recommend/logs/recommendVar-rc3-all 150 8581
        // /home/xrbin/IdeaProjects/Yulin/logs/ftpScoreVar &


        Map<String, Set<String>> insenVarPointsTo = new HashMap<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/doophome/out/" + "" + id + "-insen/database/VarPointsToApp.csv");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] array = line.split("\t");
                insenVarPointsTo.computeIfAbsent(array[1], k -> new HashSet<>()).add(array[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Set<String> vars1 = new HashSet<>();
        Set<String> vars2 = new HashSet<>();
        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/recommend/logs/" + id + "EndScoreVar50");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                vars1.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (
                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/recommend/logs/recommendVar-rc3-all");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                vars2.add(line.split("\t")[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        vars1.forEach(v -> {
            if (vars2.contains(v)) {
                selectvariables.add(v);
            }
        });

//        try (
//                FileReader reader = new FileReader("/home/xrbin/IdeaProjects/Yulin/logs/ftpScore3");
//                BufferedReader br = new BufferedReader(reader)
//        ) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                String var = line.split("\t")[0];
//                int score = Integer.parseInt(line.split("\t")[1]);
//                int endScore = score * insenVarPointsTo.computeIfAbsent(var, k -> new HashSet<>()).size();
//                util.writeFilelnWithPrefix(var + "\t" + endScore, "ftpEndScore");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public static void selectVarLoadAssignReturn2(String id) {
        try (
                FileReader reader = new FileReader("/home/xrbin/doophome/out/" + id + "-insen/database/AssignReturnValue.facts");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                selectvariables.add(line.split("\t")[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (
                FileReader reader = new FileReader("/home/xrbin/doophome/out/" + id + "-insen/database/LoadInstanceField.facts");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                selectvariables.add(line.split("\t")[2]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (
                FileReader reader = new FileReader("/home/xrbin/doophome/out/" + id + "-insen/database/LoadStaticField.facts");
                BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                selectvariables.add(line.split("\t")[2]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
