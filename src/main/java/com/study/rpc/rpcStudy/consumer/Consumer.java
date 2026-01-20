package com.study.rpc.rpcStudy.consumer;

import com.study.rpc.rpcStudy.api.Add;
import com.study.rpc.rpcStudy.codec.RequestEncoder;
import com.study.rpc.rpcStudy.message.Request;
import com.study.rpc.rpcStudy.message.Response;
import com.study.rpc.rpcStudy.codec.XHLDefineDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.CompletableFuture;

public class Consumer implements Add {

    public int add(int a, int b) {
        try {
            CompletableFuture<Integer> future = new CompletableFuture<>();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(new NioEventLoopGroup(4))
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new XHLDefineDecoder())
                                    .addLast(new RequestEncoder())
                                    .addLast(new SimpleChannelInboundHandler<Response>() {
                                        @Override
                                        protected void channelRead0(ChannelHandlerContext channelHandlerContext, Response s) throws Exception {
                                            int result = Integer.parseInt(s.getResult().toString());
                                            // 防止add阻塞，手动设置已完成
                                            future.complete(result);
                                        }
                                    });
                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect("localhost", 8889).sync();
            Request request = new Request();
            request.setServiceName(Add.class.getName());
            request.setMethodName("add");
            request.setParameterTypes(new Class[]{int.class, int.class});
            request.setParameters(new Object[]{a, b});

            channelFuture.channel().writeAndFlush(request);
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
