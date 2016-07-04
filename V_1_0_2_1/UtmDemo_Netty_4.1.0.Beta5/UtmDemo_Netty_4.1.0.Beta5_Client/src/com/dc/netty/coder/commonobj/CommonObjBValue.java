package com.dc.netty.coder.commonobj;

/**
 * 
 * CommonObjB 中存放参数的对象
 * （里面包含 类型 和 值）
 * 
 * @author Daemon
 *
 */
public class CommonObjBValue {
	
	public final byte type;
	public final Object param;

	public CommonObjBValue(byte type, Object param) {

		this.type = type;
		this.param = param;
	}

	@Override
	public String toString() {
		
		if( param instanceof byte[] ) {
			
			byte[] datas = (byte[])param;
			StringBuilder builder = new StringBuilder( datas.length << 2 + datas.length << 1 );
			
			builder.append("[ ");
			for( int i=0; i<datas.length; i++ )
				builder.append( datas[i] ).append(" ");
			
			builder.append("]");
			
			return builder.toString();
		}
		
		return param.toString();
	}
	
	

}
