package com.jindata.apiserver.service.dao;


import com.jindata.apiserver.core.KeyMaker;

import redis.clients.util.MurmurHash;

public class TokenKey implements KeyMaker {
    static final int SEED_MURMURHASH = 0x1234ABCD;

    private String email;
    private long issueDate;

    /**
     * 키 메이커 클래스를 위한 생성자.
     * 
     * @param email
     *            키 생성을 위한 인덱스 값
     * @param issueDate
     */
    public TokenKey(String email, long issueDate) {
        this.email = email;
        this.issueDate = issueDate;
    }

    public String getKey() {
        String source = email + String.valueOf(issueDate);

        return Long.toString(MurmurHash.hash64A(source.getBytes(), SEED_MURMURHASH), 16);
    }
}