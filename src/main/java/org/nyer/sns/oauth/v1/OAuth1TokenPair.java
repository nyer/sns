package org.nyer.sns.oauth.v1;

import org.nyer.sns.token.OAuthTokenPair;

public class OAuth1TokenPair extends OAuthTokenPair {
	private static final long serialVersionUID = -7973666851436911235L;

	public OAuth1TokenPair(String requestToken, String requestTokenSecret) {
		super(requestToken, requestTokenSecret);
	}
}
