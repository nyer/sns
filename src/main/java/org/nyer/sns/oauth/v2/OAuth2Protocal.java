package org.nyer.sns.oauth.v2;


public interface OAuth2Protocal {
	String authorizeUrl(String from, String scope);
	
	OAuth2TokenPair getAccessToken(String code, String refreshToken, String from);    
	
    OAuth2TokenPair getRequestToken();
}
