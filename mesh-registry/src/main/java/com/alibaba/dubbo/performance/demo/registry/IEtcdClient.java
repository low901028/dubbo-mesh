package com.alibaba.dubbo.performance.demo.registry;

import com.alibaba.dubbo.performance.demo.registry.listener.ChildListener;
import java.util.List;

/**
 * Etcd Client接口
 */
public interface IEtcdClient {
    public void create(String path);
    public List<String> addChildListener(String path, ChildListener childListener);
    public void removeChildListener(ChildListener childListener);
    public boolean isAvailable();
    public void delete(String urlPath);
    public void close() throws InterruptedException;
    public List<String> getChildren(String path);
}
