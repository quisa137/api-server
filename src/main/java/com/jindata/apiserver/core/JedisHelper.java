package com.jindata.apiserver.core;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class JedisHelper {
    protected static final String REDIS_HOST = "127.0.0.1";
    protected static final int REDIS_PORT = 6379;
    private final Set<Jedis> connectionList = new HashSet<Jedis>();
    private final JedisPool pool;
    
    private JedisHelper() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(20);
        config.setBlockWhenExhausted(true);
        
        this.pool = new JedisPool(config,REDIS_HOST,REDIS_PORT);
    }
    
    private static class LazyHolder {
        @SuppressWarnings("synthetic-access")
        private static final JedisHelper INSTANCE = new JedisHelper();
    }
    
    @SuppressWarnings("synthetic-access")
    public static JedisHelper getInstance() {
        return LazyHolder.INSTANCE;
    }
    
    final public Jedis getConnection() {
        Jedis jedis = this.pool.getResource();
        this.connectionList.add(jedis);
        return jedis;
    }
    
    @SuppressWarnings("deprecation")
    final public void returnResource(Jedis jedis) {
        this.pool.returnResource(jedis);
    }
    
    final public void destroyPool() {
        this.pool.destroy();
    }

}
