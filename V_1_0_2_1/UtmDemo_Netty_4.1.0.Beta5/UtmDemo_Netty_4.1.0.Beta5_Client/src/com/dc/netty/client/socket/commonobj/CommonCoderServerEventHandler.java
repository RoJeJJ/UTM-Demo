package com.dc.netty.client.socket.commonobj;

import com.dc.netty.client.socket.ServerEventHandler;
import com.dc.netty.coder.commonobj.CommonContentDecoder;
import com.dc.netty.coder.commonobj.CommonObjB;

/**
 * 
 * 采用CommonContentDecoder解码的 事件监听器（连接成功或失败、登录返回、退出返回、服务端发送cmd返回等）
 * 
 * @author Daemon
 *
 * @param <CmdType> cmd类型
 */
public abstract class CommonCoderServerEventHandler<CmdType> extends ServerEventHandler<CmdType> {
	
	protected CommonContentDecoder decoder = new CommonContentDecoder();
	
	@Override
	public abstract void connectSuccess();
	
	@Override
	public abstract void connectFail(Exception e);
	
	/**
	 * 登录数据返回（如果解析数据后判断登录成功，则需返回true）
	 * 
	 * @param obj 登录返回的参数
	 * @return 登录是否成功
	 */
	public abstract boolean loginBack(CommonObjB obj);
	
	@Override
	public boolean loginBack(byte[] datas, int beginIndex) {
		
		//采用CommonContentDecoder解码
		CommonObjB obj = decoder.decoder(datas, new int[]{beginIndex});
		
		return loginBack(obj);
	}
	
	/**
	 * 退出数据返回（如果解析数据后判断退出成功，则需返回true）
	 * 
	 * @param obj 退出返回的参数
	 * @return 退出是否成功
	 */
	public abstract boolean logoutBack(CommonObjB obj);

	@Override
	public boolean logoutBack(byte[] datas, int beginIndex) {
		
		//采用CommonContentDecoder解码
		CommonObjB obj = decoder.decoder(datas, new int[]{beginIndex});
		
		return logoutBack(obj);
	}

	@Override
	public abstract void disconnect();

	public abstract void cmdResponse(CmdType cmd, CommonObjB obj);
	
	@Override
	public void cmdResponse(CmdType cmd, byte[] datas, int beginIndex) {
		
		//采用CommonContentDecoder解码
		CommonObjB obj = decoder.decoder(datas, new int[]{beginIndex});
		
		cmdResponse(cmd, obj);
	}
	
}
