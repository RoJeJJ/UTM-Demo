package com.dc.sfs.handler.server;

import com.dc.qtm.handle.IRequestHandler;
import com.dc.sfs.center.BusinessCenter;
import com.dc.sfs.center.ControlCenter;
import com.dc.sfs.center.LoggerCenter;
import com.dc.sfs.cmd.Cmd;
import com.dc.sfs.obj.User;
import com.dc.sfs.obj.Visitor;
import com.dc.utm.center.UserCenter;
import com.dc.utm.event.EventManager;
import com.dc.utm.filter.user.IUserRequestFilter;
import com.dc.utm.resource.user.UserResourceCenter;
import com.dc.utm.resource.user.queue.UserQueueResource;
import com.dc.utm.user.flag.UserFlagBusiness;
import com.dc.utm.user.logout.UserLogoutCheckBusiness;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

/**
 * 
 * 用户登录检查
 * 
 * @author Daemon
 *
 */
public class OnUserLoginCheckHandler 
	extends com.dc.utm.handler.login.OnUserLoginCheckHandler<String, Integer, Visitor, Integer, User, ISFSEvent> {
	
	private static final String NULL_USERNAME = "*&";

	@SuppressWarnings("rawtypes")
	public OnUserLoginCheckHandler(
			UserCenter<Integer, User> userCenter,
			EventManager eventManager,
			UserQueueResource<Visitor, Integer, User> userQueueResource,
			IUserRequestFilter<String, Integer, Visitor, Integer, User> userRequestFilter,
			UserFlagBusiness<Visitor, Integer, User> userFlagBusiness,
			UserLogoutCheckBusiness<String, Integer, Visitor, Integer, User> userLogoutCheckBusiness,
			IRequestHandler onUserLoginHandler, UserResourceCenter<Visitor, Integer, User> userResourceCenter) {
		
		super(userCenter, eventManager, userQueueResource, userRequestFilter, userFlagBusiness, 
				userLogoutCheckBusiness, onUserLoginHandler, userResourceCenter);
	}

	@Override
	public void queueFull(int requestId, Visitor visitor, ISFSEvent event) {
		
		//队列满，给前端发送提示
		
		String userName = NULL_USERNAME;
		try {
			
			ISFSObject objQueueFull = new SFSObject();
			objQueueFull.putInt(Cmd.Login._RESULT_CODE, Cmd.ErroCode.QUEUE_FULL);
			
			userName = (String) event.getParameter(SFSEventParam.LOGIN_NAME);
			userName = userName.toLowerCase();
			
			ControlCenter.sendToVisitor(Cmd.Login.CMD, objQueueFull, visitor.session, userName);
			
		} catch(Exception e) {
			
			e.printStackTrace();
			LoggerCenter.userRquestLogger.log(userName, e);
		}
		
	}
	
	@Override
	public User loginCheck(int requestId, Visitor visitor, ISFSEvent event) {
		
		//登录检查
		
		String userName = NULL_USERNAME;
		try {
			
			userName = (String) event.getParameter(SFSEventParam.LOGIN_NAME);
			userName = userName.toLowerCase();
			String cryptedPass = (String) event.getParameter(SFSEventParam.LOGIN_PASSWORD);
			
			ISFSObject loginData = (ISFSObject)event.getParameter(SFSEventParam.LOGIN_IN_DATA);
			String loginDataJson = loginData == null ? "null" : loginData.toJson();
			
			//记录日志
			LoggerCenter.userRquestLogger.log(userName, LoggerCenter.getBeforeInfo()
					.append("cmd:login\n").append("              params:").append(loginDataJson).append("\n") );
			
			User user = BusinessCenter.userProcess.userLoginCheck(userName, visitor.session.getHashId(), cryptedPass, visitor.ip, visitor.session);
			if( user != null ) {
				
				return user;
				
			} else {
				
				//userProcess返回null，则给前端发送 用户名或者密码错误（只是示例代码，根据实际业务而定，可能有：禁止登录等等的情况导致 登录检查失败）
				ISFSObject passWordErro = new SFSObject();
				passWordErro.putInt(Cmd.Login._RESULT_CODE, Cmd.ErroCode.PASSWORD_ERROR);
				ControlCenter.sendToVisitor(Cmd.Login.CMD, passWordErro, visitor.session, userName);
				
				return null;
			}
			
		} catch(Exception e) {
			
			e.printStackTrace();
			LoggerCenter.userRquestLogger.log(userName, e);
			
			//发生异常，通知前端处理上遇到异常
			ISFSObject passWordErro = new SFSObject();
			passWordErro.putInt(Cmd.Login._RESULT_CODE, Cmd.ErroCode.UNKNOW);
			ControlCenter.sendToVisitor(Cmd.Login.CMD, passWordErro, visitor.session, userName);
			
			return null;
		}
		
	}
	
	@Override
	public void logoutLocalUserWhenUserReLogin(int requestId, Visitor visitor,
			User newUser, ISFSEvent param) {
		
		//用户已经在本sfs登录，断开旧的用户

		User oldUser = userCenter.getUser(newUser.getUserKey());
		if (oldUser != null && oldUser.sfsUser != null) {
			
			//给前端发送logout cmd，resultCode=重复登录，然后再退出该用户
			ISFSObject sfsObj = new SFSObject();
			sfsObj.putInt(Cmd.Logout._RESULT_CODE, Cmd.Logout.USER_RELOGIN);
			ControlCenter.sendToUser(Cmd.Logout.CMD, sfsObj, oldUser);
			ControlCenter.logoutUser(oldUser);
		}
		
	}

	@Override
	public void logoutRemoteUserWhenUserReLogin(int requestId, Visitor visitor,
			User newUser, ISFSEvent param) {
		// TODO 发送消息给远程的机器, 要求它退出该用户
	}
	
}
