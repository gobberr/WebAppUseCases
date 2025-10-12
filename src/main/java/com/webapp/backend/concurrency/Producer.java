package com.webapp.backend.concurrency;

public class Producer extends Thread {

    private final LogProcessor processor;
    private final String name;
    private final LogPriority[] priorities = LogPriority.values();

    public Producer(LogProcessor processor, String name) {
        this.processor = processor;
        this.name = name;
    }

    @Override
    public void run() {
        for (int i = 0; i < 25; i++) {
            LogPriority p = priorities[i % priorities.length];
            Log task = new Log(name + "-Task" + i, p);
            processor.produce(task);
            System.out.println(name + " produced " + task);
            try {
                Thread.sleep(400); // simulate time between productions
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
