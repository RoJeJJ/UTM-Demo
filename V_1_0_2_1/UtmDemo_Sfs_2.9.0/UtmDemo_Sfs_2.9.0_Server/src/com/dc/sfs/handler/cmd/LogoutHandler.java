package com.dc.sfs.handler.cmd;

import com.dc.sfs.center.ControlCenter;
import com.dc.sfs.center.LoggerCenter;
import com.dc.sfs.center.BusinessCenter;
import com.dc.sfs.cmd.Cmd;
import com.dc.sfs.obj.User;
import com.dc.sfs.obj.Visitor;
import com.dc.utm.center.UserCenter;
import com.dc.utm.event.EventManager;
import com.dc.utm.resource.user.UserResourceCenter;
import com.dc.utm.user.flag.UserFlagBusiness;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

/**
 * 
 * 用户退出登录接口
 * LogoutHandler 和 OnUserDisconectHandler只有一个会被触发 （LogoutHandler后用户将认为是游客，其断线将不会触发OnUserDisconectHandler）
 * 
 * @author Daemon
 *
 */
public class LogoutHandler extends com.dc.utm.handler.logout.OnUserLogoutHandler<Visitor, Integer, User, Object> {

	@SuppressWarnings("rawtypes")
	public LogoutHandler(UserCenter<Integer, User> userCenter,
			EventManager eventManager,
			UserFlagBusiness<Visitor, Integer, User> userFlagBusiness,
			UserResourceCenter<Visitor, Integer, User> userResourceCenter) {
		super(userCenter, eventManager, userFlagBusiness, userResourceCenter);
	}

	@Override
	public void before(int requestId, User user, Object param) {
		
		//记录日志
		LoggerCenter.userRquestLogger.log(user.userName, 
				LoggerCenter.getBeforeInfo().append("cmd:LogoutHandler\n") );
		
	}
	
	@Override
	public void userLogout(int requestId, User user, Object param) {
		
		try {
			//处理退出业务
			BusinessCenter.userProcess.userLogout(user, false);
			
		} catch(Exception e) {
			
			e.printStackTrace();
			LoggerCenter.userRquestLogger.log(user.userName, e);
		}
		
		ISFSObject returnObject = new SFSObject();
		returnObject.putInt(Cmd.Logout._RESULT_CODE, Cmd.SuccessCode.SUCCESS);
		ControlCenter.sendToUser(Cmd.Logout.CMD, returnObject, user);
		
		//记录日志
		LoggerCenter.userRquestLogger.log(user.userName, 
				LoggerCenter.getBeforeInfo().append("cmd:LogoutHandler over\n") );
		
	}
	
}
