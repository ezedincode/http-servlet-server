package com.ezedin.Httpserver.httpserver;

import com.ezedin.Httpserver.httpserver.models.HttpMethod;

import java.util.Arrays;
import java.util.HashMap;

public class httpDataUtil {
    private httpDataUtil() {

    }

    public static httpRequest parseBasicHttpRequest(String startLine) {
        if (startLine == null) {
            throw new IllegalArgumentException("Empty HTTP request (startLine is null)");
        }
        var info = startLine.split(" ");
        System.out.println(Arrays.toString(info));
        return new httpRequest(HttpMethod.valueOf(info[0]), info[1].trim(), info[2].trim(), new HashMap<>(), null);
    }
}
