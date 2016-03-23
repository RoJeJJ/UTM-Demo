package com.dc.nutm;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;

import com.dc.netty.coder.cmd.ICmdEncoder;
import com.dc.netty.coder.commonobj.IEncoder;

public class UtmClient<CmdType> {
	
	@SuppressWarnings("rawtypes")
	private final IEncoder defualtEncoder;
	private final ICmdEncoder<CmdType> cmdEncoder;
	
	private final SocketWriter writer;
	private final SocketListener<CmdType> listener;
	
	@SuppressWarnings("rawtypes")
	private final Map<CmdType, IEncoder> cmdMapEncoder;

	private boolean connected = false;
	private final ClientEventHandler<CmdType> clientEventHandler;
	
	@SuppressWarnings("rawtypes")
	public UtmClient( String host, int port, 
			IEncoder defualtEncoder, ICmdEncoder<CmdType> cmdEncoder,
			Map<CmdType, IEncoder> cmdMapEncoder,
			ClientEventHandler<CmdType> clientEventHandler ) throws IOException {

		this.defualtEncoder = defualtEncoder;
		this.cmdEncoder = cmdEncoder;
		this.cmdMapEncoder = cmdMapEncoder;
		this.clientEventHandler = clientEventHandler;
		
		Socket socket = new Socket(host, port);
		
		writer = new SocketWriter();
		writer.setOutputStream( socket.getOutputStream() );
		
		listener = new SocketListener<CmdType>( socket, clientEventHandler, this );
		new Thread( listener ).start();
		
		connected = true;
		
	}
	
	public void login(Object param) {
		
		send( clientEventHandler.login, param );
	}
	
	public void logout(Object param) {
		
		send( clientEventHandler.logout, param );
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void send(CmdType cmd, Object param) {
		
		byte[] cmdDatas;
		ArrayList<Byte> datas;
		
		cmdDatas = cmdEncoder.encoder(cmd);
		
		IEncoder encoder = cmdMapEncoder.get(cmd);
		if( encoder == null )
			datas = defualtEncoder.encoder(param);
		else 
			datas = encoder.encoder(param);
		
		try {
			
			writer.write( cmdDatas, datas );
			
		} catch (IOException e) {
			
			e.printStackTrace();
			ioException();
		}
		
	}
	
	synchronized void ioException() {
		
		if( connected )
			clientEventHandler.disconect();
		
		connected = false;
	}
	
}
