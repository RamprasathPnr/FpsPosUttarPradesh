package com.omneagate.DTO;


import lombok.Data;

@Data
public class LoginDto {

    String userName;        // Users name

    String password;        //Users password is hashed and stored.

    String deviceId;

}
