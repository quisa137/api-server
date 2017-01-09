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
import com.jindata.apiserver.service.dao.TokenUtil;

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
        TokenUtil tokenutil = new TokenUtil(this.reqHeader.get("accessToken"));
        
        if(tokenutil.isVaild()){
            this.sendSuccess();
            this.apiResult.addProperty("message", "This Token is useable");
        }else{
            this.sendError(403, tokenutil.getError());
        }
    }
}
