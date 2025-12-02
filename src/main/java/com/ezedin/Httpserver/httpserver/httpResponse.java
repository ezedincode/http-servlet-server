package com.ezedin.Httpserver.httpserver;

import com.ezedin.Httpserver.httpserver.models.HttpStatus;

import java.util.Map;

public record httpResponse (
        HttpStatus httpstatus,
        Map<String,String> header,
        String body;

){

}
