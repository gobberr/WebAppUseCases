package com.webapp.backend.deadlock;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class DeadlockApplication {


    public static void main(String[] args) {

        LockManager lockManager = new LockManager();

        ReentrantLock lock1 = new ReentrantLock();
        ReentrantLock lock2 = new ReentrantLock();
        ReentrantLock lock3 = new ReentrantLock();

        List<ReentrantLock> locks = Arrays.asList(lock1, lock2, lock3);
        List<ReentrantLock> locksReversed = Arrays.asList(lock1, lock2, lock3);

        Thread t1 = createLockThread(lockManager, locks, "Thread-1");
        Thread t2 = createLockThread(lockManager, locksReversed, "Thread-2");

        t1.start();
        t2.start();
    }

    private static Thread createLockThread(LockManager lockManager, List<ReentrantLock> locks, String threadName) {
        return new Thread(() -> {
            int retries = 3; // Number of retry attempts
            while (retries > 0) {
                try {
                    if (lockManager.acquireLocks(locks, 100)) {
                        System.out.println(threadName + " acquired locks: " + locks);
                        // Critical section --> Work with shared resources here
                        // Simulate work
                        Thread.sleep(500);
                        break; // Exit loop after successful lock acquisition
                    } else {
                        System.out.println(threadName + " failed to acquire locks. Retrying...");
                        Thread.sleep((long) (Math.random() * 500)); // Random backoff
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println(threadName + " was interrupted: " + e.getMessage());
                    break;
                } finally {
                    lockManager.releaseLocks(locks);
                    System.out.println(threadName + " released locks: " + locks);
                }
                retries--;
            }
            if (retries == 0) {
                System.out.println(threadName + " exhausted all retries and failed to acquire locks.");
            }
        }, threadName);
    }

}