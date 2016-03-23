/*
 * Copyright (C) 2013 GameServer Project
 *               Author: Daemon
 *               Date: 2013年10月8日
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dc.sfs.handler.server.sfs;

import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.exceptions.SFSLoginException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

/**
 * 
 * sfs用户登录接口（实际上并没有调用，但是必须存在，否则sfs不会派发该事件（SfsUserRequestFilterl拦截不到））
 * 
 * @author Daemon
 *
 */
public class OnUserLoginHandler_Old extends BaseServerEventHandler {
	
	@Override
	public void handleServerEvent(ISFSEvent arg0) throws SFSException {
		
		System.out.println("ACCIDENT: un expect invoke in OnUserLoginHandler!");
		throw new SFSLoginException("");
	}
	
	
	
}
