package com.jindata.apiserver.service;

import java.util.Map;

import com.jindata.apiserver.core.ApiRequestTemplate;

public class EsProxy extends ApiRequestTemplate {

    public EsProxy(Map<String, String> reqHeader,Map<String, String> reqData) {
        super(reqHeader,reqData);
    }

    @Override
    public void requestParamValidation(HTTP_METHOD method) throws RequestParamException {
        
    }

    @Override
    public void service() throws ServiceException {
                
    }
}