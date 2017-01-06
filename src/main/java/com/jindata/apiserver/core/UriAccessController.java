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
        
        for(String accesible:anymousAccessibles){
            if(uri.equals(accesible)){
                return true;
            }
        }
        if(StringUtils.isEmpty(method)) {
            method = "all";
        }
        
        if(StringUtils.isEmpty(token)||StringUtils.isEmpty(uri)) {
            return false;
        }
        
        //token을 복호화하고 정보를 분류함
        String[] keys = Crypto.decrypt(token).split("_");
        if(keys==null || keys.length != 2) {
            return false;
        }
        String hashKey = keys[0], expireDate = keys[1];
        
        //기간이 지난 token인지 확인
        if(Long.parseLong(expireDate) < System.currentTimeMillis()){
            return false;
        }        
        
        //jedis에 연결 후, 정보가 있는지 확인
        Jedis jedis = helper.getConnection();
        String userInfoText = jedis.get(hashKey);
        if(StringUtils.isEmpty(userInfoText)) {
            return false;
        }
        
        Gson gson =  new Gson();
        
        HashMap<String,Object> userinfoMap = gson.fromJson(userInfoText,HashMap.class);
        User userinfo = gson.fromJson((String) userinfoMap.get("userInfo"), User.class);
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
