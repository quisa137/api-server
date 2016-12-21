package com.jindata.apiserver.service.dto;

public class Groups {
    private int groupno;
    private String name;
    private String desc;
    
    public Groups() {
        // TODO Auto-generated constructor stub
    }
    /**
     * @return the groupno
     */
    public int getGroupno() {
        return groupno;
    }
    /**
     * @param groupno the groupno to set
     */
    public void setGroupno(int groupno) {
        this.groupno = groupno;
    }
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return the desc
     */
    public String getDesc() {
        return desc;
    }
    /**
     * @param desc the desc to set
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

}
