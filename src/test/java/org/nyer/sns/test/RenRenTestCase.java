package org.nyer.sns.test;

import java.util.Scanner;

import org.junit.BeforeClass;
import org.nyer.sns.http.DefaultPrepareHttpClient;
import org.nyer.sns.http.PrepareHttpClient;
import org.nyer.sns.oauth.OAuthDeveloperAccount;
import org.nyer.sns.pooled.OAuth2UserPoolWeibo;
import org.nyer.sns.renren.RenRen;
import org.nyer.sns.usrpool.MemoryUserPool;

public class RenRenTestCase extends AbstractOAuth2WeiboTestCase {
	protected static void init() throws Exception {
		userPoolWeibo = new OAuth2UserPoolWeibo(new MemoryUserPool(), weibo);
		
		String url = userPoolWeibo.authorizeUrl("", "read_user_status read_user_share publish_share status_update photo_upload publish_feed");
		System.out.println(url);
		System.out.println("enter the code if exists: ");
		String code = new Scanner(System.in).nextLine();
		userPoolWeibo.fetchAccessToken(userId, code, "", "");
	}
	
	@BeforeClass
	public static void setUp() throws Exception {
		String key = "";
		String secrete = "";
		String callBack = "";
		OAuthDeveloperAccount developerAccount = 
				new OAuthDeveloperAccount(key,secrete,callBack);
		
		PrepareHttpClient httpClient = new DefaultPrepareHttpClient();
		
		weibo = new RenRen(developerAccount, httpClient);
		init();
	}
}
