package org.nyer.sns.oauth;

import java.util.Map;

import org.nyer.sns.token.OAuthTokenPair;

/**
 * OAuth服务接口
 * @author leiting
 *
 */
public interface OAuth {
	/**
	 * 获取认证开发者账号信息
	 * @return
	 */
	OAuthDeveloperAccount getDeveloperAccount();

	/**
	 * 获取认证URL
	 * @return
	 */
	String getUriAuthorize();

	/**
	 * Acess token获取地址
	 * @return
	 */
	String getUriAccessToken();

	/**
	 * 获取OAuth请求发送组件
	 * @return
	 */
	OAuthEndPoint getEndPoint();
	
	/**
	 * 添加access token 获取监听器
	 * @param listener
	 */
	void addAccessTokenListener(AccessTokenListener listener);
	
	public interface AccessTokenListener {
		void onAccessTokenFetched(OAuthTokenPair tokenPair, Map<?, ?> data) throws Exception;
		void onAccessTokenFetchFailed(Map<?, ?> data);
	}
}
