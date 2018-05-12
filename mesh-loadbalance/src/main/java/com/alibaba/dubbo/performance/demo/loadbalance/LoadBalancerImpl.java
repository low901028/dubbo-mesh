package com.alibaba.dubbo.performance.demo.loadbalance;

import com.alibaba.dubbo.performance.demo.loadbalance.strategy.LoadBalancingStrategy;
import com.alibaba.dubbo.performance.demo.monitor.common.Closure;
import com.alibaba.dubbo.performance.demo.monitor.common.DynamicSet;
import com.alibaba.dubbo.performance.demo.monitor.common.HostChangeMonitor;
import com.alibaba.dubbo.performance.demo.monitor.common.ResourceExhaustedException;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;


import java.util.Collection;
import java.util.Set;

public class LoadBalancerImpl<K> implements LoadBalancer<K> {

    private final LoadBalancingStrategy<K> strategy;

    private Set<K> offeredBackends = ImmutableSet.of();

    /**
     * Creates a new load balancer that will use the given strategy.
     * 
     * @param strategy
     *            Strategy to delegate load balancing work to.
     * @param hostSet
     */
    public LoadBalancerImpl(LoadBalancingStrategy<K> strategy, DynamicSet<K> hostSet) {
        this.strategy = Preconditions.checkNotNull(strategy);
        if (hostSet != null) {
            hostSet.monitor(new HostChangeMonitor<K>() {
                @Override
                public void onChange(ImmutableSet<K> hostAndPorts) {
                    offerBackends(hostAndPorts);
                }
            });
        }
    }

    @Override
    public synchronized void offerBackends(Set<K> offeredBackends) {
        this.offeredBackends = ImmutableSet.copyOf(offeredBackends);
        this.strategy.offerBackends(this.offeredBackends, new Closure<Collection<K>>() {
            @Override
            public void execute(Collection<K> item) {

            }
        });
    }

    @Override
    public synchronized K nextBackend() throws ResourceExhaustedException {
        return strategy.nextBackend();
    }

    @Override
    public void requestResult(K key, RequestResult result, long requestTimeNanos) {
        this.strategy.addConnectResult(key, result, requestTimeNanos);
    }

    /**
     * Convenience method to create a new load balancer.
     * 
     * @param strategy
     *            Strategy to use.
     * @param <K>
     *            Backend type.
     * @return A new load balancer.
     */
    public static <K> LoadBalancerImpl<K> create(LoadBalancingStrategy<K> strategy, DynamicSet<K> hostSet) {
        return new LoadBalancerImpl<K>(strategy, hostSet);
    }

}
