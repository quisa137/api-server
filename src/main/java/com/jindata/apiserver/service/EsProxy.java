package com.jindata.apiserver.service;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.jindata.apiserver.core.ApiRequestTemplate;

@Scope("prototype")
@Service("EsProxy")
public class EsProxy extends ApiRequestTemplate {

    public EsProxy(Map<String, String> reqHeader,Map<String, String> reqData) {
        super(reqHeader,reqData);
    }

    @Override
    public void requestParamValidation(HTTP_METHOD method) throws RequestParamException {
        
    }

    @Override
    public void service() throws ServiceException {
        this.apiResult.addProperty("Exproxy", "True");
        this.sendSuccess();
    }
}
