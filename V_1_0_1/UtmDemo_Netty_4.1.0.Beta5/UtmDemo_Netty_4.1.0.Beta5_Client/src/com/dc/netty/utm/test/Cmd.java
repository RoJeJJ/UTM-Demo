package com.dc.netty.utm.test;


/**
 * 
 * 与前端交互的 接口 和 字段的定义(以下划线开始命名的字段为 返回前端的字段)
 * 
 * 
 * @author Daemon
 *
 */
public class Cmd {
	
	/**
	 * 成功 的  结果码
	 * 
	 * @author Daemon
	 *
	 */
	public static class SuccessCode {
		
		public final static int SUCCESS = 0;
	}
	
	/**
	 * 失败 的 结果码
	 * 
	 * @author Daemon
	 *
	 */
	public static class ErroCode {
		
		/**
		 * 未知错误
		 */
		public final static int UNKNOW = 5000;
		
		/**
		 * 队列满
		 */
		public final static int QUEUE_FULL = 7001;
		
		/**
		 * 用户命或密码错误
		 */
		public final static int PASSWORD_ERROR = 6003;
		/**
		 * 用户在其他地方登录
		 */
		public final static int USER_RELOGIN = 6004;
		/**
		 * 登录失败，请稍后再登录（旧的用户没有退出）
		 */
		public final static int LOGOUT_FAIL = 6005;
	}
	
	/**
	 * 基础的信息
	 * 
	 * @author Daemon
	 *
	 */
	public static class BaseInfo {
		
		/**
		 * 结果码
		 */
		public final static byte _RESULT_CODE = 1;
	}

	/**
	 * 登录接口
	 * 
	 * @author Daemon
	 *
	 */
	public static class Login extends BaseInfo {
		
		/**
		 * 命令名
		 */
		public final static int CMD = -1001;
		
		/**
		 * 用户名
		 */
		public final static byte USER_NAME = 1;
		
		/**
		 * 密码
		 */
		public final static byte PASSWORD = 2;
	}
	
	
	/**
	 * 退出接口
	 * 
	 * @author Daemon
	 *
	 */
	public static class Logout extends BaseInfo {
		
		/**
		 * 命令名
		 */
		public final static int CMD = -1003;
		
		/**
		 * 用户重复登录的标志位（表示已经给用户推送重复登录的信息了，在logoutHandler了中就不需要再推送消息了）
		 */
		public final static byte __ISRELOGIN = -7;
		
	}
	
	/**
	 * 断线接口
	 * 
	 * @author Daemon
	 *
	 */
	public static class DisConect {
		
		/**
		 * 命令名
		 */
		public final static int CMD = -1005;
	}
	
	/**
	 * 心跳接口
	 * 
	 * @author Daemon
	 *
	 */
	public static class Heartbeat extends BaseInfo {
		
		/**
		 * 命令名
		 */
		public final static int CMD = -1006;
		
		/**
		 * 请求号
		 */
		public final static byte REQUEST_CODE = 1;
		
		/**
		 * 请求号
		 */
		public final static byte _REQUEST_CODE = 101;
	}
}
