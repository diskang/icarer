package com.sjtu.icarer.thread;

import javax.inject.Inject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.sjtu.icarer.Injector;
import com.sjtu.icarer.common.config.Prefer;
import com.sjtu.icarer.common.constant.Constants;
import com.sjtu.icarer.events.RefreshScreenEvent;
import com.sjtu.icarer.ui.HomeActivity;
import com.squareup.otto.Bus;

public class UpdateReceiver extends BroadcastReceiver{
    //private static final String TAG = "UpdateReceiver";
	private Context mcontext;
	private Prefer prefer;
	@Inject Bus eventBus;
	
	public UpdateReceiver(){
		super();
		Injector.inject(this);
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		mcontext = context;
		Toast msg = Toast.makeText(context,intent.getAction(), Toast.LENGTH_SHORT);
        msg.show();

		prefer = new Prefer(mcontext);
		if (Constants.ACTION_UPDATE_INFO.equals(intent.getAction())) {	
			
			intent = new Intent(mcontext, HomeActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
			//TODO how to finish old activities
			mcontext.startActivity(intent);
			
		}else if(Intent.ACTION_SCREEN_ON.equals(intent.getAction())){
			
			if (!prefer.updatedTimeStamp()){
				updateCarer();
			}
			eventBus.post(new RefreshScreenEvent());
		}
		
	}
	
	private void updateCarer(){
		
	}
	
}
