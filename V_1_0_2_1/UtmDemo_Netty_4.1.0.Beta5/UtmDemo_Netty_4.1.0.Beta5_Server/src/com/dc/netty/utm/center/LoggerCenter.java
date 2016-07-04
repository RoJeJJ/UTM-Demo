package com.dc.netty.utm.center;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import com.dc.logger.dynamic.controller.DynamicLoggerController;
import com.dc.logger.dynamic.controller.IDynamicLoggerController;
import com.dc.logger.dynamic.controller.factory.ILoggerFactory;
import com.dc.logger.dynamic.logger.IDynamicLogger;
import com.dc.logger.dynamic.logger.rename.daily.SyncDailyRollingSizeConstraintDynamicLogger;

/**
 * 
 * 日志中心，对日志 和 常用的日志处理方法的封装
 * 
 * @author Daemon
 *
 */
public class LoggerCenter {
	
	/**
	 * 用户logger控制器（为每个用户生成一个log文件（每天生成一个新的文件，每个文件最大为102400））
	 */
	public static final DynamicLoggerController userRquestLogger = new DynamicLoggerController(new ILoggerFactory() {
		
		@Override
		public IDynamicLogger getNewLogger(IDynamicLoggerController controller,
				String targetName) {
			
			// maxIdleTime = Integer.MIN_VALUE, 为了使canClose总是返回true，因为调用close的时候回检查是否可以关闭，可以才会执行相应的操作
			return new SyncDailyRollingSizeConstraintDynamicLogger(controller, 
					ClassLoader.getSystemClassLoader().getResource("").getFile() + "../mlog/user/", targetName, "log", 
					true, 8096, true, Integer.MIN_VALUE, true, "_", "", 2,
					DEFUALT_TIME_ZONE, 102400);
			
		}
	});
	
	private static final String formatDate(long time, String pattern, TimeZone timeZone) {
		if (time == 0) return "NA";
		final SimpleDateFormat format = new SimpleDateFormat(pattern);
		format.setTimeZone(timeZone);
		return format.format(time);
	}
	
	private static final TimeZone DEFUALT_TIME_ZONE = TimeZone.getDefault();
	
	/**
	 * 获得 用户日志的 前缀（例如：“11:12:30.234 ” 这样的比较通用的开头 ）
	 * 
	 * @return
	 */
	public static final StringBuilder getBeforeInfo() {
		
		StringBuilder stringBuilder = new StringBuilder()
			.append( formatDate(System.currentTimeMillis(), "HH:mm:ss.SSS", DEFUALT_TIME_ZONE) ).append("  ");
		
		return stringBuilder;
	}
	
	
	
	static {
		
		//采用实时回收的方式，所以不需要启动关闭检查线程
//		userRquestLogger.addCloseAbleChecker();
		
		//使用了缓存的方式，这里启动一个缓存定期输出检查
		userRquestLogger.addFlushAbleChecker();
		
		userRquestLogger.startCheckerThread(60000);
	}

}
