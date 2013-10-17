package org.nyer.sns.oauth;

import org.apache.commons.lang.StringUtils;

/**
 * 第三方用户服务提供者
 * @author leiting
 *
 */
public enum OAuthProvider {
	SINA_WEIBO("sinamblog", "新浪微博",
			"https://api.weibo.com/oauth2/authorize", 
			"https://api.weibo.com/oauth2/access_token"),

    NETEASE_WEIBO("neteasemblog", "网易微博", 
    		"http://api.t.163.com/oauth/request_token",
    		"http://api.t.163.com/oauth/authenticate",
    		"http://api.t.163.com/oauth/access_token"),
    		
	NETEASE2_WEIBO("neteasemblog", "网易微博",
    		"https://api.t.163.com/oauth2/authorize",
    		"https://api.t.163.com/oauth2/access_token"),

    SOHU_WEIBO("sohumblog", "搜狐微博", 
    		"http://api.t.sohu.com/oauth/request_token", 
    		"http://api.t.sohu.com/oauth/authorize?hd=default",
    		"http://api.t.sohu.com/oauth/access_token"),
    		
	SOHU2_WEIBO("sohumblog", "搜狐微博", 
    		"https://api.t.sohu.com/oauth2/authorize",
    		"https://api.t.sohu.com/oauth2/access_token"),

    QQ2_WEIBO("qqmblog", "腾讯微博", 
    		"https://open.t.qq.com/cgi-bin/oauth2/authorize", 
    		"https://open.t.qq.com/cgi-bin/oauth2/access_token"),

    RENREN("renren", "人人网", "https://graph.renren.com/oauth/authorize",
    		"https://graph.renren.com/oauth/token")
    ;
	
	/**
     * OAuth版本
     */
    private int version;

    /**
     * “提供方类型”，是由应用为每个提供方定义的一个符号名称
     */
    private String type;

    /**
     * “提供方名称”，通常是服务提供方的官方名称
     */
    private String name;
    
    private String uriRequestToken;
    private String uriAuthorize;
    private String uriAccessToken;
    
	private OAuthProvider(int version, String type, String name,
			String uriRequestToken, String uriAuthorize, String uriAccessToken) {
		this.version = version;
		this.type = type;
		this.name = name;
		this.uriRequestToken = uriRequestToken;
		this.uriAuthorize = uriAuthorize;
		this.uriAccessToken = uriAccessToken;
	}

	private OAuthProvider(String type, String name,
			String uriRequestToken, String uriAuthorize, String uriAccessToken) {
		this(OAuthConstants.V1, type, name, uriRequestToken, uriAuthorize, uriAccessToken);
	}
	
	private OAuthProvider(String type, String name,
			String uriAuthorize, String uriAccessToken) {
		this(OAuthConstants.V2, type, name, null, uriAuthorize, uriAccessToken);
	}
	
    public int getVersion() {
        return version;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getUriRequestToken() {
		return uriRequestToken;
	}

	public String getUriAuthorize() {
		return uriAuthorize;
	}

	public String getUriAccessToken() {
		return uriAccessToken;
	}

    public static OAuthProvider getByTypeAndVersion(String type, int version) {
        for (OAuthProvider serviceProvider : values()) {
            if (StringUtils.equals(serviceProvider.getType(), type) && serviceProvider.getVersion() == version) {
                return serviceProvider;
            }
        }
        return null;
    }
}
