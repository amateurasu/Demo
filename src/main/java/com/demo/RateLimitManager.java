package com.demo;

import lombok.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This is a thread safe static singleton class which maintains all the rate limiting policies in the form of a
 * concurrent hash map. The key of the map is the instance id while the value is the Rate Limit Executor object. Based
 * on the key supplied the appropriate Rate Limiter is invoked.
 */
@Data
public final class RateLimitManager {
    /**
     * Static singleton class which is thread safe from multiple instantiation race conditions in traditional
     * singletons
     */
    @Getter
    private static final RateLimitManager instance = new RateLimitManager();

    /** Container for keeping track of all provisioned rate limits. */
    private final ConcurrentHashMap<String, RateLimitExecutor> rateLimitMap = new ConcurrentHashMap<>();

    /** Thread pool initialization */
    private final ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    // To provision a new Rate Limit Policy and executor. Callback interface to receive notifications
    public void provisionRateLimit(RateLimitExecutor builder, String instanceId, RateLimitListener listener) {
        builder.setListener(listener);
        rateLimitMap.put(instanceId, builder);
    }

    // To remove a rate limit policy
    public void deProvisionRateLimit(String instanceId) {
        try {
            rateLimitMap.remove(instanceId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // API Call to peg traffic and evaluate the rate limit provisioned
    public void pegTraffic(String instanceId) {
        rateLimitMap.get(instanceId).evaluate();
    }
}
