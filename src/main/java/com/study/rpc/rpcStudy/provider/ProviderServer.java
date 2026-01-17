package com.study.rpc.rpcStudy.provider;

import com.study.rpc.rpcStudy.codec.ResponseEncoder;
import com.study.rpc.rpcStudy.codec.XHLDefineDecoder;
import com.study.rpc.rpcStudy.message.Request;
import com.study.rpc.rpcStudy.message.Response;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

public class ProviderServer {
    private int port;

    private EventLoopGroup bossEventLoopGroup;
    private EventLoopGroup workerEventLoopGroup;

    public ProviderServer(int port) {
        this.port = port;
    }

    public void start() {
        bossEventLoopGroup = new NioEventLoopGroup();
        workerEventLoopGroup = new NioEventLoopGroup(4);
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossEventLoopGroup, workerEventLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new XHLDefineDecoder())
                                    .addLast(new ResponseEncoder())
                                    .addLast(new SimpleChannelInboundHandler<Request>() {
                                        // op,param1,param2
                                        @Override
                                        protected void channelRead0(ChannelHandlerContext channel, Request s) throws Exception {
                                            System.out.println(s);
                                            Response response = new Response();
                                            response.setResult(1);
                                            channel.writeAndFlush(response);
                                        }
                                    });
                        }
                    });
            bootstrap.bind(port).sync();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        if (bossEventLoopGroup != null) {
            bossEventLoopGroup.shutdownGracefully();
        }
        if (workerEventLoopGroup != null) {
            workerEventLoopGroup.shutdownGracefully();
        }
    }

    private static int add(int a, int b) {
        return a + b;
    }
}
