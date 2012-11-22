package com.eastaeon.www;

import com.eastaeon.www.service.SuperView;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

public class SuperviewActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
//        registScreenStatus();
        finish();
    }

	private void registScreenStatus() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(mBroadcastReceiver, filter);
	}
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Intent mainServerIntent = new Intent();
			mainServerIntent.setClass(context, SuperView.class);
			context.startService(mainServerIntent);
		}
	};
}