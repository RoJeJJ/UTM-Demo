package com.dc.netty.hello.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class SocketClient {

	public static String host = "127.0.0.1";
	public static int port = 7878;

	public static void main(String[] args) throws Exception {
		
		Socket socket = new Socket(host, port);
		
		byte[] datas = new byte[] { 0, 0, 0, 4, 115, 98 };
		byte[] datas2 = new byte[] { 115, 98 };
		
		for (;;) {
			
			socket.getOutputStream().write(datas, 0, datas.length);
			socket.getOutputStream().flush();
			
			TimeUnit.SECONDS.sleep(1);
			
			socket.getOutputStream().write(datas2, 0, datas2.length);
			socket.getOutputStream().flush();
			
		}
		
	}
}
