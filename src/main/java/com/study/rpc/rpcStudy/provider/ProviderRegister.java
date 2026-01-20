package com.study.rpc.rpcStudy.provider;


import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProviderRegister {

    private Map<String, Invocation<?>> serviceInstanceMap = new ConcurrentHashMap<>();

    public <I> void register(Class<I> interfaceClass, I serviceInstance){
        if (!interfaceClass.isInterface()) {
            throw new IllegalArgumentException("注册类型必须是一个借口");
        }
        if (serviceInstanceMap.putIfAbsent(interfaceClass.getName(), new Invocation<>(interfaceClass, serviceInstance)) != null) {
            throw new IllegalArgumentException("接口重复注册");
        }
    }

    public Invocation<?> findService(String serviceName){
        return serviceInstanceMap.get(serviceName);
    }

    //  服务实例
    public static class Invocation<I> {
        final I serviceInstance;
        final Class<I> interfaceClass;

        public Invocation(Class<I> interfaceClass, I serviceInstance) {
            this.serviceInstance = serviceInstance;
            this.interfaceClass = interfaceClass;
        }

        public Object invoke(String methodName, Class<?>[] paramClass, Object[] parameters) throws Exception {
            Method invokeMethod = interfaceClass.getDeclaredMethod(methodName, paramClass);
            return invokeMethod.invoke(serviceInstance, parameters);
        }
    }
}
