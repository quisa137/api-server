package com.jindata.apiserver.core;

import java.util.Map;

import com.jindata.apiserver.service.RequestParamException;
import com.jindata.apiserver.service.ServiceException;

public abstract class SimpleApiRequestTemplate extends ApiRequestTemplate implements SimpleApiRequest {

    public SimpleApiRequestTemplate(Map<String, String> reqData) {
        super(reqData);
        // TODO Auto-generated constructor stub
    }
    
    public void executeService() {
        try {
            HTTP_METHOD method = HTTP_METHOD.valueOf(this.reqData.get("REQUEST_METHOD"));
            this.requestParamValidation(method);
            
            switch(method) {
            case POST:
                this.post();
                break;
            case GET:
                this.get();
                break;
            case PUT:
                this.put();
                break;
            case DELETE:
                this.delete();
                break;
            default:
                this.sendNotFound();
                break;
            }
        }catch (RequestParamException e) {
            logger.error(e);
            this.apiResult.addProperty("resultCode", "405");

        }catch (ServiceException e) {
            logger.error(e);
            this.apiResult.addProperty("resultCode", "501");
        }
    }
    @Override
    public final void service() throws ServiceException {
        this.get();
    }
}
