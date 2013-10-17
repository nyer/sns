package org.nyer.sns.core;
import org.nyer.sns.oauth.OAuthProvider;
import org.nyer.sns.token.OAuthTokenPair;
import org.nyer.sns.util.page.Page;
import org.nyer.sns.util.page.PageEnumeration;
import org.nyer.sns.util.page.Pager;
import org.nyer.sns.util.page.PagerSource;

public abstract class AbstractWeibo implements Weibo {
	protected OAuthProvider oauthProvider;
	protected WeiboProtocal protocal;
	
	@Override
	public PageEnumeration<WeiboUser> getFollowerEnumerator(
			final OAuthTokenPair accessTokenPair, int pageSize) {
		PagerSource<WeiboUser> pagerSource = new PagerSource<WeiboUser>() {
			
			@Override
			public Page<WeiboUser> getPage(int page, int pageSize) {
				Page<WeiboUser> _page = getPageFollower(accessTokenPair, page, pageSize);
				if (_page != null && _page.getContent().size() >0)
					return _page;
				
				return null;
			}
		};
		
		Pager<WeiboUser> followerPager = new Pager<WeiboUser>(pagerSource);
		return followerPager.pageEnumerator(pageSize);
	}
	
	@Override
	public PageEnumeration<WeiboUser> getFriendEnumerator(
			final OAuthTokenPair accessTokenPair,
			int pageSize) {
		PagerSource<WeiboUser> pagerSource = new PagerSource<WeiboUser>() {
			
			@Override
			public Page<WeiboUser> getPage(int page, int pageSize) {
				Page<WeiboUser> _page = getPageFriend(accessTokenPair, page, pageSize);
				if (_page != null && _page.getContent().size() >0)
					return _page;
				
				return null;
			}
		};
		
		Pager<WeiboUser> friendPager = new Pager<WeiboUser>(pagerSource);
		return friendPager.pageEnumerator(pageSize);
	}
	
	@Override
	public WeiboProtocal getProtocal() {
		return protocal;
	}
	
	@Override
	public OAuthProvider getOAuthProvider() {
		return oauthProvider;
	}
}
