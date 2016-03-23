package com.dc.sfs;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.dc.qtm.handle.IRequestHandler;
import com.dc.qtm.thread.pool.LimitedUnboundedThreadPoolExecutor;
import com.dc.sfs.business.user.runnable.UserLogoutConfirmRunnable;
import com.dc.sfs.center.ControlCenter;
import com.dc.sfs.cmd.Cmd;
import com.dc.sfs.filter.sfs.SfsUserRequestFilter;
import com.dc.sfs.handler.cmd.HeartbeatHandler;
import com.dc.sfs.handler.cmd.LogoutHandler;
import com.dc.sfs.handler.server.OnUserDisconectHandler;
import com.dc.sfs.handler.server.OnUserLoginCheckHandler;
import com.dc.sfs.handler.server.OnUserLoginHandler;
import com.dc.sfs.handler.server.sfs.OnServerReadyHandler;
import com.dc.sfs.handler.server.sfs.OnUserGoneHandler;
import com.dc.sfs.handler.server.sfs.OnUserLoginHandler_Old;
import com.dc.sfs.obj.User;
import com.dc.sfs.obj.Visitor;
import com.dc.sfs.resource.logger.UserLoggerResource;
import com.dc.utm.UserThreadMode;
import com.dc.utm.event.EventManager;
import com.dc.utm.resource.user.UserResourceManager;
import com.dc.utm.user.flag.SimpleLocalUserFlagBusiness;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.extensions.SFSExtension;


/**
 * 
 * Sfs扩展启动类
 * 
 * @author Daemon
 *
 */
@SuppressWarnings("rawtypes")
public class GameServerExtension extends SFSExtension  {
	
	/**
	 * sfs扩展的名称(与sfs zone配置的一致)
	 */
	public static final String EXTENSION_NAME = "GameServer";
	
	/**
	 * utm模型
	 */
	public static final UserThreadMode<String, Integer, Visitor, Integer, User> userThreadMode;
	
	static {
		
		//游客请求cmd和对应的请求处理器
		Map<String, IRequestHandler> visitorCmdMapHandler = new HashMap<String, IRequestHandler>();
		//用户请求cmd和对应的请求处理器
		Map<String, IRequestHandler> userCmdMapHandler = new HashMap<String, IRequestHandler>();
		
		//线程池
		LimitedUnboundedThreadPoolExecutor pool = 
				new LimitedUnboundedThreadPoolExecutor(32, 32, 0L, 
						TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), 500);
		
		//事件管理器
		EventManager<String, Integer, Visitor, Integer, User> eventManager = new EventManager<String, Integer, Visitor, Integer, User>();
		
		//实例化utm模型
		userThreadMode 
			= new UserThreadMode<String, Integer, Visitor, Integer, User>(
				Cmd.Login.CMD, Cmd.DisConect.CMD, visitorCmdMapHandler, userCmdMapHandler, pool,
				eventManager
				);
		
		//添加用户日志资源管理器
		userThreadMode.getUserResourceManager().getUserResourceCenter().addUserResource( new UserLoggerResource() );
		
		//用户登录标志位管理（仅测试，详见该类说明和UserFlagBusiness说明）
		SimpleLocalUserFlagBusiness<Visitor, Integer, User> userFlagBusiness = new SimpleLocalUserFlagBusiness<Visitor, Integer, User>();
		
		//实例化用户登录处理类（注意：该类不需要注册到userCmdMapHandler中，如果用户登录检查通过，utm会自动调用该实例处理用户登录）
		OnUserLoginHandler onUserLoginHandler = new OnUserLoginHandler( userThreadMode.getUserCenter(), eventManager,
				userFlagBusiness, userThreadMode.getUserResourceManager().getUserQueueResource(), 
				userThreadMode.getUserResourceManager().getUserResourceCenter() );
		//实例化老用户退出处理类(用户退出检查线程)
		UserLogoutConfirmRunnable userLogoutCheckBusiness = new UserLogoutConfirmRunnable( userThreadMode.getUserCenter(), 
				userThreadMode.getUserResourceManager().getUserQueueResource(), userThreadMode.getUserRequestFilter(), userFlagBusiness, 
				onUserLoginHandler, eventManager, userThreadMode.getUserResourceManager().getUserResourceCenter() );
		
