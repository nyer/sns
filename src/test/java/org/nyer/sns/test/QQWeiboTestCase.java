package org.nyer.sns.test;

import org.junit.BeforeClass;
import org.nyer.sns.http.DefaultPrepareHttpClient;
import org.nyer.sns.http.PrepareHttpClient;
import org.nyer.sns.oauth.OAuthDeveloperAccount;
import org.nyer.sns.qq.QQWeibo;

public class QQWeiboTestCase extends AbstractOAuth2WeiboTestCase {
	@BeforeClass
	public static void setUp() throws Exception {
		String key = "";
		String secrete = "";
		String callBack = "";
		OAuthDeveloperAccount developerAccount = 
				new OAuthDeveloperAccount(key,secrete,callBack);
		
		PrepareHttpClient httpClient = new DefaultPrepareHttpClient();
		
		weibo = new QQWeibo(developerAccount, httpClient);
		init();
	}
}
