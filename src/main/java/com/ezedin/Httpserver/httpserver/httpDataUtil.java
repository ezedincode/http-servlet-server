package com.ezedin.Httpserver.httpserver;

import com.ezedin.Httpserver.httpserver.models.HttpMethod;

import java.util.HashMap;

public class httpDataUtil {
    private httpDataUtil() {

    }

    public static httpRequest parseBasicHttpRequest(String startLine) {
        var info = startLine.split(":");
        return new httpRequest(HttpMethod.valueOf(info[0]), info[1], info[2], new HashMap<>(), null);
    }
}
