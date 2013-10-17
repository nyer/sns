package org.nyer.sns.renren;

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

public class RenRen extends AbstractOAuth2Weibo {
	private static Logger log = Logger.getLogger(LOG_NAME);
	
	public static final String URL_SERVER = "https://api.renren.com/restserver.do";
	public RenRen(OAuthDeveloperAccount developerAccount,
			PrepareHttpClient httpClient) {
		super(developerAccount, httpClient, OAuthProvider.RENREN);
		this.protocal = new RenRenProtocal(oauth);
	}

	@Override
	public WeiboResponse publish(OAuthTokenPair accessTokenPair, String title,
			String message, String url, String clientIP) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("method", "status.set");
        params.put("status", StringUtils.abbreviate(message, 200)); // description长度不超过200
        
		return this.protocal.post(URL_SERVER, params, accessTokenPair);
	}

	@Override
	public WeiboResponse publishWithImage(OAuthTokenPair accessTokenPair,
			String title, String message, byte[] imgBytes, String imgName, String url,
			String clientIP) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("method", "photos.upload");
        params.put("caption", StringUtils.abbreviate(message, 200)); // description长度不超过200
        
        MultipartEntity requestEntity = new MultipartEntity();
        requestEntity.addPart("upload", new ByteArrayBody(imgBytes, imgName));
        
        return this.protocal.post(URL_SERVER, params, requestEntity, accessTokenPair);
	}

	@Override
	public WeiboUser fetchUser(OAuthTokenPair tokenPair) {
		Map<String, String> params = new HashMap<String, String>();
        params.put("method", "users.getLoggedInUser");
        
        WeiboResponse response = this.protocal.post(URL_SERVER, params, tokenPair);
        if (response.isStatusOK()) {
        	String userId = null;
            try {
                JSONObject obj = JSONObject.fromObject(response.getHttpResponseText());
                userId = obj.getString("uid");
                params = new HashMap<String, String>();
                params.put("method", "users.getProfileInfo");
                params.put("uid", userId);
                
                response = this.protocal.post(URL_SERVER, params, tokenPair);
                if (response.isStatusOK()) {
                    WeiboUser weiboUser = new WeiboUser(tokenPair);
                    try {
                        obj = JSONObject.fromObject(response.getHttpResponseText());

                        weiboUser.setUid(userId);
                        weiboUser.setProfileUrl("http://www.renren.com/" + userId);
                        weiboUser.setNickName(obj.getString("name"));
                        weiboUser.setImgUrl(obj.getString("headurl"));
                        
                        return weiboUser;
                    } catch (Exception e) {
                    	response.setLocalError(e);
                    }
                }
            } catch (Exception e) {
            	response.setLocalError(e);
            }
        }
        if (response.isStatusOK() == false)
        	log.error("error to get user, token: " + tokenPair + ", resp: " + response);
        
		return null;
	}

	/**
	 * 人人没有Follower的关系
	 */
	@Override
	public Page<WeiboUser> getPageFollower(
			OAuthTokenPair accessTokenPair,
			int page, int pageSize) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Page<WeiboUser> getPageFriend(
			OAuthTokenPair accessTokenPair,
			int page, int pageSize) {
		Map<String, String> params = new HashMap<String, String>();

        params.put("method", "friends.getFriends");
        params.put("page", page + "");
        params.put("count", pageSize + "");
        
        WeiboResponse response = this.protocal.post(URL_SERVER, params, accessTokenPair);
        if (response.isStatusOK()) {
        	try {
            	JSONArray friends = JSONArray.fromObject(response.getHttpResponseText());
            	
            	Page<WeiboUser> pageFriend = new Page<WeiboUser>(page, pageSize);
    			List<WeiboUser> weiboFriends = new ArrayList<WeiboUser>(friends.size());
    			pageFriend.setContent(weiboFriends);
    			
    			for (int i = 0;i < friends.size();i ++) {
    				JSONObject friend = friends.getJSONObject(i);
    				WeiboUser weiboFriend = new WeiboUser(null);
    				weiboFriend.setNickName(friend.getString("name"));
    				weiboFriend.setImgUrl(friend.getString("headurl"));
    				
                    String userId = friend.getString("id");
                    weiboFriend.setUid(userId);
                    
                    weiboFriend.setProfileUrl("http://www.renren.com/" + userId);
                    
                    weiboFriends.add(weiboFriend);
    			}
    			
    			return pageFriend;
        	} catch (Exception e) {
        		response.setLocalError(e);
        	}
        }
        
        log.error("error to get followers, resp: " + response);
        
		return null;
	}
}
