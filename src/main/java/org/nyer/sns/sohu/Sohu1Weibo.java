package org.nyer.sns.sohu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.log4j.Logger;
import org.nyer.sns.core.AbstractOAuth1Weibo;
import org.nyer.sns.core.WeiboResponse;
import org.nyer.sns.core.WeiboUser;
import org.nyer.sns.http.PrepareHttpClient;
import org.nyer.sns.oauth.OAuthDeveloperAccount;
import org.nyer.sns.oauth.OAuthProvider;
import org.nyer.sns.token.OAuthTokenPair;
import org.nyer.sns.token.TokenPool;
import org.nyer.sns.util.page.Page;

public class Sohu1Weibo extends AbstractOAuth1Weibo {
	private static Logger log = Logger.getLogger(LOG_NAME);
	
    public static final String URL_PUBLISH = "http://api.t.sohu.com/statuses/update.json";
    public static final String URL_PUBLISH_WITH_IMG = "https://api.t.sohu.com/statuses/upload.json";
    public static final String URL_USER = "http://api.t.sohu.com/account/verify_credentials.json";
    public static final String URL_FRIENDS = "http://api.t.sohu.com/statuses/friends.json";
    public static final String URL_FOLLOWERS = "http://api.t.sohu.com/statuses/followers.json";
    
    public static final int MESSAGE_MAX_LENGTH = 1990;

	public Sohu1Weibo(OAuthDeveloperAccount developerAccount,
			PrepareHttpClient httpClient,
			TokenPool requestTokenPool) {
		super(developerAccount,  requestTokenPool, httpClient, OAuthProvider.SOHU_WEIBO);
		this.protocal = new SohuWeiboProtocal(getEndPoint());
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
	public WeiboResponse publishWithImage(
			OAuthTokenPair accessTokenPair, String title,
			String message, byte[] imgBytes, String imgName, String url,
			String clientIP) {
		Map<String, String> additionalParams = new HashMap<String, String>();
		String abbreviatedMsg = StringUtils.abbreviate(message, MESSAGE_MAX_LENGTH);
        additionalParams.put("status", abbreviatedMsg);
		MultipartEntity requestEntity = new MultipartEntity();
        requestEntity.addPart("pic", new ByteArrayBody(imgBytes, imgName));
        
		return this.protocal.post(URL_PUBLISH_WITH_IMG, additionalParams, requestEntity, accessTokenPair);
	}
	
	@Override
	public WeiboUser fetchUser(
			OAuthTokenPair accessTokenPair) {
		WeiboResponse resp = this.protocal.get(URL_USER, accessTokenPair);
		try {
			if (resp.isStatusOK()) {
				WeiboUser user = new WeiboUser(accessTokenPair);
				
				JSONObject obj = JSONObject.fromObject(resp.getHttpResponseText());
                String userId = obj.getString("id");
                user.setUid(userId);
                user.setProfileUrl("http://t.sohu.com/people?uid=" + userId);
                user.setNickName(obj.getString("screen_name"));
                user.setImgUrl(obj.getString("profile_image_url"));
                
                return user;
			}
		} catch (Exception e) {
			resp.setLocalError(e);
		}

		log.error("error to fetch user, resp: " + resp);
		return null;
	}
	
	@Override
	public Page<WeiboUser> getPageFollower(
			OAuthTokenPair accessTokenPair, int page,
			int pageSize) {
		return this.getPageWeiboUser(URL_FOLLOWERS, accessTokenPair, page, pageSize);
	}

	@Override
	public Page<WeiboUser> getPageFriend(
			OAuthTokenPair accessTokenPair, int page,
			int pageSize) {
		return this.getPageWeiboUser(URL_FRIENDS, accessTokenPair, page, pageSize);
	}
	
	public Page<WeiboUser> getPageWeiboUser(
			String url, 
			OAuthTokenPair accessTokenPair, 
			int page, int pageSize) {
		Map<String, String> additionalParams = new HashMap<String, String>();
		additionalParams.put("id", accessTokenPair.getUid());
		additionalParams.put("page", page + "");
		additionalParams.put("count", pageSize + "");
		
		WeiboResponse response = this.protocal.get(url, additionalParams, accessTokenPair);
		if (response.isStatusOK()) {
			try {
				JSONArray obj = JSONArray.fromObject(response.getHttpResponseText());
				
				Page<WeiboUser> users = new Page<WeiboUser>(page, pageSize);
				List<WeiboUser> content = new ArrayList<WeiboUser>(obj.size());
				users.setContent(content);
				for (int i = 0;i < obj.size();i ++) {
					JSONObject u = obj.getJSONObject(i);
					
					WeiboUser friend = new WeiboUser(null);
					String userId = u.getString("id");
					friend.setUid(userId);
					friend.setProfileUrl("http://t.sohu.com/people?uid=" + userId);
					friend.setNickName(u.getString("screen_name"));
					friend.setImgUrl(u.getString("profile_image_url"));
	                
	                content.add(friend);
				}
				
				return users;
			} catch (Exception e) {
				response.setLocalError(e);
			}
		}
		
		log.error("error to get weibo user list, resp: " + response);
		
		return null;
	}
}
