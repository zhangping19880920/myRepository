package com.eastaeon.www.utils;
import android.util.Log;

public class Logger {
	private static int LOG_LEVEL=6;
	private static int ERROR = 1;
	private static int WARN = 2;
	private static int INFO =3;
	private static int DEBUG =4;
	private static int VERBOS=5;
	private static String TAG="ZhangPing";
	
	public static void e(String msg){
		if(LOG_LEVEL>ERROR){
			Log.e(TAG,msg);
		}
	}
	public static void w(String msg){
		if(LOG_LEVEL>WARN){
			Log.w(TAG,msg);
		}
	}
	public static void i(String msg){
		if(LOG_LEVEL>INFO){
			Log.i(TAG,msg);
		}
	}
	public static void d(String msg){
		if(LOG_LEVEL>DEBUG){
			Log.d(TAG,msg);
		}
	}
	public static void v(String msg){
		if(LOG_LEVEL>VERBOS){
			Log.v(TAG,msg);
		}
	}
	
}
