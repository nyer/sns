package org.nyer.sns.usrpool;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.nyer.sns.core.WeiboUser;
import org.nyer.sns.oauth.OAuthProvider;

public class MemoryUserPool extends AbstractUserPool {
	private Map<Serializable, WeiboUser> memoryTokenPair = new HashMap<Serializable, WeiboUser>();
	
	@Override
	public WeiboUser getUser(Serializable uid, OAuthProvider oAuthProvider) {
		Serializable id = generateID(uid, oAuthProvider);
		return memoryTokenPair.get(id);
	}

	@Override
	public void persistUser(Serializable uid, WeiboUser user) throws IOException {
		Serializable id = generateID(uid, user.getTokenPair().getProvider());
		memoryTokenPair.put(id, user);
	}

}
