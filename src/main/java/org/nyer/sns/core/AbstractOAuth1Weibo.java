package org.nyer.sns.core;

import java.io.Serializable;
import java.util.Map;

import org.nyer.sns.http.PrepareHttpClient;
import org.nyer.sns.oauth.OAuth;
import org.nyer.sns.oauth.OAuthDeveloperAccount;
import org.nyer.sns.oauth.OAuthEndPoint;
import org.nyer.sns.oauth.OAuthProvider;
import org.nyer.sns.oauth.v1.OAuth1;
import org.nyer.sns.oauth.v1.OAuth1EndPoint;
import org.nyer.sns.oauth.v1.OAuth1EndPointImpl;
import org.nyer.sns.oauth.v1.OAuth1Impl;
import org.nyer.sns.token.TokenPool;

public abstract class AbstractOAuth1Weibo extends AbstractWeibo implements OAuth1Weibo {
	protected OAuth1EndPoint endPoint;
	protected OAuth1 oauth;
	
	public AbstractOAuth1Weibo(
			OAuthDeveloperAccount developerAccount,
			TokenPool requestTokenPool, 
			PrepareHttpClient httpClient, OAuthProvider oauthProvider) {
		this.oauth = new OAuth1Impl(developerAccount, oauthProvider, httpClient, requestTokenPool);
		this.endPoint = new OAuth1EndPointImpl(oauth, httpClient);
		this.oauthProvider = oauthProvider;
	}
	
	
	@Override
	public String authorizeUrl(Serializable uid, String from, String scope) {
		return this.oauth.authorizeUrl(uid, from, scope);
	}

	public void fetchAccessToken(Serializable uid, String oauthVerifier, Map<?, ?> data)
			throws Exception {
		oauth.fetchAccessToken(uid, oauthVerifier, data);
	}

	public void addAccessTokenListener(AccessTokenListener listener) {
		oauth.addAccessTokenListener(listener);
	}

	public OAuthDeveloperAccount getDeveloperAccount() {
		return oauth.getDeveloperAccount();
	}

	public String getUriRequestToken() {
		return oauth.getUriRequestToken();
	}

	public String getUriAuthorize() {
		return oauth.getUriAuthorize();
	}

	public String getUriAccessToken() {
		return oauth.getUriAccessToken();
	}

	public OAuthEndPoint getEndPoint() {
		return oauth.getEndPoint();
	}
	
	@Override
	public OAuth getOAuth() {
		return this.oauth;
	}
}
