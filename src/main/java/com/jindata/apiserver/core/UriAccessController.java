package com.jindata.apiserver.core;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.jindata.apiserver.service.dao.Crypto;
import com.jindata.apiserver.service.dao.TokenUtil;
import com.jindata.apiserver.service.dao.TokenUtil.TokenInfo;
import com.jindata.apiserver.service.dto.Roletarget;
import com.jindata.apiserver.service.dto.User;

import redis.clients.jedis.Jedis;

@Component
public class UriAccessController {
    private static ApplicationContext springContext;
    @Autowired
    public void init(ApplicationContext springContext) {
        UriAccessController.springContext = springContext;
    }
    
    private static final JedisHelper helper = JedisHelper.getInstance();
    
    public static boolean isAccessible(String token,String uri,String method) {
        
        List<String> anymousAccessibles = (List<String>) springContext.getBean("accessibleList");
        
        if(StringUtils.isEmpty(method)) {
            method = "all";
        }
        
        if(StringUtils.isEmpty(token)||StringUtils.isEmpty(uri)) {
            return false;
        }

        for(String accesible:anymousAccessibles){
            if(uri.equals(accesible)){
                return true;
            }
        }
        
        TokenUtil tokenutil = new TokenUtil(token);
        if(!tokenutil.isVaild()){
            return false;
        }
        User userinfo = tokenutil.getUser();
        List<Roletarget> targets = userinfo.getRoletargets();
        
        if(targets==null) {
            return false;
        }
        
        for(Roletarget target:targets) {
            if(uri.matches(target.getTargetURI()) && (target.getTargetMethod().equals("A") || method.equals(target.getTargetMethod()))) {
                return true;
            }
        }
        
        return false;
    }
}
