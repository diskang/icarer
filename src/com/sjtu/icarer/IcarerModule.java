package com.sjtu.icarer;


import static com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES;

import javax.inject.Singleton;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import android.accounts.AccountManager;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sjtu.icarer.authenticator.AccountDataProvider;
import com.sjtu.icarer.authenticator.IcarerAuthenticatorActivity;
import com.sjtu.icarer.authenticator.LogoutService;
import com.sjtu.icarer.common.config.Url;
import com.sjtu.icarer.core.IcarerApplication;
import com.sjtu.icarer.core.PostFromAnyThreadBus;
import com.sjtu.icarer.core.app.UserAgentProvider;
import com.sjtu.icarer.core.web.RestAdapterRequestInterceptor;
import com.sjtu.icarer.core.web.RestErrorHandler;
import com.sjtu.icarer.service.IcarerService;
import com.sjtu.icarer.service.IcarerServiceProvider;
import com.sjtu.icarer.ui.HomeActivity;
import com.sjtu.icarer.ui.MainActivity;
import com.sjtu.icarer.ui.login.LoginActivity;
import com.squareup.otto.Bus;

import dagger.Module;
import dagger.Provides;


/**
 * Dagger module for setting up provides statements.
 * Register all of your entry points below.
 */
@Module(
    complete = false,

    injects = {
        IcarerApplication.class,
        IcarerAuthenticatorActivity.class,
        MainActivity.class,
        LoginActivity.class,
        HomeActivity.class,
        FragmentRoom.class
    },
    library = true
)
public class IcarerModule {
	@Singleton
    @Provides
    Bus provideOttoBus() {
        return new PostFromAnyThreadBus();
    }
	
	@Provides
    @Singleton
    LogoutService provideLogoutService(final Context context, final AccountManager accountManager) {
        return new LogoutService(context, accountManager);
    }
	/*HTTP request without account*/
	@Provides
	IcarerService provideIcarerService(RestAdapter restAdapter){
		return new IcarerService(restAdapter);
	}
	/*HTTP request with data already known*/
	@Provides
	IcarerServiceProvider providIcarerServiceProvider(RestAdapter restAdapter, AccountDataProvider apiKeyProvider) {
        return new IcarerServiceProvider(restAdapter, apiKeyProvider);
    }
	
	@Provides
	AccountDataProvider provideAccountDataProvider(AccountManager accountManager, Gson gson){
		return new AccountDataProvider(accountManager, gson);
	}
	
    @Provides
    Gson provideGson() {
        /**
         * GSON instance to use for all request  with date format set up for proper parsing.
         * <p/>
         * You can also configure GSON with different naming policies for your API.
         * Maybe your API is Rails API and all json values are lower case with an underscore,
         * like this "first_name" instead of "firstName".
         * You can configure GSON as such below.
         * <p/>
         *
         * public static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd")
         *         .setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES).create();
         */
        return new GsonBuilder().setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES)
        		.setDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
        		.setPrettyPrinting()
        		.serializeNulls()
        		.create();
    }
    
	@Provides
    RestErrorHandler provideRestErrorHandler(Bus bus) {
        return new RestErrorHandler(bus);
    }
	
	@Provides
    RestAdapterRequestInterceptor provideRestAdapterRequestInterceptor(UserAgentProvider userAgentProvider) {
        return new RestAdapterRequestInterceptor(userAgentProvider);
    }
	
	@Provides
    RestAdapter provideRestAdapter(RestErrorHandler restErrorHandler, RestAdapterRequestInterceptor restRequestInterceptor, Gson gson) {
        return new RestAdapter.Builder()
                .setEndpoint(Url.URL_BASE)
                .setErrorHandler(restErrorHandler)
                .setRequestInterceptor(restRequestInterceptor)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(new GsonConverter(gson))
                .build();
    }
}
