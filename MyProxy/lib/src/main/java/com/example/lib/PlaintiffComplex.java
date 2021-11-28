package com.example.lib;

public class PlaintiffComplex implements Ilawsuit,Ilawsuit2,Ilawsuit3{

    @Override
    public void describ() {
        System.out.println("PlaintiffComplex describ case, the company boss Wage arrears");
    }

    @Override
    public void Proof(int exhibit) {
        System.out.println("PlaintiffComplex Proof case, the company boss not be human, list exhibit = "+exhibit);
    }

    @Override
    public int recording() {
        System.out.println("PlaintiffComplex recording case, the company boss just Draw flatbread");
        return 0;
    }
}
