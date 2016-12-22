package com.jindata.apiserver.service;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.jindata.apiserver.core.ApiRequestTemplate;
import com.jindata.apiserver.core.JedisHelper;
import com.jindata.apiserver.core.ApiRequest.HTTP_METHOD;

import redis.clients.jedis.Jedis;

@Service("tokenExpire")
@Scope("prototype")
public class TokenExpire extends ApiRequestTemplate {
    private static final JedisHelper helper = JedisHelper.getInstance();

    public TokenExpire(Map<String, String> reqData) {
        super(reqData);
        // TODO Auto-generated constructor stub
    }

    public void requestParamValidation(HTTP_METHOD method) throws RequestParamException {
        if(StringUtils.isEmpty(this.reqData.get("token"))){
            throw new RequestParamException("Token이 없습니다");
        }
    }

    public void service() throws ServiceException {
        Jedis conn = null;
        try {
            conn = helper.getConnection();
            long result = conn.del(this.reqData.get("token"));
            System.out.println(result);
            
            this.apiResult.addProperty("resultCode", "200");
            this.apiResult.addProperty("message", "Success");
            this.apiResult.addProperty("token", this.reqData.get("token"));
        }catch (Exception e) {
            helper.returnResource(conn);
        }
    }

}
