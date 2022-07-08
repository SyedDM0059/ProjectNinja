package com.example.chatbot;

import org.springframework.http.HttpHeaders;

public class AuthHeadersManagement extends GeneralHeaders{
    public static HttpHeaders customerAuthHeaders() {
        GeneralHeaders cusHeaders = new GeneralHeaders("699");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Connection",cusHeaders.value);
        headers.add("Content-Length",cusHeaders.value1);
        headers.add("sec-ch-ua", cusHeaders.value2);
        headers.add("Accept", cusHeaders.value3);
        headers.add("Content-Type", cusHeaders.value4);
        headers.add("sec-ch-ua-mobile", cusHeaders.value5);
        headers.add("User-Agent", cusHeaders.value6);
        headers.add("sec-ch-ua-platform", cusHeaders.value7);
        headers.add("Origin", cusHeaders.value8);
        headers.add("Sec-Fetch-Site", cusHeaders.value9);
        headers.add("Sec-Fetch-Mode", cusHeaders.value10);
        headers.add("Sec-Fetch-Dest", cusHeaders.value11);
        headers.add("Referer", cusHeaders.value12);
        headers.add("Accept-Language", cusHeaders.value13);
        headers.add("Host", cusHeaders.value14);
        headers.add("Accept-Encoding", cusHeaders.value15);
        headers.add("Accept-Language", cusHeaders.value16);
        return headers;
    }
    public static HttpHeaders proposalAuthHeaders() {
        GeneralHeaders proHeaders = new GeneralHeaders("130");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Connection",proHeaders.value);
        headers.add("Content-Length",proHeaders.value1);
        headers.add("sec-ch-ua", proHeaders.value2);
        headers.add("Accept", proHeaders.value3);
        headers.add("Content-Type", proHeaders.value4);
        headers.add("sec-ch-ua-mobile", proHeaders.value5);
        headers.add("User-Agent", proHeaders.value6);
        headers.add("sec-ch-ua-platform", proHeaders.value7);
        headers.add("Origin", proHeaders.value8);
        headers.add("Sec-Fetch-Site", proHeaders.value9);
        headers.add("Sec-Fetch-Mode", proHeaders.value10);
        headers.add("Sec-Fetch-Dest", proHeaders.value11);
        headers.add("Referer", proHeaders.value12);
        headers.add("Accept-Language", proHeaders.value13);
        headers.add("Host", proHeaders.value14);
        headers.add("Accept-Encoding", proHeaders.value15);
        headers.add("Accept-Language", proHeaders.value16);
        return headers;
    }
    public static HttpHeaders fullUpdateHeaders() {
        GeneralHeaders fullUpHeaders = new GeneralHeaders("4330");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Connection",fullUpHeaders.value);
        headers.add("Content-Length",fullUpHeaders.value1);
        headers.add("sec-ch-ua", fullUpHeaders.value2);
        headers.add("Accept", fullUpHeaders.value3);
        headers.add("Content-Type", fullUpHeaders.value4);
        headers.add("sec-ch-ua-mobile", fullUpHeaders.value5);
        headers.add("User-Agent", fullUpHeaders.value6);
        headers.add("sec-ch-ua-platform", fullUpHeaders.value7);
        headers.add("Origin", fullUpHeaders.value8);
        headers.add("Sec-Fetch-Site", fullUpHeaders.value9);
        headers.add("Sec-Fetch-Mode", fullUpHeaders.value10);
        headers.add("Sec-Fetch-Dest", fullUpHeaders.value11);
        headers.add("Referer", fullUpHeaders.value12);
        headers.add("Accept-Language", fullUpHeaders.value13);
        headers.add("Host", fullUpHeaders.value14);
        headers.add("Accept-Encoding", fullUpHeaders.value15);
        headers.add("Accept-Language", fullUpHeaders.value16);
        return headers;
    }

//    public static HttpHeaders customeAuthHeaders() {
//        String value = "keep-alive";
//        String value1 = "699";
//        String value2 = ".Not/A)Brand\";v=\"99\", \"Google Chrome\";v=\"103\", \"Chromium\";v=\"103";
//        String value3 = "application/json, text/plain, */*";
//        String value4 = "application/json";
//        String value5 = "?0";
//        String value6 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/97.0.4692.71 Safari/537.36";
//        String value7 = "\"Windows\"";
//        String value8 = "https://dcmp-dev.discovermarket.com";
//        String value9 = "same-site";
//        String value10 = "cors";
//        String value11 = "empty";
//        String value12 = "https://dcmp-dev.discovermarket.com/";
//        String value13 = "en-US,en;q=0.9,vi;q=0.8";
//        String value14 = "dev.apis.discovermarket.com";
//        String value15 = "gzip, deflate, br";
//        String value16 = "en-US,en;q=0.9";
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Connection",value);
//        headers.add("Content-Length",value1);
//        headers.add("sec-ch-ua", value2);
//        headers.add("Accept", value3);
//        headers.add("Content-Type", value4);
//        headers.add("sec-ch-ua-mobile", value5);
//        headers.add("User-Agent", value6);
//        headers.add("sec-ch-ua-platform", value7);
//        headers.add("Origin", value8);
//        headers.add("Sec-Fetch-Site", value9);
//        headers.add("Sec-Fetch-Mode", value10);
//        headers.add("Sec-Fetch-Dest", value11);
//        headers.add("Referer", value12);
//        headers.add("Accept-Language", value13);
//        headers.add("Host", value14);
//        headers.add("Accept-Encoding", value15);
//        headers.add("Accept-Language", value16);
//        return headers;
//    }
//    public static HttpHeaders proposalAuthHeaders() {
//        String value = "keep-alive";
//        String value1 = "130";
//        String value2 = ".Not/A)Brand\";v=\"99\", \"Google Chrome\";v=\"103\", \"Chromium\";v=\"103";
//        String value3 = "application/json, text/plain, */*";
//        String value4 = "application/json";
//        String value5 = "?0";
//        String value6 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36";
//        String value7 = "\"Windows\"";
//        String value8 = "https://dcmp-dev.discovermarket.com";
//        String value9 = "same-site";
//        String value10 = "cors";
//        String value11 = "empty";
//        String value12 = "https://dcmp-dev.discovermarket.com/";
//        String value13 = "en-US,en;q=0.9,vi;q=0.8";
//        String value14 = "dev.apis.discovermarket.com";
//        String value15 = "gzip, deflate, br";
//        String value16 = "en-US,en;q=0.9";
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Connection",value);
//        headers.add("Content-Length",value1);
//        headers.add("sec-ch-ua", value2);
//        headers.add("Accept", value3);
//        headers.add("Content-Type", value4);
//        headers.add("sec-ch-ua-mobile", value5);
//        headers.add("User-Agent", value6);
//        headers.add("sec-ch-ua-platform", value7);
//        headers.add("Origin", value8);
//        headers.add("Sec-Fetch-Site", value9);
//        headers.add("Sec-Fetch-Mode", value10);
//        headers.add("Sec-Fetch-Dest", value11);
//        headers.add("Referer", value12);
//        headers.add("Accept-Language", value13);
//        headers.add("Host", value14);
//        headers.add("Accept-Encoding", value15);
//        headers.add("Accept-Language", value16);
//        return headers;
//    }
//    public static HttpHeaders fullUpdateHeaders() {
//
//        String value = "keep-alive";
//        String value1 = "4330";
//        String value2 = ".Not/A)Brand\";v=\"99\", \"Google Chrome\";v=\"103\", \"Chromium\";v=\"103";
//        String value3 = "application/json, text/plain, */*";
//        String value4 = "application/json";
//        String value5 = "?0";
//        String value6 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36";
//        String value7 = "\"Windows\"";
//        String value8 = "https://dcmp-dev.discovermarket.com";
//        String value9 = "same-site";
//        String value10 = "cors";
//        String value11 = "empty";
//        String value12 = "https://dcmp-dev.discovermarket.com/";
//        String value13 = "en-US,en;q=0.9,vi;q=0.8";
//        String value14 = "dev.apis.discovermarket.com";
//        String value15 = "gzip, deflate, br";
//        String value16 = "en-US,en;q=0.9";
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Connection",value);
//        headers.add("Content-Length",value1);
//        headers.add("sec-ch-ua", value2);
//        headers.add("Accept", value3);
//        headers.add("Content-Type", value4);
//        headers.add("sec-ch-ua-mobile", value5);
//        headers.add("User-Agent", value6);
//        headers.add("sec-ch-ua-platform", value7);
//        headers.add("Origin", value8);
//        headers.add("Sec-Fetch-Site", value9);
//        headers.add("Sec-Fetch-Mode", value10);
//        headers.add("Sec-Fetch-Dest", value11);
//        headers.add("Referer", value12);
//        headers.add("Accept-Language", value13);
//        headers.add("Host", value14);
//        headers.add("Accept-Encoding", value15);
//        headers.add("Accept-Language", value16);
//        return headers;
//    }
}
