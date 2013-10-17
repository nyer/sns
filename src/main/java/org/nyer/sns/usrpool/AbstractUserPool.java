package org.nyer.sns.usrpool;

import java.io.Serializable;

import org.nyer.sns.core.WeiboUser;
import org.nyer.sns.oauth.OAuthProvider;

public abstract class AbstractUserPool implements UserPool {
	protected Serializable generateID(Serializable uid, OAuthProvider oAuthProvider) {
		StringBuilder idBuf = new StringBuilder();
		idBuf.append(uid)
		.append("_").append(oAuthProvider.getName())
		.append("_").append(oAuthProvider.getType())
		.append("_").append(oAuthProvider.getVersion());
		
		return idBuf.toString();
	}
	
	protected Serializable generateID(Serializable uid, WeiboUser user) {
		OAuthProvider oAuthProvider = user.getTokenPair().getProvider();
		StringBuilder idBuf = new StringBuilder();
		idBuf.append(uid)
		.append("_").append(oAuthProvider.getName())
		.append("_").append(oAuthProvider.getType())
		.append("_").append(oAuthProvider.getVersion());
		
		return idBuf.toString();
	}
}
