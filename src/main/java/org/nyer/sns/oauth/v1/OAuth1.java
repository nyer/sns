package org.nyer.sns.oauth.v1;

import java.io.Serializable;
import java.util.Map;

import org.nyer.sns.oauth.OAuth;
import org.nyer.sns.oauth.OAuthConstants;

public interface OAuth1 extends OAuth, OAuthConstants {
	/**
	 * 返回认证URL
	 * @param uid 本地用户ID
	 * @param from
	 * @return
	 */
	String authorizeUrl(Serializable uid, String from, String scope);
	
	/**
	 * Access token具体获取逻辑
	 * @param uid
	 * @param oauthVerifier
	 * @throws Exception
	 */
	void fetchAccessToken(Serializable uid, String oauthVerifier, Map<?, ?> data) throws Exception;
	
	/**
	 * Request token 获取URL
	 * @return
	 */
	String getUriRequestToken();
}
