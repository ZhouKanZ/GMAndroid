package com.jms.cleanse.util;

import android.content.Context;

import com.jms.cleanse.JMApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * Created by LiuLy on 2017/5/9.
 */
public class FileUtil {


    public static final String PARENT_PATH = JMApplication.context.getFilesDir() + File.separator + "Boocax" + File.separator + "curDoc";
    public static final String POI_JSON = "poi.json";
    public static final String POI_TASK_JSON = "poitask.json";


    public static String readFile(String fileName) {
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
        } finally {
            try {
                fin.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    public static byte[] readPng(String fileName) {

        FileInputStream fis = null;
        byte[] pngBytes = null;
        File file = new File(PARENT_PATH, fileName);
        if (file.exists() && file.isFile()) {
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

    public static String readFileJM(String fileName) {
        String content = "";
        FileInputStream fis = null;
        File file = new File(PARENT_PATH, fileName);
        if (file.exists() && file.isFile()) {
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


    public static String readChineseFile(String filePARENT_PATHAndName) {
        String fileContent = "";
        InputStreamReader read = null;
        BufferedReader reader = null;
        try {
            File f = new File(filePARENT_PATHAndName);
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
        } finally {
            try {
                read.close();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileContent;
    }

    public static void writeFile(String data) {

        File file = new File(PARENT_PATH, "poitask.json");
        FileOutputStream fos = null;
        try {
            byte[] buff = data.getBytes();
            fos = new FileOutputStream(file);
            fos.write(buff, 0, buff.length);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
