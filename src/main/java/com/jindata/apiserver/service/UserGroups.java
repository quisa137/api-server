package com.jindata.apiserver.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.jindata.apiserver.core.ApiRequestTemplate;
import com.jindata.apiserver.service.dto.Group;
import com.jindata.apiserver.service.dto.User;

@Service("UserGroups")
@Scope("prototype")
public class UserGroups extends ApiRequestTemplate {
    @Autowired
    private SqlSession sqlSession;

    public UserGroups(Map<String, String> reqHeader,Map<String, String> reqData) {
        super(reqHeader,reqData);
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

    @Override
    public void service() throws ServiceException {
        User result = sqlSession.selectOne("users.userLogin", this.reqData);
        if(result!=null) {
            List<Group> groups = result.getGroups();
            Gson g = new Gson();
            this.apiResult.add("groups", g.toJsonTree(groups));
        }
    }
}
