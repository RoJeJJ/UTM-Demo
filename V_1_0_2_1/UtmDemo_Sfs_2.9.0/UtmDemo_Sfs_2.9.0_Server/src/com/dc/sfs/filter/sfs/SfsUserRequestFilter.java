package com.dc.sfs.filter.sfs;

import java.util.concurrent.atomic.AtomicInteger;

import com.dc.sfs.center.ControlCenter;
import com.dc.sfs.center.LoggerCenter;
import com.dc.sfs.cmd.Cmd;
import com.dc.sfs.obj.Visitor;
import com.dc.utm.filter.UserThreadModeFilter;
import com.smartfoxserver.bitswarm.sessions.ISession;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.filter.FilterAction;
import com.smartfoxserver.v2.extensions.filter.SFSExtensionFilter;

/**
 * 
 * Sfs 用户请求过滤器
 * （拦截用户请求改由utm负责处理，拦截用户登录断线等请求 改由 utm处理）
 * 
 * @author Daemon
 *
 */
public class SfsUserRequestFilter extends SFSExtensionFilter {

private final AtomicInteger requestIds = new AtomicInteger();
	
	protected final UserThreadModeFilter<String, Integer, Visitor, Integer, com.dc.sfs.obj.User> userThreadModeFilter;
	
	public SfsUserRequestFilter(
			UserThreadModeFilter<String, Integer, Visitor, Integer, com.dc.sfs.obj.User> userThreadModeFilter) {
		
		this.userThreadModeFilter = userThreadModeFilter;
	}
	
	@Override
	public void destroy() {
	}

	@Override
	public FilterAction handleClientRequest(String cmd, User user, ISFSObject params) throws SFSException {
		
		//拦截用户请求改由utm负责处理
		//调用该方法的线程池是在server.xml中配置的 extensionThreadPoolSettings（Ext），老版本sfs则是在 server.xml中的extensionControllerThreadPoolSize
		
		Integer userId = (Integer)user.getProperty(Cmd.Login._USER_ID);
		userThreadModeFilter.handleUserRequest(requestIds.getAndIncrement(), cmd, userId, params);
		return FilterAction.HALT;
	}

	@Override
	public FilterAction handleServerEvent(ISFSEvent event) throws SFSException {
		
		//拦截用户登录断线等请求 改由 utm处理
		//调用该方法的线程池是在server.xml中配置的 extensionThreadPoolSettings（Sys），老版本sfs则是在 server.xml中的systemControllerThreadPoolSize
		
		switch (event.getType()) {
		case USER_LOGIN:
			
			//用户登录
			
			ISession session = (ISession) event.getParameter(SFSEventParam.SESSION);
			int sessionId = session.getId();
			
			Visitor visitor = new Visitor(session, session.getAddress());
			userThreadModeFilter.handleVisitorRequest(requestIds.getAndIncrement(), Cmd.Login.CMD, sessionId, visitor, event);
			
			//抛出SfsStopLoginException，阻止sfs登录流程继续走下去（如果返回FilterAction.HALT，则前端会受到 loginError的回调，所以这里用异常阻止）
			//请不要尝试在这里抛出SFSLoginException，否则前端会认为登录失败
			throw new SfsStopLoginException();
			//break;
			
		case USER_DISCONNECT:
		case USER_LOGOUT:
			
			//用户断线或者注销 
			
            User sfsUser = (User)event.getParameter(SFSEventParam.USER);
            
            Integer userId = (Integer)sfsUser.getProperty(Cmd.Login._USER_ID);
            com.dc.sfs.obj.User user = ControlCenter.getUser(userId);
            if( user != null ) {
            	
            	LoggerCenter.userRquestLogger.log(sfsUser.getName(), 
    					LoggerCenter.getBeforeInfo().append("cmd:sfs user disconnect and execute utm disconnect\n") );
            	
            	userThreadModeFilter.disconnect(requestIds.getAndIncrement(), user);
            	
            } else {
            	
            	LoggerCenter.userRquestLogger.log(sfsUser.getName(), 
    					LoggerCenter.getBeforeInfo().append("cmd:sfs user disconnect only !\n") );
            }
            
			break;
		default:
			break;
		}
		
		return FilterAction.CONTINUE;
	}
	
}
