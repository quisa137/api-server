package com.jindata.apiserver.core;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;
import com.jindata.apiserver.service.RequestParamException;
import com.jindata.apiserver.service.ServiceException;

public abstract class ApiRequestTemplate implements ApiRequest {
    protected Logger logger;
    
    protected Map<String,String> reqData;
    
    protected JsonObject apiResult;

    public ApiRequestTemplate(Map<String,String> reqData) {
        this.logger = LogManager.getLogger(this.getClass());
        this.apiResult = new JsonObject();
        this.reqData = reqData;
        
        logger.info("request data : " + this.reqData);
    }

    public void executeService() {
        try {
            this.requestParamValidation();
            
            this.service();
            
            
        }catch (RequestParamException e) {
            logger.error(e);
            this.apiResult.addProperty("resultCode", "405");

        }catch (ServiceException e) {
            logger.error(e);
            this.apiResult.addProperty("resultCode", "501");
        }
    }

    public JsonObject getApiResult() {
        return this.apiResult;
    }
    protected void sendSuccess() {
        this.apiResult.addProperty("resultCode", "200");
        this.apiResult.addProperty("message", "Success");
    }
    protected void sendError(int httpCode,String message) {
        this.apiResult.addProperty("resultCode", Integer.toString(httpCode));
        this.apiResult.addProperty("message", message);
    }
}
