package com.dc.sfs.test;

import java.util.concurrent.TimeUnit;

import sfs2x.client.SmartFox;
import sfs2x.client.core.BaseEvent;
import sfs2x.client.core.IEventListener;
import sfs2x.client.core.SFSEvent;
import sfs2x.client.requests.ExtensionRequest;
import sfs2x.client.requests.LoginRequest;

import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;

/**
 * 
 * 多个用户sfs测试（由于免费版sfs只能100个用户同时在线，所以这里并没有设置特别大的在线用户量）
 * （
 * 1.用户登录 退出 断线
 * 2.用户登录过程中马上断线(详见注释 "160117_177" 处)
 * 3.用户互相顶替(clientArray 和 clientArray2 是同样的用户，由两个线程持有，他们有可能会相互顶替)
 * 4.用户重复登录
 * ）
 * 
 * @author Daemon
 *
 */
public class LoginLogoutDisconetPressTest implements IEventListener {
	
	private static final String zoneName = "GameServer";
	private String sfsIP = "127.0.0.1";
	private int sfsPort = 9933;
	
	private SmartFox smartFox;
	boolean isLogining = false;
	
	private final String prefix;
	private final String userName;
	private final String passWord;
	
	
	public LoginLogoutDisconetPressTest(String prefix, String userName, String passWord) {
		
		this.prefix = prefix;
		this.userName = userName;
		this.passWord = passWord;
	}
	
	public static void startLoginThread(final LoginLogoutDisconetPressTest[] clientArray) {
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				for(;;) {
					
					double random;
					
					for( int i=0; i<clientArray.length; i++ ) {
						
						if( clientArray[i].isLogining() ) {
							
							//用户已经登录了
							
							random = Math.random();
							if( random > 0.9 ) {
								
								//有10%的概率断线或退出
								
								if( Math.random() > 0.5 ) {
									
									//有50%的概率断线
									
									clientArray[i].disconect();
									
								} else {
									
									//有50%的概率退出
									
									clientArray[i].logout();
								}
								
								try {
									TimeUnit.MILLISECONDS.sleep(10);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								
							} else if( Math.random() > 0.95 ) {
								
								//有5%的概率 重复登录
								
								clientArray[i].connect();
							}
								
							
						} else if( Math.random() > 0.997 ) {
							
							//有0.003%概率 该用户要登录 
							
							clientArray[i].connect();
							
							try {
								TimeUnit.MILLISECONDS.sleep(200);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							
						}
						
						
					}
					
					try {
						TimeUnit.MILLISECONDS.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
				}
				
			}
		}).start();
	}
	
	public static void main(String[] args) {
		
		StringBuilder prefix = new StringBuilder();
		
		LoginLogoutDisconetPressTest[] clientArray = new LoginLogoutDisconetPressTest[1000];
		for( int i=0; i<clientArray.length; i++ ) {
				
			clientArray[i] = new LoginLogoutDisconetPressTest(prefix.toString(), String.valueOf(i+1), "888888");
//			prefix.append("               ");
		}
		
		startLoginThread(clientArray);
		
		LoginLogoutDisconetPressTest[] clientArray2 = new LoginLogoutDisconetPressTest[1000];
		for( int i=0; i<clientArray2.length; i++ ) {
				
			clientArray2[i] = new LoginLogoutDisconetPressTest(prefix.toString(), String.valueOf(i+1), "888888");
//			prefix.append("               ");
		}
		
		startLoginThread(clientArray2);
		
	}
	
	public void connect() {
		
		smartFox = new SmartFox();
		smartFox.setUseBlueBox(false);
		smartFox.addEventListener(SFSEvent.CONNECTION, this);
		smartFox.addEventListener(SFSEvent.CONNECTION_LOST, this);
		smartFox.addEventListener(SFSEvent.LOGIN, this);
		smartFox.addEventListener(SFSEvent.LOGIN_ERROR, this);
		smartFox.addEventListener(SFSEvent.EXTENSION_RESPONSE, this);
		
		smartFox.connect(sfsIP, sfsPort);
		
		isLogining = true;
	}
	
	public boolean isLogining() {
		
		return isLogining;
	}
	
	public void logout() {
		
		ISFSObject sfsObject = new SFSObject();
		smartFox.send(new ExtensionRequest(Cmd.Logout.CMD, sfsObject));
		
		isLogining = false;
		
		System.out.println(prefix+userName+":logout");
	}
	
	public void disconect() {
		
		smartFox.disconnect();

		isLogining = false;
		
		System.out.println(prefix+userName+":disconect");
	}
	
	@Override
	public void dispatch(BaseEvent event) throws SFSException {

		if (event.getType().equalsIgnoreCase(SFSEvent.CONNECTION)) {
			
			if (event.getArguments().get("success").equals(true)) {
				
				ISFSObject sfsObject = new SFSObject();
				
				smartFox.send(new LoginRequest(userName, passWord, zoneName, sfsObject));
				
				//160117_177 用户在登录过程中断线测试
				if( Math.random() > 0.8 ) {
					
					try {
						TimeUnit.MILLISECONDS.sleep((int)(Math.random()*30));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					smartFox.disconnect();
				}
				
//				System.out.println(">>>link to sfs success");
				
			} else {
				
				System.out.println(prefix+userName+"***link to sfs fail");
				
			}
			
		} else if (event.getType().equalsIgnoreCase(SFSEvent.CONNECTION_LOST)) {
			
			System.out.println(prefix+userName+"***connection lose" );
			
		} else if (event.getType().equalsIgnoreCase(SFSEvent.LOGIN)) {
			
//			System.out.println(">>>login success");
			
		} else if (event.getType().equalsIgnoreCase(SFSEvent.LOGIN_ERROR)) {
			
			System.out.println(prefix+userName+"***login erro");
			
		} else if (event.getType().equals(SFSEvent.EXTENSION_RESPONSE)) {
			
			String cmd = (String)event.getArguments().get("cmd");
			ISFSObject responseData = (ISFSObject) event.getArguments().get("params");
			
			System.out.println(prefix+userName + ":" + cmd + ":" + responseData.toJson());
		}
	}
	
}
