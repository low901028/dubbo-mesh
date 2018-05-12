package com.alibaba.dubbo.performance.demo.registry.listener;

import java.util.List;

public interface TargetChildListener {
    public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception;

}
