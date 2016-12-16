package com.jindata.apiserver.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Component
public class ServiceDispatcher {
    private static ApplicationContext springContext;
    
    @Autowired
    public void init(ApplicationContext springContext) {
        ServiceDispatcher.springContext = springContext;
    }

    protected Logger logger = LogManager.getLogger(this.getClass());
    
    public static ApiRequest dispatch(Map<String,String> requestMap) {
        
        String serviceUri = requestMap.get("REQUEST_URI");
        String beanName = null;
        
        ArrayList<Entry<String, JsonElement>> urimap = (ArrayList<Entry<String, JsonElement>>) springContext.getBean("uriMap");
        Iterator<Entry<String, JsonElement>> i =urimap.iterator();
        
        while(i.hasNext()) {
            Entry<String, JsonElement> e = i.next();
            if(serviceUri.startsWith("/"+e.getKey())){
                String httpMethod = requestMap.get("REQUEST_METHOD").toLowerCase();
                JsonElement je = e.getValue();
                if(je.isJsonObject()){
                    JsonObject obj = je.getAsJsonObject();
                    
                    if(obj.has(httpMethod)){
                        beanName = obj.get(httpMethod).getAsString();
                    }else if(obj.has("default")){
                        beanName = obj.get("default").getAsString();
                    }else{
                        beanName = "notFound";
                    }
                }else if(je.isJsonPrimitive()){
                    beanName = je.getAsString();
                }else{
                    beanName = "notFound";
                }
                break;
            }
        }

        if(serviceUri == null || beanName == null) {
            beanName = "notFound";
        }
        
        ApiRequest service = null;
        try {
            service = (ApiRequest) springContext.getBean(beanName, requestMap);
        } catch(Exception e) {
            e.printStackTrace();
            service = (ApiRequest) springContext.getBean("notFound", requestMap);
        }
        
        return service;
    }
}
