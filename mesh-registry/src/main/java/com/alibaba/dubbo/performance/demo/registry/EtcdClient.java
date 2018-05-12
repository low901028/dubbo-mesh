package com.alibaba.dubbo.performance.demo.registry;

import com.alibaba.dubbo.performance.demo.common.RpcException;
import com.alibaba.dubbo.performance.demo.common.ServerNode;
import com.alibaba.dubbo.performance.demo.monitor.common.DynamicHostSet;
import com.coreos.jetcd.Client;
import com.coreos.jetcd.Watch;
import com.coreos.jetcd.data.ByteSequence;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 客户端
 */
public class EtcdClient implements IRegistry {
    /** LOGGER */
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    /** etcd配置路径 */
    private final String configPath;

    /** {@link PathChildrenCache} */
    private PathChildrenCache cachedPath;

    /** {@link Client} */
    private final Client etcd;

    /** {@link DynamicHostSet} */
    private final DynamicHostSet hostSet = new DynamicHostSet();

    /** {@link ServerNode} */
    private final ServerNode clientNode;

    /** 锁对象 */
    private final Object lock = new Object();

    /**
     * @param configPath
     * @param etcd
     * @param clientNode
     */
    public EtcdClient(String configPath, Client etcd, ServerNode clientNode) {
        super();
        this.configPath = configPath;
        this.etcd = etcd;
        this.clientNode = clientNode;
    }

    @Override
    public void register(String config) throws RpcException {
        // 构建etcd节点
        addListener(config);
        buildPathClients(config);
        buildPathChildrenCache(true);
        build();

        try {
            cachedPath.start(StartMode.POST_INITIALIZED_EVENT);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * 创建clients节点
     * <p>
     *
     * @param config
     *            配置信息
     * @throws RpcException
     * @return 是否创建节点
     */
    private boolean buildPathClients(String config) throws RpcException {
        if (StringUtils.isEmpty(clientNode.getExt())) { // 新增节点
            String address = clientNode.genAddress() + ":i_";
            StringBuilder pathBuilder = new StringBuilder(Constants.ZK_SEPARATOR_DEFAULT + configPath);
            pathBuilder.append(Constants.ZK_SEPARATOR_DEFAULT).append(Constants.ZK_NAMESPACE_CLIENTS).append(Constants.ZK_SEPARATOR_DEFAULT).append(address);
            // 创建节点
            try {
                String pathName = zookeeper.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(pathBuilder.toString(),
                        config.getBytes(Constants.UTF8));
                if (StringUtils.isNotEmpty(pathName)) {
                    clientNode.setExt(pathName.substring(pathName.indexOf(":i_") + 1));
                }
                return true;
            } catch (Exception e) {
                String message = MessageFormat.format("Create node error in the path : {0}", pathBuilder.toString());
                LOGGER.error(message, e);
                throw new RpcException(message, e);
            }
        } else { // 节点丢失后，再增加
            String address = clientNode.genAddress();
            StringBuilder pathBuilder = new StringBuilder(configPath);
            pathBuilder.append(Constants.ZK_SEPARATOR_DEFAULT).append(Constants.ZK_NAMESPACE_CLIENTS).append(Constants.ZK_SEPARATOR_DEFAULT).append(address);
            // 创建节点
            try {
                // 注意：zk重启的过程中，节点可能会存在
                if (zookeeper.checkExists().forPath(pathBuilder.toString()) == null) {
                    zookeeper.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(pathBuilder.toString(), config.getBytes(Constants.UTF8));
                    return true;
                }
            } catch (Exception e) {
                String message = MessageFormat.format("Create node error in the path : {0}", pathBuilder.toString());
                LOGGER.error(message, e);
                throw new RpcException(message, e);
            }
        }
        return false;
    }

    /**
     * 添加监听器，防止网络异常或者zookeeper挂掉的情况
     * <p>
     *
     * @param config
     *            配置信息
     */
    private void addListener(final String config) {
        zookeeper.getConnectionStateListenable().addListener(new ConnectionStateListener() {
            @Override
            public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
                if (connectionState == ConnectionState.LOST) {
                    while (true) {
                        try {
                            if (curatorFramework.getZookeeperClient().blockUntilConnectedOrTimedOut()) {
                                if (buildPathClients(config)) {
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            LOGGER.error(e.getMessage(), e);
                            break;
                        }
                    }
                }
            }
        });
    }

    /**
     * 创建缓存路径
     * <p>
     *
     * @param cacheData
     */
    private void buildPathChildrenCache(Boolean cacheData) {
        cachedPath = new PathChildrenCache(zookeeper, Constants.ZK_SEPARATOR_DEFAULT + configPath + Constants.ZK_SEPARATOR_DEFAULT + Constants.ZK_NAMESPACE_SERVERS, cacheData);
        cachedPath.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) {
                PathChildrenCacheEvent.Type eventType = event.getType();
                switch (eventType) {
                    case CONNECTION_SUSPENDED:
                    case CONNECTION_LOST:
                        LOGGER.error("Connection error,waiting...");
                        return;
                    default:
                }
                // 任何节点的时机数据变动,都会rebuild,此处为一个"简单的"做法.
                try {
                    cachedPath.rebuild();
                    rebuild();
                } catch (Exception e) {
                    LOGGER.error("CachedPath rebuild error!", e);
                }
            }
        });
    }

