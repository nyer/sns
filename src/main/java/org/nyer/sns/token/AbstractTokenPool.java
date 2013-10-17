package org.nyer.sns.token;

import java.io.Serializable;

import org.nyer.sns.oauth.OAuthProvider;

public abstract class AbstractTokenPool implements TokenPool {
	protected Serializable generateID(Serializable uid, OAuthProvider oAuthProvider) {
		StringBuilder idBuf = new StringBuilder();
		idBuf.append(uid)
		.append("_").append(oAuthProvider.getName())
		.append("_").append(oAuthProvider.getType())
		.append("_").append(oAuthProvider.getVersion());
		
		return idBuf.toString();
	}
}
