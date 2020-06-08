package com.otc.sdk.pos.flows.sources.config;

public class CustomError {

    private final int statusCode;
    private final String message;

    public CustomError(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}
