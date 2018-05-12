package com.alibaba.dubbo.performance.demo.monitor.common;

import com.google.common.collect.ImmutableSet;

public interface HostChangeMonitor<T> {
    void onChange(ImmutableSet<T> hostAndPorts);
}
