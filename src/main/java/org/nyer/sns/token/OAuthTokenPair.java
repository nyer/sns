package org.nyer.sns.token;

import java.io.Serializable;

import org.nyer.sns.oauth.OAuthProvider;

public class OAuthTokenPair implements Serializable {
	private static final long serialVersionUID = 2757309304539342726L;
	
	/** 第三方用户的id */
	private String uid;
	
	/** access Token */
	private String token;
	
	/** auth1 need */
	private String tokenSecret;

	private OAuthProvider provider;
	
	public OAuthTokenPair(String token, String tokenSecret) {
		this.token = token;
		this.tokenSecret = tokenSecret;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getTokenSecret() {
		return tokenSecret;
	}

	public void setTokenSecret(String tokenSecret) {
		this.tokenSecret = tokenSecret;
	}

	
	public OAuthProvider getProvider() {
		return provider;
	}

	public void setProvider(OAuthProvider provider) {
		this.provider = provider;
	}

	@Override
	public String toString() {
		return "OAuthTokenPair [token=" + token + ", tokenSecret="
				+ tokenSecret + ", provider=" + provider + "]";
	}
}
