package com.jindata.apiserver.service;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.jindata.apiserver.core.ApiRequestTemplate;

@Service("GroupUsers")
@Scope("prototype")
public class GroupUsers extends ApiRequestTemplate {

    public GroupUsers(Map<String, String> reqData) {
        super(reqData);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void requestParamValidation(HTTP_METHOD method) throws RequestParamException {
        // TODO Auto-generated method stub

    }

    @Override
    public void service() throws ServiceException {
        // TODO Auto-generated method stub

    }

}
