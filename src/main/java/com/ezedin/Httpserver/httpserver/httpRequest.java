package com.ezedin.Httpserver.httpserver;

import java.util.Map;
import com.ezedin.Httpserver.httpserver.models.HttpMethod;

public record httpRequest(
        HttpMethod httpMethod,
        String Path,
        String httpProtocolVersion,
        Map<String, String> headers,
        String body
) {

    public static  httpRequest withBody(httpRequest request,String body) {
        return new httpRequest(request.httpMethod,request.Path,request.httpProtocolVersion,request.headers,body);
    }
}
