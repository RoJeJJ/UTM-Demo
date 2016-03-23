package com.dc.netty.coder.commonobj;

import java.util.ArrayList;

/**
 * 
 * 编码器
 * （负责将编码对象编码成字节数组用于传输等）
 * 
 * @author Daemon
 *
 * @param <ObjType> 被编码对象的类型
 */
public interface IEncoder<ObjType> {

	/**
	 * 编码
	 * 
	 * @param param 要编码的对象
	 * @return 编码后的字节数组
	 */
	ArrayList<Byte> encoder( ObjType param );

}
