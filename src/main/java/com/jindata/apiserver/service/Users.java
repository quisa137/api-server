package com.jindata.apiserver.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.jindata.apiserver.core.RESTApiRequestTemplate;
import com.jindata.apiserver.core.RequestParamException;
import com.jindata.apiserver.core.ServiceException;
import com.jindata.apiserver.service.dto.User;

@Service("Users")
@Scope("prototype")
public class Users extends RESTApiRequestTemplate {
    @Autowired
    private SqlSession sqlSession;
    
    private String userno = "";
    public Users(Map<String, String> reqHeader,Map<String, String> reqData) {
        super(reqHeader,reqData);
    }
    @Override
    public void requestParamValidation(HTTP_METHOD method) throws RequestParamException {
        switch(method){
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
        if(StringUtils.isEmpty(this.reqData.get("email"))){
            List<User> users = sqlSession.selectList("users.userList",this.reqData);
            Gson g = new Gson();
            this.apiResult.add("list", g.toJsonTree(users));
        }else{
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
    }
    
    @Override
    public void put() throws ServiceException {
        
    }

    @Override
    public void delete() throws ServiceException {
        if(sqlSession.delete("users.deleteByEmail",this.reqData) > 0) {
            this.apiResult.addProperty("resultCode", "200");
            this.apiResult.addProperty("message", "Success");
        }
    }
    @Override
    public void setId(String id) throws ServiceException {
        this.reqData.put("userno", id);
        this.userno = id;
    }
}
