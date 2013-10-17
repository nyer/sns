package org.nyer.sns.oauth;

import java.util.Map;

import org.apache.http.HttpEntity;
import org.nyer.sns.http.PrepareRequest;
import org.nyer.sns.token.OAuthTokenPair;

/**
 * OAuth服务接口抽象接口， 用于底层的HTTP请求发送
 * @author leiting
 *
 */
public interface OAuthEndPoint {
	PrepareRequest post(String baseUri, Map<String, String> additionalParams, HttpEntity requestEntity, OAuthTokenPair accessToken);
	PrepareRequest post(String baseUri, Map<String, String> additionalParams, OAuthTokenPair accessToken);
	PrepareRequest post(String baseUri, HttpEntity requestEntity, OAuthTokenPair accessToken);
	PrepareRequest post(String baseUri, OAuthTokenPair accessToken);
	
	PrepareRequest get(String baseUri, Map<String, String> additionalParams,OAuthTokenPair accessToken);
	PrepareRequest get(String baseUri, Map<String, String> additionalParams) ;
	PrepareRequest get(String baseUri, OAuthTokenPair accessToken) ;
}
