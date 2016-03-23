package com.dc.netty.utm.handler.utm.cmd;

import com.dc.netty.coder.commonobj.CommonObjB;
import com.dc.netty.utm.center.BusinessCenter;
import com.dc.netty.utm.center.ControlCenter;
import com.dc.netty.utm.center.LoggerCenter;
import com.dc.netty.utm.cmd.Cmd;
import com.dc.netty.utm.obj.User;
import com.dc.netty.utm.obj.Visitor;
import com.dc.utm.center.UserCenter;
import com.dc.utm.event.EventManager;
import com.dc.utm.resource.user.UserResourceCenter;
import com.dc.utm.user.flag.UserFlagBusiness;

/**
 * 
 * 用户退出登录接口
 * LogoutHandler 和 OnUserDisconectHandler只有一个会被触发 （LogoutHandler后用户将认为是游客，其断线将不会触发OnUserDisconectHandler）
 * 
 * @author Daemon
 *
 */
public class LogoutHandler extends com.dc.utm.handler.logout.OnUserLogoutHandler<Visitor, Integer, User, CommonObjB> {

	@SuppressWarnings("rawtypes")
	public LogoutHandler(UserCenter<Integer, User> userCenter,
			EventManager eventManager,
			UserFlagBusiness<Visitor, Integer, User> userFlagBusiness,
			UserResourceCenter<Visitor, Integer, User> userResourceCenter) {
		super(userCenter, eventManager, userFlagBusiness, userResourceCenter);
	}

	@Override
	public void before(int requestId, User user, CommonObjB param) {
		
		//记录日志
		LoggerCenter.userRquestLogger.log(user.userName, 
				LoggerCenter.getBeforeInfo().append("cmd:LogoutHandler\n") );
		
	}
	
	@Override
	public void userLogout(int requestId, User user, CommonObjB param) {
		
		try {
			//处理退出业务
			BusinessCenter.userProcess.userLogout(user, false);
			
		} catch(Exception e) {
			
			e.printStackTrace();
			LoggerCenter.userRquestLogger.log(user.userName, e);
		}
		
		Boolean isReLogin = param.getBool(Cmd.Logout.__ISRELOGIN);
		if( isReLogin == null || ! isReLogin ) {
			
			//不是重复登录，则告诉前端退出成功
			//（重复登录已经在OnUserLoginCheckHandler.logoutLocalUserWhenUserReLogin方法中通知前台了）
			CommonObjB returnObject = new CommonObjB();
			returnObject.putInt(Cmd.Logout._RESULT_CODE, Cmd.SuccessCode.SUCCESS);
			ControlCenter.sendToUser(Cmd.Logout.CMD, returnObject, user);
			
		}
		
		//记录日志
		LoggerCenter.userRquestLogger.log(user.userName, 
				LoggerCenter.getBeforeInfo().append("cmd:LogoutHandler over\n") );
		
	}
	
}
