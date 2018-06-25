package com.jms.cleanse.net;

public class NettySend {


    public static void sendMove(String robot_mac_address,double vx,double vy,double vtheta){
        String content = "{\"message_type\":\"move\",\"robot_mac_address\":\"" + robot_mac_address + "\",\"vx\":" + vx + ",\"vy\":" + vy + ",\"vtheta\":" + vtheta + "}";
        byte[] sendBytes = SendHelper.sendStr(content);
        NettyClient.getInstance().getChannelFuture().channel().writeAndFlush(sendBytes);
    }


}
