package com.sjtu.icarer.ui;

import static com.sjtu.icarer.events.AreaUndoEvent.UNDO_ALL;

import java.util.List;

import javax.inject.Inject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

import com.sjtu.icarer.Injector;
import com.sjtu.icarer.R;
import com.sjtu.icarer.R.string;
import com.sjtu.icarer.events.AreaUndoEvent;
import com.sjtu.icarer.events.SetupSubmitEvent;
import com.sjtu.icarer.events.TaskCancelEvent;
import com.sjtu.icarer.ui.setting.AreaPreferenceFragment;
import com.squareup.otto.Bus;

public class SetupActivity extends PreferenceActivity {
	
	@Inject protected LayoutInflater layoutInflater;
	@Inject protected Bus eventBus;
	
	@Override 
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState); 
        Injector.inject(this);
        
        if(hasHeaders()){  //如有header，则在最下面加一个button。本例无论平板还是phone，都返回true  
        	//第一个参数为xml文件中view的id，第二个参数为此view的父组件，可以为null，android会自动寻找它是否拥有父组件  
        	@SuppressLint("InflateParams")
        	LinearLayout view = (LinearLayout)layoutInflater.inflate(R.layout.view_setup_bottom, null);
            setListFooter(view);                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     
        } 
        ButterKnife.inject(this);
	}
	@Override
    public void onBuildHeaders(List<Header> target){
        loadHeadersFromResource(R.xml.headers_preference, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return AreaPreferenceFragment.class.getName().equals(fragmentName);
    }
    
    @Optional
    @OnClick(R.id.btn_setup_submit)
    public void setupSubmit(Button button){
    	eventBus.post(new SetupSubmitEvent());
    }
    @Optional
    @OnClick(R.id.btn_setup_undo)
    public void setupUndo(Button button){
    	eventBus.post(new AreaUndoEvent(UNDO_ALL));
    }
	
    @Override
    protected void onResume() {
        super.onResume();
        eventBus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        eventBus.unregister(this);
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getText(string.message_fetch_data));
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(final DialogInterface dialog) {
                eventBus.post(new TaskCancelEvent());
            }
        });
        return dialog;
    }
    
}
