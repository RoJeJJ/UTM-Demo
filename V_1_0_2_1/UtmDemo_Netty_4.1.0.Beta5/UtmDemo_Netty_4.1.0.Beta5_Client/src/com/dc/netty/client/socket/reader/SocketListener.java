package com.dc.netty.client.socket.reader;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import com.dc.netty.client.socket.ConnectionLostListener;
import com.dc.netty.client.socket.IExceptionListener;
import com.dc.netty.client.socket.MsgHandler;

/**
 * 
 * Socket监听的类
 * 
 * @author Daemon
 *
 */
public class SocketListener implements Runnable {
	
	/**
	 * 是否正在运行
	 */
	protected volatile boolean running = false;
	
	protected final Socket socket;
	/**
	 * 服务端消息处理器（处理并触发serverEventHandler相应的事件）
	 */
	@SuppressWarnings("rawtypes")
	protected final MsgHandler msgHandler;
	/**
	 * 连接断开监听器
	 */
	protected final ConnectionLostListener connectionLostListener;
	/**
	 * 异常监听器
	 */
	protected final IExceptionListener exceptionListener;
	
	
	@SuppressWarnings("rawtypes")
	public SocketListener(Socket socket, MsgHandler msgHandler,
			ConnectionLostListener connectionLostListener,
			IExceptionListener exceptionListener) {

		this.socket = socket;
		this.msgHandler = msgHandler;
		this.connectionLostListener = connectionLostListener;
		this.exceptionListener = exceptionListener;
		
		this.running = true;
	}
	
	/**
	 * 停止监听线程
	 */
	public void close() {
		
		running = false;
	}
	
	@Override
	public void run() {
		
		try {
			
			InputStream input = socket.getInputStream();
			
			byte[] datas;
			
			int h2, h1, l2, l1, length;
			for( ;running; ) {
				
				try {
					
					//读取消息长度
					h2 = input.read();
					h1 = input.read();
					l2 = input.read();
					l1 = input.read();
					
					length = h2 << 24 | h1 << 16 | l2 << 8 | l1;
					
					//读取消息体
					datas = new byte[length];
					for( int i=0; i<length; i++ ) {
						
						datas[i] = (byte)input.read();
					}
					
					//调用hander了处理消息
					msgHandler.handlerMsg(datas);
					
				} catch (IOException e) {

					exceptionListener.exception(e);
					
					//如果是连接断开，则触发connectionLostListener的断线事件（里面会触发serverEventHandler的断线事件）
					if( e.getMessage().equals("Connection reset") || e.getMessage().equals("socket closed") ) {
						
						connectionLostListener.disconnect();
					}
					
				}
				
			}
			
		} catch (IOException e) {
			
			exceptionListener.exception(e);
			
		}
	}

}
