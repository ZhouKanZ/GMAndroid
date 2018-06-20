package com.jms.cleanse.net;

import com.jms.cleanse.util.DataFormUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private NettyClient nettyClient = null;

    public NettyClientHandler(NettyClient nettyClient) {
        super();
        this.nettyClient = nettyClient;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        String strMsg = (String) msg;
//        Log.d("回复的消息：", strMsg);
        System.out.println("回复的消息："+strMsg);
//        new CommandDecoder(strMsg).decode();//将返回的消息进行解析
        super.channelRead(ctx, msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        Log.d("ClientHandler", "-------重连回调------");
        nettyClient.setConnectState(NettyClient.DISCONNECTION);
        nettyClient.connect();
        super.channelInactive(ctx);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
//        Log.d("NettyClientHandl", "registered");
        super.channelRegistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        System.out.println("active invoked");

        String msg = "{ \"message_type\":\"register_client\",\"client_type\":2,\"mac_address\":\"00:0c:29:e1:d7:c1\"}";
        byte[] src = msg.getBytes();
        int len = src.length;
        byte[] bytes = DataFormUtil.intToBytes(len);
        byte[] output = DataFormUtil.unitByteArray(bytes,src);
        ByteBuf byteBuf = ctx.alloc().buffer();
        byteBuf.writeBytes(output);

        ChannelFuture f= ctx.writeAndFlush(byteBuf);
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {

            }
        });


    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        Log.d("NettyClientHandl", "网络异常!");
        super.exceptionCaught(ctx, cause);
        ctx.close();
    }
}
