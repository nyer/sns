package org.nyer.sns.token;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.nyer.sns.oauth.OAuthProvider;

public class MemoryTokenPool extends AbstractTokenPool {
	private Map<Serializable, OAuthTokenPair> memoryTokenPair = new HashMap<Serializable, OAuthTokenPair>();
	
	@Override
	public OAuthTokenPair getToken(Serializable uid, OAuthProvider oAuthProvider) {
		Serializable id = generateID(uid, oAuthProvider);
		return memoryTokenPair.get(id);
	}

	@Override
	public void saveToken(Serializable uid, OAuthTokenPair token) throws IOException {
		Serializable id = generateID(uid, token.getProvider());
		memoryTokenPair.put(id, token);
	}

}
