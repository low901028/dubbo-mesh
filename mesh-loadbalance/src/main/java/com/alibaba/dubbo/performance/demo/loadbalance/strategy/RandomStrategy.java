package com.alibaba.dubbo.performance.demo.loadbalance.strategy;

import com.alibaba.dubbo.performance.demo.monitor.common.ResourceExhaustedException;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * A load balancer that selects a random backend each time a request is made..
 */
public class RandomStrategy<S> extends StaticLoadBalancingStrategy<S> {

    private List<S> targets = Lists.newArrayList();

    private final Random random;

    public RandomStrategy() {
        this(new Random());
    }

    @VisibleForTesting
    RandomStrategy(Random random) {
        this.random = Preconditions.checkNotNull(random);
    }

    @Override
    protected Collection<S> onBackendsOffered(Set<S> targets) {
        this.targets = ImmutableList.copyOf(targets);
        return this.targets;
    }

    @Override
    public S nextBackend() throws ResourceExhaustedException {
        if(targets.isEmpty()) {
            throw new ResourceExhaustedException("No backends.");
        }
        return targets.get(random.nextInt(targets.size()));
    }
}
