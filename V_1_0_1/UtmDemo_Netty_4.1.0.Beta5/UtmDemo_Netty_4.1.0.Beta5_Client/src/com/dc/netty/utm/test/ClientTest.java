package com.dc.netty.utm.test;

import java.util.concurrent.TimeUnit;

import com.dc.netty.client.socket.IExceptionListener;
import com.dc.netty.client.socket.commonobj.CommonCoderServerEventHandler;
import com.dc.netty.client.socket.commonobj.CommonCoderSocketClient;
import com.dc.netty.coder.cmd.NormalIntegerCmdCoder;
import com.dc.netty.coder.commonobj.CommonObjB;

/**
 * 
 * 单个用户登录测试
 * （
 * 1.测试用户登录和断线
 * 2.测试重复登录，执行两个该程序即可
 * 3.测试心跳接口
 * ）
 * 
 * @author Daemon
 *
 */
public class ClientTest extends CommonCoderServerEventHandler<Integer> {
	
	private static NormalIntegerCmdCoder cmdCoder = new NormalIntegerCmdCoder();
	
	public final CommonCoderSocketClient<Integer> client;
	
	private final String prefix;
	private final String userName;
	private final String password;
	private final boolean startHeartbeat;
	
	public ClientTest(String prefix, String userName, String password, boolean startHeartbeat, final boolean showException) {
		
		this.prefix = prefix;
		this.userName = userName;
		this.password = password;
		this.startHeartbeat = startHeartbeat;
		
		//初始化 Netty的客户端组件，并注册事件处理器（this）
		client = new CommonCoderSocketClient<Integer>(
				cmdCoder, cmdCoder, Cmd.Login.CMD, Cmd.Logout.CMD, this, new IExceptionListener() {
					
					@Override
					public void exception(Exception e) {
						
						if(showException)
							e.printStackTrace();
					}
				});
	}
	
	@Override
	public void connectSuccess() {
		
		System.out.println(prefix + "Connect Success");
		
		this.login();
	}

	@Override
	public void connectFail(Exception e) {
		System.out.println(prefix + "Connect Fail");
	}
	
	/**
	 * 登录到服务端
	 */
	public void login() {
		
		CommonObjB loginObj = new CommonObjB();
		loginObj.putUtfString(Cmd.Login.USER_NAME, userName);
		loginObj.putUtfString(Cmd.Login.PASSWORD, password);
		
		client.login(loginObj);
	}

	@Override
	public boolean loginBack(CommonObjB obj) {
		
		int resultCode = obj.getInt(Cmd.Login._RESULT_CODE);
		if( resultCode == Cmd.SuccessCode.SUCCESS ) {
			
			System.out.println(prefix + "Login Success: " + obj.toString());
			
			if( startHeartbeat )
				startHeartbeat();
			
			return true;
			
		} else {
			
			System.out.println(prefix + "Login Fail: " + obj.toString());
			return false;
		}
		
	}
	
	/**
	 * 启动心跳线程
	 */
	private void startHeartbeat() {
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				for(int i=0; ; i++) {
					
					try {
						TimeUnit.SECONDS.sleep(10);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					sendHeartBeat(i);
				}
			}
			
		}).start();
		
	}
	
	@Override
	public boolean logoutBack(CommonObjB obj) {

		int resultCode = obj.getInt(Cmd.Logout._RESULT_CODE);
		if( resultCode == Cmd.SuccessCode.SUCCESS ) {
			
			System.out.println(prefix + "Logout Success: " + obj.toString());
			
		} else if( resultCode == Cmd.ErroCode.USER_RELOGIN ) {
			
			System.out.println(prefix + "Logout Success (User ReLogin): " + obj.toString());
			
		} else if( resultCode == Cmd.ErroCode.UNKNOW ) {
			
			System.out.println(prefix + "Logout Fail: " + obj.toString());
			return false;
		}
		
		return true;
	}

	@Override
	public void disconnect() {
		
		System.out.println(prefix + "Disconnect <<<<<<<<<<<<<<");
	}
	
	@Override
	public void cmdResponse(Integer cmd, CommonObjB obj) {
		
		System.out.println( prefix + "cmd:" + cmd + ": " + obj.toString() );
		
	}
	
	public void sendHeartBeat(int requestCode) {
		
		CommonObjB heartbeat = new CommonObjB();
		heartbeat.putInt(Cmd.Heartbeat.REQUEST_CODE, requestCode);
		
		client.send(Cmd.Heartbeat.CMD, heartbeat);
	}
	
	public static void main(String[] args) throws Exception {
		
		ClientTest clientTest = new ClientTest("", "1", "888888", true, true);
		
		clientTest.client.connect("127.0.0.1", 7878);
		
	}
	
	
}











