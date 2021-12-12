package com.example.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class MyTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        /***********************************
        *线程启动的二种方式
        ***********************************/
        System.out.println("-----------线程启动的二种方式-------------");
        //Thread 启动第一种方式，直接启动
        MyThread.PrimeThread p = new MyThread.PrimeThread(143);
        p.start();

        //Thread 启动第二种方式，通过实现Runnable，将任务交给Thread执行
        MyThread.PrimeRun pTask = new MyThread.PrimeRun(143);
        new Thread(pTask).start();

        //Thread 启动第二种方式的变体，实际上是包装runnable，交给线程去执行
        MyThread.PrimCallable pCallable = new MyThread.PrimCallable(143);
        FutureTask<Long> futureTask = new FutureTask<>(pCallable);
        new Thread(futureTask).start();
        //获取返回值
        System.out.println(futureTask.get());

        /***********************************
         *破坏死锁的四种方式
         ***********************************/
        System.out.println("-----------破坏死锁的四种方式-------------");

        System.out.println("-----------dealDeadThread firstMethod-------------");
/*        DealDeadLock.addObj.OperatorOne1 addObjThread1 =
                new DealDeadLock.addObj.OperatorOne1("addObjThread1");
        DealDeadLock.addObj.OperatorTwo1 addObjThread2 =
                new DealDeadLock.addObj.OperatorTwo1("addObjThread2");
        addObjThread1.start();
        addObjThread2.start();*/

        System.out.println("-----------dealDeadThread secondMethod-------------");
        /*DealDeadLock.tryLock.OperatorOne2 tryLockThread1 =
                new DealDeadLock.tryLock.OperatorOne2("tryLockThread1");
        DealDeadLock.tryLock.OperatorTwo2 tryLockThread2 =
                new DealDeadLock.tryLock.OperatorTwo2("tryLockThread2");
        tryLockThread1.start();
        tryLockThread2.start();*/

        System.out.println("-----------dealDeadThread thirdMethod-------------");
        /*DealDeadLock.atomicLock.OperatorOne3 atomicThread1 =
                new DealDeadLock.atomicLock.OperatorOne3("atomicThread1");
        DealDeadLock.atomicLock.OperatorTwo3 atomicThread2 =
                new DealDeadLock.atomicLock.OperatorTwo3("atomicThread2");
        atomicThread1.start();
        atomicThread2.start();*/

        System.out.println("-----------dealDeadThread fourthMethod-------------");
        DealDeadLock.SequenceLock.OperatorOne4 sequenceThread1 =
                new DealDeadLock.SequenceLock.OperatorOne4("sequenceThread1");
        DealDeadLock.SequenceLock.OperatorTwo4 sequenceThread2 =
                new DealDeadLock.SequenceLock.OperatorTwo4("sequenceThread2");
        sequenceThread1.start();
        sequenceThread2.start();
    }
}