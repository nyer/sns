package org.nyer.sns.netease;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.nyer.sns.core.AbstractOAuth1Weibo;
import org.nyer.sns.core.WeiboResponse;
import org.nyer.sns.core.WeiboUser;
import org.nyer.sns.http.CachedHttpResponse;
import org.nyer.sns.http.PrepareHttpClient;
import org.nyer.sns.oauth.OAuthDeveloperAccount;
import org.nyer.sns.oauth.OAuthProvider;
import org.nyer.sns.token.OAuthTokenPair;
import org.nyer.sns.token.TokenPool;
import org.nyer.sns.util.page.Page;

public class NeteaseOAuth1Weibo extends AbstractOAuth1Weibo {
	private static Logger log = Logger.getLogger(LOG_NAME);
	
    public static final String URL_PUBLISH = "http://api.t.163.com/statuses/update.json";
    public static final String URL_USER = "http://api.t.163.com/account/verify_credentials.json";
    public static final String URL_FOLLOWERS = "http://api.t.163.com/statuses/followers.json";
    public static final String URL_FRIENDS = "http://api.t.163.com/statuses/friends.json";
    
    public static final int MESSAGE_MAX_LENGTH = 163;

    private NeteaseWeiboProtocal protocal;
	public NeteaseOAuth1Weibo(
			OAuthDeveloperAccount developerAccount,
			PrepareHttpClient httpClient,
			TokenPool requestTokenPool) {
		super(developerAccount, requestTokenPool, httpClient, OAuthProvider.NETEASE_WEIBO);
		this.protocal = new NeteaseWeiboProtocal(getEndPoint());
	}
	
	@Override
	public WeiboResponse publish(
			OAuthTokenPair accessTokenPair,
			String title, String message, String url,
			String clientIP) {
		Map<String, String> additionalParams = new HashMap<String, String>();
		String abbreviatedMsg = StringUtils.abbreviate(message, MESSAGE_MAX_LENGTH);
        additionalParams.put("status", abbreviatedMsg);
		
        return this.protocal.post(URL_PUBLISH, additionalParams, accessTokenPair);
	}

	@Override
	public WeiboUser fetchUser(
			OAuthTokenPair accessTokenPair) {
		CachedHttpResponse resp = null;
		try {
			resp = this.endPoint.get(URL_USER, accessTokenPair).send();
			if (resp.isStatusCodeOK()) {
				WeiboUser user = new WeiboUser(accessTokenPair);
				
                JSONObject obj = JSONObject.fromObject(resp.getResponseText());
                user.setNickName(obj.getString("name"));
                user.setImgUrl(obj.getString("profile_image_url"));
                String userId = obj.getString("screen_name");
                user.setUid(userId);
                user.setProfileUrl("http://t.163.com/" + userId);
                
                return user;
			}
		} catch (IOException e) {
			log.error("error to fetch user, tokenPair: " + accessTokenPair);
		}

		log.error("error to fetch netease weibo user, token: " + accessTokenPair + ", resp: " + resp);
		return null;
	}
	

	@Override
	public Page<WeiboUser> getPageFollower(
			OAuthTokenPair accessTokenPair, int page, int pageSize) {
		return getPagedWeiboUser(URL_FOLLOWERS, accessTokenPair, page, pageSize);
	}

	@Override
	public Page<WeiboUser> getPageFriend(
			OAuthTokenPair accessTokenPair, 
			int page, int pageSize) {
		return getPagedWeiboUser(URL_FRIENDS, accessTokenPair, page, pageSize);
	}
	
	private Page<WeiboUser> getPagedWeiboUser(
			String url, OAuthTokenPair accessTokenPair,
			int page,
			int pageSize) {
		Map<String, String> additionalParams = new HashMap<String, String>();
		additionalParams.put("screen_name", accessTokenPair.getUid());
		int offset = (page - 1) * pageSize;
		additionalParams.put("cursor", offset + "");
		
		WeiboResponse resp = protocal.get(url, additionalParams, accessTokenPair);
		if (resp.isStatusOK()) {
			JSONObject obj = JSONObject.fromObject(resp.getHttpResponseText());
			
			JSONArray friends = obj.getJSONArray("users");

			Page<WeiboUser> userPage = new Page<WeiboUser>(page, pageSize);
			userPage.setPrevCursor(Integer.parseInt(obj.getString("previous_cursor")));
			userPage.setNextCursor(Integer.parseInt(obj.getString("next_cursor")));
			List<WeiboUser> users = new ArrayList<WeiboUser>(friends.size());
			userPage.setContent(users);
			
			for (int i = 0;i < friends.size();i ++) {
				JSONObject follower = (JSONObject) friends.get(i);
				WeiboUser u = new WeiboUser(null);
				u.setNickName(follower.getString("name"));
				String userId = follower.getString("screen_name");
	            u.setUid(userId);
	            u.setProfileUrl("http://t.163.com/" + userId);
	            u.setImgUrl(follower.getString("profile_image_url"));
	            
	            users.add(u);
			}
			
			return userPage;
		}
		
		log.error("error to get netease weibo user list, url: " + url + ", resp: " + resp );
		return null;
	}

	@Override
	public WeiboResponse publishWithImage(
			OAuthTokenPair accessTokenPair,  String title,
			String message, byte[] imgBytes, String imgName, String url,
			String clientIP) {
		return null;
	}
}
