package com.ezedin.Httpserver.httpserver;

import com.ezedin.Httpserver.httpserver.models.HttpMethod;

import java.util.HashMap;

public class httpDataUtil {
    private httpDataUtil() {

    }

    public static httpRequest parseBasicHttpRequest(String startLine) {
        if (startLine == null || startLine.isBlank()) {
            throw new IllegalArgumentException("Empty HTTP request (startLine is null)");
        }
        var info = startLine.split(" ");
        return new httpRequest(HttpMethod.valueOf(info[0]), info[1], info[2], new HashMap<>(), null);
    }
}
