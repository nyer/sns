package org.nyer.sns.sina;

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
import org.nyer.sns.core.AbstractOAuth2Weibo;
import org.nyer.sns.core.WeiboResponse;
import org.nyer.sns.core.WeiboUser;
import org.nyer.sns.http.PrepareHttpClient;
import org.nyer.sns.oauth.OAuthDeveloperAccount;
import org.nyer.sns.oauth.OAuthProvider;
import org.nyer.sns.oauth.v2.OAuth2TokenPair;
import org.nyer.sns.token.OAuthTokenPair;
import org.nyer.sns.util.page.Page;

public class SinaWeibo extends AbstractOAuth2Weibo {
	private static Logger log = Logger.getLogger(LOG_NAME);
	
	public static final String PUBLISH_URL = "https://api.weibo.com/2/statuses/update.json";
	public static final String PUBLISH_WITH_IMG_URL = "https://upload.api.weibo.com/2/statuses/upload.json";
    public static final String USER_INFO_URL = "https://api.weibo.com/2/users/show.json";
    public static final String URL_FOLLOWERS = "https://api.weibo.com/2/friendships/followers.json";
    public static final String URL_FRIENDS = "https://api.weibo.com/2/friendships/friends.json";
	
	public SinaWeibo(OAuthDeveloperAccount developerAccount,
			PrepareHttpClient httpClient) {
		super(developerAccount, httpClient, OAuthProvider.SINA_WEIBO);
		this.protocal = new SinaWeiboProtocal(getEndPoint());
	}

	@Override
	public WeiboResponse publish(
			OAuthTokenPair accessTokenPair,
			String title,String message, String url, String clientIP) {
		Map<String, String> params = new HashMap<String, String>();
        params.put("status", StringUtils.abbreviate(message, 140)); // status长度不超过140
        
		return this.protocal.post(PUBLISH_URL, params, accessTokenPair);
	}

	@Override
	public WeiboResponse publishWithImage(
			OAuthTokenPair accessTokenPair,
			String title, String message, byte[] imgBytes, String imgName, String url,
			String clientIP) {
		Map<String, String> params = new HashMap<String, String>();
        params.put("status", StringUtils.abbreviate(message, 140)); // status长度不超过140
        
		MultipartEntity requestEntity = new MultipartEntity();
        requestEntity.addPart("pic", new ByteArrayBody(imgBytes, imgName));
        
		return this.protocal.post(PUBLISH_WITH_IMG_URL, params, requestEntity, accessTokenPair);
	}
	
	@Override
	public WeiboUser fetchUser(OAuthTokenPair accessTokenPair) {
		OAuth2TokenPair oAuth2TokenPair = (OAuth2TokenPair) accessTokenPair;
		String oauthUid = (String) oAuth2TokenPair.getMap().get("uid");
		if (StringUtils.isBlank(oauthUid)) {
			oauthUid = queryUid(oAuth2TokenPair);
		}
		
        Map<String, String> additionalParams = new HashMap<String, String>();
        additionalParams.put("uid", oauthUid);
        
        WeiboResponse response = this.protocal.get(USER_INFO_URL, additionalParams, accessTokenPair);
        if (response.isStatusOK()) {
            	JSONObject obj = JSONObject.fromObject(response.getHttpResponseText());
            	WeiboUser user = new WeiboUser(accessTokenPair);
            	user.setNickName(obj.getString("name"));
                user.setImgUrl(obj.getString("profile_image_url"));
                String userId = obj.getString("id");
                user.setUid(userId);
                user.setProfileUrl("http://weibo.com/" + userId);
                
                return user;
        }

		log.error("error to get user, resp: " + response);
		return null;
	}
	
	private String queryUid(OAuth2TokenPair accessTokenPair) {
        String baseUri = "https://api.weibo.com/2/account/get_uid.json";
    	WeiboResponse response = this.protocal.get(baseUri, accessTokenPair);
        try {
        	if (response.isStatusOK()) {
                String uid = JSONObject.fromObject(response.getHttpResponseText()).getString("uid");
                return uid;
        	}
        } catch (Exception e) {
        	response.setLocalError(e);
        }

    	log.error("error to get sina user uid, resp: " + response);
        return null;
    }
	

	@Override
	public Page<WeiboUser> getPageFollower(
			OAuthTokenPair accessTokenPair,
			int page, int pageSize) {
		return getPagedWeiboUser(URL_FOLLOWERS, accessTokenPair, page, pageSize);
	}

	/**
	 * 
	 * @param uid
	 * @param page 从0开始
	 * @param pageSize 1~200
	 * @return
	 */
	@Override
	public Page<WeiboUser> getPageFriend(
			OAuthTokenPair accessTokenPair,
			int page, int pageSize) {
		return getPagedWeiboUser(URL_FRIENDS, accessTokenPair, page, pageSize);
	}
	
	private Page<WeiboUser> getPagedWeiboUser(String url, 
			OAuthTokenPair accessTokenPair,
			int page, int pageSize) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", accessTokenPair.getToken());
		params.put("uid", accessTokenPair.getUid());
		params.put("count", pageSize + "");
		params.put("cursor", (page - 1) * pageSize + "");
		
		WeiboResponse response = this.protocal.get(url, params, accessTokenPair);
		if (response.isStatusOK()) {
			JSONObject obj = JSONObject.fromObject(response.getHttpResponseText());
			
			JSONArray friends = obj.getJSONArray("users");
			
			Page<WeiboUser> pageFriend = new Page<WeiboUser>(page, pageSize);
			pageFriend.setNextCursor(obj.getInt("next_cursor"));
			pageFriend.setPrevCursor(obj.getInt("previous_cursor"));
			List<WeiboUser> weiboFriends = new ArrayList<WeiboUser>(friends.size());
			pageFriend.setContent(weiboFriends);
			
			for (int i = 0;i < friends.size();i ++) {
				JSONObject friend = friends.getJSONObject(i);
				WeiboUser weiboFriend = new WeiboUser(null);
				weiboFriend.setNickName(friend.getString("name"));
				weiboFriend.setImgUrl(friend.getString("profile_image_url"));
				
                String userId = friend.getString("id");
                weiboFriend.setUid(userId);
                
                weiboFriend.setProfileUrl("http://weibo.com/" + userId);

                weiboFriends.add(weiboFriend);
			}
			
			return pageFriend;
		}
		
		log.error("error to get sina weibo user list, resp: " + response );
		return null;
	}
}
