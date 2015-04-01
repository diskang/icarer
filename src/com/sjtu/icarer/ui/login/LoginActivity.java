package com.sjtu.icarer.ui.login;


import java.util.List;

import javax.inject.Inject;

import android.accounts.OperationCanceledException;
import android.os.Bundle;

import com.sjtu.icarer.R;
import com.sjtu.icarer.common.utils.LogUtils;
import com.sjtu.icarer.common.utils.view.ToastUtils;
import com.sjtu.icarer.core.utils.PreferenceManager;
import com.sjtu.icarer.core.utils.SafeAsyncTask;
import com.sjtu.icarer.model.Carer;
import com.sjtu.icarer.model.Elder;
import com.sjtu.icarer.persistence.DbManager;
import com.sjtu.icarer.service.IcarerService;
import com.sjtu.icarer.service.IcarerServiceProvider;
import com.sjtu.icarer.ui.IcarerFragmentActivity;

public class LoginActivity extends IcarerFragmentActivity {
	@Inject IcarerServiceProvider icarerServiceProvider;
	@Inject PreferenceManager preferenceProvider;
	@Inject DbManager dbManager;
	private int areaId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_carer_login);

		areaId = preferenceProvider.getAreaId();
		getElders();
	
	}
	
    private void getElders() {
//    	new ListElderTask(this,areaId,icarerServiceProvider){
//    		@Override
//            protected void onSuccess(List<Elder> elders) throws Exception {
//                super.onSuccess(elders);
//
//                LogUtils.d("elder fetched");
//            }
//    	}.start();
        new SafeAsyncTask<List<Elder>>() {

            @Override
            public List<Elder> call() throws Exception {
                //final IcarerService svc = icarerServiceProvider.getService(LoginActivity.this);
                List<Elder> elders = dbManager.getElders(false);
                return elders;
                
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                if (e instanceof OperationCanceledException) {
                    // User cancelled the authentication process (back button, etc).
                    // Since auth could not take place, lets finish this activity.
                    finish();
                }
            }

            @Override
            protected void onSuccess(final List<Elder> elders) throws Exception {
                super.onSuccess(elders);
                ToastUtils.show(LoginActivity.this, "get elders ok");
                for(Elder elder: elders){
                	getCarerAfterElder(elder);
                }
            }
        }.execute();
    }
	
    private void getCarerAfterElder(final Elder elder){
    	new SafeAsyncTask<List<Carer>>() {

            @Override
            public List<Carer> call() throws Exception {
                final IcarerService svc = icarerServiceProvider.getService(LoginActivity.this);
                List<Carer> carers = dbManager.getCarerByElder(elder, true);
                return carers;
                
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                if (e instanceof OperationCanceledException) {
                	LogUtils.d("get carer failed");
                    finish();
                }
            }

            @Override
            protected void onSuccess(final List<Carer> carers) throws Exception {
                super.onSuccess(carers);
                ToastUtils.show(LoginActivity.this, "get carer success");
            }
        }.execute();
    }
    
}
