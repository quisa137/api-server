package com.jindata.apiserver.service.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data public class Group {
    private long groupno;
    private String name;
    private String description;
    private long addeduserno;
    private Date writedate;
    private List<User> users;
    private List<Role> roles;
}
