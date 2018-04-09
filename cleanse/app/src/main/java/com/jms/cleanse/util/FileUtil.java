package com.jms.cleanse.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by LiuLy on 2017/5/9.
 */
public class FileUtil {

    public static String readFile(String fileName) throws IOException {
        String res = "";
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(fileName);
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            res = new String(buffer);
            fin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            fin.close();
        }
        return res;
    }

    public static byte[] readPng(String fileName, Context context){

        FileInputStream fis = null;
        byte[] pngBytes = null;
        File file = new File(context.getFilesDir() + "/Boocax/curDoc", fileName);
        if (file.exists() && file.isFile()){
            try {
                fis = new FileInputStream(file);
                int len = fis.available();
                byte[] buff = new byte[len];
                fis.read(buff);
                pngBytes = buff;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return pngBytes;
    }

    public static String readFileJM(String fileName, Context context){
        String content = "";
        FileInputStream fis = null;
        File file = new File(context.getFilesDir() + "/Boocax/curDoc", fileName);
        if (file.exists() && file.isFile()){
            try {
                fis = new FileInputStream(file);
                int len = fis.available();
                byte[] buff = new byte[len];
                fis.read(buff);
                content = new String(buff);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return content;
    }


    public static String readChineseFile(String filePathAndName) {
        String fileContent = "";
        InputStreamReader read = null;
        BufferedReader reader = null;
        try {
            File f = new File(filePathAndName);
            if (f.isFile() && f.exists()) {
                read = new InputStreamReader(new FileInputStream(f), "GBK");
                reader = new BufferedReader(read);
                String line;
                while ((line = reader.readLine()) != null) {
                    fileContent += line;
                }
                read.close();
            }
        } catch (Exception e) {
            System.out.println("读取文件内容操作出错");
            e.printStackTrace();
        }finally {
            try {
                read.close();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileContent;
    }

}
