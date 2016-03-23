package com.dc.netty.coder.commonobj;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 * 
 * CommonObjB的编码器
 * 
 * @author Daemon
 *
 */
public class CommonContentEncoder implements IEncoder<CommonObjB> {
	
	/**
	 * 编码
	 * 
	 * @param obj 要编码的对象
	 * @param buf 编码后写入到该buffer中
	 */
	public void encoder( CommonObjB obj, ByteBuf buf ) {
		
		//获得编码方式的版本
		short version = obj.getCoderVersion();
		buf.writeShort(version);
		
		switch (version) {
		
			case CommonObjB.CODER_VERSION_1:
				
				v1_encoder(obj, buf);
				return;
	
			default:
				
				return;
		}
	}
	
	/**
	 * 以 v1编码格式 编码
	 * 
	 * @param obj 要编码的对象
	 * @param buf 编码后写入到该buffer中
	 */
	@SuppressWarnings("unchecked")
	private void v1_encoder( CommonObjB obj, ByteBuf buf ) {
		
		byte key, type;
		CommonObjBValue value;
		Object param;
		for( Entry<Byte, CommonObjBValue> entry : obj.params.entrySet() ) {
			
			key = entry.getKey();
			value = entry.getValue();
			
			type = value.type;
			param = value.param;
			
			switch (type) {
			
				case CommonObjB.TYPE_INT:
					
					buf.writeByte(CommonObjB.TYPE_INT);
					buf.writeByte(key);
					
					buf.writeInt((Integer)param);
					break;
				
				case CommonObjB.TYPE_INT_ARRAY:
					
					buf.writeByte(CommonObjB.TYPE_INT_ARRAY);
					buf.writeByte(key);
					
					List<Integer> intList = (List<Integer>)param;
					buf.writeInt(intList.size());
					
					for( Integer item : intList )
						buf.writeInt((Integer)item);
					break;
					
				case CommonObjB.TYPE_FLOAT:
					
					buf.writeByte(CommonObjB.TYPE_FLOAT);
					buf.writeByte(key);
					
					buf.writeFloat((Float)param);
					break;
					
				case CommonObjB.TYPE_FLOAT_ARRAY:
					
					buf.writeByte(CommonObjB.TYPE_FLOAT_ARRAY);
					buf.writeByte(key);
					
					List<Float> floatList = (List<Float>)param;
					buf.writeInt(floatList.size());
					
					for( Float item : floatList )
						buf.writeFloat(item);
					break;
					
				case CommonObjB.TYPE_DOUBLE:
					
					buf.writeByte(CommonObjB.TYPE_DOUBLE);
					buf.writeByte(key);
					
					buf.writeDouble((Double)param);
					break;
					
				case CommonObjB.TYPE_DOUBLE_ARRAY:
					
					buf.writeByte(CommonObjB.TYPE_DOUBLE_ARRAY);
					buf.writeByte(key);
					
					List<Double> doubleList = (List<Double>)param;
					buf.writeInt(doubleList.size());
					
					for( Double item : doubleList )
						buf.writeDouble(item);
					break;
					
				case CommonObjB.TYPE_LONG:
					
					buf.writeByte(CommonObjB.TYPE_LONG);
					buf.writeByte(key);
					
					buf.writeLong((Long)param);
					break;
					
				case CommonObjB.TYPE_LONG_ARRAY:
					
					buf.writeByte(CommonObjB.TYPE_LONG_ARRAY);
					buf.writeByte(key);
					
					List<Long> longList = (List<Long>)param;
					buf.writeInt(longList.size());
					
					for( Long item : longList )
						buf.writeLong(item);
					break;
					
				case CommonObjB.TYPE_BYTE:
					
					buf.writeByte(CommonObjB.TYPE_BYTE);
					buf.writeByte(key);
					
					buf.writeByte((Byte)param);
					break;
					
				case CommonObjB.TYPE_BYTE_ARRAY:
					
					buf.writeByte(CommonObjB.TYPE_BYTE_ARRAY);
					buf.writeByte(key);
					
					byte[] array = (byte[])param;
					buf.writeInt(array.length);
					buf.writeBytes(array);
					break;
					
				case CommonObjB.TYPE_BOOL:
					
					buf.writeByte(CommonObjB.TYPE_BOOL);
					buf.writeByte(key);
					
					buf.writeBoolean((Boolean)param);
					break;
					
				case CommonObjB.TYPE_COMMON_OBJ_B:
					
					buf.writeByte(CommonObjB.TYPE_COMMON_OBJ_B);
					buf.writeByte(key);
					
					v1_encoder( (CommonObjB)param, buf );
					break;
					
				case CommonObjB.TYPE_COMMON_OBJ_B_LIST:
					
					buf.writeByte(CommonObjB.TYPE_COMMON_OBJ_B_LIST);
					buf.writeByte(key);
					
					List<CommonObjB> commonObjBList = (List<CommonObjB>)param;
					buf.writeInt(commonObjBList.size());
					for( CommonObjB item : commonObjBList ) {
						
						v1_encoder( (CommonObjB)item, buf );
					}
					break;
					
				case CommonObjB.TYPE_UTF_STRING:
					
					buf.writeByte(CommonObjB.TYPE_UTF_STRING);
					buf.writeByte(key);
					
					byte[] stringBytes = ((String)param).getBytes();
					buf.writeInt(stringBytes.length);
					buf.writeBytes(stringBytes);
					break;
			
			}
			
		}
		
		buf.writeByte(CommonObjB.TYPE_OBJ_OVER);
		
	}
	
