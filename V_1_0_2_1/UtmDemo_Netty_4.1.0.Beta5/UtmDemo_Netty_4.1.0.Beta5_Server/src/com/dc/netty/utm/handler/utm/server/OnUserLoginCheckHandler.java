package com.dc.netty.utm.handler.utm.server;

import io.netty.channel.ChannelId;

import com.dc.netty.coder.commonobj.CommonObjB;
import com.dc.netty.utm.center.BusinessCenter;
import com.dc.netty.utm.center.ControlCenter;
import com.dc.netty.utm.center.LoggerCenter;
import com.dc.netty.utm.cmd.Cmd;
import com.dc.netty.utm.obj.User;
import com.dc.netty.utm.obj.Visitor;
import com.dc.qtm.handle.IRequestHandler;
import com.dc.utm.center.UserCenter;
import com.dc.utm.event.EventManager;
import com.dc.utm.filter.user.IUserRequestFilter;
import com.dc.utm.resource.user.UserResourceCenter;
import com.dc.utm.resource.user.queue.UserQueueResource;
import com.dc.utm.user.flag.UserFlagBusiness;
import com.dc.utm.user.logout.UserLogoutCheckBusiness;

public class OnUserLoginCheckHandler 
	extends com.dc.utm.handler.login.OnUserLoginCheckHandler<Integer, ChannelId, Visitor, Integer, User, CommonObjB> {
	
	
	@SuppressWarnings("rawtypes")
	public OnUserLoginCheckHandler(
			UserCenter<Integer, User> userCenter,
			EventManager eventManager,
			UserQueueResource<Visitor, Integer, User> userQueueResource,
			IUserRequestFilter<Integer, ChannelId, Visitor, Integer, User> userRequestFilter,
			UserFlagBusiness<Visitor, Integer, User> userFlagBusiness,
			UserLogoutCheckBusiness<Integer, ChannelId, Visitor, Integer, User> userLogoutCheckBusiness,
			IRequestHandler onUserLoginHandler,
			UserResourceCenter<Visitor, Integer, User> userResourceCenter) {
		
		super(userCenter, eventManager, userQueueResource, userRequestFilter,
				userFlagBusiness, userLogoutCheckBusiness, onUserLoginHandler,
				userResourceCenter);
	}

	private static final String NULL_USERNAME = "*&";

	
	
	@Override
	public void queueFull(int requestId, Visitor visitor, CommonObjB param) {
		
		//队列满，给前端发送提示
		
		String userName = NULL_USERNAME;
		try {
			
			CommonObjB objQueueFull = new CommonObjB();
			objQueueFull.putInt(Cmd.Login._RESULT_CODE, Cmd.ErroCode.QUEUE_FULL);
			
			userName = param.getUtfString(Cmd.Login.USER_NAME);
			userName = userName.toLowerCase();
			
			ControlCenter.sendToVisitor(Cmd.Login.CMD, objQueueFull, visitor, userName);
			
		} catch(Exception e) {
			
			e.printStackTrace();
			LoggerCenter.userRquestLogger.log(userName, e);
		}
		
	}
	
	@Override
	public User loginCheck(int requestId, Visitor visitor, CommonObjB param) {
		
		//登录检查
		
		String userName = NULL_USERNAME;
		try {
			
			userName = param.getUtfString(Cmd.Login.USER_NAME);
			userName = userName.toLowerCase();
			String password = param.getUtfString(Cmd.Login.PASSWORD);
			
			//记录日志
			LoggerCenter.userRquestLogger.log(userName, LoggerCenter.getBeforeInfo()
					.append("cmd:login\n").append("              params:").append(param).append("\n") );
			
			User user = BusinessCenter.userProcess.userLoginCheck(userName, password, visitor.ip, visitor.channelId, visitor.channel);
			if( user != null ) {
				
				return user;
				
			} else {
				
				//userProcess返回null，则给前端发送 用户名或者密码错误（只是示例代码，根据实际业务而定，可能有：禁止登录等等的情况导致 登录检查失败）
				CommonObjB passWordErro = new CommonObjB();
				passWordErro.putInt(Cmd.Login._RESULT_CODE, Cmd.ErroCode.PASSWORD_ERROR);
				ControlCenter.sendToVisitor(Cmd.Login.CMD, passWordErro, visitor, userName);
				
				return null;
			}
			
		} catch(Exception e) {
			
			e.printStackTrace();
			LoggerCenter.userRquestLogger.log(userName, e);
			
			//发生异常，通知前端处理上遇到异常
			CommonObjB passWordErro = new CommonObjB();
			passWordErro.putInt(Cmd.Login._RESULT_CODE, Cmd.ErroCode.UNKNOW);
			ControlCenter.sendToVisitor(Cmd.Login.CMD, passWordErro, visitor, userName);
			
			return null;
		}
		
	}
	
	@Override
	public void logoutLocalUserWhenUserReLogin(int requestId, Visitor visitor,
			User newUser, CommonObjB param) {
		
		//用户已经在本sfs登录，断开旧的用户
		
		User oldUser = userCenter.getUser(newUser.getUserKey());
		if (oldUser != null ) {
			
			//通知前端 有人在其他地方登录 把他挤了下来
			CommonObjB reLoginObj = new CommonObjB();
			reLoginObj.putInt(Cmd.Logout._RESULT_CODE, Cmd.ErroCode.USER_RELOGIN);
			ControlCenter.sendToUser(Cmd.Logout.CMD, reLoginObj, oldUser);
			ControlCenter.logout(oldUser, true);
		}
		
	}

	@Override
	public void logoutRemoteUserWhenUserReLogin(int requestId, Visitor visitor,
			User newUser, CommonObjB param) {
		// TODO 发送消息给远程的机器, 要求它退出该用户
	}
	
}
