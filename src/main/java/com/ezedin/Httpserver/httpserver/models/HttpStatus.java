package com.ezedin.Httpserver.httpserver.models;

public enum HttpStatus {

    OK("200", "OK"),
    CREATED("201", "CREATED"),

    // 400
    BAD_REQUEST("400", "BAD REQUEST0"),

    NOT_FOUND("404", "NOT FOUND"),

    INTERNAL_SERVER_ERROR("500", "INTERNAL SERVER ERROR");


    private final String statusCode ;
    private final String statusMessage ;
    HttpStatus(String statusCode,String statusMessage){
        this.statusCode=statusCode;
        this.statusMessage=statusMessage;
    }
    public  String getStatusCode(){
        return statusCode;
    }
    public  String getMessage(){
        return statusMessage;
    }
}
