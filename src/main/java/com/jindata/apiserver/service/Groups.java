package com.jindata.apiserver.service;

import java.util.Map;

import org.springframework.util.StringUtils;

import com.jindata.apiserver.core.SimpleApiRequestTemplate;

public class Groups extends SimpleApiRequestTemplate {

    public Groups(Map<String, String> reqHeader,Map<String, String> reqData) {
        super(reqHeader,reqData);
        // TODO Auto-generated constructor stub
    }
    @Override
    public void requestParamValidation(HTTP_METHOD method) throws RequestParamException {
        
    }

    @Override
    public void post() throws ServiceException {
        // TODO Auto-generated method stub

    }

    @Override
    public void get() throws ServiceException {
        // TODO Auto-generated method stub

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
