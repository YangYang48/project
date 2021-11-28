package com.example.lib;

//真实诉讼人（原告人）
public class Plaintiff implements Ilawsuit{
    @Override
    public void describ() {
        System.out.println("Plaintiff describ case, the company boss Wage arrears");
    }
}
