package com.dc.netty.coder.cmd;

/**
 * 
 * cmd名称 的 编码器
 * （负责将cmd编码成字节数组用于传输等）
 * 
 * @author Daemon
 *
 * @param <CmdType> cmd 类型
 */
public interface ICmdEncoder<CmdType> {

	/**
	 * 编码
	 * 
	 * @param cmd 要编码的cmd
	 * @return 编码后的字节数组
	 */
	byte[] encoder(CmdType cmd);
}
