package com.jindata.apiserver;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

public class ApiServerMain {

    public static void main(String[] args) {
        AbstractApplicationContext springContext = null;
        try {
            springContext = new AnnotationConfigApplicationContext(ApiServerConfig.class);
            springContext.registerShutdownHook();
            
            ApiServer server= springContext.getBean(ApiServer.class);
            server.start();
            
        } finally {
            if(springContext!=null) springContext.close();
        }
    }
}
