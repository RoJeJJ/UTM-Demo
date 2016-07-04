package com.dc.sfs.center;

import com.dc.sfs.business.user.UserProcess;

/**
 * 
 * 业务中心，保存着各种业务的实现(单例)
 * 
 * @author Daemon
 *
 */
public class BusinessCenter {

	/**
	 * 用户业务处理对象
	 */
	public final static UserProcess userProcess = new UserProcess();
}
