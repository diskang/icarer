package com.sjtu.icarer.thread;

import javax.inject.Inject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sjtu.icarer.Injector;
import com.sjtu.icarer.common.constant.Constants;
import com.sjtu.icarer.events.RefreshScreenEvent;
import com.sjtu.icarer.ui.HomeActivity;
import com.squareup.otto.Bus;

public class UpdateReceiver extends BroadcastReceiver{
    //private static final String TAG = "UpdateReceiver";
	private Context mcontext;
	@Inject Bus eventBus;
	
	public UpdateReceiver(){
		super();
		Injector.inject(this);
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		mcontext = context;
		/*Toast msg = Toast.makeText(context,intent.getAction(), Toast.LENGTH_SHORT);
        msg.show();*/

		String actionContent = intent.getAction();
		if (Constants.ACTION_UPDATE_INFO.equals(actionContent)) {	
			
			intent = new Intent(mcontext, HomeActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
			//TODO how to finish old activities
			mcontext.startActivity(intent);
			
		}else if(Intent.ACTION_SCREEN_ON.equals(actionContent)){
			eventBus.post(new RefreshScreenEvent());
		} else if(Intent.ACTION_BOOT_COMPLETED.equals(actionContent)){
//			LogUtils.d("boot completed");
//			intent = new Intent(mcontext, MainActivity.class);
//			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			//TODO how to finish old activities
//			mcontext.startActivity(intent);
			mcontext.startService(new Intent("com.sjtu.icarer.thread.AUTORUN"));
		}
		
	}

	
}
