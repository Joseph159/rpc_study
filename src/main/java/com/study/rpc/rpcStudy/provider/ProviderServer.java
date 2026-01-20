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


public class ProviderServer {
    private final int port;
    private final ProviderRegister providerRegister;

    private EventLoopGroup bossEventLoopGroup;
    private EventLoopGroup workerEventLoopGroup;


    public ProviderServer(int port, ProviderRegister providerRegister) {
        this.port = port;
        this.providerRegister = providerRegister;
    }

    public <I> void register(Class<I> interfaceClass, I serviceInstance){
        providerRegister.register(interfaceClass, serviceInstance);
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
                                        protected void channelRead0(ChannelHandlerContext channel, Request request) throws Exception {
                                            System.out.println(request);
                                            ProviderRegister.Invocation<?> service = providerRegister.findService(request.getServiceName());
                                            Object result = service.invoke(request.getMethodName(), request.getParameterTypes(), request.getParameters());
                                            Response response = new Response();
                                            response.setResult(result);
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
}
