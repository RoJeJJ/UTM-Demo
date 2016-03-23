package com.dc.sfs.obj;

import java.io.IOException;

import com.smartfoxserver.bitswarm.sessions.ISession;

/**
 * 
 * 游客
 * 
 * @author Daemon
 *
 */
public class Visitor {

	public final ISession session;
	public final String ip;
	
	/**
	 * @param session 游客session
	 * @param ip 游客ip
	 */
	public Visitor(ISession session, String ip) {
		this.session = session;
		this.ip = ip;
	}

	/**
	 * 获得游客ip
	 * 
	 * @return 游客ip
	 */
	public String getIP() {
		return ip;
	}
	
	/**
	 * 断开游客
	 */
	public void disconnect() {
		try {
			session.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
