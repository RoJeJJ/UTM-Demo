package com.dc.netty.client.socket;

/**
 * 
 * 异常监听器
 * 
 * @author Daemon
 *
 */
public interface IExceptionListener {

	/**
	 * 程序异常
	 * 
	 * @param e 异常
	 */
	void exception(Exception e);
	
}

