package com.jms.cleanse.util;

import android.util.Log;

import com.google.gson.Gson;
import com.jms.cleanse.JMApplication;
import com.jms.cleanse.entity.file.POIJson;

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


    public static final String PARENT_PATH =
            JMApplication.context.getFilesDir() + File.separator + "Boocax" + File.separator + "curDoc";
    public static final String POI_JSON = "poi.json";
    public static final String POI_TASK_JSON = "poitask.json";
    public static final int ADD = 0;
    public static final int DELETE = 1;
    public static final int UPDATE = 2;

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

    public static POIJson readFileJM(String fileName) {

        POIJson poiJson = null;
        String content = "";
        FileInputStream fis = null;
        File file = new File(PARENT_PATH, fileName);

        // 文件存在的时候 ps(第一次读取的时候，文件的内容为空)
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

            Gson gson = new Gson();
            poiJson = gson.fromJson(content, POIJson.class);

        }else {
            poiJson = new POIJson();
            poiJson.setVersion("1.0.0");
            poiJson.setEncoding("utf-8");
        }

        return poiJson;
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
