package com.study.rpc.nettyStudy;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import java.util.*;

public class MyNetty {

    public static void main(String[] args) {

        Map<Channel, List<String>> db = new HashMap<>();
        // 设置boss线程组和worker线程组
        ServerBootstrap serverBootstrap = new ServerBootstrap().group(new NioEventLoopGroup(), new NioEventLoopGroup())
                // 设置服务器channel类型
                .channel(NioServerSocketChannel.class)
                // 对每个子连接设置handler, 每个子连接都是SocketChannel类型
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new LineBasedFrameDecoder(1024))
                                .addLast(new StringDecoder())
                                // 写回channel的编码器
                                .addLast(new StringEncoder())
                                // 解码String类型
                                .addLast(new ReadHandler())
                                .addLast(new DBHandler(db));
                    }
                });
        ChannelFuture bind = serverBootstrap.bind(8080);
        bind.addListener(f-> {
            if (f.isSuccess()) {
                System.out.println("成功监听端口8080");
            }
            else {
                System.out.println("监听失败8080");
            }
        });
    }
    static class ReadHandler extends SimpleChannelInboundHandler<String> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
            System.out.println(s);
            String message = s + "个屁\n";
            ctx.channel().writeAndFlush(message);
            // 传播事件
            ctx.fireChannelRead(s);
        }
    }

    static class DBHandler extends SimpleChannelInboundHandler<String> {
        private Map<Channel, List<String>> db;

        public DBHandler(Map<Channel, List<String>> db) {
            this.db = db;
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
            List<String> strings = db.computeIfAbsent(ctx.channel(), k -> new ArrayList<>());
            strings.add(s);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            List<String> strings = db.get(ctx.channel());
            System.out.println(strings);
        }

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            System.out.println("channel注册了");
        }

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
            System.out.println("channel关闭了");
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("channel可用了");
        }
    }
}
