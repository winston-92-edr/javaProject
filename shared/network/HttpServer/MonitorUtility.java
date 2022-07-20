package com.mynet.shared.network.HttpServer;

import com.google.gson.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.lang.reflect.Type;
import java.util.Date;

public class MonitorUtility {

    public static String getResponseBody(HttpServletRequest req) throws Exception {
        StringBuilder responseBody = new StringBuilder();
        String currentLine;
        BufferedReader reader = req.getReader();

        while ((currentLine = reader.readLine()) != null) {
            responseBody.append(currentLine);
        }

        if (responseBody.length() == 0) {
            throw new Exception("Empty Response Body");
        }
        return responseBody.toString();
    }
}
