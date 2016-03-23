package com.dc.netty.client.socket;

/**
 * 
 * 事件监听器（连接成功或失败、登录返回、退出返回、服务端发送cmd返回等）
 * 
 * @author Daemon
 *
 * @param <CmdType> cmd 类型
 */
public abstract class ServerEventHandler<CmdType> implements IServerEventHandler<CmdType> {

	@Override
	public abstract void connectSuccess();
	
	@Override
	public abstract void connectFail(Exception e);

	@Override
	public abstract boolean loginBack(byte[] datas, int beginIndex);

	@Override
	public abstract boolean logoutBack(byte[] datas, int beginIndex);

	@Override
	public abstract void disconnect();

	@Override
	public abstract void cmdResponse(CmdType cmd, byte[] datas, int beginIndex);

}
