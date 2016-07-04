package com.dc.netty.utm.handler;

import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.dc.netty.utm.business.user.runnable.UserLogoutConfirmRunnable;
import com.dc.netty.utm.center.ControlCenter;
import com.dc.netty.utm.cmd.Cmd;
import com.dc.netty.utm.handler.utm.cmd.HeartbeatHandler;
import com.dc.netty.utm.handler.utm.cmd.LogoutHandler;
import com.dc.netty.utm.handler.utm.server.OnUserDisconectHandler;
import com.dc.netty.utm.handler.utm.server.OnUserLoginCheckHandler;
import com.dc.netty.utm.handler.utm.server.OnUserLoginHandler;
import com.dc.netty.utm.obj.User;
import com.dc.netty.utm.obj.Visitor;
import com.dc.netty.utm.resource.logger.UserLoggerResource;
import com.dc.qtm.handle.IRequestHandler;
import com.dc.qtm.thread.pool.LimitedUnboundedThreadPoolExecutor;
import com.dc.utm.UserThreadMode;
import com.dc.utm.event.EventManager;
import com.dc.utm.filter.UserThreadModeFilter;
import com.dc.utm.resource.user.UserResourceManager;
import com.dc.utm.user.flag.SimpleLocalUserFlagBusiness;

/**
 * 
 * netty的初始化类，在这里初始化utm组件
 * 
 * @author Daemon
 *
 */
public class MainHandlerInitializer extends ChannelInitializer<SocketChannel> {
	
	private final AtomicInteger requestIds = new AtomicInteger();
	
	private final UserThreadMode<Integer, ChannelId, Visitor, Integer, User> userThreadMode;
	private final UserThreadModeFilter<Integer, ChannelId, Visitor, Integer, User> userThreadModeFilter;
	
	@SuppressWarnings("rawtypes")
	public MainHandlerInitializer() {
		
		//游客请求cmd和对应的请求处理器
		Map<Integer, IRequestHandler> visitorCmdMapHandler = new HashMap<Integer, IRequestHandler>();
		//用户请求cmd和对应的请求处理器
		Map<Integer, IRequestHandler> userCmdMapHandler = new HashMap<Integer, IRequestHandler>();
		
		//线程池
		LimitedUnboundedThreadPoolExecutor pool = 
				new LimitedUnboundedThreadPoolExecutor(32, 32, 0L, 
						TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), 800);
		
		//事件管理器
		EventManager<Integer, ChannelId, Visitor, Integer, User> eventManager = new EventManager<Integer, ChannelId, Visitor, Integer, User>();
		
		//用户登录标志位管理（仅测试，详见该类说明和UserFlagBusiness说明）
		SimpleLocalUserFlagBusiness<Visitor, Integer, User> userFlagBusiness = new SimpleLocalUserFlagBusiness<Visitor, Integer, User>();
		
		//实例化utm模型
		userThreadMode 
			= new UserThreadMode<Integer, ChannelId, Visitor, Integer, User>(
				Cmd.Login.CMD, Cmd.DisConect.CMD, visitorCmdMapHandler, userCmdMapHandler, pool,
				eventManager, userFlagBusiness, 4000, 32
				);
		
		userThreadModeFilter = userThreadMode.getUserThreadModeFilter();
		ControlCenter.setUserCenter(userThreadMode.getUserCenter());
		ControlCenter.setUserThreadModeFilter(userThreadModeFilter);
		ControlCenter.setRequestIds(requestIds);
		
		//添加用户日志资源管理器
		userThreadMode.getUserResourceManager().getUserResourceCenter().addUserResource( new UserLoggerResource() );
		
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
		
		
		System.out.println("\n\n\n\n\n-------------------------------------------------------------------");
		System.out.println("----------------------------NUTM START-----------------------------");
		System.out.println("----------------------------NUTM START-----------------------------");
		System.out.println("----------------------------NUTM START-----------------------------");
		System.out.println("-------------------------------------------------------------------\n\n\n\n\n");
		
	}
	
	/**
	 * 启动一个线程，30秒钟打印一次各个 用户资源的 信息（其中UserFlag的信息是一个基本的参考（userFlag严格上并不是用户资源，详见UserFlagBusiness说明））
	 * 
	 * @param userFlagBusiness 用户登录标志位管理类
	 */
	private void startThread2PrintResourceInfo(final SimpleLocalUserFlagBusiness<Visitor, Integer, User> userFlagBusiness) {
		
		Thread printResource = new Thread(new Runnable() {
			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public void run() {
				
				UserResourceManager userResourceManager = userThreadMode.getUserResourceManager();
				
				for(;;) {
					
					try {
						TimeUnit.MILLISECONDS.sleep(30000);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					StringBuilder stringBuilder = new StringBuilder("Resource:{");
					Map<String, Integer> resourceNameMapActiveNum = userResourceManager.getResourceNameMapActiveNum();
					for( Entry<String, Integer> entry : resourceNameMapActiveNum.entrySet() ) {
						
						stringBuilder.append(entry.getKey()).append(":").append(entry.getValue()).append(", ");
					}
					stringBuilder.append("UserFlag:").append( userFlagBusiness.getUserIdSet().size() ).append("}");
					
					System.out.println( stringBuilder.toString() );
					
				}
				
				
			}
		});
		
		printResource.setName("printResource");
		printResource.start();
	}

	@Override
    protected void initChannel(SocketChannel ch) throws Exception {
		
        ChannelPipeline pipeline = ch.pipeline();
        
        //按照长度解码的解码器
        pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));  
        //长度编码器（消息的头4位 表示 这条消息的长度）
        pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));  
        
        //主处理器
        pipeline.addLast("handler", new MainHandler(requestIds, userThreadModeFilter));
        
    }
}






