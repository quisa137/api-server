package com.jindata.apiserver.core;

import java.util.Date;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.jindata.apiserver.service.dao.Crypto;

import redis.clients.jedis.Jedis;

@Component
public class UriAccessController {
    @Autowired
    private SqlSession sqlSession;
    private static final JedisHelper helper = JedisHelper.getInstance();
    
    public boolean isAccessible(String token,String uri){
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
        
        //유저정보로 권한이 있는지 확인
        
        
        return false;
    }
}
