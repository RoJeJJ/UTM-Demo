package com.dc.netty.client.socket;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import com.dc.netty.client.socket.reader.SocketListener;
import com.dc.netty.client.socket.writer.SocketWriter;
import com.dc.netty.coder.cmd.ICmdDecoder;
import com.dc.netty.coder.cmd.ICmdEncoder;

/**
 * 
 * 基于Socket的客户端，消息编码：头4位表示消息体长度(不包含头4位) + 消息体，
 * 提供了基本的 登录、退出、断线、发送消息等方法
 * 
 * @author Daemon
 *
 * @param <CmdType>
 */
public class SocketClient<CmdType> {
	
	/**
	 * cmd编码器
	 */
	protected final ICmdEncoder<CmdType> cmdEncoder;

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
	 * 服务端消息处理器（处理并触发serverEventHandler相应的事件）
	 */
	protected final MsgHandler<CmdType> msgHandler;
	
	/**
	 * 异常监听器
	 */
	protected final IExceptionListener exceptionListener;
	
	/**
	 * 客户端是否连接上了服务端
	 */
	protected volatile boolean isConnect = false;
	/**
	 * 客户端是否已登录
	 */
	protected volatile boolean isLogin = false;
	
	/**
	 * 与服务端的连接Socket
	 */
	protected Socket socket;
	/**
	 * Socket发送消息的封装类
	 */
	protected SocketWriter socketWriter;
	/**
	 * Socket监听的类
	 */
	protected SocketListener socketListener;
	/**
	 * Socket监听的线程
	 */
	protected Thread listener;
	
	
	public SocketClient(ICmdEncoder<CmdType> cmdEncoder, ICmdDecoder<CmdType> cmdDecoder,
			CmdType login, CmdType logout, 
			IServerEventHandler<CmdType> serverEventHandler,
			IExceptionListener exceptionListener) {
		
		this.cmdEncoder = cmdEncoder;
		this.login = login;
		this.logout = logout;
		this.serverEventHandler = serverEventHandler;
		this.exceptionListener = exceptionListener;
		
		msgHandler = new MsgHandler<CmdType>(this, cmdDecoder, login, logout, serverEventHandler, exceptionListener);
	}
	
	/**
	 * 设置客户端为登录成功（当serverEventHandler的Login方法返回true的时候，MsgHandler则会调用该方法）
	 */
	void loginSuccess() {
		
		isLogin = true;
	}
	
	/**
	 * 设置客户端为退出（当serverEventHandler的Logout方法返回true的时候，MsgHandler则会调用该方法）
	 */
	void logoutSuccess() {
		
		isLogin = false;
	}
	
	/**
	 * 客户端是否连接上了服务端
	 * 
	 * @return 客户端是否连接上了服务端
	 */
	public boolean isConnect() {
		return isConnect;
	}
	
	/**
	 * 客户端是否已登录
	 * 
	 * @return 客户端是否已登录
	 */
	public boolean isLogin() {
		return isLogin;
	}
	
	/**
	 * 连接服务端
	 * 
	 * @param host 地址
	 * @param port 断口
	 */
	public void connect(String host, int port) {
		
		try {
			
			//如果原来存在连接，则先断开连接
			if( isConnect )
				_disconnect(false);
			
			//连接断开监听器
			ConnectionLostListener connectionLostListener = new ConnectionLostListener(this, serverEventHandler, exceptionListener);
			
			//连接到服务端
			socket = new Socket(host, port);
			
			socketWriter = new SocketWriter( socket.getOutputStream(), connectionLostListener );
			socketListener = new SocketListener( socket, msgHandler, connectionLostListener, exceptionListener );
			
			//启动监听线程
			listener = new Thread( socketListener );
			listener.setName("socketListener");
			listener.start();
			
			isConnect = true;
			
			//触发连接成功事件
			serverEventHandler.connectSuccess();
			
		} catch (Exception e) {
			
			exceptionListener.exception(e);
			
			//触发连接失败事件
			serverEventHandler.connectFail(e);
		}
		
	}
	
	/**
	 * 登录
	 * 
	 * @param datas 发送到服务端的数据
	 * @return 是否发送了登录信息（如果为断开状态，不会发送）
	 */
	public boolean login(ArrayList<Byte> datas) {
		
		if( isConnect ) {
			
			send(login, datas);
			return true;
			
		} else {
			
			return false;
		}
		
	}
	
	/**
	 * 退出
	 * 
	 * @param datas 发送到服务端的数据
	 * @return 是否发送了退出信息（如果为断开状态或者未登录状态，不会发送）
	 */
	public boolean logout(ArrayList<Byte> datas) {
		
		if( isConnect && isLogin ) {
			
			send(logout, datas);
			return true;
			
		} else {
			
			return false;
		}
	}
	
	/**
	 * 断开连接
	 * 
	 * @param isUserOperator 是否是用户主动断开（主动断开则不会触发serverEventHandler.disconnect事件）
	 * @return 是否调用了处理方法（如果为断开状态，不会处理）
	 * 
	 * @throws IOException
	 */
	synchronized boolean _disconnect(boolean isUserOperator) throws IOException {
		
		if( isConnect ) {
			
			isConnect = false;
			isLogin = false;
			
			Socket socket = this.socket;
			SocketWriter socketWriter = this.socketWriter;
			SocketListener socketListener = this.socketListener;
			Thread listener = this.listener;
			
			if( socketListener != null ) {
				
				socketListener.close();
				this.socketListener = null;
			}
			
			if( listener != null ) {
				
				listener.interrupt();
				this.listener = null;
			}
			
			if( socket != null ) {
				
				socket.close();
				this.socket = null;
			}
			
			if( socketWriter != null ) {
				
				this.socketWriter = null;
			}
			
			if( !isUserOperator )
				serverEventHandler.disconnect();
			
			return true;
			
		} else {
			
			return false;
		}
	}
	
	/**
	 * 断开连接
	 * 
	 * @return 是否调用了处理方法（如果为断开状态，不会处理）
	 * 
	 * @throws IOException
	 */
	public boolean disconnect() throws IOException {
		
		return _disconnect(true);
	}
	
	/**
	 * 发送cmd请求
	 * 
	 * @param cmd cmd
	 * @param datas 数据
	 */
	public void send(CmdType cmd, ArrayList<Byte> datas) {
		
		try {
			
			byte[] cmdDatas = cmdEncoder.encoder(cmd);
			
			SocketWriter socketWriter = this.socketWriter;
			if( socketWriter != null )
				socketWriter.write( cmdDatas, datas );
			
		} catch (IOException e) {
			
			exceptionListener.exception(e);
		}
	}

}


