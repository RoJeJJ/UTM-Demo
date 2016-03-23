package com.dc.netty.utm.business.user.runnable;

import io.netty.channel.ChannelId;

import com.dc.netty.coder.commonobj.CommonObjB;
import com.dc.netty.utm.center.ControlCenter;
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

/**
 * 
 * 用户退出检查线程
 * 
 * 当老用户在长时间未退出的时候，给准备登录的用户发送登录错误的错误码
 * 
 * @author Daemon
 *
 */
public class UserLogoutConfirmRunnable extends UserLogoutCheckBusiness<Integer, ChannelId, Visitor, Integer, User> {

	
	@SuppressWarnings("rawtypes")
	public UserLogoutConfirmRunnable(
			UserCenter<Integer, User> userCenter,
			UserQueueResource<Visitor, Integer, User> userQueueResource,
			IUserRequestFilter<Integer, ChannelId, Visitor, Integer, User> userRequestFilter,
			UserFlagBusiness<Visitor, Integer, User> userFlagBusiness,
			IRequestHandler onUserLoginHandler,
			EventManager<Integer, ChannelId, Visitor, Integer, User> eventManager,
			UserResourceCenter<Visitor, Integer, User> userResourceCenter) {
		
		super(userCenter, userQueueResource,
				userRequestFilter, userFlagBusiness, onUserLoginHandler, eventManager,
				userResourceCenter);
	}

	@Override
	public void waitLogoutTimeOut(int requestId, Visitor visitor,
			User user, Object param) {
		
		//当老用户在长时间未退出的时候，给准备登录的用户发送登录错误的错误码
		
		CommonObjB logoutFail = new CommonObjB();
		logoutFail.putInt(Cmd.Login._RESULT_CODE, Cmd.ErroCode.LOGOUT_FAIL);
		
		ControlCenter.sendToUser(Cmd.Login.CMD, logoutFail, user);
		
	}

}
