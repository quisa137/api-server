package com.jindata.apiserver.service.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data public class User {
    private enum USERTYPE {ADMIN,MANAGER,USER};
    private long userno;
    private String username;
    private String password;
    private String email;
    private String isdeleted;
    private String isEmailAuth;
    private Date lastLogin;
    private Date writedate;
    private long grantuserno;
    private USERTYPE usertype;
    private List<Group> groups;
    private List<Role> roles;
    private List<Roletarget> roletargets;
    public void setUsertype(String usertype) {
        if(usertype.equals("U")){
            this.usertype = USERTYPE.USER;
        }else if(usertype.equals("M")){
            this.usertype = USERTYPE.MANAGER;
        }else if(usertype.equals("A")){
            this.usertype = USERTYPE.ADMIN;
        }
    }
}
