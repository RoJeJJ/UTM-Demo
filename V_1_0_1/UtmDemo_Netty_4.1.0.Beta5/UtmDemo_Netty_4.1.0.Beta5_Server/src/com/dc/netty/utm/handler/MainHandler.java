package com.dc.netty.utm.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import com.dc.netty.coder.cmd.NormalIntegerCmdCoder;
import com.dc.netty.coder.commonobj.CommonContentDecoder;
import com.dc.netty.coder.commonobj.CommonObjB;
import com.dc.netty.utm.center.ControlCenter;
import com.dc.netty.utm.center.LoggerCenter;
import com.dc.netty.utm.cmd.Cmd;
import com.dc.netty.utm.obj.Link;
import com.dc.netty.utm.obj.User;
import com.dc.netty.utm.obj.Visitor;
import com.dc.utm.filter.UserThreadModeFilter;

/**
 * 
 * Netty消息控制器（客户端连接、断开、发送消息）
 * 
 * @author Daemon
 *
 */
public class MainHandler extends SimpleChannelInboundHandler<ByteBuf> {
	
	/**
	 * 请求计数器
	 */
	private final AtomicInteger requestIds;
	
	/**
	 * utm过滤器
	 */
	private final UserThreadModeFilter<Integer, ChannelId, Visitor, Integer, User> userThreadModeFilter;
	
	
    public MainHandler(AtomicInteger requestIds, 
    		UserThreadModeFilter<Integer, ChannelId, Visitor, Integer, User> userThreadModeFilter) {
    	
    	this.requestIds = requestIds;
		this.userThreadModeFilter = userThreadModeFilter;
	}

	@Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        
		//客户端连接到服务
		
    	Channel channel = ctx.channel();
    	ChannelId channelId = channel.id();
    	String address = channel.remoteAddress().toString();
    	String ip = address.substring(1, address.indexOf(':'));
    	
    	//将连接信息（Link）放入到linkMap
    	ControlCenter.visitorLink(channelId, channel, ip);
        
        super.channelActive(ctx);
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	
    	//客户端断开连接
    	
    	ChannelId channelId = ctx.channel().id();
    	Integer userId = ControlCenter.disconnect(channelId);
    	
    	//如果是用户则调用utm的用户断线事件
    	
    	if( userId != null ) {
    		
    		User user = ControlCenter.getUser( userId );
    		if( user != null ) {
    			
    			LoggerCenter.userRquestLogger.log(user.userName,
    					LoggerCenter.getBeforeInfo().append("cmd:netty user disconnect\n") );
    			
    			userThreadModeFilter.disconnect(requestIds.getAndIncrement(), userId);
    			
    		}
    		
    	}
    	
    }
    
    /**
     * cmd的编码和解码器
     */
    private final NormalIntegerCmdCoder cmdCoder = new NormalIntegerCmdCoder();
    /**
     * CommonObjB的解码器
     */
    private final CommonContentDecoder decoder = new CommonContentDecoder();
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
    	
    	//接收到客户端消息

    	ChannelId channelId = ctx.channel().id();
    	Link link = ControlCenter.getLink(channelId);
    	Integer userId = link.getUserId();
    	
    	Integer cmd = cmdCoder.decoder(buf);
    	CommonObjB param = decoder.decoder(buf);
    	
    	if( userId == null ) {
    		
        	userThreadModeFilter.handleVisitorRequest(requestIds.getAndIncrement(), cmd, channelId, link.visitor, param);
        	
    	} else {
    		
    		if( cmd == Cmd.Logout.CMD )
    			link.logout();
    		
    		userThreadModeFilter.handleUserRequest(requestIds.getAndIncrement(), cmd, userId, param);
    	}
    	
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
    	
    	//异常处理
    	
    	if( cause instanceof IOException ) {
    		
    		IOException e = (IOException)cause;
    		
    		//如果是客户端断线事件，则捕获
    		if( e.getMessage().equals("远程主机强迫关闭了一个现有的连接。") ) {
    			
    			Link link = ControlCenter.getLink( ctx.channel().id() );
    			Integer userId = link == null ? null : link.getUserId();
    			
    			System.out.println( userId + " Connection Lost");
    			
    		} else {
    			
    			//不是客户端断线则抛出继续抛出异常
    			super.exceptionCaught(ctx, cause);;
    		}
    		
    	} else {
    		
    		//不是客户端断线则抛出继续抛出异常
    		super.exceptionCaught(ctx, cause);;
    	}
    	
    }
    
}

