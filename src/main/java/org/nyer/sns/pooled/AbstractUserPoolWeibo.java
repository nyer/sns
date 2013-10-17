package org.nyer.sns.pooled;

import java.io.Serializable;
import java.util.Map;

import org.apache.log4j.Logger;
import org.nyer.sns.core.Weibo;
import org.nyer.sns.core.WeiboResponse;
import org.nyer.sns.core.WeiboUser;
import org.nyer.sns.oauth.OAuth;
import org.nyer.sns.token.OAuthTokenPair;
import org.nyer.sns.usrpool.UserPool;
import org.nyer.sns.util.page.Page;
import org.nyer.sns.util.page.PageEnumeration;

public abstract class AbstractUserPoolWeibo implements UserPoolWeibo  {
	private static Logger log = Logger.getLogger(LOG_NAME);
	
	public static final String UID_KEY_IN_DATA = "local_uid";
	
	protected Weibo weibo;
	protected UserPool userPool;
	
	public AbstractUserPoolWeibo(Weibo weibo, UserPool userPool) {
		this.weibo = weibo;
		this.userPool = userPool;
		
		addAccessTokenListener(new AccessTokenListener() {
			
			@Override
			public void onAccessTokenFetched(Serializable uid, OAuthTokenPair tokenPair)
					throws Exception {
				WeiboUser user = AbstractUserPoolWeibo.this.weibo.fetchUser(tokenPair);
				if (user != null) {
					AbstractUserPoolWeibo.this.userPool.persistUser(uid, user);
				} else {
					log.error("error to get user, uid: " + uid + ", tokenPair: " + tokenPair);
				}
			}
			
			@Override
			public void onAccessTokenFetchFailed(Serializable uid) {
				log.error("error to fetch access token , uid: " + uid);
			}
		});
	}
	
	@Override
	public WeiboResponse publish(Serializable uid, String title,
			String message, String url, String clientIP) {
		OAuthTokenPair accessTokenPair = this.getAccessToken(uid);
		
		return this.weibo.publish(accessTokenPair, title, message, url, clientIP);
	}

	@Override
	public WeiboResponse publishWithImage(Serializable uid, String title,
			String message, byte[] imgBytes, String imgName, String url,
			String clientIP) {
		OAuthTokenPair accessTokenPair = this.getAccessToken(uid);
		
		return this.weibo.publishWithImage(accessTokenPair, title, message, imgBytes, imgName, url, clientIP);
	}

	@Override
	public UserPool getUserPool() {
		return this.userPool;
	}

	@Override
	public WeiboUser getUser(Serializable uid) {
		return getUserPool().getUser(uid, weibo.getOAuthProvider());
	}

	@Override
	public void persisitUser(Serializable uid, WeiboUser user) throws Exception {
		getUserPool().persistUser(uid, user);
	}
	
	@Override
	public WeiboUser fetchUser(OAuthTokenPair acessTokenPair) {
		return this.weibo.fetchUser(acessTokenPair);
	}
	
	@Override
	public OAuthTokenPair getAccessToken(Serializable uid) {
		return getUser(uid).getTokenPair();
	}

	@Override
	public Page<WeiboUser> getPageFollower(Serializable uid, int page,
			int pageSize) {
		OAuthTokenPair accessTokenPair = this.getAccessToken(uid);
		return this.weibo.getPageFollower(accessTokenPair, page, pageSize);
	}

	@Override
	public PageEnumeration<WeiboUser> getFollowerEnumerator(Serializable uid,
			int pageSize) {
		OAuthTokenPair accessTokenPair = this.getAccessToken(uid);
		return this.weibo.getFollowerEnumerator(accessTokenPair, pageSize);
	}

	@Override
	public Page<WeiboUser> getPageFriend(Serializable uid, int page,
			int pageSize) {
		OAuthTokenPair accessTokenPair = this.getAccessToken(uid);
		return this.weibo.getPageFriend(accessTokenPair, page, pageSize);
	}

	@Override
	public PageEnumeration<WeiboUser> getFriendEnumerator(Serializable uid,
			int pageSize) {
		OAuthTokenPair accessTokenPair = this.getAccessToken(uid);
		return this.weibo.getFriendEnumerator(accessTokenPair, pageSize);
	}

	@Override
	public void addAccessTokenListener(final AccessTokenListener listener) {
		this.weibo.addAccessTokenListener(new OAuth.AccessTokenListener() {
			
			@Override
			public void onAccessTokenFetched(OAuthTokenPair tokenPair, Map<?,?> data) throws Exception {
				listener.onAccessTokenFetched((Serializable) data.get(UID_KEY_IN_DATA), tokenPair);
			}
			
			@Override
			public void onAccessTokenFetchFailed(Map<?,?> data) {
				listener.onAccessTokenFetchFailed((Serializable) data.get(UID_KEY_IN_DATA));
			}
		});
	}

}
