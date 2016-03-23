package com.dc.netty.utm.business.user;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;

import com.dc.netty.utm.center.ServiceCenter;
import com.dc.netty.utm.obj.User;

/**
 * 
 * 用户业务处理
 * 
 * @author Daemon
 *
 */
public class UserProcess {
	
	/**
	 * 用户登录检查，成功则返回User对象，失败则返回null
	 * 
	 * @param userName 用户名
	 * @param sessionId sfs的sessionId
	 * @param password 密码
	 * @param ip ip
	 * @param channelId 用户连接的channelId
	 * @param channel 用户连接的channel
	 * @return 成功则返回User对象，失败则返回null
	 */
	public User userLoginCheck(String userName, String password, String ip, ChannelId channelId, Channel channel) {
		
		Integer userId = ServiceCenter.userService.userLoginCheck(userName, password, ip);
		if( userId != null ) {
			
			return new User(channelId, channel, ip, userId, userName);
			
		} else {
			
			return null;
		}
		
	}
	
	/**
	 * 用户登录到服务
	 * 
	 * @param user 用户对象
	 */
	public void userLoginToServer(User user) {
		
		ServiceCenter.userService.userLoginToServer(user.userId);
		
		// TODO other business
		
	}
	
	/**
	 * 用户退出服务
	 * 
	 * @param user 用户对象
	 * @param isDisconect 是否是断线导致的退出
	 */
	public void userLogout(User user, boolean isDisconect) {
		
		ServiceCenter.userService.userLogout(user.userId, isDisconect);
		
		// TODO other business
	}
	
}

