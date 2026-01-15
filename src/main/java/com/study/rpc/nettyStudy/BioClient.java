package com.study.rpc.nettyStudy;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class BioClient {
    public static void main(String[] args) throws Exception{
        Thread thread1 = new Thread(() -> {
            try {
                sendHello();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "thread-1-tom");

        Thread thread2 = new Thread(() -> {
            try {
                sendHello();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "thread-2-jerry");

        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
    }

    private static void sendHello() throws IOException {
        Socket socket = new Socket("localhost",8080);
        OutputStream os = socket.getOutputStream();
        for (int i = 0; i < 10; i++) {
            os.write((" ok " + i + " " + Thread.currentThread().getName()).getBytes());
            os.flush();
        }
        socket.close();
    }
}
