package com.sjtu.icarer.thread;

import java.util.Calendar;

import com.sjtu.icarer.common.constant.Constants;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class UpdateService extends Service{
	private static final String TAG = "UpdateService";
	
	private static final int UPDATE_ID = 42112;
	private int UPDATE_START_HOUR = 23;
	private int UPDATE_START_MINUTE =59 ;
	
	
	@Override
    public void onCreate() {
		IntentFilter filter = new IntentFilter(Intent.ACTION_BOOT_COMPLETED);
	    filter.addAction(Intent.ACTION_USER_PRESENT);
	    filter.addAction(Intent.ACTION_SCREEN_ON);
	    BroadcastReceiver mReceiver = new UpdateReceiver();
	    registerReceiver(mReceiver, filter);
	    setAlarm(this,Constants.ACTION_UPDATE_INFO,UPDATE_START_HOUR,UPDATE_START_MINUTE,UPDATE_ID);
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Received start id " + startId + ": " + intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }
	
	@Override
    public void onDestroy() {
		super.onDestroy();
		//TODO let it be a choice
		cancelAlarm(this, Constants.ACTION_UPDATE_INFO, UPDATE_ID);
	}
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
    private void setAlarm(Context context,String actionString,int hour,int minute,int requestCode){
		//创建Intent对象，action指向广播接收类
		Intent intent = new Intent(actionString);
        // intent.putExtra("msg","haha");
        //创建PendingIntent对象封装Intent，由于是使用广播，注意使用getBroadcast方法
        PendingIntent pi = PendingIntent.getBroadcast(context,requestCode,intent,PendingIntent.FLAG_UPDATE_CURRENT);
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0); 
//      really necessary??
//		if(calendar.getTimeInMillis()<System.currentTimeMillis()){//如果时间已过，加一天
//			calendar.add(Calendar.DAY_OF_YEAR, 1);
//		}			
		//设置闹钟从指定时间开始，每隔一天执行一次PendingIntent对象，注意第一个参数与第二个参数的关系
		AlarmManager mAlarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				calendar.getTimeInMillis(),
//					System.currentTimeMillis(),
				AlarmManager.INTERVAL_DAY
//					AlarmManager.INTERVAL_HALF_HOUR
				,pi);
		Log.d(TAG,actionString+" set up");
	}
    private void cancelAlarm(Context context, String actionString, int requestCode){
    	Intent intent = new Intent(actionString);
    	PendingIntent pi = PendingIntent.getBroadcast(context,requestCode,intent,PendingIntent.FLAG_UPDATE_CURRENT);
    	AlarmManager mAlarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
    	mAlarmManager.cancel(pi);
    }
    
}
