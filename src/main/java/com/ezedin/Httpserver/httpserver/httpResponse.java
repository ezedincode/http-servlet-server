package com.ezedin.Httpserver.httpserver;

import com.ezedin.Httpserver.httpserver.models.HttpStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public record httpResponse (
        HttpStatus httpstatus,
        Map<String,String> header,
        Object body
){
public static httpResponse basic(HttpStatus httpstatus) {
   return basic(httpstatus, null);
}
public static httpResponse basic(HttpStatus httpstatus, Object body) {
    return basic(httpstatus, null, body);
}
public static httpResponse basic(HttpStatus httpstatus, Map<String, String> header, Object body) {
    header = Objects.requireNonNullElse(header, new HashMap<>());
    if (body != null && !(body instanceof String)) {
        header.put("Content-Type", "json");
        header.put("Content-Length", String.valueOf(body.toString().length()));
    }else{
        header.put("Content-Type", "text/plain");
        header.put("Content-Length", String.valueOf(body.toString().length()));
    }
    return new httpResponse(httpstatus, header, body);
}

public String createHttpResponseMsg() throws JsonProcessingException {
    var strBuilder = new StringBuilder("%s %s %s\r\n".formatted(commonConstants.DEFAULT_HTTP_VERSION, httpstatus.getStatusCode(), httpstatus.getMessage()));

    for (var headerEntry : header.entrySet()) {
        strBuilder.append(String.format("%s: %s\r\n", headerEntry.getKey(), headerEntry.getValue()));
    }
    strBuilder.append("\r\n");


    if (body != null && !body.toString().isEmpty()) {
        if (!(body instanceof String)) {
            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = mapper.writeValueAsString(body);
            strBuilder.append(String.format("%s\r\n", jsonBody));
           return strBuilder.toString();
        }
        strBuilder.append(String.format("%s\r\n", body));
    }

    return strBuilder.toString();
}
}
