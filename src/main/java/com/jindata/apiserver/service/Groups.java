package com.jindata.apiserver.service;

import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.jindata.apiserver.core.RESTApiRequestTemplate;
import com.jindata.apiserver.core.RequestParamException;
import com.jindata.apiserver.core.ServiceException;

@Service("Groups")
@Scope("prototype")
public class Groups extends RESTApiRequestTemplate {
    @Autowired
    private SqlSession sqlSession;
    
    private String id;

    public Groups(Map<String, String> reqHeader,Map<String, String> reqData) {
        super(reqHeader,reqData);
        // TODO Auto-generated constructor stub
    }
    @Override
    public void requestParamValidation(HTTP_METHOD method) throws RequestParamException {
        if(method == HTTP_METHOD.POST) {
            if(StringUtils.isEmpty(this.reqData.get("name"))){
                throw new RequestParamException("Name is required");
            }
        }
    }

    @Override
    public void post() throws ServiceException {
        if(StringUtils.isEmpty(this.id)){
            sqlSession.insert("group.insertGroup",this.reqData);
        }else{
            this.reqData.put("groupno", this.id);
            sqlSession.update("group.updateGroup", this.reqData);
        }
    }

    @Override
    public void get() throws ServiceException {
        if(StringUtils.isEmpty(this.id)){
            sqlSession.selectList("group.SelectList", this.reqData);
        }else{
            sqlSession.selectOne("group.SelectOne", this.reqData);
        }

    }

    @Override
    public void put() throws ServiceException {
        // TODO Auto-generated method stub

    }

    @Override
    public void delete() throws ServiceException {
        sqlSession.delete("grpup.DeleteOne",this.reqData);
    }
    @Override
    public void setId(String id) throws ServiceException {
        this.id = id;
        this.reqData.put("groupno",id);
    }

}
