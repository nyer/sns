package org.nyer.sns.usrpool;

import java.io.IOException;
import java.io.Serializable;

import org.nyer.sns.core.WeiboUser;
import org.nyer.sns.oauth.OAuthProvider;

/**
 * 用户池，用于储存第三方用户
 * @author leiting
 *
 */
public interface UserPool {
	/**
	 * 根据本地用户ID获取第三方用户
	 * @param uid 本地用户ID
	 * @param oAuthProvider 第三方用户服务提供者
	 * @return
	 */
	WeiboUser getUser(Serializable uid, OAuthProvider oAuthProvider);
	
	/**
	 * 将第三方用户信息储存在本地
	 * @param uid 要在本地对应的用户ID
	 * @param user 第三方用户 
	 * @throws IOException
	 */
	void persistUser(Serializable uid, WeiboUser user) throws IOException;
}
