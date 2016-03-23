package com.dc.netty.coder.cmd;

/**
 * 
 * cmd名称 的 解码器
 * （负责将编码后的cmd转化为原来的数据）
 * 
 * @author Daemon
 *
 * @param <CmdType> cmd 类型
 */
public interface ICmdDecoder<CmdType> {

	/**
	 * 解码
	 * 
	 * @param datas 数据（其中包含了 编码后的cmd数据）
	 * @param readIndex 从哪个位置开始解析
	 * （eg：[8]，从datas下标8的位置开始解析（这里使用数组相当于引用类型的int，对readIndex的修改对调用的程序可见））
	 * @return 解码后的cmd
	 */
	CmdType decoder( byte[] datas, int[] readIndex );
}
