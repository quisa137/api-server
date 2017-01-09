package com.jindata.apiserver.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jindata.apiserver.core.ApiRequestTemplate;
import com.jindata.apiserver.core.JedisHelper;
import com.jindata.apiserver.core.KeyMaker;
import com.jindata.apiserver.core.RequestParamException;
import com.jindata.apiserver.core.ServiceException;
import com.jindata.apiserver.service.dao.Crypto;
import com.jindata.apiserver.service.dao.TokenKey;
import com.jindata.apiserver.service.dto.User;

import redis.clients.jedis.Jedis;

@Service("Login")
@Scope("prototype")
public class Login extends ApiRequestTemplate {
    @Autowired
    private SqlSession sqlSession;
    private static final JedisHelper helper = JedisHelper.getInstance();

    public Login(Map<String, String> reqHeader,Map<String, String> reqData) {
        super(reqHeader,reqData);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void requestParamValidation(HTTP_METHOD method) throws RequestParamException {
        if(StringUtils.isEmpty(this.reqData.get("email"))){
            throw new RequestParamException("이메일이 없습니다.");
        }
        if(StringUtils.isEmpty(this.reqData.get("password"))){
            throw new RequestParamException("암호가 없습니다.");
        }
    }
    /**
     * 로그인을 처리한다.
     * 1. 아이디/암호를 DB에서 검증
     * 2. 토큰을 생성
     * 3. 유저정보를 Hashmap에 입력
     * 4. hashmap을 json으로 변환
     * 5. Redis에 입력
     * 6. 로그인 시간 기록
     */
    @Override
    public void service() throws ServiceException {
        Jedis jedis = null;
        try{
            User result = sqlSession.selectOne("users.userLogin", this.reqData);
            
            if(result!=null) {
                final long threeHour = 1000 * 60 * 60 * 3; //60*60*3  3시간
                long issueDate = System.currentTimeMillis();
                String email = String.valueOf(result.getEmail());
                
                Map<String,Object> expinfo = new HashMap<>();
                expinfo.put("issueDate", issueDate);
                expinfo.put("expireDate", issueDate + threeHour);
                expinfo.put("email", email);
                expinfo.put("userNo", result.getUserno());
                expinfo.put("userInfo", new Gson().toJson(result));
                
                Map<String,String> param = new HashMap<>();
                param.put("userno", Long.toString(result.getUserno()));
                sqlSession.update("users.postLogin",param);
                
                KeyMaker tokenKey = new TokenKey(email, this.reqData.get("REQUEST_CLIENT_IP"),this.reqHeader.get("User-Agent"));
                
                jedis = helper.getConnection();
                String loggedUserInfo = jedis.get(tokenKey.getKey());
                
                if(StringUtils.isEmpty(loggedUserInfo)) {
                    String access_token = Crypto.encrypt(String.join("_", tokenKey.getKey(),Long.toString(issueDate + threeHour)));
                    expinfo.put("access_token", access_token);
                    Gson gson = new Gson();
                    jedis.setex(tokenKey.getKey(), (int) threeHour, gson.toJson(expinfo));
                    this.apiResult.addProperty("token", access_token);
                }else{
                    loggedUserInfo = jedis.get(tokenKey.getKey());
                    JsonObject jo = new JsonParser().parse(loggedUserInfo).getAsJsonObject();
                    this.apiResult.addProperty("token", jo.get("access_token").getAsString());
                }
                this.sendSuccess();
            }
            
            helper.returnResource(jedis);
        }catch(Exception e){
            helper.returnResource(jedis);
            this.sendError(500, "로그인 중에 에러가 발생했습니다."+e.getMessage());
        }

    }
}
