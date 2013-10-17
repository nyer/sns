package org.nyer.sns.oauth.v2;


import java.util.Map;

import org.nyer.sns.oauth.v1.OAuth1TokenPair;

public class OAuth2TokenPair extends OAuth1TokenPair {
	private static final long serialVersionUID = 2241178082628127060L;

	private transient Map<?, ?> map;

    /** access token有效时长，单位为秒 */
    private Integer expiresIn;

    /** refresh token */
    private String refreshToken;

    public OAuth2TokenPair(Map<?, ?> map, String token, Integer expiresIn, String refreshToken) {
		super(token, null);
		
        this.map = map;
        this.expiresIn = expiresIn;
        this.refreshToken = refreshToken;
        
        setUid((String) map.get("openid"));
    }
    
    public Map<?, ?> getMap() {
        return map;
    }

    public void setMap(Map<?, ?> map) {
        this.map = map;
    }

    public Integer getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    @Override
    public String getTokenSecret() {
    	throw new UnsupportedOperationException();
    }

	@Override
	public String toString() {
		return "OAuth2TokenPair [map=" + map + ", token=" + getToken()
				+ ", expiresIn=" + expiresIn + ", refreshToken=" + refreshToken
				+ "]";
	}
}
