

package com.sjtu.icarer.ui;

import javax.inject.Inject;

import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Window;
import butterknife.ButterKnife;

import com.sjtu.icarer.Injector;
import com.sjtu.icarer.common.utils.LogUtils;
import com.sjtu.icarer.core.app.PreferenceManager;
import com.sjtu.icarer.core.utils.SafeAsyncTask;
import com.sjtu.icarer.service.IcarerService;
import com.sjtu.icarer.service.IcarerServiceProvider;
import com.sjtu.icarer.ui.login.LoginActivity;


/**
 * Initial activity for the application.
 *
 * If you need to remove the authentication from the application please see
 * {@link com.sjtu.icarer.authenticator.AccountDataProvider#getAuthKey(android.app.Activity)}
 */
public class MainActivity extends IcarerFragmentActivity {

    @Inject protected IcarerServiceProvider icarerServiceProvider;
    @Inject protected PreferenceManager preferenceProvider;
    
    private boolean userHasAuthenticated = false;
    private Integer areaId;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        super.onCreate(savedInstanceState);

        
        //setContentView(R.layout.activity_welcome);
            
        Injector.inject(this);
        // Set up navigation drawer
        areaId = preferenceProvider.getAreaId();

        checkAuth();

    }
    @Override
    public void setContentView(final int layoutResId) {
        super.setContentView(layoutResId);
        // View injection with Butterknife
        ButterKnife.inject(this);
    }

 

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }


    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        
    }


    private void initScreen() {
        if (userHasAuthenticated) {
            LogUtils.d("userHasAuthenticated");
            final Intent i;
            if(areaId.equals(0)){
            	i = new Intent(this, SetupActivity.class); 
            }else{
            	i = new Intent(this, LoginActivity.class);
            }
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }

    }

    private void checkAuth() {
        new SafeAsyncTask<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                final IcarerService svc = icarerServiceProvider.getService(MainActivity.this);
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
                userHasAuthenticated = true;
                initScreen();
            }
        }.execute();
    }


}
