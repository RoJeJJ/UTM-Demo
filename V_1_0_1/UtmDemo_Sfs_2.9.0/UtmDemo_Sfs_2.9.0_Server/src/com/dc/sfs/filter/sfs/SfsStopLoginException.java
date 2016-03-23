package com.dc.sfs.filter.sfs;

import com.smartfoxserver.v2.exceptions.IErrorCode;
import com.smartfoxserver.v2.exceptions.SFSErrorData;
import com.smartfoxserver.v2.exceptions.SFSException;

/**
 * 
 * 用于停止sfs登录过程的异常（转由utm线程池处理）
 * 
 * @author Daemon
 *
 */
public class SfsStopLoginException extends SFSException {

	private static final long serialVersionUID = 1587750815111599084L;

	private static final StackTraceElement[] emptyStackTraceElements = new StackTraceElement[0];
	
	private static final SFSErrorData sfsErrorData = new SFSErrorData(new IErrorCode() {
		
		@Override
		public short getId() {
			return -1111;
		}
	});
	
	private static final Throwable throwable = new Throwable("");
	
	@Override
	public SFSErrorData getErrorData() {
		return sfsErrorData;
	}
	
	@Override
	public Throwable getCause() {
		return throwable;
	}
	
	@Override
	public String getMessage() {
		return "";
	}
	
	@Override
	public StackTraceElement[] getStackTrace() {
		return emptyStackTraceElements;
	}

	
}
