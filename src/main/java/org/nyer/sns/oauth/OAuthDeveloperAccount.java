package org.nyer.sns.oauth;

/**
 * 开发者账号
 * 
 */
public class OAuthDeveloperAccount {

    /** consumer key */
    private String key;

    /** consumer secret */
    private String secret;

    /** 回调地址 */
    private String callbackUri;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getCallbackUri() {
        return callbackUri;
    }

    public void setCallbackUri(String callbackUri) {
        this.callbackUri = callbackUri;
    }

    public OAuthDeveloperAccount(String key, String secret, String callbackUri) {
        this.key = key;
        this.secret = secret;
        this.callbackUri = callbackUri;
    }
}
