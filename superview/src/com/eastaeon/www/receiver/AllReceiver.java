package com.eastaeon.www.receiver;

import java.util.List;

import com.eastaeon.www.service.SuperView;
import com.eastaeon.www.utils.Logger;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AllReceiver extends BroadcastReceiver {
	private final  String SERVICE_NAME="com.eastaeon.www.service.SuperView";
	/**服务是否在运行*/
	private boolean isRunning = false;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Logger.i(getClass().getName() + "*****" + "allreceiver:onReceive");
		String packageName = context.getPackageName();
		String serviceName = SERVICE_NAME;
		ActivityManager am =(ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> runningServices = am.getRunningServices(50);
		for (RunningServiceInfo runningServiceInfo : runningServices) {
			String runClassName = runningServiceInfo.service.getClassName();
			String runPackageName = runningServiceInfo.service.getPackageName();
			if (runClassName.equals(serviceName)  && runPackageName.equals(packageName)) {
				isRunning = true ;
				Logger.i(getClass().getName() + "*****" + "服务已经在运行："+SERVICE_NAME);
			}
		}
		if (!isRunning) {
			startServiceforSuperView(context);
			isRunning = !isRunning;
		}
	}

	private void startServiceforSuperView(Context context) {
		Intent mainServerIntent = new Intent();
		mainServerIntent.setClass(context, SuperView.class);
		context.startService(mainServerIntent);
		Logger.i(getClass().getName() + "*****" + "startService:"+SERVICE_NAME);
	}

}