	@Override
	public ArrayList<Byte> encoder(CommonObjB obj) {
		
		ArrayList<Byte> byteList = new ArrayList<Byte>();
		
		short version = obj.getCoderVersion();
		
		byteList.add( (byte)( version >> 8 & 0xff ) );
		byteList.add( (byte)( version & 0xff ) );
		
		switch (version) {
		
			case CommonObjB.CODER_VERSION_1:
				
				v1_encoder(obj, byteList);
				return byteList;
				
			default:
				
				return null;
				
		}
		
	}
	
	/**
	 * 以 v1编码格式 编码
	 * 
	 * @param obj 要编码的对象
	 * @param byteList 编码后写入到该ArrayList中
	 */
	@SuppressWarnings("unchecked")
	private void v1_encoder( CommonObjB obj, ArrayList<Byte> byteList ) {
		
		byte key, type;
		CommonObjBValue value;
		Object param;
		for( Entry<Byte, CommonObjBValue> entry : obj.params.entrySet() ) {
			
			key = entry.getKey();
			value = entry.getValue();
			
			type = value.type;
			param = value.param;
			
			switch (type) {
			
				case CommonObjB.TYPE_INT:
					
					byteList.add(CommonObjB.TYPE_INT);
					byteList.add(key);
					
					int intValue = (Integer)param;
					byteList.add( (byte)(intValue >> 24) );
					byteList.add( (byte)(intValue >> 16) );
					byteList.add( (byte)(intValue >> 8) );
					byteList.add( (byte)intValue );
					
					break;
				
				case CommonObjB.TYPE_INT_ARRAY:
					
					byteList.add(CommonObjB.TYPE_INT_ARRAY);
					byteList.add(key);
					
					List<Integer> intList = (List<Integer>)param;
					int length = intList.size();
					byteList.add( (byte)(length >> 24) );
					byteList.add( (byte)(length >> 16) );
					byteList.add( (byte)(length >> 8) );
					byteList.add( (byte)length );
					
					for( Integer item : intList ) {
						
						intValue = (Integer)item;
						byteList.add( (byte)(intValue >> 24) );
						byteList.add( (byte)(intValue >> 16) );
						byteList.add( (byte)(intValue >> 8) );
						byteList.add( (byte)intValue );
					}
					break;
					
				case CommonObjB.TYPE_FLOAT:
					
					byteList.add(CommonObjB.TYPE_FLOAT);
					byteList.add(key);

					intValue = Float.floatToRawIntBits((Float)param);
					byteList.add( (byte)(intValue >> 24) );
					byteList.add( (byte)(intValue >> 16) );
					byteList.add( (byte)(intValue >> 8) );
					byteList.add( (byte)intValue );
					break;
					
				case CommonObjB.TYPE_FLOAT_ARRAY:
					
					byteList.add(CommonObjB.TYPE_FLOAT_ARRAY);
					byteList.add(key);
					
					List<Float> floatList = (List<Float>)param;
					length = floatList.size();
					byteList.add( (byte)(length >> 24) );
					byteList.add( (byte)(length >> 16) );
					byteList.add( (byte)(length >> 8) );
					byteList.add( (byte)length );
					
					for( Float item : floatList ) {
						
						intValue = Float.floatToRawIntBits((Float)item);
						byteList.add( (byte)(intValue >> 24) );
						byteList.add( (byte)(intValue >> 16) );
						byteList.add( (byte)(intValue >> 8) );
						byteList.add( (byte)intValue );
					}
					break;
					
				case CommonObjB.TYPE_DOUBLE:
					
					byteList.add(CommonObjB.TYPE_DOUBLE);
					byteList.add(key);
					
					long longValue = Double.doubleToRawLongBits((Double)param);
					byteList.add( (byte)(longValue >> 56) );
					byteList.add( (byte)(longValue >> 48) );
					byteList.add( (byte)(longValue >> 40) );
					byteList.add( (byte)(longValue >> 32) );
					byteList.add( (byte)(longValue >> 24) );
					byteList.add( (byte)(longValue >> 16) );
					byteList.add( (byte)(longValue >> 8) );
					byteList.add( (byte)longValue );
					break;
					
				case CommonObjB.TYPE_DOUBLE_ARRAY:
					
					byteList.add(CommonObjB.TYPE_DOUBLE_ARRAY);
					byteList.add(key);
					
					List<Double> doubleList = (List<Double>)param;
					length = doubleList.size();
					byteList.add( (byte)(length >> 24) );
					byteList.add( (byte)(length >> 16) );
					byteList.add( (byte)(length >> 8) );
					byteList.add( (byte)length );
					
					for( Double item : doubleList ) {
						
						longValue = Double.doubleToRawLongBits((Double)item);
						byteList.add( (byte)(longValue >> 56) );
						byteList.add( (byte)(longValue >> 48) );
						byteList.add( (byte)(longValue >> 40) );
						byteList.add( (byte)(longValue >> 32) );
						byteList.add( (byte)(longValue >> 24) );
						byteList.add( (byte)(longValue >> 16) );
						byteList.add( (byte)(longValue >> 8) );
						byteList.add( (byte)longValue );
					}
					break;
					
				case CommonObjB.TYPE_LONG:
					
					byteList.add(CommonObjB.TYPE_LONG);
					byteList.add(key);
					
					longValue = (Long)param;
					byteList.add( (byte)(longValue >> 56) );
					byteList.add( (byte)(longValue >> 48) );
					byteList.add( (byte)(longValue >> 40) );
					byteList.add( (byte)(longValue >> 32) );
					byteList.add( (byte)(longValue >> 24) );
					byteList.add( (byte)(longValue >> 16) );
					byteList.add( (byte)(longValue >> 8) );
					byteList.add( (byte)longValue );
					
					break;
					
				case CommonObjB.TYPE_LONG_ARRAY:
					
					byteList.add(CommonObjB.TYPE_LONG_ARRAY);
					byteList.add(key);
					
					List<Long> longList = (List<Long>)param;
					length = longList.size();
					byteList.add( (byte)(length >> 24) );
					byteList.add( (byte)(length >> 16) );
					byteList.add( (byte)(length >> 8) );
					byteList.add( (byte)length );
					
					for( Long item : longList ) {
						
						longValue = (Long)item;
						byteList.add( (byte)(longValue >> 56) );
						byteList.add( (byte)(longValue >> 48) );
						byteList.add( (byte)(longValue >> 40) );
						byteList.add( (byte)(longValue >> 32) );
						byteList.add( (byte)(longValue >> 24) );
						byteList.add( (byte)(longValue >> 16) );
						byteList.add( (byte)(longValue >> 8) );
						byteList.add( (byte)longValue );
					}
					break;
					
				case CommonObjB.TYPE_BYTE:
					
					byteList.add(CommonObjB.TYPE_BYTE);
					byteList.add(key);
					
					byteList.add((Byte)param);
					break;
					
				case CommonObjB.TYPE_BYTE_ARRAY:
					
					byteList.add(CommonObjB.TYPE_BYTE_ARRAY);
					byteList.add(key);
					
					byte[] array = (byte[])param;
					length = array.length;
					byteList.add( (byte)(length >> 24) );
					byteList.add( (byte)(length >> 16) );
					byteList.add( (byte)(length >> 8) );
					byteList.add( (byte)length );
					for( byte b : array )
						byteList.add(b);
					break;
					
				case CommonObjB.TYPE_BOOL:
					
					byteList.add(CommonObjB.TYPE_BOOL);
					byteList.add(key);
					
					byteList.add( (Boolean)param ? (byte)1 : (byte)0 );
					break;
					
				case CommonObjB.TYPE_COMMON_OBJ_B:
					
					byteList.add(CommonObjB.TYPE_COMMON_OBJ_B);
					byteList.add(key);
					
					v1_encoder( (CommonObjB)param, byteList );
					
					break;
					
				case CommonObjB.TYPE_COMMON_OBJ_B_LIST:
					
					byteList.add(CommonObjB.TYPE_COMMON_OBJ_B_LIST);
					byteList.add(key);
					
					List<CommonObjB> commonObjBList = (List<CommonObjB>)param;
					length = commonObjBList.size();
					byteList.add( (byte)(length >> 24) );
					byteList.add( (byte)(length >> 16) );
					byteList.add( (byte)(length >> 8) );
					byteList.add( (byte)length );
					for( CommonObjB item : commonObjBList ) {
						
						v1_encoder( (CommonObjB)item, byteList );
					}
					break;
					
				case CommonObjB.TYPE_UTF_STRING:
					
					byteList.add(CommonObjB.TYPE_UTF_STRING);
					byteList.add(key);
					
					byte[] stringBytes = ((String)param).getBytes();
					length = stringBytes.length;
					byteList.add( (byte)(length >> 24) );
					byteList.add( (byte)(length >> 16) );
					byteList.add( (byte)(length >> 8) );
					byteList.add( (byte)length );

					for( byte b : stringBytes )
						byteList.add(b);
					break;
			
			}
			
		}
		
		byteList.add(CommonObjB.TYPE_OBJ_OVER);
		
	}
}
