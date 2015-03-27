

package com.sjtu.icarer.authenticator;

import static android.accounts.AccountManager.KEY_AUTHTOKEN;
import static com.sjtu.icarer.common.constant.Constants.Auth.AUTHTOKEN_TYPE;
import static com.sjtu.icarer.common.constant.Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE;
import static com.sjtu.icarer.common.constant.Constants.Auth.ICARER_ACCOUNT_USER;

import java.io.IOException;

import com.google.gson.Gson;
import com.sjtu.icarer.model.User;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AccountsException;
import android.app.Activity;
import android.os.Bundle;

/**
 * Bridge class that obtains a API key for the currently configured account
 */
public class AccountDataProvider {

    private AccountManager accountManager;
    private Gson gson;
    public AccountDataProvider(AccountManager accountManager, Gson gson) {
        this.accountManager = accountManager;
        this.gson = gson;
    }

    /**
     * This call blocks, so shouldn't be called on the UI thread.
     * This call is what makes the login screen pop up. If the user has
     * not logged in there will no accounts in the {@link android.accounts.AccountManager}
     * and therefore the Activity that is referenced in the
     * {@link com.sjtu.icarer.authenticator.IcarerAccountAuthenticator} will get started.
     * If you want to remove the authentication then you can comment out the code below and return a string such as
     * "foo" and the authentication process will not be kicked off. Alternatively, you can remove this class
     * completely and clean up any references to the authenticator.
     *
     *
     * @return API key to be used for authorization with a
     * {@link com.sjtu.icarer.service.IcarerService} instance
     * @throws AccountsException
     * @throws IOException
     */
    public String getAuthKey(final Activity activity) throws AccountsException, IOException {
        final AccountManagerFuture<Bundle> accountManagerFuture
                = accountManager.getAuthTokenByFeatures(BOOTSTRAP_ACCOUNT_TYPE,
                AUTHTOKEN_TYPE, new String[0], activity, null, null, null, null);
        
        return accountManagerFuture.getResult().getString(KEY_AUTHTOKEN);
    }
    
    public User getUserData() throws AccountsException{
    	
        Account  account = accountManager.getAccountsByType(AUTHTOKEN_TYPE)[0];
        String userData = accountManager.getUserData(account,ICARER_ACCOUNT_USER);
        /*gson provided in dagger has to be LOWER_CASE_WITH_UNDERSCORES,
         * userData string stored already is CAMEL CASE
         * TODO so should remove the private class variable
         */
        gson = new Gson();
        User userObject = gson.fromJson(userData, User.class);
        
        return userObject;
    }
}
