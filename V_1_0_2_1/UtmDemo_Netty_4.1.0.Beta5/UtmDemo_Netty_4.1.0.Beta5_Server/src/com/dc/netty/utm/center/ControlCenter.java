package com.dc.netty.utm.center;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.dc.netty.coder.cmd.NormalIntegerCmdCoder;
import com.dc.netty.coder.commonobj.CommonContentEncoder;
import com.dc.netty.coder.commonobj.CommonObjB;
import com.dc.netty.utm.cmd.Cmd;
import com.dc.netty.utm.obj.Link;
import com.dc.netty.utm.obj.User;
import com.dc.netty.utm.obj.Visitor;
import com.dc.utm.center.UserCenter;
import com.dc.utm.filter.UserThreadModeFilter;

/**
 * 
 * 控制中心，对用户登录，退出，断开，收发消息进行封装
 * 
 * （
 * disconnect方法由netty触发的事件调用，logincheck由utm的线程调用，为保证其并发安全性，
 * 该对象确保：用户登录前断开则 disconnect方法返回的userId=null，用户登录后断开则disconnect方法返回的用户的userId
 * ）
 * 
 * @author Daemon
 *
 */
public class ControlCenter {
	
	/**
	 * CommonObjB的编码器
	 */
	private static final CommonContentEncoder encoder = new CommonContentEncoder();
	/**
	 * cmd的编码和解码器
	 */
	private static final NormalIntegerCmdCoder cmdCoder = new NormalIntegerCmdCoder();
	
	
	/**
	 * 已连接的客户端的ChannelId对应Link
	 */
	private static final ConcurrentHashMap<ChannelId, Link> linkMap = new ConcurrentHashMap<ChannelId, Link>();
	
	/**
	 * 用户中心
	 */
	private static UserCenter<Integer, User> userCenter;
	/**
	 * utm过滤器
	 */
	private static UserThreadModeFilter<Integer, ChannelId, Visitor, Integer, User> userThreadModeFilter;
	/**
	 * 请求计数器
	 */
	private static AtomicInteger requestIds;
	
	
	/**
	 * 设置 用户中心
	 * 
	 * @param userCenter 用户中心
	 */
	public static void setUserCenter(
			UserCenter<Integer, User> userCenter) {
		ControlCenter.userCenter = userCenter;
	}
	/**
	 * 设置utm过滤器
	 * 
	 * @param userThreadModeFilter utm过滤器
	 */
	public static void setUserThreadModeFilter(
			UserThreadModeFilter<Integer, ChannelId, Visitor, Integer, User> userThreadModeFilter) {
		ControlCenter.userThreadModeFilter = userThreadModeFilter;
	}
	
	/**
	 * 设置请求计数器
	 * 
	 * @param requestIds 请求计数器
	 */
	public static void setRequestIds(AtomicInteger requestIds) {
		ControlCenter.requestIds = requestIds;
	}



	/**
	 * 客户端连接到服务（将连接信息（Link）放入到linkMap）
	 * 
	 * @param channelId 连接对应的channel的Id
	 * @param channel 连接对应的channel
	 * @param ip 连接的ip
	 */
	public static final void visitorLink( ChannelId channelId, Channel channel, String ip ) {
		
		linkMap.put( channelId, new Link(channelId, channel, ip) );
	}
	
	/**
	 * 用户登录检查，如果用户还没断开则返回true，否则返回false
	 * 
	 * @param channelId 连接对应的channel的Id
	 * @param userId 用户id
	 * @param user 用户对象
	 * @return 用户还没断开则返回true，否则返回false
	 */
	public static final boolean logincheck(ChannelId channelId, int userId, User user) {
		
		Link link = linkMap.get(channelId);
		return link == null ? false : link.login(userId, user);
	}
	
	/**
	 * 退出登录
	 * 
	 * 将用户退出登录（触发Logout cmd）
	 * 
	 * @param user 用户对象
	 * @param isReLogin 是否是用户重复登录导致的退出
	 */
	public static final void logout(User user, boolean isReLogin) {
		
		Link link = linkMap.get(user.channelId);
		Integer userId = link == null ? null : link.logout();
		
		//fire logout event
		if( userId != null ) {
			
			CommonObjB param = new CommonObjB();
			if( isReLogin )
				param.putBool(Cmd.Logout.__ISRELOGIN, true);
			
			userThreadModeFilter.handleUserRequest(requestIds.getAndIncrement(), Cmd.Logout.CMD, user.userId, param);
			
		}
	}
	
	/**
	 * 连接断线，如果是用户则返回用户Id，否则返回null
	 * （将连接信息（Link）从linkMap中移除）
	 * 
	 * @param channelId 连接对应的channel的Id
	 * @return 如果是用户则返回用户Id，否则返回null
	 */
	public static final Integer disconnect(ChannelId channelId) {
		
		Link link = linkMap.remove(channelId);
    	return link.disconnect();
	}
	
	/**
	 * 获得连接信息
	 * 
	 * @param channelId 连接对应的channel的Id
	 * @return 连接信息
	 */
	public static final Link getLink(ChannelId channelId) {
		
    	return linkMap.get(channelId);
	}
	

	

	/**
	 * 给用户发送消息
	 * 
	 * @param cmd cmd 
	 * @param commonObjB 要发送的信息
	 * @param user 用户
	 */
	public static void sendToUser(int cmd, CommonObjB commonObjB, User user) {
		
		Channel channel = user.channel;
		
		ByteBuf byteBuf = channel.alloc().buffer();
		
		cmdCoder.encoder(cmd, byteBuf);
		encoder.encoder(commonObjB, byteBuf);
		
		channel.writeAndFlush( byteBuf );
		
	}

	/**
	 * 给游客发送信息
	 * 
	 * @param cmd cmd
	 * @param commonObjB 要发送的信息
	 * @param visitor 游客
	 * @param userName 游客的名字（记录日志的名字）
	 */
	public static void sendToVisitor(int cmd, CommonObjB commonObjB,
			Visitor visitor, String userName) {
		
		Channel channel = visitor.channel;
		
		ByteBuf byteBuf = channel.alloc().buffer();
		
		cmdCoder.encoder(cmd, byteBuf);
		encoder.encoder(commonObjB, byteBuf);
		
		channel.writeAndFlush( byteBuf );
		
	}

	public static User getUser(Integer userId) {
		
		return userCenter.getUser(userId);
	}
	
}
