package com.jms.cleanse.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import robot.boocax.com.sdkmodule.entity.entity_app.LoginEntity;

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
        super.channelRegistered(ctx);
    }

    /**
     *   on active method : send register message !
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        ByteBuf byteBuf = ctx.alloc().buffer();
        byteBuf.writeBytes(SendHelper.sendStr("{ \"message_type\":\"register_client\",\"client_type\":3,\"mac_address\":"+LoginEntity.robotMac+"}"));
        ctx.writeAndFlush(byteBuf);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.close();
    }
}
