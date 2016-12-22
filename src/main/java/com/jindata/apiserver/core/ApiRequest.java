package com.jindata.apiserver.core;

import com.google.gson.JsonObject;
import com.jindata.apiserver.service.RequestParamException;
import com.jindata.apiserver.service.ServiceException;

public interface ApiRequest {
    public enum HTTP_METHOD {
        POST,
        GET,
        PUT,
        DELETE
    };
    public void requestParamValidation(HTTP_METHOD method) throws RequestParamException;
    
    public void service() throws ServiceException;
    
    public void executeService();
    
    public JsonObject getApiResult();
}
