package io.concurrency.chapter02.exam02;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class MultiThreadExample {

    public static class Temp {
        int temp = 0;

        public void add() {
            temp++;
        }

        public int getTemp() {
            return temp;
        }
    }

    public static void main(String[] args) throws InterruptedException {

        Temp temp = new Temp();

        for (int i = 0; i < 5; i++) {
            Thread.sleep(1000);
            new Thread(() -> {
                privateMethod(temp); // private 메서드 호출
            }).start();
        }


        Thread.sleep(10000);

        System.out.println(temp.getTemp() + " 최종 결과");

    }

    private static void privateMethod(Temp temp) {

        temp.add();

        System.out.println(temp.getTemp());
    }
}
