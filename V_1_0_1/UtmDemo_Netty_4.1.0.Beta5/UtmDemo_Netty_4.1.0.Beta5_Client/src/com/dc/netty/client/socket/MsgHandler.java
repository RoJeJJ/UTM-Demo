package com.dc.netty.client.socket;

import com.dc.netty.coder.cmd.ICmdDecoder;

/**
 * 
 * 服务端消息处理器（处理并触发serverEventHandler相应的事件）
 * 
 * @author Daemon
 *
 * @param <CmdType> cmd类型
 */
public class MsgHandler<CmdType> {
	
	/**
	 * 基于Socket的客户端
	 */
	protected final SocketClient<CmdType> socketClient;
	
	/**
	 * cmd解码器
	 */
	protected final ICmdDecoder<CmdType> cmdDecoder;
	
	/**
	 * 用户登录的cmd
	 */
	protected final CmdType login;
	/**
	 * 用户退出登录的cmd
	 */
	protected final CmdType logout;
	
	/**
	 * 事件监听器（连接成功或失败、登录返回、退出返回、服务端发送cmd返回等）
	 */
	protected final IServerEventHandler<CmdType> serverEventHandler;
	
	/**
	 * 异常监听器
	 */
	protected final IExceptionListener exceptionListener;
	
	
	public MsgHandler(SocketClient<CmdType> socketClient,
			ICmdDecoder<CmdType> cmdDecoder, CmdType login,
			CmdType logout, IServerEventHandler<CmdType> serverEventHandler,
			IExceptionListener exceptionListener) {

		this.socketClient = socketClient;
		this.cmdDecoder = cmdDecoder;
		this.login = login;
		this.logout = logout;
		this.serverEventHandler = serverEventHandler;
		this.exceptionListener = exceptionListener;
	}
	
	
	public void handlerMsg(byte[] datas) {
		
		try {
			
			//解码 cmd
			int[] readIndex = new int[]{ 0 };
			CmdType cmd = cmdDecoder.decoder(datas, readIndex);
			
			if( cmd.equals(login) ) {
				
				//触发“登录”事件
				if( serverEventHandler.loginBack(datas, readIndex[0]) ) {
					
					//如果登录成功，调用登录成功方法
					socketClient.loginSuccess();
				}
				
			} else if( cmd.equals(logout) ) {
				
				//触发“退出”事件
				if( serverEventHandler.logoutBack(datas, readIndex[0]) ) {
					
					//如果退出成功，调用退出成功方法
					socketClient.logoutSuccess();
				}
				
			} else {
				
				//触发 “服务端的cmd返回” 事件
				serverEventHandler.cmdResponse(cmd, datas, readIndex[0]);
			}
			
		} catch (Exception e) {
			
			exceptionListener.equals(e);
		}
		
	}
	
}
