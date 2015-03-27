
package com.sjtu.icarer.authenticator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import static android.accounts.AccountManager.ACTION_AUTHENTICATOR_INTENT;

/**
 * Authenticator service that returns a subclass of AbstractAccountAuthenticator in onBind().
 */
public class AccountAuthenticatorService extends Service {

    private static IcarerAccountAuthenticator authenticator = null;

    @Override
    public IBinder onBind(final Intent intent) {
        if (intent != null && ACTION_AUTHENTICATOR_INTENT.equals(intent.getAction())) {
            return getAuthenticator().getIBinder();
        }
        return null;
    }

    private IcarerAccountAuthenticator getAuthenticator() {
        if (authenticator == null) {
            authenticator = new IcarerAccountAuthenticator(this);
        }
        return authenticator;
    }
}