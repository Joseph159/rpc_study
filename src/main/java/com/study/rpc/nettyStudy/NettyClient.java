package com.study.rpc.nettyStudy;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.concurrent.TimeUnit;

public class NettyClient {

    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new LineBasedFrameDecoder(1024))
                                .addLast(new StringEncoder())
                                .addLast(new StringDecoder())
                                .addLast(new SimpleChannelInboundHandler<String>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
                                        System.out.println(s);
                                    }
                                });
                    }
                });
        ChannelFuture localhost = bootstrap.connect("localhost", 8080);
        localhost.addListener(f -> {
            if (f.isSuccess()) {
                System.out.println("成功连接8080");
                // 可以执行形势任务
                EventLoop eventLoop = localhost.channel().eventLoop();
                eventLoop.scheduleAtFixedRate(() -> {
                    // 执行何种任务，第一次延迟多久，间隔多久，时间单位
                    localhost.channel().writeAndFlush("hello world" + System.currentTimeMillis() + "\n");
                    }, 0, 1, TimeUnit.SECONDS);
                }
            }
        );
    }
}
