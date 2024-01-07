package io.concurrency.chapter01.exam02;

/**
 * Thread 1, Thread 2, Thread 3이 번갈아가면서 실행되는 것을 확인할 수 있다.
 * 또한 Thread 1 이 먼저 실행된 경우, 이후 Thread 2, Thread 3 중 하나가 실행되는데, 이 때 컨텍스트 스위칭이 발생한다.
 */

public class ContextSwitchExample {
    public static void main(String[] args) {

        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("Thread 1: " + i);
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("Thread 2: " + i);
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread thread3 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("Thread 3: " + i);
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        thread1.start();
        thread2.start();
        thread3.start();
    }
}

