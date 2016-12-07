package com.jindata.apiserver;

import java.net.InetSocketAddress;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.google.gson.annotations.JsonAdapter;

@Configuration
@ImportResource("classpath:spring/hsqlApplicationContext.xml")
@ComponentScan("com.jindata.apiserver, com.jindata.apiserver.core, com.jindata.apiserver.service")
@PropertySource("classpath:api-server.properties")
public class ApiServerConfig {
    @Value("${boss.thread.count}")
    private int bossThreadCount;
    
    @Value("${worker.thread.count}")
    private int workerThreadCount;
    
    @Value("${tcp.port}")
    private int tcpPort;
    
    @Value("${ssl.port}")
    private int sslPort;
    
    @Value("${uri.mapper}")
    private String uriMapperPath;
    
    @Bean(name="bossThreadCount")
    public int getBossThreadCount() {
        return bossThreadCount; 
    }
    
    @Bean(name="workerThreadCount")
    public int getWorkerThreadCount() {
        return workerThreadCount; 
    }
    
    @Bean(name="uriMapperPath")
    public String getURIMapperPath() {
        return uriMapperPath;
    }
    
    public int getTcpPort() {
        return tcpPort;
    }
    
    public int getSslPort() {
        return sslPort;
    }
    
    @Bean(name = "tcpSocketAddress")
    public InetSocketAddress tcpPort() {
        return new InetSocketAddress(tcpPort);
    }
    
    @Bean(name = "sslSocketAddress")
    public InetSocketAddress sslPort() {
        return new InetSocketAddress(sslPort);
    }
    
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
