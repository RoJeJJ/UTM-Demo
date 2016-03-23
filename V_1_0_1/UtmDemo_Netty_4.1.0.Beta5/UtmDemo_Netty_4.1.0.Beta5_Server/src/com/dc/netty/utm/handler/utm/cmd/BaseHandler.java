package com.dc.netty.utm.handler.utm.cmd;

import com.dc.netty.coder.commonobj.CommonObjB;
import com.dc.netty.utm.center.LoggerCenter;
import com.dc.netty.utm.obj.User;
import com.dc.qtm.handle.IRequestHandler;

/**
 * 
 * 用户cmd请求的基础处理器（实现一些基础的方法）
 * 
 * @author Daemon
 *
 */
public abstract class BaseHandler implements IRequestHandler<User, CommonObjB> {
	
	protected final String cmd = this.getClass().getSimpleName();

	@Override
	public boolean isLimited(int requestId, User user, CommonObjB param) {
		
		//基础cmd受队列长度限制，当队列满的时候会调用queueFull
		return true;
	}

	@Override
	public abstract void queueFull(int requestId, User user, CommonObjB param);

	@Override
	public void before(int requestId, User user, CommonObjB param) {
		
		//记录日志
		LoggerCenter.userRquestLogger.log(user.userName, LoggerCenter.getBeforeInfo()
				.append("cmd:").append(cmd).append("\n              params:").append(param).append("\n") );
		
	}

	/**
	 * 处理用户请求
	 * 
	 * @param requestId 请求Id
	 * @param user 用户
	 * @param param 请求参数
	 */
	public abstract void handlerUserRequest(int requestId, User user, CommonObjB param);
	
	@Override
	public void handlerRequest(int requestId, User user, CommonObjB param) {
		
		//判断用户是否离线（仅仅是保险处理）
		if( user.isLogining() ) {
			
			handlerUserRequest(requestId, user, param);
		}
		
	}

	@Override
	public void after(int requestId, User user, CommonObjB param) {
	}

}
