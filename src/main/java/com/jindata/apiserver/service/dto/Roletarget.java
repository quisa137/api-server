package com.jindata.apiserver.service.dto;

import java.util.Date;

import lombok.Data;

@Data public class Roletarget {
    private long roleauthno;
    private long roleno;
    private String targetURI;
    private String targetMethod;
    private String isDenied;
    private long addeduserno;
    private Date writedate;
    private Role role;
    private long permission;
}
