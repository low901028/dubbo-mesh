package com.alibaba.dubbo.performance.demo.common;

/**
 * 常量类，用于共享常见的一些常量，如:utf-8等
 * <p>
 *
 */
public class Constants {

    /** utf-8 */
    public static final String UTF8 = "utf-8";

    //-------------------------------------------------zookeeper-------------------------------------------
    /** zookeeper根目录 */
    public static final String ZK_NAMESPACE_ROOT = "pjrpc";

    /** zookeeper目录分割符 */
    public static final String ZK_SEPARATOR_DEFAULT = "/";

    /** servers子目录 */
    public static final String ZK_NAMESPACE_SERVERS = "servers";

    /** clients子目录 */
    public static final String ZK_NAMESPACE_CLIENTS = "clients";

    /** configs子目录 */
    public static final String ZK_NAMESPACE_CONFIGS = "configs";

    /** statistics子目录 */
    public static final String ZK_NAMESPACE_STATISTICS = "statistics";

    /** zookeeper中使用时间戳作目录的格式 */
    public static final String ZK_TIME_NODE_FORMAT = "yyyyMMddHHmmss";

    /** zookeeper中总计节点名称 */
    public static final String ZK_NAMESPACE_TOTAL = "total";

    /** zookeeper中详细节点名称 */
    public static final String ZK_NAMESPACE_DETAIL = "detail";

    //-------------------------------------------------etcd-------------------------------------------
    /** zookeeper根目录 */
    public static final String ETCD_NAMESPACE_ROOT = "dubbo-mesh";

    /** zookeeper目录分割符 */
    public static final String ETCD_SEPARATOR_DEFAULT = "/";

    /** servers子目录 */
    public static final String ETCD_NAMESPACE_SERVERS = "servers";

    /** clients子目录 */
    public static final String ETCD_NAMESPACE_CLIENTS = "clients";

    /** configs子目录 */
    public static final String ETCD_NAMESPACE_CONFIGS = "configs";

    /** statistics子目录 */
    public static final String ETCD_NAMESPACE_STATISTICS = "statistics";

    /** ETCD中使用时间戳作目录的格式 */
    public static final String ETCD_TIME_NODE_FORMAT = "yyyyMMddHHmmss";

    /** ETCD中总计节点名称 */
    public static final String ETCD_NAMESPACE_TOTAL = "total";

    /** ETCD中详细节点名称 */
    public static final String ETCD_NAMESPACE_DETAIL = "detail";

    /**
     * private constructor
     */
    private Constants() {
        super();
    }

}
