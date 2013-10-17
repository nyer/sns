package org.nyer.sns.core;

import org.nyer.sns.oauth.OAuth;
import org.nyer.sns.oauth.OAuthProvider;
import org.nyer.sns.token.OAuthTokenPair;
import org.nyer.sns.util.page.Page;
import org.nyer.sns.util.page.PageEnumeration;

/**
 * 微博接口
 * @author leiting
 *
 */
public interface Weibo extends OAuth {
	/**
	 * 发表一个微博
	 * @param acessTokenPair
	 * @param title
	 * @param message
	 * @param url
	 * @param clientIP
	 * @return
	 */
	WeiboResponse publish(
			OAuthTokenPair acessTokenPair,
			String title, 
			String message, String url,
            String clientIP);
	
	/**
	 * 发表一个带图微博
	 * @param acessTokenPair
	 * @param title
	 * @param message
	 * @param imgBytes 图片字节数组
	 * @param imgName 图片名称，一些微博服务要求图片后缀名必须与图片类型一致
	 * @param url
	 * @param clientIP
	 * @return
	 */
	WeiboResponse publishWithImage(
			OAuthTokenPair acessTokenPair,
			String title, 
			String message, byte[] imgBytes, String imgName,
			String url,
            String clientIP);
	
	/**
	 * 从第三方服务获取用户信息
	 * @param acessTokenPair
	 * @return
	 */
	WeiboUser fetchUser(OAuthTokenPair acessTokenPair);
	
	/**
	 * 获取协议
	 * @return
	 */
	WeiboProtocal getProtocal();
	
	/**
	 * 获取指定页的被关注好友
	 * @param acessTokenPair
	 * @param page
	 * @param pageSize
	 * @return
	 */
	Page<WeiboUser> getPageFollower(
			OAuthTokenPair acessTokenPair, int page, int pageSize);
	
	/**
	 * 获取被关注好友遍历器
	 * @param acessTokenPair
	 * @param pageSize
	 * @return
	 */
	PageEnumeration<WeiboUser> getFollowerEnumerator(
			OAuthTokenPair acessTokenPair, int pageSize);
	
	/**
	 * 获取指定页的关注好友
	 * @param acessTokenPair
	 * @param page
	 * @param pageSize
	 * @return
	 */
	Page<WeiboUser> getPageFriend(
			OAuthTokenPair acessTokenPair, int page, int pageSize);
	
	/**
	 * 获取关注好友遍历器
	 * @param acessTokenPair
	 * @param pageSize
	 * @return
	 */
	PageEnumeration<WeiboUser> getFriendEnumerator(
			OAuthTokenPair acessTokenPair, int pageSize);
	
	/**
	 * 获取第三方服务商
	 * @return
	 */
	OAuthProvider getOAuthProvider();
	
	/**
	 * 获取OAuth
	 * @return
	 */
	OAuth getOAuth();
}
