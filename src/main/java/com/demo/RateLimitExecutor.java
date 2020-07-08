package com.demo;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

/** This class calculates the TPS and applies the throttling policy It uses granular write locking for thread safety. */
@Data
@Slf4j
public class RateLimitExecutor {
    /* Defaults to seconds */
    private TimeUnit timeUnit;
    /* Current transactions counted */
    private long transactions = 0L;
    /* Transactions per second allowed */
    private long threshold;
    /* Calculated Transactions per second */
    private long tps;
    /* Throttle key */
    private String instanceId;
    /* Timestamp for evaluation */
    private double timeStamp;
    /* Thread Safety aids */
    private ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private WriteLock wLock = rwLock.writeLock();
    /* Callback handle */
    private RateLimitListener listener;

    public RateLimitExecutor() {
        this.timeStamp = System.currentTimeMillis();
    }

    public void evaluate() {
        log.info("Starting Rate Limit evaluation. Threshold set is: {}", threshold);
        ++transactions;
        wLock.lock();
        // Take the current timestamp
        long currentTime = System.currentTimeMillis();
        // Get the delta time elapsed
        double deltaTime = (currentTime - timeStamp);
        log.info("Delta time elapsed: {}", deltaTime);
        // Calculate transactions per second
        tps = (long) (transactions / deltaTime * 1000L);
        // Don’t print TPS on the very first hit as its misleading
        if (transactions != 1) System.out.println("TPS is {}" + tps);
        // What is higher, TPS threshold or transactions per second? Exclude the very first transaction to avoid 
        // false positives
        if (tps >= threshold && transactions != 1) {
            System.out.println(
                "Rate limit has been breached, Transaction Number: " + transactions + "in delta time(milliseconds): "
                    + deltaTime + "Threshold: " + threshold);
            RateLimitManager.getInstance().getThreadPool().execute(new WorkerThread(listener));
        }
        // Leave write lock
        wLock.unlock();
    }

    public void build(TimeUnit time, long threshold) {
        this.timeUnit = time;
        this.threshold = threshold;
    }
}
