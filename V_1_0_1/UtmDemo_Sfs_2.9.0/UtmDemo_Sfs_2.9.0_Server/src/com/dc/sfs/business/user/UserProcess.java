package com.dc.sfs.business.user;

import com.dc.sfs.center.ServiceCenter;
import com.dc.sfs.obj.User;
import com.smartfoxserver.bitswarm.sessions.ISession;

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
	 * @param session 用户sfs的session
	 * @return 成功则返回User对象，失败则返回null
	 */
	public User userLoginCheck(String userName, String sessionId, String password, String ip, ISession session) {
		
		Integer userId = ServiceCenter.userService.userLoginCheck(userName, sessionId, password);
		if( userId != null ) {
			
			return new User( session, ip, userId, userName );
			
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

