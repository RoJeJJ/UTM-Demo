package com.dc.sfs.center;

import java.util.ArrayList;

import com.dc.sfs.GameServerExtension;
import com.dc.sfs.obj.User;
import com.dc.utm.center.UserCenter;
import com.smartfoxserver.bitswarm.sessions.ISession;
import com.smartfoxserver.v2.api.ISFSApi;
import com.smartfoxserver.v2.api.response.ISFSResponseApi;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * 
 * 控制中心，对sfs提供的用户登录，退出，断开，收发消息进行封装
 * (仅对一个 SFSExtension进行封装，不适合多个extension的情形)
 * 
 * @author Daemon
 *
 */
public class ControlCenter {

	private static GameServerExtension extension;
	private static ISFSResponseApi sfsResponseApi;
	private static ISFSApi sfsApi;
	/**
	 * 用户中心
	 */
	private static UserCenter<Integer, User> userCenter;
	
	/**
	 * 设置SFSExtension对象
	 * 
	 * @param extension SFSExtension对象
	 */
	public static void setExtension(GameServerExtension extension) {
		
		ControlCenter.extension = extension;
		ControlCenter.sfsResponseApi = extension.getApi().getResponseAPI();
		ControlCenter.sfsApi = extension.getApi();
		
		ControlCenter.userCenter = GameServerExtension.userThreadMode.getUserCenter();
	}
	
	/**
	 * 给游客发送信息
	 * 
	 * @param cmdName cmd
	 * @param params 要发送的信息
	 * @param recipient 游客的session
	 * @param name 游客的名字（记录日志的名字）
	 */
	public static void sendToVisitor( String cmdName, ISFSObject params, ISession recipient, String name ) {
		
		//记录与游客的沟通日志
		LoggerCenter.userRquestLogger.log( name, 
				LoggerCenter.getBeforeInfo().append("back cmd:").append(cmdName).append("\n")
					.append("              params:").append( params.toJson() ).append("\n") );
		
		//给游客发送信息
		ArrayList<ISession> recipients = new ArrayList<ISession>(1);
		recipients.add(recipient);
		sfsResponseApi.sendExtResponse(cmdName, params, recipients, null, false);
		
	}
	
	/**
	 * 给用户发送信息
	 * 
	 * @param cmdName cmd
	 * @param params 要发送的信息
	 * @param user 用户
	 */
	public static void sendToUser( String cmdName, ISFSObject params, User user ) {
		
		//记录与客户的沟通日志
		LoggerCenter.userRquestLogger.log( user.userName, 
				LoggerCenter.getBeforeInfo().append("back cmd:").append(cmdName).append("\n")
					.append("              params:").append( params.toJson() ).append("\n") );
		
		//给用户发送信息
		extension.send(cmdName, params, user.sfsUser);
	}
	
	/**
	 * 登录用户
	 * 
	 * @param session 将要登录用户的session
	 * @param userName 用户名
	 * @return sfs User对象
	 */
	public static com.smartfoxserver.v2.entities.User loginUser(ISession session, String userName) {
		
		return sfsApi.login(session, userName, "", GameServerExtension.EXTENSION_NAME, null);
	}
	
//	public static com.smartfoxserver.v2.entities.User getUser(String userName) {
//		
//		return sfsApi.getUserByName(userName);
//	}
	
	/**
	 * 注销用户
	 * 
	 * @param user 要注销的用户
	 */
	public static void logoutUser(User user) {
		sfsApi.logout( user.sfsUser );
	}
	
	public static void disconnect(User user) {
		
		try {
			sfsApi.disconnectUser(user.sfsUser);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static User getUser(Integer userId) {
		
		return userCenter.getUser(userId);
	}
}
