package com.study.rpc.rpcStudy.provider;

public class ProviderApp {
    public static void main(String[] args) {
        ProviderServer server = new ProviderServer(8889);
        server.start();
    }
}
