package com.dc.netty.coder.cmd;

import io.netty.buffer.ByteBuf;

/**
 * 
 * int类型cmd的编码和解码器
 * 
 * @author Daemon
 *
 */
public class NormalIntegerCmdCoder implements ICmdDecoder<Integer>, ICmdEncoder<Integer> {

	/**
	 * 解码（将编码后的cmd转化为原来的数据）
	 * 
	 * @param buf 从该buf中读取数据
	 * @return 解码后的cmd
	 */
	public Integer decoder( ByteBuf buf ) {
		
		return buf.readInt();
	}
	
	/**
	 * 编码（将cmd编码成字节数组用于传输等）
	 * 
	 * @param cmd 要编码的cmd
	 * @param buf 编码后写入到该buffer中
	 */
	public void encoder( Integer cmd, ByteBuf buf ) {
		
		buf.writeInt(cmd);
	}
	
	@Override
	public Integer decoder( byte[] datas, int[] readIndex ) {
		
		int index = readIndex[0];
		readIndex[0] = readIndex[0] + 4;
		
		return datas[index] << 24 | (datas[index+1] & 0xff) << 16 | (datas[index+2] & 0xff) << 8 | (datas[index+3] & 0xff);
		
		
	}
	
	@Override
	public byte[] encoder( Integer cmd ) {
		
		byte[] cmdDatas = new byte[4];
		
		int cmd_i = cmd;
		
		cmdDatas[0] = (byte)( cmd_i >>> 24 & 0xff  );
		cmdDatas[1] = (byte)( cmd_i >>> 16 & 0xff  );
		cmdDatas[2] = (byte)( cmd_i >>> 8 & 0xff  );
		cmdDatas[3] = (byte)( cmd_i & 0xff );
		
		return cmdDatas;
	}
}
