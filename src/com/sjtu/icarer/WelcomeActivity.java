package com.sjtu.icarer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.sjtu.icarer.common.config.Prefer;


public class WelcomeActivity extends Activity {

	private final int WEL_DISPLAY_LENGHT = 1000;
	
	//private static final String TAG = "WelcomeActivity";
	
	
	private String roomNumber;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		
		Prefer prefer = new Prefer(this);
		String geroId = prefer.getGeroId();
		roomNumber = prefer.getRoomNumber();
		startService(new Intent("com.sjtu.thread.AUTORUN"));
		if(geroId==null||roomNumber==null||geroId.isEmpty()||roomNumber.isEmpty()){
			newActivity(0);
		}
		else{
			newActivity(1);
		}
		
	}
	
	private void newActivity(final int bool){
		// bool=0: login, else: room
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent intent;
				if(bool==0){
					intent = new Intent(WelcomeActivity.this, SettingActivity.class);
				}
				else{
					intent = new Intent(WelcomeActivity.this, HomeActivity.class);
//					intent = new Intent(WelcomeActivity.this, CatchExceptionLogActivity.class);
				}
				WelcomeActivity.this.startActivity(intent);
				WelcomeActivity.this.finish();
			}
		}, WEL_DISPLAY_LENGHT);
	}
	

}
