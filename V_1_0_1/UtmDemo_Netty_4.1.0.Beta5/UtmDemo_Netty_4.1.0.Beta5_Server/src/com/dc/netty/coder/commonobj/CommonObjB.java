package com.dc.netty.coder.commonobj;

import java.util.HashMap;
import java.util.List;

/**
 * 存放通用类型的对象
 * 
 * @author Daemon
 *
 */
public class CommonObjB {
	
	/**
	 * 编码版本 v1
	 */
	public static final short CODER_VERSION_1 = 1;
	
	/**
	 * 对象结束标志
	 */
	public static final byte TYPE_OBJ_OVER = -1;
	
	public static final byte TYPE_INT = 1;
	public static final byte TYPE_INT_ARRAY = 2;
	public static final byte TYPE_FLOAT = 3;
	public static final byte TYPE_FLOAT_ARRAY = 4;
	public static final byte TYPE_DOUBLE = 5;
	public static final byte TYPE_DOUBLE_ARRAY = 6;
	public static final byte TYPE_LONG = 7;
	public static final byte TYPE_LONG_ARRAY = 8;
	public static final byte TYPE_BYTE = 9;
	public static final byte TYPE_BYTE_ARRAY = 10;
	public static final byte TYPE_BOOL = 11;
	public static final byte TYPE_COMMON_OBJ_B = 12;
	public static final byte TYPE_COMMON_OBJ_B_LIST = 13;
	public static final byte TYPE_UTF_STRING = 14;
	
	/**
	 * 存放参数的Map
	 */
	protected HashMap<Byte, CommonObjBValue> params = new HashMap<Byte, CommonObjBValue>();
	
	/**
	 * 要求编码的版本
	 * 
	 * @return
	 */
	public short getCoderVersion() {
		
		return CODER_VERSION_1;
	}
	
	public void putInt(byte key, int value) {
		
		params.put( key, new CommonObjBValue(TYPE_INT, value) );
	}
	
	public Integer getInt(byte key) {
		
		CommonObjBValue objValue = params.get( key );
		if( objValue == null )
			return null;
		
		return (Integer)objValue.param;
	}
	
	public void putIntList(byte key, List<Integer> value) {
		
		params.put( key, new CommonObjBValue(TYPE_INT_ARRAY, value) );
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> getIntList(byte key) {
		
		CommonObjBValue objValue = params.get( key );
		if( objValue == null )
			return null;
		
		return (List<Integer>)objValue.param;
	}
	
	public void putFloat(byte key, float value) {
		
		params.put( key, new CommonObjBValue(TYPE_FLOAT, value) );
	}
	
	public Float getFloat(byte key) {
		
		CommonObjBValue objValue = params.get( key );
		if( objValue == null )
			return null;
		
		return (Float)objValue.param;
	}
	
	public void putFloatList(byte key, List<Float> value) {
		
		params.put( key, new CommonObjBValue(TYPE_FLOAT_ARRAY, value) );
	}
	
	@SuppressWarnings("unchecked")
	public List<Float> getFloatList(byte key) {
		
		CommonObjBValue objValue = params.get( key );
		if( objValue == null )
			return null;
		
		return (List<Float>)objValue.param;
	}
	
	public void putDouble(byte key, double value) {
		
		params.put( key, new CommonObjBValue(TYPE_DOUBLE, value) );
	}
	
	public Double getDouble(byte key) {
		
		CommonObjBValue objValue = params.get( key );
		if( objValue == null )
			return null;
		
		return (Double)objValue.param;
	}
	
	public void putDoubleList(byte key, List<Double> value) {
		
		params.put( key, new CommonObjBValue(TYPE_DOUBLE_ARRAY, value) );
	}
	
	@SuppressWarnings("unchecked")
	public List<Double> getDoubleList(byte key) {
		
		CommonObjBValue objValue = params.get( key );
		if( objValue == null )
			return null;
		
		return (List<Double>)objValue.param;
	}
	
	public void putLong(byte key, long value) {
		
		params.put( key, new CommonObjBValue(TYPE_LONG, value) );
	}
	
	public Long getLong(byte key) {
		
		CommonObjBValue objValue = params.get( key );
		if( objValue == null )
			return null;
		
		return (Long)objValue.param;
	}
	
	public void putLongList(byte key, List<Long> value) {
		
		params.put( key, new CommonObjBValue(TYPE_LONG_ARRAY, value) );
	}
	
	@SuppressWarnings("unchecked")
	public List<Long> getLongList(byte key) {
		
		CommonObjBValue objValue = params.get( key );
		if( objValue == null )
			return null;
		
		return (List<Long>)objValue.param;
	}
	
	public void putByte(byte key, byte value) {
		
		params.put( key, new CommonObjBValue(TYPE_BYTE, value) );
	}
	
	public Byte getByte(byte key) {
		
		CommonObjBValue objValue = params.get( key );
		if( objValue == null )
			return null;
		
		return (Byte)objValue.param;
	}
	
	public void putByteArray(byte key, byte[] value) {
		
		params.put( key, new CommonObjBValue(TYPE_BYTE_ARRAY, value));
	}
	
	public byte[] getByteArray(byte key) {
		
		CommonObjBValue objValue = params.get( key );
		if( objValue == null )
			return null;
		
		return (byte[])objValue.param;
	}
	
	public void putBool(byte key, boolean value) {
		
		params.put( key, new CommonObjBValue(TYPE_BOOL, value) );
	}
	
	public Boolean getBool(byte key) {
		
		CommonObjBValue objValue = params.get( key );
		if( objValue == null )
			return null;
		
		return (Boolean)objValue.param;
	}
	
	public void putCommonObjB(byte key, CommonObjB value) {
		
		params.put( key, new CommonObjBValue(TYPE_COMMON_OBJ_B, value) );
	}
	
	public CommonObjB getCommonObjB(byte key) {
		
		CommonObjBValue objValue = params.get( key );
		if( objValue == null )
			return null;
		
		return (CommonObjB)objValue.param;
	}
	
	public void putCommonObjBList(byte key, List<CommonObjB> value) {
		
		params.put( key, new CommonObjBValue(TYPE_COMMON_OBJ_B_LIST, value) );
	}
	
	@SuppressWarnings("unchecked")
	public List<CommonObjB> getCommonObjBList(byte key) {
		
		CommonObjBValue objValue = params.get( key );
		if( objValue == null )
			return null;
		
		return (List<CommonObjB>)objValue.param;
	}
	
	public void putUtfString(byte key, String value) {
		
		params.put( key, new CommonObjBValue(TYPE_UTF_STRING, value) );
	}
	
	public String getUtfString(byte key) {
		
		CommonObjBValue objValue = params.get( key );
		if( objValue == null )
			return null;
		
		return (String)objValue.param;
	}

	@Override
	public String toString() {
		return params.toString();
	}
	
	
}
