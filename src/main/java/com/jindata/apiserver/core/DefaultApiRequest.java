package com.jindata.apiserver.core;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service("notFound")
@Scope("prototype")
public class DefaultApiRequest extends ApiRequestTemplate {
    public DefaultApiRequest(Map<String, String> reqHeader,Map<String, String> reqData) {
        super(reqHeader, reqData);
    }

    public void service() {
        this.apiResult.addProperty("resultCode", "404");
    }

    public void requestParamValidation(HTTP_METHOD method) throws RequestParamException {
        // TODO Auto-generated method stub
    }
}