package com.dc.nutm;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class SocketListener<CmdType> implements Runnable {
	
	private final Socket socket;
	private final ClientEventHandler<CmdType> clientEventHandler;
	private final UtmClient<CmdType> utmClient;
	
	public SocketListener(Socket socket, ClientEventHandler<CmdType> clientEventHandler, 
			UtmClient<CmdType> utmClient) {

		this.socket = socket;
		this.clientEventHandler = clientEventHandler;
		this.utmClient = utmClient;
	}

	@Override
	public void run() {
		
		try {
			
			InputStream input = socket.getInputStream();
			ClientEventHandler<CmdType> clientEventHandler = this.clientEventHandler;
			
			byte[] datas;
			
			int h2, h1, l2, l1, length;
			for(;;) {
				
				try {
					
					h2 = input.read();
					h1 = input.read();
					l2 = input.read();
					l1 = input.read();
					
					length = h2 << 24 | h1 << 16 | l2 << 8 | l1;
					
					datas = new byte[length];
					for( int i=0; i<length; i++ ) {
						
						datas[i] = (byte)input.read();
					}
					
					clientEventHandler.getMsg(datas);
					
				} catch (IOException e) {

					e.printStackTrace();
					utmClient.ioException();
				}
				
			}
			
		} catch (IOException e1) {
			
			e1.printStackTrace();
			utmClient.ioException();
			
		}
	}

}
