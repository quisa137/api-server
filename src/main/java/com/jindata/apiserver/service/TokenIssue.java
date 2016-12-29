package com.jindata.apiserver.service;

import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.gson.JsonObject;
import com.jindata.apiserver.core.ApiRequestTemplate;
import com.jindata.apiserver.core.JedisHelper;
import com.jindata.apiserver.core.KeyMaker;
import com.jindata.apiserver.service.dao.TokenKey;

import redis.clients.jedis.Jedis;

@Service("tokenIssue")
@Scope("prototype")
public class TokenIssue extends ApiRequestTemplate {
    private static final JedisHelper helper = JedisHelper.getInstance();
    
    @Autowired
    private SqlSession sqlSession;

    public TokenIssue(Map<String, String> reqHeader,Map<String, String> reqData) {
        super(reqHeader,reqData);
        // TODO Auto-generated constructor stub
    }

    public void requestParamValidation(HTTP_METHOD method) throws RequestParamException {
        if(StringUtils.isEmpty(this.reqData.get("userNo"))) {
            throw new RequestParamException("userNo가 없습니다");
        }
        
        if(StringUtils.isEmpty(this.reqData.get("password"))) {
            throw new RequestParamException("password가 없습니다");
        }
    }

    public void service() throws ServiceException {
        Jedis jedis = null;
        
        try {
            Map<String,String> result = sqlSession.selectOne("users.userInfoByPassword",this.reqData);
            if(result!=null){
                final long threeHour = 60 * 60 * 3;
                long issueDate = System.currentTimeMillis();
                String email = String.valueOf(result.get("USERID"));
                
                JsonObject token = new JsonObject();
                token.addProperty("issueDate", issueDate);
                token.addProperty("expireDate", issueDate + threeHour);
                token.addProperty("email", email);
                token.addProperty("userNo", reqData.get("userNo"));
                
                //token 저장
                KeyMaker tokenKey = new TokenKey(email, this.reqData.get("REQUEST_CLIENT_IP"),this.reqHeader.get("User-Agent"));
                jedis = helper.getConnection();
                jedis.setex(tokenKey.getKey(), (int) threeHour, token.toString());
                
                //helper
                this.apiResult.addProperty("resultCode", "200");
                this.apiResult.addProperty("message", "Success");
                this.apiResult.addProperty("token", tokenKey.getKey());
            } else {
                this.apiResult.addProperty("resultCode", "404");
                this.apiResult.addProperty("message", "authFailed");
            }
            
            helper.returnResource(jedis);
        } catch (Exception e) {
            helper.returnResource(jedis);
        }
    }
}
