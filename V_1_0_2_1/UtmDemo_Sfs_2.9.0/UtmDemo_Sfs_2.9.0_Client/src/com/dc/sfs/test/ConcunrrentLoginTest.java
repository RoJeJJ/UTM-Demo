package com.dc.sfs.test;

import java.util.concurrent.TimeUnit;

/**
 * 
 * 并发的sfs用户登录测试（5个线程登录用户："1"，2个线程登录用户："2"）
 * 
 * @author Daemon
 *
 */
public class ConcunrrentLoginTest {
	
	public static void startLoginThread(final LoginLogoutDisconetPressTest client) {
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				for(;;) {
					
					client.connect();
					
					try {
						TimeUnit.MILLISECONDS.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
				}
				
			}
		}).start();
	}

	public static void main(String[] args) {
		
		//并发的sfs用户登录测试（5个线程登录用户："1"，2个线程登录用户："2"）
		
		LoginLogoutDisconetPressTest client = new LoginLogoutDisconetPressTest("", "1", "888888");
		startLoginThread(client);
		
		LoginLogoutDisconetPressTest client2 = new LoginLogoutDisconetPressTest("", "1", "888888");
		startLoginThread(client2);
		
		LoginLogoutDisconetPressTest client3 = new LoginLogoutDisconetPressTest("", "1", "888888");
		startLoginThread(client3);
		
		LoginLogoutDisconetPressTest client4 = new LoginLogoutDisconetPressTest("", "1", "888888");
		startLoginThread(client4);
		
		LoginLogoutDisconetPressTest client5 = new LoginLogoutDisconetPressTest("", "1", "888888");
		startLoginThread(client5);
		
		LoginLogoutDisconetPressTest client6 = new LoginLogoutDisconetPressTest("               ", "2", "888888");
		startLoginThread(client6);
		
		LoginLogoutDisconetPressTest client7 = new LoginLogoutDisconetPressTest("               ", "2", "888888");
		startLoginThread(client7);
	}
}
