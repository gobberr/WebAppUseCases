package com.webapp.backend.concurrency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.*;

@Component
public class LogProcessor {

    private final BlockingQueue<Log> queue = new PriorityBlockingQueue<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public LogProcessor() {
        // Schedule a task to check and update log priorities every 2 seconds
        scheduler.scheduleAtFixedRate(this::updateLogPriorities, 2, 2, TimeUnit.SECONDS);
    }

    public void produce(Log task) {
        queue.offer(task);
    }

    public Log consume() throws InterruptedException {
        return queue.take();
    }

    private void updateLogPriorities() {
        for (Log log : queue) {
            if (log.getPriority() != LogPriority.CRITICAL &&
                    Duration.between(log.getTimestamp(), LocalDateTime.now()).getSeconds() > 3) {
                log.increasePriority();
            }
        }
    }

}
