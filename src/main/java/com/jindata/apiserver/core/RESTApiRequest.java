package com.jindata.apiserver.core;

public interface RESTApiRequest extends ApiRequest {
    /**
     * insert와 update를 담당한다. id 가 없으면 insert 
     * id가 있으면 update이다 .id는 core에서 uri 뒤에 붙어 있는 문자열을 입력한다. 
     * @throws ServiceException
     */
    public void post() throws ServiceException;
    /**
     * list와 detail을 담당한다. id 가 없으면 list 
     * id가 있으면 detail이다 .id는 core에서 uri 뒤에 붙어 있는 문자열을 입력한다. 
     * @throws ServiceException
     */
    public void get() throws ServiceException;
    
    public void put() throws ServiceException;
    /**
     * 삭제를 맡는다.
     * @throws ServiceException
     */
    public void delete() throws ServiceException;
    
    public void setId(String id) throws ServiceException;
}
