package com.example.moftech.smartmenu.Model;

public class Token {
    private String tokens;
    private boolean isServerToken;

    public Token() {
    }

    public Token(String tokens, boolean isServerToken) {
        this.tokens = tokens;
        this.isServerToken = isServerToken;
    }

    public String getTokens() {
        return tokens;
    }

    public void setTokens(String tokens) {
        this.tokens = tokens;
    }

    public boolean isServerToken() {
        return isServerToken;
    }

    public void setServerToken(boolean serverToken) {
        isServerToken = serverToken;
    }
}
