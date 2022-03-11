package cn.mcfun.entity;

import org.apache.http.impl.client.BasicCookieStore;

public class UserInfo {
    BasicCookieStore cookie;
    String userId;
    String authKey;
    String secretKey;
    String order;
    String file;
    String continueKey;
    String continuePass;
    String key;
    String pass;
    String ip;

    public UserInfo() {
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public BasicCookieStore getCookie() {
        return cookie;
    }

    public void setCookie(BasicCookieStore cookie) {
        this.cookie = cookie;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getContinueKey() {
        return continueKey;
    }

    public void setContinueKey(String continueKey) {
        this.continueKey = continueKey;
    }

    public String getContinuePass() {
        return continuePass;
    }

    public void setContinuePass(String continuePass) {
        this.continuePass = continuePass;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
