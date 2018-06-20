package com.jms.cleanse.util;

import java.io.ByteArrayInputStream;

public class DataFormUtil {

    /**
     * 合并byte数组
     */
    public static byte[] unitByteArray(byte[] byte1,byte[] byte2){
        byte[] unitByte = new byte[byte1.length + byte2.length];
        System.arraycopy(byte1, 0, unitByte, 0, byte1.length);
        System.arraycopy(byte2, 0, unitByte, byte1.length, byte2.length);
        return unitByte;
    }

    public static byte[] string_to_byte(String request) {
        int int_register = (new ByteArrayInputStream(request.getBytes())).available();
        byte[] bytes = new byte[]{(byte)(int_register % 256), (byte)(int_register / 256 % 256), (byte)(int_register / 256 / 256 % 256), (byte)(int_register / 256 / 256 / 256)};
        return bytes;
    }


    /**
     *  int -- byte[]
     * @param value
     * @return
     */
    public static byte[] intToBytes(int value){
        byte[] head= new byte[4];
        head [0] = (byte) (value % 256);
        head [1] = (byte) ((value / 256) % 256);
        head [2] = (byte) (((value / 256) / 256) % 256);
        head [3] = (byte) (((value / 256) / 256) / 256);
        return head;

    }

}
