package com.dc.nutm;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.dc.netty.coder.cmd.ICmdDecoder;
import com.dc.netty.coder.cmd.NormalIntegerCmdCoder;
import com.dc.netty.coder.commonobj.CommonContentDecoder;
import com.dc.netty.coder.commonobj.CommonContentEncoder;
import com.dc.netty.coder.commonobj.CommonObjB;
import com.dc.netty.coder.commonobj.IDecoder;
import com.dc.netty.coder.commonobj.IEncoder;
import com.dc.netty.utm.cmd.Cmd;

public class UtmClientDemo extends ClientEventHandler<Integer> {

	public UtmClientDemo(Integer login, Integer logout,
			ICmdDecoder<Integer> cmdDecoder,
			Map<Integer, IDecoder> cmdMapDecoder, IDecoder defaultDecoder) {
		
		super(login, logout, cmdDecoder, cmdMapDecoder, defaultDecoder);
	}

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws IOException {
		
		UtmClientDemo clientDemo = new UtmClientDemo( Cmd.Login.CMD,  Cmd.Logout.CMD, 
				new NormalIntegerCmdCoder(), new HashMap<Integer, IDecoder>(), new CommonContentDecoder() );
		
		UtmClient<Integer> utmClient = new UtmClient<Integer>("127.0.0.1", 7878, 
				new CommonContentEncoder(), new NormalIntegerCmdCoder(),
				new HashMap<Integer, IEncoder>(), clientDemo);
		
		CommonObjB loginObj = new CommonObjB();
		loginObj.putUtfString( Cmd.Login.USER_NAME, "aabb00" );
		loginObj.putUtfString( Cmd.Login.PASSWORD, "888888" );
		utmClient.login(loginObj);
		
		for(;;) {
			
			try {
				TimeUnit.SECONDS.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public void loginBack(Object param) {
		
		System.out.println( param.toString() );
	}

	@Override
	public void cmdBack(Integer cmd, Object param) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void logoutBack(Object param) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reconectSuccess() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disconect() {
		// TODO Auto-generated method stub
		
	}
}
