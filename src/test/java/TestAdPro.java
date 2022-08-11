
import com.xrbin.ddpt.model.Allocation;
import com.xrbin.utils.util;

import java.io.*;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class TestAdPro {
    public static void main(String[] args) {
        String path = "/home/xrbin/Desktop/share/高程项目一1/";
        File file = new File(path);
        File[] fs = file.listFiles();
        int i = 0;
        for(File f:fs) {
//            if (!f.isDirectory()) {
//                String newPath = path + getContinuousNumber(f.toString()).toString();
//                if (newPath.split("/")[6].length() > 7) {
//                    try {
////                        Runtime.getRuntime().exec("rm -r " + path + "/0000000000");
////                        Runtime.getRuntime().exec("mkdir " + newPath);
////                        Runtime.getRuntime().exec("cp -r " + f.toString() + " " + newPath);
////                        Runtime.getRuntime().exec("unzip " + f.toString() + " -d " + newPath);
//                        i++;
//                        System.out.println("mkdir " + newPath);
////                        System.out.println("cp -r " + f.toString() + " " + newPath);
//                        System.out.println("unzip " + f.toString() + " -d " + newPath);
//                    } catch (Exception e) {
//                        System.err.print("----------------" + e.toString());
//                    }
////                    System.out.println("\n------------" + newPath + "-final.cpp");
////                    asd = true;
////                    dfsToOneCpp(path + "/0000000000",  newPath + "-final.cpp");
//                }
//            }
//            if (!f.isDirectory()) {
//                String newPath = path + getContinuousNumber(f.toString()).toString();
//                System.out.println(i++ + "" + f + " - " + newPath);
//                try {
//                    Runtime.getRuntime().exec("mkdir " + newPath);
////                    Runtime.getRuntime().exec("cp -r " + f.toString() + " " + newPath + "/");
//                    Runtime.getRuntime().exec("unzip " + f.toString() + " -d " + newPath + "/");
//                } catch (Exception e) {
//                    System.err.print("----------------" + e.toString());
//                }
//            }
//
            if (f.isDirectory()) {
                System.out.println("\n------------" + f.toString() + "-final.cpp");
                asd = true;
                dfsToOneCpp(f.toString(), f.toString() + "-final.cpp");
            }

            if(f.toString().split(" ").length > 1) {

//                i++;
//                System.out.println(f.toString());
//                testRenameFile(f.toString());
            }
        }
        System.out.println(i);
    }

    private static void testRenameFile(String fileName) {
        String newFileName = fileName.replace(' ', '-');
        try {
            File src = new File(fileName);
            File des = new File(newFileName);
            if (des.exists()) {
                boolean res = des.delete();
                if (!res) {
                    System.out.println("Failed to delete file");
                }
            }
            if (!src.renameTo(des)) {
                System.out.println("Failed to renameTo file");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static boolean asd = true;
    public static void dfsToOneCpp(String args, String desCpp) {
        File file = new File(args);
        if(file.listFiles() == null) return;
        File[] fs = file.listFiles();
        for(File f:fs){
            if(!f.isDirectory()) {
                if(f.toString().endsWith(".cpp") || f.toString().endsWith(".h") || f.toString().endsWith(".c")) {
//                    System.out.println(f);
                    try (
                            FileReader reader = new FileReader(f.toString());
                            BufferedReader br = new BufferedReader(reader)
                    ) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            util.writeFileln(line, desCpp);
                            if(asd) {
                                System.out.println(desCpp);
                                asd = false;
                            }
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            else {
//                System.out.println(f);
                dfsToOneCpp(f.toString(), desCpp);
            }
        }
    }

    public static StringBuffer getContinuousNumber(String str) {
        StringBuffer res = new StringBuffer();
        for(int i = 0;i < str.length();i++) {
            if('0' <= str.charAt(i) && str.charAt(i) <= '9') {
                res.append(str.charAt(i));
            }
        }
        return res;
    }
}

/**`
 * @author HY
 */
class ZipUtil {

    public static int BUFFER = 1024;

    public static void main(String[] args) {
        unzip("D:\\test.zip","D:\\");
    }

    public static String unzip(String filePath,String zipDir) {
        String name = "";
        try {
            BufferedOutputStream dest = null;
            BufferedInputStream is = null;
            ZipEntry entry;
            ZipFile zipfile = new ZipFile(filePath);

            Enumeration dir = zipfile.entries();
            while (dir.hasMoreElements()){
                entry = (ZipEntry) dir.nextElement();

                if( entry.isDirectory()){
                    name = entry.getName();
                    name = name.substring(0, name.length() - 1);
                    File fileObject = new File(zipDir + name);
                    fileObject.mkdir();
                }
            }

            Enumeration e = zipfile.entries();
            while (e.hasMoreElements()) {
                entry = (ZipEntry) e.nextElement();
                if( entry.isDirectory()){
                    continue;
                }else{
                    is = new BufferedInputStream(zipfile.getInputStream(entry));
                    int count;
                    byte[] dataByte = new byte[BUFFER];
                    FileOutputStream fos = new FileOutputStream(zipDir+entry.getName());
                    dest = new BufferedOutputStream(fos, BUFFER);
                    while ((count = is.read(dataByte, 0, BUFFER)) != -1) {
                        dest.write(dataByte, 0, count);
                    }
                    dest.flush();
                    dest.close();
                    is.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  name;
    }


}