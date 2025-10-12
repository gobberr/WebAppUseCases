package com.webapp.backend.deadlock;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class LockManager {

    // Acquire a list of locks sequentially with a timeout for each lock
    public boolean acquireLocks(List<ReentrantLock> locks, long timeoutMs) throws InterruptedException {
        long start = System.currentTimeMillis();
        for (ReentrantLock lock : locks) {
            long elapsed = System.currentTimeMillis() - start;
            long remaining = Math.max(0, timeoutMs - elapsed);
            if (!lock.tryLock(remaining, TimeUnit.MILLISECONDS)) {
                releaseLocks(locks); // Release all acquired locks if one fails
                return false;
            }
        }
        return true;
    }

    // Release locks in reverse order
    public void releaseLocks(List<ReentrantLock> locks) {
        for (int i = locks.size() - 1; i >= 0; i--) {
            ReentrantLock lock = locks.get(i);
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
