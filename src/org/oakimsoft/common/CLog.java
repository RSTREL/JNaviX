package org.oakimsoft.common;

import java.util.Calendar;

public class CLog {
	public static void info(String information){
		Calendar cal = Calendar.getInstance();
		System.out.println(cal.get(Calendar.HOUR)+":" +
				   			cal.get(Calendar.MINUTE)+":" +  
				   			cal.get(Calendar.SECOND)+"." +
				   			cal.get(Calendar.MILLISECOND)+"| " +
				   			information);  
				   			
	}
	
	public static void info(Object object, String information){
		Calendar cal = Calendar.getInstance();
		System.out.println(cal.get(Calendar.HOUR_OF_DAY)+":" +
				   			cal.get(Calendar.MINUTE)+":" +  
				   			cal.get(Calendar.SECOND)+"." +
				   			cal.get(Calendar.MILLISECOND)+"| " +
				   			object.getClass().getName() + " Method:"+
				   			Thread.currentThread().getStackTrace()[2].getMethodName()+ " INFO:"+
				   			information);  
				   			
	}
		
	

}
