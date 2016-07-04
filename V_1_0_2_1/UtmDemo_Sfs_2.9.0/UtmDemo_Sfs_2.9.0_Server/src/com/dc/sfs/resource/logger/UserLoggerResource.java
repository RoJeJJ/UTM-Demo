package com.dc.sfs.resource.logger;

import java.util.Set;

import com.dc.sfs.center.LoggerCenter;
import com.dc.sfs.obj.User;
import com.dc.sfs.obj.Visitor;
import com.dc.utm.resource.user.IUserResource;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;

/**
 * 
 * 用户日志资源管理
 * （对于日志资源有一个比较特殊的情况就是：同个用户同时在多个地方请求登录，如果有一个密码错误等则会关闭资源，
 * 但是其他正在处理的登录的资源可能因此被关闭了，不过(用户在调用DynamicLoggerController的方法时，如果没有则会创建)，所以不会有问题，
 * 只是日志被关闭了又打开而已）
 * 
 * 当然也可以使用DynamicLoggerController的长时间未使用则关闭的功能（意味着日志资源不会马上释放），这样这个类就不需要了，
 * 只需要更改maxIdleTime为要设置的时间，并打开LogCenter类中注释 "userRquestLogger.addCloseAbleChecker();" 即可
 * 
 * @author Daemon
 *
 */
public class UserLoggerResource implements IUserResource<Visitor, Integer, User> {

	
	
	@Override
	public void beforeUserLoginCheck(Visitor visitor, Object param) {
		
		//用户在调用DynamicLoggerController的方法时，如果没有则会创建，所以不在此处声明
		//create
	}

	@Override
	public void userLoginCheckFail(Visitor visitor, Object param) {
		
		//回收日志资源 
		try {
			
			ISFSEvent event =  (ISFSEvent)param;
			String userName = (String) event.getParameter(SFSEventParam.LOGIN_NAME);
			userName = userName.toLowerCase();
			
			LoggerCenter.userRquestLogger.close(userName);
			
		} catch(Exception e) {
			
			e.printStackTrace();
		}
		
	}

	@Override
	public void setLoginFlagSuccess(Integer userKey, User user) {
	}

	@Override
	public void waitUserLogoutTimeOut(Integer userKey, User user) {
		
		//回收日志资源 
		LoggerCenter.userRquestLogger.close(user.userName);
	}

	@Override
	public void beforeLoginLinkCheck(Integer userKey, User user) {
	}

	@Override
	public void failInLoginLinkCheck(Integer userKey, User user) {
		
		//回收日志资源 
		LoggerCenter.userRquestLogger.close(user.userName);
	}

	@Override
	public void userIn(Integer userKey, User user) {
	}

	@Override
	public void userOut(Integer userKey, User user) {
		
		//回收日志资源 
		LoggerCenter.userRquestLogger.close(user.userName);
	}

	@Override
	public int getActiveCount() {
		
		return LoggerCenter.userRquestLogger.getActiveCount();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Set getAciveUserInfo() {
		
		return LoggerCenter.userRquestLogger.getActiveTarget();
	}

	@Override
	public String getName() {
		return "UserLogger";
	}
}
