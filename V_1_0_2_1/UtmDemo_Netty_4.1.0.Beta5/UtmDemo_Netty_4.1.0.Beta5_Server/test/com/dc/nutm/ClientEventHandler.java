package com.dc.nutm;

import java.util.Map;

import com.dc.netty.coder.cmd.ICmdDecoder;
import com.dc.netty.coder.commonobj.IDecoder;

public abstract class ClientEventHandler<CmdType> {
	
	final CmdType login, logout;
	
	private final ICmdDecoder<CmdType> cmdDecoder;
	private final Map<CmdType, IDecoder> cmdMapDecoder;
	private final IDecoder defaultDecoder;
	
	public ClientEventHandler( CmdType login, CmdType logout, ICmdDecoder<CmdType> cmdDecoder,
			Map<CmdType, IDecoder> cmdMapDecoder, IDecoder defaultDecoder) {

		this.login = login;
		this.logout = logout;
		this.cmdDecoder = cmdDecoder;
		this.cmdMapDecoder = cmdMapDecoder;
		this.defaultDecoder = defaultDecoder;
	}

	void getMsg(byte[] datas) {
		
		int[] readIndex = new int[]{ 0 };
		CmdType cmd = cmdDecoder.decoder(datas, readIndex);
		
		IDecoder decoder = cmdMapDecoder.get(cmd);
		if( decoder == null )
			decoder = defaultDecoder;
		
		Object param = decoder.decoder(datas, readIndex);
		
		if( login.equals(cmd) )
			loginBack(param);
		else if( logout.equals(cmd) )
			logoutBack(param);
		else 
			cmdBack(cmd, param);
		
	}
	
	public abstract void loginBack(Object param);
	
	public abstract void cmdBack(CmdType cmd, Object param);
	
	public abstract void logoutBack(Object param);
	
	public abstract void reconectSuccess();
	
	public abstract void disconect();
	
	
	
}
