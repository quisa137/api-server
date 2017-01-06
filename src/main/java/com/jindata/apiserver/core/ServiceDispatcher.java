package com.jindata.apiserver.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
    
    public static ApiRequest dispatch(Map<String,String> requestHeader,Map<String,String> requestBody) {
        
        String serviceUri = requestBody.get("REQUEST_URI");
        String beanName = null;
        
        List<Entry<String, JsonElement>> urimap = (ArrayList<Entry<String, JsonElement>>) springContext.getBean("uriMap");
        
        for (Entry<String, JsonElement> entry : urimap) {
            if(serviceUri.startsWith("/" + entry.getKey())){
                String httpMethod = requestBody.get("REQUEST_METHOD").toLowerCase();
                JsonElement je = entry.getValue();
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
            if(!beanName.equals("notFound") 
                    && UriAccessController.isAccessible(requestHeader.get("accessToken"), serviceUri, requestBody.get("REQUEST_METHOD")) == false) {
                service = (ApiRequest) springContext.getBean("Unauthorized", requestHeader, requestBody);
            }else{
                service = (ApiRequest) springContext.getBean(beanName, requestHeader, requestBody);
            }
        } catch(Exception e) {
            e.printStackTrace();
            service = (ApiRequest) springContext.getBean("notFound", requestHeader, requestBody);
        }
        
        return service;
    }
}
