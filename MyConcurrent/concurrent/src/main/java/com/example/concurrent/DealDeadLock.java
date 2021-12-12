package com.example.concurrent;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DealDeadLock {
    //对象锁
    public static Object obj1 = new Object();//第一个锁
    public static Object obj2 = new Object();//第二个锁
    //可重入锁
    private static Lock obj3 = new ReentrantLock();//第一个锁
    private static Lock obj4 = new ReentrantLock();//第二个锁

    //1.破坏死锁的第一种方式 增加资源数
    static class addObj{
        static void OperatorOneScramble() throws InterruptedException {
            String threadName = Thread.currentThread().getName();
            System.out.println(threadName+" get first-1");
            System.out.println(threadName+" get second-1");
            Thread.sleep(100);
        }

        static void OperatorTwoScramble() throws InterruptedException {
            String threadName = Thread.currentThread().getName();
            System.out.println(threadName+" get first-2");
            System.out.println(threadName+" get second-2");
            Thread.sleep(100);
        }

        //子线程，代表操作者1
        static class OperatorOne1 extends Thread{
            private String name;

            public OperatorOne1(String name) {
                this.name = name;
            }

            @Override
            public void run() {
                Thread.currentThread().setName(name);
                try {
                    addObj.OperatorOneScramble();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        //子线程，代表操作者2
        static class OperatorTwo1 extends Thread{
            private String name;

            public OperatorTwo1(String name) {
                this.name = name;
            }

            @Override
            public void run() {
                Thread.currentThread().setName(name);
                try {
                    addObj.OperatorTwoScramble();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //2.破坏死锁的第二种方式 通过尝试拿资源的方式
    static class tryLock{
        static void OperatorOneScramble() throws InterruptedException {
            String threadName = Thread.currentThread().getName();
            Random r = new Random();
            while(true)
            {
                if (obj3.tryLock()){
                    System.out.println(threadName+" get first");
                    try{
                        if (obj4.tryLock()){
                           try {
                               System.out.println(threadName+" get second");
                               System.out.println(threadName+" success done!");
                               break;
                           }finally {
                               System.out.println(threadName+" obj4.unlock");
                               obj4.unlock();
                           }
                        }
                    }finally {
                        obj3.unlock();
                    }
                }
                Thread.sleep(r.nextInt(3));
            }
        }

        static void OperatorTwoScramble() throws InterruptedException {
            String threadName = Thread.currentThread().getName();
            Random r = new Random();
            while(true)
            {
                if (obj4.tryLock()){
                    System.out.println(threadName+" get first");
                    try{
                        if (obj3.tryLock()){
                            try{
                                System.out.println(threadName+" get second");
                                System.out.println(threadName+" success done!");
                                break;
                            }finally {
                                System.out.println(threadName+" obj3.unlock");
                                obj3.unlock();
                            }
                        }
                    }finally {
                        obj4.unlock();
                    }
                }
                Thread.sleep(r.nextInt(3));
            }
        }

        //子线程，代表操作者1
        static class OperatorOne2 extends Thread{
            private String name;

            public OperatorOne2(String name) {
                this.name = name;
            }

            @Override
            public void run() {
                Thread.currentThread().setName(name);
                try {
                    tryLock.OperatorOneScramble();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        //子线程，代表操作者2
        static class OperatorTwo2 extends Thread{
            private String name;

            public OperatorTwo2(String name) {
                this.name = name;
            }

            @Override
            public void run() {
                Thread.currentThread().setName(name);
                try {
                    tryLock.OperatorTwoScramble();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //3.破坏死锁的第三种方式 原子性的方式拿资源
    static class atomicLock{
        static void OperatorOneScramble() throws InterruptedException {
            String threadName = Thread.currentThread().getName();
            synchronized (obj1){
                System.out.println(threadName+" get first");
                System.out.println(threadName+" get second");
                Thread.sleep(100);
            }
        }

        static void OperatorTwoScramble() throws InterruptedException {
            String threadName = Thread.currentThread().getName();
            synchronized (obj1){
                System.out.println(threadName+" get first");
                System.out.println(threadName+" get second");
                Thread.sleep(100);
            }
        }

        //子线程，代表操作者1
        static class OperatorOne3 extends Thread{
            private String name;

            public OperatorOne3(String name) {
                this.name = name;
            }

            @Override
            public void run() {
                Thread.currentThread().setName(name);
                try {
                    atomicLock.OperatorOneScramble();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        //子线程，代表操作者2
        static class OperatorTwo3 extends Thread{
            private String name;

            public OperatorTwo3(String name) {
                this.name = name;
            }

            @Override
            public void run() {
                Thread.currentThread().setName(name);
                try {
                    atomicLock.OperatorTwoScramble();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //4.破坏死锁的第四种方式 按照顺序obj1-->obj2的方式拿资源
    static class SequenceLock{
        static void OperatorOneScramble() throws InterruptedException {
            String threadName = Thread.currentThread().getName();
            synchronized (obj1){
                System.out.println(threadName+" get first");
                Thread.sleep(100);
                synchronized (obj2){
                    System.out.println(threadName+" get second");
                }
            }
        }

        static void OperatorTwoScramble() throws InterruptedException {
            String threadName = Thread.currentThread().getName();
            synchronized (obj1){
                System.out.println(threadName+" get first");
                Thread.sleep(100);
                synchronized (obj2){
                    System.out.println(threadName+" get second");
                }
            }
        }

        //子线程，代表操作者1
        static class OperatorOne4 extends Thread{
            private String name;

            public OperatorOne4(String name) {
                this.name = name;
            }

            @Override
            public void run() {
                Thread.currentThread().setName(name);
                try {
                    SequenceLock.OperatorOneScramble();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        //子线程，代表操作者2
        static class OperatorTwo4 extends Thread{
            private String name;

            public OperatorTwo4(String name) {
                this.name = name;
            }

            @Override
            public void run() {
                Thread.currentThread().setName(name);
                try {
                    SequenceLock.OperatorTwoScramble();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
