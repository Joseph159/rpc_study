package com.study.rpc.nettyStudy;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class BioServer {
    public static void main(String[] args) throws Exception{
        // 服务端socket
        ServerSocket socket = new ServerSocket(8080);
        // 客户端socket
        while (true){
            Socket accept = socket.accept();

            InputStream inputStream = accept.getInputStream();
            byte[] buffer = new byte[1024];
            int len;
            while((len = inputStream.read(buffer)) != -1){
                String message = new String(buffer, 0, len);
                System.out.println(message);
            }
            System.out.println("断开连接");
        }
    }
}
