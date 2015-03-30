package com.sjtu.icarer.ui.login;


import java.util.List;

import javax.inject.Inject;

import android.accounts.OperationCanceledException;
import android.os.Bundle;

import com.sjtu.icarer.R;
import com.sjtu.icarer.common.utils.LogUtils;
import com.sjtu.icarer.common.utils.view.ToastUtils;
import com.sjtu.icarer.core.app.PreferenceManager;
import com.sjtu.icarer.core.setup.ListElderTask;
import com.sjtu.icarer.core.utils.SafeAsyncTask;
import com.sjtu.icarer.model.Elder;
import com.sjtu.icarer.service.IcarerService;
import com.sjtu.icarer.service.IcarerServiceProvider;
import com.sjtu.icarer.ui.IcarerFragmentActivity;

public class LoginActivity extends IcarerFragmentActivity {
	@Inject IcarerServiceProvider icarerServiceProvider;
	@Inject PreferenceManager preferenceProvider;
	
	private int areaId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_carer_login);

		areaId = preferenceProvider.getAreaId();
		checkAuth();
	
	}
	
    private void checkAuth() {
    	new ListElderTask(this,areaId,icarerServiceProvider){
    		@Override
            protected void onSuccess(List<Elder> elders) throws Exception {
                super.onSuccess(elders);

                LogUtils.d("elder fetched");
            }
    	}.start();
//        new SafeAsyncTask<Boolean>() {
//
//            @Override
//            public Boolean call() throws Exception {
//                final IcarerService svc = icarerServiceProvider.getService(LoginActivity.this);
//                List<Elder> elders= svc.getElderByArea(areaId);
//                //List<Area> areas =svc.getAreas(1,0);
////                Ln.d(area.toString());
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
//                ToastUtils.show(LoginActivity.this, "Login activity");
//            }
//        }.execute();
    }
	
    
}
