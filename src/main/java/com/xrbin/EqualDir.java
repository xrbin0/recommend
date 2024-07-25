package com.xrbin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EqualDir {
    public static void main(String[] args) {
        if (args.length == 2) {
            equalDir(args[0], args[1]);
        }
        else {
            System.err.println("arg wrong.");
        }
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
        if (set1 == set2) return true;
        if (set1 == null || set2 == null) return false;
        if (set1.size() != set2.size()) return false;
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
}
