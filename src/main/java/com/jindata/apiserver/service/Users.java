package com.jindata.apiserver.service;

import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.jindata.apiserver.core.SimpleApiRequestTemplate;
import com.jindata.apiserver.service.dto.User;

@Service("Users")
@Scope("prototype")
public class Users extends SimpleApiRequestTemplate {
    @Autowired
    private SqlSession sqlSession;
    
    public Users(Map<String, String> reqData) {
        super(reqData);
    }
    @Override
    public void requestParamValidation(HTTP_METHOD method) throws RequestParamException {
        switch(method){
        case GET:
            if(StringUtils.isEmpty(reqData.get("email"))){
                throw new RequestParamException("이메일이 없습니다.");
            }
            break;
        case POST:
            if(StringUtils.isEmpty(reqData.get("email"))){
                throw new RequestParamException("이메일이 없습니다.");
            }
            if(StringUtils.isEmpty(reqData.get("username"))){
                throw new RequestParamException("사용자 이름이 없습니다.");
            }
            if(StringUtils.isEmpty(reqData.get("password"))){
                throw new RequestParamException("암호가 없습니다.");
            }
            break;
        default:
            break;
        }
    }
    @Override
    public void post() throws ServiceException {
        User user = new User();
        user.setEmail(reqData.get("email"));
        user.setUsername(reqData.get("username"));
        user.setPassword(reqData.get("password"));
        if(sqlSession.insert("userCreate", user)>0){
            this.sendSuccess();
        };
    }

    @Override
    public void get() throws ServiceException {
        Map<String,String> result = sqlSession.selectOne("users.userInfoByEmail", this.reqData);
        if(result != null) {
            String userNo = String.valueOf(result.get("USERNO"));
            
            this.apiResult.addProperty("resultCode", "200");
            this.apiResult.addProperty("message", "Success");
            this.apiResult.addProperty("userNo", userNo);
        }else{
            this.apiResult.addProperty("resultCode", "404");
            this.apiResult.addProperty("message", "Fail");
        }
    }
    
    @Override
    public void put() throws ServiceException {
        // TODO Auto-generated method stub

    }

    @Override
    public void delete() throws ServiceException {
        // TODO Auto-generated method stub

    }
}
