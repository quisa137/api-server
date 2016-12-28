package com.jindata.apiserver.service.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data public class Role {
    private long roleno;
    private String name;
    private String description;
    private long groupno;
    private long addeduserno;
    private Date writedate;
    private Group group;
    private List<User> users;
    private List<Roletarget> targets;
}
