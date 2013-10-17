package org.nyer.sns.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
	NeteaseWeiboTestCase.class, QQWeiboTestCase.class,
	RenRenTestCase.class, SinaWeiboTestCase.class,
	SohuWeiboTestCase.class
	})
public class SNSTestSuite {
}
