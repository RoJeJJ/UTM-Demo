package com.dc.netty.utm.test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.dc.netty.coder.commonobj.CommonObjB;

/**
 * 
 * 多个用户测试
 * （
 * 1.用户登录 退出 断线
 * 2.用户登录过程中马上断线(详见注释 "160117_177" 处)
 * 3.用户互相顶替(clientArray 和 clientArray2 是存在同样的用户，由两个线程持有，他们有可能会相互顶替)
 * 4.用户重复登录
 * ）
 * 
 * @author Daemon
 *
 */
public class LoginLogoutDisconetPressTest {
	
	public static void startLoginThread(final ClientTest[] clientArray) {
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				CommonObjB obj = new CommonObjB();
				
				for(;;) {
					
					double random;
					
					for( int i=0; i<clientArray.length; i++ ) {
						
						if( clientArray[i].client.isLogin() ) {
							
							//用户已经登录了
							
							random = Math.random();
							if( random > 0.9 ) {
								
								//有10%的概率断线或退出
								
								if( Math.random() > 0.5 ) {
									
									//有50%的概率断线
									
									try {
										clientArray[i].client.disconnect();
									} catch (IOException e) {
										e.printStackTrace();
									}
									
								} else {
									
									//有50%的概率退出
									
									clientArray[i].client.logout(obj);
								}
								
								try {
									TimeUnit.MILLISECONDS.sleep(10);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								
							} else if( Math.random() > 0.95 ) {
								
								//有5%的概率 重复登录
								
								clientArray[i].client.connect("127.0.0.1", 7878);
							}
								
							
						} else if( Math.random() > 0.96 ) {
							
							//有0.003%概率 该用户要登录 
							
							clientArray[i].client.connect("127.0.0.1", 7878);
							
							try {
								TimeUnit.MILLISECONDS.sleep(10);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							
						}
						
						
					}
					
					try {
						TimeUnit.MILLISECONDS.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
				}
				
			}
		}).start();
	}
	
	public static void main(String[] args) {
		
		StringBuilder prefix = new StringBuilder();
		
		ClientTest[] clientArray = new ClientTest[10000];
		for( int i=0; i<clientArray.length; i++ ) {
				
			clientArray[i] = new ClientTest(prefix.toString(), String.valueOf(i+1), "888888", false, false);
//			prefix.append("               ");
		}
		
		startLoginThread(clientArray);
		
		ClientTest[] clientArray2 = new ClientTest[10000];
		for( int i=0; i<clientArray2.length; i++ ) {
				
			clientArray2[i] = new ClientTest(prefix.toString(), String.valueOf(i+5001), "888888", false, false);
//			prefix.append("               ");
		}
		
		startLoginThread(clientArray2);
		
	}
	
}




