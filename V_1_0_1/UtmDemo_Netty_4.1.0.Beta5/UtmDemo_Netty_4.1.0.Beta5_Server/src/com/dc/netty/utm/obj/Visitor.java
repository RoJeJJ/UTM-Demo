package com.dc.netty.utm.obj;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;

/**
 * 
 * 游客
 * 
 * @author Daemon
 *
 */
public class Visitor {

	/**
	 * 游客对应的netty channel 的 id
	 */
	public final ChannelId channelId;
	/**
	 * 游客对应的netty channel
	 */
	public final Channel channel;
	/**
	 * 游客ip
	 */
	public final String ip;
	
	public Visitor(ChannelId channelId, Channel channel, String ip) {
		
		this.channelId = channelId;
		this.channel = channel;
		this.ip = ip;
		
	}

	/**
	 * 获得游客的ip
	 * 
	 * @return 游客的ip
	 */
	public String getIP() {
		return ip;
	}

	@Override
	public String toString() {
		return "Visitor [channelId=" + channelId + ", channel=" + channel
				+ ", ip=" + ip + "]";
	}
	
	
}
