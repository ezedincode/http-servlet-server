package com.ezedin.Httpserver.httpserver;

import com.ezedin.Httpserver.httpserver.models.HttpStatus;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public record httpResponse (
        HttpStatus httpstatus,
        Map<String,String> header,
        String body
){
public static httpResponse basic(HttpStatus httpstatus) {
   return basic(httpstatus, null);
}
public static httpResponse basic(HttpStatus httpstatus, String body) {
    return basic(httpstatus, null, body);
}
public static httpResponse basic(HttpStatus httpstatus, Map<String, String> header, String body) {
    header = Objects.requireNonNullElse(header, new HashMap<>());
    if (body != null) {
        header.put("Content-Type", "text/plain");
        header.put("Content-Length", String.valueOf(body.length()));
    }
    return new httpResponse(httpstatus, header, body);
}
public String createHttpResponseMsg(){
    var strBuilder = new StringBuilder("%s %s %s\r\n".formatted(commonConstants.DEFAULT_HTTP_VERSION, httpStatus.getHttpStatusCode(), httpStatus.getMessage()));

    for (var headerEntry : header.entrySet()) {
        strBuilder.append(String.format("%s: %s\r\n", headerEntry.getKey(), headerEntry.getValue()));
    }
    strBuilder.append("\r\n");

    if (body != null && !body.isEmpty()) {
        strBuilder.append(String.format("%s\r\n", body));
    }

    return strBuilder.toString();
}
}
