package com.example.lib;

//律师事务所的律师
public class Lawyer implements Ilawsuit{
    Ilawsuit mIlawsuit = null;

    //实现外部注入
    public Lawyer(Ilawsuit ilawsuit){
        mIlawsuit = ilawsuit;
    }

    @Override
    public void describ() {
        System.out.println("Instead of client description");
        mIlawsuit.describ();
    }
}
