package com.dc.netty.client.socket;


/**
 * 
 * 事件监听器（连接成功或失败、登录返回、退出返回、服务端发送cmd返回等）
 * 
 * @author Daemon
 *
 * @param <CmdType>
 */
public interface IServerEventHandler<CmdType> {
	
	/**
	 * 连接服务端成功
	 */
	void connectSuccess();
	
	/**
	 * 连接服务端失败
	 * 
	 * @param e 失败的异常
	 * ( 1. UnknownHostException - if the IP address of the host could not be determined. 
	 * 	 2. IOException - if an I/O error occurs when creating the socket.
	 *   3. SecurityException - if a security manager exists and its checkConnect method doesn't allow the operation.
	 *   )
	 */
	void connectFail(Exception e);

	/**
	 * 登录数据返回（如果解析数据后判断登录成功，则需返回true）
	 * 
	 * @param datas 登录返回的数据
	 * @param beginIndex 从数据的哪个位置开始解析（在beginIndex是已经被解析的数据（其他用途 eg：记录cmd类型））
	 * @return 登录是否成功
	 */
	boolean loginBack(byte[] datas, int beginIndex);
	
	/**
	 * 退出数据返回（如果解析数据后判断退出成功，则需返回true）
	 * 
	 * @param datas 退出返回的数据
	 * @param beginIndex 从数据的哪个位置开始解析（在beginIndex是已经被解析的数据（其他用途 eg：记录cmd类型））
	 * @return 退出是否成功
	 */
	boolean logoutBack(byte[] datas, int beginIndex);
	
	/**
	 * 和服务端的连接断开
	 */
	void disconnect();
	
	/**
	 * 服务端的cmd返回
	 * 
	 * @param cmd cmd
	 * @param datas 对应的数据
	 * @param beginIndex 从数据的哪个位置开始解析（在beginIndex是已经被解析的数据（其他用途 eg：记录cmd类型））
	 */
	void cmdResponse(CmdType cmd, byte[] datas, int beginIndex);
	
}
