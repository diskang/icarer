package com.sjtu.icarer.ui;

import java.util.List;

import javax.inject.Inject;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.sjtu.icarer.common.utils.view.ToastUtils;
import com.sjtu.icarer.common.utils.view.Toaster;
import com.sjtu.icarer.core.utils.SafeAsyncTask;
import com.sjtu.icarer.events.AreaUndoEvent;
import com.sjtu.icarer.service.IcarerService;
import com.sjtu.icarer.service.IcarerServiceProvider;
import com.sjtu.icarer.ui.login.LoginActivity;
import com.sjtu.icarer.ui.setting.AreaPreferenceFragment;
import com.squareup.otto.Bus;

import static com.sjtu.icarer.events.AreaUndoEvent.UNDO_ALL;

public class SetupActivity extends PreferenceActivity {
	@Inject protected IcarerServiceProvider icarerServiceProvider;
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
    	Toaster.showLong(this,"redo");
    	//do sth put into preference
    	final Intent i = new Intent(this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
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
    
//    private void getBuilding(){
//    	new SafeAsyncTask<Boolean>() {
//
//            @Override
//            public Boolean call() throws Exception {
//                final IcarerService svc = icarerServiceProvider.getService(SetupActivity.this);
//                svc.getAreas(1,0);
//                Area area =svc.getArea(0);
//                Ln.d(area.toString());
//                return svc != null;
//            }
//
//            @Override
//            protected void onException(final Exception e) throws RuntimeException {
//                super.onException(e);
//                if (e instanceof OperationCanceledException) {
//                    // User cancelled the authentication process (back button, etc).
//                    // Since auth could not take place, lets finish this activity.
//                    finish();
//                }
//            }
//
//            @Override
//            protected void onSuccess(final Boolean hasAuthenticated) throws Exception {
//                super.onSuccess(hasAuthenticated);
//                ToastUtils.show(SetupActivity.this, "Setup activity");
//            }
//        }.execute();
//    }
}
