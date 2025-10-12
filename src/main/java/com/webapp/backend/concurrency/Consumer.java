package com.webapp.backend.concurrency;



public class Consumer extends Thread {

    private final LogProcessor processor;

    public Consumer(LogProcessor processor, String name) {
        super(name);
        this.processor = processor;
    }

    @Override
    public void run() {
        try {
            while (!isInterrupted()) {
                Log task = processor.consume();
                task.run();
                Thread.sleep(500); // simulate work
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
