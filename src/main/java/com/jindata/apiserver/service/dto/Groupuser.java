package com.jindata.apiserver.service.dto;

import java.util.Date;

import lombok.Data;

@Data public class Groupuser {
    private long groupno;
    private long userno;
    private Date writedate;
    private long addeduserno;
    private Group group;
    private User user;
}
