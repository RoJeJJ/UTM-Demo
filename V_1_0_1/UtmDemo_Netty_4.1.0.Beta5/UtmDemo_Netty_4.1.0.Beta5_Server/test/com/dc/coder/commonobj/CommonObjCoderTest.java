package com.dc.coder.commonobj;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

import java.util.ArrayList;

import com.dc.netty.coder.commonobj.CommonContentDecoder;
import com.dc.netty.coder.commonobj.CommonContentEncoder;
import com.dc.netty.coder.commonobj.CommonObjB;

public class CommonObjCoderTest {
	
	static CommonContentEncoder encoder = new CommonContentEncoder();
	static CommonContentDecoder decoder = new CommonContentDecoder();
	
	public static void normalTest() {
		
		CommonObjB obj = new CommonObjB();
		
		obj.putInt((byte)1, 10101);
		
		ArrayList<Integer> t = new ArrayList<Integer>();
		t.add(10);t.add(7777);
		obj.putIntList((byte)2, t);
		
		obj.putFloat((byte)3, (float)0.112);
		
		ArrayList<Float> t2 = new ArrayList<Float>();
		t2.add((float)-0.99);t2.add((float)1110.9009);
		obj.putFloatList((byte)4, t2);
		
		obj.putDouble((byte)5, 1090.99999112);
		
		ArrayList<Double> t3 = new ArrayList<Double>();
		t3.add(-0.997897797979);t3.add(9990088888.9009);t3.add(110.9009);
		obj.putDoubleList((byte)6, t3);
		
		obj.putLong((byte)7, -101015555555552l);
		
		ArrayList<Long> t4 = new ArrayList<Long>();
		t4.add(222222222222222222l);t4.add(2l);t4.add(1l);
		obj.putLongList((byte)8, t4);
		
		obj.putByte((byte)9, (byte)100);
		
		obj.putByteArray((byte)10, new byte[]{-1,2,3,4,5,6,7,8,9,0});
		
		obj.putBool((byte)11, true);
		
		CommonObjB inner = new CommonObjB();
		inner.putLong((byte)1, -999);
		obj.putCommonObjB((byte)12, inner);
		
		ArrayList<CommonObjB> list = new ArrayList<CommonObjB>();
		list.add(inner);
		obj.putCommonObjBList((byte)13, list);
		
		obj.putUtfString((byte)14, "人才啊啊啊啊啊啊");

		
		ByteBuf byteBuf = UnpooledByteBufAllocator.DEFAULT.buffer();
		encoder.encoder(obj, byteBuf);
		
		ArrayList<Byte> byteList = encoder.encoder(obj);
		
		System.out.println();
		System.out.println("byte datas:");
		int size = byteBuf.writerIndex();
		byte[] datas = new byte[size];
		for( int i=0; i<size; i++ ) {
			
			datas[i] = byteBuf.readByte();
			System.out.print( datas[i] + " " );
		}
		
		System.out.println();
		
		byte[] datas2 = new byte[byteList.size()];
		for( int i=0; i<datas2.length; i++ ) {
			
			datas2[i] = byteList.get(i);
			System.out.print( byteList.get(i) + " " );
		}
			
		System.out.println();
		byteBuf.resetReaderIndex();
		
		CommonObjB obj2 = decoder.decoder(byteBuf);
		CommonObjB obj3 = decoder.decoder(datas,new int[]{0});
		CommonObjB obj4 = decoder.decoder(datas2,new int[]{0});
		
		System.out.println();
		System.out.println();
		System.out.println( "before:" + obj.toString() );
		System.out.println( "after1:" + obj2.toString() );
		System.out.println( "after2:" + obj3.toString() );
		System.out.println( "after3:" + obj4.toString() );
		System.out.println();
	}

	public static void main(String[] args) {
		
		normalTest();
		
	}
}



