package com.jindata.apiserver.service;

import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.CredentialsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jindata.apiserver.core.ApiRequestTemplate;
import com.jindata.apiserver.core.JedisHelper;
import com.jindata.apiserver.core.KeyMaker;
import com.jindata.apiserver.core.RequestParamException;
import com.jindata.apiserver.core.ServiceException;
import com.jindata.apiserver.service.dao.Crypto;
import com.jindata.apiserver.service.dao.TokenKey;

import redis.clients.jedis.Jedis;

@Service("Login")
@Scope("prototype")
public class Login extends ApiRequestTemplate {
    @Autowired
    private SqlSession sqlSession;
    
    @Autowired
    @Qualifier("apiserverSalt")
    private String apiserverSalt;
    
    @Autowired
    @Qualifier("sessionRetentionTime")
    private int sessionRetentionTime;

    private JedisHelper helper = JedisHelper.getInstance();
    
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
        Jedis jedis = helper.getConnection();
        try{
            String email = this.reqData.get("email");
            KeyMaker tokenKey = new TokenKey(email, this.reqData.get("REQUEST_CLIENT_IP"),this.reqHeader.get("User-Agent"));
            String hashKey = tokenKey.getKey();
            String access_token = "";
            
            Subject currentUser = SecurityUtils.getSubject();
            
            Sha256Hash hashkey = new Sha256Hash(this.reqData.get("password"),apiserverSalt);
            
            UsernamePasswordToken pwdToken = new UsernamePasswordToken(email, hashkey.toString(),hashKey);
            pwdToken.setRememberMe(true);
            
            currentUser.login(pwdToken);
            
            if(currentUser.isAuthenticated()) {
                this.reqData.put("password", hashkey.toString());
                
                if(jedis.exists(pwdToken.getHost())) {
                    String infoText = jedis.get(hashKey);
                    JsonObject userinfo = new JsonParser().parse(infoText).getAsJsonObject();
                    long issueDate = System.currentTimeMillis();
                    long expireDate = userinfo.get("expireDate").getAsLong();
                    
                    if(expireDate == 0 || expireDate < issueDate){
                        long newExpireDate = issueDate+sessionRetentionTime;
                        access_token = Crypto.encrypt(String.join("_", tokenKey.getKey(),Long.toString(newExpireDate)));
                        userinfo.addProperty("accessToken", access_token);
                        userinfo.addProperty("issueDate", issueDate);
                        userinfo.addProperty("expireDate", newExpireDate);
                        jedis.setex(hashKey,sessionRetentionTime, userinfo.toString());
                    }else{
                        access_token = userinfo.get("accessToken").getAsString();
                    }
                    this.apiResult.addProperty("token", access_token);
                }
                this.sendSuccess();
            }
        }catch(AccountException e){
            this.sendError(400, "Could not Find Account");
        }catch(CredentialsException e){
            this.sendError(400, "Password isn`t Collect");
        }catch(AuthenticationException e){
            this.sendError(400, e.getMessage());
        }catch(Exception e){
            this.sendError(500, "Error in login process"+e.getMessage());
        }finally{
            helper.returnResource(jedis);
        }
    }
}
