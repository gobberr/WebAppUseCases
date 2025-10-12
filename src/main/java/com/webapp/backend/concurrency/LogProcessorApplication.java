package com.webapp.backend.concurrency;

public class LogProcessorApplication {

    public static void main(String[] args) {

        LogProcessor processor = new LogProcessor();

        // Start multiple producers
        new Producer(processor, "Producer-A").start();
        new Producer(processor, "Producer-B").start();

        // Start multiple consumers
        new Consumer(processor, "Consumer-A").start();
        new Consumer(processor, "Consumer-B").start();
    }
}
