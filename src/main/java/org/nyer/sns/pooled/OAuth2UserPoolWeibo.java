package org.nyer.sns.pooled;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.nyer.sns.core.OAuth2Weibo;
import org.nyer.sns.oauth.v2.OAuth2;
import org.nyer.sns.usrpool.UserPool;

public class OAuth2UserPoolWeibo extends AbstractUserPoolWeibo {
	private OAuth2 oAuth2Weibo;
	public OAuth2UserPoolWeibo(UserPool userPool, OAuth2Weibo weibo) {
		super(weibo, userPool);
		this.oAuth2Weibo = weibo;
	}

	public void fetchAccessToken(
			Serializable uid,
			String code, String refreshToken, 
			String from) throws Exception {
		Map<Object, Object> data = new HashMap<Object, Object>();
		data.put(UID_KEY_IN_DATA, uid);
		
		oAuth2Weibo.fetchAccessToken(code, refreshToken, from, data);
	}

	public String authorizeUrl(String from, String scope) {
		return oAuth2Weibo.authorizeUrl(from, scope);
	}
}
