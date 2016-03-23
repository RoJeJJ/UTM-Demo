package com.dc.netty.coder.commonobj.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import com.dc.netty.coder.commonobj.CommonContentDecoder;
import com.dc.netty.coder.commonobj.CommonContentEncoder;
import com.dc.netty.coder.commonobj.CommonObjB;

public class SocketClient implements Runnable {
	
	private final InputStream input;
	
	public SocketClient(InputStream input) {
		
		this.input = input;
	}

	public static String host = "127.0.0.1";
	public static int port = 7878;

	public static void main(String[] args) throws Exception {
		
		CommonObjB obj = new CommonObjB();
		obj.putUtfString((byte)100, "收到请回复！ 收到请回复!");
		
		ByteBuf byteBuf = UnpooledByteBufAllocator.DEFAULT.buffer();
		
		
		
		Socket socket = new Socket(host, port);
		
		new Thread( new SocketClient( socket.getInputStream() ) ).start();
		
		
		CommonContentEncoder encoder = new CommonContentEncoder();
		
		int index = 0;
		OutputStream out = socket.getOutputStream();
		for (;;) {
			
			byteBuf.clear();
			
			obj.putInt((byte)0, index++);
			encoder.encoder(obj, byteBuf);
			
			int size = byteBuf.writerIndex();
			byte[] datas = new byte[size+4];
			
			datas[0] = (byte)( size >> 24 );
			datas[1] = (byte)( ( size >> 16 ) & 0x0FF );
			datas[2] = (byte)( ( size >> 8 ) & 0x0FF );
			datas[3] = (byte)( size  & 0x0FF );
			
			for( int i=0; i<size; i++ ) {
				datas[i+4] = byteBuf.readByte();
			}
			
			out.write( datas, 0, datas.length);
			out.flush();
			
			TimeUnit.MILLISECONDS.sleep(1000);
		}
		
	}

	@Override
	public void run() {
		
		CommonContentDecoder decoder = new CommonContentDecoder();
		
		ByteBuf byteBuf = UnpooledByteBufAllocator.DEFAULT.buffer();
		
		CommonObjB obj;
		int h2, h1, l2, l1, length, index;
		for(;;) {
			
			try {
				
				h2 = input.read();
				h1 = input.read();
				l2 = input.read();
				l1 = input.read();
				
				length = h2 << 24 | h1 << 16 | l2 << 8 | l1;
				
				byteBuf.clear();
				
				for( int i=0; i<length; i++ ) {
					
					byteBuf.writeByte( input.read() );
				}
				
				obj = decoder.decoder(byteBuf);
				index = obj.getInt((byte)0);
				
//				if( index % 100000 == 0 )
		    		System.out.println( "Server Say: " + index );
				
//				if( Math.random() > 0.99999 )
		    		System.out.println( "Server Say: " + obj.getUtfString((byte)99) );
				
			} catch (Exception e) {

				e.printStackTrace();
			}
			
		}
		
	}
}




