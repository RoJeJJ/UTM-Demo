/*
 * Copyright (C) 2013 TestGameServer Project
 *               Author: Daemon
 *               Date: 2013年9月11日
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
 * 单个sfs用户登录测试
 * （
 * 1.测试sfs用户登录和断线
 * 2.测试sfs重复登录，执行两个该程序即可
 * 3.测试心跳接口
 * ）
 * 
 * @author Daemon
 *
 */
public class SfsClientTest implements IEventListener {
	
	private SmartFox smartFox;
	
	private String sfsIP = "127.0.0.1";
	private int sfsPort = 9933;
	
	private String userName = "";
	private String passWord = "";
	private String zoneName = "";
	
	public static void main(String[] args) throws Exception {
		
		new SfsClientTest();
	}
	
	public SfsClientTest() {
		
		smartFox = new SmartFox();
		smartFox.setUseBlueBox(false);
		smartFox.addEventListener(SFSEvent.CONNECTION, this);
		smartFox.addEventListener(SFSEvent.CONNECTION_LOST, this);
		smartFox.addEventListener(SFSEvent.LOGIN, this);
		smartFox.addEventListener(SFSEvent.LOGIN_ERROR, this);
		smartFox.addEventListener(SFSEvent.EXTENSION_RESPONSE, this);
		
		login("1", "888888", "GameServer");
	}
	
	public void login( String userName, String passWord, String zoneName ) {
		
		this.userName = userName;
		this.passWord = passWord;
		this.zoneName = zoneName;
		
		smartFox.connect(sfsIP, sfsPort);
	}
	
	@Override
	public void dispatch(BaseEvent event) throws SFSException {

		if (event.getType().equalsIgnoreCase(SFSEvent.CONNECTION)) {
			
			if (event.getArguments().get("success").equals(true)) {
				
				ISFSObject sfsObject = new SFSObject();
				
				smartFox.send(new LoginRequest(userName, passWord, zoneName, sfsObject));
				
				System.out.println(">>>link to sfs success");
				
			} else {
				
				System.out.println("***link to sfs fail");
				
			}
			
		} else if (event.getType().equalsIgnoreCase(SFSEvent.CONNECTION_LOST)) {
			
			System.out.println( "connection lose" );
			
		} else if (event.getType().equalsIgnoreCase(SFSEvent.LOGIN)) {
			
			System.out.println(">>>login success");
			
		} else if (event.getType().equalsIgnoreCase(SFSEvent.LOGIN_ERROR)) {
			
			System.out.println("***login erro");
			
		} else if (event.getType().equals(SFSEvent.EXTENSION_RESPONSE)) {
			
			String cmd = (String)event.getArguments().get("cmd");
			ISFSObject responseData = (ISFSObject) event.getArguments().get("params");
			
			System.out.println(cmd + ": " + responseData.toJson());
			
			if( cmd.equals(Cmd.Login.CMD) 
					&& responseData.getInt(Cmd.Login._RESULT_CODE) == Cmd.SuccessCode.SUCCESS ) {
				
				new Thread( new Runnable() {
					
					@Override
					public void run() {
						
						for( int i=0; ; i++ ) {
							
							try {
								TimeUnit.SECONDS.sleep(10);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							
							sendHeartBeat(i);
						}
						
					}
				} ).start();;
			}
		}
	}
	
	public void sendHeartBeat(int requestCode) {
		
		ISFSObject param = new SFSObject();
		param.putInt(Cmd.Heartbeat.REQUEST_CODE, requestCode);
		
		smartFox.send(new ExtensionRequest(Cmd.Heartbeat.CMD, param));
	}
	
}
