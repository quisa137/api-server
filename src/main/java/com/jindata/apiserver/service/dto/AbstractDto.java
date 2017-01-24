package com.jindata.apiserver.service.dto;

import com.google.gson.Gson;

public abstract class AbstractDto {
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
