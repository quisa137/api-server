package com.jindata.apiserver.service.dto;

import java.util.List;

import lombok.Data;

@Data public class User {
    private int userno;
    private String username;
    private String password;
    private String email;
    private List<Group> groups;
    private String isdeleted;
}
