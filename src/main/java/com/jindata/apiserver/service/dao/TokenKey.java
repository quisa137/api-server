package com.jindata.apiserver.service.dao;


import com.jindata.apiserver.core.KeyMaker;

import redis.clients.util.MurmurHash;

public class TokenKey implements KeyMaker {
    static final int SEED_MURMURHASH = 0x1234ABCD;

    private String email;
    private String requestClientIP;
    private String userAgent;

    /**
     * 키 메이커 클래스를 위한 생성자.
     * 
     * @param email
     *            키 생성을 위한 인덱스 값
     * @param issueDate
     */
    public TokenKey(String email, String requestClientIP, String userAgent) {
        this.email = email;
        this.requestClientIP = requestClientIP;
        this.userAgent = userAgent;
    }

    public String getKey() {
        return Long.toString(MurmurHash.hash64A(String.join("_", email,requestClientIP,userAgent).getBytes(), SEED_MURMURHASH), 16);
    }
}