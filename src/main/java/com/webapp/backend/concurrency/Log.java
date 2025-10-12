package com.webapp.backend.concurrency;

import lombok.Getter;

import java.time.LocalDateTime;

public class Log implements Runnable, Comparable<Log> {

    private final String content;
    @Getter
    private LogPriority priority;
    @Getter
    private final LocalDateTime timestamp;

    public Log(String content, LogPriority priority) {
        this.content = content;
        this.priority = priority;
        this.timestamp = LocalDateTime.now();
    }

    public synchronized void increasePriority() {
        if (priority.ordinal() < LogPriority.CRITICAL.ordinal()) {
            LogPriority prevPriority = priority;
            priority = LogPriority.values()[priority.ordinal() + 1];
            System.out.println("Log [" + content + "] priority increased from " + prevPriority + " to " + priority);
        } else {
            System.out.println("Log [" + content + "] is already at CRITICAL priority.");
        }
    }

    @Override
    public void run() {

        System.out.println(Thread.currentThread().getName() +
                " processed [" + priority + "] " + content);
        try {
            Thread.sleep(600); // simulate processing time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public int compareTo(Log other) {
        int byPriority = Integer.compare(other.priority.weight(), this.priority.weight());
        if (byPriority != 0) return byPriority;
        return this.timestamp.compareTo(other.timestamp);
    }

    @Override
    public String toString() {
        return "Log{content='" + content + "', priority=" + priority + ", timestamp=" + timestamp + "}";
    }

}
