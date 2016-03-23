package com.dc.netty.coder.commonobj;

import io.netty.buffer.ByteBuf;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * 
 * CommonObjB的解码器
 * 
 * @author Daemon
 *
 */
public class CommonContentDecoder implements IDecoder {
	
	/**
	 * 从ByteBuf中读取数据解析成一个CommonObjB
	 * 
	 * @param buf 从该buf中读取数据
	 * @return 解码后的CommonObjB
	 */
	public CommonObjB decoder( ByteBuf buf ) {
		
		//获得编码方式的版本
		short version = buf.readShort();
		
		switch (version) {
		
			case CommonObjB.CODER_VERSION_1:
				
				return v1_decoder(buf);
	
			default:
				
				return null;
		}
		
	}

	/**
	 * v1编码格式数据的解码
	 * 
	 * @param buf 从该buf中读取数据
	 * @return 解码后的CommonObjB
	 */
	private CommonObjB v1_decoder( ByteBuf buf ) {
		
		CommonObjB obj = new CommonObjB();
		
		byte key, type;
		for( ; ; ) {
			
			//获取对象类型
			type = buf.readByte();
			if( type == CommonObjB.TYPE_OBJ_OVER )
				break;
			
			//获取对象名称
			key = buf.readByte();
			
			switch (type) {
			
				case CommonObjB.TYPE_INT:
					
					obj.putInt( key, buf.readInt() );
					break;
	
				case CommonObjB.TYPE_INT_ARRAY:
					
					int intArraySize = buf.readInt();
					ArrayList<Integer> intList = new ArrayList<Integer>(intArraySize);
					for( int j=0; j<intArraySize; j++ )
						intList.add( buf.readInt() );
					
					obj.putIntList(key, intList);
					break;
					
				case CommonObjB.TYPE_FLOAT:
					
					obj.putFloat( key, buf.readFloat() );
					break;
					
				case CommonObjB.TYPE_FLOAT_ARRAY:
					
					int floatArraySize = buf.readInt();
					ArrayList<Float> floatList = new ArrayList<Float>(floatArraySize);
					for( int j=0; j<floatArraySize; j++ )
						floatList.add( buf.readFloat() );
					
					obj.putFloatList(key, floatList);
					break;

				case CommonObjB.TYPE_DOUBLE:
					
					obj.putDouble( key, buf.readDouble() );
					break;
					
				case CommonObjB.TYPE_DOUBLE_ARRAY:
					
					int doubleArraySize = buf.readInt();
					ArrayList<Double> doubleList = new ArrayList<Double>(doubleArraySize);
					for( int j=0; j<doubleArraySize; j++ )
						doubleList.add( buf.readDouble() );
					
					obj.putDoubleList(key, doubleList);
					break;
	
				case CommonObjB.TYPE_LONG:
					
					obj.putLong( key, buf.readLong() );
					break;
					
				case CommonObjB.TYPE_LONG_ARRAY:
					
					int longArraySize = buf.readInt();
					ArrayList<Long> longList = new ArrayList<Long>(longArraySize);
					for( int j=0; j<longArraySize; j++ )
						longList.add( buf.readLong() );
					
					obj.putLongList(key, longList);
					break;
					
				case CommonObjB.TYPE_BYTE:
					
					obj.putByte( key, buf.readByte() );
					break;

				case CommonObjB.TYPE_BYTE_ARRAY:
					
					int byteArraySize = buf.readInt();
					byte[] byteArray = new byte[byteArraySize];
					buf.readBytes(byteArray);
					obj.putByteArray( key, byteArray );
					break;
	
				case CommonObjB.TYPE_BOOL:
					
					obj.putBool( key, buf.readBoolean() );
					break;
					
				case CommonObjB.TYPE_COMMON_OBJ_B:
					
					obj.putCommonObjB( key, v1_decoder(buf) );
					break;
					
				case CommonObjB.TYPE_COMMON_OBJ_B_LIST:
					
					int objArraySize = buf.readInt();
					ArrayList<CommonObjB> objList = new ArrayList<CommonObjB>(objArraySize);
					for( int j=0; j<objArraySize; j++ )
						objList.add( v1_decoder(buf) );
					
					obj.putCommonObjBList( key, objList );
					
					break;
					
				case CommonObjB.TYPE_UTF_STRING:
					
					try {
						
						int stringByteArraySize = buf.readInt();
						byte[] stringByteArray = new byte[stringByteArraySize];
						buf.readBytes(stringByteArray);
						
						obj.putUtfString( key, new String(stringByteArray, "UTF-8") );
						
					} catch (UnsupportedEncodingException e) {
						
						e.printStackTrace();
					}
				
					break;
			}
			
		}
		
		
		return obj;
	}
	
