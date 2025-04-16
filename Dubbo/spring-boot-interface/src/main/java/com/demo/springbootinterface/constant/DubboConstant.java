package com.demo.springbootinterface.constant;

public enum DubboConstant {
    TOKEN_ID("token_id");

    public final String value;

    public String getValue() {
        return value;
    }

    DubboConstant(String value) {
        this.value = value;
    }
}