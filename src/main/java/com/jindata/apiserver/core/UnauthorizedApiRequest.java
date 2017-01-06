package com.jindata.apiserver.core;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.jindata.apiserver.service.RequestParamException;
import com.jindata.apiserver.service.ServiceException;

@Service("Unauthorized")
@Scope("prototype")
public class UnauthorizedApiRequest extends ApiRequestTemplate {

    public UnauthorizedApiRequest(Map<String, String> reqHeader, Map<String, String> reqData) {
        super(reqHeader, reqData);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void requestParamValidation(HTTP_METHOD method) throws RequestParamException {
        // TODO Auto-generated method stub

    }

    @Override
    public void service() throws ServiceException {
        this.apiResult.addProperty("resultcode", "405");
        this.apiResult.addProperty("message", "Unauthorized API");
    }
}
