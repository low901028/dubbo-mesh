package com.alibaba.dubbo.performance.demo.monitor.common;

public interface DynamicSet<K> {

    void monitor(HostChangeMonitor<K> monitor);
}
