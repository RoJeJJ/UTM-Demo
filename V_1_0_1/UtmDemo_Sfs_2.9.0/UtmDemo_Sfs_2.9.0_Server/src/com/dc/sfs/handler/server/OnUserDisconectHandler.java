package com.dc.sfs.handler.server;

import com.dc.sfs.center.LoggerCenter;
import com.dc.sfs.center.BusinessCenter;
import com.dc.sfs.obj.User;
import com.dc.sfs.obj.Visitor;
import com.dc.utm.center.UserCenter;
import com.dc.utm.event.EventManager;
import com.dc.utm.resource.user.UserResourceCenter;
import com.dc.utm.user.flag.UserFlagBusiness;

/**
 * 
 * 用户断线接口
 * LogoutHandler 和 OnUserDisconectHandler只有一个会被触发 （LogoutHandler后用户将认为是游客，其断线将不会触发OnUserDisconectHandler）
 * 
 * @author Daemon
 *
 */
public class OnUserDisconectHandler extends com.dc.utm.handler.logout.OnUserDisconectHandler<Visitor, Integer, User, Object> {
	
	
	@SuppressWarnings("rawtypes")
	public OnUserDisconectHandler(UserCenter<Integer, User> userCenter,
			EventManager eventManager,
			UserFlagBusiness<Visitor, Integer, User> userFlagBusiness,
			UserResourceCenter<Visitor, Integer, User> userResourceCenter) {
		
		super(userCenter, eventManager, userFlagBusiness, userResourceCenter);
	}
	
	@Override
	public void before(int requestId, User user, Object param) {
		
		//记录日志
		LoggerCenter.userRquestLogger.log(user.userName, 
				LoggerCenter.getBeforeInfo().append("cmd:OnUserDisconectHandler\n") );
		
	}
	
	@Override
	public void userDisconect(int requestId, User user, Object param) {
		
		try {
			
			//处理断线业务
			BusinessCenter.userProcess.userLogout(user, true);
			
		} catch(Exception e) {
			
			e.printStackTrace();
			LoggerCenter.userRquestLogger.log(user.userName, e);
		}
		
		//记录日志
		LoggerCenter.userRquestLogger.log(user.userName, 
				LoggerCenter.getBeforeInfo().append("cmd:OnUserDisconectHandler over\n") );
	}

}
