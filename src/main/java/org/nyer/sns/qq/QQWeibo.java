package org.nyer.sns.qq;

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

public class QQWeibo extends AbstractOAuth2Weibo {
	private static Logger log = Logger.getLogger(LOG_NAME);
	
	public static final String PUBLISH_URL = "https://open.t.qq.com/api/t/add";
    public static final String USER_INFO_URL = "https://open.t.qq.com/api/user/info";
	public static final String URL_FRIENDS = "https://open.t.qq.com/api/friends/idollist_s";
	public static final String URL_FOLLOWERS = "https://open.t.qq.com/api/friends/fanslist_s";
	public static final String URL_UPLOADIMG = "https://open.t.qq.com/api/t/add_pic";
	
	public QQWeibo(
			OAuthDeveloperAccount developerAccount,
			PrepareHttpClient httpClient) {
		super(developerAccount, httpClient, OAuthProvider.QQ2_WEIBO);
		this.protocal = new QQWeiboProtocal(this);
	}

	@Override
	public WeiboResponse publish(
			OAuthTokenPair accessTokenPair,
			String title,
			String message, String url, String clientIP) {
		Map<String, String> additionalParams = new HashMap<String, String>();
        additionalParams.put("content", StringUtils.abbreviate(message, 140));
        additionalParams.put("format", "json");
        return this.protocal.post(PUBLISH_URL, additionalParams, accessTokenPair);
	}

	@Override
	public WeiboResponse publishWithImage(
			OAuthTokenPair accessTokenPair, String title,
			String message, byte[] imgBytes, String imgName, String url,
			String clientIP) {
        Map<String, String> additionalParams = new HashMap<String, String>();
        additionalParams.put("content", StringUtils.abbreviate(message, 140));
        additionalParams.put("format", "json");

		MultipartEntity requestEntity = new MultipartEntity();
        requestEntity.addPart("pic", new ByteArrayBody(imgBytes, imgName));
        
        return this.protocal.post(URL_UPLOADIMG, additionalParams, requestEntity, accessTokenPair);
	}
	
	@Override
	public WeiboUser fetchUser(OAuthTokenPair accessTokenPair) {
        Map<String, String> additionalParams = new HashMap<String, String>();
        additionalParams.put("format", "json");
        WeiboResponse response = null;
    	try {
	        response = this.protocal.get(USER_INFO_URL, additionalParams, accessTokenPair);
	        if (response.isStatusOK()) {
	            	JSONObject obj = JSONObject.fromObject(response.getHttpResponseText()).getJSONObject("data");
	            	WeiboUser user = new WeiboUser(accessTokenPair);
	                user.setNickName(obj.getString("nick"));
	                user.setImgUrl(obj.getString("head") + "/180"); // 修正腾讯微博头像url无法访问
	                user.setUid(obj.getString("openid"));
	
	                String name = obj.getString("name");
	                user.setProfileUrl("http://t.qq.com/" + name + "?preview");
	                
	                return user;
	        }
    	} catch (Exception ex) {
    		response.setLocalError(ex);
    	}

    	log.error("error to get user, resp: " + response);
		return null;
	}
	
	@Override
	public Page<WeiboUser> getPageFollower(
			OAuthTokenPair accessTokenPair,
			int page, int pageSize) {
		return getPagedWeiboUser(URL_FOLLOWERS, accessTokenPair, page, pageSize);
	}
	
	private Page<WeiboUser> getPagedWeiboUser(
			String url, 
			OAuthTokenPair accessTokenPair,
			int page, int pageSize) {
		Map<String, String> additionalParams = new HashMap<String, String>();
		additionalParams.put("format", "json");
		additionalParams.put("reqnum", pageSize + "");
		additionalParams.put("startindex", (page - 1) * pageSize + "");
		
		WeiboResponse response = this.protocal.get(url, additionalParams, accessTokenPair);
		if (response.isStatusOK()) {
			try {
				JSONObject obj = JSONObject.fromObject(response.getHttpResponseText());
				JSONObject data = obj.getJSONObject("data");
				// 列表为空
				if (data != null && data.isNullObject() == false) {
					JSONArray arr = data.getJSONArray("info");
					Page<WeiboUser> pageUser = new Page<WeiboUser>(page, pageSize);
		            List<WeiboUser> users = new ArrayList<WeiboUser>(arr.size());
		            pageUser.setContent(users);
		            
		            for (int i = 0; i < arr.size();i ++) {
		            	JSONObject friend = arr.getJSONObject(i);
		            	
		            	WeiboUser weiboFriend = new WeiboUser(null);
		            	weiboFriend.setNickName(friend.getString("nick"));
		            	weiboFriend.setImgUrl(friend.getString("head") + "/180"); // 修正腾讯微博头像url无法访问
		            	weiboFriend.setUid(friend.getString("openid"));

		                String name = friend.getString("name");
		                weiboFriend.setProfileUrl("http://t.qq.com/" + name + "?preview");
		                
		                users.add(weiboFriend);
		            }
		            
		            return pageUser;
				}
			} catch (Exception e) {
				response.setLocalError(e);
			}
		}
		
		if (response.isStatusOK() == false)
			log.error("error to fetch paged weibo user, resp: " + response);
		
		return null;
	}

	@Override
	public Page<WeiboUser> getPageFriend(
			OAuthTokenPair accessTokenPair,
			int page, int pageSize) {
		return getPagedWeiboUser(URL_FRIENDS, accessTokenPair, page, pageSize);
	}
}
