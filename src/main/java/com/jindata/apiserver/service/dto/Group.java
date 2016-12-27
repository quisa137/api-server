package com.jindata.apiserver.service.dto;

import java.util.List;

import lombok.Data;

@Data public class Group {
    private int groupno;
    private String name;
    private String desc;
    private List<User> users;
}
