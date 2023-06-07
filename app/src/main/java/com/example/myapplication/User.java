package com.example.myapplication;

public class User {
    private String name, email , password , phone;
    public  User (){}
    public  User (String name ,String passwoerd , String email, String phone){
        this.email=email;
        this.name=name;
        this.password=passwoerd;
        this.phone=phone;


    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public String getPassword(){
        return password;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public String getPhone(){
        return phone;
    }
    public void setPhone(String phone){
        this.phone = phone;
    }


    public void setVerified(boolean b) {
    }
}






