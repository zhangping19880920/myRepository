package com.eastaeon.www.service;

import java.util.ArrayList;

import com.eastaeon.www.R;
import com.eastaeon.www.ZPMusic;
import com.eastaeon.www.ZPVideo;
import com.eastaeon.www.utils.Logger;
import com.eastaeon.www.utils.Utils;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class SuperView extends Service implements OnClickListener {
	private String CONFIG_PREFERENCE_NAME ;
	private int[] LISTVIEWID ;
	private WindowManager wm;
	private WindowManager.LayoutParams indicatorParams;
	private WindowManager.LayoutParams contentParams;
	/** 指示器 */
	private ImageView mIndicatorView;
	
	/** 窗口宽度 */
	private int mWindowWidth;
	
	/** 窗口高度 */
	private int mWindowHeight;
	
	/** 控制面板 */
	private LinearLayout mControlPlane;
	
	/** 执行单击操作 */
	private static final int SINGLE_CLICK = 1;
	
	
	/** Handler */
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SINGLE_CLICK: //hand single click delay 550 ms
				Logger.i(getClass().getName() + "*****" + "go go to to single click");
				setContentViewShowAndHide(mControlPlane);
				break;
			}
		}
	};
	/** 退出按钮 */
	private ImageView iv_control_exit;
	
	/** ListView 内容 */
	private ListView lv_controlplane_content;

	private SharedPreferences mConfigSP;

	private String[] mControlPlaneArrays;
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		CONFIG_PREFERENCE_NAME = getString(R.string.config_preference_name) ;
		LISTVIEWID = getResources().getIntArray(R.array.listview_item_id);
		Logger.i(getClass().getName() + "*****" + "SuperView:onCreate");
		mConfigSP = getSharedPreferences(CONFIG_PREFERENCE_NAME, MODE_PRIVATE);
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		mIndicatorView = (ImageView) View.inflate(getApplicationContext(), R.layout.indicator, null);
		mControlPlane = (LinearLayout) View.inflate(getApplicationContext(), R.layout.controlplane, null);
		findControlPlaneView();
		setControlPlaneView();
		setIndicatorWindowManagerParams();
		setControlPlaneWindowManagerParams();
		setIndicatorTouchListener(mIndicatorView);
		super.onCreate();
	}
	/** 设置控制面板的条目 */
	private void setControlPlaneView() {
		mControlPlaneArrays = getResources().getStringArray(R.array.control_plane_array);
		if (mControlPlaneArrays != null && mControlPlaneArrays.length > 0) { //根据数组填充ListView
			lv_controlplane_content.setAdapter(new ControlPlaneAdapter(mControlPlaneArrays));
		}else {
			Log.e("zhangping", "mControlPlaneArrays  null || mControlPlaneArrays.length = 0");
		}
	}

	/** 从xml找到控制面板 */
	private void findControlPlaneView() {
		if (mControlPlane != null ) {
			iv_control_exit = (ImageView) mControlPlane.findViewById(R.id.iv_control_exit);
			lv_controlplane_content = (ListView) mControlPlane.findViewById(R.id.lv_controlplane_content);
			iv_control_exit.setOnClickListener(this);
		}
	}

	/**设置ControlPlane的窗口属性*/
	private void setControlPlaneWindowManagerParams() {
		contentParams = new LayoutParams();
		contentParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		contentParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		contentParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE ;
		contentParams.format = PixelFormat.TRANSLUCENT;
		contentParams.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
		if (mControlPlane!=null) {
			mControlPlane.setVisibility(View.GONE);
			wm.addView(mControlPlane, contentParams);
		}
	}

	/**设置indicator的窗口属性*/
	private void setIndicatorWindowManagerParams() {
		indicatorParams = new LayoutParams();
		indicatorParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		indicatorParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		indicatorParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE ;
		indicatorParams.format = PixelFormat.TRANSLUCENT;
		indicatorParams.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
		if (mIndicatorView!=null) {
			wm.addView(mIndicatorView, indicatorParams);
		}
	}

	/**
	 * 设置指示器的触摸监听
	 * 
	 * @param iv_indicator
	 */
	private void setIndicatorTouchListener(ImageView indicator) {
		indicator.setOnTouchListener(new OnTouchListener() {
			
			/** 手指按下的X点坐标，用来移动指示器的 */
			private int startX;
			
			/** 手指按下的Y点坐标，用来移动指示器的 */
			private int startY;
			
			/** 时间戳，用来判断是否是双击 */
			private long firstTime = 0;
			
			/** 坐标戳X，用来判断是否是单击 */
			private int singleX;
			
			/** 坐标戳Y，用来判断是否是单击 */
			private int singleY;
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mIndicatorView.setImageDrawable(getResources().getDrawable(R.drawable.iv_indicator_pressed));
					startX = singleX = (int) event.getRawX();
					startY = singleY = (int) event.getRawY();
					Logger.i(getClass().getName() + "*****" + "ACTION_DOWN:startX:"+startX+"   startY:"+startY);
					break;
				case MotionEvent.ACTION_MOVE:
//					Logger.i(getClass().getName() + "*****" + "ACTION_MOVE");
					int x = (int) event.getRawX();
					int y = (int) event.getRawY();
					int dx = x - startX;
					int dy = y - startY;
					indicatorParams.x += dx;
					indicatorParams.y += dy;
					wm.updateViewLayout(mIndicatorView, indicatorParams);
					
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();

					break;
				case MotionEvent.ACTION_UP:
//					Logger.i(getClass().getName() + "*****" + "ACTION_UP");
					mIndicatorView.setImageDrawable(getResources().getDrawable(R.drawable.iv_indicator_normal));
					int upX = (int) event.getRawX();
					int upY = (int) event.getRawY();
					Logger.i(getClass().getName() + "*****" + "singleX:"+singleX+"  singleY:"+singleY+"   upX:"+upX+"    upY"+upY);
					if (isSingleClick(singleX, singleY, upX, upY)) {
						Logger.i(getClass().getName() + "*****" + "ACTION_UP:and is single click");
						handler.sendEmptyMessageDelayed(SINGLE_CLICK, 450);
					}
					long nowTime = System.currentTimeMillis();
					if (isDoubleClick(firstTime, nowTime)) { //做双击的事
						Logger.i(getClass().getName() + "*****" + "double click");
						goHome();
					};
					break;
				}
				return true;
			}
			private boolean isSingleClick(int x1, int y1, int x2, int y2) {
				
				return (Math.abs(x1 - x2) < 5 && Math.abs(y1 - y2) < 5 ) ;
			}
			
			/** 回到桌面 */
			private void goHome() {
				Intent goHomeIntent =new Intent();
				goHomeIntent.setAction(Intent.ACTION_MAIN);
				goHomeIntent.addCategory(Intent.CATEGORY_HOME);
				goHomeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(goHomeIntent);
			}
			
			/** 判断是不是双击 */
			private boolean isDoubleClick(long firstClickTime, long nowClickTime) {
				boolean flag = false;
				if (nowClickTime - firstClickTime < 420) { //double click
					firstTime = 0;
					handler.removeMessages(SINGLE_CLICK);
					flag = true ;
				}else {  //single click
						
					firstTime =nowClickTime;
				}
				return flag;
			}
		});
	}

	@Override
	public void onStart(Intent intent, int startId) {
		mWindowWidth = wm.getDefaultDisplay().getWidth();
		mWindowHeight = wm.getDefaultDisplay().getHeight();
		Logger.i(getClass().getName() + "*****" + "mWindowWidth: "+mWindowWidth+":::::mWindowHeight:"+mWindowHeight);
		super.onStart(intent, startId);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_control_exit:
			Logger.i(getClass().getName() + "*****" + "我被点击了："+v.getId());
			setContentViewShowAndHide(mControlPlane);
			break;
		case 1000:
			Logger.i(getClass().getName() + "*****" + "我被点击了："+v.getId());
			Intent videoPlayerIntent =new Intent(getApplicationContext(), ZPVideo.class);
			videoPlayerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(videoPlayerIntent);
			Logger.i(getClass().getName() + "*****" + "startActivity:Music.class");
			setContentViewShowAndHide(mControlPlane);
			break;
		case 1001:
			Logger.i(getClass().getName() + "*****" + "我被点击了："+v.getId());
			Intent musicPlayerIntent = new Intent(getApplicationContext(), ZPMusic.class);
			musicPlayerIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(musicPlayerIntent);
			Logger.i(getClass().getName() + "*****" + "startActivity:Music.class");
			setContentViewShowAndHide(mControlPlane);
			break;
		}
	}
	
	/** 设置控制面板的显示和隐藏 */
	public void setContentViewShowAndHide(View view){
		if (view != null) {
			if (view.getVisibility() == View.GONE) {
				view.setVisibility(View.VISIBLE);
			} else {
				view.setVisibility(View.GONE);
			}
			int[] controlPlaneLocation = getControlPlaneLocation();
			indicatorGotoOrigin(controlPlaneLocation);
		}
	}
	
	/**
	 * return 数组 [0]:表示x坐标 
	 * 		  数组 [1]:表示y坐标
	 * 获取指示器的位置*/
	private int[] getControlPlaneLocation() {
		/*
		int mControlPlaneLocationLeft = mControlPlane.getLeft();
		int mControlPlaneLocationRight = mControlPlane.getRight();
		int mControlPlaneLocationTop = mControlPlane.getTop();
		int mControlPlaneLocationBottom = mControlPlane.getBottom();
		int mControlPlaneLocationX = (int) mControlPlane.getX();
		int mControlPlaneLocationY = (int) mControlPlane.getY();
		int mControlPlaneLocationWidth = mControlPlane.getLayoutParams().width;
		int mControlPlaneLocationHeight = mControlPlane.getLayoutParams().height;
		
		Logger.i(getClass().getName() + "*****" + +mControlPlaneLocationX+",,"+mControlPlaneLocationY+"::::"+mControlPlaneLocationLeft+","+mControlPlaneLocationRight+","+mControlPlaneLocationTop+","+mControlPlaneLocationBottom+","+mControlPlaneLocationWidth+","+mControlPlaneLocationHeight);
		*/
		int sIndicatorWidth = mIndicatorView.getWidth();
		int sIndicatorHeight = mIndicatorView.getHeight();
		Logger.i(getClass().getName() + "*****" + "sIndicatorWidth:"+sIndicatorWidth+":sIndicatorHeight"+sIndicatorHeight);
		int locationX = mConfigSP.getInt(getString(R.string.config_preference_indicator_location_x_key), (mWindowWidth/2)-sIndicatorWidth/2-15);
		int locationY = mConfigSP.getInt(getString(R.string.config_preference_indicator_location_y_key), (-mWindowHeight/2)-sIndicatorHeight/2+90);
		int[] indicator_location =new int[]{locationX , locationY};
		Logger.i(getClass().getName() + "*****" + "indicator_location[0]:"+indicator_location[0]+":indicator_location[1]:"+indicator_location[1]);
		return indicator_location ;
	}

	/**重置指示器的位置，避免被ContentPlane挡住
	 * @param controlPlaneLocation */
	private void indicatorGotoOrigin(int[] controlPlaneLocation) {
		if (mIndicatorView != null && controlPlaneLocation != null && controlPlaneLocation.length ==2) {
			indicatorParams.x =controlPlaneLocation[0];
			indicatorParams.y =controlPlaneLocation[1];
			wm.updateViewLayout(mIndicatorView, indicatorParams);
		}
	}
	private class ControlPlaneAdapter extends BaseAdapter {
		private String[] mListName ;
		public ControlPlaneAdapter(String[] mControlPlaneArrays){
			mListName = mControlPlaneArrays ;
		};
		@Override
		public int getCount() {
			
			return mListName.length;
		}

		@Override
		public Object getItem(int position) {
			
			return mListName[position];
		}

		@Override
		public long getItemId(int position) {
			
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView tv_name = new TextView(getApplicationContext());
			tv_name.setTextSize(20);
			android.widget.AbsListView.LayoutParams lp = new android.widget.AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			tv_name.setGravity(Gravity.CENTER);
			tv_name.setPadding(0, 10, 0, 10);
			tv_name.setLayoutParams(lp);
			tv_name.setText(mListName[position]);
			tv_name.setTextColor(Color.BLUE);
			tv_name.setId(LISTVIEWID[position]);
			tv_name.setClickable(true);
			tv_name.setOnClickListener(SuperView.this);
			return tv_name;
		}
		
	}
}
