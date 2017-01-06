package com.jindata.apiserver.service.dto;

import java.util.Date;

import lombok.Data;

@Data public class Roleuser {
    private long userno;
    private long groupno;
    private long roleno;
    private Date writedate;
    private long addeduserno;
    private User user;
    private Group group;
    private Role role;
}
