package com.alibaba.dubbo.performance.demo.loadbalance;

import com.alibaba.dubbo.performance.demo.loadbalance.common.ResourceExhaustedException;

import java.util.Set;


/**
 * 源码参考：<a href=
 * "https://github.com/twitter/commons/tree/master/src/java/com/twitter/common/net/loadbalancing"
 * >Twitter</a>
 * <p>
 *
 */
public interface LoadBalancer<K> extends RequestTracker<K> {

    void offerBackends(Set<K> offeredBackends);

    K nextBackend() throws ResourceExhaustedException;
}