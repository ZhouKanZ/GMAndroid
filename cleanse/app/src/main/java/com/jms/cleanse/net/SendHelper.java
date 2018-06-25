package com.jms.cleanse.net;

import com.jms.cleanse.util.DataFormUtil;

public class SendHelper {

    /**
     *  convert sendStr to byte[]
     * @param msg
     * @return
     */
    public static byte[] sendStr(String msg){
//        String msg = "{ \"message_type\":\"register_client\",\"client_type\":2,\"mac_address\":\"00:0c:29:e1:d7:c1\"}";
        byte[] src = msg.getBytes();
        int len = src.length;
        byte[] bytes = DataFormUtil.intToBytes(len);
        byte[] output = DataFormUtil.unitByteArray(bytes,src);
        return output;
    }

}
