package com.alibaba.dubbo.performance.demo.loadbalance;

public interface RequestTracker<T> {

    /**
     * 已完成的请求跟踪.
     * 
     * @param key
     *            Key to identify the owner of the request.
     * @param result
     *            Result of the request.
     * @param requestTimeNanos
     *            Time duration spent waiting for the request to complete.
     */
    void requestResult(T key, RequestResult result, long requestTimeNanos);

    enum RequestResult {
        FAILED, TIMEOUT, SUCCESS, DEAD,
    }
}