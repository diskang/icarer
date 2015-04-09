package com.sjtu.icarer.authenticator;

import static android.R.layout.simple_dropdown_item_1line;
import static android.accounts.AccountManager.KEY_ACCOUNT_NAME;
import static android.accounts.AccountManager.KEY_ACCOUNT_TYPE;
import static android.accounts.AccountManager.KEY_AUTHTOKEN;
import static android.accounts.AccountManager.KEY_BOOLEAN_RESULT;
import static android.view.KeyEvent.ACTION_DOWN;
import static android.view.KeyEvent.KEYCODE_ENTER;
import static android.view.inputmethod.EditorInfo.IME_ACTION_DONE;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit.RetrofitError;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.sjtu.icarer.Injector;
import com.sjtu.icarer.R;
import com.sjtu.icarer.R.id;
import com.sjtu.icarer.R.layout;
import com.sjtu.icarer.R.string;
import com.sjtu.icarer.common.constant.Constants;
import com.sjtu.icarer.common.utils.Ln;
import com.sjtu.icarer.common.utils.view.Toaster;
import com.sjtu.icarer.core.utils.Named;
import com.sjtu.icarer.core.utils.SafeAsyncTask;
import com.sjtu.icarer.events.UnAuthorizedErrorEvent;
import com.sjtu.icarer.model.User;
import com.sjtu.icarer.service.IcarerService;
import com.sjtu.icarer.ui.view.TextWatcherAdapter;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * Activity to authenticate the user against an API (example API on Parse.com)
 */
public class IcarerAuthenticatorActivity extends ActionBarAccountAuthenticatorActivity {

    /**
     * PARAM_CONFIRM_CREDENTIALS
     */
    public static final String PARAM_CONFIRM_CREDENTIALS = "confirmCredentials";

    /**
     * PARAM_PASSWORD
     */
    public static final String PARAM_PASSWORD = "password";

    /**
     * PARAM_USERNAME
     */
    public static final String PARAM_USERNAME = "username";

    /**
     * PARAM_AUTHTOKEN_TYPE
     */
    public static final String PARAM_AUTHTOKEN_TYPE ="authtokenType";


    private AccountManager accountManager;

    @Inject @Named("unAuth")IcarerService icarerService;
    @Inject Bus bus;

    @InjectView(id.et_username) protected AutoCompleteTextView usernameText;
    @InjectView(id.et_password) protected EditText passwordText;
    @InjectView(id.b_signin) protected Button signInButton;

    private final TextWatcher watcher = validationTextWatcher();

    private SafeAsyncTask<Boolean> authenticationTask;
    private String authToken;
    private String authTokenType;

    /**
     * If set we are just checking that the user knows their credentials; this
     * doesn't cause the user's password to be changed on the device.
     */
    private Boolean confirmCredentials = false;

    private String username;

    private String password;
    /**
     * store geroId in account
     */
    private User loginUser;
    
    /**
     * In this instance the token is simply the sessionId returned from Parse.com. This could be a
     * oauth token or some other type of timed token that expires/etc. We're just using the parse.com
     * sessionId to prove the example of how to utilize a token.
     */
    private String token;

    /**
     * Was the original caller asking for an entirely new account?
     */
    protected boolean requestNewAccount = false;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        Injector.inject(this);

        accountManager = AccountManager.get(this);

        final Intent intent = getIntent();
        username = intent.getStringExtra(PARAM_USERNAME);
        authTokenType = intent.getStringExtra(PARAM_AUTHTOKEN_TYPE);
        confirmCredentials = intent.getBooleanExtra(PARAM_CONFIRM_CREDENTIALS, false);

        requestNewAccount = username == null;

        setContentView(layout.act_admin_login);

        ButterKnife.inject(this);

        usernameText.setAdapter(new ArrayAdapter<String>(this,
                simple_dropdown_item_1line, userUsernameAccounts()));

        passwordText.setOnKeyListener(new OnKeyListener() {

            public boolean onKey(final View v, final int keyCode, final KeyEvent event) {
                if (event != null && ACTION_DOWN == event.getAction()
                        && keyCode == KEYCODE_ENTER && signInButton.isEnabled()) {
                    handleLogin(signInButton);
                    return true;
                }
                return false;
            }
        });

        passwordText.setOnEditorActionListener(new OnEditorActionListener() {

            public boolean onEditorAction(final TextView v, final int actionId,
                                          final KeyEvent event) {
                if (actionId == IME_ACTION_DONE && signInButton.isEnabled()) {
                    handleLogin(signInButton);
                    return true;
                }
                return false;
            }
        });

        usernameText.addTextChangedListener(watcher);
        passwordText.addTextChangedListener(watcher);

