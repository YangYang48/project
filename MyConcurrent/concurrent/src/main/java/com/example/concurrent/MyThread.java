package com.example.concurrent;

import java.util.concurrent.Callable;

public class MyThread {
    //第一种方式
    static class PrimeThread extends Thread {
        long minPrime;
        PrimeThread(long minPrime) {
            this.minPrime = minPrime;
        }

        public void run() {
            // compute primes larger than minPrime
            System.out.println("->>>PrimeThread is starting!");
        }
    }

    //第二种方式
    static class PrimeRun implements Runnable {
        long minPrime;
        PrimeRun(long minPrime) {
            this.minPrime = minPrime;
        }

        public void run() {
            // compute primes larger than minPrime
            System.out.println("->>>PrimeRun is starting!");
        }
    }

    static class PrimCallable implements Callable<Long>{
        long minPrime;

        public PrimCallable(long minPrime) {
            this.minPrime = minPrime;
        }


        @Override
        public Long call() throws Exception {
            System.out.println("->>>PrimCallable is starting!");
            return minPrime;
        }
    }
}
