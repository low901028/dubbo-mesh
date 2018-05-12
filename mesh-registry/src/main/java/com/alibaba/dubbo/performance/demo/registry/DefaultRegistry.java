package com.alibaba.dubbo.performance.demo.registry;


import com.alibaba.dubbo.performance.demo.common.RpcException;
import com.alibaba.dubbo.performance.demo.common.ServerNode;
import com.alibaba.dubbo.performance.demo.monitor.common.DynamicHostSet;

/**
 * 默认注册方式（配置地址）
 * <p>
 *
 */
public class DefaultRegistry implements IRegistry {

    /** {@link DynamicHostSet} */
    private final DynamicHostSet hostSet = new DynamicHostSet();

    /**
     * @param serverAddress
     */
    public DefaultRegistry(String serverAddress) {
        String[] hostnames = serverAddress.split(";");// "ip:port;ip:port"
        for (String hostname : hostnames) {
            String[] address = hostname.split(":");
            hostSet.addServerInstance(new ServerNode(address[0], Integer.valueOf(address[1])));
        }
    }

    @Override
    public void register(String config) throws RpcException {
        // nothing
    }

    @Override
    public DynamicHostSet findAllService() {
        return hostSet;
    }

    @Override
    public void unregister() {
        // nothing
    }

}