        final TextView signUpText = (TextView) findViewById(id.tv_signup);
        signUpText.setMovementMethod(LinkMovementMethod.getInstance());
        signUpText.setText(Html.fromHtml(getString(string.signup_link)));
    }

    private List<String> userUsernameAccounts() {
        final Account[] accounts = accountManager.getAccountsByType("com.google");
        final List<String> usernameAddresses = new ArrayList<String>(accounts.length);
        for (final Account account : accounts) {
            usernameAddresses.add(account.name);
        }
        return usernameAddresses;
    }

    private TextWatcher validationTextWatcher() {
        return new TextWatcherAdapter() {
            public void afterTextChanged(final Editable gitDirEditText) {
                updateUIWithValidation();
            }

        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);
        updateUIWithValidation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    private void updateUIWithValidation() {
        final boolean populated = populated(usernameText) && populated(passwordText);
        signInButton.setEnabled(populated);
    }

    private boolean populated(final EditText editText) {
        return editText.length() > 0;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getText(string.message_signing_in));
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(final DialogInterface dialog) {
                if (authenticationTask != null) {
                    authenticationTask.cancel(true);
                }
            }
        });
        return dialog;
    }

    @Subscribe
    public void onUnAuthorizedErrorEvent(UnAuthorizedErrorEvent unAuthorizedErrorEvent) {
        // Could not authorize for some reason.
    	Toaster.showLong(IcarerAuthenticatorActivity.this, R.string.message_bad_credentials);
    }

    /**
     * Handles onClick event on the Submit button. Sends username/password to
     * the server for authentication.
     * <p/>
     * Specified by android:onClick="handleLogin" in the layout xml
     *
     * @param view
     */
    public void handleLogin(final View view) {
        if (authenticationTask != null) {
            return;
        }

        if (requestNewAccount) {
            username = usernameText.getText().toString();
        }

        password = passwordText.getText().toString();
        showProgress();

        authenticationTask = new SafeAsyncTask<Boolean>() {
            public Boolean call() throws Exception {

//                final String query = String.format("%s=%s&%s=%s",
//                        PARAM_USERNAME, , PARAM_PASSWORD, password);

                User loginResponse = icarerService.authenticate(username, password);
                loginUser = loginResponse;
                token = loginResponse.getDigest();

                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                // Retrofit Errors are handled inside of the {
                if(!(e instanceof RetrofitError)) {
                    final Throwable cause = e.getCause() != null ? e.getCause() : e;
                    if(cause != null) {
                        Toaster.showLong(IcarerAuthenticatorActivity.this, cause.getMessage());
                    }
                }
            }

            @Override
            public void onSuccess(final Boolean authSuccess) {
                onAuthenticationResult(authSuccess);
            }

            @Override
            protected void onFinally() throws RuntimeException {
                hideProgress();
                authenticationTask = null;
            }
        };
        authenticationTask.execute();
    }

    /**
     * Called when response is received from the server for confirm credentials
     * request. See onAuthenticationResult(). Sets the
     * AccountAuthenticatorResult which is sent back to the caller.
     *
     * @param result
     */
    protected void finishConfirmCredentials(final boolean result) {
        final Account account = new Account(username, Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE);
        accountManager.setPassword(account, password);

        final Intent intent = new Intent();
        intent.putExtra(KEY_BOOLEAN_RESULT, result);
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Called when response is received from the server for authentication
     * request. See onAuthenticationResult(). Sets the
     * AccountAuthenticatorResult which is sent back to the caller. Also sets
     * the authToken in AccountManager for this account.
     */

    protected void finishLogin() {
        final Account account = new Account(username, Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE);

        if (requestNewAccount) {
            accountManager.addAccountExplicitly(account, password, null);
            //set digest value to authToken
        	accountManager.setAuthToken(account, authTokenType, token);
        	//store user data, GERO ID,...
        	accountManager.setUserData(account, Constants.Auth.ICARER_ACCOUNT_USER, loginUser.toString());
        } else {
            accountManager.setPassword(account, password);
        }

        authToken = token;

        final Intent intent = new Intent();
        intent.putExtra(KEY_ACCOUNT_NAME, username);
        intent.putExtra(KEY_ACCOUNT_TYPE, Constants.Auth.BOOTSTRAP_ACCOUNT_TYPE);

        if (authTokenType != null
                && authTokenType.equals(Constants.Auth.AUTHTOKEN_TYPE)) {
            intent.putExtra(KEY_AUTHTOKEN, authToken);
        }

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * Hide progress dialog
     */
    @SuppressWarnings("deprecation")
    protected void hideProgress() {
        dismissDialog(0);
    }

    /**
     * Show progress dialog
     */
    @SuppressWarnings("deprecation")
    protected void showProgress() {
        showDialog(0);
    }

    /**
     * Called when the authentication process completes (see attemptLogin()).
     *
     * @param result
     */
    public void onAuthenticationResult(final boolean result) {
        if (result) {
            if (!confirmCredentials) {
                finishLogin();
            } else {
                finishConfirmCredentials(true);
            }
        } else {
            Ln.d("onAuthenticationResult: failed to authenticate");
            if (requestNewAccount) {
            	Toaster.showLong(IcarerAuthenticatorActivity.this,
                        string.message_auth_failed_new_account);
            } else {
            	Toaster.showLong(IcarerAuthenticatorActivity.this,
                        string.message_auth_failed);
            }
        }
    }
}
