package com.dc.netty.coder.commonobj;

/**
 * 
 * 解码器接口
 * （负责将编码后的数据转化为原来的Object）
 * 
 * @author Daemon
 *
 */
public interface IDecoder {

	/**
	 * 解码
	 * 
	 * @param datas 数据（其中包含了 这次要解码的数据）
	 * @param readIndex 从哪个位置开始解析
	 * （eg：[8]，从datas下标8的位置开始解析（这里使用数组相当于引用类型的int，对readIndex的修改对调用的程序可见））
	 * @return 解码后的Object
	 */
	Object decoder( byte[] datas, int[] readIndex );
}
