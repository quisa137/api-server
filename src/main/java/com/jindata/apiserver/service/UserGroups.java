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
import com.jindata.apiserver.core.JedisHelper;
import com.jindata.apiserver.core.RequestParamException;
import com.jindata.apiserver.core.ServiceException;
import com.jindata.apiserver.service.dao.Crypto;
import com.jindata.apiserver.service.dao.TokenUtil;
import com.jindata.apiserver.service.dto.Group;
import com.jindata.apiserver.service.dto.User;

import redis.clients.jedis.Jedis;

@Service("UserGroups")
@Scope("prototype")
public class UserGroups extends ApiRequestTemplate {
    @Autowired
    private SqlSession sqlSession;
    @Autowired
    private static final JedisHelper helper = JedisHelper.getInstance();

    public UserGroups(Map<String, String> reqHeader,Map<String, String> reqData) {
        super(reqHeader,reqData);
    }

    @Override
    public void requestParamValidation(HTTP_METHOD method) throws RequestParamException {
        if(StringUtils.isEmpty(this.reqHeader.get("accessToken"))){
            throw new RequestParamException("has not Access Token");
        }
    }

    @Override
    public void service() throws ServiceException {
        TokenUtil tokenutil = new TokenUtil(this.reqData.get("accessToken"));
        User result = tokenutil.getUser();

        if(result!=null) {
            List<Group> groups = result.getGroups();
            Gson g = new Gson();
            this.apiResult.add("groups", g.toJsonTree(groups));
        }
    }
}
