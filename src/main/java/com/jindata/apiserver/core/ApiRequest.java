package com.jindata.apiserver.core;

import com.google.gson.JsonObject;

public interface ApiRequest {
    public enum HTTP_METHOD {
        POST,
        GET,
        PUT,
        DELETE
    };
    
    /**
     * 로직 처리에 앞서 로직에 필요한 여러가지 값을 체크하여 에러이면 RequestParamException을 내놓는다.
     * @param method
     * @throws RequestParamException
     */
    public void requestParamValidation(HTTP_METHOD method) throws RequestParamException;
    
    /**
     * 로직을 처리하여 ApiRequest에 결과값을 저장한다.
     * @throws ServiceException
     */
    public void service() throws ServiceException;
    
    public void executeService();
    
    public JsonObject getApiResult();
}
