package com.jindata.apiserver.service;

import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.jindata.apiserver.core.ApiRequestTemplate;

@Service("userInfo")
@Scope("prototype")
public class UserInfo extends ApiRequestTemplate {
    @Autowired
    private SqlSession sqlSession;
    
    public UserInfo(Map<String, String> reqData) {
        super(reqData);
    }

    public void requestParamValidation() throws RequestParamException {
        if(StringUtils.isEmpty(this.reqData.get("email"))) {
            throw new RequestParamException("Not have Email");
        }
    }

    public void service() throws ServiceException {
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
