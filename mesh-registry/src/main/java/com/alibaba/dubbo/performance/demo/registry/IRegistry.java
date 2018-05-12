package com.alibaba.dubbo.performance.demo.registry;


import com.alibaba.dubbo.performance.demo.common.RpcException;
import com.alibaba.dubbo.performance.demo.monitor.common.DynamicHostSet;

/**
 * 注册中心
 * <p>
 *
 */
public interface IRegistry {

    /**
     * 注册<br>
     * 包括：provider和client
     * <p>
     * 
     * @param config
     *            配置信息
     * @throws RpcException
     */
    void register(String config) throws RpcException;

    /**
     * 获取所有服务
     * <p>
     * 
     * @return
     */
    DynamicHostSet findAllService();

    /**
     * 服务注销
     * <p>
     */
    void unregister();
}
