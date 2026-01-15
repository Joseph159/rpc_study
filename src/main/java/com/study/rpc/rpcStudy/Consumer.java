package com.study.rpc.rpcStudy;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.concurrent.CompletableFuture;

public class Consumer {

    public int add(int a, int b) throws Exception {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup(4))
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new LineBasedFrameDecoder(1024))
                                .addLast(new StringDecoder())
                                .addLast(new StringEncoder())
                                .addLast(new SimpleChannelInboundHandler<String>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
                                        int i = Integer.parseInt(s);
                                        future.complete(i);
                                        channelHandlerContext.close();
                                    }
                                });
                    }
                });
        ChannelFuture channelFuture = bootstrap.connect("localhost", 8888).sync();
        channelFuture.channel().writeAndFlush("add," +  a + "," + b + "\n");

        return future.get();
    }
}
