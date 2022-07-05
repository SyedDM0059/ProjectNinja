package com.example.chatbot;
import org.json.JSONArray;
import org.springframework.data.annotation.Id;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
public class TokenManagement {
    RestTemplate restTemplate = new RestTemplate();
    public String tokenization() {

        String value = "keep-alive";
        String value2 = "Not;A Brand\";v=\"99\", \"Google Chrome\";v=\"97\", \"Chromium\";v=\"97";
        String value3 = "application/json, text/plain, */*";
        String value4 = "application/json";
        String value5 = "?0";
        String value6 = "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.71 Safari/537.36";
        String value7 = "sec-ch-ua-platform: \"Windows\"";
        String value8 = "Origin: https://dcmp-dev.discovermarket.com";
        String value9 = "Sec-Fetch-Site: same-site";
        String value10 = "Sec-Fetch-Mode: cors";
        String value11 = "Sec-Fetch-Dest: empty";
        String value12 = "Referer: https://dcmp-dev.discovermarket.com/";
        String value13 = "Accept-Language: en-US,en;q=0.9,vi;q=0.8";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Connection",value);
        headers.add("sec-ch-ua", value2);
        headers.add("Accept", value3);
        headers.add("Content-Type", value4);
        headers.add("sec-ch-ua-mobile", value5);
        headers.add("User-Agent", value6);
        headers.add("sec-ch-ua-platform", value7);
        headers.add("Origin", value8);
        headers.add("Sec-Fetch-Site", value9);
        headers.add("Sec-Fetch-Mode", value10);
        headers.add("Sec-Fetch-Dest", value11);
        headers.add("Referer", value12);
        headers.add("Accept-Language", value13);
        HttpEntity<String> httpEntityFirst = new HttpEntity<>("{\"username\":\"tobedeleted@discovermarket.com\",\"password\":\"Bud542381\",\"tenantCode\":\"TE0000001\"}", headers);
        ResponseEntity<String> tokenCheck = restTemplate.exchange("https://dev.apis.discovermarket.com/authentication/v2/auth/login",
                HttpMethod.POST, httpEntityFirst, String.class);
        JSONObject tokenize = new JSONObject(tokenCheck.getBody());
        return tokenize.getJSONObject("data").getString("token");
    }
}
