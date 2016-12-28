package com.jindata.apiserver.service;

import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jindata.apiserver.core.JedisHelper;
import com.jindata.apiserver.core.KeyMaker;
import com.jindata.apiserver.core.SimpleApiRequestTemplate;
import com.jindata.apiserver.service.dao.TokenKey;

import redis.clients.jedis.Jedis;

@Scope("prototype")
@Service("Tokens")
public class Tokens extends SimpleApiRequestTemplate{
private static final JedisHelper helper = JedisHelper.getInstance();
    
    @Autowired
    private SqlSession sqlSession;
    public Tokens(Map<String, String> reqData) {
        super(reqData);
    }

    @Override
    public void requestParamValidation(HTTP_METHOD method) throws RequestParamException {
        if(method == HTTP_METHOD.POST) {
            if(StringUtils.isEmpty(this.reqData.get("userNo"))) {
                throw new RequestParamException("userNo가 없습니다");
            }
            
            if(StringUtils.isEmpty(this.reqData.get("password"))) {
                throw new RequestParamException("password가 없습니다");
            }
        }else if(method == HTTP_METHOD.GET) {
            if(StringUtils.isEmpty(this.reqData.get("token"))) {
                throw new RequestParamException("token이 없습니다");
            }
        }else if(method == HTTP_METHOD.DELETE) {
            if(StringUtils.isEmpty(this.reqData.get("token"))){
                throw new RequestParamException("Token이 없습니다");
            }
        }
    }
    /**
     * 토큰발급
     */
    @Override
    public void post() throws ServiceException {
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
                KeyMaker tokenKey = new TokenKey(email, issueDate);
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

    /**
     * 토큰 검증
     */
    @Override
    public void get() throws ServiceException {
        Jedis conn = null;
        try {
            conn = helper.getConnection();
            String tokenString = conn.get(this.reqData.get("token"));
            if(tokenString == null) {
                this.apiResult.addProperty("resultCode", "404");
                this.apiResult.addProperty("message", "not found");
            }else{
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

    @Override
    public void put() throws ServiceException {
        // TODO Auto-generated method stub
        
    }

    /**
     * 토큰 삭제
     */
    @Override
    public void delete() throws ServiceException {
        // TODO Auto-generated method stub
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
