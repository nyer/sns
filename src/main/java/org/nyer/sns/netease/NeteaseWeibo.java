package org.nyer.sns.netease;

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
import org.nyer.sns.token.OAuthTokenPair;
import org.nyer.sns.util.page.Page;

/**
 * 基于OAuth2的网易微博客户端
 * @author leiting
 *
 */
public class NeteaseWeibo extends AbstractOAuth2Weibo {
	private static Logger log = Logger.getLogger(LOG_NAME);
	
	public static final String URL_PUBLISH = "https://api.t.163.com/statuses/update.json";
    public static final String URL_USER = "https://api.t.163.com/account/verify_credentials.json";
    public static final String URL_FOLLOWERS = "https://api.t.163.com/statuses/followers.json";
    public static final String URL_FRIENDS = "https://api.t.163.com/statuses/friends.json";
    public static final String URL_UPLOAD_IMG = "https://api.t.163.com/statuses/upload.json";
    
    public static final int MESSAGE_MAX_LENGTH = 163;

	public NeteaseWeibo(OAuthDeveloperAccount developerAccount,
			PrepareHttpClient httpClient) {
		super(developerAccount, httpClient, OAuthProvider.NETEASE2_WEIBO);
		this.protocal = new NeteaseWeiboProtocal(getEndPoint());
	}
	
	@Override
	public WeiboResponse publish(OAuthTokenPair accessTokenPair,
			String title, String message, String url,
			String clientIP) {
		Map<String, String> additionalParams = new HashMap<String, String>();
		String abbreviatedMsg = StringUtils.abbreviate(message, MESSAGE_MAX_LENGTH);
        additionalParams.put("status", abbreviatedMsg);
        
		return protocal.post(URL_PUBLISH, additionalParams, accessTokenPair);
	}

	/**
	 * 发表一个带图微博
	 * 
	 * @param imgName 后缀名必须与图片类型一致
	 */
	@Override
	public WeiboResponse publishWithImage(OAuthTokenPair accessTokenPair,
			String title, String message,byte[] imgBytes, String imgName,
			String url, String clientIP) {
		WeiboResponse resp = uploadImg(accessTokenPair, imgBytes, imgName);
		if (resp.isStatusOK()) {
			JSONObject obj = JSONObject.fromObject(resp.getHttpResponseText());
            String imageUrl = obj.getString("upload_image_url");
            
			message = StringUtils.abbreviate(message, MESSAGE_MAX_LENGTH - imageUrl.length());
			message += imageUrl;
			
			resp = publish(accessTokenPair, title, message, imageUrl, clientIP);
		}
		
		return resp;
	}
	
	/**
	 * 上传图片
	 * @param tokenPair
	 * @return
	 */
	private WeiboResponse uploadImg(OAuthTokenPair tokenPair, byte[] imgBytes, String imgName) {
		MultipartEntity requestEntity = new MultipartEntity();
        requestEntity.addPart("pic", new ByteArrayBody(imgBytes, imgName));
        
        return this.protocal.post(URL_UPLOAD_IMG, requestEntity, tokenPair);
	}
	
	@Override
	public WeiboUser fetchUser(OAuthTokenPair accessTokenPair) {
		WeiboResponse resp = protocal.get(URL_USER, accessTokenPair);
		if (resp.isStatusOK()) {
			try {
				WeiboUser user = new WeiboUser(accessTokenPair);
				
	            JSONObject obj = JSONObject.fromObject(resp.getHttpResponseText());
	            user.setNickName(obj.getString("name"));
	            user.setImgUrl(obj.getString("profile_image_url"));
	            String userId = obj.getString("screen_name");
	            user.setUid(userId);
	            user.setProfileUrl("http://t.163.com/" + userId);
	            
	            return user;
			} catch (Exception e) {
				resp.setLocalError(e);
			}
		}
		
		log.error("error to fetch netease weibo user, resp: " + resp);
		return null;
	}
	
	@Override
	public Page<WeiboUser> getPageFollower(OAuthTokenPair accessTokenPair, int page, int pageSize) {
		return getPagedWeiboUser(URL_FOLLOWERS, accessTokenPair, page, pageSize);
	}

	@Override
	public Page<WeiboUser> getPageFriend(OAuthTokenPair accessTokenPair, int page,
			int pageSize) {
		return getPagedWeiboUser(URL_FRIENDS, accessTokenPair, page, pageSize);
	}
	
	private Page<WeiboUser> getPagedWeiboUser(
			String url, OAuthTokenPair accessTokenPair,
			int page, int pageSize) {
		Map<String, String> additionalParams = new HashMap<String, String>();
		additionalParams.put("screen_name", accessTokenPair.getUid() + "");
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
		
		log.error("error to get netease weibo user list, resp: " + resp);
		return null;
	}
}
