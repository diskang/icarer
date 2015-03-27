package com.sjtu.icarer.ui.login;


import javax.inject.Inject;

import android.accounts.OperationCanceledException;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.sjtu.icarer.Injector;
import com.sjtu.icarer.R;
import com.sjtu.icarer.common.utils.Ln;
import com.sjtu.icarer.common.utils.SafeAsyncTask;
import com.sjtu.icarer.common.utils.view.ToastUtils;
import com.sjtu.icarer.model.Area;
import com.sjtu.icarer.service.IcarerService;
import com.sjtu.icarer.service.IcarerServiceProvider;

public class LoginActivity extends ActionBarActivity {
	@InjectView(R.id.toolbar) protected Toolbar toolbar;
	@Inject IcarerServiceProvider icarerServiceProvider;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_carer_login);
		ButterKnife.inject(this);
		Injector.inject(this);
		
		toolbar.setLogo(R.drawable.ic_launcher);
		// Title
		toolbar.setTitle("—HOUSECARE— ");
		// Sub Title
		toolbar.setSubtitle(" 沪上养老专业品牌");
		 
		setSupportActionBar(toolbar);
		
		checkAuth();
	
	}
	
    private void checkAuth() {
        new SafeAsyncTask<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                final IcarerService svc = icarerServiceProvider.getService(LoginActivity.this);
                Area area =svc.getArea(0);
                Ln.d(area.toString());
                return svc != null;
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
            protected void onSuccess(final Boolean hasAuthenticated) throws Exception {
                super.onSuccess(hasAuthenticated);
                ToastUtils.show(LoginActivity.this, "Login activity");
            }
        }.execute();
    }
	
    
}
