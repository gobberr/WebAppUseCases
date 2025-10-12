package com.webapp.backend.concurrency;

public enum LogPriority {

    LOW(0),
    NORMAL(1),
    HIGH(2),
    CRITICAL(3);

    private final int weight;

    LogPriority(int w) {
        this.weight = w;
    }

    public int weight() {
        return weight;
    }
}
