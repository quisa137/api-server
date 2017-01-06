package com.jindata.apiserver.service.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data public class User {
    private long userno;
    private String username;
    private String password;
    private String email;
    private String isdeleted;
    private String isEmailAuth;
    private Date lastLogin;
    private Date writedate;
    private long grantuserno;
    private List<Group> groups;
    private List<Role> roles;
    private List<Roletarget> roletargets;
}
