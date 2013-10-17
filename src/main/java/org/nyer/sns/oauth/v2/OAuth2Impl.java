package org.nyer.sns.oauth.v2;


import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.nyer.sns.http.PrepareHttpClient;
import org.nyer.sns.oauth.OAuthConstants;
import org.nyer.sns.oauth.OAuthDeveloperAccount;
import org.nyer.sns.oauth.OAuthEndPoint;
import org.nyer.sns.oauth.OAuthProvider;

public class OAuth2Impl implements OAuth2, OAuthConstants {
	/** 开发者账号 */
    protected OAuthDeveloperAccount developerAccount;

    private OAuthProvider provider;
    
    private OAuth2Protocal authProtocal;
    
    private OAuth2EndPoint endPoint;
    
    private CopyOnWriteArrayList<OAuth2.AccessTokenListener> _listeners = new CopyOnWriteArrayList<OAuth2.AccessTokenListener>();
    
    public OAuth2Impl(OAuthDeveloperAccount developerAccount,
			OAuthProvider oAuthProvider,
			PrepareHttpClient httpClient) {
		this.developerAccount = developerAccount;
		this.provider = oAuthProvider;
		
		this.endPoint = new OAuth2EndPointImpl(httpClient);
		this.authProtocal = new OAuth2ProtocalImpl(this, endPoint);
	}
    
	@Override
	public String authorizeUrl(String from, String scope) {
		return this.authProtocal.authorizeUrl(from, scope);
	}

	@Override
	public void fetchAccessToken(
			String code, String refreshToken,
			String from, Map<?, ?> data) throws Exception {
		OAuth2TokenPair tokenPair = this.authProtocal.getAccessToken(code, refreshToken, from);
		if (tokenPair != null)
			tokenPair.setProvider(provider);
		
		for (int i = 0, s = _listeners.size();i < s;i ++) {
    		AccessTokenListener listener = _listeners.get(i);
    		if (tokenPair != null)
    			listener.onAccessTokenFetched(tokenPair, data);
    		else
    			listener.onAccessTokenFetchFailed(data);
    	}
	}


	@Override
	public void addAccessTokenListener(AccessTokenListener listener) {
		this._listeners.add(listener);
	}
	
	@Override
	public OAuthDeveloperAccount getDeveloperAccount() {
		return this.developerAccount;
	}

	@Override
	public String getUriAuthorize() {
		return this.provider.getUriAuthorize();
	}

	@Override
	public String getUriAccessToken() {
		return this.provider.getUriAccessToken();
	}

	@Override
	public OAuthEndPoint getEndPoint() {
		return this.endPoint;
	}
}
