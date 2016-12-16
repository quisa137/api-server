package com.jindata.apiserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
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
    
    private JsonObject json = null;
    
    @Bean(name="bossThreadCount")
    public int getBossThreadCount() {
        return bossThreadCount; 
    }
    
    @Bean(name="workerThreadCount")
    public int getWorkerThreadCount() {
        return workerThreadCount; 
    }
    
    @Bean(name="uriMap")
    public JsonObject getURIMap() {
        /*
        Map<String, String> env = System.getenv();
        Iterator i = env.keySet().iterator();
        Properties p = System.getProperties();
        
        while(i.hasNext()) {
            String key = (String) i.next();
            System.out.println("ENV : key :"+key+", value :"+env.get(key));
        }
        i = p.keySet().iterator();
        while(i.hasNext()) {
            String key = (String) i.next();
            System.out.println("Prop : key :"+key+", value :"+p.getProperty(key));
        }*/
        ;
        if(json == null) {
            try {
                JsonElement jelement = new JsonParser().parse(new InputStreamReader(getClass().getResourceAsStream(uriMapperPath)));
                json = jelement.getAsJsonObject();
            } catch (JsonIOException | JsonSyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return json ;
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
