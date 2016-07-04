package com.dc.netty.client.socket.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import com.dc.netty.client.socket.ConnectionLostListener;

/**
 * 
 * Socket发送消息的封装类
 * 
 * @author Daemon
 *
 */
public class SocketWriter {

	protected final OutputStream outputStream;
	
	/**
	 * 连接断开监听器
	 */
	protected final ConnectionLostListener connectionLostListener;
	
	public SocketWriter(OutputStream outputStream, 
			ConnectionLostListener connectionLostListener) {
		
		this.outputStream = outputStream;
		this.connectionLostListener = connectionLostListener;
	}
	
	/**
	 * 发送消息到服务端
	 * 
	 * @param cmdDatas cmd
	 * @param datas 参数
	 * @throws IOException io异常
	 */
	public void write(byte[] cmdDatas, ArrayList<Byte> datas) throws IOException {
		
		try {
			
			//消息长度（不包含头部的4个字节（表示消息的长度））
			int size = cmdDatas.length + datas.size();
			
			
			
//			//要发送的整个消息（包含头部的4个字节（表示消息的长度））
//			byte[] msg = new byte[size+4];
//			
//			//写入长度
//			msg[0] = (byte)( size >> 24 );
//			msg[1] = (byte)( ( size >> 16 ) & 0x0FF );
//			msg[2] = (byte)( ( size >> 8 ) & 0x0FF );
//			msg[3] = (byte)( size  & 0x0FF );
//			
//			//写入cmd
//	        System.arraycopy(cmdDatas, 0, msg, 4, cmdDatas.length);
//			
//	        //写入datas
//			int dataSize = datas.size();
//			for( int i=0, j=4+cmdDatas.length; i<dataSize; i++, j++ )
//				msg[j] = datas.get(i);
//			
//			synchronized(this) {
//				
//				//发送消息
//				outputStream.write(msg);
//				outputStream.flush();
//			}
			
			
			
			synchronized(this) {
				
				//写入长度
				outputStream.write( (byte)( size >> 24 ) );
				outputStream.write( (byte)( ( size >> 16 ) & 0x0FF ) );
				outputStream.write( (byte)( ( size >> 8 ) & 0x0FF ) );
				outputStream.write( (byte)( size  & 0x0FF ) );
				
				//写入cmd
				outputStream.write(cmdDatas);
				
				//写入datas
				for( byte d : datas )
					outputStream.write(d);
				
				//强制要求发送数据
				outputStream.flush();
				
			}
			
		} catch (IOException e) {
			
			if( e.getMessage().equals("Connection reset by peer: socket write error") ) {
				
				connectionLostListener.disconnect();
				
			} else {
				
				throw e;
			}
			
		}
		
	}
	
	
}



