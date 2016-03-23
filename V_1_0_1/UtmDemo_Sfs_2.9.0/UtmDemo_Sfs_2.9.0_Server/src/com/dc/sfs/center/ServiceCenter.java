package com.dc.sfs.center;

import com.dc.sfs.service.user.UserService;

/**
 * 
 * 服务中心，保存着各种服务的实现(单例)
 * 
 * @author Daemon
 *
 */
public class ServiceCenter {

	/**
	 * 用户服务对象
	 */
	public final static UserService userService = new UserService();
}