    /**
     * 构建服务信息 <br>
     * <br>
     * 注意：构建时直接操作zookeeper，不使用PathChildrenCache,原因参考：{@link PathChildrenCache}
     * <p>
     *
     * @throws RpcException
     */
    private void build() throws RpcException {
        List<String> childrenList = null;
        String path = Constants.ZK_SEPARATOR_DEFAULT + configPath + Constants.ZK_SEPARATOR_DEFAULT + Constants.ZK_NAMESPACE_SERVERS;
        try {
            childrenList = zookeeper.getChildren().forPath(path);
        } catch (Exception e) {
            String message = MessageFormat.format("Get children node error in the path : {0}", path);
            LOGGER.error(message, e);
            throw new RpcException(message, e);
        }

        if (CollectionUtils.isEmpty(childrenList)) {
            LOGGER.error("Not find a service in zookeeper!");
            throw new RpcException("Not find a service in zookeeper!");
        }

        List<ServerNode> current = new ArrayList<ServerNode>();
        for (String children : childrenList) {
            current.addAll(ServerNodeUtils.transfer(children));
        }
        freshContainer(current);
    }

    /**
     * 重新构建服务信息
     * <p>
     */
    protected void rebuild() {
        List<ChildData> children = cachedPath.getCurrentData();
        if (children == null || children.isEmpty()) {
            // 有可能所有的thrift server都与zookeeper断开了链接
            // 但是,有可能,thrift client与thrift server之间的网络是良好的
            // 所以这种情况不清除live节点
            // <code> hostSet.getLives().clear();</>
            LOGGER.error("Thrift server-cluster error!");
            return;
        }
        List<ServerNode> current = new ArrayList<ServerNode>();
        for (ChildData data : children) {
            String path = data.getPath();
            String address = path.substring(path.lastIndexOf(Constants.ZK_SEPARATOR_DEFAULT) + 1);
            LOGGER.debug("Server address {}.", address);
            current.addAll(ServerNodeUtils.transfer(address));
        }
        freshContainer(current);
    }

    /**
     * 刷新容器
     * <p>
     *
     * @param current
     */
    private void freshContainer(List<ServerNode> current) {
        synchronized (lock) {
            hostSet.replaceWithList(current);
        }
    }

    @Override
    public DynamicHostSet findAllService() {
        return hostSet;
    }

    @Override
    public void unregister() {
        try {
            cachedPath.close();
            zookeeper.close();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
