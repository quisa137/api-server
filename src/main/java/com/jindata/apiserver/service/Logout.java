package com.jindata.apiserver.service;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.jindata.apiserver.core.ApiRequestTemplate;
import com.jindata.apiserver.core.JedisHelper;
import com.jindata.apiserver.core.RequestParamException;
import com.jindata.apiserver.core.ServiceException;
import com.jindata.apiserver.service.dao.Crypto;

import redis.clients.jedis.Jedis;

@Scope("prototype")
@Service("Logout")
public class Logout extends ApiRequestTemplate{
    private static final JedisHelper helper = JedisHelper.getInstance();
    public Logout(Map<String, String> reqHeader, Map<String, String> reqData) {
        super(reqHeader, reqData);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void requestParamValidation(HTTP_METHOD method) throws RequestParamException {
        if(StringUtils.isEmpty(this.reqHeader.get("accessToken"))){
            throw new RequestParamException("토큰이 없습니다.");
        }
    }

    @Override
    public void service() throws ServiceException {
        String[] plainText = Crypto.decrypt(this.reqHeader.get("accessToken")).split("_");
        if(plainText.length!=2){
            throw new ServiceException("잘못된 토큰입니다.");
        }
        String hashkey=plainText[0],expireDate = plainText[1];
                
        Jedis jedis = helper.getConnection();
        
        if(jedis.del(hashkey) == 1){
            helper.returnResource(jedis);
            this.sendSuccess();
        }else{
            this.sendError(500, "로그아웃 실패");
        }
    }
}
