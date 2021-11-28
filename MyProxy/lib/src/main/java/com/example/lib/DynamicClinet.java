package com.example.lib;

import java.io.FileOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DynamicClinet {
    public static void main(String[] args){
        //原告人的身份是一名打工人
        System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles","true");
        Ilawsuit worker = new Plaintiff();
        //这个new Class[]{Ilawsuit.class}里面的还可以添加，主要根据真实对象有多少个实现
        // 这里实现了BaseIlawsuit就只添加Ilawsuit.class即可
        Ilawsuit proxy = (Ilawsuit) Proxy.newProxyInstance(DynamicClinet.class.getClassLoader(),
                                                new Class[]{Ilawsuit.class}, new DynamicHandler(worker));
        proxy.describ();


        //相当于我有好几个诉讼抽象类
        // 诉讼抽象类A，主要案情描述，开始举证
        // 法律诉讼抽象类B，主要是罗列证物
        // 法律诉讼抽象类，主要是录音证据
        // 最终通过一个原告反馈在一场官司里面，不然需要拆开分成三个律师代理三场官司，太浪费钱了
        /*final PlaintiffComplex workerA = new PlaintiffComplex();

        Object obj = null;

        obj = Proxy.newProxyInstance(DynamicClinet.class.getClassLoader(),
                                     new Class[]{Ilawsuit.class, Ilawsuit2.class, Ilawsuit3.class},
                                     new InvocationHandler(){
                                         @Override
                                         public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                             return method.invoke(workerA, args);
                                         }
                                     });

        Ilawsuit ilawsuit = (Ilawsuit) obj;
        ilawsuit.describ();
        Ilawsuit2 ilawsuit2 = (Ilawsuit2) obj;
        ilawsuit2.Proof(100);
        Ilawsuit3 ilawsuit3 = (Ilawsuit3) obj;
        int ret = ilawsuit3.recording();
        System.out.println("DynamicClinet workerA recording ret ="+ret);*/
    }

    //生成class文件
    private static void proxy() throws Exception {
        String name = Ilawsuit.class.getName() + "$Proxy0";
        //生成代理指定接口的Class数据
        byte[] bytes = ProxyGenerator.generateProxyClass(name, new Class[]{Ilawsuit.class});
        FileOutputStream fos = new FileOutputStream("lib/" + name + ".class");
        fos.write(bytes);
        fos.close();
    }
}
