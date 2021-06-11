package com.hanhai.cloud.utils;

import jdk.internal.util.xml.impl.Input;
import sun.nio.cs.UTF_32;
import sun.nio.cs.ext.GBK;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

/**
 * 解压zip文件
 * @author Michael sun
 */
public  class UnzipFile {
    /**
     * 解压zip文件
     *
     * @param targetPath
     * @param zipFilePath
     */
    public static   void unzipFile(String targetPath, String zipFilePath) throws Exception{
        ZipFile zip = new ZipFile(new File(zipFilePath), Charset.forName("GBK"));

        System.out.println("开始解压:" + zip.getName() + "...");
        Enumeration<? extends ZipEntry> iter = zip.entries();
        while (iter.hasMoreElements()) {
            ZipEntry entry = iter.nextElement();
            String zipPath = entry.getName();
            try {

                if (entry.isDirectory()) {
                    File zipFolder = new File(targetPath + File.separator
                            + zipPath);
                    if (!zipFolder.exists()) {
                        zipFolder.mkdirs();
                    }
                } else {
                    File file = new File(targetPath + File.separator
                            + zipPath);
                    if (!file.exists()) {
                        File pathDir = file.getParentFile();
                        pathDir.mkdirs();
                        file.createNewFile();
                    }

                    FileOutputStream fos = new FileOutputStream(file);

                    byte[] buff = new byte[1024];
                    int len = 0;
                    InputStream inputStream = zip.getInputStream(entry);
                    while ((len = inputStream.read(buff)) != -1) {
                        fos.write(buff, 0, len);
                    }
                    fos.close();
                    inputStream.close();
                }
                System.out.println("成功解压:" + zipPath);

            } catch (Exception e) {
                System.out.println("解压" + zipPath + "失败");
                continue;
            }
        }

        System.out.println("解压结束");
    }

    /**
     * @param args
     */
//    public static void main(String[] args) {
//        String targetPath = "D:\\test\\2zip";
//        String zipFile = "C:\\Users\\dell\\Desktop\\1111.zip";
//        UnzipFile unzip = new UnzipFile();
//        unzip.unzipFile(targetPath, zipFile);
//    }
}