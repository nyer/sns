package org.nyer.sns.pooled;

import java.io.Serializable;

import org.nyer.sns.core.WeiboResponse;
import org.nyer.sns.core.WeiboUser;
import org.nyer.sns.oauth.OAuthConstants;
import org.nyer.sns.token.OAuthTokenPair;
import org.nyer.sns.usrpool.UserPool;
import org.nyer.sns.util.page.Page;
import org.nyer.sns.util.page.PageEnumeration;

/**
 * 微博接口
 * @author leiting
 *
 */
public interface UserPoolWeibo extends OAuthConstants {
	/**
	 * 发表一个微博
	 * @param uid
	 * @param title
	 * @param message
	 * @param url
	 * @param clientIP
	 * @return
	 */
	WeiboResponse publish(
			Serializable uid, String title, 
			String message, String url,
            String clientIP);
	
	/**
	 * 发表一个带图微博
	 * @param uid
	 * @param title
	 * @param message
	 * @param imgBytes 图片字节数组
	 * @param imgName 图片名称，一些微博服务要求图片名必须要求与图片类型一致
	 * @param url
	 * @param clientIP
	 * @return
	 */
	WeiboResponse publishWithImage(
			Serializable uid, String title, 
			String message, byte[] imgBytes, String imgName,
			String url,
            String clientIP);
	
	/**
	 * 获得用户池
	 * @return
	 */
	UserPool getUserPool();
	
	/**
	 * 从用户池中拿取用户
	 * @param uid
	 * @param tokenPair
	 * @return
	 */
	WeiboUser getUser(Serializable uid);
	
	/**
	 * 将第三方用户保存到用户池中
	 * @param uid 本地的用户id
	 * @param user 第三方用户
	 */
	void persisitUser(Serializable uid, WeiboUser user) throws Exception;
	
	/**
	 * 从第三方服务获取用户信息
	 * @param acessTokenPair
	 * @return
	 */
	WeiboUser fetchUser(OAuthTokenPair acessTokenPair);
	
	/**
	 * 从用户池拿取用户，并返回其中的Access Token pair
	 * @param uid
	 * @return
	 */
	OAuthTokenPair getAccessToken(Serializable uid);
	
	/**
	 * 获取指定页的被关注好友
	 * @param page
	 * @param pageSize
	 * @return
	 */
	Page<WeiboUser> getPageFollower(Serializable uid, int page, int pageSize);
	
	/**
	 * 获取被关注好友遍历器
	 * @param pageSize
	 * @return
	 */
	PageEnumeration<WeiboUser> getFollowerEnumerator(Serializable uid, int pageSize);
	
	/**
	 * 获取指定页的关注好友
	 * @param page
	 * @param pageSize
	 * @return
	 */
	Page<WeiboUser> getPageFriend(Serializable uid, int page, int pageSize);
	
	/**
	 * 获取关注好友遍历器
	 * @param pageSize
	 * @return
	 */
	PageEnumeration<WeiboUser> getFriendEnumerator(Serializable uid, int pageSize);
	
	/**
	 * 添加access token 获取监听器
	 * @param listener
	 */
	void addAccessTokenListener(AccessTokenListener listener);
	
	public interface AccessTokenListener {
		void onAccessTokenFetched(Serializable uid, OAuthTokenPair tokenPair) throws Exception;
		void onAccessTokenFetchFailed(Serializable uid);
	}
}
