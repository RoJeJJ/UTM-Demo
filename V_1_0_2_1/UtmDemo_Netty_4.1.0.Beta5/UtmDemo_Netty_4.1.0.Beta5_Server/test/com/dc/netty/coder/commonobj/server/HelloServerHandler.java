package com.dc.netty.coder.commonobj.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import com.dc.netty.coder.commonobj.CommonContentDecoder;
import com.dc.netty.coder.commonobj.CommonContentEncoder;
import com.dc.netty.coder.commonobj.CommonObjB;

public class HelloServerHandler extends SimpleChannelInboundHandler<ByteBuf> {
	
	private final CommonContentEncoder encoder = new CommonContentEncoder();
	private final CommonContentDecoder decoder = new CommonContentDecoder();
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        
    	Channel channel = ctx.channel();
    	
    	CommonObjB obj = decoder.decoder(msg);
    	
    	int index = obj.getInt((byte)0);
//    	if( index % 100000 == 0 )
    		System.out.println( channel.remoteAddress() + " Say: " + index );
    	
//    	if( Math.random() > 0.99999 )
    		System.out.println( channel.remoteAddress() + " Say: " + obj.getUtfString((byte)100) );
        
    	CommonObjB objReturn = new CommonObjB();
    	objReturn.putInt((byte)0, index);
    	objReturn.putUtfString((byte)99, "你妹啊 什么情况？？？？");
    	
    	ByteBuf byteBuf = channel.alloc().buffer();
    	encoder.encoder( objReturn, byteBuf );
    	
    	channel.writeAndFlush( byteBuf );
    	
    }
    
    /*
     * 
     * 覆盖 channelActive 方法 在channel被启用的时候触发 (在建立连接的时候)
     * 
     * channelActive 和 channelInActive 在后面的内容中讲述，这里先不做详细的描述
     * */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        
        System.out.println("RamoteAddress : " + ctx.channel().remoteAddress() + " active !");
        
        super.channelActive(ctx);
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	
    	
    }
    
    
}