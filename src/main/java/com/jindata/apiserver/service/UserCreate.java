package com.jindata.apiserver.service;

import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.jindata.apiserver.core.ApiRequestTemplate;
import com.jindata.apiserver.service.dto.Users;

@Service("userCreate")
@Scope("prototype")
public class UserCreate  extends ApiRequestTemplate {
    @Autowired
    private SqlSession sqlSession;
    
    public UserCreate(Map<String, String> reqData) {
        super(reqData);
    }

    @Override
    public void requestParamValidation() throws RequestParamException {
        if(StringUtils.isEmpty(reqData.get("email"))){
            throw new RequestParamException("이메일이 없습니다.");
        }
        if(StringUtils.isEmpty(reqData.get("username"))){
            throw new RequestParamException("사용자 이름이 없습니다.");
        }
        if(StringUtils.isEmpty(reqData.get("password"))){
            throw new RequestParamException("암호가 없습니다.");
        }
    }

    @Override
    public void service() throws ServiceException {
        Users user = new Users();
        user.setEmail(reqData.get("email"));
        user.setUsername(reqData.get("username"));
        user.setPassword(reqData.get("password"));
        
        if(sqlSession.insert("userInfoInsert", user)>0){
            this.sendSuccess();
        };
    }

}
