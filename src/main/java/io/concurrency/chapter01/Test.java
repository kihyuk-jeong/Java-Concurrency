package io.concurrency.chapter01;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Test {
    public static void main(String[] args) {

        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Task 1 - Search
        Runnable searchTask = () -> {
            System.out.println("start 1");
            // Call your search method here
        };

        // Task 2 - Delete
        Runnable deleteTask = () -> {
            // Call your delete method here
            System.out.println("start 2");

        };

        Runnable deleteTask2 = () -> {
            // Call your delete method here
            System.out.println("start 3");

        };

        // Execute both tasks concurrently
        executor.submit(searchTask);
        executor.submit(deleteTask);
        executor.submit(deleteTask2);
    }
}
