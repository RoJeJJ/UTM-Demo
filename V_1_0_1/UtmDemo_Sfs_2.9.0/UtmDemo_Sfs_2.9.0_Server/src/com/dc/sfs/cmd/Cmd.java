package com.dc.sfs.cmd;

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
		 * 队列满
		 */
		public final static int QUEUE_FULL = 6099;
		
		/**
		 * 未知错误
		 */
		public final static int UNKNOW = 5000;
		
		/**
		 * 用户命或密码错误
		 */
		public final static int PASSWORD_ERROR = 4000;
		
		/**
		 * 登录失败，请稍后再登录（旧的用户没有退出）
		 */
		public final static int LOGOUT_FAIL = 4011;
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
		public final static String _RESULT_CODE = "A";
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
		public final static String CMD = "login";
		
		/**
		 * 用户Id
		 */
		public final static String _USER_ID = "_";
	}
	
	/**
	 * 退出接口
	 * 
	 * @author Daemon
	 *
	 */
	public static class Logout extends BaseInfo {
		
		public final static int USER_RELOGIN = 6008;
		
		/**
		 * 命令名
		 */
		public final static String CMD = "logout";
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
		public final static String CMD = "disconect";
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
		public final static String CMD = "heartbeat";
		
		/**
		 * 请求号
		 */
		public final static String REQUEST_CODE = "a";
		
		/**
		 * 请求号
		 */
		public final static String _REQUEST_CODE = "C";
	}
}
