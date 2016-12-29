package com.jindata.apiserver.service;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jindata.apiserver.core.ApiRequestTemplate;
import com.jindata.apiserver.core.JedisHelper;

import redis.clients.jedis.Jedis;

@Service("tokenVerify")
@Scope("prototype")
public class TokenVerify extends ApiRequestTemplate {
    private static final JedisHelper helper = JedisHelper.getInstance();
    public TokenVerify(Map<String, String> reqHeader,Map<String, String> reqData) {
        super(reqHeader,reqData);
        // TODO Auto-generated constructor stub
    }

    public void requestParamValidation(HTTP_METHOD method) throws RequestParamException {
        if(StringUtils.isEmpty(this.reqData.get("token"))) {
            throw new RequestParamException("token이 없습니다");
        }
    }

    public void service() throws ServiceException {
        Jedis conn = null;
        try {
            conn = helper.getConnection();
            String tokenString = conn.get(this.reqData.get("token"));
            if(tokenString == null) {
                this.sendNotFound();
            } else {
                Gson gson = new Gson();
                JsonObject token = gson.fromJson(tokenString, JsonObject.class);
                
                this.apiResult.addProperty("resultCode", "200");
                this.apiResult.addProperty("message", "Success");
                this.apiResult.add("issueDate", token.get("issueDate"));
                this.apiResult.add("email", token.get("email"));
                this.apiResult.add("userNo", token.get("userNo"));
            }
        } catch(Exception e) {
            helper.returnResource(conn);
        }
    }

}
