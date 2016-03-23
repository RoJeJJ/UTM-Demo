package com.dc.netty.utm.obj;

import java.util.concurrent.atomic.AtomicInteger;

import io.netty.channel.Channel;
import io.netty.channel.ChannelId;

/**
 * 
 * netty连接信息的封装类(记录着连接是 游客 还是 用户)
 * 
 * 游客连接后就创建了该对象，
 * 在登录的时候检查这个连接是否已经断开（如果断开连接则会设置标志位），如果断开了就登录失败，没有断开则登录成功
 * 
 * （
 * disconnect方法由netty触发的事件调用，login由utm的线程调用，为保证其并发安全性，
 * 该对象确保：用户登录前断开则 disconnect方法返回的userId=null，用户登录后断开则disconnect方法返回的用户的userId
 * ）
 * 
 * _userId的变化过程：
 *  连接了       -->   登录        -->   退出        -->   断线
 *   -1         userId        -1          -7
 * 
 * 
 * @author Daemon
 *
 */
public class Link {

	/**
	 * 游客对象
	 */
	public final Visitor visitor;
	/**
	 * 用户id
	 */
	private AtomicInteger _userId = new AtomicInteger(-1);
	/**
	 * 用户对象
	 */
	public User user;
	
	public Link(ChannelId channelId, Channel channel, String ip) {
		
		this.visitor = new Visitor(channelId, channel, ip);
	}
	
	/**
	 * 这个连接是否是用户
	 * 
	 * @return 这个连接是否是用户
	 */
	public boolean isUser() {
		
		return _userId.get() >= 0;
	}
	
	/**
	 * 获得连接对应的用户Id，不是用户则返回null
	 * 
	 * @return 获得连接对应的用户Id，不是用户则返回null
	 */
	public Integer getUserId() {
		
		Integer userId = _userId.get();
		if( userId >= 0 ) {
			
			return userId;
			
		} else {
			
			return null;
		}
		
	}
	
	/**
	 * 该连接对应的用户登录，检查用户是否已经断开，如果断开则返回false
	 * 
	 * @param userId 用户Id
	 * @param user 用户
	 * @return 是否仍然连接
	 */
	public boolean login(int userId, User user) {
		
		if( this._userId.compareAndSet(-1, userId) ) {
			
			this.user = user;
			
			return true;
			
		} else {
			
			return false;
		}
		
	}
	
	/**
	 * @return 获得连接对应的用户Id，不是用户则返回null
	 */
	public Integer logout() {
		
		Integer userId = _userId.getAndSet(-1);
		if( userId >= 0 ) {
			
			return userId;
			
		} else {
			
			return null;
		}
	}
	
	/**
	 * @return 获得连接对应的用户Id，不是用户则返回null
	 */
	public Integer disconnect() {
		
		Integer userId = _userId.getAndSet(-7);
		if( userId >= 0 ) {
			
			return userId;
			
		} else {
			
			return null;
		}
	}
}
