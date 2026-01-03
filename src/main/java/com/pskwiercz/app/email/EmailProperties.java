package com.pskwiercz.app.email;

import lombok.Data;

@Data
public class EmailProperties {

    public static final String DEFAULT_HOST = "smtp.gmail.com";
    public static final int DEFAULT_PORT = 587;
    public static final String DEFAULT_SENDER = "myEmail@gmail.com";
    public static final String DEFAULT_USERNAME = "myEmail@gmail.com";
    public static final String DEFAULT_PASSWORD = "defaultPassword";
    public static final boolean DEFAULT_AUTH = true;
    public static final boolean DEFAULT_STARTTLS = true;
}