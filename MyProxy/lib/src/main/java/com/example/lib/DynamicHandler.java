package com.example.lib;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class DynamicHandler implements InvocationHandler
{
    private Object obj;

    public DynamicHandler(final Object obj)
    {
        this.obj = obj;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("->>>before function");

        //obj是构造时候的注入，一般指的是具体对象（被代理的对象）
        // args为基类函数中的参数，method为实际的方法，意思就是调用obj真实对象的method方法，且参数为args
        Object res = method.invoke(obj, args);
        System.out.println("->>>after function");
        return res;
    }
}
