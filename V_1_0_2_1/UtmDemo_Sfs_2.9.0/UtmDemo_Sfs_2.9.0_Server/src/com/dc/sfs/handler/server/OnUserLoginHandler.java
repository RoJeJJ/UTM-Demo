package com.dc.sfs.handler.server;

import com.dc.sfs.center.ControlCenter;
import com.dc.sfs.center.LoggerCenter;
import com.dc.sfs.center.BusinessCenter;
import com.dc.sfs.cmd.Cmd;
import com.dc.sfs.obj.User;
import com.dc.sfs.obj.Visitor;
import com.dc.utm.center.UserCenter;
import com.dc.utm.event.EventManager;
import com.dc.utm.resource.user.UserResourceCenter;
import com.dc.utm.resource.user.queue.UserQueueResource;
import com.dc.utm.user.flag.UserFlagBusiness;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;


/**
 * 
 * 用户登录
 * 
 * @author Daemon
 *
 */
public class OnUserLoginHandler extends com.dc.utm.handler.login.OnUserLoginHandler<Visitor, Integer, User, ISFSEvent>  {

	
	@SuppressWarnings("rawtypes")
	public OnUserLoginHandler(UserCenter<Integer, User> userCenter,
			EventManager eventManager,
			UserFlagBusiness<Visitor, Integer, User> userFlagBusiness,
			UserQueueResource<Visitor, Integer, User> userTaskQueueBusiness,
			UserResourceCenter<Visitor, Integer, User> userResourceCenter) {
		super(userCenter, eventManager, userFlagBusiness, userTaskQueueBusiness, userResourceCenter);
	}
	
	
	@Override
	public void before(int requestId, User user, ISFSEvent param) {
		
		super.before(requestId, user, param);
		
		//记录日志
		LoggerCenter.userRquestLogger.log(user.userName, LoggerCenter.getBeforeInfo().append("cmd:OnUserLoginHandler\n") );
	}

	@Override
	public boolean loginLinkCheck(int requestId, User user, ISFSEvent event) {
		
		//将用户登录到sfs，如果用户已经断开则返回的sfsUser为空
		
		com.smartfoxserver.v2.entities.User sfsUser = ControlCenter.loginUser(user.session, user.userName);
		
		if( sfsUser != null ) {
			
			//在sfs user中条件参数：用户Id
			sfsUser.setProperty(Cmd.Login._USER_ID, user.getUserKey());
			//设置User对应的sfs user对象
			user.setSfsUser(sfsUser);
			
			//登录成功
			return true;
			
		} else {
			
			//登录失败
			return false;
		}
		
	}
	
	@Override
	public void userLogin(int requestId, User user, ISFSEvent event) {
		
		//用户登录成功
		
		try {
			//用户登录成功业务处理
			BusinessCenter.userProcess.userLoginToServer(user);
			
		} catch(Exception e) {
			
			e.printStackTrace();
			LoggerCenter.userRquestLogger.log(user.userName, e);
		}
		
		//通知前端登录成功
		ISFSObject loginSuccess = new SFSObject();
		loginSuccess.putInt(Cmd.Login._RESULT_CODE, Cmd.SuccessCode.SUCCESS);
		ControlCenter.sendToUser(Cmd.Login.CMD, loginSuccess, user);
		
	}

}
