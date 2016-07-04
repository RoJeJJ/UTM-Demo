package com.dc.sfs.handler.cmd;

import com.dc.sfs.center.ControlCenter;
import com.dc.sfs.center.LoggerCenter;
import com.dc.sfs.cmd.Cmd;
import com.dc.sfs.obj.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;

/**
 * 
 * 用户心跳接口
 * 
 * @author Daemon
 *
 */
public class HeartbeatHandler extends BaseHandler {

	@Override
	public void handlerUserRequest(int requestId, User user, ISFSObject param) {
		
		//给用户返回心跳信息
		
		try {
			
			int requestCode = param.getInt(Cmd.Heartbeat.REQUEST_CODE);
			
			SFSObject returnObject = new SFSObject();
			returnObject.putInt(Cmd.Heartbeat._RESULT_CODE, Cmd.SuccessCode.SUCCESS);
			returnObject.putInt(Cmd.Heartbeat._REQUEST_CODE, requestCode);

			ControlCenter.sendToUser(Cmd.Heartbeat.CMD, returnObject, user);
			
		} catch (Exception e) {
			
			SFSObject returnObject = new SFSObject();
			returnObject.putInt(Cmd.Heartbeat._RESULT_CODE, Cmd.ErroCode.UNKNOW);
			ControlCenter.sendToUser(Cmd.Heartbeat.CMD, returnObject, user);
			
			LoggerCenter.userRquestLogger.log(user.userName, e);
		}
	}

	@Override
	public void queueFull(int requestId, User user, ISFSObject param) {
		
		//即使在队列满也给用户返回心跳信息（该处理并不耗时（sfs的发送消息方法是异步的，不会照成这里阻塞））
		
		try {
			
			int requestCode = param.getInt(Cmd.Heartbeat.REQUEST_CODE);
			
			SFSObject returnObject = new SFSObject();
			returnObject.putInt(Cmd.Heartbeat._RESULT_CODE, Cmd.SuccessCode.SUCCESS);
			returnObject.putInt(Cmd.Heartbeat._REQUEST_CODE, requestCode);

			ControlCenter.sendToUser(Cmd.Heartbeat.CMD, returnObject, user);
			
		} catch (Exception e) {
			
			SFSObject returnObject = new SFSObject();
			returnObject.putInt(Cmd.Heartbeat._RESULT_CODE, Cmd.ErroCode.UNKNOW);
			ControlCenter.sendToUser(Cmd.Heartbeat.CMD, returnObject, user);
			
			LoggerCenter.userRquestLogger.log(user.userName, e);
		}
		
	}

}
