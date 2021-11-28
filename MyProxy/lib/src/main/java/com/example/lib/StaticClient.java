package com.example.lib;

public class StaticClient {
    public static void main(String[] args){
        //原告人的身份是一名打工人
        Ilawsuit worker = new Plaintiff();
        //打工人委托给有经验的律师进行打官司
        Ilawsuit lawyer = new Lawyer(worker);
        //律师大法庭上描述案件，开始举证
        lawyer.describ();
    }
}
