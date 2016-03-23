package com.dc.netty.utm.obj;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;

import com.dc.utm.entity.BaseUser;

/**
 * 
 * 用户
 * 
 * @author Daemon
 *
 */
public class User extends BaseUser<Integer> {

	/**
	 * 用户对应的netty channel 的 id
	 */
	public final ChannelId channelId;
	/**
	 * 用户对应的netty channel
	 */
	public final Channel channel;
	/**
	 * 用户ip
	 */
	public final String ip;
	
	/**
	 * 用户id
	 */
	public final int userId;
	/**
	 * 用户名
	 */
	public final String userName;
	
	public User(ChannelId channelId, Channel channel, String ip, int userId, String userName) {
		
		this.channelId = channelId;
		this.channel = channel;
		this.ip = ip;
		this.userId = userId;
		this.userName = userName;
	}

	@Override
	public String toString() {
		return "User [channelId=" + channelId + ", channel=" + channel
				+ ", ip=" + ip + ", userId=" + userId + ", userName="
				+ userName + "]";
	}

	/**
	 * 获得用户ip
	 * 
	 * @return 用户ip
	 */
	public String getIP() {
		return ip;
	}

	@Override
	public Integer getUserKey() {
		return userId;
	}

	@Override
	public void killConnect() {
		channel.disconnect();
	}
	
}
