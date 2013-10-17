package org.nyer.sns.oauth.v2;

import java.util.Map;

import org.nyer.sns.oauth.OAuth;
import org.nyer.sns.oauth.OAuthConstants;

public interface OAuth2 extends OAuth, OAuthConstants {
	String authorizeUrl(String from, String scope);
	
	void fetchAccessToken(String code, String refreshToken, 
			String from, Map<?, ?> data) throws Exception;
}
