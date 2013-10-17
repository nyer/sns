package org.nyer.sns.core;

import org.nyer.sns.token.OAuthTokenPair;

public class WeiboUser {
	private OAuthTokenPair tokenPair;
	private String nickName;
	private String profileUrl;
	private String imgUrl;
	private String uid;
	
	public WeiboUser(OAuthTokenPair tokenPair) {
		this.tokenPair = tokenPair;
	}
	
	public OAuthTokenPair getTokenPair() {
		return tokenPair;
	}
	public void setTokenPair(OAuthTokenPair tokenPair) {
		this.tokenPair = tokenPair;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getProfileUrl() {
		return profileUrl;
	}
	public void setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
		if (tokenPair != null)
			this.tokenPair.setUid(uid);
	}
	@Override
	public String toString() {
		return "UserProfile [tokenPair=" + tokenPair + ", nickName=" + nickName
				+ ", profieUrl=" + profileUrl + ", imgUrl=" + imgUrl + ", uid="
				+ uid + "]";
	}
}
