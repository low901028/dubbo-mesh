package com.alibaba.dubbo.performance.demo.loadbalance.strategy;


import com.alibaba.dubbo.performance.demo.loadbalance.RequestTracker;
import com.alibaba.dubbo.performance.demo.monitor.common.Closure;

import java.util.Collection;
import java.util.Set;

abstract class StaticLoadBalancingStrategy<K> implements LoadBalancingStrategy<K> {

    @Override
    public final void offerBackends(Set<K> offeredBackends, Closure<Collection<K>> onBackendsChosen) {
        onBackendsChosen.execute(onBackendsOffered(offeredBackends));
    }

    protected abstract Collection<K> onBackendsOffered(Set<K> offeredBackends);

    @Override
    public void addConnectResult(K backendKey, RequestTracker.RequestResult result, long connectTimeNanos) {
        // No-op.
    }

}
