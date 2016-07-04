package com.dc.sfs.handler.server.sfs;

import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

/**
 * 
 * sfs用户断线或退出接口（实际上并没有什么处理，但是必须存在，否则sfs不会派发该事件（SfsUserRequestFilterl拦截不到））
 * 
 * @author Daemon
 *
 */
public class OnUserGoneHandler extends BaseServerEventHandler {

	public OnUserGoneHandler() {
	}
	
    @Override
	public void handleServerEvent(ISFSEvent event) throws SFSException {
	}
    
}
