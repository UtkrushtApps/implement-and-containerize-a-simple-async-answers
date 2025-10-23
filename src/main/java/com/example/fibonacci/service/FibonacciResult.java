package com.example.fibonacci.service;

public class FibonacciResult {
    public enum Status {
        PENDING,
        COMPLETED,
        FAILED
    }

    private final int n;
    private final Status status;
    private final Long result;
    private final String error;

    public FibonacciResult(int n, Status status, Long result, String error) {
        this.n = n;
        this.status = status;
        this.result = result;
        this.error = error;
    }

    public static FibonacciResult pending(int n) {
        return new FibonacciResult(n, Status.PENDING, null, null);
    }
    public static FibonacciResult completed(int n, long result) {
        return new FibonacciResult(n, Status.COMPLETED, result, null);
    }
    public static FibonacciResult failed(int n, Throwable error) {
        return new FibonacciResult(n, Status.FAILED, null, error.getMessage());
    }

    public int getN() {
        return n;
    }
    public Status getStatus() {
        return status;
    }
    public Long getResult() {
        return result;
    }
    public String getError() {
        return error;
    }
}
