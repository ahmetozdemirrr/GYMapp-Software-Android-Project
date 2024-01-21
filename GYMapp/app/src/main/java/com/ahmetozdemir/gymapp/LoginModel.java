package com.ahmetozdemir.gymapp;

public class LoginModel
{
    private String userName;
    private String password;

    public LoginModel (String userName, String password)
    {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName()
    {
        return this.userName;
    }

    public String getPassword()
    {
        return this.password;
    }
}
