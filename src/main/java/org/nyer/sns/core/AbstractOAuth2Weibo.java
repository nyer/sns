package org.nyer.sns.core;

import java.util.Map;

import org.nyer.sns.http.PrepareHttpClient;
import org.nyer.sns.oauth.OAuth;
import org.nyer.sns.oauth.OAuthConstants;
import org.nyer.sns.oauth.OAuthDeveloperAccount;
import org.nyer.sns.oauth.OAuthEndPoint;
import org.nyer.sns.oauth.OAuthProvider;
import org.nyer.sns.oauth.v2.OAuth2;
import org.nyer.sns.oauth.v2.OAuth2Impl;

public abstract class AbstractOAuth2Weibo extends AbstractWeibo implements OAuth2Weibo, OAuthConstants {
	protected OAuth2 oauth;
	
	public AbstractOAuth2Weibo(
			OAuthDeveloperAccount developerAccount,
			PrepareHttpClient httpClient, OAuthProvider oauthProvider) {
		this.oauth = new OAuth2Impl(developerAccount, oauthProvider, httpClient);
		this.oauthProvider = oauthProvider;
	}
	
	@Override
	public String authorizeUrl(String from, String scope) {
		return this.oauth.authorizeUrl(from, scope);
	}

	public void fetchAccessToken(
			String code, String refreshToken,
			String from, Map<?, ?> data) throws Exception {
		oauth.fetchAccessToken(code, refreshToken, from, data);
	}

	public void addAccessTokenListener(AccessTokenListener listener) {
		oauth.addAccessTokenListener(listener);
	}

	public OAuthDeveloperAccount getDeveloperAccount() {
		return oauth.getDeveloperAccount();
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
