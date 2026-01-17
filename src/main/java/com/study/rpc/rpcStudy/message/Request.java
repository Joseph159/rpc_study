package com.study.rpc.rpcStudy.message;

import lombok.Data;

@Data
public class Request {
    private String serviceName;
    private String methodName;
    private String[] parameterTypes;
    private Object[] parameters;
}
