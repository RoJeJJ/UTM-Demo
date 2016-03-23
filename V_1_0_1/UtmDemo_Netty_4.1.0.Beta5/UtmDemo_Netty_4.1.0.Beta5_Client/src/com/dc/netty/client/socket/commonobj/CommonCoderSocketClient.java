package com.dc.netty.client.socket.commonobj;

import java.util.ArrayList;

import com.dc.netty.client.socket.IExceptionListener;
import com.dc.netty.client.socket.IServerEventHandler;
import com.dc.netty.client.socket.SocketClient;
import com.dc.netty.coder.cmd.ICmdDecoder;
import com.dc.netty.coder.cmd.ICmdEncoder;
import com.dc.netty.coder.commonobj.CommonContentEncoder;
import com.dc.netty.coder.commonobj.CommonObjB;
import com.dc.netty.coder.commonobj.IEncoder;

/**
 * 
 * 使用CommonObjB作为交互信息（采用CommonContentEncoder进行编码）的 Socket的客户端
 * 
 * @author Daemon
 *
 * @param <CmdType> cmd 类型
 */
public class CommonCoderSocketClient<CmdType> extends SocketClient<CmdType> {
	
	/**
	 * CommonObjB的编码器
	 */
	protected final IEncoder<CommonObjB> defualtEncoder = new CommonContentEncoder();
	
	public CommonCoderSocketClient(ICmdEncoder<CmdType> cmdEncoder,  ICmdDecoder<CmdType> cmdDecoder,
			CmdType login, CmdType logout,
			IServerEventHandler<CmdType> serverEventHandler,
			IExceptionListener exceptionListener) {
		
		super(cmdEncoder, cmdDecoder, login, logout, serverEventHandler, exceptionListener);
	}
	
	/**
	 * 编码，将CommonObjB对象转化成byte数组
	 * 
	 * @param params
	 * @return
	 */
	protected ArrayList<Byte> encoder(CommonObjB params) {
		
		return defualtEncoder.encoder(params);
	}
	
	/**
	 * 用户登录
	 * 
	 * @param loginObj 登录参数
	 */
	public void login(CommonObjB loginObj) {
		
		super.login( encoder(loginObj) );
	}
	
	/**
	 * 用户退出
	 * 
	 * @param logoutObj 退出参数
	 */
	public void logout(CommonObjB logoutObj) {
		
		super.logout( encoder(logoutObj) );
	}
	
	/**
	 * 发送cmd请求
	 * 
	 * @param cmd cmd
	 * @param params 请求参数
	 */
	public void send(CmdType cmd, CommonObjB params) {
		
		super.send( cmd, encoder(params) );
	}
}

