package com.alibaba.dubbo.performance.demo.registry;

import com.alibaba.dubbo.performance.demo.common.RpcException;
import com.alibaba.dubbo.performance.demo.monitor.common.DynamicHostSet;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import com.coreos.jetcd.Client;
import com.coreos.jetcd.KV;
import com.coreos.jetcd.Lease;
import com.coreos.jetcd.data.ByteSequence;
import com.coreos.jetcd.kv.GetResponse;
import com.coreos.jetcd.options.GetOption;
import com.coreos.jetcd.options.PutOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class EtcdRegistry implements IRegistry{
    /** LOGGER */
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    /**etcd**/
    private final Client client;

    /**服务etcd的目录**/
    private final String etcdPath;

    private final String address;

    /**授权**/
    private final String auth;

    public EtcdRegistry(Client client, String etcdPath, String address, String auth){
        this.client = client;
        this.etcdPath = etcdPath;
        this.address = address;
        this.auth = auth;
    }

    @Override
    public void register(String config) throws RpcException {

    }

    @Override
    public DynamicHostSet findAllService() {
        return null;
    }

    @Override
    public void unregister() {

    }
}
