package com.sjtu.icarer.service;

import java.io.IOException;

import retrofit.RestAdapter;
import android.accounts.AccountsException;
import android.app.Activity;

import com.sjtu.icarer.authenticator.AccountDataProvider;

public class IcarerServiceProvider {
    private RestAdapter restAdapter;
    private AccountDataProvider accountDataProvider;
    
    public IcarerServiceProvider(RestAdapter restAdapter, AccountDataProvider accountDataProvider) {
    	this.restAdapter = restAdapter;
        this.accountDataProvider = accountDataProvider;
	}
    
    /**
     * Get service for configured accountDataProvider 
     * <p/>
     * This method gets an auth key and so it blocks and shouldn't be called on the main thread.
     *
     * @return icarer service
     * @throws IOException
     * @throws AccountsException
     */
    public IcarerService getService(final Activity activity)
            throws IOException, AccountsException {
        // The call to keyProvider.getAuthKey(...) is what initiates the login screen. Call that now.
    	accountDataProvider.getAuthKey(activity);
    	//User userData = accountDataProvider.getUserData();
        // TODO: See how that affects the bootstrap service.
        return new IcarerService(restAdapter, accountDataProvider);
    }
}
