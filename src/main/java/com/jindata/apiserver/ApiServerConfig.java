package com.jindata.apiserver;

import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
@Configuration
@PropertySource("classpath:api-server.properties")
@ImportResource("classpath:spring/mysqlApplicationContext.xml")
@ComponentScan("com.jindata.apiserver, com.jindata.apiserver.core, com.jindata.apiserver.service")
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
    
    private List<Entry<String, JsonElement>> uriList = null;
    
    @Bean(name="bossThreadCount")
    public int getBossThreadCount() {
        return bossThreadCount; 
    }
    
    @Bean(name="workerThreadCount")
    public int getWorkerThreadCount() {
        return workerThreadCount; 
    }
    /**
     * JSON 파일을 읽어 uri 길이 순으로 정렬한 ArrayList를 리턴한다
     * @return
     */
    @Bean(name="uriMap")
    public ArrayList<Entry<String, JsonElement>> getURIMap() {
        if(uriList == null) {
            try {
                JsonElement jelement = new JsonParser().parse(new InputStreamReader(getClass().getResourceAsStream(uriMapperPath)));
                uriList = new ArrayList<>(jelement.getAsJsonObject().entrySet());
                Collections.sort(uriList,new StringLengthComparator());
            } catch (JsonIOException | JsonSyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return (ArrayList<Entry<String, JsonElement>>) uriList;
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
    
    private static class StringLengthComparator implements Comparator<Entry<String, JsonElement>> {
        @Override
        public int compare(Entry<String, JsonElement> o1, Entry<String, JsonElement> o2) {
            String s1 = o1.getKey();
            String s2 = o2.getKey();
            
            if(s1.length() > s2.length()) {
                return -1;
            }else if(s1.length() < s2.length()) {
                return 1;
            }else{
                //문자열 길이가 같으면 가나다 오름차순
                return s1.compareTo(s2);
            }
        }
    }
}
