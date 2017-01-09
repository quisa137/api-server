package com.jindata.apiserver.service.dao;

import java.util.HashMap;

import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.jindata.apiserver.core.JedisHelper;
import com.jindata.apiserver.service.dto.User;

import redis.clients.jedis.Jedis;

/**
 * 토큰을 복호화 하여 
 * @author SGcom
 */
public class TokenUtil {
    private String encryptedToken = "";
    private TokenInfo info;
    private JedisHelper helper = JedisHelper.getInstance();
    private String error = "";
    
    public TokenUtil(String token) {
        // TODO Auto-generated constructor stub
        encryptedToken = token;
        
        String plainText = Crypto.decrypt(encryptedToken);
        
        if(StringUtils.isEmpty(plainText)){
            error = "Crypto is empty";
            info = null;
        }else{
            String[] temp = plainText.split("_");
            
            if(temp != null && temp.length == 2) {
                info = new TokenInfo(temp[0], temp[1]);
            }else{
                error = "Crypto is not vaild";
                info = null;
            }
        }
    }
    
    public TokenInfo getTokeninfo(){
        return info;
    }
    public class TokenInfo {
        private TokenInfo(String hashKey,String expireDate) {
            this.hashKey = hashKey;
            this.expireDate = Long.parseLong(expireDate);
        }
        private String hashKey = "";
        private long expireDate = 0;
        public String getHashkey() {
            return hashKey;
        }
        public long getExpiredate() {
            return expireDate;
        }
    }
    
    public boolean isVaild() {
        if(StringUtils.isEmpty(encryptedToken)) {
            return false;
        }
        if(info == null) {
            return false;
        }
        if(StringUtils.isEmpty(info.getHashkey()) || StringUtils.isEmpty(info.getExpiredate())) {
            error = "Token info is not vaild";
            return false;
        }
        
        if(info.getExpiredate() < System.currentTimeMillis()){
            error = "Token is expired";
            return false;
        }
        
        Jedis jedis = helper.getConnection();
        String plainText = jedis.get(info.getHashkey());
        
        if(StringUtils.isEmpty(plainText)){
            error = "User not Signin";
            return false;
        }
        
        helper.returnResource(jedis);
        return true;
    }
    
    public User getUser() {
        if(this.isVaild()){
            Jedis jedis = helper.getConnection();
            Gson gson = new Gson();
            HashMap<String,Object> userinfoMap = gson.fromJson(jedis.get(info.getHashkey()), HashMap.class);
            helper.returnResource(jedis);
            return gson.fromJson((String) userinfoMap.get("userInfo"), User.class);
        }else{
            return null;
        }
    }
    
    public String getError() {
        return error;
    }
}
