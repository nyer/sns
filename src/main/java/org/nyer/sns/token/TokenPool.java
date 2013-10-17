package org.nyer.sns.token;

import java.io.IOException;
import java.io.Serializable;

import org.nyer.sns.oauth.OAuthProvider;

public interface TokenPool {
	OAuthTokenPair getToken(Serializable uid, OAuthProvider oAuthProvider);
	
	void saveToken(Serializable uid, OAuthTokenPair token) throws IOException;
}
