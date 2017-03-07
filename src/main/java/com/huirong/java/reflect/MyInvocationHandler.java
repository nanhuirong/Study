package com.huirong.java.reflect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by huirong on 17-3-6.
 * 要完成动态代理, 需要定义一个invocationHandler接口的实现子类,
 */
public class MyInvocationHandler implements InvocationHandler {
    private Object obj = null;
    public Object bind(Object obj){
        this.obj = obj;
        return Proxy.newProxyInstance(obj.getClass().getClassLoader(),
                obj.getClass().getInterfaces(), this);
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object temp = method.invoke(this.obj, args);
        return temp;
    }
}
