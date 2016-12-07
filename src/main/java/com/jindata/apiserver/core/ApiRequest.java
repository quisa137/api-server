package com.jindata.apiserver.core;

import com.google.gson.JsonObject;
import com.jindata.apiserver.service.RequestParamException;
import com.jindata.apiserver.service.ServiceException;

public interface ApiRequest {
    public void requestParamValidation() throws RequestParamException;
    
    public void service() throws ServiceException;
    
    public void executeService();
    
    public JsonObject getApiResult();
}
