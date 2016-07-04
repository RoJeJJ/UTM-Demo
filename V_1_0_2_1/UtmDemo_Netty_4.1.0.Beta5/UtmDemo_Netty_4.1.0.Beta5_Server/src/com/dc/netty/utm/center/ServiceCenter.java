package com.dc.netty.utm.center;

import com.dc.netty.utm.service.user.UserService;

/**
 * 
 * 服务中心，存放各个服务
 * 
 * @author Daemon
 *
 */
public class ServiceCenter {

	/**
	 * 用户服务
	 */
	public final static UserService userService = new UserService();
}
