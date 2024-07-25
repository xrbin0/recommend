package com.xrbin.utils;

import com.xrbin.ddpt.model.DatabaseManager;
import com.xrbin.ddpt.utils;
import soot.SootMethod;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class util {
    public static void p(String s) {
        System.out.print(s);
    }

    public static void pR(String s) {
        System.out.print(StaticData.R + s + StaticData.E);
    }

    public static void pG(String s) {
        System.out.print(StaticData.G + s + StaticData.E);
    }

    public static void pY(String s) {
        System.out.print(StaticData.Y + s + StaticData.E);
    }

    public static void pB(String s) {
        System.out.print(StaticData.B + s + StaticData.E);
    }

    public static void pP(String s) {
        System.out.print(StaticData.P + s + StaticData.E);
    }

    public static void pBG(String s) {
        System.out.print(StaticData.BG + s + StaticData.E);
    }

    public static void pln(String s) {
        System.out.println(s);
    }

    public static void plnR(String s) {
        System.out.println(StaticData.R + s + StaticData.E);
    }

    public static void plnG(String s) {
        System.out.println(StaticData.G + s + StaticData.E);
    }

    public static void plnY(String s) {
        System.out.println(StaticData.Y + s + StaticData.E);
    }

    public static void plnB(String s) {
        System.out.println(StaticData.B + s + StaticData.E);
    }

    public static void plnP(String s) {
        System.out.println(StaticData.P + s + StaticData.E);
    }

    public static void plnBG(String s) {
        System.out.println(StaticData.BG + s + StaticData.E);
    }

    public static void myprint(String s, String color, boolean flag) {
        if(!flag) return;
        if (utils.modle == utils.DEBUG) {
            switch (color) {
                case StaticData.E:
                    util.p(s); break;
                case StaticData.R:
                    util.pR(s); break;
                case StaticData.B:
                    util.pB(s); break;
                case StaticData.BG:
                    util.pBG(s); break;
                case StaticData.G:
                    util.pG(s); break;
                case StaticData.Y:
                    util.pY(s); break;
                default:
                    util.p(s);
            }
        }
    }

    public static void myprint(String s, boolean flag) {
        if(!flag) return;
        if (utils.modle == utils.DEBUG) {
            util.p(s);
        }
    }

    public static void myprintln(String s, String color, boolean flag) {
        if(!flag) return;
        if (utils.modle == utils.DEBUG) {
            if (utils.FILE) {
                writeFile(s + "\n", "logs/output");
            }
            else {
                switch (color) {
                    case StaticData.E:
                        util.pln(s);
                        break;
                    case StaticData.R:
                        util.plnR(s);
                        break;
                    case StaticData.B:
                        util.plnB(s);
                        break;
                    case StaticData.BG:
                        plnBG(s);
                        break;
                    case StaticData.G:
                        util.plnG(s);
                        break;
                    case StaticData.Y:
                        util.plnY(s);
                        break;
                    default:
                        util.pln(s);
                }
            }
        }
    }

    public static void myprintln(String s, boolean flag) {
        if(!flag) return;
        if (utils.modle == utils.DEBUG) {
            if (utils.FILE) {
                writeFile(s + "\n", "logs/output");
            }
            else {
                util.pln(s);
            }
        }
    }

    public static void writeFileln(String s, String fileName) {
        writeFile(s + "\n", fileName);
    }

    public static void writeFilelnWithPrefix(String s, String fileName) {
        writeFile(s + "\n", "logs/" + fileName);
    }

    public static void writeFileWithPrefix(String s, String fileName) {
        writeFile(s, "logs/" + fileName);
    }

    public static void writeFile(String s, String fileName) {
        try {
            File file = new File(fileName);
            FileOutputStream fos = null;
            if (!file.exists()) {
                file.createNewFile();//如果文件不存在，就创建该文件
                fos = new FileOutputStream(file);//首次写入获取
            } else {
                //如果文件已存在，那么就在文件末尾追加写入
                fos = new FileOutputStream(file, true);//这里构造方法多了一个参数true,表示在文件末尾追加写入
            }

            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");//指定以UTF-8格式写入文件

            osw.write(s);
            osw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getSubDate(Date d, String s) {
        if(!utils.STAT) return;

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss:SSS");
        String formatStr = formatter.format(new Date(date.getTime() - d.getTime()));
        plnG(formatStr + s);
    }

    public static void getSubDate(Date d) {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss:SSS");
        String formatStr = formatter.format(new Date(date.getTime() - d.getTime()));
        plnG(formatStr);
    }

    public String repeatString(String str, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    public static void getTime(String s) {
        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss a");// a为am/pm的标记
        Date date = new Date();// 获取当前时间
        plnBG(sdf.format(date) + " - " + s); // 输出已经格式化的现在时间（24小时制）
    }

    public static void equalDir(String dir1, String dir2) {
        List<File> fileList1 = new ArrayList<File>();
        File file1 = new File(dir1);
        File[] files1 = file1.listFiles();
        List<File> fileList2 = new ArrayList<File>();
        File file2 = new File(dir2);
        File[] files2 = file2.listFiles();

        if (files1 == null || files2 == null) {// 如果目录为空，直接退出
            return;
        }
        for (File f : files1) {
            if (f.isFile()) {
                fileList1.add(f);
            }
        }
        for (File f : files2) {
            if (f.isFile()) {
                fileList2.add(f);
            }
        }
        if (fileList1.size() != fileList2.size()) {
            System.err.println("diff");
            return;
        }
        for (File file : fileList1) {
            System.out.println(file);
            Set<String> s1 = new HashSet<>();
            Set<String> s2 = new HashSet<>();
            try (
                    FileReader reader = new FileReader(dir1 + "/" + file.getName());
                    BufferedReader br = new BufferedReader(reader)
            ) {
                String line;
                while ((line = br.readLine()) != null) {
                    s1.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try (
                    FileReader reader = new FileReader(dir2 + "/" + file.getName());
                    BufferedReader br = new BufferedReader(reader)
            ) {
                String line;
                while ((line = br.readLine()) != null) {
                    s2.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!equalSet(s1, s2)) {
                System.err.println("diff");
                return;
            }
        }
        System.err.println("same");
    }

    public static boolean equalSet(Set<String> set1, Set<String> set2) {
        if(set1 == set2) return true;
        if(set1 == null || set2 == null) return false;
        if(set1.size() != set2.size()) return false;
        for (String s : set1) {
            if (!set2.contains(s)) {
                return false;
            }
        }
        for (String s : set2) {
            if (!set1.contains(s)) {
                return false;
            }
        }
        return true;
    }

    public static void rmFile(String fileName) {
        try {
            Runtime.getRuntime().exec("rm " + fileName);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
