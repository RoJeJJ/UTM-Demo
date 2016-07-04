package com.dc.sfs.business.user.runnable;

import com.dc.qtm.handle.IRequestHandler;
import com.dc.sfs.center.ControlCenter;
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
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

public class UserLogoutConfirmRunnable extends UserLogoutCheckBusiness<String, Integer, Visitor, Integer, User> {

	
	@SuppressWarnings("rawtypes")
	public UserLogoutConfirmRunnable(
			UserCenter<Integer, User> userCenter,
			UserQueueResource<Visitor, Integer, User> userTaskQueueBusiness,
			IUserRequestFilter<String, Integer, Visitor, Integer, User> userRequestFilter,
			UserFlagBusiness<Visitor, Integer, User> userFlagBusiness,
			IRequestHandler onUserLoginHandler,
			EventManager<String, Integer, Visitor, Integer, User> eventManager,
			UserResourceCenter<Visitor, Integer, User> userResourceCenter) {
		super(userCenter, userTaskQueueBusiness, userRequestFilter, userFlagBusiness,
				onUserLoginHandler, eventManager, userResourceCenter);
	}
	
	@Override
	public void waitLogoutTimeOut(int requestId, Visitor visitor,
			User user, Object param) {
		
		ISFSObject sfsObject = new SFSObject();
		sfsObject.putInt(Cmd.Login._RESULT_CODE, Cmd.ErroCode.LOGOUT_FAIL);
		
		ControlCenter.sendToVisitor(Cmd.Login.CMD, sfsObject, user.session, user.userName);
		
	}

}
