package com.mynet.proxyserver.model;


import com.mynet.shared.types.ServerType;

public class LoginRequest {
    private String id;
    private String appVersion;
    private String platform;
    private String loginPurpose;
    private String token;
    private ServerType serverType;
    private int friendsCount;
    private String analyticsDevId; //analytics
    private String analyticsSessionId; //analytics
    private String secretKey;

    public String getSecretKey() {
        return secretKey;
    }

    public int getFriendsCount() {
        return friendsCount;
    }

    public void setFriendsCount(int friendsCount) {
        this.friendsCount = friendsCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getLoginPurpose() {
        return loginPurpose;
    }

    public void setLoginPurpose(String loginPurpose) {
        this.loginPurpose = loginPurpose;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ServerType getServerType() {
        return serverType;
    }

    public String getAnalyticsDevId() {
        return analyticsDevId;
    }

    public String getAnalyticsSessionId() {
        return analyticsSessionId;
    }

    @Override
    public String toString() {
        return "LoginRequest{" +
                "id='" + id + '\'' +
                ", appVersion=" + appVersion +
                ", platform='" + platform + '\'' +
                ", loginPurpose='" + loginPurpose + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
