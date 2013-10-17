package org.nyer.sns.test;

import java.io.Serializable;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.nyer.sns.core.OAuth2Weibo;
import org.nyer.sns.core.WeiboResponse;
import org.nyer.sns.core.WeiboUser;
import org.nyer.sns.pooled.OAuth2UserPoolWeibo;
import org.nyer.sns.usrpool.MemoryUserPool;
import org.nyer.sns.util.page.Page;
import org.nyer.sns.util.page.PageEnumeration;

public class AbstractOAuth2WeiboTestCase {
	protected static OAuth2Weibo weibo;
	protected static OAuth2UserPoolWeibo userPoolWeibo;
	protected static Serializable userId = "wello";
	
	protected static void init() throws Exception {
		userPoolWeibo = new OAuth2UserPoolWeibo(new MemoryUserPool(), weibo);
		
		String url = userPoolWeibo.authorizeUrl("", "basic");
		System.out.println(url);
		System.out.println("enter the code if exists: ");
		String code = new Scanner(System.in).nextLine();
		userPoolWeibo.fetchAccessToken(userId, code, "", "");
	}
	
	@Test
	public void testPublish() {
		System.out.println("test publish ");
		WeiboResponse response = null;
		try {
			response = userPoolWeibo.publish(userId, "haha", "it is a weibo", "", "");
		} catch (Exception e) {
		}
		Assert.assertNotNull(response);
		Assert.assertTrue(response.isStatusOK());
	}
	
	@Test
	public void testPublishWithImage() {
		System.out.println("test publish with a image");
		WeiboResponse response = null;
		try {
			byte[] imgBytes = IOUtils.toByteArray(
					NeteaseWeiboTestCase.class.getResourceAsStream("/20131015152402.jpg"));
			String imgName = "20131015152402.jpg";
			
			response = userPoolWeibo.publishWithImage(userId, "haha", "haha a post with a image", imgBytes, imgName, "", "");
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Assert.assertNotNull(response);
		Assert.assertTrue(response.isStatusOK());
	}
	
	@Test
	public void testGetFollowers() {
		System.out.println("test get followers");
		PageEnumeration<WeiboUser> pageEnumerator = userPoolWeibo.getFollowerEnumerator(userId, 30);
		Page<WeiboUser> pageFollowers = null;
		while ((pageFollowers = pageEnumerator.nextPage()) != null) {
			System.out.println(pageFollowers);
		}
	}
	
	@Test
	public void testGetFriends() {
		System.out.println("test get friends");
		PageEnumeration<WeiboUser> pageEnumerator = userPoolWeibo.getFriendEnumerator(userId, 30);
		Page<WeiboUser> pageFollowers = null;
		while ((pageFollowers = pageEnumerator.nextPage()) != null) {
			System.out.println(pageFollowers);
		}
	}
}
