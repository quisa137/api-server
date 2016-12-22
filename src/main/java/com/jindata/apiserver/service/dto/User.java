package com.jindata.apiserver.service.dto;

public class User {
    private int userno;
    private String username;
    private String password;
    private String email;
    
    public User() {
    }

    /**
     * @return the userno
     */
    public int getUserno() {
        return userno;
    }

    /**
     * @param userno the userno to set
     */
    public void setUserno(int userno) {
        this.userno = userno;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

}
