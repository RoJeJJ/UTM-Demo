package com.dc.sfs.handler.server.sfs;

import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

/**
 * 
 * sfs初始化完成接口
 * 
 * @author Daemon
 *
 */
public class OnServerReadyHandler extends BaseServerEventHandler {

	@Override
	public void handleServerEvent(ISFSEvent event) throws SFSException {
		
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println("<<=========================Server ready===============================>>");
		System.out.println("<<=========================Server ready===============================>>");
		System.out.println("<<=========================Server ready===============================>>");
		System.out.println("<<=========================Server ready===============================>>");
		System.out.println("<<=========================Server ready===============================>>");
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
	}

}
