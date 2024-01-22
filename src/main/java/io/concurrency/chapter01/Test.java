package io.concurrency.chapter01;

public class Test {
    public static void main(String[] args) {

        Temp1.VALUES[0] = 10;


        System.out.println( Temp1.VALUES[0] );
    }
}
