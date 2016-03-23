package com.dc.netty.utm.handler.utm.server;

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
import com.dc.utm.resource.user.queue.UserQueueResource;
import com.dc.utm.user.flag.UserFlagBusiness;


public class OnUserLoginHandler extends com.dc.utm.handler.login.OnUserLoginHandler<Visitor, Integer, User, CommonObjB>  {

	@SuppressWarnings("rawtypes")
	public OnUserLoginHandler(UserCenter<Integer, User> userCenter,
			EventManager eventManager,
			UserFlagBusiness<Visitor, Integer, User> userFlagBusiness,
			UserQueueResource<Visitor, Integer, User> userTaskQueueBusiness,
			UserResourceCenter<Visitor, Integer, User> userResourceCenter) {
		super(userCenter, eventManager, userFlagBusiness, userTaskQueueBusiness, userResourceCenter);
	}

	@Override
	public boolean loginLinkCheck(int requestId, User user, CommonObjB param) {
		
		return ControlCenter.logincheck(user.channelId, user.userId, user);
		
	}

	@Override
	public void userLogin(int requestId, User user, CommonObjB param) {
		
		//用户登录成功
		
		try {
			//用户登录成功业务处理
			BusinessCenter.userProcess.userLoginToServer(user);
			
		} catch(Exception e) {
			
			e.printStackTrace();
			LoggerCenter.userRquestLogger.log(user.userName, e);
		}
		
		//通知前端登录成功
		CommonObjB loginSuccess = new CommonObjB();
		loginSuccess.putInt(Cmd.Login._RESULT_CODE, Cmd.SuccessCode.SUCCESS);
		ControlCenter.sendToUser(Cmd.Login.CMD, loginSuccess, user);
		
	}

}
