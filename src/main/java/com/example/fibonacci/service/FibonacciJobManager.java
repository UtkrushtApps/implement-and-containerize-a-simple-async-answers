package com.example.fibonacci.service;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@Service
public class FibonacciJobManager implements DisposableBean {
    private final ExecutorService executorService;
    private final ScheduledExecutorService cleanupExecutor;
    private final ConcurrentMap<String, Future<Long>> jobs;
    private final ConcurrentMap<String, FibonacciResult> results;

    public FibonacciJobManager() {
        // Use cached thread pool for flexibility, tune as needed
        this.executorService = Executors.newCachedThreadPool();
        this.cleanupExecutor = Executors.newSingleThreadScheduledExecutor();
        this.jobs = new ConcurrentHashMap<>();
        this.results = new ConcurrentHashMap<>();
    }

    public String submitJob(int n) {
        String jobId = UUID.randomUUID().toString();
        Future<Long> future = executorService.submit(() -> {
            try {
                long value = fibonacci(n);
                results.put(jobId, FibonacciResult.completed(n, value));
                return value;
            } catch (Exception e) {
                results.put(jobId, FibonacciResult.failed(n, e));
                throw e;
            }
        });
        results.put(jobId, FibonacciResult.pending(n));
        jobs.put(jobId, future);
        return jobId;
    }

    public FibonacciResult getJobResult(String jobId) {
        FibonacciResult res = results.get(jobId);
        if (res == null) return null;
        if (res.getStatus() == FibonacciResult.Status.PENDING) {
            Future<Long> fut = jobs.get(jobId);
            if (fut == null) return res;
            if (fut.isDone()) {
                try {
                    long value = fut.get();
                    FibonacciResult completed = FibonacciResult.completed(res.getN(), value);
                    results.put(jobId, completed);
                    return completed;
                } catch (ExecutionException ee) {
                    FibonacciResult failed = FibonacciResult.failed(res.getN(), ee.getCause());
                    results.put(jobId, failed);
                    return failed;
                } catch (Exception e) {
                    FibonacciResult failed = FibonacciResult.failed(res.getN(), e);
                    results.put(jobId, failed);
                    return failed;
                }
            }
        }
        return res;
    }

    // Remove completed/failed jobs after they are done for N minutes
    @PostConstruct
    public void startCleaner() {
        cleanupExecutor.scheduleAtFixedRate(this::cleanupCompleted, 60, 60, TimeUnit.SECONDS);
    }

    private void cleanupCompleted() {
        for (Map.Entry<String, Future<Long>> entry : jobs.entrySet()) {
            String jobId = entry.getKey();
            Future<Long> fut = entry.getValue();
            FibonacciResult res = results.get(jobId);
            if (fut.isDone() && res != null && res.getStatus() != FibonacciResult.Status.PENDING) {
                // Remove after they are done, retain for short interval
                jobs.remove(jobId);
                results.remove(jobId);
            }
        }
    }

    @Override
    public void destroy() throws Exception {
        executorService.shutdownNow();
        cleanupExecutor.shutdownNow();
    }

    private long fibonacci(int n) {
        if (n < 0) throw new IllegalArgumentException("n must be >= 0");
        if (n <= 1) return n;
        long prev = 0, curr = 1;
        for (int i = 2; i <= n; i++) {
            long sum = prev + curr;
            prev = curr;
            curr = sum;
        }
        return curr;
    }
}
