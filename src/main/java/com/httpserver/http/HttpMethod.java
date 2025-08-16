package com.httpserver.http;

public enum HttpMethod {
    GET, HEAD;

    // Maximum length of HTTP method names
    // Using maximum length to validate incoming requests
    public static final int MAX_LENGTH;

    // in future, if I add other HTTP methods like POST, DELETE, max_length will handle it automatically and no malicious code will be allocated resources or cause buffer overflows
    static {
        int tempMaxLength = -1;
        for (HttpMethod method : values()) {
            if (method.name().length() > tempMaxLength) {
                tempMaxLength = method.name().length();
            } 
        }
        MAX_LENGTH = tempMaxLength;
    }
}
