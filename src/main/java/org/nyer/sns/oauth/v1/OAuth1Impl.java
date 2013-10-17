package org.nyer.sns.oauth.v1;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;
import org.nyer.sns.http.PrepareHttpClient;
import org.nyer.sns.oauth.OAuthConstants;
import org.nyer.sns.oauth.OAuthDeveloperAccount;
import org.nyer.sns.oauth.OAuthEndPoint;
import org.nyer.sns.oauth.OAuthProvider;
import org.nyer.sns.token.TokenPool;


public class OAuth1Impl implements OAuth1, OAuthConstants {
	private static Logger log = Logger.getLogger(LOG_NAME);
	
	/** 开发者账号 */
    protected OAuthDeveloperAccount developerAccount;
    
    private OAuthProvider oAuthProvider;
    
    private OAuth1Protocal authProtocal;
    
    private TokenPool requestTokenPool;
    
    private OAuth1EndPoint endPoint;
    
    private CopyOnWriteArrayList<AccessTokenListener> _listeners = new CopyOnWriteArrayList<OAuth1.AccessTokenListener>();
    
    public OAuth1Impl(OAuthDeveloperAccount developerAccount,
    		OAuthProvider oAuthProvider,
			PrepareHttpClient httpClient, TokenPool requestTokenPool) {
		this.developerAccount = developerAccount;
		this.oAuthProvider = oAuthProvider;
		
		this.endPoint = new OAuth1EndPointImpl(this, httpClient);
		this.authProtocal = new OAuth1ProtocalImpl(this, endPoint);
		
		this.requestTokenPool = requestTokenPool;
	}
    
    @Override
    public String authorizeUrl(Serializable uid, String from, String scope) {
    	OAuth1TokenPair requestTokenPair = this.authProtocal.getRequestToken();
    	requestTokenPair.setProvider(oAuthProvider);
    	
    	try {
			this.requestTokenPool.saveToken(uid, requestTokenPair);
		} catch (IOException e) {
			log.error("error to get request token", e);
			return null;
		}
    	
    	return this.authProtocal.authorizeUrl(requestTokenPair, from, scope);
    }
    
    @Override
    public void fetchAccessToken(Serializable uid, String oauthVerifier, Map<?, ?> data) throws Exception {
    	OAuth1TokenPair requestTokenPair = (OAuth1TokenPair) 
    			this.requestTokenPool.getToken(uid, oAuthProvider);
    	
    	OAuth1TokenPair accessTokenPair = this.authProtocal.getAccessToken(oauthVerifier, requestTokenPair);
    	if (accessTokenPair != null)
    		accessTokenPair.setProvider(oAuthProvider);
    	
    	for (int i = 0, s = _listeners.size();i < s;i ++) {
    		AccessTokenListener listener = _listeners.get(i);
    		if (accessTokenPair != null)
    			listener.onAccessTokenFetched(accessTokenPair, data);
    		else
    			listener.onAccessTokenFetchFailed(data);
    	}
    }
    
    @Override
    public void addAccessTokenListener(
    		AccessTokenListener listener) {
    	_listeners.add(listener);
    }
    
	@Override
	public OAuthDeveloperAccount getDeveloperAccount() {
		return this.developerAccount;
	}

	@Override
	public String getUriRequestToken() {
		return oAuthProvider.getUriRequestToken();
	}

	@Override
	public String getUriAuthorize() {
		return oAuthProvider.getUriAuthorize();
	}

	@Override
	public String getUriAccessToken() {
		return oAuthProvider.getUriAccessToken();
	}
	
	@Override
	public OAuthEndPoint getEndPoint() {
		return this.endPoint;
	}
}