	@Override
	public CommonObjB decoder( byte[] datas, int[] readIndexs ) {
		
		//获得编码方式的版本
		short version = (short)( datas[ readIndexs[0] ] << 8 | datas[ readIndexs[0]+1 ] );

		readIndexs[0] = readIndexs[0] + 2;
		
		switch (version) {
		
			case CommonObjB.CODER_VERSION_1:
				
				return v1_decoder( datas, readIndexs );
	
			default:
				
				return null;
		}
		
	}
	
	/**
	 * v1编码格式数据的解码
	 * 
	 * @param datas 数据（其中包含了 这次要解码的数据）
	 * @param readIndexs 从哪个位置开始解析
	 * （eg：[8]，从datas下标8的位置开始解析（这里使用数组相当于引用类型的int，对readIndex的修改对调用的程序可见））
	 * @return 解码后的CommonObjB
	 */
	private CommonObjB v1_decoder( byte[] datas, int[] readIndexs ) {
		
		int index = readIndexs[0];
		
		CommonObjB obj = new CommonObjB();
		
		byte key, type;
		for( ; ; ) {
			
			type = datas[index++];
			if( type == CommonObjB.TYPE_OBJ_OVER )
				break;
			
			key = datas[index++];
			
			switch (type) {
			
				case CommonObjB.TYPE_INT:
					
					obj.putInt( key, datas[index++] << 24 | (datas[index++] & 0xff) << 16 | (datas[index++] & 0xff) << 8 | (datas[index++] & 0xff) );
					break;
	
				case CommonObjB.TYPE_INT_ARRAY:
					
					int intArraySize = datas[index++] << 24 | (datas[index++] & 0xff) << 16 | (datas[index++] & 0xff) << 8 | (datas[index++] & 0xff);
					ArrayList<Integer> intList = new ArrayList<Integer>(intArraySize);
					for( int j=0; j<intArraySize; j++ )
						intList.add( datas[index++] << 24 | (datas[index++] & 0xff) << 16 | (datas[index++] & 0xff) << 8 | (datas[index++] & 0xff) );
					
					obj.putIntList(key, intList);
					break;
					
				case CommonObjB.TYPE_FLOAT:
					
					obj.putFloat( key, Float.intBitsToFloat( datas[index++] << 24 | (datas[index++] & 0xff) << 16 | (datas[index++] & 0xff) << 8 | (datas[index++] & 0xff) ) );
					break;
					
				case CommonObjB.TYPE_FLOAT_ARRAY:
					
					int floatArraySize = datas[index++] << 24 | (datas[index++] & 0xff) << 16 | (datas[index++] & 0xff) << 8 | (datas[index++] & 0xff);
					ArrayList<Float> floatList = new ArrayList<Float>(floatArraySize);
					for( int j=0; j<floatArraySize; j++ )
						floatList.add( Float.intBitsToFloat( datas[index++] << 24 | (datas[index++] & 0xff) << 16 | (datas[index++] & 0xff) << 8 | (datas[index++] & 0xff) ) );
					
					obj.putFloatList(key, floatList);
					break;

				case CommonObjB.TYPE_DOUBLE:
					
					long d = (long) datas[index++] << 56 |
			                  ((long) datas[index++] & 0xff) << 48 |
			                  ((long) datas[index++] & 0xff) << 40 |
			                  ((long) datas[index++] & 0xff) << 32 |
			                  ((long) datas[index++] & 0xff) << 24 |
			                  ((long) datas[index++] & 0xff) << 16 |
			                  ((long) datas[index++] & 0xff) <<  8 |
			                   (long) datas[index++] & 0xff;
					
					obj.putDouble( key, Double.longBitsToDouble( d ) );
					break;
					
				case CommonObjB.TYPE_DOUBLE_ARRAY:
					
					
					int doubleArraySize = datas[index++] << 24 | (datas[index++] & 0xff) << 16 | (datas[index++] & 0xff) << 8 | (datas[index++] & 0xff);
					ArrayList<Double> doubleList = new ArrayList<Double>(doubleArraySize);
					for( int j=0; j<doubleArraySize; j++ ) {
						
						d = (long) datas[index++] << 56 |
				                  ((long) datas[index++] & 0xff) << 48 |
				                  ((long) datas[index++] & 0xff) << 40 |
				                  ((long) datas[index++] & 0xff) << 32 |
				                  ((long) datas[index++] & 0xff) << 24 |
				                  ((long) datas[index++] & 0xff) << 16 |
				                  ((long) datas[index++] & 0xff) <<  8 |
				                   (long) datas[index++] & 0xff;
						
						doubleList.add( Double.longBitsToDouble( d ) );
					}
					
					obj.putDoubleList(key, doubleList);
					break;
	
				case CommonObjB.TYPE_LONG:
					
					long l = (long) datas[index++] << 56 |
	                  ((long) datas[index++] & 0xff) << 48 |
	                  ((long) datas[index++] & 0xff) << 40 |
	                  ((long) datas[index++] & 0xff) << 32 |
	                  ((long) datas[index++] & 0xff) << 24 |
	                  ((long) datas[index++] & 0xff) << 16 |
	                  ((long) datas[index++] & 0xff) <<  8 |
	                   (long) datas[index++] & 0xff;
					
					obj.putLong( key, l );
					break;
					
				case CommonObjB.TYPE_LONG_ARRAY:
					
					int longArraySize = datas[index++] << 24 | (datas[index++] & 0xff) << 16 | (datas[index++] & 0xff) << 8 | (datas[index++] & 0xff);
					ArrayList<Long> longList = new ArrayList<Long>(longArraySize);
					for( int j=0; j<longArraySize; j++ ) {
						
						l = (long) datas[index++] << 56 |
				                  ((long) datas[index++] & 0xff) << 48 |
				                  ((long) datas[index++] & 0xff) << 40 |
				                  ((long) datas[index++] & 0xff) << 32 |
				                  ((long) datas[index++] & 0xff) << 24 |
				                  ((long) datas[index++] & 0xff) << 16 |
				                  ((long) datas[index++] & 0xff) <<  8 |
				                   (long) datas[index++] & 0xff;
						
						longList.add( l );
					}
						
					
					obj.putLongList(key, longList);
					break;
					
				case CommonObjB.TYPE_BYTE:
					
					obj.putByte( key, datas[index++] );
					break;

				case CommonObjB.TYPE_BYTE_ARRAY:
					
					int byteArraySize = datas[index++] << 24 | (datas[index++] & 0xff) << 16 | (datas[index++] & 0xff) << 8 | (datas[index++] & 0xff);
					byte[] byteArray = new byte[byteArraySize];
					
					System.arraycopy(datas, index, byteArray, 0, byteArraySize);
					index = index + byteArraySize;
					
					obj.putByteArray( key, byteArray );
					break;
	
				case CommonObjB.TYPE_BOOL:
					
					obj.putBool( key, datas[index++] != 0 );
					break;
					
				case CommonObjB.TYPE_COMMON_OBJ_B:
					
					readIndexs[0] = index;
					obj.putCommonObjB( key, v1_decoder(datas,readIndexs) );
					index = readIndexs[0];
					
					break;
					
				case CommonObjB.TYPE_COMMON_OBJ_B_LIST:
					
					int objArraySize = datas[index++] << 24 | (datas[index++] & 0xff) << 16 | (datas[index++] & 0xff) << 8 | (datas[index++] & 0xff);
					ArrayList<CommonObjB> objList = new ArrayList<CommonObjB>(objArraySize);
					
					readIndexs[0] = index;
					
					for( int j=0; j<objArraySize; j++ ) {
						
						objList.add( v1_decoder(datas,readIndexs) );
					}
					
					index = readIndexs[0];
					
					obj.putCommonObjBList( key, objList );
					
					break;
					
				case CommonObjB.TYPE_UTF_STRING:
					
					try {
						
						int stringByteArraySize = datas[index++] << 24 | (datas[index++] & 0xff) << 16 | (datas[index++] & 0xff) << 8 | (datas[index++] & 0xff);
						
						obj.putUtfString( key, new String(datas, index, stringByteArraySize, "UTF-8") );
						index = index + stringByteArraySize;
						
					} catch (UnsupportedEncodingException e) {
						
						e.printStackTrace();
					}
				
					break;
			}
			
		}
		
		readIndexs[0] = index;
		
		return obj;
	}
	
}