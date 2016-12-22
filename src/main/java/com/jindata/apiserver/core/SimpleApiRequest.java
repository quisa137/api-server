package com.jindata.apiserver.core;

import com.jindata.apiserver.service.ServiceException;

public interface SimpleApiRequest extends ApiRequest {
    public void post() throws ServiceException;
    
    public void get() throws ServiceException;
    
    public void put() throws ServiceException;
    
    public void delete() throws ServiceException;
}
