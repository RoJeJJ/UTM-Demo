package com.dc.nutm;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class SocketWriter {

	private volatile OutputStream outputStream;
	
	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	public synchronized void write(byte[] cmdDatas, ArrayList<Byte> datas) throws IOException {
		
		int size = cmdDatas.length + datas.size();
		
		//长度
		outputStream.write( (byte)( size >> 24 ) );
		outputStream.write( (byte)( ( size >> 16 ) & 0x0FF ) );
		outputStream.write( (byte)( ( size >> 8 ) & 0x0FF ) );
		outputStream.write( (byte)( size  & 0x0FF ) );
		
		//cmd
		outputStream.write(cmdDatas);
		
		//数据
		for( byte d : datas )
			outputStream.write(d);
	}
	
	
}



