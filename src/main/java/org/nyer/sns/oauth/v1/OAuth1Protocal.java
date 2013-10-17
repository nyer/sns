package org.nyer.sns.oauth.v1;


/**
 * OAuth1服务协议
 * @author leiting
 *
 */
public interface OAuth1Protocal {
	String authorizeUrl(OAuth1TokenPair requestTokenPair, String from, String scope);
	
	OAuth1TokenPair getAccessToken(String oauthVerifier, OAuth1TokenPair requestTokenPair);    
	
    OAuth1TokenPair getRequestToken();
}
