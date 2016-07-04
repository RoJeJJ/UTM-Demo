package com.dc.netty.utm.handler.utm.cmd;

import com.dc.netty.coder.commonobj.CommonObjB;
import com.dc.netty.utm.center.ControlCenter;
import com.dc.netty.utm.center.LoggerCenter;
import com.dc.netty.utm.cmd.Cmd;
import com.dc.netty.utm.obj.User;

/**
 * 
 * 用户心跳接口
 * 
 * @author Daemon
 *
 */
public class HeartbeatHandler extends BaseHandler {

	@Override
	public void handlerUserRequest(int requestId, User user, CommonObjB param) {
		
		//给用户返回心跳信息
		
		try {
			
			int requestCode = param.getInt(Cmd.Heartbeat.REQUEST_CODE);
			
			CommonObjB returnObject = new CommonObjB();
			returnObject.putInt(Cmd.Heartbeat._RESULT_CODE, Cmd.SuccessCode.SUCCESS);
			returnObject.putInt(Cmd.Heartbeat._REQUEST_CODE, requestCode);

			ControlCenter.sendToUser(Cmd.Heartbeat.CMD, returnObject, user);
			
		} catch (Exception e) {
			
			CommonObjB returnObject = new CommonObjB();
			returnObject.putInt(Cmd.Heartbeat._RESULT_CODE, Cmd.ErroCode.UNKNOW);
			ControlCenter.sendToUser(Cmd.Heartbeat.CMD, returnObject, user);
			
			LoggerCenter.userRquestLogger.log(user.userName, e);
		}
	}

	@Override
	public void queueFull(int requestId, User user, CommonObjB param) {
		
		//即使在队列满也给用户返回心跳信息（该处理并不耗时（sfs的发送消息方法是异步的，不会照成这里阻塞））
		
		try {
			
			int requestCode = param.getInt(Cmd.Heartbeat.REQUEST_CODE);
			
			CommonObjB returnObject = new CommonObjB();
			returnObject.putInt(Cmd.Heartbeat._RESULT_CODE, Cmd.SuccessCode.SUCCESS);
			returnObject.putInt(Cmd.Heartbeat._REQUEST_CODE, requestCode);

			ControlCenter.sendToUser(Cmd.Heartbeat.CMD, returnObject, user);
			
		} catch (Exception e) {
			
			CommonObjB returnObject = new CommonObjB();
			returnObject.putInt(Cmd.Heartbeat._RESULT_CODE, Cmd.ErroCode.UNKNOW);
			ControlCenter.sendToUser(Cmd.Heartbeat.CMD, returnObject, user);
			
			LoggerCenter.userRquestLogger.log(user.userName, e);
		}
		
	}

}
