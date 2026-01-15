package com.study.rpc.nettyStudy;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class NioServer {
    public static void main(String[] args) throws Exception {
        Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress("localhost",8080));
        // 多路复用，channel本身并不具备读写功能
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            // 阻塞，监听器等待某些事件再触发
            selector.select();
            // 每一个channel对应一个selectedKey
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()) {
                    // key对应的channel，可以直接强转
                    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                    SocketChannel client = serverSocketChannel.accept();
                    client.configureBlocking(false);
                    // 让监听器同时可以监听客户端
                    client.register(selector, SelectionKey.OP_READ);
                    System.out.println("客户端开启连接" + client.getRemoteAddress());
                }
                if (key.isReadable()) {
                    SocketChannel channel = (SocketChannel) key.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(2);
                    int length = channel.read(byteBuffer);
                    // 客户端关闭连接
                    if (length == -1) {
                        System.out.println("客户端断开连接" + channel.getRemoteAddress());
                        channel.close();
                    }
                    else {
                        byteBuffer.flip();
                        byte[] buffer = new byte[byteBuffer.remaining()];
                        byteBuffer.get(buffer);
                        String message = new String(buffer);
                        System.out.println(message);
                    }
                }
            }
        }
    }
}
