package com.study.rpc.rpcStudy.provider;

import com.study.rpc.rpcStudy.api.Add;

public class AddImpl implements Add {
    @Override
    public int add(int a, int b) {
        return a + b;
    }
}
