package com.jms.cleanse.net;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import robot.boocax.com.sdkmodule.entity.entity_app.LoginEntity;

public class NettyClient {

    public static final int DISCONNECTION = 0;
    public static final int CONNECTING = 1;
    public static final int CONNECTED = 2;

    private EventLoopGroup group = null;
    private Bootstrap bootstrap = null;
    private ChannelFuture channelFuture = null;
    private static NettyClient nettyClient = null;
    private ArrayBlockingQueue<String> sendQueue = new ArrayBlockingQueue<String>(5000);
    private boolean sendFlag = true;
    private SendThread sendThread = new SendThread();
    private int port = 6789;

    private int connectState = DISCONNECTION;
    private boolean flag = true;

    /***
     *  singleton
     * @return
     */
    public static NettyClient getInstance() {
        if (nettyClient == null) {
            nettyClient = new NettyClient();
        }
        return nettyClient;
    }

    public ChannelFuture getChannelFuture() {
        return channelFuture;
    }

    private NettyClient() {
        init();
    }

    private void init() {
        setConnectState(DISCONNECTION);
        bootstrap = new Bootstrap();
        group = new NioEventLoopGroup();
        bootstrap.group(group);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline pipeline = socketChannel.pipeline();
                pipeline.addLast("clientHandler", new NettyClientHandler(nettyClient));
            }
        });
        startSendThread();
    }

    public void uninit() {
        stopSendThread();
        if (channelFuture != null) {
            channelFuture.channel().closeFuture();
            channelFuture.channel().close();
            channelFuture = null;
        }
        if (group != null) {
            group.shutdownGracefully();
            group = null;
            nettyClient = null;
            bootstrap = null;
        }
        setConnectState(DISCONNECTION);
        flag = false;
    }

    public void insertCmd(String cmd) {
        sendQueue.offer(cmd);
    }

    private void stopSendThread() {
        sendQueue.clear();
        sendFlag = false;
        sendThread.interrupt();
    }

    private void startSendThread() {
        sendQueue.clear();
        sendFlag = true;
        sendThread.start();
    }

    public void connect() {
        if (getConnectState() != CONNECTED) {
            setConnectState(CONNECTING);
            ChannelFuture f = bootstrap.connect(LoginEntity.serverIP, port);
            f.addListener(listener);
        }
    }

    private ChannelFutureListener listener = new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            if (future.isSuccess()) {
                channelFuture = future;
                setConnectState(CONNECTED);
            } else {
                setConnectState(DISCONNECTION);
                future.channel().eventLoop().schedule(new Runnable() {
                    @Override
                    public void run() {
                        if (flag) {
                            connect();
                        }
                    }
                }, 3L, TimeUnit.SECONDS);
            }
        }
    };

    public void setConnectState(int connectState) {
        this.connectState = connectState;
    }

    public int getConnectState() {
        return connectState;
    }

    /**
     * 发送消息的线程
     */
    private class SendThread extends Thread {
        @Override
        public void run() {
            while (sendFlag) {
                try {
                    String cmd = sendQueue.take();
                    if (channelFuture != null && cmd != null) {
//                        channelFuture.channel().writeAndFlush(ByteBufUtils.getSendByteBuf(cmd));
                    }
                } catch (InterruptedException e) {
                    sendThread.interrupt();
                }
            }
        }
    }

}
