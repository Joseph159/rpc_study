package com.study.rpc.rpcStudy;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class Provider {

    public static void main(String[] args) throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup(4))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new LineBasedFrameDecoder(1024))
                                .addLast(new StringDecoder())
                                .addLast(new StringEncoder())
                                .addLast(new SimpleChannelInboundHandler<String>() {
                                    // op,param1,param2
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext channel, String s) throws Exception {
                                        String[] split = s.split(",");
                                        String method = split[0];
                                        int a = Integer.parseInt(split[1]);
                                        int b = Integer.parseInt(split[2]);
                                        if (method.equals("add")) {
                                            int result = a + b;
                                            channel.writeAndFlush(result + "\n");
                                        }
                                    }
                                });
                    }
                });
        ChannelFuture bind = bootstrap.bind(8888).sync();

    }

    private static int add(int a, int b) {
        return a + b;
    }
}
