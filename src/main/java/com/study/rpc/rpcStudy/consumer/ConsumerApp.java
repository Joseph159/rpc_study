package com.study.rpc.rpcStudy.consumer;

import com.study.rpc.rpcStudy.api.Add;

public class ConsumerApp {

    public static void main(String[] args) throws Exception {
        Add consumer = new Consumer();
        System.out.println(consumer.add(1, 2));
        System.out.println(consumer.add(1, 88));
        System.out.println(consumer.add(1, 2));
    }
}
