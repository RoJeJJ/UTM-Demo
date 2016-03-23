package com.dc.netty.client.socket;

import java.io.IOException;

/**
 * 
 * 连接断开监听器
 * 
 * @author Daemon
 *
 */
public class ConnectionLostListener {

	@SuppressWarnings("rawtypes")
	protected final SocketClient socketClient;
	@SuppressWarnings("rawtypes")
	protected final IServerEventHandler serverEventHandler;
	protected final IExceptionListener exceptionListener;
	
	
	
	@SuppressWarnings("rawtypes")
	public ConnectionLostListener(SocketClient socketClient,
			IServerEventHandler serverEventHandler,
			IExceptionListener exceptionListener) {

		this.socketClient = socketClient;
		this.serverEventHandler = serverEventHandler;
		this.exceptionListener = exceptionListener;
	}



	/**
	 * 与服务端连接断开
	 */
	public void disconnect() {
		
		try {
			
			//调用断线处理
			socketClient._disconnect(false);
			
		} catch (IOException e) {
			
			exceptionListener.exception(e);
		}
		
		
	}
}
