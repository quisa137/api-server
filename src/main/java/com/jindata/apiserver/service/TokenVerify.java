package com.jindata.apiserver.service;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.jindata.apiserver.core.ApiRequestTemplate;
import com.jindata.apiserver.core.JedisHelper;
import com.jindata.apiserver.service.dao.Crypto;

import redis.clients.jedis.Jedis;

@Service("TokenVerify")
@Scope("prototype")
public class TokenVerify extends ApiRequestTemplate {
    private static final JedisHelper helper = JedisHelper.getInstance();
    public TokenVerify(Map<String, String> reqHeader,Map<String, String> reqData) {
        super(reqHeader,reqData);
    }

    public void requestParamValidation(HTTP_METHOD method) throws RequestParamException {
        if(StringUtils.isEmpty(this.reqHeader.get("accessToken"))) {
            throw new RequestParamException("Not has Token");
        }
    }

    public void service() throws ServiceException {
        String accessToken = this.reqHeader.get("accessToken");
        String plaintext = Crypto.decrypt(accessToken);
        
        if(StringUtils.isEmpty(plaintext)){
            this.sendError(403, "Token Error");
        }
        
        String[] temp = plaintext.split("_");
        if(temp.length != 2){
            this.sendError(403, "Token Error");
            return;
        }
        String hashKey = temp[0], expireDate = temp[1];
        
        Jedis jedis = helper.getConnection();
        
        if(Long.parseLong(expireDate) < System.currentTimeMillis()){
            this.sendError(403, "Token Expired");
        }
        
        if(jedis.exists(hashKey)==false){
            this.sendError(403, "Token Error");
        }
        
        this.sendSuccess();
        this.apiResult.addProperty("message", "This Token is useable");
    }

}
