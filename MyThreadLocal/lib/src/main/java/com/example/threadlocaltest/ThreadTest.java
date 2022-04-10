package com.example.threadlocaltest;

public class ThreadTest {
    public static void main(String[] args) throws InterruptedException {
        Thread parentThread = new Thread(() -> {
            ThreadLocal<Integer> tLocal = new ThreadLocal<>();
            tLocal.set(1);
            InheritableThreadLocal<Integer> iTLocal = new InheritableThreadLocal<>();
            iTLocal.set(2);
            System.out.println("->>>threadLocal=" + tLocal.get());
            System.out.println("->>>inheritableThreadLocal=" + iTLocal.get());

            new Thread(() -> {
                System.out.println("threadLocal=" + tLocal.get());
                System.out.println("inheritableThreadLocal=" + iTLocal.get());
                System.out.println("son Thread exit");
            }, "子线程").start();
            System.out.println("parentThread Thread exit");
        }, "父线程");
        parentThread.start();
        System.out.println("main exit");
    }
}
