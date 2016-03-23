package com.dc.sfs.obj;

import java.util.HashMap;

import com.dc.sfs.center.ControlCenter;
import com.dc.utm.entity.BaseUser;
import com.smartfoxserver.bitswarm.sessions.ISession;

/**
 * 
 * 用户
 * 
 * @author Daemon
 *
 */
public class User extends BaseUser<Integer> {
	
	/**
	 * 用户Id
	 */
	public final int userId;
	/**
	 * 用户名
	 */
	public final String userName;
	
	/**
	 * 用户session
	 */
	public final ISession session;
	/**
	 * 用户对于的sfs User对象
	 */
	public com.smartfoxserver.v2.entities.User sfsUser;
	/**
	 * 用户ip
	 */
	public final String ip;
	
	/**
	 * 用户参数
	 */
	private final HashMap<Integer, Object> paramMap = new HashMap<Integer, Object>(2);
	
	/**
	 * @param session 用户session
	 * @param ip 用户ip
	 * @param userId 用户Id
	 * @param userName 用户名
	 */
	public User(ISession session, String ip, int userId, String userName) {
		
		this.session = session;
		this.ip = ip;
		this.userId = userId;
		this.userName = userName;
	}
	
	/**
	 * 获得用户参数（容器为普通hashMap，注意并发问题）
	 * 
	 * @param key key
	 * @return 该key对应的参数
	 */
	@SuppressWarnings("unchecked")
	public <T> T getParam(Integer key) {
		
		return (T)paramMap.get(key);
	}
	
	/**
	 * 移除用户参数（容器为普通hashMap，注意并发问题）
	 * 
	 * @param key key
	 * @return 被移除的参数
	 */
	@SuppressWarnings("unchecked")
	public <T> T removeParam(Integer key) {
		
		return (T)paramMap.remove(key);
	}
	
	/**
	 * 设置用户参数（容器为普通hashMap，注意并发问题）
	 * 
	 * @param key key
	 * @param value value
	 */
	public void setParam(Integer key, Object value) {
		
		paramMap.put(key, value);
	}
	
	/**
	 * 获得用户ip
	 * 
	 * @return 用户ip
	 */
	public String getIP() {
		return ip;
	}
	
	/**
	 * 设置用户对应的sfs User对象
	 * 
	 * @param sfsUser
	 */
	public void setSfsUser(com.smartfoxserver.v2.entities.User sfsUser) {
		this.sfsUser = sfsUser;
	}

	@Override
	public Integer getUserKey() {
		return userId;
	}
	
	@Override
	public void killConnect(){
		
		ControlCenter.disconnect(this);
	}

}
