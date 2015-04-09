package com.sjtu.icarer;


import static com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES;

import java.sql.Time;

import javax.inject.Singleton;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sjtu.icarer.authenticator.AccountDataProvider;
import com.sjtu.icarer.authenticator.IcarerAuthenticatorActivity;
import com.sjtu.icarer.authenticator.LogoutService;
import com.sjtu.icarer.common.config.Url;
import com.sjtu.icarer.core.utils.Named;
import com.sjtu.icarer.core.utils.PostFromAnyThreadBus;
import com.sjtu.icarer.core.utils.PreferenceManager;
import com.sjtu.icarer.core.utils.RestAdapterRequestInterceptor;
import com.sjtu.icarer.core.utils.RestErrorHandler;
import com.sjtu.icarer.core.utils.UserAgentProvider;
import com.sjtu.icarer.model.utils.JsonIntDeserializer;
import com.sjtu.icarer.model.utils.JsonSqlDateDeserializer;
import com.sjtu.icarer.model.utils.JsonTimeDeserializer;
import com.sjtu.icarer.service.IcarerService;
import com.sjtu.icarer.service.IcarerServiceProvider;
import com.sjtu.icarer.thread.UpdateReceiver;
import com.sjtu.icarer.thread.UpdateService;
import com.sjtu.icarer.ui.HomeActivity;
import com.sjtu.icarer.ui.MainActivity;
import com.sjtu.icarer.ui.SettingActivity;
import com.sjtu.icarer.ui.SetupActivity;
import com.sjtu.icarer.ui.area.AreaItemsFragment;
import com.sjtu.icarer.ui.deprecated.FragmentRoom;
import com.sjtu.icarer.ui.elder.ElderItemsFragment;
import com.sjtu.icarer.ui.setting.AreaPreferenceFragment;
import com.sjtu.icarer.ui.setting.ClearCacheFragment;
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
        SetupActivity.class,
        SettingActivity.class,
        AreaPreferenceFragment.class,
        ClearCacheFragment.class,
        HomeActivity.class,
        AreaItemsFragment.class,
        ElderItemsFragment.class,
        UpdateService.class,
        UpdateReceiver.class,
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
	@Provides @Named("unAuth")
	IcarerService provideUnAuthIcarerService(RestAdapter restAdapter){
		return new IcarerService(restAdapter);
	}
	
	@Provides @Named("Auth")
	IcarerService provideAuthIcarerService(RestAdapter restAdapter, AccountDataProvider apiKeyProvider){
		return new IcarerService(restAdapter,apiKeyProvider);
	}
	
	/*HTTP request with data already known*/
	@Provides 
	IcarerServiceProvider provideIcarerServiceProvider(RestAdapter restAdapter, AccountDataProvider apiKeyProvider) {
        return new IcarerServiceProvider(restAdapter, apiKeyProvider);
    }
	
	@Provides
	AccountDataProvider provideAccountDataProvider(AccountManager accountManager, Gson gson){
		return new AccountDataProvider(accountManager, gson);
	}
	
	@Provides
	PreferenceManager providePreferenceProvider(SharedPreferences sharedPreferences){
		return new PreferenceManager(sharedPreferences);
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
        		.setDateFormat("yyyy-MM-dd HH:mm:ss")
        		.registerTypeAdapter(Time.class, new JsonTimeDeserializer())
        		.registerTypeAdapter(int.class, new JsonIntDeserializer())
        		.registerTypeAdapter(java.sql.Date.class, new JsonSqlDateDeserializer())
        		.setPrettyPrinting()
//        		.serializeNulls()
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
	
	//Database now
//	@Provides
//	DbHelper provideDbHelper(Context context){
//		return new DbHelper(context);
//	}
	
//	@Provides
//	DbCache provideCache(){
//		return new DbCache();
//	}
	
//	@Provides
//	DbManager provideDbManager(@Named("Auth")IcarerService icarerService,
//			                   PreferenceManager preferenceManager,
//			                   DbCache dbCache){
//		return new DbManager(icarerService, preferenceManager,dbCache);
//	}
	
}
