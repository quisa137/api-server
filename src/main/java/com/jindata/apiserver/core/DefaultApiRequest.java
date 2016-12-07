package com.jindata.apiserver.core;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.jindata.apiserver.service.RequestParamException;

@Service("notFound")
@Scope("prototype")
public class DefaultApiRequest extends ApiRequestTemplate {

    public DefaultApiRequest(Map<String, String> reqData) {
        super(reqData);
    }

    public void service() {
        this.apiResult.addProperty("resultCode", "404");
    }

    public void requestParamValidation() throws RequestParamException {
        // TODO Auto-generated method stub
        
    }
}