		//注册游客登录检查的处理器
		visitorCmdMapHandler.put(Cmd.Login.CMD, new OnUserLoginCheckHandler( userThreadMode.getUserCenter(), eventManager,
				userThreadMode.getUserResourceManager().getUserQueueResource(), userThreadMode.getUserRequestFilter(), userFlagBusiness, 
				userLogoutCheckBusiness, onUserLoginHandler, userThreadMode.getUserResourceManager().getUserResourceCenter() ));
		
		//注册用户断线的处理器
		userCmdMapHandler.put(Cmd.DisConect.CMD, new OnUserDisconectHandler( userThreadMode.getUserCenter(), eventManager,
				userFlagBusiness, userThreadMode.getUserResourceManager().getUserResourceCenter() ));
		//注册用户退出的处理器
		userCmdMapHandler.put(Cmd.Logout.CMD, new LogoutHandler( userThreadMode.getUserCenter(), eventManager,
				userFlagBusiness, userThreadMode.getUserResourceManager().getUserResourceCenter() ));
		//注册用户心跳的处理器
		userCmdMapHandler.put(Cmd.Heartbeat.CMD, new HeartbeatHandler());
		
		
		//启动 用户退出检查线程
		Thread userLogoutCheckRunnable = new Thread(userLogoutCheckBusiness);
		userLogoutCheckRunnable.setName("userLogoutCheckRunnable");
		userLogoutCheckRunnable.start();
		
		
		startThread2PrintResourceInfo(userFlagBusiness);
	}
	
	/**
	 * 启动一个线程，30秒钟打印一次各个 用户资源的 信息（其中UserFlag的信息是一个基本的参考（userFlag严格上并不是用户资源，详见UserFlagBusiness说明））
	 * 
	 * @param userFlagBusiness 用户登录标志位管理类
	 */
	private static void startThread2PrintResourceInfo(final SimpleLocalUserFlagBusiness<Visitor, Integer, User> userFlagBusiness) {
		
		Thread printResource = new Thread(new Runnable() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				
				UserResourceManager userResourceManager = userThreadMode.getUserResourceManager();
				
				for(;;) {
					
					StringBuilder stringBuilder = new StringBuilder("Resource:{");
					Map<String, Integer> resourceNameMapActiveNum = userResourceManager.getResourceNameMapActiveNum();
					for( Entry<String, Integer> entry : resourceNameMapActiveNum.entrySet() ) {
						
						stringBuilder.append(entry.getKey()).append(":").append(entry.getValue()).append(", ");
					}
					stringBuilder.append("UserFlag:").append( userFlagBusiness.getUserIdSet().size() ).append("}");
					
					System.out.println( stringBuilder.toString() );
					
					try {
						TimeUnit.MILLISECONDS.sleep(30000);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
				
				
			}
		});
		
		printResource.setName("printResource");
		printResource.start();
	}
	
	@Override
	public void init() {
		
		addFilter();
		
		addEventHandler();
		
		ControlCenter.setExtension(this);
	}
	
	/**
	 * 添加sfs过滤器
	 */
	private void addFilter() {

		//UTM 过滤器（拦截用户请求改由utm负责处理，拦截用户登录断线等请求 改由 utm处理）
		addFilter("SynchronousControl", new SfsUserRequestFilter(userThreadMode.getUserThreadModeFilter()));
	}
	
	/**
	 * 添加sfs事件监听器
	 */
	private void addEventHandler() {
		
		// 用户登陆事件（实际上并没有调用，但是必须存在，否则sfs不会派发该事件（SfsUserRequestFilterl拦截不到））
		addEventHandler(SFSEventType.USER_LOGIN, OnUserLoginHandler_Old.class);
		// 用户断线事件（实际上并没有什么处理，但是必须存在，否则sfs不会派发该事件（SfsUserRequestFilterl拦截不到））
		addEventHandler(SFSEventType.USER_LOGOUT, OnUserGoneHandler.class);
		// 用户断线事件（实际上并没有什么处理，但是必须存在，否则sfs不会派发该事件（SfsUserRequestFilterl拦截不到））
		addEventHandler(SFSEventType.USER_DISCONNECT, OnUserGoneHandler.class);
	    // 服务器启动完毕事件
	    addEventHandler(SFSEventType.SERVER_READY, OnServerReadyHandler.class);
	}
}

