package org.nyer.sns.pooled;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.nyer.sns.core.OAuth1Weibo;
import org.nyer.sns.usrpool.UserPool;

public class OAuth1UserPoolWeibo extends AbstractUserPoolWeibo {
	private OAuth1Weibo oAuth1Weibo;
	
	public OAuth1UserPoolWeibo(UserPool userPool, OAuth1Weibo weibo) {
		super(weibo, userPool);
		this.oAuth1Weibo = weibo;
	}

	public void fetchAccessToken(
			Serializable uid,
			String oauthVerifier, 
			String from) throws Exception {
		Map<Object, Object> data = new HashMap<Object, Object>();
		data.put(UID_KEY_IN_DATA, uid);
		
		oAuth1Weibo.fetchAccessToken(uid, oauthVerifier, data);
	}

	public String authorizeUrl(Serializable uid, String from, String scope) {
		return this.oAuth1Weibo.authorizeUrl(uid, from, scope);
	}
}
