package com.study.rpc.rpcStudy.provider;

import com.study.rpc.rpcStudy.api.Add;

public class ProviderApp {
    public static void main(String[] args) {
        ProviderServer server = new ProviderServer(8889, new ProviderRegister());
        server.register(Add.class, new AddImpl());
        server.start();
    }
}
