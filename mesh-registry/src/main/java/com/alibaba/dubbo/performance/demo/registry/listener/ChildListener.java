package com.alibaba.dubbo.performance.demo.registry.listener;

import java.util.List;

public interface ChildListener {
    void childChanged(String path, List<String> children);

}
