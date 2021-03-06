package com.bdcom.dce.view.util;

import com.bdcom.dce.util.logger.ErrorLogger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author francis yuan <br>
 * E-mail: yuanjiajun@bdcom.com.cn <br>
 * @version 2012-11-20 <br>
 * Auto-Generated by eclipse Juno <br>
 */

public class Hook {

	private Method hook;
	
	private Object source;
	
	public Hook(Object source, String methodName) {
		this.source = source;
		Class<?> clazz = source.getClass();
		Method declaredMethod = null;
		try {
			declaredMethod = clazz.getDeclaredMethod(methodName,(Class<?>[]) null );
		} catch (SecurityException e) {
			ErrorLogger.log(e.getMessage());
		} catch (NoSuchMethodException e) {
			ErrorLogger.log(e.getMessage());
		} finally {
			hook = declaredMethod;
		}
	}
	
	public void invoke() {
		if ( null != hook && null != source ) {
			try {
				hook.setAccessible(true);
				hook.invoke(source, (Object[]) null );
			} catch (IllegalArgumentException e) {
				ErrorLogger.log(e.getMessage());
			} catch (IllegalAccessException e) {
				ErrorLogger.log(e.getMessage());
			} catch (InvocationTargetException e) {
				ErrorLogger.log(e.getMessage());
			} 
		}
	}
}
